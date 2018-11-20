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

import java.util.Collections;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.html5.grid.AlignItems;
import de.iwes.widgets.html.html5.grid.JustifyItems;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;

/**
 * Implementations:
 * <ul>
 * 	<li>{@link SimpleGrid}: intended for static HTML grids, for instance a settings menu. Example: 
 * 		<code>
 * 			new SimpleGrid(page, gridId, true)
 * 				.addItem(text0, true, null).addItem(widget0, false, null)
 * 				.addItem(text1, true, null).addItem(widget1, false, null);
 * 		</code>
 *  <li>{@link TemplateGrid}: intended for dynamic HTML grids. Example:
 *  	<code>
 * 			new TemplateGrid&lt;T&gt;(page, gridId, false, template) {
 *				
 *				&#64;Override 
 * 				public void onGET(OgemaHttpRequest req) {
 * 					update(provider.&lt;T&gt; getAllElements(), req);
 * 				}
 * 
 * 			};
 * 		</code>
 * 		where the provider, for instance, could be a {@link TemplateDropdown} or 
 * 		{@link TemplateMultiselect} where the user selects the elements to be displayed.
 * 		Each selected object will be displayed in a row of its own. The form of the row
 * 		is determined by the passed {@link RowTemplate}.
 *  <li>{@link NamedAreaGrid}
 *  	Similar to SimpleGrid, but the cells carry names, specified in the {@link NamedAreaGrid#setTemplateAreas(java.util.List, OgemaHttpRequest)}
 *  	or {@link NamedAreaGrid#setDefaultTemplateAreas(java.util.List)} method. Multiple adjacent cells may carry the same name, 
 *  	implying that items can span multiple cells. Example:
 *  	<code>
 * 			NamedAreaGrid grid = new NamedAreaGrid(page, "gridId", true);
 * 			grid.setDefaultTemplateAreas(Arrays.asList(
 *				"header header header",
 *				"area0  area1  area2",
 *				"icontoggle icon icon",
 *				". icon icon",
 *				"footer footer footer"
 *	    	));
 *			grid.addItem("header", "Header text", null).addItem("area0", someWidget, null);
 * 		</code>
 * </ul>
 */
public abstract class AbstractGrid extends OgemaWidgetBase<GridData> {

	private static final long serialVersionUID = 1L;
	private static final String childrenSelector = ">div>*:not([data-empty])";
	private boolean defaultAppendFillColumn = false;
	private boolean defaultPrependFillColumn = false;
	private String defaultColTemplate = null;
	private String defaultRowTemplate = "auto";
	private String defaultColGap = null;
	private String defaultRowGap = null;

	protected AbstractGrid(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDynamicWidget(true);
	}
	
