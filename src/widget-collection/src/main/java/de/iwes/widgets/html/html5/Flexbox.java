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

import java.util.List;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.FlexDirection;
import de.iwes.widgets.html.html5.flexbox.FlexWrap;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;

public class Flexbox extends OgemaWidgetBase<FlexboxData> {

	private static final long serialVersionUID = 1L;
	private JustifyContent defaultJustifyContent = null;
	private FlexDirection defaultFlexDirection = null;
	private FlexWrap defaultFlexWrap = null;
	private AlignItems defaultAlignItems = null;
	private AlignContent defaultAlignContent = null;
	private boolean defaultAddEmptyItem = false;
	
	/**
	 * An Html flexbox. See for instance https://css-tricks.com/snippets/css/a-guide-to-flexbox/<br>
	 * It serves as a container for multiple subwidgets, and controls their positioning on the page.<br>
	 * 
	 * An important property that often needs to be set is <code>justify-content</code>, which defines the
	 * arrangement of items in a row (see {@link #setJustifyContent(JustifyContent, OgemaHttpRequest)}.<br> 
	 * Typically, a good default is to set <tt>justifyContent = SPACE_BETWEEN</tt>, and <tt>alignItems = CENTER</tt>
	 * 
	 * Note: contrary to most other {@link OgemaWidget widgets}, the default
	 * Flexbox constructor is of global type, i.e. data is not session-specific. 
	 * The reason for this choice is that a Flexbox defines the structure of the page, but does 
	 * not itself contain any data (only its subwidgets do).  
	 * To create a session-specific Flexbox instead, use the 3-args constructor, and pass 
	 * <code>false</code> as third argument.  
	 * @param page
	 * @param id
	 */
	public Flexbox(WidgetPage<?> page, String id) {
		this(page, id, true);
	}

	/**
	 * An Html flexbox. See for instance https://css-tricks.com/snippets/css/a-guide-to-flexbox/<br>
	 * It servers as a container for multiple subwidgets, and controls their positioning on the page.
	 * 
	 * @param page
	 * @param id
	 * @param globalWidget
	 */
	public Flexbox(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDynamicWidget(true);
	}
	
	public Flexbox(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		setDynamicWidget(true);
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return Flexbox.class;
	}

