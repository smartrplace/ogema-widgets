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
package de.iwes.widgets.html.form.dropdown;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.LabelledItem;

public class ProxyDropdownOption extends DropdownOption {

	private static final long serialVersionUID = 1L;
	private final LabelledItem item;

	public ProxyDropdownOption(LabelledItem item, boolean selected) {
		super(item.id(), selected);
		this.item = item;
	}
	
	@Override
	public JSONObject getJSON(OgemaLocale locale) {
		final JSONObject json = super.getJSON(locale);
		final String label = item.label(locale);
		final String labelEncoded = label != null ? StringEscapeUtils.escapeHtml4(label) : valueEncoded; 
		json.put("label", labelEncoded);
		final String description = item.description(locale);
		if (description != null)
			json.put("tooltip", StringEscapeUtils.escapeHtml4(description));
		return json;
	}
	
	@Override
	public String label(OgemaLocale locale) {
		return item.label(locale);
	}
	
	@Override
	public String description(OgemaLocale locale) {
		return item.description(locale);
	}
	
	@Override
	public String getLabel() {
		return label(OgemaLocale.ENGLISH);
	}
	
}