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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.iwes.widgets.api.extended.HtmlLibrary.LibType;
import de.iwes.widgets.api.extended.impl.SessionExpiredException;
import de.iwes.widgets.api.extended.impl.WidgetSessionManagement;
import de.iwes.widgets.api.services.IconService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Base class for widget implementations.
 * @param <T>
 */
public abstract class OgemaWidgetBase<T extends WidgetData>  extends HttpServlet implements OgemaWidget {  

    private static final long serialVersionUID = 1L;
//    protected boolean governingWidget = false;
//    public List<String> pageInstancesToUpdate = new ArrayList<String>();
    private final boolean globalWidget;
    private volatile T globalOptions;	// = null if globalWidget == false
    private long defaultPollingInterval = -1;	 
    private boolean defaultSendValueOnChange = true;
    private Set<WidgetStyle<?>> defaultStyles = null;
    private boolean defaultVisibility  =true;
	private String defaultWidth = null;
	private String defaultMaxWidth = null;
	private String defaultMinWidth = null;
	private String defaultHeight = null;
	private String defaultTooltip = null;
	private String defaultBackgroundColor = null;
	private Map<String, Map<String, String>> defaultCssMap;
    private final WidgetSessionManagement<T> sessionManagement;  // never null
    private final boolean isSessionSpecific;
    /** 
    * Map keys: widgetID1, widgetID, triggeringAction, triggeredAction, optional: args; 
    * values for the triggers: see {@link TriggeredAction} and {@link TriggeringAction}
    */
    final List<Map<String,Object>> globalConnectElements = new CopyOnWriteArrayList<>();
    final List<Map<String,Object>> globalConnectGroups = new CopyOnWriteArrayList<>();

    private boolean forceUpdate = false;
    private boolean defaultWaitForPendingRequest = false;
    private boolean postponeLoading = false;
    private boolean preloadGroup = false;
    /**
     * Use {@link #getGroups()} to initialize this on demand
     */
    volatile Set<String> groups; // entries: groupIds 

	private boolean dynamicWidget = false;
	private final String id;
	// must not be visible to subwidgets/apps rely on this not being visible
	private final WidgetPageBase<?> page;
	
	/**
	 * FIXME synchronization; weak references?
	 * Use {@link #getDependencies()} to initialize this on demand
	 */
	@Deprecated
    private volatile Set<OgemaWidgetBase<?>> dependencies;
    protected volatile boolean governingWidget = false;
    /**
     * The set of widgets which have this one as a dependency. This has weak values for the following reason: 
     * if the parent widget is session-dependent, but this one is not, a hard reference may lead to a 
     * memory leak.<br>
     * Use {@link #getParents()} to initialize this on demand
     */
    @Deprecated
    private volatile Cache<String, OgemaWidgetBase<T>> parents;
    
    
//    protected Set<OgemaWidgetBase<?>> parents = new HashSet<OgemaWidgetBase<?>>();

	/*
     * ********* constructors **********************
     */

	/**
	 * Standard constructor
	 * @param page
	 * @param id
	 * 		must be a valid Java variable name
	 */
	protected OgemaWidgetBase(WidgetPage<?> page, String id) {	 // default: session-dependent options objects
		this(page, id,false);
	}
	
	/**
	 * Create a new widget, configure whether data is sent to the server by default
	 * @param page
	 * @param id
	 * 		must be a valid Java variable name
	 * @param sendValueOnChange
	 */
	@Deprecated
	protected OgemaWidgetBase(WidgetPage<?> page, String id, SendValue sendValueOnChange) {	 // default: session-dependent options objects
		this(page, id,false, sendValueOnChange);
	}
	
	/** 
	 * Create global (session-independent) widgets
	 * @param page
	 * @param id
	 * 		must be a valid Java variable name
	 * @param globalWidget
	 */
    protected OgemaWidgetBase(WidgetPage<?> page, String id, boolean globalWidget) {
    	if (!isValidJavaIdentifier(id))
    		throw new IllegalArgumentException("Could not create new widget, id " + id + " is not a valid Java identifier.");
    	this.id = id;
		this.page = (WidgetPageBase<?>) page;
        this.globalWidget = globalWidget;
        this.isSessionSpecific = false;
    	this.sessionManagement = this.page.registerNew(this, globalWidget);
    	this.registerJsDependencies();
    	if (!globalWidget && this.sessionManagement == null)
    		throw new NullPointerException("Session management is null");
    }
    
    @Deprecated
    protected OgemaWidgetBase(WidgetPage<?> page, String id, boolean globalWidget, SendValue sendValueOnChange) {
    	this(page, id, globalWidget);
		if(sendValueOnChange == SendValue.TRUE) {
			defaultSendValueOnChange = true;
		} else {
			defaultSendValueOnChange = false;
		}
    }	
	/** register widget as subwidget of some other widget / parent must implement 
	 *  {@link #registerSubWidget(OgemaWidget, OgemaHttpRequest)} method 
	 */
/*    public OgemaWidget(OgemaWidget<?> parent, String id, boolean globalWidget) {
    	this.id = parent.id + "_" + id;		// note: separators "/" and "." not Javascript-compatible 
		this.page = parent.page;
        this.globalWidget = globalWidget;
        if (globalWidget) {
//        	this.options = null;
        } else {
//        	 this.options = new ConcurrentHashMap<String, T>();
        	 this.globalOptions = null;
        }
    	this.parent = parent;
    	parent.registerSubWidget(this, null);
    	WidgetSessionData<T> sm = page.registerNew(this);
    	if (!globalWidget) {
    		this.sessionManagement = sm;
    	}else {
    		this.sessionManagement = null;
    	}
    } */
    
