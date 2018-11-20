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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.tools.widgets.test.base.GenericWidget;
import org.ogema.tools.widgets.test.base.WidgetLoader;

import de.iwes.widgets.html.form.dropdown.DropdownOption;

public class TestDropdown extends GenericWidget {

	public TestDropdown(WidgetLoader client, String id, String servletPath) {
		super(client, id, servletPath);
	}

	@Override
	protected String getSubmitData() {
		JSONArray arr = new JSONArray();
		String selected = getSelectedValue();
		if (selected != null)
			arr.put(selected);
		return arr.toString();
	}
	
	public List<DropdownOption> getOptions() {
		JSONArray options = getWidgetData().getJSONArray("options");
		Iterator<Object> it = options.iterator();
		List<DropdownOption> result = new ArrayList<>();
		while (it.hasNext()) {
			JSONObject opt = (JSONObject) it.next();
			String value = opt.getString("value");
			String label  = opt.getString("label");
			boolean selected = opt.getBoolean("selected");
			DropdownOption ddo = new DropdownOption(value, label, selected);
			result.add(ddo);
		}
		return result;
	}
	
	public String getSelectedValue() {
		JSONArray options = getWidgetData().getJSONArray("options");
		Iterator<Object> it = options.iterator();
		while (it.hasNext()) {
			JSONObject opt = (JSONObject) it.next();
			boolean selected = opt.getBoolean("selected");
			if (!selected)
				continue;
			String value = opt.getString("value");
			return value;
		}
		return null;
	}
	
	/**
	 * 
	 * @param newValue
	 * @throws IllegalArgumentException
	 * 		if the requested value is not found
	 */
	public synchronized void select(String newValue) throws IllegalArgumentException {
		JSONArray options = widgetData.getJSONArray("options");
		Iterator<Object> it = options.iterator();
		boolean found = false;
		while (it.hasNext()) {
			JSONObject opt = (JSONObject) it.next();
			String value = opt.getString("value");
			boolean selected = value.equals(newValue);
			if (selected) 
				found = true;
			opt.put("selected", selected);
		}
		if (!found)
			throw new IllegalArgumentException("Option " + newValue + " not found");
	}

}
