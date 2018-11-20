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
package de.iwes.widgets.pattern.page.impl;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownData;

public abstract class ControllableDropdown extends Dropdown {

	private static final long serialVersionUID = 1L;
	private boolean initialActiveStatus = true;
	
	public ControllableDropdown(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	/**** Inherited methods ***/
	
	@Override
	public ControllableDropdownOptions createNewSession() {
		return new ControllableDropdownOptions(this);
	}
	
	@Override
	public ControllableDropdownOptions getData(OgemaHttpRequest req) {
		return (ControllableDropdownOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		ControllableDropdownOptions opt2 = (ControllableDropdownOptions) opt;
		opt2.setActive(initialActiveStatus);
	}
	
	
	/*** Options ***/
	
	public class ControllableDropdownOptions extends DropdownData {
		
		private boolean isActive = true;

		public ControllableDropdownOptions(ControllableDropdown cd) {
			super(cd);
		}
		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			JSONObject result = super.retrieveGETData(req);
			if (!isActive) {
		        JSONArray array = new JSONArray();
				result.put("options", array);
				return result;
			}
			return result;
		}
		
		public void setActive(boolean status) {
			isActive = status;
		}

	}
	
	
	/******** Public mehtods ********/
	
	public void setInitialActiveStatus(boolean status) {
		this.initialActiveStatus = status;
	}
	
	public void setActive(boolean status, OgemaHttpRequest req) {
		getData(req).setActive(status);
	}
	
}
