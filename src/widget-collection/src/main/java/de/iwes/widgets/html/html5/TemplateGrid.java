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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.complextable.RowTemplate.Row;
import de.iwes.widgets.html.html5.grid.Content;

/**
 * A tabular grid, whose rows are modeled on a class T. Might be used instead of {@link DynamicTable}, since it has 
 * better responsive properties than the latter, and does not require heavy dependencies. Example usage:
 * <code>
 * 		new TemplateGrid&lt;T&gt;(page, gridId, false, template) {
 *			
 *			&#64;Override 
 * 			public void onGET(OgemaHttpRequest req) {
 * 				update(provider.&lt;T&gt; getAllElements(), req);
 * 			}
 * 		};
 * 	</code>
 * 
 * See {@link AbstractGrid} for more grid types.
 * @param <T>
 */
public class TemplateGrid<T> extends AbstractGrid {

	private static final long serialVersionUID = 1L;
	private final RowTemplate<T> template;
	private Comparator<T> comparator;

	public TemplateGrid(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<T> template) {
		super(page, id, globalWidget);
		this.template = Objects.requireNonNull(template);
	}

	public TemplateGrid(OgemaWidget parent, String id, OgemaHttpRequest req, RowTemplate<T> template) {
		super(parent, id, req);
		this.template = Objects.requireNonNull(template);
	}
	
	@Override
	public GridData createNewSession() {
		return new TemplateGridData<T>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplateGridData<T> getData(OgemaHttpRequest req) {
		return (TemplateGridData<T>) super.getData(req);
	}
	
	public RowTemplate<T> getTemplate() {
		return template;
	}

	public Comparator<T> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	public void update(Collection<T> items, OgemaHttpRequest req) {
		getData(req).update(items, req);
	}
	
	public Collection<Row> getRows(OgemaHttpRequest req) {
		return new ArrayList<Row>(getData(req).getRows().values());
	}
	
	/**
	 * Set a property for the cells in a specific row. For instance:
	 * <ul>
	 *   <li>style = "border", value = "2px solid #ffa94d"
	 *   <li>style = "border-radius", value = "5px"
	 *   <li>style = "background-color", value = "#ffd8a8"
	 *   <li>style = "padding", value = "1em"
	 *   <li>style = "color", value = "red"    
	 * </ul>
	 * @param row
	 * 		the {@link RowTemplate#getLineId(Object) line id} of a row, or "__headerRow__" for the header
	 * @param style
	 * @param value
	 * @param req
	 */
	public void setChildPropertyRow(String row, String style, String value, OgemaHttpRequest req) {
		getData(req).addCssItem( ">div>*[data-row=" + row + "]:not([data-empty])", Collections.singletonMap(style, value));
	}
	
	/**
	 * Set a CSS property for a single cell.
	 * @param row
	 * @param col
	 * @param style
	 * @param value
	 * @param req
	 */
	public void setCellProperty(String row, String col, String style, String value, OgemaHttpRequest req) {
		getData(req).addCssItem( ">div>*[data-row=" + row + "][data-col=" + col + "]:not([data-empty])", Collections.singletonMap(style, value));
	}
	
	/**
	 * Set a property for the cells in a specific column (excluding the header). For instance:
	 * <ul>
	 *   <li>style = "border", value = "2px solid #ffa94d"
	 *   <li>style = "border-radius", value = "5px"
	 *   <li>style = "background-color", value = "#ffd8a8"
	 *   <li>style = "padding", value = "1em"
	 *   <li>style = "color", value = "red"    
	 * </ul>
	 * @param col
	 * 		the column id of a col, as specified in the {@link RowTemplate#addRow(Object, OgemaHttpRequest)} method
	 * @param style
	 * @param value
	 * @param req
	 */
	public void setChildPropertyCol(String col, String style, String value, OgemaHttpRequest req) {
		getData(req).addCssItem( ">div>*[data-col=" + col + "]", Collections.singletonMap(style, value));
	}
	
	/**
	 * See {@link #setChildPropertyRow(String, String, String, OgemaHttpRequest)}
	 * @param row
	 * @param style
	 * @param value
	 */
	public void setDefaultChildPropertyRow(String row, String style, String value) {
		addDefaultCssItem(">div>*[data-row=" + row + "]:not([data-empty])", Collections.singletonMap(style, value));
	}
	
	/**
	 * See {@link #setChildPropertyCol(String, String, String, OgemaHttpRequest)}
	 * @param col
	 * @param style
	 * @param value
	 */
	public void setDefaultChildPropertyCol(String col, String style, String value) {
		addDefaultCssItem( ">div>*[data-col=" + col + "]", Collections.singletonMap(style, value));
	}
	
	/**
	 * Set a CSS property for a single cell.
	 * @param row
	 * @param col
	 * @param style
	 * @param value
	 */
	public void setDefaultCellProperty(String row, String col, String style, String value) {
		addDefaultCssItem( ">div>*[data-row=" + row + "][data-col=" + col + "]:not([data-empty])", Collections.singletonMap(style, value));
	}
	
}
