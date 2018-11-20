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
