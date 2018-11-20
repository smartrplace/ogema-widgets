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
package de.iwes.widgets.resource.widget.colorpicker;

import org.ogema.core.model.units.ColourResource;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.form.textfield.TextFieldData;
import de.iwes.widgets.html.form.textfield.TextFieldType;

public class ResourceColorpicker extends TextField implements ResourceSelector<ColourResource> {

	private static final long serialVersionUID = 1L;
	private ColourResource defaultResource = null;

	public ResourceColorpicker(WidgetPage<?> page, String id) {
		super(page, id);
		super.setDefaultType(TextFieldType.COLOR);
	}
	
	/*
	 ************* Inherited methods *****************
	 */

	@Override
	public ResourceColorpickerData createNewSession() {
		return new ResourceColorpickerData(this);
	}
	
	@Override
	public ResourceColorpickerData getData(OgemaHttpRequest req) {
		return (ResourceColorpickerData) super.getData(req);
	}

	@Override
	protected void setDefaultValues(TextFieldData opt) {
		super.setDefaultValues(opt);
		((ResourceColorpickerData) opt).setResource(defaultResource);
	}
	
	/*
	 ************* Public methods *****************
	 */
	
	@Override
	public void selectDefaultItem(ColourResource defaultResource) {
		this.defaultResource = defaultResource;
	}

	@Override
	public void selectItem(ColourResource res, OgemaHttpRequest req) {
		getData(req).setResource(res);
	}

	@Override
	public ColourResource getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getResource();
	}
	
	/** 
	 * Hexadecimal color representation. For other formats use 
	 * {@link #getSelectedItem(OgemaHttpRequest)}, and see methods in
	 * {@link ColourResource}. 
	 * Returns null if no resource is selected.
	 */
	public String getColorString(OgemaHttpRequest req) {
		return getData(req).getColorString();
	}
	
	/*
	 ************* Not supported *****************
	 */
	
	@Override
	public void setDefaultType(TextFieldType type) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setType(TextFieldType type, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}

}
