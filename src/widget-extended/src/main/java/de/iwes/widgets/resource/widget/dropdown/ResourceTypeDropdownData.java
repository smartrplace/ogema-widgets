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
package de.iwes.widgets.resource.widget.dropdown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.DropdownData;

public class ResourceTypeDropdownData extends DropdownData {
	
	private Map<String,Class<? extends Resource>> allowedTypes;
	private final ResourceAccess ra;
	boolean fullName = true;
	private Class<? extends Resource> selectedType = null;
	private final Map<String, DropdownOption> opts = new LinkedHashMap<String, DropdownOption>(); 
	private final boolean filterAbstractTypes;

	public ResourceTypeDropdownData(ResourceTypeDropdown dropdown, boolean filterAbstractTypes, ResourceAccess ra, List<Class<? extends Resource>> allowedTypes) {
		super(dropdown);
		this.filterAbstractTypes = filterAbstractTypes;
		if (allowedTypes != null) {
			this.allowedTypes = getTypesMap(allowedTypes);
			updateOptionsFixed();
		}
		else {
			this.allowedTypes = new HashMap<String, Class<? extends Resource>>();
			this.allowedTypes.put(Resource.class.getName(),Resource.class);
			this.allowedTypes.put(FloatResource.class.getName(),FloatResource.class);
			this.allowedTypes.put(StringResource.class.getName(),StringResource.class);
			this.allowedTypes.put(IntegerResource.class.getName(),IntegerResource.class);
			this.allowedTypes.put(BooleanResource.class.getName(),BooleanResource.class);
			this.allowedTypes.put(TimeResource.class.getName(),TimeResource.class);
			this.allowedTypes.put(AbsoluteSchedule.class.getName(),AbsoluteSchedule.class);
//			opts.put(Resource.class.getName(), getOpt(Resource.class, true));
			for (Map.Entry<String, Class<? extends Resource>> entry: this.allowedTypes.entrySet()) {
				Class<? extends Resource> cl = entry.getValue();
				opts.put(entry.getKey(), getOpt(cl,cl ==Resource.class));
			}
			
		}
		this.ra = ra;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {  
		updateOptions();
		setOptions(opts.values());
		setSelectedType();
		return super.retrieveGETData(req);
	}
	
	@Override
	public JSONObject onPOST(String json, OgemaHttpRequest req) {
		JSONObject returnVal =  super.onPOST(json, req);
		setSelectedType();
		return returnVal;
	}
	
	/*
	 ********** Public methods *********
	 */
	
	public Class<? extends Resource> getSelectedType() {
		return selectedType;
	}
	
	public List<Class<? extends Resource>> getAllowedTypes() {
		return new ArrayList<Class<? extends Resource>>(allowedTypes.values());
	}
	
	public void setAllowedTypes(List<Class<? extends Resource>> allowedTypes) {
		this.allowedTypes = getTypesMap(allowedTypes);
		updateOptionsFixed();
	}
	
	/*
	 ********** Internal methods *********
	 */
	
	private void setSelectedType() {
		String selected = getSelectedValue();
		if (selected == null) 
			selectedType = null;
		else 
			selectedType = allowedTypes.get(selected);
	}
	
	private void updateOptions() {
		if (ra == null)
			updateOptionsFixed();
		else
			checkResources();
	}
	
	/*
	 * This method is only called if ra != null
	 */
	private void checkResources() {
		String selected = getSelectedValue();
		List<String> oldKeys = new ArrayList<>(opts.keySet());
		oldKeys.remove(Resource.class.getName());
		oldKeys.remove(IntegerResource.class.getName());
		oldKeys.remove(FloatResource.class.getName());
		oldKeys.remove(BooleanResource.class.getName());
		oldKeys.remove(TimeResource.class.getName());
		oldKeys.remove(StringResource.class.getName());
		oldKeys.remove(AbsoluteSchedule.class.getName());
//		Set<String> currentTypes = opts.keySet();
		boolean selectedFound = false;
		List<String> newTypes = new ArrayList<String>();
		List<Resource> resources = ra.getResources(Resource.class);
		for (Resource resource : resources) {
			Class<? extends Resource> cl = resource.getResourceType();
			if (filterAbstractTypes && isAbstract(cl)) 
				continue;
			String type  = cl.getName();
			if (opts.containsKey(type)) {
				oldKeys.remove(type);
				continue;
			}
			newTypes.add(type);
			boolean sel = type.equals(selected);
			opts.put(type, getOpt(cl, sel));
			allowedTypes.put(type, cl);
			if (sel)
				selectedFound = true;
		}
		for (String key: oldKeys) {
			opts.remove(key);
			allowedTypes.remove(key);
		}
		if (!selectedFound)
			selectFirst();
	}
	
	/*
	 * This method is only called if ra == null
	 */
	private void updateOptionsFixed() {
		String selected = getSelectedValue();
		opts.clear();
		boolean selectedFound = false;
		for (Class<? extends Resource> type : allowedTypes.values()) {
			String name = type.getName();
			boolean sel = name.equals(selected);
			opts.put(name, getOpt(type, sel));
			if (sel)
				selectedFound = true;
		}
		if (!selectedFound)
			selectFirst();
	}
	
	private boolean isAbstract(Class<? extends Resource> type) {
		return (type == Resource.class || type == ValueResource.class || type == SingleValueResource.class);
	}
	
	private void selectFirst() {
		if (!opts.isEmpty())
			opts.values().iterator().next().select(true);
	}
	
	private DropdownOption getOpt(Class<? extends Resource> type, boolean selected) {
		DropdownOption opt = new DropdownOption(type.getName(),	fullName ? type.getName() : type.getSimpleName(), selected);
		return opt;
	}
	
	private Map<String, Class<? extends Resource>> getTypesMap(Collection<Class<? extends Resource>> in) {
		Map<String, Class<? extends Resource>> map = new HashMap<>();
		for (Class<? extends Resource> type : in) {
			map.put(type.getName(), type);
		}
		return map;
	}
}