	/**
 	 * register widget as subwidget of some other widget / page- or session-specific widget: only exists for the session
	 * specified by the OgemaHttpRequest req, which must not be null  
 	 * @param parent
	 * @param id
	 * 		must be a valid Java variable name
	 * @param req
	 */
    protected OgemaWidgetBase(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	if (!isValidJavaIdentifier(id))
    		throw new IllegalArgumentException("Could not create new widget, id " + id + " is not a valid Java identifier.");
    	OgemaWidgetBase<?> par = (OgemaWidgetBase<?>) parent;
    	this.id = id + "_" + req.getSessionId();
		this.page = par.page;
        this.globalWidget = false;
        this.isSessionSpecific = true;
    	this.sessionManagement = par.page.registerNew(this, req);
    	if (par.preloadGroup)
    		par.getData(req).addPreloadWidget(this.id);
    	this.registerJsDependencies();
    }  
    
    @Deprecated
    protected OgemaWidgetBase(OgemaWidget parent, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
    	this(parent, id, req);
		if(sendValueOnChange == SendValue.TRUE) {
			defaultSendValueOnChange = true;
		} else {
			defaultSendValueOnChange = false;
		}
    }
    
    /**
     * TODO documentation... what is the difference to the method above?
     * @param page
     * @param id
     * 		must be a valid Java variable name
     * @param req
     */
    protected OgemaWidgetBase(WidgetPage<?> page, String id, OgemaHttpRequest req) {
    	if (!isValidJavaIdentifier(id))
    		throw new IllegalArgumentException("Could not create new widget, id " + id + " is not a valid Java identifier.");
		this.page = (WidgetPageBase<?>) page;
		this.isSessionSpecific = (req != null);
    	if(req == null) {
    		this.id = id;
            this.globalWidget = false;
        	this.sessionManagement = this.page.registerNew(this, false);
    	} else {
    		this.id = id + "_" + req.getSessionId();
            this.globalWidget = false; //displayed for a single page anyways
        	this.sessionManagement = this.page.registerNew(this, req);
    	}
    	this.registerJsDependencies();
    }  
//    protected OgemaWidget(WidgetPageI<?> page, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
//    	this(page, id, req);
//		if(sendValueOnChange == SendValue.TRUE) {
//			defaultSendValueOnChange = true;
//		} else {
//			defaultSendValueOnChange = false;
//		}
//    }	
    
    /**
     * A central utility function. If the widget is of global type (session-independent), null can be passed as argument. 
     *  
     */    
    public T getData(OgemaHttpRequest req) {
    	if (globalWidget) {
    		if (globalOptions == null) {
    			globalOptions = createNewSession();
    			globalOptions.initialRequest = null;
    			setDefaultValues(globalOptions);
//    			globalOptions.widget = this;
    		}
    		return globalOptions;
    	}
//    	if(req == null) {
//    		System.out.println("Id:"+getId());
//    	}
    	T opt = sessionManagement.getSessionData(req);
    	if (opt == null) throw new SessionExpiredException();
    	if (!opt.initialized) { // try to avoid synchronization overhead 
	    	synchronized (opt) {
	    		if (!opt.initialized) {	// need to recheck within synchronized block
	        		opt.initialRequest = req; //new HistoricHttpRequest(req);
	        		setDefaultValues(opt);
	        		if (this.preloadGroup)
	        			opt.preloadWidgets = new ArrayList<>(64); 
	        		opt.initialized = true;
//	        		opt.widget = this;
	        	}
			}
    	}
    	return opt;
    }
    
	/*********** functions to be overridden by derived class *************/
    
    public abstract Class<? extends OgemaWidgetBase<?>> getWidgetClass();
    
    /** Override to return subtype of WidgetOptions specific to the respective SubWidget, and set default values, if any. */
    public abstract T createNewSession();
    
    protected void setDefaultValues(T opt) {
    	opt.setPollingInterval(defaultPollingInterval);
        opt.setSendValueOnChange(defaultSendValueOnChange);
        opt.dynamicWidget = dynamicWidget;
        if (defaultStyles != null && !defaultStyles.isEmpty()) {
        	opt.setStyles(defaultStyles);
        }
        opt.setWidgetVisibility(defaultVisibility);
//        opt.globalConnectElements = globalConnectElements;
//        opt.globalConnectGroups = globalConnectGroups;
        opt.setWaitForPendingRequest(defaultWaitForPendingRequest);
		if (defaultWidth != null)
			opt.setWidth(defaultWidth);
		if (defaultMaxWidth != null)
			opt.setMaxWidth(defaultMaxWidth);
		if (defaultMinWidth != null)
			opt.setMinWidth(defaultMinWidth);
		if (defaultHeight != null)
			opt.setHeight(defaultHeight);
		if (defaultBackgroundColor != null)
			opt.setBackgroundColor(defaultBackgroundColor);
		if (defaultCssMap != null)
			opt.addCssMap(defaultCssMap);
		if (defaultTooltip != null)
			opt.setToolTip(defaultTooltip);
    }
    
    /*
     * Must be overridden if widget's package name does not end on widget name in lower case
     */
    protected void registerJsDependencies() {
    	Class<? extends OgemaWidgetBase<?>> clazz = getWidgetClass();
    	String className = clazz.getSimpleName();
    	String guessUrl = "/ogema/widget/" + className.toLowerCase() + "/" + className + ".js";
    	this.registerLibrary(true, className, guessUrl);
    }
    
    /*
     * ********** functions to be overridden by application ************
     */

//    public void onPOSTBeforeDependingWidgets(String data, OgemaHttpRequest req) {};
    
    @Override
    public void onGET(OgemaHttpRequest req) {};
    
    @Override
    public void onPrePOST(String data, OgemaHttpRequest req) {};
    
    @Override
    public void onPOSTComplete(String data, OgemaHttpRequest req) {};
   
    @Override
    public void updateDependentWidgets(OgemaHttpRequest req) {};
    
    /**
     * ******** public methods  *************
     */
    
