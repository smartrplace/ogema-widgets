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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class DataTableData extends WidgetData {
	
	private final Map<String,Map<String,String>> data = new LinkedHashMap<String, Map<String,String>>();
	private final Map<String,String> colIdList = new LinkedHashMap<String, String>();
	private boolean paging = true;
	private SelectType select = SelectType.SINGLE;
	private String selectedRow = null; // rowId
	
	public enum SelectType {
		OFF, SINGLE, OS, MULTI;
		
		public JSONObject getJson() {
			JSONObject obj = new JSONObject();
			String value;
			switch(this) {
			case OFF:
				value = "api";
				break;
			case SINGLE:
				value = "single";
				break;
			case OS:
				value = "os";
				break;
			case MULTI:
				value = "multi";
				break;
			default:
				return null;
			}
			obj.put("style", value);
			return obj;
		}
		
	}
	
	// TODO add header and footer, options for them, ...
    
    /************* constructor **********************/

    public DataTableData(DataTable table) {
    	super(table);
    }
        
    /******* Inherited methods ******/
	 
    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {  
        JSONObject result = new JSONObject();        
//        result.put("data", data.values());
        result.put("data", getData());
        result.put("columns", getColMap());
        result.put("select", select.getJson());
        return result;
    }

   @Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
	   JSONObject obj  = new JSONObject(data);
	   JSONArray array = obj.getJSONObject("data").getJSONArray("currentRows");
	   if (array == null || array.length() == 0) {
		   selectedRow = null;
	   }
	   else {
		   selectedRow = array.getJSONObject(0).getString("__id__");
	   }
	   return obj;
	}
   
   	/******** public methods ***********/

    public void addRow(String rowId, Map<String,String> row) {
    	data.put(rowId, row);
    }
    
    public void addRows(Map<String, Map<String,String>> rows) {
    	data.putAll(rows);
    }
    
    public void clear() {
    	data.clear();
    	colIdList.clear();
    }
    
    public void setColumnTitles(Map<String,String> columns) {
    	colIdList.clear();
    	colIdList.putAll(columns);
    }
    
    public void addColumn(String id,String title) {
    	colIdList.put(id,title);
    }
    
	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public  SelectType getSelect() {
		return select;
	}

	public void setSelect(SelectType select) {
		this.select = select;
	}
	
	public String getSelectedRow() {
		return selectedRow;
	}
	
	/******** private methods ***********/

    private List<Map<String,String>> getColMap() {
    	List<Map<String,String>> result = new LinkedList<Map<String,String>>();
    	Iterator<Map.Entry<String, String>> it = colIdList.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<String, String> entry = it.next();
    		Map<String,String> auxMap = new HashMap<String, String>();
    		auxMap.put("data", entry.getKey()); // column id
    		auxMap.put("sTitle", entry.getValue()); // column title for display
    		result.add(auxMap);
    	}
    	return result;
    }
	
    private Collection<Map<String, String>> getData() { // basically one can return data.values() here, except that the id of the rows should be transmitted as well
    	List<Map<String, String>> list = new LinkedList<Map<String,String>>();
    	Iterator<Map.Entry<String, Map<String,String>>> it = data.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry<String, Map<String,String>> entry = it.next();
    		Map<String,String> map = new HashMap<String, String>(entry.getValue());
    		map.put("__id__", entry.getKey());
    		list.add(map);
    	}
    	return list;
    }
}
