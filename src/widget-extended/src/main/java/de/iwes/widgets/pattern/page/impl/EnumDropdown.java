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