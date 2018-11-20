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
package org.ogema.tools.widget.test.tools;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetPageBase;

public class TestWidget extends OgemaWidgetBase<TestWidgetData> {

	private static final long serialVersionUID = 1L;

	public TestWidget(WidgetPageBase<?> page, String id) {
		super(page, id);
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return TestWidget.class;
	}

	@Override
	public TestWidgetData createNewSession() {
		return new TestWidgetData(this);
	}

}
