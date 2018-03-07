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
