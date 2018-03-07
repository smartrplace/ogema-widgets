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

package de.iwes.widgets.pattern.page.impl;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.multiselect.Multiselect;
import de.iwes.widgets.html.multiselect.MultiselectData;

/**
 * Use with {@see ArrayResource}s that carry a non-trivial {@see EntryType#enumType()} annotation
 */
// FIXME move to default package?
public class EnumMultiselect extends Multiselect {
	
	private static final long serialVersionUID = 1L;
	private boolean initialActiveStatus = true;
	@SuppressWarnings("rawtypes")
	private final Class<? extends Enum> enumType;
	// Map<enum,map(enum)>, or if map method does not exist: Map<enum,enum> (identity)
	private final Map<String,String> mapValues; 
	
	public EnumMultiselect(WidgetPage<?> page, String id, @SuppressWarnings("rawtypes") Class<? extends Enum> enumType) {
		super(page, id);
		this.enumType = enumType;
		this.mapValues = initMap();
		setDefaultWidth("100%");
		setDefaultOptions(getDDOptions());
	}
	
	private  Map<String,String> initMap() {
		Method map = getMap();
		Map<String,String> mp = new LinkedHashMap<String, String>();
		for (@SuppressWarnings("rawtypes") Enum en: enumType.getEnumConstants()) {
			String label = en.name();
			String value = label;
			if (map!=null) {
				try {
					value = map.invoke(en).toString();
				}catch(Exception e) {}
			}
			mp.put(label, value);
		}
		return mp;
	}
	
	private class MyOptions extends MultiselectData {

		private boolean isActive = true;
		
		public MyOptions(EnumMultiselect arm) {
			super(arm);
		}
		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			JSONObject result = super.retrieveGETData(req);
			if (!isActive) {	       
		        JSONArray array = new JSONArray();
				result.put("options", array);
				return result;
			}
			return result;
		}

		public void setActive(boolean status) {
			isActive = status;
		}

	}
	
	@Override
	public MultiselectData createNewSession() {
		return new MyOptions(this);
	}
	
	@Override
	public MyOptions getData(OgemaHttpRequest req) {
		return (EnumMultiselect.MyOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(MultiselectData opt) {
		super.setDefaultValues(opt);
		((MyOptions) opt).setActive(initialActiveStatus);
	}

	private Set<DropdownOption> getDDOptions() {
		Set<DropdownOption> opts = new LinkedHashSet<DropdownOption>();
		for (Map.Entry<String, String> entry: mapValues.entrySet()) {
			DropdownOption opt = new DropdownOption(entry.getValue(), entry.getKey(), false);
			opts.add(opt);
		}
		return opts;
	}
	
	public void setActive(boolean status, OgemaHttpRequest req) {
		getData(req).setActive(status);
	}
	
	public void setInitialActiveStatus(boolean status) {
		initialActiveStatus = status;
	}
	
	public void setSelectedValues(Collection<String> labels, OgemaHttpRequest req) {
		List<String> values = new ArrayList<String>();
		for (String lab: labels) {
			String value = getKey(lab);
			if (value == null) continue;
			values.add(value);
		}
		selectMultipleOptions(values, req);
	}

	
	private String getKey(String value) {
		for (Map.Entry<String, String> entry: mapValues.entrySet()) {
			if (entry.getValue().equals(value))
				return entry.getKey();
		}
		return null;
	}
	
	
	private final Method getMap() { 
		return AccessController.doPrivileged(new PrivilegedAction<Method>() {

			@Override
			public Method run() {
				Method map = null;
				try {
					map = enumType.getDeclaredMethod("map");
				} catch (Exception e) {}
				return map;
			}
		});
		
	}
	
}
