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

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.plus.TemplateData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.PageSnippetTemplate;

public class ListGroupData<T> extends WidgetData implements TemplateData<T> {
	
	public static final WidgetStyle<ListGroup<?>> BOOTSTRAP_LIST_GROUP;
			
			
	static {
		Map<String, List<String>> css = new HashMap<String, List<String>>();
		css.put("ul", Arrays.asList("list-group"));
		css.put("ul>li", Arrays.asList("list-group-item"));
		BOOTSTRAP_LIST_GROUP = new WidgetStyle<>(css,2);
	}

	protected final PageSnippetTemplate<T> template;
	// elements and snippets must be kept parallel
	protected final List<T> elements = new ArrayList<>(); 
	protected final List<OgemaWidget> snippets = new ArrayList<>();
	protected T selected;
	
	protected ListGroupData(ListGroup<T> widget, PageSnippetTemplate<T> template) {
		super(widget);
		this.template = template;
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject response = new JSONObject();
		JSONArray data = new JSONArray();
		readLock();
		try {
			for (OgemaWidget snippet: snippets) {
				data.put(snippet.getId());
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
			OgemaWidget snippet = template.getSnippet(resource, getInitialRequest());
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
			OgemaWidget snippet = snippets.remove(idx);
			snippet.destroyWidget();
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

}
