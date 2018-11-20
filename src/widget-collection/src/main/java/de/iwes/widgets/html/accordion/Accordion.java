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
package de.iwes.widgets.html.accordion;

import java.util.Set;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.LabelledItem;

/**
 * An Accordion, that displays multiple tabs (see {@link AccordionItem}), which can be
 * shrunk and expanded.
 */
public class Accordion extends OgemaWidgetBase<AccordionData> {

	static final long serialVersionUID = 1L;
    private boolean defaultHideInactive;


	/*********** Constructors **********/

    public Accordion(WidgetPage<?> page, String id) {
        this(page, id, false);
    }    
    
    public Accordion(WidgetPage<?> page, String id, boolean globalWidget) {
        super(page, id, globalWidget);
        super.setDynamicWidget(true);
    }
    
    public Accordion(OgemaWidget parent, String id, OgemaHttpRequest req) {
        super(parent, id,req);
        super.setDynamicWidget(true);
    }
    
    /******* Inherited methods ******/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Accordion.class;
    }
    
    @Override
	public AccordionData createNewSession() {
    	return new AccordionData(this);
    }
    
    @Override
    protected void setDefaultValues(AccordionData opt) {
    	opt.hideInactive(defaultHideInactive);
    	super.setDefaultValues(opt);
    }

    /********** Public methods **********/
    

    public boolean isDefaultHideInactive() {
		return defaultHideInactive;
	}

	public void setDefaultHideInactive(boolean defaultHideInactive) {
		this.defaultHideInactive = defaultHideInactive;
	}

    //Collapse items except for active-item
    public void hideInactive(boolean hideInactive, OgemaHttpRequest req) {
    	getData(req).hideInactive(hideInactive);;
    }

    public boolean isVisible(OgemaHttpRequest req) {
        return getData(req).isVisible();
    }

    public void setVisible(boolean visible, OgemaHttpRequest req) {
    	getData(req).setWidgetVisibility(visible);
    }


    //Items (HTML provided)
    /**
     * Do not pass unchecked user data as html to this method, as it is vulnerable to 
     * cross site scripting attacks.
     * @param title
     * @param html
     * @param req
     * @deprecated consider using {@link #addItem(LabelledItem, String, boolean, OgemaHttpRequest)} 
     * instead, which supports id and label.
     */
    @Deprecated
    public void addItem(String title, String html, OgemaHttpRequest req) {
    	getData(req).addItem(title, html);
    }

    /**
     * Do not pass unchecked user data as html to this method, as it is vulnerable to 
     * cross site scripting attacks.
     * @param title
     * @param html
     * @param expanded
     * @param req
     * @deprecated consider using {@link #addItem(LabelledItem, String, boolean, OgemaHttpRequest)} 
     * instead, which supports id and label.
     */
    @Deprecated
    public void addItem(String title, String html, boolean expanded, OgemaHttpRequest req) {
    	getData(req).addItem(title, html, expanded);
    }
    
    public void addItem(LabelledItem item, String html, boolean expanded, OgemaHttpRequest req) {
    	getData(req).addItem(item, html, expanded);
    }    
    
    /**
     * 
     * @param title
     * @param widget
     * @param req
     * @deprecated consider using {@link #addItem(LabelledItem, OgemaWidget, boolean, OgemaHttpRequest)} 
     * instead, which supports id and label.
     */
    @Deprecated
    public void addItem(String title, OgemaWidget widget, OgemaHttpRequest req) {
    	getData(req).addWidget(title, widget);
    }
    
    /**
     * @param title
     * @param widget
     * @param expanded
     * @param req
     * @deprecated consider using {@link #addItem(LabelledItem, OgemaWidget, boolean, OgemaHttpRequest)} 
     * instead, which supports id and label.
     */
    @Deprecated
    public void addItem(String title, OgemaWidget widget, boolean expanded, OgemaHttpRequest req) {
    	getData(req).addWidget(title, widget, expanded);
    }
    
    public void addItem(LabelledItem item, OgemaWidget widget, boolean expanded, OgemaHttpRequest req) {
    	getData(req).addWidget(item, widget, expanded);
    }
    
    //Pages (external HTML)
    public void addPage(String title, String path, OgemaHttpRequest req) {
    	getData(req).addPage(title, path, false);
    }

    public void addPage(String title, String path, boolean expanded, OgemaHttpRequest req) {
    	getData(req).addPage(title, path, expanded);
    }

    public boolean removeItem(AccordionItem item, OgemaHttpRequest req) {
        return getData(req).removeItem(item);
    }

    public boolean removeItem(String title, OgemaHttpRequest req) {
    	return getData(req).removeItem(title);
    }

    public void clearItems(OgemaHttpRequest req) {
        getData(req).clearItems();
    }
    
    public boolean hasItem(String title, OgemaHttpRequest req) {
    	return getData(req).hasItem(title);
    }
    
    public AccordionItem getItem(String title, OgemaHttpRequest req) {
    	return getData(req).getItem(title);
    }  
    
    public Set<String> getAllItems(OgemaHttpRequest req) {
    	return getData(req).getAllItems();
    }
}