    /**
     * @return widget instance id
     */
    @Override
	public final String getId() {
        return id;
    }
    
    
    @Override
	public void destroyWidget() {
    	if (globalWidget) {
    		if(globalOptions != null) {
    			globalOptions.destroy(); // removes potential subwidgets; note: the parent widget is not explicitly informed, we assume that it will be handled by the app
    									 // TODO remove subwidgets of non-global widgets?
    		}
    	}
        final Cache<String, OgemaWidgetBase<T>> parents = this.parents;
        if (parents != null) {
	    	for(OgemaWidgetBase<?> p: parents.asMap().values()) {
	    		p.unregisterDependentWidget(this);
	    	}
        }
        page.unregister(this);
//		sessionManagement and options will be destroyed via  OSGi service
    }
    
    public String getTag() {
        return "<div class=\"ogema-widget\" id=\"" + getId() + "\"></div>";
    }
    
    /**
     * If true, the widget data is shared between sessions. In any case,
     * the widget exists for all sessions.
     * @return
     */
    public boolean isGlobalWidget() {
    	return globalWidget;
    }
    
    /**
     * If true, the widgets exists only for a specific session
     * @return
     */
    public boolean isSessionSpecific() {
    	return isSessionSpecific;
    }
   
    public boolean isDynamicWidget() { 
        return dynamicWidget;
    }

    public void setDynamicWidget(boolean dynamicWidget) {
        this.dynamicWidget = dynamicWidget;
    }

    @Override
	public long getDefaultPollingInterval() {
		return defaultPollingInterval;
	}

	@Override
	public void setDefaultPollingInterval(long defaultPollingInterval) {
		this.defaultPollingInterval = defaultPollingInterval;
	}

	@Override
	public boolean isDefaultSendValueOnChange() {
		return defaultSendValueOnChange;
	}

	@Override
	public void setDefaultSendValueOnChange(boolean defaultSendValueOnChange) {
		this.defaultSendValueOnChange = defaultSendValueOnChange;
	}

	@Override
	public Set<WidgetStyle<?>> getDefaultStyles() {
		return Collections.unmodifiableSet(defaultStyles);
	}

	@Override
	public void setDefaultStyles(Collection<WidgetStyle<?>> defaultStyles) {
		if (defaultStyles==null) 
			this.defaultStyles = null;
		else
			this.defaultStyles = new LinkedHashSet<WidgetStyle<?>>(defaultStyles);
	}
	
	@Override
	public void setDefaultVisibility(boolean defaultVisibility) {
		this.defaultVisibility = defaultVisibility;
	}
	
	@Override
	public boolean getDefaultVisibility() {
		return defaultVisibility;
	}
	
	public boolean isVisible(OgemaHttpRequest req) {
		return getData(req).isVisible();
	}
	
	@Override
	public void addDefaultStyle(WidgetStyle<?> defaultStyle) {
		if (defaultStyle == null) 
			return;
		if (defaultStyles == null) 
			defaultStyles = new LinkedHashSet<>(); 
		defaultStyles.add(defaultStyle);
	}
	
	/**
	 * Add a css property to the widget's main div, such as "font-weight: bold;"
	 * @param property
	 * 		a css style property, such as "font-weight"
	 * @param value
	 * 		a css style value, such as "bold"
	 */
	public void addDefaultCssStyle(String property, String value) {
		addDefaultCssItem("", Collections.singletonMap(property, value));
	}
	
	/**
	 * Add a css property to the widget's main div, such as "font-weight: bold;"
	 * @param property
	 * 		a css style property, such as "font-weight"
	 * @param value
	 * 		a css style value, such as "bold"
	 */
	public void addCssStyle(String property, String value, OgemaHttpRequest req) {
		addCssItem("", Collections.singletonMap(property, value), req);
	}
	
	/**
	 * Set the margin of the widget for all four sides
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 */
	public void setDefaultMargin(String margin) {
		addDefaultCssItem("", Collections.singletonMap("margin", Objects.requireNonNull(margin)));
	}
	
	/**
	 * Set the margin of the widget for all four sides
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 * @param req
	 */
	public void setMargin(String margin, OgemaHttpRequest req) {
		addCssItem("", Collections.singletonMap("margin", Objects.requireNonNull(margin)), req);
	}
	
	
	/**
	 * Set the margin of the widget
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	public void setDefaultMargin(String margin, boolean top, boolean left, boolean bottom, boolean right) {
		final Map<String,String> props = new HashMap<>(4);
		if (top)
			props.put("margin-top", margin);
		if (bottom)
			props.put("margin-bottom", margin);
		if (left)
			props.put("margin-left", margin);
		if (right)
			props.put("margin-right", margin);
		addDefaultCssItem("", props);
	}
	
	/**
	 * Set the margin of the widget
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 * @param req
	 */
	public void setMargin(String margin, boolean top, boolean left, boolean bottom, boolean right, OgemaHttpRequest req) {
		final Map<String,String> props = new HashMap<>(4);
		if (top)
			props.put("margin-top", margin);
		if (bottom)
			props.put("margin-bottom", margin);
		if (left)
			props.put("margin-left", margin);
		if (right)
			props.put("margin-right", margin);
		addCssItem("", props, req);
	}
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 */
	public void setDefaultPadding(String padding) {
		addDefaultCssItem("", Collections.singletonMap("padding", padding));
	}
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 * @param req
	 */
	public void setPadding(String padding, OgemaHttpRequest req) {
		addCssItem("", Collections.singletonMap("padding", padding), req);
	}
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	public void setDefaultPadding(String padding, boolean top, boolean left, boolean bottom, boolean right) {
		final Map<String,String> props = new HashMap<>(4);
		if (top)
			props.put("padding-top", padding);
		if (bottom)
			props.put("padding-bottom", padding);
		if (left)
			props.put("padding-left", padding);
		if (right)
			props.put("padding-right", padding);
		addDefaultCssItem("", props);
	}
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 * @param req
	 */
	public void setPadding(String padding, boolean top, boolean left, boolean bottom, boolean right, OgemaHttpRequest req) {
		final Map<String,String> props = new HashMap<>(4);
		if (top)
			props.put("padding-top", padding);
		if (bottom)
			props.put("padding-bottom", padding);
		if (left)
			props.put("padding-left", padding);
		if (right)
			props.put("padding-right", padding);
		addCssItem("", props, req);
	}
    
