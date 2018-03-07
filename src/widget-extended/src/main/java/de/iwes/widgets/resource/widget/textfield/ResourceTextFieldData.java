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

import java.util.Locale;

import org.json.JSONObject;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextFieldData;

public class ResourceTextFieldData<T extends Resource> extends TextFieldData {
	
	private T selectedResource = null;

	public ResourceTextFieldData(ResourceTextField<T> textField) {
		super(textField);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		updateOnGET(req.getLocale().getLocale());
		return super.retrieveGETData(req);
	}
	
	@SuppressWarnings("unchecked")
	protected void updateOnGET(Locale locale) {
		String newValue;
		if (selectedResource == null)
			newValue = ((ResourceTextField<?>) widget).getEmptyLabel(locale) ;
		else
			newValue = ((ResourceTextField<T>) widget).format(selectedResource, locale);
		super.setValue(newValue);
	}

	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("not supported by ResourceTextField; use derived class instead");
	}

	public T getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(T selectedResource) {
		this.selectedResource = selectedResource;
	}
	
}
