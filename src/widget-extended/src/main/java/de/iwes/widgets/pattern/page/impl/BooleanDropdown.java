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

import java.util.LinkedHashSet;
import java.util.Set;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.form.dropdown.DropdownOption;

class BooleanDropdown extends ControllableDropdown {
	
	static final long serialVersionUID = 1L;
//	static final DropdownOption FALSE_SELECTED = new DropdownOption("FALSE", "FALSE", true);
//	static final DropdownOption TRUE_DESELECTED = new DropdownOption("TRUE", "TRUE", false);
//	static final DropdownOption FALSE_DESELECTED = new DropdownOption("FALSE", "FALSE", false);
//	static final DropdownOption TRUE_SELECTED = new DropdownOption("TRUE", "TRUE", true);

	public BooleanDropdown(WidgetPage<?> page, String id, String defaultValue) {
		super(page, id);
		Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
		boolean defaultSelected = false;
		try {
			defaultSelected = Boolean.parseBoolean(defaultValue);
		} catch (Exception e) {}
		options.add(new DropdownOption("FALSE", "FALSE", !defaultSelected));
		options.add(new DropdownOption("TRUE", "TRUE", defaultSelected));
		setDefaultOptions(options);
	}

}