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
package de.iwes.widgets.api.widgets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * An Html template that can be added to a {@link WidgetPage}.
 */
public interface OgemaWidget {

	/**
	 * @return
	 * 		The unique (per page) id of this widget. Must be a valid Java variable name.
	 */
	String getId();
	
	/**
	 * Get the page this widget belongs to. 
	 * @return
	 */
	WidgetPage<?> getPage();
	
	/* 
	 ******* Override the following methods in your app's derived classes *******
	 */
	
	/**
	 * Override this method to perform some action before a GET request to this widget
	 * from a user page is executed server-side.
	 * <br>
	 * This method is particularly suited to an object-oriented approach to widget dependencies:
	 * the widget updates itself every time its data are requested. On the other hand,
	 * the {@link #updateDependentWidgets(OgemaHttpRequest)} method can be used to update 
	 * other widgets within the request for a "governing widget".  
	 *  
	 * @param req
	 * 		identifies the user session and contains all request information
	 */
    void onGET(OgemaHttpRequest req);
    
	/**
	 * Override this method to perform some action before a POST request to this widget
	 * from a user page is executed server-side. 
	 * @param data
	 * 		the request body; this is widget-specific, and need not normally
	 * 		be evaluated in an app
	 * @param req
	 * 		identifies the user session and contains all request information
	 */
    void onPrePOST(String data, OgemaHttpRequest req);
    
	/**
	 * Override this method to perform some action after a POST request to this widget
	 * from a user page is executed server-side and before the answer is sent back to the client.
 	 *
	 * @param data
	 * 		the request body; this is widget-specific, and need not normally
	 * 		be evaluated in an app
	 * @param req
	 * 		identifies the user session and contains all request information
	 */
    void onPOSTComplete(String data, OgemaHttpRequest req);
    
    /**
     * Override if needed. The method is intended for a top-down approach
     * for updating dependent widgets, where the "governing widget" sets the values 
     * of its dependent widgets. It is called after each POST request of the governing 
     * widget and also after each GET request. Widgets whose values are updated in this method 
     * should be added to the dependency chain, via {@link #registerDependentWidget(OgemaWidget)}.
     * 
     * @param req
	 * 		identifies the user session and contains all request information
     */
    @Deprecated
    void updateDependentWidgets(OgemaHttpRequest req);

    
    /*
     *********** Other methods ************
     */
    
    // Note: this was previously called "destroy", which led to a name collision with GenericServlet#destroy
    /**
     * Destroy this widget, and unregister its servlet from the Http service.
     * Also removes all subwidgets. This need not normally be called within an application,
     * but is rather used by container widgets, who manage the lifecycle of their subwidgets. 
     */
    void destroyWidget();
    
    /**
     * Returns the default polling interval
     * @return
     * 	-1 if polling has not been configured for this widget, the polling interval in ms otherwise.  
     */
    long getDefaultPollingInterval();

    /**
     * Set the default polling interval. Note that this only takes effect for sessions
     * which do not yet exist at the time of execution.
     * @param defaultPollingInterval
     * 		in ms. Set to negative value to disable polling.
     */
	void setDefaultPollingInterval(long defaultPollingInterval);
	
	/**
	 * Get the polling interval for a specific session.
	 * @param req
	 * @return
	 * 		-1 if polling is disabled for this session, the polling interval in ms otherwise.  
	 */
	long getPollingInterval(OgemaHttpRequest req);

    /**
     * Set the polling interval for a specific session.
     * @param pollingInterval; in ms; if &lt;=0, then no polling
     */
    void setPollingInterval(long pollingInterval, OgemaHttpRequest req);
    
    /**
     * @see #setDefaultSendValueOnChange(boolean)
     * @return 
     */
	boolean isDefaultSendValueOnChange();

    /**
     * Set whether the widget shall send its value to the server on every update through the user. 
     * Think of a text field: the server can be updated either every time a user enters a new word,
     * or server-side updates could be triggered only when the user clicks a "Save"-button (a Form-like concept). 
     * 	
     */
	void setDefaultSendValueOnChange(boolean defaultSendValueOnChange);
	
    /**
     * @see #isDefaultSendValueOnChange()
     * session-specific version
     */
    boolean isSendValueOnChange(OgemaHttpRequest req);

    /**
     * @see #setDefaultSendValueOnChange(boolean)
     * session-specific version - should be used only in exceptional cases.
     */
    void setSendValueOnChange(boolean sendValueOnChange, OgemaHttpRequest req);
	
