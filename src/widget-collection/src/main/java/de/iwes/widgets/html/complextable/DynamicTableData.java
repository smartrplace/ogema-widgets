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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.plus.TemplateData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.RowTemplate.Row;

public class DynamicTableData<T> extends WidgetData implements TemplateData<T> {
	
	public static final WidgetStyle<DynamicTable<?>> TABLE_PURE = new WidgetStyle<DynamicTable<?>>("div>div>table",Collections.singletonList("table"),2); 
	public static final WidgetStyle<DynamicTable<?>> TABLE_STRIPED = new WidgetStyle<DynamicTable<?>>("div>div>table",Arrays.asList("table","table-striped"),2);
	public static final WidgetStyle<DynamicTable<?>> BOLD_HEADER = new WidgetStyle<DynamicTable<?>>("div>div>table",Collections.singletonList("boldHeader"),2);
	/**
	 * @deprecated use {@link WidgetData#TEXT_ALIGNMENT_CENTERED} instead
	 */
	@Deprecated
	public static final WidgetStyle<DynamicTable<?>> CELL_ALIGNMENT_CENTERED = new WidgetStyle<DynamicTable<?>>("div>div>table>tbody",Collections.singletonList("text-center"),2);
	/**
	 * @deprecated use {@link WidgetData#TEXT_ALIGNMENT_LEFT} instead
	 */
	@Deprecated
	public static final WidgetStyle<DynamicTable<?>> CELL_ALIGNMENT_LEFT = new WidgetStyle<DynamicTable<?>>("div>div>table>tbody",Collections.singletonList("text-left"),2);
	/**
	 * @deprecated use {@link WidgetData#TEXT_ALIGNMENT_RIGHT} instead
	 */
	@Deprecated
	public static final WidgetStyle<DynamicTable<?>> CELL_ALIGNMENT_RIGHT = new WidgetStyle<DynamicTable<?>>("div>div>table>tbody",Collections.singletonList("text-right"),2); 
	
	protected Map<String,Map<String,Object>> content;
	protected Map<String,Integer> columnSizes;
	protected String tableClasses = "";
	protected Comparator<String> rowIdComparator = null;
	protected final List<T> objects = new ArrayList<>();
	
	/************************** constructor ***********************/

	public DynamicTableData(DynamicTable<T> table) {
		super(table);
		this.content = new LinkedHashMap<String,Map<String,Object>>();
		this.columnSizes = new HashMap<>();
        addStyle(TABLE_PURE);  // default
	}
 
	/************************** public methods that can be overwritten ***********************/
	/**
     * This method is triggered by POST requests
     */
    @Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {   
		throw new UnsupportedOperationException("POST not supported");
	}
    
	/************************** public methods ***********************/
    
    public void updateRows(Collection<? extends T> newObjects) {
    	updateRows(newObjects, getInitialRequest());
    }
    
    public void refreshHeader() {
    	Map<String,Object> map = content.remove(DynamicTable.HEADER_ROW_ID);
    	if (map != null) {
    		for (Object obj:map.values()) {
    			if (obj instanceof OgemaWidgetBase<?>) {
    				try {
    					((OgemaWidgetBase<?>) obj).destroyWidget();
    				} catch (Exception e) {
    					LoggerFactory.getLogger(getClass()).error("Could not remove header widget " + obj,e);
    				}
    			}
    		}
    	}
    	content.put(DynamicTable.HEADER_ROW_ID, getTemplate().getHeader());
    }
    
