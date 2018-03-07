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