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

package de.iwes.widgets.html.datatable;

import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.datatable.DataTableData.SelectType;

/**
 * A table containing static data (text). For tables that can change or contain other widgets
 * use {@see DynamicTable} instead. This widget provides paging, ordering, and search possibility, that is
 * not available in DynamicTable at the moment. 
 */
public class DataTable extends OgemaWidgetBase<DataTableData>  {
	
//	public static final TriggeringAction ROW_SELECTED = new TriggeringAction("row_selected");
	
    private static final long serialVersionUID = 550713654103033621L;
    
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
	
//	@Override
//	protected void setDefaultValues(DataTableOptions opt) {
//		super.setDefaultValues(opt);
//	}
    
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

}