    /**
     * Get widget styles applicable to all sessions.
     * @return
     */
	Set<WidgetStyle<?>> getDefaultStyles();

	/**
	 * Set styles, applicable to all sessions
	 * @param defaultStyles
	 */
	void setDefaultStyles(Collection<WidgetStyle<?>> defaultStyles);
	
	/**
	 * @see OgemaWidget#setDefaultStyles(Collection)
	 * @param defaultStyle
	 */
	void addDefaultStyle(WidgetStyle<?> defaultStyle);
	
	/**
	 * @see #addDefaultStyle(WidgetStyle)
	 * session-specific version
	 * @param style
	 * @param req
	 */
    void addStyle(WidgetStyle<?> style, OgemaHttpRequest req);

    /**
     * Like {@link OgemaWidget#addStyle(WidgetStyle, OgemaHttpRequest)}, except that 
     * any previous style definitions are removed.
     * @param style
     * @param req
     */
    void setStyle(WidgetStyle<?> style, OgemaHttpRequest req);

    /**
     * Remove style from a widget for the specified session.
     * @param style
     * @param req
     * @return
     */
    boolean removeStyle(WidgetStyle<?> style, OgemaHttpRequest req);

    /**
     * Get the style definitions for this widget and the specified session.
     * @param req
     * @return
     * 		a copy of the list of styles 
     */
    List<WidgetStyle<?>> getStyles(OgemaHttpRequest req);
	
	/**
	 * Set initial widget visibility
	 * @param defaultVisibility
	 */
	void setDefaultVisibility(boolean defaultVisibility);
	
	/**
	 * @see #setDefaultVisibility(boolean)
	 * @return
	 */
	boolean getDefaultVisibility();
	
	/**
	 * @see #setDefaultVisibility(boolean)
	 * session-specific version
	 * @param visible
	 * @param req
	 */
    void setWidgetVisibility(boolean visible, OgemaHttpRequest req);

	/**
	 * Set the default width. 
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 */
	void setDefaultWidth(String width);
	
	/**
	 * Set the default max-width. 
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 */
	void setDefaultMaxWidth(String width);
	
	/**
	 * Set the default min-width. 
	 * @param width
	 * 		An HTML width identifier, such as "20%", or "60px";
	 */
	void setDefaultMinWidth(String width);
    
	/**
	 * @see #setDefaultWidth(String)
	 * session-specific version
	 * @param width
	 * @param req
	 */
	void setWidth(String width, OgemaHttpRequest req);

	/**
	 * @see #setDefaultWidth(String)
	 * session-specific version
	 * @param width
	 * @param req
	 */
	void setMaxWidth(String width, OgemaHttpRequest req);

	/**
	 * @see #setDefaultWidth(String)
	 * session-specific version
	 * @param width
	 * @param req
	 */
	void setMinWidth(String width, OgemaHttpRequest req);

	
	/**
	 * Set the default height. 
	 * @param height
	 * 		e.g. "20%", or "60px";
	 */
	void setDefaultHeight(String height);
    
	/**
	 * @see #setDefaultHeight(String)
	 * session-specific version
	 * @param height
	 * @param req
	 */
	void setHeight(String height, OgemaHttpRequest req);
    
	/**
	 * @see #addCssItem(String, Map, OgemaHttpRequest)
	 * @see #getCssMap(OgemaHttpRequest)
	 * @param req
	 * @return
	 */
    Map<String, Map<String, String>> getCssMap(OgemaHttpRequest req);

    /**
     * @see #addCssItem(String, Map, OgemaHttpRequest)
     * Here, outer keys of the nested map are selectors (e.g. CSS classnames), inner map keys
     * are CSS-properties
     * @param css
     * @param req
     */
    void setCssMap(Map<String, Map<String, String>> css, OgemaHttpRequest req);

    
	/**
	 * This is a utility method used mainly by widget classes implementing some
	 * functionality, but not normally within an application. It is recommended to 
	 * use e.g. {@link WidgetStyle}s instead. That said, it is possible to make use
	 * of the method in an app, as well. <br>
	 *
     * Parameter: a map of the form <code>{ selector : { key1: prop1, key2: prop2}}</code>, which
     * will be transformed to a CSS-String of the form <br>
     * <code>"#widgetID selector {key1: prop1; key2: prop2;}"</code> <br>
     * (i.e. selector is the classname to which the CSS properties apply, map keys
     * are CSS-properties)<br>
     * 
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
     * Selectors can be concatenated just like in ordinary HTML, e.g. ".myClass#myId" or
     * ".myClass #myId" are valid as well.
     * 
     *  @param selector
     *  @param value
     *  @param req
     */
    void addCssItem(String selector, Map<String, String> value, OgemaHttpRequest req);
    
