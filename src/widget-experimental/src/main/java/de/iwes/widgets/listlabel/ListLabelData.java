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
package de.iwes.widgets.listlabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.LabelData;

public class ListLabelData extends LabelData {
	
	private final List<String> values = new ArrayList<>();  
	private final Map<String,String> tooltips = new HashMap<>();

	public ListLabelData(ListLabel label) {
		super(label);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final StringBuilder sb = new StringBuilder("<ub>");
		readLock();
		try {
			for (String value: values) {
				final String tooltip = tooltips.get(value);
				if (tooltip != null) 
					sb.append("<li title=\"").append(tooltip).append("\">");
				else
					sb.append("<li>");
				sb.append(StringEscapeUtils.escapeHtml4(value));
			}
		} finally {
			readUnlock();
		}
 		sb.append("</ub>");
 		setHtml(sb.toString());
		return super.retrieveGETData(req);
	}
	
	protected void setValues(List<String> values) {
		writeLock();
		try {
			this.values.clear();
			this.values.addAll(values);
			tooltips.keySet().retainAll(values);
		} finally {
			writeUnlock();
		}
	}
	
	protected void setTooltips(Map<String,String> values) {
		writeLock();
		try {
			for (Map.Entry<String, String> entry: values.entrySet()) {
				final String key = entry.getKey();
				if (this.values.contains(key)) {
					tooltips.put(key, entry.getValue());
				}
			}
		} finally {
			writeUnlock();
		}
	}
	
	protected void addTooltip(String key, String tooltip) {
		writeLock();
		try {
			if (tooltip == null || tooltip.isEmpty())
				tooltips.remove(key);
			else if (this.values.contains(key)) 
				tooltips.put(key, tooltip);
		} finally {
			writeUnlock();
		}
	}
	
	protected void addValue(String value) {
		writeLock();
		try {
			this.values.add(value);
		} finally {
			writeUnlock();
		}
	}
	
	protected void removeValue(String value) {
		writeLock();
		try {
			this.values.remove(value);
			tooltips.remove(value);
		} finally {
			writeUnlock();
		}
	}
	
	protected void clear() {
		writeLock();
		try {
			this.values.clear();
			tooltips.clear();
		} finally {
			writeUnlock();
		}
	}
	
}
