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
