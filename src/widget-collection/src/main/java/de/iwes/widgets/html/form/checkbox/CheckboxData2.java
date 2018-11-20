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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class CheckboxData2 extends WidgetData {

    protected final List<CheckboxEntry> checkboxList = new ArrayList<>(3);

	/************* constructor **********************/

    public CheckboxData2(Checkbox2 checkbox) {
    	super(checkbox);
    } 

    /******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        final OgemaLocale locale = req.getLocale();
        final JSONArray arr = new JSONArray(checkboxList.stream()
        	.map(entry -> entry.toJson(locale))
        	.collect(Collectors.toList()));
        result.put("items", arr);
        return result;
    }

    @Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
        JSONObject request = new JSONObject(data);
        for (String entry : request.getString("data").split("&")) {
        	if (entry.indexOf('=') == -1)
        		continue;
            final String key = entry.split("=")[0];
            final String value = entry.split("=")[1];
            final CheckboxEntry check = getEntry(key);
            if (check != null)
            	check.setState(Boolean.valueOf(value));
        }
        return request;
    }

    private CheckboxEntry getEntry(final String id) {
    	if (id == null)
    		return null;
    	return checkboxList.stream().filter(e -> e.id().equals(id)).findAny().orElse(null);
    }
    
    /******* Public methods ******/
    
    protected List<String> getCheckboxIds() {
    	return checkboxList.stream().map(e -> e.id()).collect(Collectors.toList());
    }
    
    protected List<CheckboxEntry> getCheckboxList() {
        return checkboxList.stream().map(e -> e.clone()).collect(Collectors.toList());
    }
    
    protected void setCheckboxList(Collection<CheckboxEntry> newList) {
        if (newList == null) 
        	newList = Collections.emptySet();
        this.checkboxList.clear();
        newList.stream().forEach(e -> checkboxList.add(e.clone()));
    }
    
    protected void addEntry(final CheckboxEntry entry) {
    	Objects.requireNonNull(entry);
    	checkboxList.add(entry.clone());
    }
    
    protected boolean removeEntry(final String id) {
    	final CheckboxEntry e= getEntry(id);
    	if (e == null)
    		return false;
    	return checkboxList.remove(e);
    }
    
    protected void deselectAll() {
    	checkboxList.forEach(entry -> entry.setState(false));
    }
    
    protected void selectAll() {
    	checkboxList.forEach(entry -> entry.setState(true));
    }
    
    protected boolean isChecked(final String id) {
    	if (id == null)
    		return false;
    	final CheckboxEntry entry = getEntry(id);
    	if (entry == null)
    		return false;
    	return entry.isChecked();
    }
    
    protected boolean checkSingleValue(final String id) {
    	if (setState(id, true)) {
    		deselectAll();
    		return true;
    	}
    	return false;
    }
   
    protected boolean setState(final String id, final boolean checked) {
    	if (id == null)
    		return false;
    	final CheckboxEntry entry = getEntry(id);
    	if (entry == null)
    		return false;
    	entry.setState(checked);
    	return true;
    }


}
