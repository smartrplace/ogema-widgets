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
	private final Map<Integer,String> str;
	private final Map<Integer,OgemaWidgetBase<?>> subwidgets;
	private final Map<Integer,HtmlItem> htmlItems;
	private String backgroundImg = null;

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
			String html = getHtml();
			obj.put("html", html);
	//		obj.put("subWidgets", getSubWidgetIds());
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

	private void clear() {
		htmlItems.clear();
		subwidgets.clear();
		str.clear();
	}

	
	
}
