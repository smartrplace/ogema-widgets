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

import java.util.Objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.LabelledItem;

public abstract class CheckboxEntry implements Cloneable, LabelledItem {

	private final String id;
	private boolean checked;
	
	public CheckboxEntry(String id) {
		this.id = Objects.requireNonNull(id);
	}
	
	public String id() {
		return id;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public abstract String label(OgemaLocale locale);
	
	protected void setState(boolean checked) {
		this.checked = checked;
	}
	
	protected JSONObject toJson(OgemaLocale locale) {
		String label = null;
		try {
			label = label(locale); 
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		if (label == null)
			label = id; // fallback
		else
			label = StringEscapeUtils.escapeHtml4(label);
		final JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("label", label);
		obj.put("checked", checked);
		String description = description(locale);
		if (description != null)
			obj.put("tooltip", StringEscapeUtils.escapeHtml4(description));
		return obj;
	}
	
	@Override
	public CheckboxEntry clone() {
		try {
			return (CheckboxEntry) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
}
