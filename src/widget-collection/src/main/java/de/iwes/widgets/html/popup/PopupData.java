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
package de.iwes.widgets.html.popup;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class PopupData extends WidgetData {

	// use TriggeredAction.SHOW_WIDGET and TriggeredAction.HIDE_WIDGET instead
//    private static final Object[] argsTrue = {true};
//    private static final Object[] argsFalse = {false};
//    public static final TriggeredAction SET_VISIBILITY_TRUE = new TriggeredAction("setVisibility", argsTrue);
//    public static final TriggeredAction SET_VISIBILITY_FALSE = new TriggeredAction("setVisibility", argsFalse);  

	/*
	 * We only escape the title, since normally header, body, and footer contain other widgets, which will take 
	 * care of escaping. Escaping the headerHtml if it contains another widget would break the functionality.
	 */
    private String title, titleEscaped, headerHTML, bodyHTML, footerHTML; // bodyHTML inherited from PageSnippet
    private OgemaWidgetBase<?> headerWidget, bodyWidget, footerWidget; // either HTML or widget can be set
    
    /************* constructor **********************/
    
    public PopupData(Popup popup) {
        this(popup, "", null, null, null);
    }

    public PopupData(Popup popup, String title, String headerHTML, String bodyHTML, String footerHTML) {
        super(popup);
        this.title = title;
        this.titleEscaped = StringEscapeUtils.escapeHtml4(title);
        this.headerHTML = headerHTML;
        this.bodyHTML = bodyHTML;  
        this.footerHTML = footerHTML;
        this.visible = false; // visibility can be changed using triggerAction 
    }

    /******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        result.put("title", titleEscaped);
        result.put("headerHTML", getHeaderHTML());
        result.put("bodyHTML", getBodyHTML());
        result.put("footerHTML", getFooterHTML());
        return result;
    }
    
    @Override
    protected Collection<OgemaWidget> getSubWidgets() {
    	Set<OgemaWidget> widgets = new LinkedHashSet<OgemaWidget>();
    	if (headerWidget != null) widgets.add(headerWidget);
    	if (bodyWidget != null) widgets.add(bodyWidget);
    	if (footerWidget != null) widgets.add(footerWidget);
    	return widgets;
    }
    
    @Override
    protected void removeSubWidgets() {
    	removeWidget("header");
    	removeWidget("body");
    	removeWidget("footer");
    }
    
    @Override
    protected boolean removeSubWidget(OgemaWidgetBase<?> subwidget) {
    	throw new UnsupportedOperationException("Method not supported: PopupData.removeSubWidget(OgemaWidget<?>)");
    }
    
    @Override
    public void setWidgetVisibility(boolean visible) {
    	// not supported, but cannot throw exception here, because this would crash the setting of default values;
    	// an exception is thrown by the corresponding Popup-method though
    }
    
    @Override
    protected String getWidthSelector() {
    	return ">.modal>.modal-dialog";
    }
    
    /******** public methods ***********/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleEscaped = StringEscapeUtils.escapeHtml4(title);
    }

    public String getHeaderHTML() {
        if (headerHTML != null) return headerHTML;
        else if (headerWidget != null) return headerWidget.getTag();
        else return "";
    } 

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param headerHTML
     */
    public void setHeader(String headerHTML) {
    	removeWidget("header");
        this.headerHTML = headerHTML;
    }
    
    public void setHeader(OgemaWidgetBase<?> headerWidget) {
    	this.headerHTML = null;
    	removeWidget("header");
    	this.headerWidget = headerWidget;    	
    }

    public String getBodyHTML() {		
    	 if (bodyHTML != null) return bodyHTML;
         else if (bodyWidget != null) return bodyWidget.getTag();
         else return "";
    }

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param bodyHTML
     */
    public void setBody(String bodyHTML) {
    	removeWidget("body");
    	this.bodyHTML = bodyHTML;
    } 
    
    public void setBody(OgemaWidgetBase<?> bodyWidget) {
    	removeWidget("body");
    	this.bodyWidget = bodyWidget;
    	this.bodyHTML = null;
    }

    public String getFooterHTML() {
    	if (footerHTML != null) return footerHTML;
        else if (footerWidget != null) return footerWidget.getTag();
        else return "";
    }

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param footerHTML
     */
    public void setFooter(String footerHTML) {
    	removeWidget("footer");
        this.footerHTML = footerHTML;
    }
    
    public void setFooter(OgemaWidgetBase<?> footerWidget) {
    	removeWidget("footer");
        this.footerWidget = footerWidget;
        this.footerHTML = null;
    }

    private void removeWidget(String section) {
    	switch(section) {
    	case "header":
    		if (headerWidget != null) {
        		headerWidget.destroyWidget();
        		headerWidget = null;
        	}
    		break;
    	case "body":
    		if (bodyWidget != null) {
        		bodyWidget.destroyWidget();
        		bodyWidget = null;
        	}
    		break;
    	case "footer":
    		if (footerWidget != null) {
    			footerWidget.destroyWidget();
    			footerWidget = null;
        	}
    		break;
    	}
    	
    }
    
}
