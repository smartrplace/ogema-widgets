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

package de.iwes.widgets.api.extended;

import java.util.LinkedHashSet;
import java.util.Set;

import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

class AllWidgetsGroupImpl implements WidgetGroupDerived {
	
	private final PageRegistration page;
	
	AllWidgetsGroupImpl(PageRegistration page) {
		this.page = page;
	}

	@Override
	public String getId() {
		return "all";
	}

	@Override
	public Set<OgemaWidget> getWidgets() {
		return new LinkedHashSet<OgemaWidget>(page.getWidgetsBase(null));
	}

	@Override
	public Set<OgemaWidgetBase<?>> getWidgetsImpl() {
		return new LinkedHashSet<OgemaWidgetBase<?>>(page.getWidgetsBase(null));
	}

	@Override
	public boolean addWidget(OgemaWidget widget) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeWidget(OgemaWidget widget) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return page.getWidgetsBase(null).size();
	}


	@Override
	public void setPollingInterval(long interval) {
		throw new UnsupportedOperationException("Polling for all widgets not supported");
	}

	@Override
	public long getPollingInterval() {
		throw new UnsupportedOperationException("Polling for all widgets not supported");
	}

	@Override
	public void setDefaultSendValueOnChange(boolean sendValue) {
		throw new UnsupportedOperationException("Operation for all widgets not supported");
	}
	
	@Override
	public void setDefaultVisibility(boolean visible) {
		throw new UnsupportedOperationException("Operation for all widgets not supported");
	}

	@Override
	public void setWidgetVisibility(boolean visible, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Operation for all widgets not supported");
	}
	
	
}
