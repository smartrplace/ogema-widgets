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