    /**
     * remove all rows not contained in newObjects, and add those that are new.
     * Does not modify those rows that were available and are contained in newObjects.
     * @param newIds
     */
    // note regarding locks... since removeRow involves calls to subwidget destroy method, we should 
    // avoid keeping a lock for the whole duration of this call, even thouhg this means that there can
    // can be race conditions in theory
    public void updateRows(Collection<? extends T> newObjects, OgemaHttpRequest req) {
    	if (newObjects == null)
    		newObjects = Collections.emptyList();
    	List<String> forRemoval = new ArrayList<String>();
//    	List<T> forAddition = new ArrayList<>();
    	List<String> newIds = new ArrayList<>();
    	Set<String> olds;
    	writeLock();
    	try {
    		objects.clear();
    		objects.addAll(newObjects);
    		olds = new HashSet<>(content.keySet());
	    	RowTemplate<T> template = getTemplate();
	    	for (T newObj: newObjects) {
	    		String newId = template.getLineId(newObj);
	    		newIds.add(newId);
	    		if (!olds.contains(newId))
//	    			forAddition.add(newObj);
	    			addItem(newObj, req);
	    	}
	    	
	    	for (String old :olds) {
	    		if (!newIds.contains(old) && !old.equals(DynamicTable.HEADER_ROW_ID))
	    			forRemoval.add(old);
	    	}
//	    	for (T add: forAddition) {
//	    		addRow(add, req); // TODO?
//	    	}
    	} finally {
    		writeUnlock();
    	}
    	for (String rem: forRemoval) {
    		removeRow(rem); // TODO improve?
    	}

    }
    
    /**
     * return cell entry for cell (rowId,colId), or null if the specified cell does not exist.
     */
    public Object getCellContent(String rowId, String colId) {
    	readLock();
    	try {
	    	if (content.get(rowId)==null) return null;
	    	return content.get(rowId).get(colId);
    	} finally {
    		readUnlock();
    	}
    	
    }
    
    public Set<String> getRows() {
    	readLock();
    	try {
    		return new LinkedHashSet<>(content.keySet());
    	} finally {
    		readUnlock();
    	}
    }
    
    public Set<String> getColumns() {
    	readLock();
    	try {
	    	Set<String> rows = getRows(); // only acquires read lock
	    	if (rows.isEmpty()) return rows;
	    	String firstItem = rows.iterator().next();
    		return new LinkedHashSet<>(content.get(firstItem).keySet());
    	} finally {
    		readUnlock();
    	} 
    }
    
    public Map<String,Map<String,Object>> getTable() {
    	readLock();
    	try {
//    		return content;
    		return new HashMap<>(content); // TODO check if this is sufficient
    	} finally {
    		readUnlock();
    	}
    }
    
    @Override
    protected void removeSubWidgets() {
    	clear();
    }
    
    public boolean removeRow(String rowId) {
    	List<OgemaWidgetBase<?>> widgets =getWidgets(rowId);
    	// System.out.println("Removing row " + rowId + ", number of widgets " + widgets.size());
    	for (OgemaWidgetBase<?> widget: widgets) {
			widget.destroyWidget();
		}
    	writeLock();
    	try {
    		Iterator<T> it = objects.iterator();
    		while(it.hasNext()) {
    			T obj = it.next();
    			if (getTemplate().getLineId(obj).equals(rowId)) {
    				it.remove();
    				break;
    			}
    		}
	    	if (content.remove(rowId)==null) {
	    		return false;
	    	}
    	} finally {
    		writeUnlock();
    	}
    	return true;
    }
    
