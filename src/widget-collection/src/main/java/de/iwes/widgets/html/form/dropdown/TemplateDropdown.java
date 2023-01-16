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
package de.iwes.widgets.html.form.dropdown;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.iwes.widgets.api.extended.plus.SelectorTemplate;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.DefaultDisplayTemplate;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A special {@link Dropdown}, where the selectable items correspond to 
 * objects of a class T. By means of a template, the id and label of the 
 * objects can be set.
 *
 * @param <T>
 */
public class TemplateDropdown<T> extends Dropdown implements TemplateWidget<T>, SelectorTemplate<T> {

	private static final long serialVersionUID = 1L;
	protected DisplayTemplate<T> template = new DefaultDisplayTemplate<>();
	private Collection<T> defaultItems = null; 
	private T defaultSelected = null;
	
	public TemplateDropdown(WidgetPage<?> page, String id) {
		super(page, id);
	}

	public TemplateDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public TemplateDropdown(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	public void setTemplate(DisplayTemplate<T> template) {
		Objects.requireNonNull(template);
		this.template = template;
	}
	
	@Override
	public TemplateDropdownData<T> createNewSession() {
		return new TemplateDropdownData<>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplateDropdownData<T> getData(OgemaHttpRequest req) {
		return (TemplateDropdownData<T>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		TemplateDropdownData<T> opt2 = (TemplateDropdownData<T>) opt;
		if (defaultItems != null) {
			opt2.update(defaultItems);
		}
		if (defaultSelected != null)
			opt2.selectItem(defaultSelected);
	}

	/*
	 ********** Public methods **********
	 */
	
	public boolean addItem(T item, OgemaHttpRequest req) {
		return getData(req).addItem(item);
	}

	public boolean removeItem(T item, OgemaHttpRequest req) {
		return getData(req).removeItem(item);
	}
	
	/**
	 * This requires a set of default options to be set
	 * @see #setDefaultItems(Collection)
	 * @param item
	 */
	@Override
	public void selectDefaultItem(T item) {
		this.defaultSelected = item;
	}
	
	/**
	 * Set the selected item server-side. If the item is not in the set of
	 * options for the session the method has no effect.
	 * 
	 * @param item
	 * @param req
	 * 		identifies the user session
	 */
	public void selectItem(T item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}
	
	public T getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}
	
	public void update(Collection<? extends T> items, OgemaHttpRequest req) {
		getData(req).update(items, null, req);
	}
	
    /**
     * Set new dropdown options. Old ones not contained in items will be removed.
     * 
     * @param items
     * 		Map&lt;value, label&gt;
     * @param select 
     * 		Specify an item to be selected, in case the previously 
     * 		selected item is no longer contained in the collection.
     * @param req
     */
     public void update(Collection<? extends T> items, T select, OgemaHttpRequest req) {
     	 getData(req).update(items, select, req);
     }
	
	/**
	 * Set a collection of default items. If the collection is ordered,
	 * the first item will be selected by default (or the empty one, if 
	 * available), otherwise a random one will be selected.
	 * @param items
	 */
	public void setDefaultItems(Collection<T> items) {
		this.defaultItems = items;
	}
	
	/**
	 * Returns a map with all items as keys. The values indicate whether the respective item 
	 * is selected.
	 * @param req
	 * @return
	 */
	public Map<T, Boolean> getSelectionItems(OgemaHttpRequest req) {
		return getData(req).getSelectionItems();
	}
	
	/**
	 * Get all items that are available for selection
	 * @param req
	 * @return
	 */
	public List<T> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}
	
	/**
	 * Not supported, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addOption(String label, String value, boolean selected, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported, use {@link #removeItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void removeOption(String value, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported, use {@link #update(Collection, OgemaHttpRequest)} instead
	 */
	@Override
	public void update(Map<String, String> values, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported, use {@link #update(Collection, OgemaHttpRequest)} instead
	 */
	@Override
	public void setOptions(Collection<DropdownOption> options, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported, use {@link #setDefaultOptions(Collection)} instead
	 */
	@Override
	public void setDefaultOptions(Collection<DropdownOption> defaultOptions) {
		throw new UnsupportedOperationException();

	}
	
}
