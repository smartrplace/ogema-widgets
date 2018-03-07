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
