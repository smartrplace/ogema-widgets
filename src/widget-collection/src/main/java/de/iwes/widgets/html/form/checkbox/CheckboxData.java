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
