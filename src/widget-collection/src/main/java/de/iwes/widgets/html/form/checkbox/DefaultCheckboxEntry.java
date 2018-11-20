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

import java.util.Objects;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class DefaultCheckboxEntry extends CheckboxEntry {

	private final String label;
	
	public DefaultCheckboxEntry(String id, String label, boolean checked) {
		super(id);
		this.label = Objects.requireNonNull(label);
		setState(checked);
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}
	
}
