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

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A table whose rows are modeled on some Java class <code>T</code>. 
 * A {@link RowTemplate} defines how the rows look like. The cells of the table
 * may contain other widgets.<br>
 * The table is dynamic in the sense that rows can be added and removed.
 * If only a static table with fixed text is needed, consider using the 
 * simpler org.ogema.gui.api.widgets.html.StaticTable instead.
 *  
 * @param <T>
 */
public class DynamicTable<T> extends OgemaWidgetBase<DynamicTableData<T>> implements TemplateWidget<T> {
	
	protected RowTemplate<T> rowTemplate;
	protected Map<String,Object> header = null;
	protected String defaultHeaderColor = null;
	protected String defaultHeaderFontColor = null;
	private static final long serialVersionUID = 55043765433620L;
	protected Map<String,Integer> defaultColumnSizes = null; 
	protected Comparator<String> defaultRowIdComparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			if (o1 == null) {
				if (o2 != null) return 1;
				else return 0;
			} 
			else if (o2 == null) return -1;
			boolean head1 = o1.equals(HEADER_ROW_ID);
			boolean head2 = o2.equals(HEADER_ROW_ID);
			if (head1 != head2) {
				if (head1) 
					return -1;
				else
					return 1;
			}
			return o1.compareTo(o2);
		}
	};

	
	public static final String HEADER_ROW_ID = "__headerRow__";
    
	/************************** constructors ***********************/

	/** Default: session-dependent table */
	public DynamicTable(WidgetPage<?> page, String id) {
		this(page, id, false);
	}
	
	/** set globalTable = true for a global (session-independent) table */
	public DynamicTable(WidgetPage<?> page, String id, boolean globalTable) {
		super(page, id, globalTable);
        addDefaultStyle(DynamicTableData.TABLE_PURE); 
        setDynamicWidget(true);
	}
	
	public DynamicTable(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
        addDefaultStyle(DynamicTableData.TABLE_PURE); 
        setDynamicWidget(true);
	}
 
	/************************** inherited methods ***********************/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return (Class) DynamicTable.class;
	} 

	@Override
	public DynamicTableData<T> createNewSession() {
		return new DynamicTableData<T>(this);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "angular", "/ogema/jslib/angular/angular-1.6.4.min.js");
		this.registerLibrary(true, "DynamicTable", "/ogema/widget/complextable/DynamicTable.js");
	}

	@Override
	protected void setDefaultValues(DynamicTableData<T> opt) {
		super.setDefaultValues(opt);
		if (header != null) {
			opt.addRow(HEADER_ROW_ID, header);
		}
		if (defaultColumnSizes != null) {
			opt.setColumnSizes(defaultColumnSizes);
		}
		opt.setRowIdComparator(defaultRowIdComparator);
		if (defaultHeaderColor != null) {
			opt.setHeaderColor(defaultHeaderColor);
		}
		if (defaultHeaderFontColor != null) {
			opt.setHeaderFontColor(defaultHeaderFontColor);
		}
	}
	
	/************************** public methods ***********************/
	

	public RowTemplate<T> getRowTemplate() {
		return rowTemplate;
	}

	public void setRowTemplate(RowTemplate<T> rowTemplate) {
		this.rowTemplate = rowTemplate;		
		if (rowTemplate == null) return;
		// FIXME header can be dynamic too... need to check for updaets in GET!
		Map<String,Object> header = rowTemplate.getHeader();
		if (header == null || header.isEmpty()) {
			this.header = null;
			return;
		}
		this.header = header;
	 }
	
 	 @Override // make visible 
	 public void setComposite() {
		 super.setComposite();
	 }
 	 
	 @Override // make visible 
	 public void setComposite(long subwidgetPollingRateMs) {
		 super.setComposite(subwidgetPollingRateMs);
	 }
	
	 /**
	  * Use with row template only.
	  * @param req
	  */
	 public void refreshHeader(OgemaHttpRequest req) {
		 getData(req).refreshHeader();
	 }
	
//	public Map<String, Object> getHeader() {
//		return header;
//	}

