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

package de.iwes.widgets.html.complextable2;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.complextable.DynamicTable;

/**
 * A version of the {@link DynamicTable} that does not depend
 * on angularJS
 */
public class DynamicTable2<T> extends DynamicTable<T> {

	private static final long serialVersionUID = 1L;

	public DynamicTable2(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public DynamicTable2(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "DynamicTable2", "/ogema/widget/complextable2/DynamicTable2.js");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return (Class) DynamicTable2.class;
	}

}