    public boolean removeColumn(String colId) {
    	Set<String> rows = getRows();
    	boolean colFound = false;
    	Iterator<String> it = rows.iterator();
    	while(it.hasNext()) {
    		String row = it.next();
    		Object obj;
    		readLock();
    		try {
	    		Map<String,Object> map  = content.get(row);
	    		if (map == null)
	    			continue;
	    		obj = map.remove(colId);
	    		if (obj == null)
	    			continue;
    			colFound = true;
    		} finally {
    			readUnlock();
    		}
    		if (obj instanceof OgemaWidgetBase<?>) {
    			OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) obj;
    			widget.destroyWidget();
    		}
    	}
    	return colFound;
    }
        
    public void addRow(String lineId, Map<String,Object> columns) {
    	writeLock(); // several read locks acquired within the method
    	try {
    		final Set<String> existingColumns = getColumns();
	    	if (existingColumns.size() > 0) {
	    		if (!columns.keySet().equals(existingColumns)) {
	    			throw new RuntimeException("Columns in new row must coincide exactly with existing columns.");
				}
	    	}    	
	    	content.put(lineId, new LinkedHashMap<>(columns));
    	} finally {
    		writeUnlock();
    	}
    }
    
    public boolean removeItem(T object) {
    	if (object == null)
    		return false;
    	return removeRow(getTemplate().getLineId(object));
    }
    
    public boolean addItem(T object) {
    	return addItem(object,getInitialRequest()); // XXX ugly
    }
    
	public boolean addItem(T object, OgemaHttpRequest req) {
		if (object == null)
			return false;
    	if (getTemplate() == null ) {
    		throw new UnsupportedOperationException("RowTemplate required to generate new row.");
    	}
    	String lineId = getTemplate().getLineId(object);
    	if (lineId == null) {
    		LoggerFactory.getLogger(getClass()).warn("Line id is null for object {}", object);
    		return false;
    	}
    	if (getRows().contains(lineId)) {
    		removeRow(lineId);
    	}
		Row row = getTemplate().addRow(object,req);
    	if (row == null) return false;
		Map<String,Object> map = row.cells;
		writeLock();
		try {
			content.put(lineId, map);
			if (!objects.contains(object))
				objects.add(object);
			columnSizes.putAll(row.columnSizes);
		} finally {
			writeUnlock();
		}
		return true;
    }
    
    public void setCell(String rowId, String colId, Object value) {
    	Object oldContent;
    	writeLock();
    	try {
    		if (!content.keySet().contains(rowId) || !content.get(rowId).containsKey(colId)) return;
    		oldContent = content.get(rowId).put(colId, value);
    	} finally {
    		writeUnlock();
    	}
    	if (oldContent instanceof OgemaWidgetBase<?>) {
    		OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) oldContent;
    		widget.destroyWidget();
    	}
//    	if (value instanceof IOgemaWidgetSimple) {
//    		IOgemaWidgetSimple widget = (IOgemaWidgetSimple) value;
//    		super.registerNewWidget(widget.getId());
//    		//widgetService.registerWidget(widget);
//    	}
    }
    
    // delete all content
    public void clear() {
    	List<Map<String, Object>> contentCopy;
    	writeLock();
    	try {
	    	contentCopy = new ArrayList<>(content.values());
	    	content.clear();
	    	objects.clear();
    	} finally {
    		writeUnlock();
    	}
    	
    	// System.out.println("  Clearing table content " + content );
    	for(Map<String,Object> map: contentCopy) {
    		for(Object obj: map.values()) {
	    		if(obj instanceof OgemaWidgetBase<?>) {
	    			OgemaWidgetBase<?> widget = (OgemaWidgetBase<?>) obj;
	    			widget.destroyWidget();
	    		}
    		}
    	}
    }
	
	/**
	 * @param 1 <= size <= 12 (Bootstrap grid system); if this is not set for a given column, a default value is used
	 */
	public void setColumnSize(String columnId, int size) {
		if (size < 1 || size > 12) throw new IllegalArgumentException("Column size must be an integer between 1 and 12.");
		writeLock();
		try {
			columnSizes.put(columnId, size);
		} finally {
			writeUnlock();
		}
	}
	
	void setColumnSizes(Map<String,Integer> cols) {
		writeLock();
		try {
			columnSizes.putAll(cols);
		} finally {
			writeUnlock();
		}
	}
	
	/**
	 * 
	 * @return -1 if column size has not been explicitly set (a default value is used in this case, but this is not returned here)
	 */
	public int getColumnSize(String columnId) {
		readLock();
		try {
			if (!columnSizes.keySet().contains(columnId)) return -1;
			return columnSizes.get(columnId);
		} finally {
			readUnlock();
		}
	}
	
    /**
     * Use {@link OgemaWidget<?>#addStyle(WidgetStyle)} or {@link OgemaWidget<?>#setStyle(WidgetStyle)} instead
     */
	@Deprecated
	public void setTableClasses(String classes) {
		tableClasses = classes;
	}
	
	@Deprecated
	public String getTableClasses() {
		return tableClasses;
	}
	
	public Comparator<String> getRowIdComparator() {
		readLock();
		try {
			return rowIdComparator;
		} finally {
			readUnlock();
		}
	}

	public void setRowIdComparator(Comparator<String> rowIdComparator) {
		writeLock(); 
		try {
			this.rowIdComparator = rowIdComparator;
		} finally {
			writeUnlock();
		}
	}
	
	public void setHeaderColor(String rgb) {
		// note: the selector >table selects only the direct child -> this does not affect nested tables
		Map<String, String> value = getCssMap().get("table>tbody>tr:first-child");
		if (value == null)
			value = new HashMap<String, String>();
		rgb = checkColorString(rgb);
		value.put("background-color", rgb);
		addCssItem("table>tbody>tr:first-child", value);
	}
	
	public void setHeaderFontColor(String rgb) {
		Map<String, String> value = getCssMap().get("table>tbody>tr:first-child");
		if (value == null)
			value = new HashMap<String, String>();
		rgb = checkColorString(rgb);
		value.put("color", rgb);
		addCssItem("table>tbody>tr:first-child", value);
	}
	
	/**
	 * 
	 * @param widgetID
	 * @param alignment: left, right, center
	 * Deprecated: use {@link #setStyle(WidgetStyle)} instead, with arguments {@link #CELL_ALIGNMENT_CENTERED}, {@link #CELL_ALIGNMENT_LEFT}, and {@link #CELL_ALIGNMENT_RIGHT}.
	 */