	/**
	 * Set to true in order to prevent two requests from the same session to be processed in parallel. 
	 * Applies to both GET and POST requests. <br>
	 * Note that this must be set before the session is created in order to take effect 
	 * (preferably set it right after creating the widget).<br>
	 * Default: false
	 */
    public void setWaitForPendingRequest(boolean wait) {
    	defaultWaitForPendingRequest = wait;
    }

	/**
	 * Use to force HTML rewrites on every GET, even though the content has not changed. <br>
	 * Note: this parameter is only evaluated by certain widgets (e.g. see org.ogema.tools.widget.html.popup.Popup)
	 */
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	
	@Override
	public void postponeLoading() {
		this.postponeLoading = true;
	}
	
	public boolean isPostponeLoading() {
		return postponeLoading;
	}

	@Override
	public void preloadSubwidgets() {
		this.preloadGroup = true;
	}
	
	public boolean isPreloadSubwidgets() {
		return this.preloadGroup;
	}
	
	public List<String> getPreloadWidgets(OgemaHttpRequest req) {
		return this.preloadGroup ? getData(req).getPreloadWidgets() : null;
	}
	
	// FIXME still needed?
    /**
     * Set widget to be the init widget of the page. This widget should make
     * sure that it triggers updates of all dependent widgets. The method should
     * only be called once per page.
     */
//    public void makeInitWidget() {	
//        getPage().setWidgetInitStatus(this, true);
//    }

/*    public void notifyResourceChanged(String pageInstanceId) {  // TODO (but probably rather in ResourceWidget than here...)
        pageInstancesToUpdate.add(pageInstanceId);
    } */
    
    /*
     * ************* Public methods delegated to options object ***********
     */
    
	@Override
	public long getPollingInterval(OgemaHttpRequest req) {
        return getData(req).getPollingInterval();
    }

    /**
     *
     * @param pollingInterval; in ms; if &lt;=0, then no polling
     */
    @Override
	public void setPollingInterval(long pollingInterval, OgemaHttpRequest req) {
    	getData(req).setPollingInterval(pollingInterval);
    }

    /**
     * outer key is the classname to which the CSS properties apply, inner keys
     * are CSS-properties<br>
     * see {@link #addCssItem(String, Map, OgemaHttpRequest)}
     */
    @Override
	public Map<String, Map<String, String>> getCssMap(OgemaHttpRequest req) {
        return getData(req).getCssMap();
    }

    /**
     * see {@link #addCssItem(String, Map, OgemaHttpRequest)}
     */
    @Override
	public void setCssMap(Map<String, Map<String, String>> css, OgemaHttpRequest req) {
    	getData(req).setCssMap(css);
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
     * <li>"#myId", in which case it only applies to a subelement with the
     * specified ID</li>
     * </ul>
     *
     */
    @Override
	public void addCssItem(String selector, Map<String, String> value, OgemaHttpRequest req) {
    	getData(req).addCssItem(selector, value);
    }
    
    public void addDefaultCssItem(String selector, Map<String,String> value) {
    	Objects.requireNonNull(selector);
    	Objects.requireNonNull(value);
    	value.entrySet().forEach(entry -> {
    		Objects.requireNonNull(entry.getKey());
    		Objects.requireNonNull(entry.getValue());
    	});
    	if (defaultCssMap == null)
    		defaultCssMap = new LinkedHashMap<>(4);
    	 Map<String,String> old = defaultCssMap.get(selector);
    	 if (old == null) {
    		 old = new HashMap<>(4);
    		 defaultCssMap.put(selector, old);
    	 }
    	 for (Map.Entry<String, String> entry : value.entrySet()) {
    		 old.put(entry.getKey(), WidgetData.escapeHtmlAttributeValue(entry.getValue()));
    	 }
    }
    
    /**
     * see {@link #addCssItem(String, Map, OgemaHttpRequest)}
     */
    @Override
	public void removeCSSItems(String selector, OgemaHttpRequest req) {
    	getData(req).removeCSSItems(selector);
    }

    /**
     * see {@link #addCssItem(String, Map, OgemaHttpRequest)}
     */
    @Override
	public void removeCSSItem(String selector, String property, OgemaHttpRequest req) {
    	getData(req).removeCSSItem(selector, property);
    }
    
    public void setDefaultBackgroundColor(String color) {
    	this.defaultBackgroundColor = color;
    }
    
    public void setBackgroundColor(String color, OgemaHttpRequest req) {
    	getData(req).setBackgroundColor(color);
    }

    public void setDefaultToolTip(String tooltip) {
    	this.defaultTooltip = tooltip;
    }
    
    public void setToolTip(String tooltip, OgemaHttpRequest req) {
		this.getData(req).addAttribute("title", tooltip);
	}
	
	public boolean removeToolTip(OgemaHttpRequest req) {
		return this.getData(req).removeAttribute("title");
	}
    
    @Override
	public void addStyle(WidgetStyle<?> style, OgemaHttpRequest req) {
    	getData(req).addStyle(style);
    }
    
    public void setStyles(Collection<WidgetStyle<?>> styles, OgemaHttpRequest req) {
    	getData(req).setStyles(styles);
    }

    @Override
	public void setStyle(WidgetStyle<?> style, OgemaHttpRequest req) {
    	getData(req).setStyle(style);
    }

    @Override
	public boolean removeStyle(WidgetStyle<?> style, OgemaHttpRequest req) {
        return getData(req).removeStyle(style);
    }

    /**
     * returns a copy of the list of styles
     */
    @Override
	public List<WidgetStyle<?>> getStyles(OgemaHttpRequest req) {
        return getData(req).getStyles();
    }

    // TODO add documentation
    @Override
	public void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req) {
    	triggerAction(target, triggeringAction, triggeredAction, 0, req);
    }

