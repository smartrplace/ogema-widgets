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
package de.iwes.widgets.html.datatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.datatable.DataTableData.SelectType;

/**
 * A table containing static data (text). For tables that can change or contain other widgets
 * use DynamicTable instead. This widget provides paging, ordering, and search possibility, that is
 * not available in DynamicTable at the moment. 
 */
public class DataTable extends OgemaWidgetBase<DataTableData>  {
	
//	public static final TriggeringAction ROW_SELECTED = new TriggeringAction("row_selected");
	
    private static final long serialVersionUID = 550713654103033621L;
	private List<Map<Integer, Boolean>> defaultSortInfo = null;

    
    /************* constructor **********************/

    public DataTable(WidgetPage<?> page, String id) {
    	super(page, id);
    }
    
    public DataTable(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, globalWidget);
    }
    
    public DataTable(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent, id, req);
    }
    
    @Override
    protected void registerJsDependencies() {
    	super.registerLibrary(true, "jQuery.fn.dataTable", "/ogema/widget/datatable/lib/datatables.js");
    	super.registerJsDependencies();
    }
    
    /******* Inherited methods ******/
    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return DataTable.class;
    }

	@Override
	public DataTableData createNewSession() {
		return new DataTableData(this);
	}
	
	@Override
	protected void setDefaultValues(DataTableData opt) {
		super.setDefaultValues(opt);
		if (defaultSortInfo != null) {
			defaultSortInfo.stream()
				.forEach(map -> map.entrySet().stream().forEach(entry -> opt.sort(entry.getKey(), entry.getValue())));
		}
	}
	
  	/******** public methods ***********/

    public void addRow(String rowId, Map<String,String> row, OgemaHttpRequest req) {
    	getData(req).addRow(rowId, row);
    }
    
    public void addRows(Map<String, Map<String,String>> rows, OgemaHttpRequest req) {
    	getData(req).addRows(rows);
    }
    
    public void clear(OgemaHttpRequest req) {
    	getData(req).clear();
    }
    
//    public void removeRow(String rowId, OgemaHttpRequest req) {
//    	getOptions(req).removeRow(rowId);
//    }
    
    public void setColumnTitles(Map<String,String> columns, OgemaHttpRequest req) {
    	getData(req).setColumnTitles(columns);
    }
    
    public void addColumn(String id,String title, OgemaHttpRequest req) {
    	getData(req).addColumn(id, title);
    }
    
    
	public boolean isPaging(OgemaHttpRequest req) {
		return getData(req).isPaging();
	}

	public void setPaging(boolean paging, OgemaHttpRequest req) {
		getData(req).setPaging(paging);
	}

	public SelectType getSelect(OgemaHttpRequest req) {
		return getData(req).getSelect();
	}

	public void setSelect(SelectType select, OgemaHttpRequest req) {
		getData(req).setSelect(select);;
	}

	public String getSelectedRow(OgemaHttpRequest req) {
		return getData(req).getSelectedRow();
	}
	
	/**
	 * Set ordering of columns
	 * @param columnIdx
	 * @param ascendingOrDescending
	 * @param req
	 */
	public void sort(int columnIdx, boolean ascendingOrDescending, OgemaHttpRequest req) {
		getData(req).sort(columnIdx, ascendingOrDescending);
	}
	
	/**
	 * Set default ordering of columns
	 * @param columnIdx
	 * @param ascendingOrDescending
	 * @param req
	 */
	public void sortDefault(int columnIdx, boolean ascendingOrDescending) {
		if (defaultSortInfo == null)
			defaultSortInfo = new ArrayList<>(4);
		defaultSortInfo.add(Collections.singletonMap(columnIdx, ascendingOrDescending));
	}

}
