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
package org.ogema.tools.widgets.test.base.widgets;

import org.ogema.tools.widgets.test.base.GenericWidget;
import org.ogema.tools.widgets.test.base.WidgetLoader;

public class TestTextField extends GenericWidget {

	public TestTextField(WidgetLoader client, String id, String servletPath) {
		super(client, id, servletPath);
	}

	public String getValue() {
		return getWidgetData().getString("value");
	}
	
	public synchronized void setValue(String value) {
		widgetData.put("value",value);
	}
	
	@Override
	protected String getSubmitData() {
		return getValue();
	}
	
}
