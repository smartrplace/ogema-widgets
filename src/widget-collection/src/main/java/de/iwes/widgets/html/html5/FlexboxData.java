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
package de.iwes.widgets.html.html5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.FlexDirection;
import de.iwes.widgets.html.html5.flexbox.FlexWrap;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;

public class FlexboxData extends WidgetData {
	
	private final List<OgemaWidget> items = new ArrayList<>();
	private JustifyContent justifyContent = null;
	private FlexDirection flexDirection = null;
	private FlexWrap flexWrap = null;
	private AlignItems alignItems = null;
	private AlignContent alignContent = null;
	private String columnGap = null;
	private boolean addEmptyItem = false;

	public FlexboxData(Flexbox flexbox) {
		super(flexbox);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		
		Map<String,String> css = new HashMap<>();
		css.put("display", "flex");
		if (justifyContent != null)
			css.put("justify-content", justifyContent.getIdentifier());
		if (flexDirection != null)
			css.put("flex-direction", flexDirection.getIdentifier());
		if (flexWrap != null)
			css.put("flex-wrap", flexWrap.getIdentifier());
		if (alignItems != null)
			css.put("align-items", alignItems.getIdentifier());
		if (alignContent != null)
			css.put("align-content", alignContent.getIdentifier());
		if (columnGap != null)
			css.put("column-gap", columnGap);
		addCssItem(">div", css);		
		JSONObject obj = new JSONObject();
		JSONArray items = new JSONArray();
		Iterator<OgemaWidget> it = this.items.iterator();
		while (it.hasNext()) 
			items.put(((OgemaWidgetBase<?>) it.next()).getTag());
		if (addEmptyItem)
			items.put("<div></div>");
		obj.put("items", items);
		return obj;
	}

	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected Collection<OgemaWidget> getSubWidgets() {
		return items;
	}
	
	@Override
	protected String getWidthSelector() {
		return ">div";
	}
	
	public void addItem(OgemaWidget widget) {
		items.add(widget);
	}
	
	public void addItem(int position,OgemaWidget widget) throws IndexOutOfBoundsException {
		items.add(position, widget);
	}
	
	/**
	 * Remove the subwidget from the items list, and destroy it
	 * @param widget
	 * @return
	 */
	public boolean removeItem(OgemaWidget widget) {
		if (widget == null) return false;
		boolean result =  items.remove(widget);
		widget.destroyWidget();
		removeCSSItems("#" + widget.getId() + ".ogema-widget");
		return result;
	}
	
	public OgemaWidget removeItem(int position) throws IndexOutOfBoundsException {
		OgemaWidget w = items.remove(position);
		if (w != null) {
			w.destroyWidget();
			removeCSSItems("#" + w.getId() + ".ogema-widget");
		}
		return w;
	}
	
	public void clear() {
		Iterator<OgemaWidget> it = items.iterator();
		while (it.hasNext()) {
			OgemaWidget w  = it.next();
			w.destroyWidget();
		}
		items.clear();
	}
	
	public List<OgemaWidget> getItems() {
		return new ArrayList<>(items);
	}

	public JustifyContent getJustifyContent() {
		return justifyContent;
	}

	public void setJustifyContent(JustifyContent justifyContent) {
		this.justifyContent = justifyContent;
	}

	public FlexDirection getFlexDirection() {
		return flexDirection;
	}

	public void setFlexDirection(FlexDirection flexDirection) {
		this.flexDirection = flexDirection;
	}

	public FlexWrap getFlexWrap() {
		return flexWrap;
	}

	public void setFlexWrap(FlexWrap flexWrap) {
		this.flexWrap = flexWrap;
	}

	public AlignItems getAlignItems() {
		return alignItems;
	}

	public void setAlignItems(AlignItems alignItems) {
		this.alignItems = alignItems;
	}

	public AlignContent getAlignContent() {
		return alignContent;
	}

	public void setAlignContent(AlignContent alignContent) {
		this.alignContent = alignContent;
	}
	
	public String getColumnGap() {
		return columnGap;
	}
	
	public void setColumnGap(String columnGap) {
		this.columnGap = columnGap;
	}
	
	protected void setAddEmptyOption(boolean addEmptyItem) {
		this.addEmptyItem = addEmptyItem;
	}
	
	/**
	 * Set a minimum margin for one of the items.
	 * 
	 * @param direction
	 * 		either null for all four, or one of "left", "right", "top", "bottom".
	 * @param size
	 * 		a CSS size specifier, like "10px"
	 * @param item
	 * 		a widget that has been previously added as a sub-item to the Flexbox 
	 * 		({@link #addItem(OgemaWidget)}). 
	 * @throws IllegalArgumentException
	 * 		if the item has not been added yet, or the direction parameter is invalid.
	 */
	public void setMargin(String direction, String size, OgemaWidget item) throws IllegalArgumentException {
		Objects.requireNonNull(item);
		Objects.requireNonNull(size);
		if (!items.contains(item))
			throw new IllegalArgumentException("Item " + item + " must be added to items list before a margin can be set");
		checkDirectionString(direction);
		Map<String,String> css = new HashMap<String, String>();
		String prop = "margin";
		if (direction != null && !direction.isEmpty())
			prop += "-" + direction;
		css.put(prop, size);
 		addCssItem("#" + item.getId() + ".ogema-widget", css);
	}
	
	/**
	 * Set the "flex-grow" property for an item. This specifies how leftover whitespace is distributed to
	 * the items; if item1 has flex-grow = 2 and item2 has flex-grow = 1, then item1 will receive twice as much 
	 * whitespace as item2.
	 * 
	 * @param value
	 * 		a positive integer
	 * @param item
	 * 	 	a widget that has been previously added as a sub-item to the Flexbox 
	 * 		({@link #addItem(OgemaWidget)}). 
	 * @throws IllegalArgumentException
	 * 		if the item has not been added yet, or the value is negative
	 */
	public void setFlexGrow(int value, OgemaWidget item) throws IllegalArgumentException {
		if (value < 0)
			throw new IllegalArgumentException("Value must be positive, got " + value);
		if (item == null) {
			final Map<String,String> css = Collections.singletonMap("flex-grow", String.valueOf(value));
			addCssItem(">div:last-child", css);
			return;
		}
		Objects.requireNonNull(item);
		if (!items.contains(item))
			throw new IllegalArgumentException("Item " + item + " must be added to items list before the flex grow property can be set");
		final Map<String,String> css = Collections.singletonMap("flex-grow", String.valueOf(value));
		addCssItem("#" + item.getId() + ".ogema-widget", css);
	}
	
	protected boolean isAddEmptyItem() {
		return addEmptyItem;
	}
	
	private static final void checkDirectionString(String in) {
		if (in == null || in.isEmpty()) return;
		if (!in.equals("left") && !in.equals("right") && !in.equals("top") && !in.equals("bottom"))
			throw new IllegalArgumentException("Invalid direction: " +in + "; use left, right, top or bottom");
	}
}
