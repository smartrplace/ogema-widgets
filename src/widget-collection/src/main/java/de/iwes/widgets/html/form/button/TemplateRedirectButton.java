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

package de.iwes.widgets.html.form.button;

import java.util.Objects;

import org.json.JSONObject;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.plus.SelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;

/**
 * A special RedirectButton where the url parameter of the page to be opened corresponds to
 * an object of the generic type T, which can be either set explicitly in the app, or be 
 * deduced from a {@link TemplateDropdown} or some other {@link SelectorTemplate} widget.<br>
 * This widget hence supports two operating modes, and correspondingly, there are two sets of
 * constructors which can used, distinguished by whether they require a SelectorTemplate widget 
 * to be passed as parameter, or not.
 * <br>
 * The page parameter is appended in the form
 * <code>?configId=value1</code>
 * where value1 is obtained from the selected object's toString() method.
 * This can be adapted by overwriting the {@link #getConfigId(Object)} method.
 *  
 * @see RedirectButton 
 */
public class TemplateRedirectButton<T> extends RedirectButton implements SelectorTemplate<T> {  

    private static final long serialVersionUID = 5507136541033621L;
    
	public static final String PAGE_CONFIG_PARAMETER = "configId";
    private final SelectorTemplate<T> selector; 
	// note that this is only used if the selected object is set explicitly, instead of being deduced from a SelectorTemplate
	private T defaultSelected;
   
    /************* constructor **********************/
    
    /**
     * When using this constructor, the selected object must be set explicitly in the app.
     * @param page
     * @param id
     * @param text
     */
    public TemplateRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl) {
    	super(page, id, text, defaultUrl);
    	this.selector = null;
    }
    
    /**
     * When using this constructor, the selected object must be set explicitly in the app.
     * Session-widget, only exists for one user session.
     * @param parent
     * @param id
     * @param text
     * @param destinationUrl
     * @param req
     */
    public TemplateRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, req);
    	this.selector = null;
    }
    
    /**
     * When using this constructor, the selected object will be deduced automatically from the selector template.
     * @param page
     * @param id
     * @param text
     * @param selector
     */
    public TemplateRedirectButton(WidgetPage<?> page, String id, String text, SelectorTemplate<T> selector) {
    	this(page, id, "", null, selector, false);
    }

    /**
     * When using this constructor, the selected object will be deduced automatically from the selector template.
     * @param page
     * @param id
     * @param text
     * @param selector
     * @param globalWidget
     */
    public TemplateRedirectButton(WidgetPage<?> page, String id, String text, String destinationUrl, SelectorTemplate<T> selector, boolean globalWidget) {
    	super(page, id, text, destinationUrl, globalWidget);
    	Objects.requireNonNull(selector);
    	this.selector = selector;
    }

    /**
     * When using this constructor, the selected object will be deduced automatically from the selector template.
     * Session-widget, only exists for one user session.
     * @param parent
     * @param id
     * @param text
     * @param destinationUrl
     * @param selector
     * @param req
     */
    public TemplateRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl,SelectorTemplate<T> selector, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, req);
    	Objects.requireNonNull(selector);
    	this.selector = selector;
    }
    
    /********** methods to be overridden *************/
	
    /**
     * The id returned by this method will be attached to the redirected page url in the form ?configId=id
     * @param object
     * 		The selected object.
     * @return
     */
    protected String getConfigId(T object) {
    	if (object instanceof Resource)
    		return ((Resource) object).getPath();
    	else if (object instanceof ResourcePattern) 
    		return ((ResourcePattern<?>) object).model.getPath();
    	return object.toString();
    }
    
    /********** options *************/
    
    public class TemplateRedirectData extends RedirectButtonData {
    	
    	// note that this is only used if the selected object is set explicitly, instead of being deduced from a SelectorTemplate
    	private T selected;

		public TemplateRedirectData(TemplateRedirectButton<T> button) {
			super(button);
		}
    	
		@Override
		public JSONObject onPOST(String data, OgemaHttpRequest req) {
			readLock();
			try {
				T object = null;
				if (selected != null) {
					object = selected;
				}
				else if (selector != null) {
					object = selector.getSelectedItem(req);
				}
				if (object == null)
					removeParameter(PAGE_CONFIG_PARAMETER);
				else
					addParameter(PAGE_CONFIG_PARAMETER, getConfigId(object));
			} finally {
				readUnlock();
			}
			return super.onPOST(data, req);
		}
		
		public void selectItem(T selected) {
			writeLock();
			try {
				this.selected = selected;
			} finally {
				writeUnlock();
			}
		}
    	
		public T getSelectedItem() {
			readLock();
			try {
				return selected;
			} finally {
				readUnlock();
			}
		}
		
    }
    
    @Override
	public TemplateRedirectData createNewSession() {
    	return new TemplateRedirectData(this);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public TemplateRedirectData getData(OgemaHttpRequest req) {
    	return (TemplateRedirectData) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(ButtonData opt) {
    	super.setDefaultValues(opt);
    	@SuppressWarnings("unchecked")
		TemplateRedirectData data = (TemplateRedirectData) opt;
    	data.selectItem(defaultSelected);
    }
    
    /********** Public methods *************/
    
	@Override
	public void selectItem(T selected, OgemaHttpRequest req) {
		getData(req).selectItem(selected);
	}
	
	@Override
	public T getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}
	
	@Override
	public void selectDefaultItem(T item) {
		this.defaultSelected = item;
	}
   
}
