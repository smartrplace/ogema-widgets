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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.grid.Content;
import de.iwes.widgets.html.html5.grid.ContentType;

// TODO synchronization for global widget
public class GridData extends WidgetData {
	
	// rows<columns>; 
	private final List<List<Content>> items = new ArrayList<>();
	protected boolean appendEmptyColumn = false;
	protected boolean prependEmptyColumn = false;
	// see https://css-tricks.com/snippets/css/complete-guide-grid/ for the meaning of these fields
	private String colTemplate = null;
	private String rowTemplate = "auto";
	private String colGap = null;
	private String rowGap = null;

	protected GridData(AbstractGrid widget) {
		super(widget);
		addCssItem(">div", Collections.singletonMap("display", "grid"));
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final JSONObject json = new JSONObject();
		final JSONArray rows = new JSONArray();
		readLock();
		try {
			items.stream()
				.map(this::mapToJson)
				.forEach(rows::put);
			json.put("data", rows);
			final StringBuilder colBuilder = new StringBuilder();
			if (prependEmptyColumn)
				colBuilder.append("1fr ");
			if (colTemplate == null) {
				final int sz = !items.isEmpty() ? items.get(0).size() : 0;
				if (sz > 0)
					colBuilder.append("repeat( ").append(sz).append(", auto)");
			} else {
				colBuilder.append(colTemplate);
			}
			if (appendEmptyColumn) {
				colBuilder.append(" 1fr");
			}
			json.put("colTemplate", colBuilder.toString());
			if (rowTemplate != null) {
				json.put("rowTemplate", rowTemplate);
			}
			if (colGap != null)
				json.put("colGap", colGap);
			if (rowGap != null)
				json.put("rowGap", rowGap);
		} finally {
			readUnlock();
		}
		return json;
	}
	
	private final List<JSONObject> mapToJson(final List<Content> col) {
		Stream<Content> stream;
		if (prependEmptyColumn)
			stream = Stream.concat(Stream.of(Content.EMPTY_CONTENT), col.stream());
		else
			stream = col.stream();
		if (appendEmptyColumn)
			stream = Stream.concat(stream, Stream.of(Content.EMPTY_CONTENT));
		return stream.map(Content::toJson).collect(Collectors.toList());
	}
	
	protected List<List<Content>> getItems() {
		readLock();
		try {
			return items.stream()
				.map(list -> list.stream().collect(Collectors.toList()))
				.collect(Collectors.toList());
		} finally {
			readUnlock();
		}
	}

	protected void addItem(Object item, boolean newRow) {
		writeLock();
		try {
			final List<Content> row;
			if (newRow || items.isEmpty()) {
				row = new ArrayList<>(8);
				items.add(row);
			} else
				row = items.get(items.size()-1);
			row.add(wrap(item));
		} finally {
			writeUnlock();
		}
	}
	
	private static Content wrap(Object o) {
		if (o == null)
			return Content.EMPTY_CONTENT;
		if (o instanceof OgemaWidget)
			return new Content(o, ContentType.OGEMA_WIDGET);
		if (o instanceof String)
			return new Content(o, ContentType.TEXT);
		if (o instanceof HtmlItem)
			return new Content(((HtmlItem) o).getHtml(), ContentType.HMTL);
		else throw new IllegalArgumentException("Illegal content " + o);
	}	
	
	
	/**
	 * Insert a new item to the grid. Existing items are not replaced, but may be moved to the right.
	 * @param row
	 * @param col
	 * @param item
	 * @throws IndexOutOfBoundsException if row is larger than 
	 */
	protected void addItem(int row, int col, Object item) {
		writeLock();
		try {
			final int rowSz = rowSize();
			if (row > rowSz)
				throw new IndexOutOfBoundsException("Row " + row + " does not exists. There are " + rowSz + " rows");
			if (row == rowSz)
				items.add(new ArrayList<>(8));
			final List<Content> cols = items.get(row);
			final int colSz = colSize(row);
			if (col > colSz)
				throw new IndexOutOfBoundsException("Columns " + col + " does not exists. There are " + colSz + " columns in row " + row);
			cols.add(wrap(item));
		} finally {
			writeUnlock();
		}
	}

	protected void setPrependFillColumn(boolean doPrepend) {
		this.prependEmptyColumn = doPrepend;
	}
	
	protected void setAppendFillColumn(boolean doAdd) {
		this.appendEmptyColumn = doAdd;
	}
	
	protected boolean isPrependFillColumn() {
		return prependEmptyColumn;
	}
	
	protected boolean isAppendFillColumn() {
		return appendEmptyColumn;
	}
	
	/**
	 * @param row
	 * @param col
	 * @throws IndexOutOfBoundsException
	 */
	void deleteItem(int row, int col) {
		writeLock();
		try {
			final Content o = items.get(row).remove(col);
			if (o.getContentType() == ContentType.OGEMA_WIDGET)
				((OgemaWidgetBase<?>) o.getContent()).destroyWidget();
		} finally {
			writeUnlock();
		}
	}
	
	void clear() {
		writeLock();
		try {
			items.stream()
				.flatMap(List::stream)
				.filter(c -> c.getContentType() == ContentType.OGEMA_WIDGET)
				.map(Content::getContent)
				.map(c -> (OgemaWidget) c)
				.forEach(OgemaWidget::destroyWidget);
			this.items.clear();
		} finally {
			writeUnlock();
		}
	}
	
	boolean deleteItem(OgemaWidget item) {
		writeLock();
		try {
			for (List<Content> col: items) {
				final Iterator<Content> it = col.iterator();
				while (it.hasNext()) {
					final Content item2 = it.next();
					if (item.equals(item2.getContent())) {
						it.remove();
						item.destroyWidget();
						return true;
					}
				}
			}
		} finally {
			writeUnlock();
		}
		return false;
	}
	
	int rowSize() {
		readLock();
		try {
			return items.size();
		} finally {
			readUnlock();
		}
	}
	
	protected void setColumnTemplate(String template) {
		this.colTemplate = template;
	}
	
	protected void setRowTemplate(String template) {
		this.rowTemplate = template;
	}
	
	protected String getColumnTemplate() {
		return colTemplate;
	}
	
	protected String getRowTemplate() {
		return rowTemplate;
	}
	
	protected void setColumnGap(String gap) {
		this.colGap  =gap;
	}
	
	protected String getColumnGap() {
		return colGap;
	}
	
	protected void setRowGap(String gap) {
		this.rowGap = gap;
	}
	
	protected String getRowGap() {
		return rowGap;
	}
	
	/**
	 * 
	 * @param row
	 * @throws IndexOutOfBoundsException if row is larger than {@link #rowSize()}-1
	 */
	int colSize(int row) {
		final int sz = rowSize();
		if (row > sz-1)
			throw new IndexOutOfBoundsException("Row " + row + " does not exists. There are " + sz + " rows");
		return items.get(row).size();
	}

}