//	@Deprecated
//	public void setWidgetAlignment(String widgetID, String alignment) {
//		widgetAlignment.put(widgetID, alignment);
//	}

/*
   // use css file instead ("#TABLE_ID tbody tr:nth-child(4) {your-style}") instead, or th:first-child
   public void setRowClass(String row, String css) {   
		rowClasses.put(row, css);
	}
	
	public String getRowClass(String row) {
		return rowClasses.get(row);
	} */

  
	/***************** private and internal public methods ****************/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
//		System.out.println("ComplexTableWidget " + this.getId() + " received GET message.");
		// FIXME the write lock is currently required because we are setting a style element in this method... 
		// if we use a read lock it will deadlock
		writeLock();
		try {
			List<String> keys = new ArrayList<String>(content.keySet());
			if (rowIdComparator != null)
				Collections.sort(keys,rowIdComparator);
			JSONObject html = new JSONObject();
			JSONArray rows = new JSONArray();
			JSONArray cols = new JSONArray();
	//		int colcnt = -1;
			int rowcnt = -1;
	//		System.out.println("In DynamicTable row-keys:"+keys.size());
			if (keys.isEmpty()) {
	//			return html;
			}
			Iterator<String> rwIt = keys.iterator();
			while (rwIt.hasNext()) {
				rowcnt++;
				String row = rwIt.next();
				rows.put(row);
				Map<String,Object> rowContent = content.get(row);
				Set<String> columns = rowContent.keySet();
				Iterator<String> colIt = columns.iterator();
				JSONObject rowObj = new JSONObject();
				while (colIt.hasNext()) {
	//				colcnt++;
					String col = colIt.next();
					if (rowcnt == 0) {
						cols.put(col);
					}
					Object cellContent = rowContent.get(col);
					if (cellContent == null) {
						rowObj.put(col, "");
					}
					else if (cellContent instanceof OgemaWidgetBase<?>) {
						rowObj.put(col, ((OgemaWidgetBase<?>) cellContent).getTag());
					}
					else 
						rowObj.put(col, "<p>" + StringEscapeUtils.escapeHtml4(cellContent.toString()) + "</p>"); 
//					else if (isSimple(cellContent)) {
//						rowObj.put(col, "<p class=\"text-center\">" + String.valueOf(cellContent) + "</p>"); 
//					}
//					else {
//						rowObj.put(col, "");
//					}		
				}
				html.put(row, rowObj);
			}
			JSONObject options = new JSONObject();
	//		options.put("rowClass", rowClasses);
			JSONObject result = new JSONObject();
			result.put("html", html);
			JSONObject colClass = new JSONObject();
			Set<String> columns = getColumns();
			Iterator<String> it = columns.iterator();
	//		int colSize = (int) Math.max(1,Math.floor(12 / columns.size()));
			Set<String> sizeKeys = columnSizes.keySet();
			int defaultColSize = getUnsetColSize();
			Map<String,List<String>> sizesMap = new HashMap<String, List<String>>();
			while (it.hasNext()) {
				String col = it.next();
				int colSize = defaultColSize;
				if (sizeKeys.contains(col)) {
					colSize = columnSizes.get(col);
				}
				colClass.put(col, "col-sm-" + String.valueOf(colSize));  
				sizesMap.put("row #" + col, Arrays.asList("col","col-sm-" + String.valueOf(colSize)));
			}
			WidgetStyle<DynamicTable<?>> st = new WidgetStyle<DynamicTable<?>>(sizesMap,1);
			// preferred methpd
			addStyle(st);
			// deprecated method: use special options object
			options.put("colClass", colClass);
			if (tableClasses != null && !tableClasses.isEmpty()) {
				options.put("tableClasses", tableClasses);
			}
			result.put("options",options);
			result.put("rows", rows);  // required to keep track of sorting
			result.put("cols", cols);
			return result;
		} finally {
			writeUnlock();
		}
	}
	
	public List<T> getItems() {
		readLock();
		try {
			return new ArrayList<>(objects);
		} finally {
			readUnlock();
		}
	}
	
