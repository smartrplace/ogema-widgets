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

package de.iwes.widgets.html.emptywidget;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Only required for some derived widgets
 */
public abstract class EmptyWidget extends OgemaWidgetBase<EmptyData> {

	private static final long serialVersionUID = 1L;

	public EmptyWidget(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public EmptyWidget(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public EmptyWidget(WidgetPage<?> page, String id, OgemaHttpRequest req) {
		super(page, id, req);
	}
	
	public EmptyWidget(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return EmptyWidget.class;
	}

	@Override
	public EmptyData createNewSession() {
		return new EmptyData(this);
	}

}
