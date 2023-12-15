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
package de.iwes.widgets.html.form.textfield;

import java.util.Arrays;

public enum TextFieldType {
	
	TEXT("text"), PASSWORD("password"), SUBMIT("submit"), RADIO("radio"), CHECKBOX("checkbox"), BUTTON("button"),
	// the following types require HTML5
	COLOR("color"), EMAIL("email"), MONTH("month"),
	NUMBER("number"), RANGE("range"), SEARCH("search"), TIME("time"), URL("url"), WEEK("week"), DATE("date"), DATETIME_LOCAL("datetime-local");
	
	private final String type;
	
	private TextFieldType(String type) {
		this.type = type;
	}
	
	public String getTypeString() {
		return type;
	}
	
	public static TextFieldType of(String type) {
		if (type == null)
			return null;
		final String type1 = type.toLowerCase();
		return Arrays.stream(TextFieldType.values())
			.filter(t -> type1.equals(t.type))
			.findAny()
			.orElse(null);
	}
	
}
