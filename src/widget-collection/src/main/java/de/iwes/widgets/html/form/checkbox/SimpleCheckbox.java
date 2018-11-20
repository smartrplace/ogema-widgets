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
package de.iwes.widgets.html.form.checkbox;

import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** 
 * Checkbox that represents a single boolean value and allows to edit it
 */
public class SimpleCheckbox extends Checkbox implements SimpleCheckboxI {

	private static final long serialVersionUID = 1L;
	private boolean defaultSelected = false; //null
	private String defaultText;

	public SimpleCheckbox(WidgetPage<?> page, String id, String defaultText) {
		super(page, id);
		setDefaultText(defaultText);
	}
	
	public SimpleCheckbox(WidgetPage<?> page, String id, String defaultText, boolean defaultValue) {
		super(page, id);
		setDefaultText(defaultText);
		setDefaultValue(defaultValue);
	}
	
	public SimpleCheckbox(OgemaWidget parent, String id, String defaultText, OgemaHttpRequest req) {
		super(parent, id, req);
		setDefaultText(defaultText);
	}

	/*
	 ********** Internal methods ***********
	 */
	
	@Override
	public SimpleCheckboxData createNewSession() {
		return new SimpleCheckboxData(this);
	}
	
	@Override
	public SimpleCheckboxData getData(OgemaHttpRequest req) {
		return (SimpleCheckboxData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(CheckboxData opt) {
		super.setDefaultValues(opt);
		SimpleCheckboxData opt2 = (SimpleCheckboxData) opt;
		opt2.setValue(defaultSelected);
		opt2.setText(defaultText);
	}
	
	
	/*
	 ********** Public methods ***********
	 */
	
	public boolean getValue(OgemaHttpRequest req) {
		return getData(req).getValue();
	}

	public void setValue(boolean value, OgemaHttpRequest req) throws UnsupportedOperationException {
		getData(req).setValue(value);
	}

	public void setDefaultValue(boolean value) throws UnsupportedOperationException {
		this.defaultSelected = value;
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
