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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.plus.TemplateData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.DisplayTemplate;
import de.iwes.widgets.template.LabelledItem;

public class TemplateDropdownData<T> extends DropdownData implements TemplateData<T> {
	
	protected final Map<T,Boolean> listOptions = new LinkedHashMap<>();
	
	public TemplateDropdownData(TemplateDropdown<T> dropdown) {
		super(dropdown);
	}
	
	@Override
	public JSONObject onPOST(String json, OgemaHttpRequest req) {
		JSONObject result;
		writeLock(); 
		try {
			result =  super.onPOST(json, req);
			String selected = getSelectedValue();
			for (Map.Entry<T, Boolean> entry: listOptions.entrySet()) {
				String val = getValueAndLabel(entry.getKey())[0];
				boolean sel = (val.equals(selected));
				entry.setValue(sel);
			}
		} finally {
			writeUnlock();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean addItem(T item) {
		if (item == null)
			return false;
//		String[] valLab = getValueAndLabel(item);
		writeLock();
		try {
			if (listOptions.containsKey(item))
				return false;
			
//			addOption(valLab[1], valLab[0], false);
			addOption(new TemplateBasedLabelledItem<T>(item, (TemplateDropdown<T>) widget), false);
			
			boolean selected = getOptions().size() ==1 && this.urlParam == null;
			listOptions.put(item,selected);
			return true;
		} finally {
			writeUnlock();
		}
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 * 	true: item found and remove, false: not found
	 */
	public boolean removeItem(T item) {
		if (item == null)
			return false;
		String[] valLab = getValueAndLabel(item);
		writeLock();
		try {
			removeOption(valLab[0]); 
			return listOptions.remove(item) != null;
		} finally {
			writeUnlock();
		}
	}
	
	public void selectItem(T item) {
		if (item == null) {
			if(addEmptyOpt)
				selectSingleOption(null);
			return;
		}
		String[] valLab = getValueAndLabel(item);
		writeLock();
		try {
			if (!listOptions.containsKey(item))
				return;
			selectSingleOption(valLab[0]); // locks
		} finally {
			writeUnlock();
		}
	}
	
	@Override
	public void selectSingleOption(String value) {
		writeLock();
		try {
			super.selectSingleOption(value);
			value = getSelectedValue();
			for (Map.Entry<T, Boolean> entry: listOptions.entrySet()) {
				boolean sel = getValueAndLabel(entry.getKey())[0].equals(value);
				entry.setValue(sel);
			}
		} finally {
			writeUnlock();
		}
	}
	
	@Override
	public void changeSelection(String value, boolean newState) {
		writeLock();
		try {
			super.changeSelection(value, newState);
			for (Map.Entry<T, Boolean> entry: listOptions.entrySet()) {
				if (value.equals(getValueAndLabel(entry.getKey())[0])) {
					entry.setValue(newState);
					break;
				}
			}
		} finally {
			writeUnlock();
		}
	}
	
	@Override
	public void selectMultipleOptions(Collection<String> selectedOptions) {
		throw new UnsupportedOperationException("Selecting multiple options not possible; use Multiselect widget instead.");
	};
	
	public T getSelectedItem() {
		readLock();
		try {
			for (Map.Entry<T, Boolean> entry: listOptions.entrySet()) {
				if (entry.getValue())
					return entry.getKey();
			}
		} finally {
			readUnlock();
		}
		return null;
	}
	
	public void update(Collection<? extends T> items) {
		update(items, null);
	}
	
	/**
	 * Update the map of selected items, and in case the old selected item is no
	 * longer included, select a new one.  
	 * @param items
	 * @param select see {@link DropdownData#update(Map, String)}
	 */
	public void update(Collection<? extends T> items, T select) {
		Map<String,String> map = new LinkedHashMap<>();
		final Map<String, String> optGroups = optGroupsActive ? new HashMap<>() : null;
		final DisplayTemplate<T> template = ((TemplateDropdown<T>) widget).template;
		Map<String, String> descriptions = null; 
		String sel = null;
		for (T item: items) {
			String[] valLab = getValueAndLabel(item);
			map.put(valLab[0], valLab[1]);
			if (item.equals(select))
				sel = valLab[0];
			if (optGroupsActive) {
				final String optGroup = template.getOptGroup(item, OgemaLocale.ENGLISH); // XXX cannot set locale this way
				if (optGroup != null)
					optGroups.put(valLab[0], optGroup);
			}
			final String descr = template.getDescription(item, OgemaLocale.ENGLISH);
			if (descr != null && !descr.isEmpty()) {
				if (descriptions == null)
					descriptions = new HashMap<>();
				descriptions.put(valLab[0], descr);
			}
		}
		writeLock();
		try {
			// FIXME -> adds non-LabelledItem elements... works differently than addItem() method
			super.update(map, sel, descriptions, optGroups); 
			String selected = getSelectedValue();
			if (selected == null && this.urlParam == null && (!map.isEmpty() || addEmptyOpt)) {
				LoggerFactory.getLogger(getClass()).error("Error in dropdown widget: no item selected although items should be available");
				options.get(0).select(true);
				selected = options.get(0).id();
			}
			listOptions.clear();
			for (T item: items) {
				listOptions.put(item, getValueAndLabel(item)[0].equals(selected));
			}
		} finally {
			writeUnlock();
		}
	}
	
	public Map<T, Boolean> getSelectionItems() {
		readLock();
		try {
			return new LinkedHashMap<>(listOptions);
		} finally {
			readUnlock();
		}
	}
	
	public List<T> getItems() {
		readLock();
		try {
			return new ArrayList<>(listOptions.keySet());
		} finally {
			readUnlock();
		}
	}
	
	// FIXME need locale here!
	protected String[] getValueAndLabel(T item) {
		@SuppressWarnings("unchecked")
		DisplayTemplate<T> template = ((TemplateDropdown<T>) widget).template;
		String label = template.getLabel(item, (getInitialRequest() != null ? getInitialRequest().getLocale() : OgemaLocale.ENGLISH)); // XXX
		String value = template.getId(item);
		Objects.requireNonNull(label);
		Objects.requireNonNull(value);
		return new String[]{value, label};
	}
	
	static class TemplateBasedLabelledItem<T> implements LabelledItem {
		
		private final T object;
		private final TemplateDropdown<T> dropdown;
		
		public TemplateBasedLabelledItem(T object, TemplateDropdown<T> dropdown) {
			this.object = object;
			this.dropdown = dropdown;
		}

		@Override
		public String id() {
			return dropdown.template.getId(object);
		}

		@Override
		public String label(OgemaLocale locale) {
			return dropdown.template.getLabel(object, locale);
		}
		
		@Override
		public String description(OgemaLocale locale) {
			return dropdown.template.getDescription(object, locale);
		}
		
		public String getOptGroup(OgemaLocale locale) {
			return dropdown.template.getOptGroup(object, locale);
		}
		
	}
	
}
