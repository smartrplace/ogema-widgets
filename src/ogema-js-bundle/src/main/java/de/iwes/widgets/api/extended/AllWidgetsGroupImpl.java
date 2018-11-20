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
package de.iwes.widgets.api.extended;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

class AllWidgetsGroupImpl implements WidgetGroupDerived {
	
	private final PageRegistrationI page;
	
	AllWidgetsGroupImpl(PageRegistrationI page) {
		this.page = page;
	}

	@Override
	public String getId() {
		return "all";
	}

	@Override
	public Set<OgemaWidget> getWidgets() {
		return new LinkedHashSet<OgemaWidget>(getWidgetsBase(null));
	}

	@Override
	public Set<OgemaWidgetBase<?>> getWidgetsImpl() {
		return new LinkedHashSet<OgemaWidgetBase<?>>(getWidgetsBase(null));
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
		return getWidgetsBase(null).size();
	}

	private List<OgemaWidgetBase<?>> getWidgetsBase(String sessionId) {
		final PageRegistration reg = page instanceof PageRegistration ? (PageRegistration) page : ((LazyPageRegistration) page).getRegistration();
		return reg.getWidgetsBase(sessionId);
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
