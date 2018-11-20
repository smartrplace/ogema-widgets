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
package de.iwes.widgets.html.complextable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public abstract class RowTemplate<T>  {
	
	public static class Row {
		
		public final Map<String,Object> cells = new LinkedHashMap<String,Object>();
		public final Map<String,Integer> columnSizes = new HashMap<String,Integer>();
		/**
		 * 
		 * @param columnId
		 * @param cellContent can be of simple type (String or wrapper for primitives, like Integer, Float, ...), or of type
		 * global widget
		 */
		public final void addCell(String columnId, Object cellContent) {
			cells.put(columnId,cellContent);
		}
		public final void addCell(Object cellContent) {
			addCell(String.valueOf(cells.size()), cellContent);
		}
		
		/**
		 * 1 &lt;= columnSize &lt;= 12 (Bootstrap grid system). It is not required to set the column size explicitly.
		 */
		public final void addCell(String columnId, Object cellContent, int columnSize) {
			cells.put(columnId,cellContent);
			columnSizes.put(columnId,columnSize);
		}
		public final void addCell(Object cellContent, int columnSize) {
			String columnId = String.valueOf(cells.size());
			cells.put(columnId,cellContent);
			columnSizes.put(columnId,columnSize);
		}
		
		//public boolean removeCell(String columnId) {  // problem: widgets not deregistered
		//	if (cells.remove(columnId)!=null) return true;
		//	return false;
		//}
		
	}
	
	/**
	 * This function will be executed upon calling the {@link DynamicTable#addRow(String, Map, OgemaHttpRequest) addRow} method of a {@link DynamicTable} that uses this RowTemplate.<br>
	 * Use method {@link Row#addCell(String, Object) Row.addCell} to add columns to your row template.<br>
	 * For a global table pass null as second argument (req), for a page- or session-specific table the request must be provided
	 * Return null to indicate that no row shall be added. 
	 */	
	public abstract Row addRow(T object, OgemaHttpRequest req);
	
	/**
	 * Must return a unique id for the object, to be used as line id.
	 * @param object
	 * @return
	 * 		a valid Java identifier (variable name)
	 */
	public abstract String getLineId(T object);
	
	/**
	 * Add a header to the table. This may return null or an empty map, in 
	 * which case no header will be shown. Otherwise, the map keys (= column identifiers) must be 
	 * the same as the cell identifiers in the row returned by {@link #addRow(Object, OgemaHttpRequest)}.<br>
	 * The values of the map may either Strings, or widgets (typically org.ogema.tools.widget.html.form.label.Labels).
	 * The advantage of using widgets over strings is that they can adapt to the language setting.
	 * @return
	 * 		<code>Map&lt;column id, column header&gt;</code><br>
	 * 		It is recommended to return a {@link java.util.LinkedHashMap}, then the order of columns will be preserved
	 * 
	 */
	public abstract Map<String,Object> getHeader();
	
}
