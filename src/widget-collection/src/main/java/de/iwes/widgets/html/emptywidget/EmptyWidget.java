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
