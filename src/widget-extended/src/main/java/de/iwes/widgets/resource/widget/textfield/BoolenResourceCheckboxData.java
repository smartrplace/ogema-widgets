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

package de.iwes.widgets.resource.widget.textfield;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.ogema.core.model.simple.BooleanResource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.checkbox.CheckboxData;

public class BoolenResourceCheckboxData extends CheckboxData {
	
	private String text = null;
	private BooleanResource selected = null;
	
	public BoolenResourceCheckboxData(BooleanResourceCheckbox checkbox) {
		super(checkbox);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		checkboxList.clear();
		if (selected != null)  {
			String text;
			if (this.text != null)
				text = this.text;
			else
				text = selected.getPath();			
			checkboxList.put(text, selected.getValue());
		}
		
		return super.retrieveGETData(req);
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject request = super.onPOST(data, req);
		//cleanUp();
		boolean bval = false;
		for(Entry<String, Boolean> e: checkboxList.entrySet()) {
			bval = e.getValue();
		}
	
		selected.setValue(bval);
		return request;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BooleanResource getSelectedResource() {
		return selected;
	}

	public void setSelectedResource(BooleanResource selected) {
		this.selected = selected;
	}

	/**
	 * Not supported by BooleanResourceCheckbox
	 */
	@Override
	public void setCheckboxList(Map<String, Boolean> newList) {
		throw new UnsupportedOperationException("Not supported by BooleanResourceCheckbox");
	}
	
}