	protected AbstractGrid(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		setDynamicWidget(true);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "AbstractGrid", "/ogema/widget/html5/AbstractGrid.js");
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return AbstractGrid.class;
	}

	@Override
	public GridData createNewSession() {
		return new GridData(this);
	}
	
	@Override
	protected void setDefaultValues(GridData opt) {
		super.setDefaultValues(opt);
		opt.setAppendFillColumn(defaultAppendFillColumn);
		opt.setPrependFillColumn(defaultPrependFillColumn);
		opt.setColumnTemplate(defaultColTemplate);
		opt.setRowTemplate(defaultRowTemplate);
		opt.setColumnGap(defaultColGap);
		opt.setRowGap(defaultRowGap);
	}
	
	
	/**
	 * If set to true, an empty column will be prepended to the grid with growing factor 1 ("1fr")
	 * @param doPrepend
	 */
	public void setDefaultPrependFillColumn(boolean doPrepend) {
		this.defaultPrependFillColumn = doPrepend;
	}
	
	/**
	 * If set to true, an empty column will be prepended to the grid with growing factor 1 ("1fr")
	 * @param doPrepend
	 * @param req
	 */
	public void setPrependFillColumn(boolean doPrepend, OgemaHttpRequest req) {
		getData(req).setPrependFillColumn(doPrepend);
	}
	
	/**
	 * If set to true, an empty column will be added with growing factor 1 ("1fr")
	 * @param doAppend
	 */
	public void setDefaultAppendFillColumn(boolean doAppend) {
		this.defaultAppendFillColumn = doAppend;
	}
	
	/**
	 * If set to true, an empty column will be added with growing factor 1 ("1fr")
	 * @param doAppend
	 * @param req
	 */
	public void setAppendFillColumn(boolean doAppend, OgemaHttpRequest req) {
		getData(req).setAppendFillColumn(doAppend);
	}
	
	/**
	 * Prepend and append a column that fills any unneeded space? This is a 
	 * shorthand for 
	 * {@link #setDefaultAppendFillColumn(boolean)} and {@link #setDefaultPrependFillColumn(boolean)}.  
	 */
	public void setDefaultFillColumns(boolean addColumns) {
		setDefaultAppendFillColumn(addColumns);
		setDefaultPrependFillColumn(addColumns);
	}
	
	/**
	 * Prepend and append a column that fills any unneeded space? This is a 
	 * shorthand for 
	 * {@link #setAppendFillColumn(boolean, OgemaHttpRequest)} and {@link #setPrependFillColumn(boolean, OgemaHttpRequest)}.  
	 */
	public void setFillColumns(boolean addColumns, OgemaHttpRequest req) {
		setAppendFillColumn(addColumns, req);
		setPrependFillColumn(addColumns, req);
	}
	
	public boolean isPrependFillColumn(OgemaHttpRequest req) {
		return getData(req).isPrependFillColumn();
	}
	
	public boolean isAppendFillColumn(OgemaHttpRequest req) {
		return getData(req).isAppendFillColumn();
	}
	
	public int rowSize(OgemaHttpRequest req) {
		return getData(req).rowSize();
	}
	
	/**
	 * 
	 * @param row
	 * @throws IndexOutOfBoundsException if row is larger than {@link #rowSize(OgemaHttpRequest)}-1
	 */
	public int colSize(int row, OgemaHttpRequest req) {
		return getData(req).colSize(row);
	}
	
	/**
	 * @param template
	 * 		e.g. "2em repeat(4, 15px) 1fr" 
	 */
	public void setDefaultColumnTemplate(String template) {
		this.defaultColTemplate = template;
	}
	
	
	/**
	 * @param template
	 * 		e.g. "2em repeat(4, 15px) 1fr" 
	 * @param req
	 */
	public void setColumnTemplate(String template, OgemaHttpRequest req) {
		getData(req).setColumnTemplate(template);
	}
	
	/**
	 * 
	 * @param template
	 * 		e.g. "auto 1.5em"
	 */
	public void setDefaultRowTemplate(String template) {
		this.defaultRowTemplate = template;
	}
	
	/**
	 * 
	 * @param template
	 * 		e.g. "auto 1.5em"
	 * @param req
	 */
	public void setRowTemplate(String template, OgemaHttpRequest req) {
		getData(req).setRowTemplate(template);
	}
	
	/**
	 * @param gap
	 * 		e.g. "1em", or "10px". Default value is null (unset).
	 */
	public void setDefaultColumnGap(String gap) {
		this.defaultColGap = gap;
	}
	
	/**
	 * @param gap
	 * 		e.g. "1em", or "10px". Default value is null (unset).
	 * @param req
	 */
	public void setColumnGap(String gap, OgemaHttpRequest req) {
		getData(req).setColumnGap(gap);
	}

	/**
	 * @param req
	 * @return
	 * 		null if this property is unset, or a String such as "1em" or "5px"
	 */
	public String getColumnGap(OgemaHttpRequest req) {
		return getData(req).getColumnGap();
	}
	
	/**
	 * @param req
	 * @return
	 * 		null if this property is unset, or a String such as "1em" or "5px"
	 */
	public String getRowGap(OgemaHttpRequest req) {
		return getData(req).getRowGap();
	}
	
	
	/**
	 * @param gap
	 * 		e.g. "1em", or "10px". Default value is null (unset).
	 */
	public void setDefaultRowGap(String gap) {
		this.defaultRowGap = gap;
	}
	
	/**
	 * @param gap
	 * 		e.g. "1em", or "10px". Default value is null (unset).
	 * @param req
	 */
	public void setRowGap(String gap, OgemaHttpRequest req) {
		getData(req).setRowGap(gap);
	}
	
	/**
	 * Set a property for the cells (excluding cells in empty filling columns). For instance:
	 * <ul>
	 *   <li>style = "border", value = "2px solid #ffa94d"
	 *   <li>style = "border-radius", value = "5px"
	 *   <li>style = "background-color", value = "#ffd8a8"
	 *   <li>style = "padding", value = "1em"
	 *   <li>style = "color", value = "red"    
	 * </ul>
	 * @param style
	 * @param value
	 * @param req
	 */
	public void setChildProperty(String style, String value, OgemaHttpRequest req) {
		getData(req).addCssItem(childrenSelector, Collections.singletonMap(style, value));
	}
	
	/**
	 * Set a property for cells in even or odd rows only. See {@link #setChildProperty(String, String, OgemaHttpRequest)} for examples.
	 * @param evenOrOdd
	 * @param style
	 * @param value
	 * @param req
	 */
	public void setChildPropertyAlternatingRows(boolean evenOrOdd, String style, String value, OgemaHttpRequest req) {
		getData(req).addCssItem(getAlternatingRowSelector(evenOrOdd), Collections.singletonMap(style, value));
	}

	/**
	 * Set a property for cells in even or odd rows only. See {@link #setChildProperty(String, String, OgemaHttpRequest)} for examples.
	 * @param evenOrOdd
	 * @param style
	 * @param value
	 */
	public void setDefaultChildPropertyAlternatingRows(boolean evenOrOdd, String style, String value) {
		addDefaultCssItem(getAlternatingRowSelector(evenOrOdd), Collections.singletonMap(style, value));
	}
	
	public void removeChildPropertyAlternatingRows(boolean evenOrOdd, String style, OgemaHttpRequest req) {
		getData(req).removeCSSItem(getAlternatingRowSelector(evenOrOdd), style);
	}
	
	private static final String getAlternatingRowSelector(final boolean evenOrOdd) {
		return ">div>*[data-rowtype=\"" + (evenOrOdd ? 0 : 1) + "\"]:not([data-empty]):not([data-row=" + DynamicTable.HEADER_ROW_ID+"])";
	}
	
	/**
	 * See {@link #setChildProperty(String, String, OgemaHttpRequest)}
	 * @param style
	 * @param req
	 */
	public void removeChildProperty(String style, OgemaHttpRequest req) {
		getData(req).removeCSSItem(childrenSelector, style);
	}
	
	/**
	 * See {@link #setChildProperty(String, String, OgemaHttpRequest)}
	 * @param style
	 * @param value
	 */
	public void setDefaultChildProperty(String style, String value) {
		addDefaultCssItem(childrenSelector, Collections.singletonMap(style, value));
	}
	
	/**
	 * Determines the elements' horizontal alignment. Default is {@link JustifyItems#STRETCH}
	 * @param justifyItems
	 * @param req
	 */
	public void setJustifyItems(JustifyItems justifyItems, OgemaHttpRequest req) {
		addCssItem(">div", Collections.singletonMap("justify-items", justifyItems.getValue()), req);
	}
	
	/**
	 * Determines the elements' horizontal alignment. Default is {@link JustifyItems#STRETCH}
	 * @param justifyItems
	 */
	public void setDefaultJustifyItems(JustifyItems justifyItems) {
		addDefaultCssItem(">div", Collections.singletonMap("justify-items", justifyItems.getValue()));
	}
	
	/**
	 * Determines the elements' vertical alignment. Default is {@link AlignItems#STRETCH}
	 * @param alignItems
	 * @param req
	 */
	public void setAlignItems(AlignItems alignItems, OgemaHttpRequest req) {
		addCssItem(">div", Collections.singletonMap("align-items", alignItems.getValue()), req);
	}
	
	/**
	 * Determines the elements' vertical alignment. Default is {@link AlignItems#STRETCH}
	 * @param alignItems
	 */
	public void setDefaultAlignItems(AlignItems alignItems) {
		addDefaultCssItem(">div", Collections.singletonMap("align-items", alignItems.getValue()));
	}
	
	public void setGridStyle(GridStyle style, OgemaHttpRequest req) {
		final String background0 = style.getBackgroundColor0();
		final String background1 = style.getBackgroundColor1();
		final String padding = style.getPadding();
		final String border = style.getBorder();
		if (background0 != null)
			setChildPropertyAlternatingRows(true, "background-color", background0, req);
		else
			removeChildPropertyAlternatingRows(true, "background-color", req);
		if (background1 != null)
			setChildPropertyAlternatingRows(false, "background-color", background1, req);
		else
			removeChildPropertyAlternatingRows(false, "background-color", req);
		if (padding != null)
			setChildProperty("padding", padding, req);
		else
			removeChildProperty("padding", req);
		if (border != null)
			setChildProperty("border", border, req);
		else
			removeChildProperty("border", req);
	}
	
	public void setDefaultGridStye(GridStyle style) {
		final String background0 = style.getBackgroundColor0();
		final String background1 = style.getBackgroundColor1();
		final String padding = style.getPadding();
		final String border = style.getBorder();
		if (background0 != null)
			setDefaultChildPropertyAlternatingRows(true, "background-color", background0);
		if (background1 != null)
			setDefaultChildPropertyAlternatingRows(false, "background-color", background1);
		if (padding != null)
			setDefaultChildProperty("padding", padding);
		if (border != null)
			setDefaultChildProperty("border", border);
	}

}
