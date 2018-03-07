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

import org.ogema.core.model.simple.BooleanResource;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.checkbox.CheckboxData;

/** 
 * Checkbox that represents the value of a BooleanResource and allows to edit it
 */
public class BooleanResourceCheckbox extends Checkbox implements ResourceSelector<BooleanResource> {

	private static final long serialVersionUID = 1L;
	private BooleanResource defaultSelected = null;
	private String defaultText;

	public BooleanResourceCheckbox(WidgetPage<?> page, String id, String defaultText) {
		super(page, id);
		setDefaultText(defaultText);
	}
	
	public BooleanResourceCheckbox(WidgetPage<?> page, String id, String defaultText, BooleanResource defaultResource) {
		super(page, id);
		setDefaultText(defaultText);
		selectDefaultItem(defaultResource);
	}
	
	public BooleanResourceCheckbox(OgemaWidget parent, String id, String defaultText, OgemaHttpRequest req) {
		super(parent, id, req);
		setDefaultText(defaultText);
	}

	/*
	 ********** Internal methods ***********
	 */
	
	@Override
	public BoolenResourceCheckboxData createNewSession() {
		return new BoolenResourceCheckboxData(this);
	}
	
	@Override
	public BoolenResourceCheckboxData getData(OgemaHttpRequest req) {
		return (BoolenResourceCheckboxData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(CheckboxData opt) {
		super.setDefaultValues(opt);
		BoolenResourceCheckboxData opt2 = (BoolenResourceCheckboxData) opt;
		opt2.setSelectedResource(defaultSelected);
		opt2.setText(defaultText);
	}
	
	
	/*
	 ********** Public methods ***********
	 */
	
	@Override
	public BooleanResource getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedResource();
	}

	@Override
	public void selectItem(BooleanResource resource, OgemaHttpRequest req) throws UnsupportedOperationException {
		getData(req).setSelectedResource(resource);
	}

	@Override
	public void selectDefaultItem(BooleanResource resource) throws UnsupportedOperationException {
		this.defaultSelected = resource;
	}
	
	public void setDefaultText(String text) {
		this.defaultText = text;
	}
	
	public String getText(OgemaHttpRequest req) {
		return getData(req).getText();
	}

	public void setText(String text, OgemaHttpRequest req) {
		getData(req).setText(text);
	}

	/**
	 * Not supported by BooleanResourceCheckbox
	 */
	@Override
	public void setCheckboxList(Map<String, Boolean> newList, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by BooleanResourceCheckbox");
	}
	

}
