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
package de.iwes.widgets.html.listgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.plus.TemplateData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.PageSnippetTemplate;
import de.iwes.widgets.template.WidgetTemplate;

public class ListGroupData<T> extends WidgetData implements TemplateData<T> {
	
	public static final WidgetStyle<ListGroup<?>> BOOTSTRAP_LIST_GROUP;
			
			
	static {
		Map<String, List<String>> css = new HashMap<String, List<String>>();
		css.put("ul", Arrays.asList("list-group"));
		css.put("ul>li", Arrays.asList("list-group-item"));
		BOOTSTRAP_LIST_GROUP = new WidgetStyle<>(css,2);
	}

	@Deprecated
	protected final PageSnippetTemplate<T> template;
	protected final WidgetTemplate<T> widgetTemplate;
	// elements and snippets must be kept parallel
	protected final List<T> elements = new ArrayList<>(); 
	protected final List<Object> snippets = new ArrayList<>();
	protected T selected;
	
	protected ListGroupData(ListGroup<T> widget, PageSnippetTemplate<T> template, WidgetTemplate<T> widgetTemplate) {
		super(widget);
		this.template = template;
		this.widgetTemplate = widgetTemplate;
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject response = new JSONObject();
		JSONArray data = new JSONArray();
		readLock();
		try {
			for (Object snippet: snippets) {
				data.put(getHtml(snippet));
			}
		} finally {
			readUnlock();
		}
		response.put("data", data);
		return response;
	}

	// executed when the user clicks on a list entry (if active)
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) throws UnsupportedOperationException {
		JSONObject obj = new JSONObject(data);
		int sel = -1;
		if (obj.has("selected")) {
			sel = obj.getInt("selected");
			if (sel >= elements.size()) 
				sel = -1;
		}
		if (sel >= 0)
			selected = elements.get(sel);
		else
			selected = null;
		return obj;
	}

	@Override
	public boolean addItem(T resource) {
		writeLock();
		try {
			if (elements.contains(resource))
				return false;
			elements.add(resource);
			final Object snippet = template != null ? template.getSnippet(resource, getInitialRequest())
					: widgetTemplate.getItem(resource, widget, getInitialRequest());
			return snippets.add(snippet);
		} finally {
			writeUnlock();
		}
	}

	@Override
	public boolean removeItem(T resource) {
		writeLock();
		try {
			int idx = elements.indexOf(resource);
			if (idx < 0) 
				return false;
			elements.remove(idx);
			final Object snippet = snippets.remove(idx);
			if (snippet instanceof OgemaWidgetBase<?>)
				((OgemaWidgetBase<?>) snippet).destroyWidget();
			return true;
		} finally {
			writeUnlock();
		}
	}

	@Override
	public List<T> getItems() {
		readLock();
		try {
			return new ArrayList<>(elements);
		} finally {
			readUnlock();
		}
	}
	
	public void update(Collection<T> items) {
		List<T> forRemoval = new ArrayList<>();
		writeLock();
		try {
			Iterator<T> oldIt = elements.iterator();
			while (oldIt.hasNext()) {
				T old = oldIt.next();
				if (!items.contains(old)) 
					forRemoval.add(old);
			}
			for (T old: forRemoval) {
				removeItem(old);
			}
			for (T item : items) {
				if (!elements.contains(item))
					addItem(item);
			}
		} finally {
			writeUnlock();
		}
	}
	
	/**
	 * Last selection by the user; may be null
	 * @return
	 */
	public T getSelected() {
		return selected;
	}

	private final JSONObject getHtml(final Object item) {
		if (widgetTemplate != null)
			return widgetTemplate.serialize(item);
		final JSONObject json = new JSONObject();
		json.put("type",2);
		json.put("content", ((OgemaWidgetBase<?>) item).getId());
		return json;
	}
	
}
