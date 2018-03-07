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

package de.iwes.widgets.resource.widget.label;

import org.json.JSONObject;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.LabelData;
import de.iwes.widgets.resource.widget.textfield.ResourceTextField;

public class ResourceLabelData<T extends Resource> extends LabelData {
	
	private T selectedResource = null;

	public ResourceLabelData(ResourceLabel<T> textField) {
		super(textField);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		String newValue;
		if (selectedResource == null)
			newValue = ((ResourceLabel<?>) widget).getEmptyLabel(req.getLocale().getLocale()) ;
		else
			newValue = ((ResourceLabel<T>) widget).format(selectedResource, req.getLocale().getLocale());
		super.setText(newValue);
		return super.retrieveGETData(req);
	}

	public T getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(T selectedResource) {
		this.selectedResource = selectedResource;
	}
}