    // TODO unregisterTriggerAction
    @Override
    public void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level, OgemaHttpRequest req) {
    	getData(req).triggerAction(target, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),level);
    }
    
    @Override
	public void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction) {
        triggerAction(target, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),0);
    }
    
    @Override
    public void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level) {
        triggerAction(target, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),level);
    }

    void triggerAction(OgemaWidget target, String triggeringAction, String triggeredAction, Object[] args, int level) {
        Map<String,Object> cncObj = new LinkedHashMap<String, Object>();
        String widgetID2 = target.getId();
        cncObj.put("widgetID1", id);
        cncObj.put("widgetID2", widgetID2);
        cncObj.put("triggeringAction", triggeringAction);
        cncObj.put("triggeredAction", triggeredAction);
        if (args != null) {
            cncObj.put("args", args);
        }
        cncObj.put("level", level);
        globalConnectElements.add(cncObj);       
        page.app.getWidgetService().update(this);
    }
    
    void removeTriggerAction(OgemaWidget target, String triggeringAction, String triggeredAction, Object[] args) {
    	String widgetID2 = target.getId();
    	for(int i=globalConnectElements.size()-1; i>=0; i--) {
    		Map<String,Object> cnObj = globalConnectElements.get(i);
    		if(id.equals(cnObj.get("widgetID1")) &&
    				widgetID2.equals(cnObj.get("widgetID2")) &&
    				triggeringAction.equals(cnObj.get("triggeringAction")) &&
    				triggeredAction.equals(cnObj.get("triggeredAction"))) {
    			globalConnectElements.remove(cnObj);
    			break;
    		}
    	}
    }
    @Override
	public void removeTriggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction) {
    	removeTriggerAction(target, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs());
    }
    
    /**
     * Remove all session-specific widget and group triggers. Global triggers are unaffected.
     * @param req
     */
 	public void clearSessionTriggers(OgemaHttpRequest req) {
    	getData(req).clearTriggerActions();
    }
    
  	public void removeTriggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req) {
    	getData(req).removeTriggerAction(target);
    }
  	
    public void triggerAction(OgemaWidget target, String triggeringAction, String triggeredAction) {
        triggerAction(target, triggeringAction, triggeredAction, (Object[]) null, 0);
    }
    
    // TODO add documentation
    @Override
	public void triggerAction(WidgetGroup triggeredGroup, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req) {
    	getData(req).triggerGroupAction(triggeredGroup, triggeringAction, triggeredAction);
    }

    public void triggerGroupAction(WidgetGroup triggeredGroup, String triggeringAction, String triggeredAction, Object[] args, OgemaHttpRequest req) {
    	getData(req).triggerGroupAction(triggeredGroup, triggeringAction, triggeredAction, args,0);
    }

    public void triggerGroupAction(WidgetGroup triggeredGroup, String triggeringAction, String triggeredAction, OgemaHttpRequest req) {
    	getData(req).triggerGroupAction(triggeredGroup, triggeringAction, triggeredAction);
    }
    
    @Override
    public void triggerAction(WidgetGroup triggeredGroup, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level, OgemaHttpRequest req) {
    	getData(req).triggerGroupAction(triggeredGroup, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),level);
    }
    

    @Override
	public void triggerAction(WidgetGroup triggeredGroup, TriggeringAction triggeringAction, TriggeredAction triggeredAction) {
        triggerGroupAction(triggeredGroup, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),0);
    }
    
    @Override
    public void triggerAction(WidgetGroup triggeredGroup, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level) {
        triggerGroupAction(triggeredGroup, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs(),level);
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
        globalConnectGroups.add(cncObj);
    }

    public void triggerGroupAction(WidgetGroup triggeredGroup, String triggeringAction, String triggeredAction) {
        triggerGroupAction(triggeredGroup, triggeringAction, triggeredAction, (Object[]) null,0);
    }
    
    @Override
    public void removeTriggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction) {
    	removeTriggerAction(target, triggeringAction.getAction(), triggeredAction.getAction(), triggeredAction.getArgs());
    }
    
    public void removeTriggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req) {
    	getData(req).removeTriggerAction(target);
    }
    
    void removeTriggerAction(WidgetGroup target, String triggeringAction, String triggeredAction, Object[] args) {
    	String groupId = target.getId();
    	for(int i=globalConnectGroups.size()-1; i>=0; i--) {
    		Map<String,Object> cnObj = globalConnectGroups.get(i);
    		if(id.equals(cnObj.get("widgetID1")) &&
    				groupId.equals(cnObj.get("widgetID2")) &&
    				triggeringAction.equals(cnObj.get("triggeringAction")) &&
    				triggeredAction.equals(cnObj.get("triggeredAction"))) {
    			globalConnectGroups.remove(cnObj);
    			break;
    		}
    	}
    }
    
    /**
     * A dependent widget will updated itself on the client side when a POST
     * request on the governing widget is finished. In this way the server can
     * update the dependent widgets when a new value for the governing widget is
     * received. When the page is initialized or when a POST is received on the
     * server side the generation of the new JSON data of the dependent widget
     * is triggered by the governing widget so that the page contains consistent
     * data
     *
     * @param other
     */
    @Override
    public void registerDependentWidget(OgemaWidget other) {
    	OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) other;
    	// TODO group dependency 
    	triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
    	//triggerAction(widget, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
    	registerInternalDependency(widget);
    }

    protected void registerInternalDependency(OgemaWidgetBase<?> widget) {
        getDependencies().add(widget);
        widget.getParents().put(id,(OgemaWidgetBase) this);
        governingWidget = true;
    }
    
    @Override
    public void registerDependentWidget(OgemaWidget other, OgemaHttpRequest req) {
    	OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) other;
        getData(req).triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        registerInternalDependency(widget, req);
    }
    
    protected void registerInternalDependency(OgemaWidgetBase<?> widget, OgemaHttpRequest req) {
        getData(req).getsessionDependencies().add(widget);
        governingWidget = true;    	
    }
    
    @Deprecated
    private Cache<String, OgemaWidgetBase<T>> getParents() {
        Cache<String, OgemaWidgetBase<T>> parents = this.parents;
        if (parents == null) {
        	synchronized (this) {
        		parents = this.parents;
        		if (parents == null) {
        			parents = CacheBuilder.newBuilder()
        					.initialCapacity(4)
        					.concurrencyLevel(2)
        					.weakValues()
        					.build();
        			this.parents = parents;
        		}
			}
        }
        return parents;
    }
    
    @Deprecated
    private Set<OgemaWidgetBase<?>> getDependencies() {
    	Set<OgemaWidgetBase<?>> dependencies = this.dependencies;
    	if (dependencies == null) {
    		synchronized (this) {
    			dependencies = this.dependencies;
    	    	if (dependencies == null) {
    	    		dependencies = Collections.newSetFromMap(new ConcurrentHashMap<>(4));
    	    		this.dependencies = dependencies;
    	    	}
			}
    	}
    	return dependencies;
    }
    
    private Set<String> getGroups() {
    	Set<String> groups = this.groups;
    	if (groups == null) {
    		synchronized (this) {
				groups = this.groups;
				if (groups==null) {
					groups = new HashSet<>(4);
					this.groups = groups;
				}
			}
    	}
    	return groups;
    }
    
    @Deprecated
    @Override
    public void unregisterDependentWidget(OgemaWidget other) {
    	OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) other;
    	removeTriggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
    	final Set<OgemaWidgetBase<?>> dependencies = this.dependencies;
    	if (dependencies != null)
    		dependencies.remove(widget);
    }

    @Override
    public void triggerOnPOST(OgemaWidget other) {
     	triggerAction(other, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
    }

    @Override
    public void triggerOnPOST(OgemaWidget other, OgemaHttpRequest req) {
    	OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) other;
        getData(req).triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
    }
    
    @Override
	public void setWidgetVisibility(boolean visible, OgemaHttpRequest req) {
    	getData(req).setWidgetVisibility(visible);
    }

	/**
	 * Set the default width
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 */
	@Override
	public void setDefaultWidth(String width) {
		this.defaultWidth = width;
		WidgetData o = createNewSession();
		o.getWidthSelector(); // just to test that this is implemented
	}
	
	/**
	 * Set the default max-width
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 */
	@Override
	public void setDefaultMaxWidth(String width) {
		this.defaultMaxWidth = width;
		WidgetData o = createNewSession();
		o.getWidthSelector(); // just to test that this is implemented
	}
	
	/**
	 * Set the default min-width
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 */
	@Override
	public void setDefaultMinWidth(String width) {
		this.defaultMinWidth = width;
		WidgetData o = createNewSession();
		o.getWidthSelector(); // just to test that this is implemented
	}
	
	/**
	 * Set the width
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 * @param req
	 */
	@Override
	public void setWidth(String width, OgemaHttpRequest req) {
		getData(req).setWidth(width);
	}
	
	/**
	 * Set the max-width
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 * @param req
	 */
	@Override
	public void setMaxWidth(String width, OgemaHttpRequest req) {
		getData(req).setMaxWidth(width);
	}
    
	/**
	 * Set the min-width
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 * @param req
	 */
	@Override
	public void setMinWidth(String width, OgemaHttpRequest req) {
		getData(req).setMinWidth(width);
	}
	
	@Override
	public void setDefaultHeight(String height) {
		this.defaultHeight = height;
		WidgetData o = createNewSession();
		o.getWidthSelector(); // just to test that this is implemented
	}
	
	@Override
	public void setHeight(String height, OgemaHttpRequest req) {
		getData(req).setHeight(height);
	}
	
    /**
     * only relevant to widgets displaying a value that can be changed by the
     * user
     */
    @Override
	public boolean isSendValueOnChange(OgemaHttpRequest req) {
        return getData(req).doSendValueOnChange();
    }

    /**
     * only relevant to widgets displaying a value that can be changed by the
     * user
     */
    @Override
	public void setSendValueOnChange(boolean sendValueOnChange, OgemaHttpRequest req) {
    	getData(req).setSendValueOnChange(sendValueOnChange);
    }    

    @Override
	public boolean isDisabled(OgemaHttpRequest req) {
        return getData(req).isDisabled();
    }

    @Override
	public void disable(OgemaHttpRequest req) {
    	getData(req).disable();
    }

    @Override
	public void enable(OgemaHttpRequest req) {
    	getData(req).enable();
    }

    /*
     * -> Javadoc doesn't accept Javascript in comments
     * 
     * Add an HTML attribute and a corresponding Javascript action, like in
     * <button onclick&#61;"window&#46;open(otherPage&#46;html,_blank)&#59;">
     *
     * @param attr: HTML attribute, e.g. "onclick"
     * @param action: Javascript function to execute, e.g.
     * <code>window&#46;open(otherPage&#46;html,_blank)&#59;</code>
     */
    @Override
	public void addAttribute(String attr, String action, OgemaHttpRequest req) {
    	getData(req).addAttribute(attr, action);
    }

    /**
     * Remove HTML attribute, like "onclick"
     */
    @Override
	public boolean removeAttribute(String attr, OgemaHttpRequest req) {
        return getData(req).removeAttribute(attr);
    }

    /**
     * Get Javascript function associated to attribute attr
     */
    @Override
	public String getAttribute(String attr, OgemaHttpRequest req) {
        return getData(req).getAttribute(attr);
    }
    
    @Override
    public String toString() {
    	return "Widget " + id + ", type: " + getWidgetClass().getSimpleName();
    }
    
    /*************** protected functions ***********/
    
    @Override
	public WidgetPageBase<?> getPage() {
    	return page;
    }

	/**
	 * Registering a Javascript or CSS library here can in some cases lead to faster loading results; it allows the library to be provided by an external server, instead of 
	 * the OGEMA gateway.<br> 
	 * Even libraries that have been registered via this method should still be referenced in the widget's HTML file, in order for all fallback mechanisms to work properly.
	 * @param type
	 * 		true: Javascript, false: CSS
	 * @param identifier
	 * 		for JS libs: Javascript: 'if (identifier) { }' must be satisfied iff the library has been loaded<br>
	 * 		For instance, one can check whether the jquery lib has been by loaded already through 'if (jQuery) { console.log("everything fine"); } else { throw "jquery missing" }'
	 * 		Hence the identifier for jquery is simply "jQuery". Typically, for other libraries similar global Javascript variables exist that can be used to check whether the 
	 * 		library has been loaded or not, and one of those should be used as identifier. If no such global identifier variable exists for a given library it should not be registered
	 * 		via this method, but only loaded through the widget's HTML file.<br>
	 * @param path
	 * 		relative url w.r.t. OGEMA base URL, e.g. "/ogema/jslib/widgetLoader.js"
	 */
    protected final void registerLibrary(boolean type, String identifier,String path) {
    	if (page instanceof ServletBasedWidgetPage) {
	    	HtmlLibrary library = new HtmlLibrary(type ? LibType.JS : LibType.CSS, identifier, path);
	    	((ServletBasedWidgetPage<?>) page).registerLibrary(library);
    	}
    }

    /**
     * ******** internal methods  *************
     */
    
    public void appendWidgetInformation(OgemaHttpRequest req, JSONObject result) {
    	onGET(req);
		getData(req).appendWidgetInformation(req,result);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
    	try{
    		OgemaHttpRequest ogReq = new OgemaHttpRequest(req, true);
	        WidgetData opt = getData(ogReq);
	        setLastInteractionTime(ogReq);
	        
        	onGET(ogReq); // set values of this widget
        	JSONObject results = opt.getWidgetInformation(ogReq);
        	updateDependentWidgets(ogReq); // set values in other widgets
        	
	        if (forceUpdate) results.put("forceUpdate", forceUpdate);
	        resp.setContentType("application/json");
	        resp.setCharacterEncoding("UTF-8");
	        resp.getWriter().write(results.toString());
	        resp.setStatus(200);
    	} catch (SessionExpiredException e) {
    		JSONObject results = new JSONObject("{expired: true}");
    		resp.setContentType("application/json");
    		resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(results.toString());
            resp.setStatus(500);
    	} catch (Throwable e) {
    		e.printStackTrace();
//    		throw new RuntimeException("Server error",e);
    		// FIXME only show exception if debug mode is activated
    		String msg = "An exception has occured. " + serializeHtml(e);
    		JSONObject results = new JSONObject();
    		results.put("exception", msg);
    		resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(results.toString());
            resp.setStatus(500);
    	}

    }
    
    private void appendOrderedWidgetsOnPost(OgemaHttpRequest req, JSONObject result) { 
    	List<OgemaWidgetBase<?>> dependencies = new ArrayList<>();
    	gatherTransitiveDependencies(this, dependencies, true, req);
    	getPage().app.getWidgetService().sortWidgets(dependencies);
    	for (OgemaWidgetBase<?> dependency: dependencies) {
    		dependency.appendWidgetInformation(req, result);
    	}
    }

    private static final void gatherTransitiveDependencies(OgemaWidgetBase<?> widget, List<OgemaWidgetBase<?>> dependencies, boolean init, OgemaHttpRequest req) {
    	if (dependencies.contains(widget))
    		return;
    	if (!init)
    		dependencies.add(widget); // base widget is not added to its own dependencies
    	final Set<OgemaWidgetBase<?>> deps = widget.dependencies;
    	if (deps != null) {
	    	for (OgemaWidgetBase<?> dep: deps) {
	    		gatherTransitiveDependencies(dep, dependencies, false, req);
	    	}
    	}
    	final Set<OgemaWidgetBase<?>> sessionDeps = widget.getData(req).sessionDependencies;
    	if (sessionDeps != null) {
	    	for (OgemaWidgetBase<?> dep: sessionDeps) {
	    		gatherTransitiveDependencies(dep, dependencies, false, req);
	    	}
    	}
    	
    }
  
      // ordering missing
