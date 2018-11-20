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
package de.iwes.widgets.html.form.checkbox;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class SimpleCheckboxData extends CheckboxData {
	
	private String text = null;
	private boolean selected = false;
	
	public SimpleCheckboxData(SimpleCheckbox checkbox) {
		super(checkbox);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		checkboxList.clear();
		String text;
		if (this.text != null)
			text = this.text;
		else
			text = "";			
		checkboxList.put(text, selected);
		
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
	
		selected = bval;
		return request;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getValue() {
		return selected;
	}

	public void setValue(boolean value) {
		this.selected = value;
	}

	/**
	 * Not supported by BooleanResourceCheckbox
	 */
	@Override
	public void setCheckboxList(Map<String, Boolean> newList) {
		throw new UnsupportedOperationException("Not supported by BooleanResourceCheckbox");
	}
	
}
