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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.json.JSONObject;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.plus.MultiSelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;

/**
 * A special RedirectButton where the url parameters of the page to be opened are 
 * deduced from a {@link TemplateMultiselect} or some other {@link MultiSelectorTemplate} widget.
 * The values are appended in the form
 * <code>?configId=value1,value2,value3,...</code>
 * By default, the value corresponding to some selected object is obtained from the toString method.
 * This can be adapted by overwriting the {@link #getConfigId(Object)} method.
 *  
 * @see RedirectButton
 * 
 */
public class TemplateMultiRedirectButton<T> extends RedirectButton implements MultiSelectorTemplate<T> {  

	public static final String PAGE_CONFIG_PARAMETER = "configId";
    private static final long serialVersionUID = 5507136541033621L;
    private final MultiSelectorTemplate<T> selector;
    private Set<T> defaultItems = null;
   
    /************* constructor **********************/
    
    public TemplateMultiRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl) {
    	super(page, id, text, defaultUrl);
    	this.selector = null;
    	this.defaultItems = new LinkedHashSet<>();
    }
    
    public TemplateMultiRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, req);
    	this.selector = null;
    	this.defaultItems = new LinkedHashSet<>();
	}
    
    public TemplateMultiRedirectButton(WidgetPage<?> page, String id, String text, MultiSelectorTemplate<T> selector) {
    	this(page, id, "", null, selector, false);
    }

    public TemplateMultiRedirectButton(WidgetPage<?> page, String id, String text, String destinationUrl, MultiSelectorTemplate<T> selector, boolean globalWidget) {
    	super(page, id, text, destinationUrl, globalWidget);
    	Objects.requireNonNull(selector);
    	this.selector = selector;
    }

    public TemplateMultiRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, MultiSelectorTemplate<T> selector, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, req);
    	Objects.requireNonNull(selector);
    	this.selector = selector;
    }
    
    
    @Override
	public TemplateMultiRedirectData createNewSession() {
    	return new TemplateMultiRedirectData(this);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public TemplateMultiRedirectData getData(OgemaHttpRequest req) {
    	return (TemplateMultiRedirectData) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(ButtonData opt) {
    	super.setDefaultValues(opt);
    	@SuppressWarnings("unchecked")
		TemplateMultiRedirectData opt2 = (TemplateMultiRedirectData) opt;
    	opt2.items = defaultItems;
    }
    
    /********** methods to be overridden *************/
	
    /**
     * Determine the config id for a single selected object.
     * @param object
     * @return
     */
    protected String getConfigId(T object) {
    	if (object instanceof Resource)
    		return ((Resource) object).getPath();
    	else if (object instanceof ResourcePattern) 
    		return ((ResourcePattern<?>) object).model.getPath();
    	return object.toString();
    }
    
    /**
     * The ids returned by this method will be attached to the page url in the form ?configId=id1,id2,id3
     * @param objects
     * 		The selected objects.
     * @return
     */
    protected String[] getConfigIds(List<T> objects) {
    	String[] arr = new String[objects.size()];
    	for (int i=0;i<objects.size();i++) {
    		arr[i] = getConfigId(objects.get(i));
    	}
    	return arr;
    }
    
    /********** options *************/
    
    public class TemplateMultiRedirectData extends RedirectButtonData {
    	
    	private Set<T> items = null;

		public TemplateMultiRedirectData(TemplateMultiRedirectButton<T> button) {
			super(button);
		}
    	
		@Override
		public JSONObject onPOST(String data, OgemaHttpRequest req) {
			readLock();
			try {
				List<T> selected = null;
				if (items != null)
					selected = new ArrayList<>(items);
				else if (selector != null)
					selected = selector.getSelectedItems(req); // a bit dangerous to call this from within the lock
				if (selected == null || selected.isEmpty())
					removeParameter(PAGE_CONFIG_PARAMETER);
				else
					addParameter(PAGE_CONFIG_PARAMETER, getConfigIds(selected));
			} finally {
				readUnlock();
			}
			return super.onPOST(data, req);
		}
		
		public List<T> getSelectedItems(OgemaHttpRequest req) {
			readLock();
			try {
				return new ArrayList<>(items);
			} finally {
				readUnlock();
			}
		}

		// throws Nullpointer in automatic mode
		public void selectItems(Collection<T> items) {
			writeLock();
			try {
				this.items.clear();
				this.items.addAll(items);
			} finally {
				writeUnlock();
			}
		}

		public boolean addSelectedItem(T item) {
			writeLock();
			try {
				return this.items.add(item);
			} finally {
				writeUnlock();
			}
		}
		
		public boolean removeSelectedItem(T item) {
			writeLock();
			try {
				return this.items.remove(item);
			} finally {
				writeUnlock();
			}
		}
    }
    
    /********** Public methods *************/
    

	@Override
	public List<T> getSelectedItems(OgemaHttpRequest req) {
		if (selector != null)
			return selector.getSelectedItems(req);
		else
			return getData(req).getSelectedItems(req);
	}

	/**
	 * @param items
	 * @param req
	 * @throws NullPointerException
	 * 		if this widget is operated in automatic mode, i.e. the selected items are determined from 
	 * 		a MultiSelectorTemplate
	 */
	@Override
	public void selectItems(Collection<T> items, OgemaHttpRequest req) {
		getData(req).selectItems(items);
	}

	@Override
	public void selectDefaultItems(Collection<T> items) {
		this.defaultItems = new LinkedHashSet<>(items);
	}
    
}
