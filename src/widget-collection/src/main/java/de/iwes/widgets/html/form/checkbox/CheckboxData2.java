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
