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
		 * {@link GlobalWidget}.
		 */
		public final void addCell(String columnId, Object cellContent) {
			cells.put(columnId,cellContent);
		}
		public final void addCell(Object cellContent) {
			addCell(String.valueOf(cells.size()), cellContent);
		}
		
		/**
		 * 1 <= columnSize <= 12 (Bootstrap grid system). It is not required to set the column size explicitly.
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
	 * This function will be executed upon calling the {@link DynamicTable#addRow(String) addRow} method of a {@link DynamicTable} that uses this RowTemplate.<br>
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
	 * the same as the cell identifiers in the row returned by {@link #addRowItem(Object, OgemaHttpRequest)}.<br>
	 * The values of the map may either Strings, or widgets (typically {@see org.ogema.tools.widget.html.form.label.Labels}).
	 * The advantage of using widgets over strings is that they can adapt to the language setting.
	 * @return
	 * 		<code>Map&lt;column id, column header&gt;</code><br>
	 * 		It is recommended to return a {@see java.util.LinkedHashMap}, then the order of columns will be preserved
	 * 
	 */
	public abstract Map<String,Object> getHeader();
	
}