/*    private void appendWidgetsOnPost(OgemaWidgetBase<?> widget, OgemaHttpRequest req, List<OgemaWidgetBase<?>> done, JSONObject result) {
       Iterator<OgemaWidgetBase<?>> it = widget.dependencies.iterator();
       while(it.hasNext()) {
	   		OgemaWidgetBase<?> w = it.next();
            if (done.contains(w)) {
                continue;
            }
            w.appendWidgetInformation(req, result);
            done.add(w);
            appendWidgetsOnPost(w, req, done, result);
       }
       for(OgemaWidgetBase<?> w: getData(req).sessionDependencies) {
           if (done.contains(w)) {
               continue;
           }
           w.appendWidgetInformation(req, result);
           done.add(w);
           appendWidgetsOnPost(w, req, done, result);
       }
    }
*/    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //System.out.println("POST request to instance with id " + id);
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
            String request = sb.toString();  // TODO convert to JSON immediately?
            OgemaHttpRequest ogReq = new OgemaHttpRequest(req, false);
            //The governing widget should start the dependency chain in its onPOST method, so the flag is not required here // outdated
//            ogReq.startProcessingDependencies = false;
            WidgetData opt = getData(ogReq);
//            if (opt == null) {  // session expired
//            	JSONObject results = new JSONObject("{expired: true}");
//                resp.getWriter().write(results.toString());
//                resp.setStatus(200);
//                return;
//            }

            JSONObject result = null;
            //if(postSemaphore) Thread.sleep(0);
            //try { postSemaphore = true;
            onPrePOST(request, ogReq);
            // handle widgets whose sendPOST is triggered by this widget's prePOST 
            try {
            	JSONObject requestObj = new JSONObject(request);
            	sessionManagement.handleTriggeredPOSTs(requestObj,ogReq);
            } catch (JSONException e) {
            	LoggerFactory.getLogger(getClass()).error("Could not trigger POST for " + e);
            }
            // FIXME remove case distinction?
            if (governingWidget) {
            	result = new JSONObject();
//                JSONObject widgetReply = opt.onPOST(request, ogReq);
//                ogReq.json.append("onPOSTReply", widgetReply.toString());
            	result.append("onPOSTReply", opt.onPOST(request, ogReq));
            	onPOSTComplete(request, ogReq);
                updateDependentWidgets(ogReq);
//                onPOSTBeforeDependingWidgets(request, ogReq);
                //now collect also the actual updated JSON information for the dependent widgets
//                appendWidgetsOnPost(this, ogReq, new ArrayList<OgemaWidgetBase<?>>(), result);
                appendOrderedWidgetsOnPost(ogReq, result);
            } else {
                result = opt.onPOST(request, ogReq);
                onPOSTComplete(request, ogReq);
                updateDependentWidgets(ogReq);
           }
