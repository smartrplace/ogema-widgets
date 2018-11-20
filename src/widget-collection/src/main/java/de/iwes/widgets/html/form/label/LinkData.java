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
package de.iwes.widgets.html.form.label;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class LinkData extends WidgetData {

	private String text;
	private String url;
	private boolean newTab = true;
	
	protected LinkData(Link widget) {
		super(widget);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final JSONObject json = new JSONObject();
		final String text;
		final String url;
		final boolean newTab;
		readLock();
		try {
			text = this.text;
			url = this.url;
			newTab = this.newTab;
		} finally {
			readUnlock();
		}
		if (text != null && url != null) {
			json.put("text", text);
			json.put("url", url);
			json.put("newTab", newTab);
		}
		return json;
	}

	protected void setText(final String text) {
		writeLock();
		try {
			this.text = text == null ? null : StringEscapeUtils.escapeHtml4(text);
		} finally {
			writeUnlock();
		}
	}
	
	protected void setUrl(String url) {
		writeLock();
		try {
			this.url = url;
		} finally {
			writeUnlock();
		}
	}
	
	protected void setNewTab(boolean newTab) {
		writeLock();
		try {
			this.newTab = newTab;
		} finally {
			writeUnlock();
		}
	}
	
	protected String getText() {
		readLock();
		try {
			return text;
		} finally {
			readUnlock();
		}
	}
	
	protected String getUrl() {
		readLock();
		try {
			return url;
		} finally {
			readUnlock();
		}
	}
	
	protected boolean isNewTab() {
		readLock();
		try {
			return newTab;
		} finally {
			readUnlock();
		}
	}
	
}
