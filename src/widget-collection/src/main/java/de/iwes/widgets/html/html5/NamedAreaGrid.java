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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.grid.Content;

public class NamedAreaGrid extends AbstractGrid {

	private static final long serialVersionUID = 1L;
	private Map<String, Content> defaultContent;
	private List<String> defaultTemplateAreas;

	public NamedAreaGrid(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public NamedAreaGrid(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	@Override
	public GridData createNewSession() {
		return new NamedAreaGridData(this);
	}
	
	@Override
	public NamedAreaGridData getData(OgemaHttpRequest req) {
		return (NamedAreaGridData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(GridData opt) {
		super.setDefaultValues(opt);
		final NamedAreaGridData data = (NamedAreaGridData) opt;
		if (defaultContent != null)
			data.items.putAll(defaultContent);
		if (defaultTemplateAreas != null)
			data.setTemplateAreas(defaultTemplateAreas);
	}
	
	/**
	 * Example:<br>
	 * <code>
	 * "header header header header"
     * "main main . sidebar"
     * "footer footer footer footer";
     * </code>
     * All rows must have the same size
	 * @param templateAreas
	 * @throws IllegalArgumentException if the rows do not all have the same size
	 */
	public void setDefaultTemplateAreas(final List<String> templateAreas) {
		if (templateAreas == null || templateAreas.isEmpty()) {
			defaultTemplateAreas = null;
			return;
		}
		if (templateAreas.stream()
				.mapToInt(row -> row.trim().split("\\s+").length)
				.distinct().count() > 1)
				throw new IllegalArgumentException("All rows must have the same length");
		this.defaultTemplateAreas = new ArrayList<>(templateAreas);
	}
	
	/**
	 * @param items
	 * 		keys: area names; values: items to be displayed (widgets, text or HTML items)
	 */
	public void setDefaultContent(final Map<String, Object> items) {
		if (items == null || items.isEmpty()) {
			this.defaultContent = null;
			return;
		}
		this.defaultContent = items.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> new Content(entry.getValue())));
	}
	
	/**
	 * @param req
	 * @return
	 *  	an unmodifiable map. Keys: area name, values: items. 
	 */
	public Map<String, Content> getItems(OgemaHttpRequest req) {
		return getData(req).getItemsMap();
	}
	
	/**
	 * Example:<br>
	 * <code>
	 * "header header header header"
     * "main main . sidebar"
     * "footer footer footer footer";
     * </code>
     * All rows must have the same size
	 * @param templateAreas
	 * @param req
	 * @throws IllegalArgumentException if the rows do not all have the same size
	 */
	public void setTemplateAreas(final List<String> templateAreas, OgemaHttpRequest req) {
		getData(req).setTemplateAreas(templateAreas);
	}
	
	/**
	 * @param items
	 * 		keys: area names; values: items to be displayed (widgets, text or HTML items)
	 * @param req
	 */
	public void update(final Map<String, Object> items, OgemaHttpRequest req) {
		getData(req).update(items);
	}

	public NamedAreaGrid addItem(String area, Object item, OgemaHttpRequest req) {
		getData(req).addItem(area, item);
		return this;
	}
	
	public void removeItem(String area, OgemaHttpRequest req) {
		removeItem(area, req);
	}
	
	/**
	 * Set a property for a named area element. For instance:
	 * <ul>
	 *   <li>style = "border", value = "2px solid #ffa94d"
	 *   <li>style = "border-radius", value = "5px"
	 *   <li>style = "background-color", value = "#ffd8a8"
	 *   <li>style = "padding", value = "1em"
	 *   <li>style = "color", value = "red"    
	 * </ul>
	 * @param area
	 * 		the area identifier of an item. See {@link #setTemplateAreas(List, OgemaHttpRequest)} or
	 * 		{@link #setDefaultTemplateAreas(List)}
	 * @param property
	 * 		CSS property, such as "color", "padding" or "border"
	 * @param value
	 * 		CSS value, such as "red" or "2em"
	 * @param req
	 */
	public void setChildPropertyArea(String area, String property, String value, OgemaHttpRequest req) {
		getData(req).addCssItem( ">div>*[data-area=" + area + "]", Collections.singletonMap(property, value));
	}
	
	public void addChildPropertiesArea(String area, Map<String, String> cssMap, OgemaHttpRequest req) {
		getData(req).addCssItem(">div>*[data-area=" + area + "]", cssMap);
	}
	
	public void removeChildPropertyArea(String area, String property, OgemaHttpRequest req) {
		getData(req).removeCSSItem(">div>*[data-area=" + area + "]", property);
	}
	
	/**
	 * See {@link #setChildPropertyArea(String, String, String, OgemaHttpRequest)}
	 * @param area
	 * @param property
	 * @param value
	 */
	public void setDefaultChildPropertyArea(String area, String property, String value) {
		addDefaultCssItem( ">div>*[data-area=" + area + "]", Collections.singletonMap(property, value));
	}
	
	/**
	 * see {@link #setChildPropertyArea(String, String, String, OgemaHttpRequest)}.
	 * @param area
	 * @param cssMap
	 */
	public void setDefaultChildPropertyArea(String area, Map<String, String> cssMap) {
		addDefaultCssItem( ">div>*[data-area=" + area + "]", cssMap);
	}

	/*
	 * 
	 */
	
	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setChildPropertyAlternatingRows(boolean evenOrOdd, String style, String value, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by NamedAreaGrid");
	}
	
	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setDefaultChildPropertyAlternatingRows(boolean evenOrOdd, String style, String value) {
		throw new UnsupportedOperationException("Not supported by NamedAreaGrid");
	}
	
	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void removeChildPropertyAlternatingRows(boolean evenOrOdd, String style, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by NamedAreaGrid");
	}
	
	
}