//            onPOSTComplete(request, ogReq);
            //} catch(Exception e) {e.printStackTrace();}finally{postSemaphore=false;}
           
//            if (ogReq.json.length() > 0) {
//                //resp.setContentType("application/json");
//                resp.getWriter().write(ogReq.json.toString());
//            } else if (result != null && !result.isEmpty()) {
//                resp.getWriter().write(result);
//            }
//            else {// the widget would throw an exception in this case anyway
//            	throw new IllegalStateException("Widget POST data inconsistent");
//            }
            if (result == null) {
            	LoggerFactory.getLogger(getClass()).warn("JSON POST response null, this should probably not happen: {}", this);
                result = new JSONObject();
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(result.toString());
            resp.setStatus(200);
    	} catch (SessionExpiredException e) {
    		JSONObject results = new JSONObject("{expired: true}");
    		resp.setContentType("application/json");
    		resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(results.toString());
            resp.setStatus(500);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
    	} catch (Throwable e) {
    		e.printStackTrace();
//    		throw new RuntimeException("Server error",e);
    		// FIXME only show exception if debug mode is activated
    		String msg = "An exception has occured. " + serializeHtml(e);
    		JSONObject results = new JSONObject();
    		results.put("exception", msg);
    		resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(results.toString());
            resp.setStatus(500);
		}

    }

    @Override
    public int hashCode() {
    	return id.hashCode();
    }
    
    // two widgets on the same page with the same id are equal!
    @SuppressWarnings("rawtypes")
	@Override
    public boolean equals(Object obj) {
    	if (obj == this) return true;
    	if (obj == null || !(obj instanceof OgemaWidgetBase)) return false;
    	OgemaWidgetBase<?> other = (OgemaWidgetBase<?>) obj;
    	if (!this.page.equals(other.page)) return false;
    	if (!((OgemaWidgetBase) obj).page.equals(page)) return false; 
    	return ((OgemaWidgetBase) obj).getId().equals(id);
    }
    
    /** Pass req = null if this is a global widget */
 /*   public void registerSubWidget(OgemaWidget<?> subwidget, OgemaHttpRequest req) {
    	getOptions(req).registerSubWidget(subwidget);
    } */
    
    /** Pass req = null if this a global widget */
    public boolean unregisterSubWidget(OgemaWidgetBase<?> subwidget, OgemaHttpRequest req) {
    	WidgetData opt = getData(req);
    	if (opt == null) return false;		// may happen that session has expired already
    	return opt.removeSubWidget(subwidget);
    }
    
    private void setLastInteractionTime(OgemaHttpRequest req) {
        if (!globalWidget) sessionManagement.setLastInteractionTime(req);
    }

    /**
     * Internal method
     * @param groupId
     */
    public void addGroup(String groupId) { // FIXME should not be public, but necessary since WidgetGroupImpl must access the method
    	getGroups().add(groupId);
    }
    
    /**
     * Internal method
     * @param groupId
     */
    public boolean removeGroup(final String groupId) {  // FIXME should not be public, but necessary since WidgetGroupImpl must access the method
    	final Set<String> groups = this.groups;
    	if (groupId != null && groups != null)
    		return groups.remove(groupId);
    	else
    		return false;
    }
    
    private final static String serializeHtml(Throwable e) {
    	StringBuilder sb = new StringBuilder(e.toString() + "<br>");
    	for (StackTraceElement ste: e.getStackTrace()) {
			sb.append(ste.toString() + "<br>");
		}
    	Throwable cause = e.getCause();
    	if (cause != null) {
    		sb.append("<br>caused by: " + serializeHtml(cause));
    	}
    	return sb.toString();
    }
    
    private final static boolean isValidJavaIdentifier(String input) {
    	// numbers as start character are fine here -> no, they are not... leads to errors
        if (input == null ||input.isEmpty() || !Character.isJavaIdentifierStart(input.charAt(0)))  
            return false;
        for (int i = 1; i < input.length(); i++) {
            if (!Character.isJavaIdentifierPart(input.charAt(i))) {
            	if (input.charAt(i) != '-') // whitelisted
            		return false;
            }
        }
        return true;
    }
    
    protected final IconService getIconService() {
    	return page.getApp().getIconService();
    }
    
    protected final NameService getNameService() {
    	return page.getApp().getNameService();
    }
    
    // no locks are required if the widget is not global. In this case all requests associated to one session
    // are executed in the same thread
    protected final void readLock(OgemaHttpRequest req) {
 	   getData(req).readLock();
    }
    
    protected final void readUnlock(OgemaHttpRequest req) {
    	getData(req).readUnlock();
    }
    
    protected final void writeLock(OgemaHttpRequest req) {
    	getData(req).writeLock();
    }
    
    protected final void writeUnlock(OgemaHttpRequest req) {
    	getData(req).writeUnlock();
    }
    
}
