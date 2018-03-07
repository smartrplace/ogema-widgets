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

package de.iwes.widgets.object.widget.dropdown.deprecated;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdownData;

public class ObjectDropdownData<R> extends TemplateDropdownData<R> {

	protected List<? extends R> objectList;
	
	public ObjectDropdownData(ObjectDropdown<R> dropdown) {
		super(dropdown);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		return super.retrieveGETData(req);
	}
	
	protected void updateOnGet() {
		if (objectList == null)
			objectList = Collections.emptyList();
		update(objectList);
	}

	public void setList(List<? extends R> objectList) {
		writeLock();
		try {
			this.objectList = objectList;
		} finally {
			writeUnlock();
		}
	}

	public List<? extends R> getList() {
		return objectList;
	}
	
	
}
