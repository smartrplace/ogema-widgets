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
