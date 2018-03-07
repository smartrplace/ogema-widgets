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

package de.iwes.widgets.api.widgets.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.iwes.widgets.api.widgets.OgemaWidget;

public class HtmlItem {

	private int counter = 0;
	private String type;
	private final Map<Integer,String> innerHtml;
	private final Map<Integer,HtmlItem> innerItems;
	private final Map<Integer,OgemaWidget> innerWidgets;
	private final Map<String,String> attributes;
	
	public HtmlItem(String type) {
		this.type = type;
		this.innerItems = new LinkedHashMap<Integer,HtmlItem>();
		this.innerHtml = new LinkedHashMap<Integer, String>();
		this.innerWidgets = new LinkedHashMap<Integer, OgemaWidget>();
		this.attributes = new LinkedHashMap<String,String>();
	}
		
	public HtmlItem addSubItem(HtmlItem item) {
		innerItems.put(counter,item);
		counter++;
		return this;
	}
	
	public HtmlItem addSubItem(String html) {
		innerHtml.put(counter, html);
		counter++;
		return this;
	}
	
	public HtmlItem addSubItem(OgemaWidget widget) {
		innerWidgets.put(counter,widget);
		counter++;
		return this;
	}
	
/*	public void addAttribute(String name,String value) {
		attributes.put(name, value);		 
	} */
	
	public HtmlItem addStyle(HtmlStyle style) {
		Map<String,String> newAttr = style.getAttributes();
		Iterator<Entry<String,String>> it  = newAttr.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			String key = entry.getKey();
			if (!attributes.containsKey(key)) {
				attributes.put(key,entry.getValue());
			} else {
				String oldVal = attributes.get(key);
				if (oldVal == null) oldVal = "";
				switch (key) {
				case "style":
					attributes.put(key, oldVal + "; " + entry.getValue());
					break;
				case "class":
					attributes.put(key, oldVal + " " + entry.getValue());
					break;
				default: 
					attributes.put(key, oldVal + " " + entry.getValue());
				}	
			}
		}
		return this;
	}
	
	/**
	 * 
	 * @param widthString
	 * 		e.g. "50%" or "100px"
	 * @return
	 */
	public HtmlItem setWidth(String widthString) {
		if (widthString == null) {
			// TODO remove widht
			return this;
		}
		if (!widthString.endsWith(";")) 
			widthString += ";";
		if (!widthString.startsWith("width:"))
			widthString = "width:" + widthString;
		return addStyle(new HtmlStyle("style", widthString));
	}
	
	/**
	 * 
	 * @param widthString
	 * 		e.g. "50%" or "100px"
	 * @return
	 */
	public HtmlItem setMaxWidth(String widthString) {
		if (widthString == null) {
			// TODO remove max-widht
			return this;
		}
		if (!widthString.endsWith(";")) 
			widthString += ";";
		if (!widthString.startsWith("max-width:"))
			widthString = "max-width:" + widthString;
		return addStyle(new HtmlStyle("style", widthString));
	}
	
	public String getHtml() {
		StringBuilder html = new StringBuilder("<" + type);
		Iterator<Entry<String,String>> attrIt = attributes.entrySet().iterator();
		while (attrIt.hasNext()) {
			Entry<String,String> entry = attrIt.next();
			html.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
		}
		html.append(">");
		for (int i=0;i<counter;i++) {
			if (innerHtml.containsKey(i)) {
				html.append(innerHtml.get(i));
			} else if (innerItems.containsKey(i)) {
				html.append(innerItems.get(i).getHtml());
			} else if (innerWidgets.containsKey(i)) {
				OgemaWidget appendWidget = innerWidgets.get(i);
				try {
					html.append(getTag(innerWidgets.get(i)));
				} catch(NullPointerException e) {
					throw new IllegalStateException("Widget "+appendWidget.getId()+" not initialized correctly!");
				}
			}
			else {
				throw new RuntimeException("HTML content not found.");
			}
		}
		if (!type.equals("br")) html.append("</" + type + ">");
		return html.toString();
	}
	
	public Object getSubItem(int nr) {
		if (nr >= counter) throw new IllegalArgumentException("Index exceeds array size, " + nr + ", " + counter);
//		if(nr >= counter) return null;
		if (innerHtml.containsKey(nr)) {
			return innerHtml.get(nr);
		} else if (innerItems.containsKey(nr)) {
			return innerItems.get(nr);
		} else if (innerWidgets.containsKey(nr)) {
			return innerWidgets.get(nr);
		}
		else throw new RuntimeException("HTML content not found.");
	}
	
	public Collection<OgemaWidget> getSubWidgets() {
		List<OgemaWidget> subs = new ArrayList<OgemaWidget>();
		subs.addAll(innerWidgets.values());
		for (HtmlItem item: innerItems.values()) {
			subs.addAll(item.getSubWidgets());
		}
		return subs;
	}
	
	@Override
	public String toString() {
		return getHtml();
	}
	
	private static String getTag(OgemaWidget widget) {
	       return "<div class=\"ogema-widget\" id=\"" + widget.getId() + "\"></div>";
	}
	
}
