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
package de.iwes.widgets.pattern.page.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.form.dropdown.DropdownOption;

@SuppressWarnings("rawtypes")
class EnumDropdown<E extends Enum> extends ControllableDropdown {

	private static final long serialVersionUID = 1L;
	private final Class<E> targetType;
	private final List<String> options; 
	private final Set<DropdownOption> dropdownOptions = new LinkedHashSet<DropdownOption>();
	private String defaultOption = null;

	public EnumDropdown(WidgetPage<?> page, String id, Class<E> targetType) {
		super(page, id);
		this.targetType = targetType;
		this.options = Collections.unmodifiableList(setOptions());
	}
	
	private List<String> setOptions() {
		List<String> options = new ArrayList<String>();
		E[] enums = targetType.getEnumConstants();
		String def = defaultOption;
		if (def == null && this.options != null && !this.options.isEmpty()) 
			def = this.options.get(0);
		for (E e: enums) {
			String name = e.name();
			options.add(name);
			dropdownOptions.add(new DropdownOption(name,name,name.equals(def)));
		}
		setDefaultOptions(dropdownOptions);
		return options;
	}
	
	List<String> getValidOptions() {
		return this.options;
	}
	
	Class<E> getEnumType() {
		return targetType; 
	}
	
	// should be part of Dropdown!
	void selectDefault(String val) {
		if (val ==  null || !options.contains(val)) {
			LoggerFactory.getLogger(getClass()).warn("Invalid default value " + val + " for " + targetType.getSimpleName());
			return;
		}
		E[] enums = targetType.getEnumConstants();
		dropdownOptions.clear();
		for (E e: enums) {
			String name = e.name();
			dropdownOptions.add(new DropdownOption(name,name,name.equals(val)));
		}
		setDefaultOptions(dropdownOptions);
	}
	
	
//	@Override
//	public void onGET(OgemaHttpRequest req) {
//		DropdownOption opt = getSelected(req);
//		String selected  =null;
//		if (opt != null) {
//			selected = opt.getValue();
//		}
//		Set<DropdownOption> options;
//		List<R> resources = am.getResourceAccess().getResources(targetType);
//		options = getTargetOptions((Collection<Resource>) resources,selected,req);
//		setOptions(options, req);
//	}
	
}