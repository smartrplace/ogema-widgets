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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.template.DisplayTemplate;
import de.iwes.widgets.template.LabelledItem;

public class TemplateMultiselectData<T> extends MultiselectData {

	protected final Map<T,Boolean> listOptions = new LinkedHashMap<>();
	
	public TemplateMultiselectData(TemplateMultiselect<T> multiselect) {
		super(multiselect);
	}
	
	public JSONObject onPOST(String json, OgemaHttpRequest req) {
		JSONObject result;
		writeLock(); 
		try {
			result =  super.onPOST(json, req);
			Collection<String> selected = getSelectedValues();
			for (Map.Entry<T, Boolean> entry: listOptions.entrySet()) {
				String val = getValueAndLabel(entry.getKey())[0];
				boolean sel = selected.contains(val);
				entry.setValue(sel);
			}
		} finally {
			writeUnlock();
		}
		return result;
	}

	public boolean addItem(T item) {
		String[] valLab = getValueAndLabel(item);
		writeLock();
		try {
			if (listOptions.containsKey(item))
				return false;
			addOption(valLab[1], valLab[0], false);
			listOptions.put(item,false); 
		} finally {
			writeUnlock();
		}
		return true;
	}
	
	public boolean removeItem(T item) {
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
		String[] valLab = getValueAndLabel(item);
		writeLock();
		try {
			selectSingleOption(valLab[0]); // locks
			listOptions.put(item, true);
		} finally {
			writeUnlock();
		}
	}
	
	public void selectMultipleItems(Collection<T> items) {
		List<String> selected = new ArrayList<>();
		writeLock();
		try {
		for (T it: items) {
			String[] valLab = getValueAndLabel(it);
			selected.add(valLab[0]);
			if (listOptions.containsKey(valLab)) {
				listOptions.put(it, true);
			}
		}
		selectMultipleOptions(selected);
		} finally {
			writeUnlock();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(Collection<? extends T> items) {
		final Collection<T> list;
		final Comparator<? super T> comparator = ((TemplateMultiselect<T>) widget).comparator;
		if (comparator == null)
			list = (Collection) items;
		else {
			list = new ArrayList<T>(items);
			Collections.sort((List) list, comparator);
		}
		
		Map<String,String> map = new LinkedHashMap<>();
		for (T item: list) {
			String[] valLab = getValueAndLabel(item);
			map.put(valLab[0], valLab[1]);
		}
		writeLock();
		try {
			update(map);
			Collection<String> selected = getSelectedValues();
			listOptions.clear();
			for (T item: items) {
				listOptions.put(item, selected.contains(getValueAndLabel(item)[0]));
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
	
	@Override
	public void clear() {
		writeLock();
		try {
			super.clear();
			listOptions.clear();
		} finally {
			writeUnlock();
		}
	}
	
	@Override
	public void selectMultipleOptions(Collection<String> selectedOptions) {
		writeLock();
		try {
			super.selectMultipleOptions(selectedOptions);
			Collection<String> selected = getSelectedValues();
			for (Map.Entry<T, Boolean> entry: listOptions.entrySet()) {
				boolean sel = selected.contains(getValueAndLabel(entry.getKey())[0]);
				entry.setValue(sel);
			}
		} finally {
			writeUnlock();
		}
	}
	
	@Override
	public void selectSingleOption(String value) {
		selectMultipleOptions(Arrays.asList(new String[]{value}));
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
	
	// FIXME need locale here
	protected String[] getValueAndLabel(T item) {
		@SuppressWarnings("unchecked")
		DisplayTemplate<T> template = ((TemplateMultiselect<T>) widget).template;
		String label = template.getLabel(item, (getInitialRequest() != null ? getInitialRequest().getLocale() : OgemaLocale.ENGLISH));
		String value = template.getId(item);
		Objects.requireNonNull(label);
		Objects.requireNonNull(value);
		return new String[]{value, label};
	}
	
	private static class TemplateBasedLabelledItem<T> implements LabelledItem {
		
		private final T object;
		private final TemplateMultiselect<T> dropdown;
		
		public TemplateBasedLabelledItem(T object, TemplateMultiselect<T> dropdown) {
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
		
	}
	
	
}
