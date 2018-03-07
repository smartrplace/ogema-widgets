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

package de.iwes.widgets.html.form.textfield;

public enum TextFieldType {
	
	TEXT("text"), PASSWORD("password"), SUBMIT("submit"), RADIO("radio"), CHECKBOX("checkbox"), BUTTON("button"),
	// the following types require HTML5
	COLOR("color"), EMAIL("email"), MONTH("month"),
	NUMBER("number"), RANGE("range"), SEARCH("search"), TIME("time"), URL("url"), WEEK("week");
	
	private final String type;
	
	private TextFieldType(String type) {
		this.type = type;
	}
	
	public String getTypeString() {
		return type;
	}
	
}
