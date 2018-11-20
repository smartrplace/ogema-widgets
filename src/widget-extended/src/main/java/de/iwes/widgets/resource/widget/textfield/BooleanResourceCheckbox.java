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