    /**
     * @see #addCssItem(String, Map, OgemaHttpRequest)
     * @param selector
     * @param req
     */
    void removeCSSItems(String selector, OgemaHttpRequest req);

    /**
     * @see #addCssItem(String, Map, OgemaHttpRequest)
     * @param selector
     * @param property
     * @param req
     */
    void removeCSSItem(String selector, String property, OgemaHttpRequest req);
    
    /**
	 * Add a css property to the widget's main div, such as "font-weight: bold;"
	 * @param property
	 * 		a css style property, such as "font-weight"
	 * @param value
	 * 		a css style value, such as "bold"
	 */
	void addDefaultCssStyle(String property, String value);
	
	/**
	 * Add a css property to the widget's main div, such as "font-weight: bold;"
	 * @param property
	 * 		a css style property, such as "font-weight"
	 * @param value
	 * 		a css style value, such as "bold"
	 */
	void addCssStyle(String property, String value, OgemaHttpRequest req);
	
	/**
	 * Set the margin of the widget for all four sides
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 */
	void setDefaultMargin(String margin);
	
	/**
	 * Set the margin of the widget for all four sides
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 * @param req
	 */
	void setMargin(String margin, OgemaHttpRequest req);
	
	
	/**
	 * Set the margin of the widget
	 * @param margin
	 * 		e.g. "1em" or "3px"
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	void setDefaultMargin(String margin, boolean top, boolean left, boolean bottom, boolean right);
	
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
	void setMargin(String margin, boolean top, boolean left, boolean bottom, boolean right, OgemaHttpRequest req);
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 */
	void setDefaultPadding(String padding);
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 * @param req
	 */
	void setPadding(String padding, OgemaHttpRequest req);
	