	@Override
	public FlexboxData createNewSession() {
		FlexboxData opt = new FlexboxData(this);
		return opt;
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "Flexbox", "/ogema/widget/html5/Flexbox.js");
	}
	
	@Override
	protected void setDefaultValues(FlexboxData opt) {
		super.setDefaultValues(opt);
		opt.setJustifyContent(defaultJustifyContent);
		opt.setFlexDirection(defaultFlexDirection);
		opt.setFlexWrap(defaultFlexWrap);
		opt.setAlignContent(defaultAlignContent);
		opt.setAlignItems(defaultAlignItems);
		opt.setAddEmptyOption(defaultAddEmptyItem);
	}
	
	/**
	 * @param widget
	 * @param req
	 * @return
	 * 		this
	 */
	public Flexbox addItem(OgemaWidget widget, OgemaHttpRequest req) {
		getData(req).addItem(widget);
		return this;
	}
	
	public Flexbox addItem(int position,OgemaWidget widget, OgemaHttpRequest req) throws IndexOutOfBoundsException {
		getData(req).addItem(position, widget);
		return this;
	}
	
	/**
	 * Remove the subwidget from the items list, and destroy it
	 * @param widget
	 * @return
	 */
	public boolean removeItem(OgemaWidget widget, OgemaHttpRequest req) {
		return getData(req).removeItem(widget);
	}
	
	public OgemaWidget removeItem(int position, OgemaHttpRequest req) throws IndexOutOfBoundsException {
		return getData(req).removeItem(position);
	}
	
	public void clear(OgemaHttpRequest req) {
		getData(req).clear();
	}
	
	public List<OgemaWidget> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}
	
	/**
	 * Specify the arrangement of items in a row (or column, depending on the  
	 * flex directions property) - left, right, centered with space around items,
	 * centered with space between items, etc. Default is "flex-left".
	 * @param justifyContent
	 */
	public void setDefaultJustifyContent(JustifyContent justifyContent) {
		this.defaultJustifyContent = justifyContent;
		if (isGlobalWidget())
			setJustifyContent(justifyContent, null);
	}
	
	public void setDefaultFlexDirection(FlexDirection flexDirection) {
		this.defaultFlexDirection = flexDirection;
		if (isGlobalWidget())
			setFlexDirection(flexDirection, null);
	}
	
	public void setDefaultFlexWrap(FlexWrap flexWrap) {
		this.defaultFlexWrap = flexWrap;
		if (isGlobalWidget())
			setFlexWrap(flexWrap, null);
	}
	
	public void setDefaultAlignContent(AlignContent alignContent) {
		this.defaultAlignContent = alignContent;
		if (isGlobalWidget())
			setAlignContent(alignContent, null);
	}
	
	public void setDefaultAlignItems(AlignItems alignItems) {
		this.defaultAlignItems = alignItems;
		if (isGlobalWidget())
			setAlignItems(alignItems, null);
	}
	
	public JustifyContent getJustifyContent(OgemaHttpRequest req) {
		return getData(req).getJustifyContent();
	}

	public void setJustifyContent(JustifyContent justifyContent,OgemaHttpRequest req) {
		getData(req).setJustifyContent(justifyContent);
	}
	
	public FlexDirection getFlexDirection(OgemaHttpRequest req) {
		return getData(req).getFlexDirection();
	}

	public void setFlexDirection(FlexDirection flexDirection,OgemaHttpRequest req) {
		getData(req).setFlexDirection(flexDirection);
	}

	public FlexWrap getFlexWrap(OgemaHttpRequest req) {
		return getData(req).getFlexWrap();
	}

	public void setFlexWrap(FlexWrap flexWrap, OgemaHttpRequest req) {
		getData(req).setFlexWrap(flexWrap);
	}

	public AlignItems getAlignItems(OgemaHttpRequest req) {
		return getData(req).getAlignItems();
	}

	public void setAlignItems(AlignItems alignItems, OgemaHttpRequest req) {
		getData(req).setAlignItems(alignItems);
	}

	public AlignContent getAlignContent(OgemaHttpRequest req) {
		return getData(req).getAlignContent();
	}

	public void setAlignContent(AlignContent alignContent, OgemaHttpRequest req) {
		getData(req).setAlignContent(alignContent);
	}
	
	public String getColumnGap(OgemaHttpRequest req) {
		return getData(req).getColumnGap();
	}
	
	public void setColumnGap(String columnGap, OgemaHttpRequest req) {
		getData(req).setColumnGap(columnGap);
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
	 * 		({@link #addItem(OgemaWidget, OgemaHttpRequest)}). 
	 * @param req
	 * @throws IllegalArgumentException
	 * 		if the item has not been added yet, or the direction parameter is invalid.
	 */
	public void setMargin(String direction, String size, OgemaWidget item, OgemaHttpRequest req) throws IllegalArgumentException {
		getData(req).setMargin(direction, size, item);
	}
	
	/**
	 * Set the "flex-grow" property for an item. This specifies how leftover whitespace is distributed to
	 * the items; if item1 has flex-grow = 2 and item2 has flex-grow = 1, then item1 will receive twice as much 
	 * whitespace as item2. Default: 0.
	 * 
	 * @param value
	 * 		a positive integer
	 * @param item
	 * 	 	a widget that has been previously added as a sub-item to the Flexbox 
	 * 		({@link #addItem(OgemaWidget, OgemaHttpRequest)}), or null if 
	 * 		{@link #setAddEmptyItem(boolean, OgemaHttpRequest) addEmptyItem} is true,
	 * 		in which case the option applies to the empty item.
	 * @param req
	 * @throws IllegalArgumentException
	 * 		if the item has not been added yet, or the value is negative
	 */
	public void setFlexGrow(int value, OgemaWidget item, OgemaHttpRequest req) throws IllegalArgumentException {
		getData(req).setFlexGrow(value, item);
	}
	
	
	/**
	 * Add an empty item to the end of the items list, that will take up the remaining space?
	 * @param addEmptyItem
	 */
	public void setDefaultAddEmptyItem(boolean addEmptyItem) {
		this.defaultAddEmptyItem = addEmptyItem; 
	}
	
	public void setAddEmptyItem(boolean addEmptyItem, OgemaHttpRequest req) {
		getData(req).setAddEmptyOption(addEmptyItem);
	}
	

}