/*	@Override
	public Class<? extends OgemaWidget> getWidgetClass() {
		return ComplexTable.class;
	} */

//	private static String getWidgetHTML(OgemaWidgetBase<?> widget) {
//			return "<div class=\"ogema-widget\" id=\"" + widget.getId() + "\"></div>";
//	}
	
//	private static final boolean isSimple(Object obj) {
//		if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Short || obj instanceof Long || obj instanceof Double || obj instanceof Boolean) return true;
//		if (obj.getClass().isPrimitive()) return true;		
//		return false;
//	}
	
	private List<OgemaWidgetBase<?>> getWidgets(String rowId) {
		List<OgemaWidgetBase<?>> list = new LinkedList<OgemaWidgetBase<?>>();
		readLock();
		try {
			Map<String,Object> map = content.get(rowId);
			if (map == null) return list;		
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				Object obj = map.get(key);
				if (obj instanceof OgemaWidgetBase<?>) {
					list.add((OgemaWidgetBase<?>) obj);
				}
			}	
		} finally {
			readUnlock();
		}
		return list;
	}
	
	private int getTotalColumnSize() {
		readLock();
		try {
			Collection<Integer> values = columnSizes.values();
			Iterator<Integer> it = values.iterator();
			int total = 0;
			while (it.hasNext()) total = total + it.next().intValue();
			return total;
		} finally {
			readUnlock();
		}
	}
	
	private int getNrOfUnsetColumnSizes() {
		readLock();
		try {
			Set<String> colkeys = columnSizes.keySet();
			Set<String> allkeys = getColumns();
			Iterator<String> it = allkeys.iterator();
			int result = 0;
			while(it.hasNext()) {
				String key = it.next();
				if (!colkeys.contains(key)) result++;
			}
			return result;
		} finally {
			readUnlock();
		}
	}
	
	private int getUnsetColSize() {
		int totalSizeSet;
		int spaceLeft;
		int unsetCols;
		readLock();
		try {
			totalSizeSet = getTotalColumnSize() % 12;
			spaceLeft = 12 - totalSizeSet;
			unsetCols = getNrOfUnsetColumnSizes();
		} finally {
			readUnlock();
		}
		if (unsetCols == 0) return 0;
		if (unsetCols >= spaceLeft) return 1;
		return (int) Math.floor(spaceLeft/unsetCols);
	}
	
	private static String checkColorString(String rgb) {
		if (rgb.startsWith("#"))
			return rgb;
		int sz = rgb.length();
		if ((sz != 3 && sz != 6) || rgb.equals("red") || rgb.equals("yellow"))
			return rgb;
		return "#" + rgb;
	}
	
	@SuppressWarnings("unchecked")
	protected RowTemplate<T> getTemplate() {
		return ((DynamicTable<T>) widget).rowTemplate;
	}
}
