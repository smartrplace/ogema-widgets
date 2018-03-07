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
