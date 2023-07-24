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
package de.iwes.widgets.api.extended.html.bricks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.html.Linebreak;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class PageSnippetData extends WidgetData {
	
	private int counter = 0;
	protected final Map<Integer,String> str;
	protected final Map<Integer,OgemaWidgetBase<?>> subwidgets;
	protected final Map<Integer,HtmlItem> htmlItems;
	private String backgroundImg = null;
	/**
	 * 0: legacy default mode
	 * 1: sub-content preserving mode; try not to destroy subwidgets and other content on update
	 */
	private int updateMode = 0;

	/*********** Constructor **********/

	public PageSnippetData(PageSnippet snippet) {
		super(snippet);
		this.subwidgets = new LinkedHashMap<Integer,OgemaWidgetBase<?>>();
		this.str = new LinkedHashMap<Integer, String>();
		this.htmlItems = new LinkedHashMap<Integer, HtmlItem>();
	}
	
	/******** Inherited methods *******/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject obj = new JSONObject();
		readLock();
		try {
			switch (updateMode) {
			case 0:
				String html = getHtml();
				obj.put("html", html);
				break;
			case 1:
				// acutal type: List<[int|string, int, string]>
				List<Object[]> items = getItems();
				obj.put("items", items);
				break;
			}
			if (backgroundImg != null) obj.put("background-img", backgroundImg);
		} finally {
			readUnlock();
		}
		return obj;
	}	
	
	@Override
	protected boolean removeSubWidget(OgemaWidgetBase<?> subwidget) {
		throw new UnsupportedOperationException("Removal of individual subwidgets not supported");  // this is ok: session management only requires removeSubWidgets() to be implemented
	}
	
	@Override
	protected void removeSubWidgets() {
		final List<OgemaWidget> w = new ArrayList<>();
		writeLock();
		try {
//			super.removeSubWidgets();
			w.addAll(subwidgets.values());
			Iterator<HtmlItem> it = htmlItems.values().iterator();
			while(it.hasNext()) {
				Collection<OgemaWidget> subs = it.next().getSubWidgets();
				for (OgemaWidget sw : subs ) {
					w.add(sw);
				}
			}
			clear();
		} finally {
			writeUnlock();
		}
		for (OgemaWidget widget: w) {
			widget.destroyWidget();
		}
	}

	@Override
	protected Collection<OgemaWidget> getSubWidgets() {
		Set<OgemaWidget> set = new LinkedHashSet<OgemaWidget>();
		readLock(); 
		try {
			set.addAll(subwidgets.values());
			Iterator<HtmlItem> it = htmlItems.values().iterator();
			while(it.hasNext()) {
				Collection<OgemaWidget> subs = it.next().getSubWidgets();
				for (OgemaWidget sw : subs ) {
					set.add(sw);
				}
	//			set.addAll(it.next().getSubWidgets());
			}
		}
		finally {
			readUnlock();
		}
		return set;
	}
	
	@Override
	protected String getWidthSelector() {
		return ">div";
	}
	
	/*********** Public methods **********/
	
	public void append (String html) {
		if(html == null)
			throw new NullPointerException("html cannot be null!");
		writeLock(); 
		try {
			str.put(counter,html);
			counter++;
		} finally {
			writeUnlock();
		}
	}
	
	public void append (HtmlItem item) {
		if(item == null)
			throw new NullPointerException("HtmlItem cannot be null!");
		writeLock(); 
		try {
			htmlItems.put(counter, item);
			counter++;
		} finally {
			writeUnlock();
		}
	}
	
	public void linebreak() {
		append(Linebreak.getInstance());
	}
	
	protected void append (OgemaWidget widget) {
		if(widget == null)
			throw new NullPointerException("HtmlItem cannot be null!");
		writeLock(); 
		try {
			subwidgets.put(counter, (OgemaWidgetBase<?>) widget);
			counter++;
		} finally {
			writeUnlock();
		}
	}
	
	private static <T> void itemsRemoved(List<Integer> positions, Map<Integer, T> map) {
		final Map<Integer, T> newItems = new HashMap<>();
		final List<Integer> forRemoval = new ArrayList<>();
		for (Map.Entry<Integer, T> entry: map.entrySet()) {
			final int key = entry.getKey();
			final int smaller = (int) positions.stream().filter(pos -> pos < key).count();
			if (smaller > 0) {
				forRemoval.add(key);
				newItems.put(key - smaller, entry.getValue());
			}
		}
		forRemoval.stream().forEach(i -> map.remove(i));
		newItems.entrySet().forEach(entry -> map.put(entry.getKey(), entry.getValue()));
	}
	
	private void itemsRemoved(List<Integer> positions) {
		PageSnippetData.itemsRemoved(positions, htmlItems);
		PageSnippetData.itemsRemoved(positions, subwidgets);
		PageSnippetData.itemsRemoved(positions, str);
		counter = counter - positions.size();
	}
	
	public void remove(HtmlItem item) {
		if(item == null)
			throw new NullPointerException("HtmlItem cannot be null!");
		writeLock(); 
		try {
			final List<Integer> forRemoval = new ArrayList<>();
			for (Map.Entry<Integer, HtmlItem> entry: htmlItems.entrySet()) {
				if (entry.getValue().equals(item)) {
					forRemoval.add(entry.getKey());
				}
			}
			if (!forRemoval.isEmpty()) {
				forRemoval.stream().map(i -> htmlItems.remove(i)).forEach(item2 -> item2.getSubWidgets().forEach(w -> w.destroyWidget()));
				this.itemsRemoved(forRemoval);
			}
		} finally {
			writeUnlock();
		}
	}
	
	public void removeItems(Collection<HtmlItem> items) {
		if(items == null)
			throw new NullPointerException("HtmlItems cannot be null!");
		writeLock(); 
		try {
			final List<Integer> forRemoval = new ArrayList<>();
			for (Map.Entry<Integer, HtmlItem> entry: htmlItems.entrySet()) {
				if (items.contains(entry.getValue())) {
					forRemoval.add(entry.getKey());
				}
			}
			if (!forRemoval.isEmpty()) {
				forRemoval.stream().map(i -> htmlItems.remove(i)).forEach(item2 -> item2.getSubWidgets().forEach(w -> w.destroyWidget()));
				this.itemsRemoved(forRemoval);
			}
		} finally {
			writeUnlock();
		}
	}
	
	public void remove(OgemaWidget widget) {
		this.removeWidgets(Collections.singletonList(widget));
	}
	
	public void removeWidgets(Collection<OgemaWidget> items) {
		if(items == null)
			throw new NullPointerException("array cannot be null");
		if (items.size() == 0)
			return;
		writeLock(); 
		try {
			final List<Integer> forRemoval = new ArrayList<>();
			for (Map.Entry<Integer, OgemaWidgetBase<?>> entry: subwidgets.entrySet()) {
				final String id = entry.getValue().getId();
				final boolean contained = items.stream().filter(item -> item.getId().equals(id)).findAny().isPresent();
				if (contained) {
					forRemoval.add(entry.getKey());
				}
			}
			if (!forRemoval.isEmpty()) {
				forRemoval.stream().forEach(i -> {
					final OgemaWidgetBase<?> widget = subwidgets.remove(i);
					widget.destroyWidget();
				});
				this.itemsRemoved(forRemoval);
			}
		} finally {
			writeUnlock();
		}
	}
	
	
	public String getBackgroundImg() {
		return backgroundImg;
	}

	public void setBackgroundImg(String backgroundImg) {
		writeLock(); 
		try {
			this.backgroundImg = backgroundImg;
		} finally {
			writeUnlock();
		}
	}
	
	public int getUpdateMode() {
		return this.updateMode;
	}
	
	public void setUpdateMode(int mode) {
		if (mode != 0 && mode != 1)
			throw new IllegalArgumentException("Invalid update mode " + mode + ", expecting 0 or 1.");
		this.updateMode = mode;
	}
	
	/********* Internal methods ********/
	
 /* protected List<String> getSubWidgetIds() {
		List<String> list = new LinkedList<String>();
		Iterator<OgemaWidget<?>> it = getSubWidgets().iterator();
		while (it.hasNext()) {
			list.add(it.next().getId());
		}
		return list;
	} */
	
	protected String getHtml() {
		StringBuilder html = new StringBuilder("");
		for (int i=0;i<counter;i++) {
			if (subwidgets.containsKey(i)) {
				html.append(subwidgets.get(i).getTag());
			} else if (htmlItems.containsKey(i)) {
				html.append(htmlItems.get(i).getHtml());
			}
			else if (str.containsKey(i)) {
				html.append(str.get(i));
			} // if entry has been deleted, simply ignore it
		}
		return html.toString();
	}
	
	// Array<[int|string, int, string>, 
	//   where [object hash code or string value, type (0=widget, 1=html, 2=string), HTML/string value/widget id] 
	protected List<Object[]> getItems() {
		final List<Object[]> items = new ArrayList<Object[]>(counter);
		for (int i=0;i<counter;i++) {
			if (subwidgets.containsKey(i)) {
				final OgemaWidgetBase<?> w = subwidgets.get(i); 
				items.add(new Object[]{System.identityHashCode(w), 0, w.getId()});
			} else if (htmlItems.containsKey(i)) {
				final HtmlItem item = htmlItems.get(i); 
				items.add(new Object[]{System.identityHashCode(i), 1, item.getHtml()});
			} else {
				if (str.containsKey(i)) {
					final String content = str.get(i);
					items.add(new Object[]{content, 2, content});	
				} // if it has been deleted, simply continue
				
			}
			 
		}
		return items;
	}

	private void clear() {
		htmlItems.clear();
		subwidgets.clear();
		str.clear();
	}

	
	
}