//	//  Header set via template
//	public void setHeader(Map<String, Object> header) {
//		this.header = header;
//	}
    
    /**
     * return cell entry for cell (rowId,colId), or null if the specified cell does not exist.
     */
    public Object getCellContent(String rowId, String colId, OgemaHttpRequest req) {
    	return getData(req).getCellContent(rowId, colId);
    }
    
    public Set<String> getRows(OgemaHttpRequest req) {
    	return getData(req).getRows();
    }
    
    public Set<String> getColumns(OgemaHttpRequest req) {
    	return getData(req).getColumns();
    }
    
    public Map<String,Map<String,Object>> getTable(OgemaHttpRequest req) {
    	return getData(req).getTable();
    }
    
    public boolean removeRow(String rowId, OgemaHttpRequest req) {
    	return getData(req).removeRow(rowId);
    }
    
    public boolean removeColumn(String colId, OgemaHttpRequest req) {
    	return removeColumn(colId, req);
    }
        
    /**
     * Set a row explicitly. Consider using {@link #addItem(Object, OgemaHttpRequest)} instead,
     * which uses the template.
     * @param lineId
     * @param columns
     * @param req
     */
    public void addRow(String lineId, Map<String,Object> columns, OgemaHttpRequest req) {
    	getData(req).addRow(lineId, columns);	
    }
    
    /**
     * Add a row modeled on the object passed. This requires the row template to be set.
     * @see #setRowTemplate(RowTemplate)
     * @param object
     * @param req
     * @return
     * 		true: row successfully created (even if it existed previously), false: could not be created.
     */
    public boolean addItem(T object, OgemaHttpRequest req) {
    	return getData(req).addItem(object, req);
    }
    
    /**
     * Remove a row modeled on the object passed.
     * @param object
     * @param req
     * @return
     */
    public boolean removeItem(T object, OgemaHttpRequest req) {
    	return getData(req).removeItem(object);
    }
    
    
    /**
     * Remove all rows not contained in newRows, and add those that are new.
     * Does not modify those rows that were available and are also contained in newRows.
     * @param newRows
     * @param req
     */
    public void updateRows(Collection<T> newRows, OgemaHttpRequest req) {
    	getData(req).updateRows(newRows, req);
    }
    
    public void setCell(String rowId, String colId, Object value, OgemaHttpRequest req) {
    	getData(req).setCell(rowId, colId, value);
    }
    
    // delete all content
    public void clear(OgemaHttpRequest req) {
    	getData(req).clear();
    }

	
    public void setDefaultColumnSize(String columnId, int size) {
    	if (size < 1 || size > 12) throw new IllegalArgumentException("Column size must be an integer between 1 and 12.");
    	if (defaultColumnSizes == null)
    		defaultColumnSizes = new HashMap<String, Integer>();
    	defaultColumnSizes.put(columnId, size);
    }
    
	/**
	 * @param columnId 
	 * @param size &lt;= size &lt;= 12 (Bootstrap grid system); if this is not set for a given column, a default value is used
	 * @param req
	 */
	public void setColumnSize(String columnId, int size, OgemaHttpRequest req) {
		getData(req).setColumnSize(columnId, size);
	}
	
	/**
	 * 
	 * @return -1 if column size has not been explicitly set (a default value is used in this case, but this is not returned here)
	 */
	public int getColumnSize(String columnId, OgemaHttpRequest req) {
		return getData(req).getColumnSize(columnId);
	}
	
	public void setDefaultRowIdComparator(Comparator<String> rowIdComparator) {
		this.defaultRowIdComparator = rowIdComparator;
	}
	
	public Comparator<String> getRowIdComparator(OgemaHttpRequest req) {
		return getData(req).getRowIdComparator();
	}

	public void setRowIdComparator(Comparator<String> rowIdComparator, OgemaHttpRequest req) {
		getData(req).setRowIdComparator(rowIdComparator);
	}
	
	/**
	 * Set the background color of the first table row.
	 * @param rgb
	 * 		A color string, such "#FFAA44"
	 */
	public void setDefaultHeaderColor(String rgb) {
		this.defaultHeaderColor = rgb;
	}
	
	/**
	 * Set the color of the first table row.
	 * @param rgb
	 * 		A color string, such "#FFAA44"
	 * @param req
	 */
	public void setHeaderColor(String rgb, OgemaHttpRequest req) {
		getData(req).setHeaderColor(rgb);
	}
	
	/**
	 * Set the text color of the first table row.
	 * @param rgb
	 * 		A color string, such "#FFAA44"
	 */
	public void setDefaultHeaderFontColor(String rgb) {
		this.defaultHeaderFontColor = rgb;
	}
	
	/**
	 * Set the text color of the first table row.
	 * @param rgb
	 * 		A color string, such "#FFAA44"
	 */
	public void setHeaderFontColor(String rgb, OgemaHttpRequest req) {
		getData(req).setHeaderFontColor(rgb);
	}
	
	/**
	 * @param backgroundColor may be null, but then items scrolling below the header remain visible.
	 */
	public void setDefaultStickyHeader(String backgroundColor) {
		addDefaultCssItem(">div>div>table>tbody>tr:first-child", stickyHeader(backgroundColor));
	}
	
	public void setStickyHeader(String backgroundColor, OgemaHttpRequest req) {
		addCssItem(">div>div>table>tbody>tr:first-child", stickyHeader(backgroundColor), req);
	}
	
	private static Map<String, String> stickyHeader(String backgroundColor) {
		final Stream.Builder<Map.Entry<String, String>> builder = Stream.<Map.Entry<String, String>> builder()
			.add(new AbstractMap.SimpleEntry<String, String>("position", "sticky"))
			.add(new AbstractMap.SimpleEntry<String, String>("top", "0px"))
			.add(new AbstractMap.SimpleEntry<String, String>("z-index", "100"));
		if (backgroundColor != null)
			builder.add(new AbstractMap.SimpleEntry<String, String>("background-color", backgroundColor));
		return builder.build().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	/**
	 * Get the items corresponding to the rows of the table 
	 * @param req
	 * @return
	 */
	public List<T> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}

}
