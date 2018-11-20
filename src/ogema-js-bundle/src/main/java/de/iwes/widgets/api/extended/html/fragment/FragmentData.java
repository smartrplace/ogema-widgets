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
package de.iwes.widgets.api.extended.html.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

class FragmentData extends WidgetData {
	
	private final List<Object> items = new ArrayList<>();

	protected FragmentData(FragmentWidget widget) {
		super(widget);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final JSONArray array = new JSONArray();
		readLock();
		try {
			items.forEach(item -> array.put(toJson(item)));
		} finally {
			readUnlock();
		}
		final JSONObject json = new JSONObject();
		json.put("html", array);
		return json;
	}
	
	void updateItems(Collection<Object> items) {
		writeLock();
		try {
			final List<Object> forDeletion = this.items.stream()
				.filter(i -> !items.contains(i))
				.collect(Collectors.toList());
			this.items.removeAll(forDeletion);
			forDeletion.forEach(item -> {
				if (item instanceof OgemaWidgetBase<?>)
					((OgemaWidgetBase<?>) item).destroyWidget();
			});
			items.stream()
			 	.filter(item -> !this.items.contains(item))
			 	.forEach(this.items::add);
		} finally {
			writeUnlock();
		}
	}
	
	void addItem(Object item) {
		writeLock();
		try {
			items.add(item);
		} finally {
			writeUnlock();
		}
	}
	
	void removeItem(Object item) {
		writeLock();
		try {
			if (items.remove(item) && item instanceof OgemaWidgetBase<?>)
				((OgemaWidgetBase<?>) item).destroyWidget();
		} finally {
			writeUnlock();
		}
	}
	
	List<OgemaWidget> getSubwidgets() {
		readLock();
		try {
			return items.stream()
				.filter(item -> item instanceof OgemaWidget)
				.map(item -> (OgemaWidget) item)
				.collect(Collectors.toList());
		} finally {
			readUnlock();
		}
	}

	private static JSONObject toJson(final Object item) {
		final int type = item instanceof OgemaWidgetBase<?> ? 2 : 
			item instanceof HtmlItem ? 1 : 0;
		final JSONObject json = new JSONObject();
		switch (type) {
		case 2:
			json.put("tag", "div");
			json.put("content", ((OgemaWidgetBase<?>) item).getId());
			break;
		case 1:
			json.put("tag", ((HtmlItem) item).getTag());
			json.put("content", ((HtmlItem) item).getHtml(false));
			final Map<String,String> attributes = ((HtmlItem) item).getAttributes();
			if (attributes != null && !attributes.isEmpty())
				json.put("attributes", attributes);
			break;
		default:
			json.put("content", StringEscapeUtils.escapeHtml4(String.valueOf(item)));
		}
		json.put("type", type);
		return json;
	}
	
}
