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