	/**
	 * Set the padding of the widget
	 * @param padding
	 * 		e.g. "1em" or "3px"
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	void setDefaultPadding(String padding, boolean top, boolean left, boolean bottom, boolean right);
	
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
	void setPadding(String padding, boolean top, boolean left, boolean bottom, boolean right, OgemaHttpRequest req);

    /**
     * Trigger an action of another widget. Typically, both the triggering and triggered actions are Http requests, either GET or POST.
     * Other types are possible, though, for instance {@link TriggeredAction#HIDE_WIDGET}, which hides the widget in the client browser.<br>
     * Note that the target widget must exist globally, not just in one session. For the latter case, use 
     * {@link #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction, OgemaHttpRequest)}.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     */
    void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction);
    
    /**
     * Like {@link #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)}, except that a level can be specified, in order
     * to specify an ordering of the dependencies. The client-side actions triggered by this widget and the specified trigger
     * will be executed in ascending order according to the level.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param level
     */
    void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level);
    
    /**
     * @see #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)
     * This version of the method must be used to trigger an action of a widget that only exist within one session.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param req
     */
    void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req);
    
    /**
     * @see #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)
     * This version of the method must be used to trigger an action of a widget that only exist within one session.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param level
     * @param req
     */
    void triggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level, OgemaHttpRequest req);

    
    /**
     * remove an action trigger
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     */
    void removeTriggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction);
    
    /**
     * remove an action trigger
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param req
     */
    void removeTriggerAction(OgemaWidget target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req);
    
    /**
     * @see #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)
     * Here the action of a widget group is triggered.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     */
    void triggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction);
    
    /**
     * @see #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)
     * Here the action of a widget group is triggered.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param level
     */
    void triggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level);
    
    /**
     * @see #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction, OgemaHttpRequest)
     * Here the action of a widget group is triggered.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param req
     */
    void triggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req);
    
    /**
     * @see #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction, OgemaHttpRequest)
     * Here the action of a widget group is triggered.
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param level
     * @param req
     */
    void triggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, int level, OgemaHttpRequest req);

    
    /**
     * remove an action trigger
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     */
    void removeTriggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction);
    
    /**
     * remove an action trigger
     * @param target
     * @param triggeringAction
     * @param triggeredAction
     * @param req
     */
    void removeTriggerAction(WidgetGroup target, TriggeringAction triggeringAction, TriggeredAction triggeredAction, OgemaHttpRequest req);
    
    /**
     * @see #disable(OgemaHttpRequest)
     * @see #enable(OgemaHttpRequest)
     * @param req
     * @return
     */
    boolean isDisabled(OgemaHttpRequest req);

    /**
     * Disable any dynamic functionality of the widget. The effect of this method differs between widgets.
     * @see #enable(OgemaHttpRequest)
     * @param req
     */
    void disable(OgemaHttpRequest req);

    /**
     * Enable any dynamic functionality of the widget. The effect of this method differs between widgets.
     * @see #disable(OgemaHttpRequest)
     * @param req
     */
    void enable(OgemaHttpRequest req);

    /**
     * A utility method that is mostly used by implementing widget classes, 
     * rather than in applications.<br>
     * 
     * Add an HTML attribute and a corresponding Javascript action.
     * 
     * @param attr: HTML attribute, e.g. "onclick"
     * @param action: Javascript function to execute, e.g.
     * "window.open(otherPage.html,_blank);"
     */
    void addAttribute(String attr, String action, OgemaHttpRequest req);

    /**
     * Remove HTML attribute, like "onclick"
     * @see #addAttribute(String, String, OgemaHttpRequest)
     * @param attr
     * @param req
     * @return
     */
    boolean removeAttribute(String attr, OgemaHttpRequest req);

    /**
     * Get Javascript function associated to attribute attr
     * @see #addAttribute(String, String, OgemaHttpRequest)
     * @param attr
     * @param req
     * @return
     */
    String getAttribute(String attr, OgemaHttpRequest req);

    /**
     * A dependent widget will updated itself on the client side when a POST
     * request on the governing widget is finished. In this way the server can
     * update the dependent widgets when a new value for the governing widget is
     * received. When the page is initialized or when a POST is received on the
     * server side the generation of the new JSON data of the dependent widget
     * is triggered by the governing widget so that the page contains consistent
     * data
     * <br>
     * Note that this method of updating the dependent widget is not compatible with
     * the client-side updates triggered by {@link #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)},
     * and related methods. Using this method on a widget will deactivate all client-side
     * triggers!
     *
     * @param other
     * 		the dependent widget
     * @deprecated use {@link #triggerOnPOST(OgemaWidget)} instead
     */
    @Deprecated
    void registerDependentWidget(OgemaWidget other);
    
    /**
     * Trigger a GET request of the passed widget whenever a POST request is submitted
     * by this widget. Equivalent to {@link #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)}
     * with arguments {@link TriggeringAction#POST_REQUEST} and {@link TriggeredAction#GET_REQUEST}.
     *
     * @param other
     * 		the dependent widget
     */
    void triggerOnPOST(OgemaWidget other);
    
    /**
     * @see #registerDependentWidget(OgemaWidget)
     * session-specific version; use in particular when other only exists in one session
     * <br>
     * Note that this method of updating the dependent widget is not compatible with
     * the client-side updates triggered by {@link #triggerAction(OgemaWidget, TriggeringAction, TriggeredAction)},
     * and related methods. Using this method on a widget will deactivate all client-side
     * triggers!
     * 
     * @param other
     * @param req
     * @deprecated use {@link #triggerOnPOST(OgemaWidget, OgemaHttpRequest)} instead
     */
    @Deprecated
    void registerDependentWidget(OgemaWidget other, OgemaHttpRequest req);

    /**
     * @see #registerDependentWidget(OgemaWidget)
     * session-specific version; use in particular when other widget only exists in one session
      * 
     * @param other
     * @param req
     */
    void triggerOnPOST(OgemaWidget other, OgemaHttpRequest req);

    /**
     * @see #registerDependentWidget(OgemaWidget)
     * @param widget
     */
    @Deprecated
    void unregisterDependentWidget(OgemaWidget widget);
    
    /**
     * Call this on widgets that have an expensive {@link #onGET(OgemaHttpRequest)} method, to avoid
     * them blokcing the loading of the page.
     */
    void postponeLoading();
    
    /**
     * Call this on widgets that have an excessive number of (session-specific) subwidgets, causing 
     * a lot of HTTP requests for initial data loading. It will cause a common preloading request for 
     * those widgets that gathers all the init data for them in a single call.
     * The method must be called before the subwidgets have been created.
     */
    void preloadSubwidgets();
    
	static enum SendValue {
		TRUE,
		FALSE
	}
	
}
