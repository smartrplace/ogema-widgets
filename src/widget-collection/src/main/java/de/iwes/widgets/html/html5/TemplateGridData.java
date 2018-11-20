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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.complextable.RowTemplate.Row;
import de.iwes.widgets.html.html5.grid.Content;
import de.iwes.widgets.html.html5.grid.ContentType;

public class TemplateGridData<T> extends GridData {
	
	private Map<String, Object> header;
	private final List<T> items = new ArrayList<>();
	private final Map<T, Row> rows = new HashMap<>();
	
	protected TemplateGridData(TemplateGrid<T> widget) {
		super(widget);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final JSONObject json = super.retrieveGETData(req);
		final JSONArray arr= new JSONArray();
		if (header == null)
			header = getTemplate().getHeader();
		if (header != null) {
			Stream<JSONObject> stream = header.entrySet().stream()
					.map(TemplateGridData::mapToJsonHeader);
			if (prependEmptyColumn)
				stream = Stream.concat(Stream.of(Content.EMPTY_CONTENT.toJson()), stream);
			if (appendEmptyColumn)
				stream = Stream.concat(stream, Stream.of(Content.EMPTY_CONTENT.toJson()));
			arr.put(stream.collect(Collectors.toList()));
		}
		items.stream()
			.map(this::getEntry)
			.filter(Objects::nonNull)
			.map(this::mapToJson)
			.forEach(arr::put);
		json.put("data", arr);
		return json;
	}
	
	protected void update(Collection<T> items, OgemaHttpRequest req) {
		final RowTemplate<T> template = getTemplate();
		this.items.stream()
			.filter(item -> !items.contains(item))
			.map(rows::remove)
			.filter(row -> row != null)
			.forEach(this::destroyRow);
		items.stream()
			.filter(item -> !this.items.contains(item))
			.forEach(item -> rows.put(item, template.addRow(item, req)));
		if (rows.isEmpty())
			setColumnTemplate("");
		else // TODO take into account template size info?
			setColumnTemplate("repeat(" + rows.values().iterator().next().cells.size() + ", auto)");
		this.items.clear();
		this.items.addAll(items);
		final Comparator<T> comp = getComparator();
		if (comp != null)
			Collections.sort(this.items, comp);
	}
	
	@SuppressWarnings("unchecked")
	private final RowTemplate<T> getTemplate() {
		return ((TemplateGrid<T>) widget).getTemplate();
	}
	
	@SuppressWarnings("unchecked")
	private final Comparator<T> getComparator() {
		return ((TemplateGrid<T>) widget).getComparator();		
	}
	
	protected List<T> getItems0() {
		return new ArrayList<>(items); 
	}
	
	private void destroyRow(final Row row) {
		row.cells.values().stream()
			.filter(o -> o instanceof OgemaWidgetBase)
			.map(o -> (OgemaWidgetBase<?>) o)
			.forEach(OgemaWidgetBase::destroyWidget);
	}
	
	private static final JSONObject mapToJsonHeader(Map.Entry<String,Object> header) {
		final JSONObject json = new JSONObject();
		final Object val = header.getValue();
		final String value = val instanceof OgemaWidget ? ((OgemaWidget) val).getId() : val.toString();
		json.put("type", val instanceof OgemaWidget ? ContentType.OGEMA_WIDGET.getType() : ContentType.TEXT.getType());
		json.put("content", value);
		json.put("row", DynamicTable.HEADER_ROW_ID);
		json.put("col", header.getKey());
		return json;
	}
	
	private final List<JSONObject> mapToJson(final Map.Entry<T, Row> row) {
		final T t = row.getKey();
		final String id = getTemplate().getLineId(t);
		final Row r = row.getValue();
		Stream<JSONObject> stream = r.cells.entrySet().stream()
			.map(o -> toJson(id, o.getKey(), o.getValue()));
		if (prependEmptyColumn)
			stream = Stream.concat(Stream.of(Content.EMPTY_CONTENT.toJson()), stream);
		if (appendEmptyColumn)
			stream = Stream.concat(stream, Stream.of(Content.EMPTY_CONTENT.toJson()));
		return stream.collect(Collectors.toList());
	}
	
	private static JSONObject toJson(final String itemId, String colId, final Object o) {
		final JSONObject json = new JSONObject();
		if (o instanceof OgemaWidget) {
			json.put("type", ContentType.OGEMA_WIDGET.getType());
			json.put("content", ((OgemaWidget) o).getId());
		}
		else if (o instanceof String) {
			json.put("type", ContentType.TEXT.getType());
			json.put("content", o);
		} else {
			json.put("type", ContentType.EMPTY.getType());
			json.put("content", "");
		}
		if (itemId != null)
			json.put("row", itemId);
		if (colId != null)
			json.put("col", colId);
		return json;
	}
	
	protected Map<T, Row> getRows() {
		return rows;
	}
	
	private final Map.Entry<T, Row> getEntry(T item) {
		return rows.entrySet().stream()
			.filter(entry -> entry.getKey().equals(item))
			.findAny().orElse(null);
	}
	
}
