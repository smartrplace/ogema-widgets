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
