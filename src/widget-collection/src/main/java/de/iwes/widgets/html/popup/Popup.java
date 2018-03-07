/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.html.popup;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class Popup extends OgemaWidgetBase<PopupData> {

	private static final long serialVersionUID = 1L;
	private String defaultTitle, defaultHeaderHTML, defaultBodyHTML, defaultFooterHTML;
	
	/************* constructor **********************/
	
    public Popup(WidgetPage<?> page, String id) {
        this(page, id, "");
    }

    public Popup(WidgetPage<?> page, String id, String title) {
        this(page, id, title, "", "", "");
    }

	public Popup(WidgetPage<?> page, String id, String defaultTitle, String defaultHeaderHTML, String defaultBodyHTML, String defaultFooterHTML) {
        super(page, id);
        super.setDynamicWidget(true);
        this.defaultBodyHTML = defaultBodyHTML;
        this.defaultFooterHTML = defaultFooterHTML;
        this.defaultHeaderHTML = defaultHeaderHTML;
  		this.defaultTitle = defaultTitle;
    }
	
    public Popup(WidgetPage<?> page, String id, boolean globalWidget) {
        this(page,id,"",globalWidget);
    }

    public Popup(WidgetPage<?> page, String id, String title, boolean globalWidget) {
    	super(page, id, globalWidget);
    	super.setDynamicWidget(true);
    	defaultBodyHTML = "";
    	defaultFooterHTML = "";
    	defaultHeaderHTML = "";
    }
    
    public Popup(OgemaWidget parent, String id, String title, OgemaHttpRequest req) {
    	super(parent, id, req);
    	super.setDynamicWidget(true);
    	defaultBodyHTML = "";
    	defaultFooterHTML = "";
    	defaultHeaderHTML = "";
    }
    
    /******* Inherited methods ******/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Popup.class;
    }
    
    @Override
	public PopupData createNewSession() {
    	return new PopupData(this);
    }
    
    @Override
    protected void setDefaultValues(PopupData opt) {
    	opt.setTitle(defaultTitle);
    	opt.setBody(defaultBodyHTML);
    	opt.setHeader(defaultHeaderHTML);
    	opt.setFooter(defaultFooterHTML);
    	super.setDefaultValues(opt);
    }
    
    @Override
    public void setWidgetVisibility(boolean visible, OgemaHttpRequest req) {
    	throw new UnsupportedOperationException("Change popup visibility using the method triggerAction on some other widget, "
    			+ "with target TriggeringAction.SHOW_WIDGET");
    }
    
    @Override
    public void setDefaultVisibility(boolean defaultVisibility) {
    	throw new UnsupportedOperationException("Change popup visibility using the method triggerAction on some other widget, "
    			+ "with target TriggeringAction.SHOW_WIDGET");
    }
    
    @Override
    public boolean getDefaultVisibility() {
    	return false;
    }

    /******** public methods ***********/
    
    public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param defaultHeaderHTML
     * 
     */
	public void setDefaultHeaderHTML(String defaultHeaderHTML) {
		this.defaultHeaderHTML = defaultHeaderHTML;
	}

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param defaultBodyHTML
     */
	public void setDefaultBodyHTML(String defaultBodyHTML) {
		this.defaultBodyHTML = defaultBodyHTML;
	}

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param defaultFooterHTML
     */
	public void setDefaultFooterHTML(String defaultFooterHTML) {
		this.defaultFooterHTML = defaultFooterHTML;
	}

	public String getTitle(OgemaHttpRequest req) {
        return getData(req).getTitle();
    }

    public void setTitle(String title, OgemaHttpRequest req) {
    	getData(req).setTitle(title);
    }

    public String getHeaderHTML(OgemaHttpRequest req) {
        return getData(req).getHeaderHTML();
    }

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param headerHTML
     * @param req
     */
    public void setHeader(String headerHTML, OgemaHttpRequest req) {
    	getData(req).setHeader(headerHTML);
    }
    
    public void setHeader(OgemaWidgetBase<?> widget, OgemaHttpRequest req) {
    	getData(req).setHeader(widget);
    }

    public String getBodyHTML(OgemaHttpRequest req) { 
        return getData(req).getBodyHTML();
    }

    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param bodyHTML
     * @param req
     */
    public void setBody(String bodyHTML, OgemaHttpRequest req) {
    	getData(req).setBody(bodyHTML);
    } 
    
    public void setBody(OgemaWidgetBase<?> widget, OgemaHttpRequest req) {
    	getData(req).setBody(widget);
    } 

    public String getFooterHTML(OgemaHttpRequest req) {
        return getData(req).getFooterHTML();
    }


    /**
     * Do not pass unchecked user data, as this is vulnerable to cross site scripting attacks
     * @param footerHTML
     * @param req
     */
    public void setFooter(String footerHTML, OgemaHttpRequest req) {
    	getData(req).setFooter(footerHTML);
    }
    
    public void setFooter(OgemaWidgetBase<?> widget, OgemaHttpRequest req) {
    	getData(req).setFooter(widget);
    }

}
