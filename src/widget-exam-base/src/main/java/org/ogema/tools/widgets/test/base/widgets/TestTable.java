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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.ogema.tools.widgets.test.base.GenericWidget;
import org.ogema.tools.widgets.test.base.WidgetLoader;

public class TestTable extends GenericWidget {

	public TestTable(WidgetLoader client, String id, String servletPath) {
		super(client, id, servletPath);
	}

	@Override
	protected boolean checkForReload(JSONObject newData) {
		return !getRows().equals(getRows(newData));
	}
	
	
	public synchronized Set<String> getRows() {
		return getRows(widgetData);
	}
	
	private static Set<String> getRows(JSONObject data) {
		if (data == null || !data.has("html"))
			return Collections.emptySet();
		JSONObject html = data.getJSONObject("html");
		return new HashSet<>(html.keySet());
	}
	
}
