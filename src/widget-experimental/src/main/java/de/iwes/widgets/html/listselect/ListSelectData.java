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

package de.iwes.widgets.html.listselect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ListSelectData extends WidgetData implements Comparator<String> {

    protected Map<String,String[]> arrayValues;    
    protected String[] header;
    // ids
    protected final List<String> selected = new ArrayList<String>(); 
    
    /**
     * default: true
     */
    protected boolean responsive;
    /**
     * modify css style of the page. Default null. 
     * EXAMPLES:
     * 		{\"color\":\"#003399\"\}  // change text color
     * 		{\"color\":\"#003399\"\}  // change text color
     */
    protected String[] css; 

	/************************** constructors ***********************/
    
    public ListSelectData(ListSelect select, String[] header) {
    	super(select);
    	this.header = header;
	}
    
    /******* Inherited methods ******/
    
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		// TODO submit selected rows 
		JSONArray headers = new JSONArray();
		int counter= 0;
		for (String head: this.header) {	
			JSONObject headObj = new JSONObject();
			headObj.put("headerItem",head);
			headObj.put("headerPosition", counter);
			headers.put(headObj);
			counter++;
		}	
//		System.out.println(" Get request being processed. Columns: " + String.valueOf(header.length) + ", rows: " + String.valueOf(arrayValues.size()) );	
		List<String> keys  = new ArrayList<String>(this.arrayValues.keySet());
		Collections.sort(keys, this);
		JSONArray rows = new JSONArray();
		for (String key: keys) {
			String[] strArr = this.arrayValues.get(key);
			JSONObject obj = new JSONObject();
			obj.put("lWidgetId", key);
			int colCounter = 0;
			for (String head: this.header) {	
				if (strArr.length <= colCounter) {
					obj.put(head,"");
				}		
				else {
					obj.put(head,strArr[colCounter]);
				}	
				colCounter++;
			}	
			rows.put(obj);
		}
		JSONObject params = this.getParameters();		
		JSONObject results = new JSONObject();
		results.put("rows", rows);
		results.put("headers",headers);
		results.put("params", params);
		return results;
	}

	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {   
		JSONObject jsonObj = new JSONObject(data);
		selected.clear();
		JSONObject changed = new JSONObject();
		Iterator<?> keys = jsonObj.keys();
		boolean aRowHasChanged = false;
        while( keys.hasNext() ){
            String key = (String) keys.next();
			if (key.equals("changedRowZZ")) {
				changed.put("id",jsonObj.get(key));
				changed.put("value", jsonObj.get((String) jsonObj.get(key))); // boolean value expected
				aRowHasChanged = true;
				continue;
			}
			if (jsonObj.getBoolean(key)) {
				selected.add(key);
			}	
        }
		
		//System.out.println("Selected items: " + selected.toString());
        if (aRowHasChanged)  {
        	this.onChange(jsonObj);
        } 
        return jsonObj;
	}
    
	
	/************************** public methods that can be overwritten ***********************/
	
	/**
	 * do something if a row is de-/selected
	 * @param obj: specifies the row, and whether it has been selected or deselected. Two entries: 'id' (String) and 'selected' (boolean)
	 */
	public String onChange(JSONObject obj) {
		return "clicked on table: " + obj.toString();		
	}
	
	/**
	 * doesn't deviate from the standard sort function, but can be overwritten to change the sorting of rows according to keys in {@link #arrayValues}
	 */
	@Override
	public int compare(String key1, String key2) {
		return key1.compareTo(key2);
	}
	
	
	
	/************************** public methods ***********************/
	
	public List<String> getSelectedIds() {
		return selected;		
	}
	
	public Map<String,String[]> getSelectedItems() {		
		Map<String,String[]> map = new HashMap<String,String[]>();
		for (String key : arrayValues.keySet())   {
			if (selected.contains(key)) {
				map.put(key,arrayValues.get(key));
			}
		}
		return map;		
	}
	
	public Map<String,String[]> getArrayValues() {
		return new HashMap<String,String[]>(arrayValues); // do not return the map itself, so it cannot be modified from outside (it is possible via setArrayValues)
	}
	
	public void addArrayValues(Map<String,String[]> newEntry) {
		this.arrayValues.putAll(newEntry);
	}
	
	public void removeArrayValues(List<String> keys) {
		for (String key : keys) {
			arrayValues.remove(key);
			selected.remove(key);
		}
	}
	
	public void clearValues() {
		this.selected.clear();
		this.arrayValues.clear();
	}

	public void setArrayValues(Map<String,String[]> arrayValues, String[] header) {
		this.arrayValues = arrayValues;
		this.header = header;
		Set<String> allowedValues = arrayValues.keySet();
		for (String key: selected) {
			if (!allowedValues.contains(key)) {
				selected.remove(key);
			}
		}
	}

	public String[] getHeader() {
		return header;
	}

	public boolean isResponsive() {
		return responsive;
	}
	
    public String[] getCss() {
		return css;
	}

    
    @Deprecated
	public void setCss(String[] css) {
		this.css = css;
	}
	
	/***************** private and internal public methods ****************/	

	private JSONObject getParameters() {
		JSONObject params = new JSONObject();
		params.put("responsive", responsive);
		//if (css != null && cssChanged) {
		if (css != null) {
			params.put("css", css);
		}
		return params;
	}

	
}
