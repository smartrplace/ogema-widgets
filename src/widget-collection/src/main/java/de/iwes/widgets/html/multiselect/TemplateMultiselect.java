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
package de.iwes.widgets.html.multiselect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.iwes.widgets.api.extended.plus.MultiSelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.template.DefaultDisplayTemplate;
import de.iwes.widgets.template.DisplayTemplate;

/** 
 * A special {@link Multiselect}, where the selectable items correspond to 
 * objects of a class T. By means of a template, the id and label of the 
 * objects can be set.
 *  
 *  @author cnoelle
 */
public class TemplateMultiselect<T> extends Multiselect implements MultiSelectorTemplate<T> {

    private static final long serialVersionUID = 1L;
	protected DisplayTemplate<T> template = new DefaultDisplayTemplate<>();
	private Collection<T> defaultItems = null;
	private Collection<T> defaultSelectedItems = null; 
	Comparator<? super T> comparator = new Comparator<T>() {

		@Override
		public int compare(T o1, T o2) {
			if (o1 == o2)
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;
			return template.getLabel(o1, OgemaLocale.ENGLISH).compareToIgnoreCase(template.getLabel(o2, OgemaLocale.ENGLISH));
		}
	};

	/*********** Constructors **********/
	
	public TemplateMultiselect(WidgetPage<?> page, String id) {
		super(page, id);
	}

	public TemplateMultiselect(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public TemplateMultiselect(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	public void setTemplate(DisplayTemplate<T> template) {
		Objects.requireNonNull(template);
		this.template = template;
	}
	
	public DisplayTemplate<T> getTemplate() {
		return template;
	}
	
	@Override
	public TemplateMultiselectData<T> createNewSession() {
		return new TemplateMultiselectData<>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplateMultiselectData<T> getData(OgemaHttpRequest req) {
		return (TemplateMultiselectData<T>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(MultiselectData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		TemplateMultiselectData<T> opt2 = (TemplateMultiselectData<T>) opt;
		if (defaultItems != null) {
			opt2.update(defaultItems);
		}
		if (defaultSelectedItems != null) {
			opt2.selectMultipleItems(defaultSelectedItems);
			opt2.defaultSelected();
		}
	}
	
	public boolean addItem(T item, OgemaHttpRequest req) {
		return getData(req).addItem(item);
	}

	public boolean removeItem(T item, OgemaHttpRequest req) {
		return getData(req).removeItem(item);
	}
	
	public void selectItem(T item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}
	
	/**
	 * Deselects items not included
	 * @param items
	 * @param req
	 */
	public void selectMultipleItems(Collection<T> items, OgemaHttpRequest req) {
		getData(req).selectMultipleItems(items);
	}
	
	public void update(Collection<T> items, OgemaHttpRequest req) {
		getData(req).update(items);
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
	
	public List<T> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}
	
	public List<T> getSelectedItems(OgemaHttpRequest req) {
		Map<T,Boolean> map = getSelectionItems(req);
		List<T> items= new ArrayList<>();
		for (Map.Entry<T, Boolean> entry: map.entrySet()) {
			if (entry.getValue())
				items.add(entry.getKey());
		}
		return items;
	}
	
	@Override
	public void selectItems(Collection<T> items, OgemaHttpRequest req) {
		getData(req).selectMultipleItems(items);
	}
	
	/**
	 * Set a collection of default items. If the collection is ordered,
	 * the first item will be selected by default (or the empty one, if 
	 * available), otherwise a random one will be selected.
	 * Only use this in MANUAL update mode.
	 * 
	 * Note: this does not preselect the items, but only sets the selectable items
	 * @param items
	 */
	// XXX name misleading -> only sets the selectable items
	@Override
	public void selectDefaultItems(Collection<T> items) {
		this.defaultItems = items;
	}
	
	/**
	 * Preselect a set of default items. Note that these items must be 
	 * added to the multiselect via {@link #selectDefaultItems(Collection)}
	 * as well.
	 * @param selectedItems
	 */
	public void setDefaultSelectedItems(Collection<T> selectedItems) {
		this.defaultSelectedItems = selectedItems;
	}
	
	/**
	 * Define a comparator for sorting of items. Pass null to disable sorting.
	 * @param comparator
	 */
	public void setComparator(Comparator<? super T> comparator) {
		this.comparator = comparator;
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
	
	/**
	 * Not supported, use {@link #selectMultipleItems(Collection, OgemaHttpRequest)}
	 */
	@Override
	public void selectMultipleOptions(Collection<String> selectedOptions, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
    
	/**
	 * Not supported, use {@link #selectItem(Object, OgemaHttpRequest)}
	 */
    @Override
    public void selectSingleOption(String value, OgemaHttpRequest req) throws UnsupportedOperationException {
    	throw new UnsupportedOperationException();
    }
    
}
