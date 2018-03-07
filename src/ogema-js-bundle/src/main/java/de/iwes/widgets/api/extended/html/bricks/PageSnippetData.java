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
		writeLock(); 
		try {
			str.put(counter,html);
			counter++;
		} finally {
			writeUnlock();
		}
	}
	
	public void append (HtmlItem item) {
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
