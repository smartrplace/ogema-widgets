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

package de.iwes.widgets.html.form.checkbox;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class CheckboxData extends WidgetData {

    protected final Map<String, Boolean> checkboxList = new LinkedHashMap<>();
    private String title;

	/************* constructor **********************/

    public CheckboxData(Checkbox checkbox) {
    	super(checkbox);
    } 

    /******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
//      if(req.widgetObject != null) {
//        	result.put("checkboxList", req.widgetObject);
//        } else {
        	result.put("checkboxList", checkboxList);
//        }
//        if(req.widetValue != null) {
//        	result.put("title", req.widetValue);
//        } else {
        	result.put("title", title);
//        }
        return result;
    }

    @Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
        JSONObject request = new JSONObject(data);
        data = request.getString("data");
        try {
            String[] map = data.split("&");
            checkboxList.clear();
            for (String entry : map) {
                String key = entry.split("=")[0];
                String value = entry.split("=")[1];
                checkboxList.put(key, Boolean.valueOf(value));
//                req.widetValue = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    /******* Public methods ******/
    
    public Map<String, Boolean> getCheckboxList() {
        return new LinkedHashMap<String, Boolean>(checkboxList);
    }
    
    public void setCheckboxList(Map<String, Boolean> newList) {
        if (newList == null) 
        	newList = Collections.emptyMap();
        this.checkboxList.clear();
        this.checkboxList.putAll(newList);
    }
    
    public void deselectAll() {
    	for (String entry : checkboxList.keySet()) 
    		checkboxList.put(entry, false);
    }
    
    public void selectAll() {
    	for (String entry : checkboxList.keySet()) 
    		checkboxList.put(entry, true);
    }
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
