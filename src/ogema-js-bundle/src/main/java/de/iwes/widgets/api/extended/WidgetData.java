/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.widgets.api.extended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Each {@link OgemaWidgetBase} has one WidgetData instance per user session.  
 */
// note to implementers: locking is only required for global widgets, widget implementations that do not
// provide a global variant need not bother about locks and synchronization.
public abstract class WidgetData {
	
	public static final WidgetStyle<?> TEXT_ALIGNMENT_CENTERED = new WidgetStyle<OgemaWidget>("",Collections.singletonList("text-center"),4);
	public static final WidgetStyle<?> TEXT_ALIGNMENT_LEFT = new WidgetStyle<OgemaWidget>("",Collections.singletonList("text-left"),4);
	public static final WidgetStyle<?> TEXT_ALIGNMENT_RIGHT = new WidgetStyle<OgemaWidget>("",Collections.singletonList("text-right"),4);
	
	private final Map<String, String> attributes;
	private final List<Map<String,Object>> connectElements;
	private final List<Map<String,Object>> connectGroups;
	protected final OgemaWidgetBase<?> widget;
	private final Map<String, Map<String, String>> cssMap;
    // Note: dependencies listed here are only relevant for POST requests
    private boolean disabled = false;
    private final String id;
    private long pollingInterval = -1;
    private boolean sendValueOnChange = true;
    private final List<WidgetStyle<?>> styles;
    private boolean waitForPendingRequest = false;
    protected boolean visible = true;
    volatile boolean initialized = false;
    boolean dynamicWidget;
    /* set to null for global widget, to the initial request otherwise */
    // FIXME required?
    OgemaHttpRequest initialRequest; 
    protected final ReentrantReadWriteLock lock;
    protected final boolean globalWidget;

    /**
     * Use {@link #getsessionDependencies()} to initialize this on demand
     */
    @Deprecated
    Set<OgemaWidgetBase<?>> sessionDependencies;
    
    /***************** Constructor ********************/
    
    // TODO lazy initialization of the maps and lists
    protected WidgetData(OgemaWidgetBase<?> widget) {
    	this.widget = widget;
    	this.attributes = new LinkedHashMap<String, String>(4);
    	this.connectElements = new LinkedList<Map<String,Object>>();
    	this.connectGroups = new LinkedList<Map<String,Object>>();
		this.cssMap = new LinkedHashMap<String, Map<String,String>>(4);
		this.id = widget.getId();
		this.styles = new LinkedList<WidgetStyle<?>>();
		this.globalWidget = widget.isGlobalWidget();
		if (globalWidget)
			lock =new ReentrantReadWriteLock();
		else
			lock = null;
//		this.subWidgets = new LinkedHashSet<OgemaWidget<?>>();
	}    
    
    /********* Methods to be overridden in derived class ***********/
    
    public abstract JSONObject retrieveGETData(OgemaHttpRequest req);

