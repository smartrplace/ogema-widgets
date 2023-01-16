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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.DropdownData;

// TODO replace by ResourceTypeDropdown
public class TypeSelectorDropdown<R extends Resource> extends Dropdown {

	private static final long serialVersionUID = 1L;
	
	private final Collection<Class<? extends R>> defaultAllowedTypes;
	private final NameService nameService;

	public TypeSelectorDropdown(WidgetPage<?> page, String id) {
		this(page, id, null);
	}
	
	public TypeSelectorDropdown(WidgetPage<?> page, String id,  Collection<Class<? extends R>> defaultAllowedTypes) {
		super(page, id);
		this.nameService = getNameService();
		this.defaultAllowedTypes = new LinkedHashSet<Class<? extends R>>(defaultAllowedTypes);
	}
	
	@Override
	public TypeSelectorDropdownOptions createNewSession() {
		return new TypeSelectorDropdownOptions(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TypeSelectorDropdownOptions getData(OgemaHttpRequest req) {
		return (TypeSelectorDropdown<R>.TypeSelectorDropdownOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		TypeSelectorDropdownOptions opt2 = (TypeSelectorDropdown<R>.TypeSelectorDropdownOptions) opt;
		if (defaultAllowedTypes != null)
			opt2.setAllowedTypes(defaultAllowedTypes);
	}
	
	/****** Public methods ****/

	public Collection<Class<? extends R>> getAllowedTypes(OgemaHttpRequest req) {
		return getData(req).getAllowedTypes();
	}

	public void setAllowedTypes(Collection<Class<? extends R>> allowedTypes, OgemaHttpRequest req) {
		getData(req).setAllowedTypes(allowedTypes);
	}
	
	public void addAllowedType(Class<? extends R> type, OgemaHttpRequest req) {
		getData(req).addAllowedType(type);
	}
	
	public void removeAllowedType(Class<? extends R> type, OgemaHttpRequest req) {
		getData(req).removeAllowedType(type);
	}
	
	/***** Options type ****/
	
	public class TypeSelectorDropdownOptions extends DropdownData {
		
		private final Set<Class<? extends R>> allowedTypes =new LinkedHashSet<Class<? extends R>>();

		public TypeSelectorDropdownOptions(TypeSelectorDropdown<R> tsd) {
			super(tsd);
		}
		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			OgemaLocale locale = req.getLocale();
			Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
			boolean firstOption = true;
			writeLock();
			try {
				for (Class<? extends R> type : allowedTypes) {
					DropdownOption opt = new DropdownOption(type.getName(), getTypeName(type, locale), firstOption); 
					options.add(opt);
					firstOption = false;
				}
				setOptions(options, req);
			} finally {
				writeUnlock();
			}
			return super.retrieveGETData(req);
		}
		
		private String getTypeName(Class<? extends R> type, OgemaLocale locale) {
			String name = null;
			if (nameService != null)
				name = nameService.getName(type, locale, true);
			if (name == null)
				name = type.getSimpleName();
			return name;
		}

		protected Collection<Class<? extends R>> getAllowedTypes() {
			readLock();
			try {
				return new LinkedHashSet<Class<? extends R>>(allowedTypes);	
			} finally {
				readUnlock();
			}
		}

		protected void setAllowedTypes(Collection<Class<? extends R>> allowedTypes) {
			writeLock();
			try {
				this.allowedTypes.clear();
				if (allowedTypes != null)
					this.allowedTypes.addAll(allowedTypes);
			} finally {
				writeUnlock();
			}
		}
		
		protected void addAllowedType(Class<? extends R> type) {
			writeLock();
			try {
				allowedTypes.add(type);
			} finally {
				writeUnlock();
			}
		}
		
		protected void removeAllowedType(Class<? extends R> type) {
			writeLock();
			try {
				allowedTypes.remove(type);
			} finally {
				writeUnlock();
			}
		}
		
	}
	
}
