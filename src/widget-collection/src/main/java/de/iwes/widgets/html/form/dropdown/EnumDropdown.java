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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.DisplayTemplate;

@SuppressWarnings("rawtypes")
public class EnumDropdown<E extends Enum> extends TemplateDropdown<E> {

	private static final long serialVersionUID = 1L;
	private final Class<E> type;

	public EnumDropdown(WidgetPage<?> page, String id, Class<E> type) {
		this(page, id, false, type);
	}
	
	public EnumDropdown(WidgetPage<?> page, String id, boolean globalWidget, Class<E> type) {
		super(page, id);
		Objects.requireNonNull(type);
		this.type = type;
		this.template = new DefaultEnumTemplate<>();
		setDefaultItems(Arrays.asList(getAllElements()));
		setComparator(new EnumComparator());
	}

	public EnumDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, Class<E> type) {
		super(parent, id, req);
		Objects.requireNonNull(type);
		this.type = type;
		this.template = new DefaultEnumTemplate<>();
		setDefaultItems(Arrays.asList(getAllElements()));
		setComparator(new EnumComparator());
	}
	
//	@Override
//	public EnumDropdownData<E> getData(OgemaHttpRequest req) {
//		return (EnumDropdownData<E>) super.getData(req);
//	}
	
	/**
	 * Override to filter out individual enum constants
	 * @return
	 */
	protected E[] getAllElements() {
		return type.getEnumConstants();
	}
	
	protected static class DefaultEnumTemplate<F extends Enum> implements DisplayTemplate<F> {

		@Override
		public String getId(F object) {
			return object.name();
		}

		@Override
		public String getLabel(F object, OgemaLocale locale) {
			return object.toString();
		}
		
	}
	
	protected class EnumComparator implements Comparator<DropdownOption> {

		@Override
		public int compare(DropdownOption o1, DropdownOption o2) {
			if (o1 == o2)
				return 0;
			String val1 = o1.id();
			String val2 = o2.id();
			if (val1.equals(val2))
				return 0;
			if (val1.equals(DropdownData.EMPTY_OPT_ID))
				return -1;
			if (val2.equals(DropdownData.EMPTY_OPT_ID))
				return 1;
			E[] el = getAllElements();
			for (E e: el) {
				String name = e.name();
				if (name.equals(val1))
					return -1;
				else if (name.equals(val2))
					return 1;
			}
			return 0;
		}

	}
	
	@Override
	public void setAddEmptyOption(boolean addEmptyOption, OgemaHttpRequest req) {
		super.setAddEmptyOption(addEmptyOption, req);
		update(Arrays.asList(getAllElements()), req);
	}
	
	@Override
	public void setAddEmptyOption(boolean addEmptyOption, String emptyOptLabel, OgemaHttpRequest req) {
		super.setAddEmptyOption(addEmptyOption, emptyOptLabel, req);
		update(Arrays.asList(getAllElements()), req);
	} 
	
	/**
	 * not supported by EnumDropdown
	 */
	@Override
	public void setDefaultOptions(Collection<DropdownOption> defaultOptions) {
		throw new UnsupportedOperationException("not supported by EnumDropdown");
	}
	
	/**
	 * not supported by EnumDropdown
	 */
	@Override
	public void setOptions(Collection<DropdownOption> options, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("not supported by EnumDropdown");
	}
	
}