    public JSONObject onPOST(String data, OgemaHttpRequest req) throws UnsupportedOperationException {
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Override if your derived widget supports subwidgets. -> should be done in widget-specific way 
     */
/*    protected void registerSubWidget(OgemaWidget<?> subwidget) {
//    	subWidgets.add(subwidget);
    	throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support subwidgets.");
    }*/ 
    
    /**
     * Override this if your widget supports width configuration. Return a CSS selector for the tag to which the width 
     * setting shall be applied, such as "&gt;div" for an immediate child tag of the widget, or ".someClass" for a 
     * class-based selection.
     * @return
     */
    protected String getWidthSelector() {
    	throw new UnsupportedOperationException("Widget type " + widget.getWidgetClass().getSimpleName() + " does not support width");
    }
    
    /**
     * This method need not be supported
     */
    protected boolean removeSubWidget(OgemaWidgetBase<?> subwidget) {
    	boolean result;
    	writeLock();
    	try {
	    	result = getSubWidgets().remove(subwidget);
    	} finally {
    		writeUnlock();
    	}
    	if (result) {
    		subwidget.destroyWidget();
    	}
    	return result;
    	
    }
    
    /**
     * This method must be implemented, it is used by the session management upon removal of the WidgetOptions object
     */
    protected void removeSubWidgets() {
    	writeLock();
    	List<OgemaWidget> subwidgets;
    	try {
    		subwidgets = new ArrayList<>(getSubWidgets());
    		getSubWidgets().clear();
    	} finally {
        	writeUnlock();
    	}
    	// System.out.println("     Calling removeSubwidgets for " + id+ ", subwidgets = " + getSubWidgets());
    	Iterator<OgemaWidget> it = subwidgets.iterator();
    	while (it.hasNext()) {
    		OgemaWidget sw = it.next();
    		sw.destroyWidget();
    	}
    	
    }
    
    /**
     * Override if your widget supports subwidgets; must return a live view of the collection, not a copy.
     */
    protected  Collection<OgemaWidget> getSubWidgets() {  
		Collection<OgemaWidget> empty = Collections.emptySet();
		return empty;
    }
    
    /*************** Public methods *****************/
    
    public void destroy() {
    	removeSubWidgets();
    }
    
    long getPollingInterval() {
    	readLock();
    	try {
    		return pollingInterval;
    	} finally {
    		readUnlock();
    	}
    }
    
    /**
    * @param pollingInterval; in ms; if &lt;=0, then no polling
    */
   protected void setPollingInterval(long pollingInterval) {
	   writeLock();
	   try {
		   this.pollingInterval = pollingInterval;
	   } finally {
		   writeUnlock();
	   }
   }
   
   /**
    * outer key is the classname to which the CSS properties apply, inner keys
    * are CSS-properties<br>
    * see {@link #addCssItem(String, Map)}
    */
    protected Map<String, Map<String, String>> getCssMap() {
       readLock();
       try {
    	   return new HashMap<>(cssMap);
       } finally {
    	   readUnlock();
       }
   }

   protected void addCssMap(Map<String, Map<String, String>> css)  {
	   writeLock();
	   try {
		   for (Map.Entry<String, Map<String,String>> entry :css.entrySet()) {
			   addCssItem(entry.getKey(), entry.getValue());
		   }
	   } finally {
		   writeUnlock();
	   }
   }
    
   /**
    * see {@link #addCssItem(String, Map)}
    */
   void setCssMap(Map<String, Map<String, String>> css) {
	   writeLock();
	   try {
		   this.cssMap.clear();
		   if (css != null) {
			   css.entrySet().stream()
			   		.filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
			   		.forEach(entry -> cssMap.put(entry.getKey(), new HashMap<>(entry.getValue())));
		   }
	   } finally {
		   writeUnlock();
	   }
   }

   /**
    * Parameter: a map of the form { selector : { key1: prop1, key2: prop2}},
    * will be transformed to a CSS-String of the form <br>
    * "#widgetID selector {key1: prop1; key2: prop2;}" <br>
    * Note that the selector (e.g. ".className") may apply to subelements of
    * the actual widget.<br>
    * The selector can be of the following forms:
    * <ul>
    * <li>empty string "", in which case the setting applies to the whole
    * widget (all subelements)</li>
    * <li>".myClassName", in which case the setting applies to all
    * HTML-subelements of the specified class</li>
    * <li>"tagName", in which case it applies to all tags of the specified type
    * (e.g. "tr" means it will apply to all rows of a table, "tr:first-child"
    * only applies to the first row, etc)</li>
    * <li>"&gt;tagName", in which case it applies to all immediate child tags of
    * the specified type</li>
    * <li>"#myId", in which case it only applies to a subelement with the
    * specified ID</li>
    * </ul>
    *
    */
   public void addCssItem(String selector, Map<String, String> value) {
	   writeLock();
	   try {
		   Map<String,String> old = cssMap.get(selector);
		   if (old == null) {
			   old = new HashMap<>(4);
			   cssMap.put(selector, old);
		   }
		   for (Map.Entry<String, String> entry : value.entrySet()) {
			   old.put(entry.getKey(), escapeHtmlAttributeValue(entry.getValue()));
		   }
	   } finally {
		   writeUnlock();
	   }
   }
   
   /**
    * Unsafe, but sometimes required. Accessed by reflections, do not remove or refactor.
    * @param selector
    * @param value
    */
   private void addCssItemUnescaped(String selector, Map<String, String> value) {
	   writeLock();
	   try {
		   Map<String,String> old = cssMap.get(selector);
		   if (old == null) {
			   old = new HashMap<>(4);
			   cssMap.put(selector, old);
		   }
		   for (Map.Entry<String, String> entry : value.entrySet()) {
			   old.put(entry.getKey(), entry.getValue());
		   }
	   } finally {
		   writeUnlock();
	   }
   }
   
   /**
    * See {@link #addCssItem(String, Map)}.
    * @param selector
    * @return
    */
   public Map<String,String> getCssItem(String selector) {
	   readLock();
	   try {
		   return cssMap.get(selector);
	   } finally {
		   readUnlock();
	   }
   }	

   /**
    * see {@link #addCssItem(String, Map)}
    */
   public void removeCSSItems(String selector) {
	   writeLock();
	   try {
		   cssMap.remove(selector);
	   } finally {
		   writeUnlock();
	   }
   }
   
   /**
    * see {@link #addCssItem(String, Map)}
    */
   public void removeCSSItem(String selector, String property) {
	   readLock();
	   try {
		   Map<String,String> map = cssMap.get(selector);
		   if (map == null)
			   return;
		   map.remove(property);
		   if (map.isEmpty())
			   cssMap.remove(selector);
	   } finally {
		   readUnlock();
	   }
   }
   
    protected void setBackgroundColor(String color) {
    	Map<String, String> mp = new HashMap<>();
		mp.put("background-color", color);
		addCssItem("", mp);
    }
   
	public void setWidth(String width) {
		if (width == null || width.isEmpty()) {
			removeCSSItem(getWidthSelector(), "width");
			return;
		}
		if (width.charAt(width.length()-1) == ';')
			width = width.substring(0, width.length()-1);
		addCssItem(getWidthSelector(), Collections.singletonMap("width", width));
	}
	
	public void setMaxWidth(String width) {
		if (width == null || width.isEmpty()) {
			removeCSSItem(getWidthSelector(), "max-width");
			return;
		}
		if (width.charAt(width.length()-1) == ';')
			width = width.substring(0, width.length()-1);
		addCssItem(getWidthSelector(), Collections.singletonMap("max-width", width));
	}
	
	public void setMinWidth(String width) {
		if (width == null || width.isEmpty()) {
			removeCSSItem(getWidthSelector(), "min-width");
			return;
		}
		if (width.charAt(width.length()-1) == ';')
			width = width.substring(0, width.length()-1);
		addCssItem(getWidthSelector(), Collections.singletonMap("min-width", width));
	}
	
	/**
	 * By default, this is setting is applied to the dom element specified by {@link #getWidthSelector()}. 
	 * Override in order to specify a different behaviour.
	 * @param height
	 */
	protected void setHeight(String height) {
		addCssItem(getWidthSelector(), Collections.singletonMap("height", height));
	}

	public void setToolTip(String tooltip) {
		writeLock();
		try {
			attributes.put("title", tooltip);
		} finally {
			writeUnlock();
		}
	}
	
	public String removeToolTip() {
		writeLock();
		try {
			String removed = attributes.remove("title");
			if(removed.equals(""))
				return null;
			return removed;
		} finally {
			writeUnlock();
		}
	}
	
	protected void addStyle(WidgetStyle<?> style) {
	   writeLock();
	   try {
	       if (!styles.contains(style)) {
	           styles.add(style);
	       }
	   } finally {
		   writeUnlock();
	   }
   }

   protected void setStyle(WidgetStyle<?> style) {
	   writeLock();
	   try {
	       styles.clear();
	       styles.add(style);
	   } finally {
		   writeUnlock();
	   }
   }
   
   protected void setStyles(Collection<WidgetStyle<?>> styles) {
	   writeLock();
	   try {
	       this.styles.clear();
	       this.styles.addAll(styles);
	   } finally {
		   writeUnlock();
	   }
   }
   

   protected boolean removeStyle(WidgetStyle<?> style) {
	   writeLock();
	   try {
		   return styles.remove(style);
	   } finally {
		   writeUnlock();
	   }
   }
   

   /**
    * only relevant to widgets displaying a value that can be changed by the
    * user
    */
   boolean doSendValueOnChange() {
	   readLock();
	   try {
		   return sendValueOnChange;
	   } finally {
		   readUnlock();
	   }
   }

   /**
    * only relevant to widgets displaying a value that can be changed by the
    * user
    */
   void setSendValueOnChange(boolean sendValueOnChange) {
	   writeLock();
	   try {
		   this.sendValueOnChange = sendValueOnChange;
	   } finally {
		   writeUnlock();
	   }
   }

   /**
    * returns a copy of the list of styles
    */
   List<WidgetStyle<?>> getStyles() {
	   readLock();
	   try {
		   return new LinkedList<WidgetStyle<?>>(styles);
	   } finally {
		   readUnlock();
	   }
   }
   
   void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction) {
       triggerAction(target, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),0);
   }

   void triggerAction(OgemaWidget target, String triggeringAction, String triggeredAction, Object[] args, int level) {
	   String widgetID2 = target.getId();
       Map<String,Object> cncObj = new LinkedHashMap<String, Object>();
       cncObj.put("widgetID1", id);
       cncObj.put("widgetID2", widgetID2);
       cncObj.put("triggeringAction", triggeringAction);
       cncObj.put("triggeredAction", triggeredAction);
       cncObj.put("level", level);
       if (args != null) {
           cncObj.put("args", args);
       }
       writeLock();
       try {
    	   connectElements.add(cncObj);
       } finally {
    	   writeUnlock();
       }
   }
   
   void clearTriggerActions() {
       writeLock();
       try {
    	   connectElements.clear();
    	   connectGroups.clear();
       } finally {
    	   writeUnlock();
       }
   }

   void removeTriggerAction(OgemaWidget target) {
	   String widgetID2 = target.getId();
       writeLock();
       try {
    	   final Iterator<Map<String,Object>> it = connectElements.iterator();
    	   while (it.hasNext()) {
    		   final String w2 = (String) it.next().get("widgetID2");
    		   if (widgetID2.equals(w2)) {
    			   it.remove();
    			   break;
    		   }
    	   }
       } finally {
    	   writeUnlock();
       }
   }

   void triggerAction(OgemaWidget target, String triggeringAction, String triggeredAction) {
       triggerAction(target, triggeringAction, triggeredAction, null,0);
   }
   
   void triggerGroupAction(WidgetGroup triggeredGroup, TriggeringAction triggeringAction, TriggeredAction triggeredAction) {
       triggerGroupAction(triggeredGroup, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),0);
   }

   void triggerGroupAction(WidgetGroup triggeredGroup, String triggeringAction, String triggeredAction, Object[] args, int level) {
       Map<String,Object> cncObj = new LinkedHashMap<String, Object>();
       cncObj.put("widgetID1", id);
       cncObj.put("groupID2", triggeredGroup.getId());
       cncObj.put("triggeringAction", triggeringAction);
       cncObj.put("triggeredAction", triggeredAction);
       cncObj.put("level", level);
       if (args != null) {
           cncObj.put("args", args);
       }
       writeLock();
       try {
    	   connectGroups.add(cncObj);
       } finally {
    	   writeUnlock();
       }
   }
   
   void removeTriggerAction(WidgetGroup triggeredGroup) {
	   String widgetID2 = triggeredGroup.getId();
       writeLock();
       try {
    	   final Iterator<Map<String,Object>> it = connectGroups.iterator();
    	   while (it.hasNext()) {
    		   final String w2 = (String) it.next().get("groupID2");
    		   if (widgetID2.equals(w2)) {
    			   it.remove();
    			   break;
    		   }
    	   }
       } finally {
    	   writeUnlock();
       }
   }

   void triggerGroupAction(WidgetGroup triggeredGroup, String triggeringAction, String triggeredAction) {
       triggerGroupAction(triggeredGroup, triggeringAction, triggeredAction, null,0);
   }

   /*
    * A dependent widget will updated itself on the client side when a POST
    * request on the governing widget is finished. In this way the server can
    * update the dependent widgets when a new value for the governing widget is
    * received. When the page is initialized or when a POST is received on the
    * server side the generation of the new JSON data of the dependent widget
    * is triggered by the governing widget so that the page contains consistent
    * data
    *
    * @param widget
    * @return
    */
   /*<S extends OgemaWidget<?>> S registerDependentWidget(S widget) {
       triggerAction(widget.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
       dependencies.add(widget);
       //widget.getPage().setWidgetDepedencyStatus(widget, true);
//       governingWidget = true;
       return widget;
   } */

   public void setWidgetVisibility(boolean visible) {
	   writeLock();
       try {
    	   this.visible = visible;
       } finally {
    	   writeUnlock();
       }
   }
   
   public boolean isVisible() {
	   readLock();
	   try {
		   return visible;
	   } finally {
		   readUnlock();
	   }
   }
   
   public boolean isDisabled() {
	   readLock();
	   try {
		   return disabled;
	   } finally {
		   readUnlock();
	   }
   }

   public void disable() {
	   writeLock();
	   try {
		   this.disabled = true;
	   } finally {
		   writeUnlock();
	   }
   }

   public void enable() {
	   writeLock();
	   try {
		   this.disabled = false;
	   } finally {
		   writeUnlock();
	   } 
   }
   
   void setWaitForPendingRequest(boolean wait) {
	   writeLock();
	   try {
		   this.waitForPendingRequest = wait;
	   } finally {
		   writeUnlock();
	   }
   }
   
   boolean getWaitForPendingRequest() {
	   readLock();
	   try {
		   return waitForPendingRequest;
	   } finally {
		   readUnlock();
	   }
   }

   /*
    * 
    * -> Javadoc doesn't accept Javascript in comments
    * 
    * Add an HTML attribute and a corresponding Javascript action, like in
    * <button onclick="window.open(otherPage.html,_blank);">
    *
    * @param attr: HTML attribute, e.g. "onclick"
    * @param action: Javascript function to execute, e.g.
    * "window.open(otherPage.html,_blank);"
    */
   public void addAttribute(String attr, String action) {
	   writeLock();
	   try {
		   attributes.put(attr, action);
	   } finally {
		   writeUnlock();
	   }
   }

   /**
    * Remove HTML attribute, like "onclick"
    */
   protected boolean removeAttribute(String attr) {
	   writeLock();
	   try {
	       return (attributes.remove(attr) != null);
	   } finally {
		   writeUnlock();
	   }
   }

   /**
    * Get Javascript function associated to attribute attr
    */
   protected String getAttribute(String attr) {
	   readLock();
	   try {
		   return attributes.get(attr);
	   } finally {
		   readUnlock();
	   }
   } 

   /*
   public boolean addSubWidget(OgemaWidget<?> widget) {
       return subWidgets.add(widget);
   }

   public boolean removeSubWidget(OgemaWidget<?> widget) {
       return subWidgets.remove(widget.getId());
   }
   
   /*
    * Utility methods for widgets implementation.
    * See also 
    */
   
   public static String escapeHtmlAttributeValue(String value) {
	   if (value == null)
		   return null;
	   return value.replace("\"", "&quot;").replace("'", "&#x27;"); // or ' -> &#x39;
   }
   
   public static String unescapeHtmlAttributeValue(String value) {
	   if (value == null)
		   return null;
	   return value.replace("&quot;", "\"").replace("&#x27;", "'"); // or ' -> &#x39;
   }
   
   /*************** Internal methods ***************/
   
   /**
    * @param str
    * @return
    * @deprecated use {@link Arrays#asList(Object...)} or {@link Collections#singletonList(Object)}
    */
   @Deprecated 
   protected static List<String> getList(String... str) {
	   return Arrays.asList(str);
   }

   protected void appendWidgetInformation(OgemaHttpRequest req, JSONObject result) {
       JSONObject widgetJson = getWidgetInformation(req);
       result.append(id, widgetJson); 
   }

   JSONObject getWidgetInformation(OgemaHttpRequest req) {

//	   	req.widetValue = null;
//	   	req.widgetObject = null;
//	   	req.widgetAttribute.clear();
//	   	req.widgetAttributeObject.clear();
       JSONObject results = retrieveGETData(req); // must not hold a lock while calling uncontrolled code... may lead to deadlock
       readLock();
       try {
    	   final Set<String> groups = widget.groups;
	       if (groups != null && !groups.isEmpty())
	    	   results.put("widgetGroups", groups);
	  		//Map<String, String> pars = req.getParams();
	       //For now every widgets gets information on the page it sits separatly
	       //pars.put("pageInstance", req.getPageInstanceId());
	       //results.put("params", pars);
	       if (widget.governingWidget) {
	           results.put("governingWidget", widget.governingWidget);
	       }
	       List<Map<String,Object>> cnc = new LinkedList<Map<String,Object>>();
	       cnc.addAll(connectElements);
	       cnc.addAll(widget.globalConnectElements);
	       if (cnc.size() > 0) {
	    	   
	           results.put("connectWidgets", cnc);
	       }
	       List<Map<String,Object>> cncGroups = new LinkedList<Map<String,Object>>();
	       cncGroups.addAll(connectGroups);
	       cncGroups.addAll(widget.globalConnectGroups);
	       if (cncGroups.size() > 0) {
	    	   
	           results.put("connectGroups", cncGroups);
	       }
	/*       if(req.widgetAttribute.containsKey("pollingInterval")) {
	       		results.put("pollingInterval", req.widgetAttribute.get("pollingInterval"));
	       } else { */
	       		results.put("pollingInterval", pollingInterval);
	//       }
	       if (!sendValueOnChange) {
	           results.put("sendValueOnChange", sendValueOnChange);
	       }
	       if (waitForPendingRequest) {
	    	   results.put("waitForPendingRequest", waitForPendingRequest);
	       }
	       
	       results.put("dynamicWidget", dynamicWidget);
	/*       boolean setBool;
	       if(req.widgetAttribute.containsKey("disabled")) {
	       	setBool = req.widgetAttribute.get("disabled").equals("true");
	       } else {
	       	setBool = disabled;
	   	}
	       if (setBool) {
	           results.put("disabled", setBool);
	       } */
	       results.put("disabled", disabled);
	       
	       results.put("visible", visible);
	       if (attributes.size() > 0) {
	           results.put("attributes", attributes);
	       }
	/*       if(req.widgetAttributeObject.containsKey("cssMap")) {
	           results.put("cssMap", req.widgetAttributeObject.get("cssMap"));
	       } else if (!cssMap.isEmpty()) { */
	           results.put("cssMap", cssMap);
	//       }
	       if (!styles.isEmpty()) {
	           Map<String, List<String>> idStylesMap = new LinkedHashMap<String, List<String>>();
	           Map<String, List<String>> classStylesMap = new LinkedHashMap<String, List<String>>();
	           Map<String, List<String>> childTagStylesMap = new LinkedHashMap<String, List<String>>();
	           Map<String, List<String>> tagStylesMap = new LinkedHashMap<String, List<String>>();
	           List<String> widgetStyles = new ArrayList<>();
               Iterator<WidgetStyle<?>> it = styles.iterator();
               while (it.hasNext()) {
                   WidgetStyle<?> st = it.next();         // would be much easier to put internalStylesMap.putAll(it.next().getCssMap()), but then only one style can set classes for a given element
                   if (st == null) continue;
                   Iterator<Entry<String, List<String>>> itInt = st.getCssMap().entrySet().iterator();
                   while (itInt.hasNext()) {
                       Entry<String, List<String>> entry = itInt.next();
                       switch (st.getSelectorType()) {
                       case 0:  // id identifier
                           List<String> list = idStylesMap.get(entry.getKey());
                           if (list == null) {
                               list = new LinkedList<String>();
                               idStylesMap.put(entry.getKey(), list);
                           }
                           // https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.234_-_CSS_Escape_And_Strictly_Validate_Before_Inserting_Untrusted_Data_into_HTML_Style_Property_Values
                           for (String s: entry.getValue())
                        	   list.add(escapeHtmlAttributeValue(s));
//                           list.addAll(entry.getValue());
                           break;
                       case 1:  // class identifier
                           list = classStylesMap.get(entry.getKey());
                           if (list == null) {
                               list = new LinkedList<String>();
                               classStylesMap.put(entry.getKey(), list);
                           }
                           for (String s: entry.getValue())
                        	   list.add(escapeHtmlAttributeValue(s));
//                           list.addAll(entry.getValue());
                           break;
                       case 2: // direct children tag identifier
                    	   list = childTagStylesMap.get(entry.getKey());
                           if (list == null) {
                               list = new LinkedList<String>();
                               childTagStylesMap.put(entry.getKey(), list);
                           }
                           for (String s: entry.getValue())
                        	   list.add(escapeHtmlAttributeValue(s));
//                           list.addAll(entry.getValue());
                           break;
                       case 3: // tag identifier
                    	   list = tagStylesMap.get(entry.getKey());
                           if (list == null) {
                               list = new LinkedList<String>();
                               tagStylesMap.put(entry.getKey(), list);
                           }
                           for (String s: entry.getValue())
                        	   list.add(escapeHtmlAttributeValue(s));
//                           list.addAll(entry.getValue());
                           break;
                       case 4:
                    	   if (!entry.getKey().equals(""))
                    		   continue;
                           for (String s: entry.getValue())
                        	   widgetStyles.add(escapeHtmlAttributeValue(s));
//                    	   widgetStyles.addAll(entry.getValue());
                       }
                   }
               }
//				results.put("styles",internalStylesMap);
               JSONObject styles = new JSONObject();
               styles.put("idStyles", idStylesMap);
               styles.put("classStyles", classStylesMap);
               styles.put("childTagStyles", childTagStylesMap);
               styles.put("tagStyles", tagStylesMap);
               styles.put("widgetStyles", widgetStyles);
               results.put("styles", styles);
           }
	       Collection<OgemaWidget> subwidgets = getSubWidgets();
	       if (!subwidgets.isEmpty()) {
	    	   JSONArray arr = new JSONArray(); 
	    	   for (OgemaWidget s: subwidgets) {
	    		   arr.put(s.getId());
	    	   }
	    	   results.put("subWidgets", arr);
	       }
	       return results;
       } finally {
    	   readUnlock();
       }
   }

   // FIXME can we get rid of this?
   protected OgemaHttpRequest getInitialRequest() {
	   readLock();
	   try {
		   return initialRequest;
	   } finally {
		   readUnlock();
	   }
   }
   
   @Deprecated
   Set<OgemaWidgetBase<?>> getsessionDependencies() {
   	Set<OgemaWidgetBase<?>> sessionDependencies = this.sessionDependencies;
   	if (sessionDependencies == null) {
   		synchronized (this) {
   			sessionDependencies = this.sessionDependencies;
   	    	if (sessionDependencies == null) {
   	    		sessionDependencies = Collections.newSetFromMap(new ConcurrentHashMap<>());
   	    		this.sessionDependencies = sessionDependencies;
   	    	}
			}
   	}
   	return sessionDependencies;
   }
   
   // no locks are required if the widget is not global. In this case all requests associated to one session
   // are executed in the same thread
   protected final void readLock() {
	   if (globalWidget)
		   lock.readLock().lock();
   }
   
   protected final void readUnlock() {
	   if (globalWidget)
		   lock.readLock().unlock();
   }
   
   protected final void writeLock() {
	   if (globalWidget) {
		   lock.writeLock().lock();
	   }
   }
   
   protected final void writeUnlock() {
	   if (globalWidget)
		   lock.writeLock().unlock();
   }
}
