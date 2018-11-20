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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.grid.Content;
import de.iwes.widgets.html.html5.grid.ContentType;

public class NamedAreaGridData extends GridData {

	private boolean appendEmpty;
	private boolean prependEmpty;
	private List<String> templateAreas;
	// Map<area name, content>
	final Map<String, Content> items = new HashMap<>();
	
	protected NamedAreaGridData(NamedAreaGrid widget) {
		super(widget);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final String colTemplate = getColumnTemplate();
		final int[] rowsCols = setTemplateAreasInternal(templateAreas);
		final StringBuilder templateBuilder = new StringBuilder();
		if (rowsCols == null) {
			setColumnTemplate(null);
			setRowTemplate(null);
		} else {
			final int templateSize = colTemplate == null ? 0 : colTemplate.split("\\s+").length;
			final int szWithoutEmpties = rowsCols[1];
			int szWithEmpties = szWithoutEmpties;
			if (prependEmpty)
				szWithEmpties++;
			if (appendEmpty)
				szWithEmpties++;
			if (szWithEmpties != templateSize) {
				if (prependEmpty)
					templateBuilder.append("1fr ");
				if (templateSize == szWithoutEmpties) {
					if (colTemplate != null)
						templateBuilder.append(colTemplate);
				} else {
					final String newColTemplate = "repeat(" + szWithoutEmpties + ", auto)";
					setColumnTemplate(newColTemplate);
					templateBuilder.append(newColTemplate);
				}
				if (appendEmpty)
					templateBuilder.append(" 1fr");
			}
			final String rowTemplate = getRowTemplate();
			final int rowSz = rowTemplate == null ? 0 : rowTemplate.split("\\s+").length;
			if (rowSz != rowsCols[0]) {
				setRowTemplate("repeat(" + rowsCols[0] + ", auto)");
			}
		}
		final JSONObject json = super.retrieveGETData(req);
		if (templateBuilder.length() > 0)
			json.put("colTemplate", templateBuilder.toString());
		final JSONArray arr = new JSONArray();
		arr.put(items.entrySet().stream()
			.map(NamedAreaGridData::map)
			.collect(Collectors.toList()));
		json.put("data", arr);
		return json;
	}

	/**
	 * 
	 * @param templateAreas
	 * @return
	 * 		[rows, cols]
	 */
	private int[] setTemplateAreasInternal(final List<String> templateAreas) {
		if (templateAreas == null  || templateAreas.isEmpty()) {
			removeCSSItem(">div", "grid-template-areas");
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String area : templateAreas) {
			if (!first)
				sb.append('\n');
			first = false;
			sb.append('\'');
			if (prependEmpty) {
				sb.append('.');
				sb.append(' ');
			}
			sb.append(area);
			if (appendEmpty) {
				sb.append(' ');
				sb.append('.');
			}
			sb.append('\'');
		}
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			
			public Void run() {
				try {
					final Method m = WidgetData.class.getDeclaredMethod("addCssItemUnescaped", String.class, Map.class);
					m.setAccessible(true);
					m.invoke(NamedAreaGridData.this, ">div", Collections.singletonMap("grid-template-areas", sb.toString()));
				} catch (Exception e) {
					if (e instanceof RuntimeException)
						throw (RuntimeException) e;
					else
						throw new RuntimeException(e);
				}
				return null;
			}	
			
		});
//		addCssMap(Collections.singletonMap(">div", Collections.singletonMap("grid-template-areas", "'" + templateAreas.stream().collect(Collectors.joining("'\n'")) + "'")));
		return new int[] {templateAreas.size(), templateAreas.get(0).trim().split("\\s+").length};
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
	protected void setTemplateAreas(final List<String> templateAreas) {
		if (templateAreas == null  || templateAreas.isEmpty()) {
			this.templateAreas = null;
			return;
		}
		if (templateAreas.stream()
			.mapToInt(row -> row.trim().split("\\s+").length)
			.distinct().count() > 1)
			throw new IllegalArgumentException("All rows must have the same length");
		this.templateAreas = new ArrayList<>(templateAreas);
	}
	
	/**
	 * @param items
	 * 		keys: area names; values: items to be displayed (widgets, text or HTML items)
	 */
	protected void update(final Map<String, Object> items) {
		this.items.values().forEach(NamedAreaGridData::destroyItem);
		this.items.clear();
		if (items != null && !items.isEmpty()) {
			items.entrySet().stream()
				.forEach(entry -> this.items.put(entry.getKey(), new Content(entry.getValue())));
		}
	}
	
	protected Map<String, Content> getItemsMap() {
		return Collections.unmodifiableMap(items);
	}

	protected void addItem(String area, Object item) {
		Objects.requireNonNull(area);
		Objects.requireNonNull(item);
		destroyItem(this.items.put(area, new Content(item)));
	}
	
	protected void removeItem(String area) {
		destroyItem(this.items.remove(area));
	}
	
	@Override
	protected void setAppendFillColumn(boolean doAdd) {
		this.appendEmpty = doAdd;
	}
	
	@Override
	protected boolean isAppendFillColumn() {
		return appendEmpty;
	}
	
	@Override
	protected void setPrependFillColumn(boolean doPrepend) {
		this.prependEmpty = doPrepend;
	}
	
	@Override
	protected boolean isPrependFillColumn() {
		return prependEmpty;
	}
	
	private static void destroyItem(final Content o) {
		if (o == null || o.getContentType() != ContentType.OGEMA_WIDGET)
			return;
		try {
			((OgemaWidgetBase<?>) o.getContent()).destroyWidget();
		} catch (Exception e) {
			LoggerFactory.getLogger(NamedAreaGridData.class).warn("Destroying widget failed",e);
		}
	}
	
	private static JSONObject map(final Map.Entry<String, Content> entry) {
		final JSONObject json = entry.getValue().toJson();
		json.put("area", entry.getKey());
		return json;
	}
	
}
