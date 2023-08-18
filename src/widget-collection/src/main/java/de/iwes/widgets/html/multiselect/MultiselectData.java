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
package de.iwes.widgets.html.multiselect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;

public class MultiselectData extends WidgetData {
	
	// TODO add predefined styles
	
    protected final Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
    private String width = null;
    protected String urlParam = null;
    protected boolean isDefaultSelected = false;  // only relevant if urlParam is set
    
	/*********** Constructor **********/
	
	public MultiselectData(Multiselect multiselect) {
		super(multiselect);
	}
	
	/******* Inherited methods ******/
	
	@Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        readLock();
        try {
	        for (DropdownOption o : options) {
	            array.put(o.getJSON(req.getLocale()));
	        }
	        if (width != null) {
	        	result.put("width", width);
	        }
	        if (urlParam != null) {
	        	result.put("syncParam", urlParam);
	        	if (this.isDefaultSelected) {
	        		result.put("defaultSelected", true);
	        		this.isDefaultSelected = false;
	        	}
	        }
        } finally {
        	readUnlock();
        }
        result.put("options", array);
        return result;
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) {
        JSONObject obj = new JSONObject(json);
        if (obj.has("data") ) {
        	JSONArray arr = obj.getJSONArray("data");
        	List<String> selectedOptions = new ArrayList<String>();
        	for (int i=0;i<arr.length();i++) {
				String value = arr.getString(i);
        		selectedOptions.add(unescapeHtmlAttributeValue(value));
        	}
        	selectMultipleOptions(selectedOptions);
//        	if (selectedOptions.isEmpty()) return obj.toString();
        }
        return obj;
    }
    
    @Override
    protected String getWidthSelector() {
    	return ">div>div";
    }
	
	/********** Public methods **********/
    
    public List<DropdownOption> getOptions() {
    	readLock();
    	try {
    		return new LinkedList<DropdownOption>(options);
    	} finally {
    		readUnlock();
    	}
    }
    
    protected boolean isEmpty() {
    	readLock();
    	try {
    		return options.isEmpty();
    	} finally {
    		readUnlock();
    	}
    }

    public void setOptions(Collection<DropdownOption> options) {
    	writeLock();
    	try {
	    	this.options.clear();
	    	for (DropdownOption opt: options) {
	    		try {
					this.options.add(opt.clone());
				} catch (CloneNotSupportedException e) { /* does not occur */}
	    	}
	        this.options.addAll(options);
    	} finally {
    		writeUnlock();
    	}
    }

    public void addOption(String label, String value, boolean selected) {
    	writeLock();
    	try {
	    	removeOption(value);
	        options.add(new DropdownOption(label, value, selected));
    	} finally {
    		writeUnlock();
    	}
    }
    
    public DropdownOption getOption(String value) {
    	readLock();
    	try {
	    	for (DropdownOption opt : options) {
	    		if (opt.id().equals(value))
	    			return opt;
	    	}
    	} finally {
    		readUnlock();
    	}
    	return null;    	
    }
    
    public Collection<DropdownOption> getSelected() {
    	List<DropdownOption> list = new ArrayList<DropdownOption>();
    	readLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption op = it.next();
	    		if (op.isSelected())
	    			list.add(op);
	    	}
    	} finally {
    		readUnlock();
    	}
    	return list;
    }
    
    public Collection<String> getSelectedValues() {
    	List<String> list = new ArrayList<String>();
    	readLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption op = it.next();
	    		if (op.isSelected())
	    			list.add(op.id());
	    	}
    	} finally {
    		readUnlock();
    	}
    	return list;
    }
    
    @Deprecated
    public Collection<String> getSelectedLabels() {
    	List<String> list = new ArrayList<String>();
    	readLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption op = it.next();
	    		if (op.isSelected())
	    			list.add(op.getLabel());
	    	}
    	} finally {
    		readUnlock();
    	}
    	return list;
    }
    
    public Collection<String> getSelectedLabels(final OgemaLocale locale) {
    	List<String> list = new ArrayList<String>();
    	readLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption op = it.next();
	    		if (op.isSelected())
	    			list.add(op.label(locale));
	    	}
    	} finally {
    		readUnlock();
    	}
    	return list;
    }

    public void selectSingleOption(String value) {
    	writeLock();
    	try {
	        for (DropdownOption o : options) {
	        	if (o.id().equals(value))
	        		o.select(true);
	        	break;
	        }
    	} finally {
    		writeUnlock();
    	}
    }
    
    public void defaultSelected() {
    	this.isDefaultSelected = true;
    }
    
    public void selectMultipleOptions(Collection<String> selectedOptions) {
    	writeLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption opt = it.next();
	    		opt.select(selectedOptions.contains(opt.id()));
	    	}
    	} finally {
    		writeUnlock();
    	}
    }

    public void changeSelection(String value, boolean newState) {
    	writeLock();
    	try {
	        for (DropdownOption o : options) {
	            if (o.id().equals(value)) {
	                o.select(newState);
	                return;
	            }
	        }
    	} finally {
    		writeUnlock();
    	}
    }

    public void removeOption(String value) {
    	writeLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	        	DropdownOption o = it.next();
	            if (o.id().equals(value)) {
	                it.remove();
	                return;
	            }
	        }
    	} finally {
    		writeUnlock();
    	}
    }
    
    /*
	public String getWidth() {
		readLock();
		try {
			return width;
		} finally {
			readUnlock();
		}
	}

	public void setWidth(String width) {
		writeLock();
		try {
			this.width = width;
		} finally {
			writeUnlock();
		}
	}
	*/
	
	   /**
     * @param values
     * 		Map&lt;value,label&gt;
     */
    public void update(Map<String,String> values) {
    	writeLock();
    	try {
	    	List<DropdownOption> opts = getOptions();
	    	Iterator<DropdownOption> it = opts.iterator();
	    	while (it.hasNext()) {	
	    		DropdownOption opt = it.next();
	    		if (!values.keySet().contains(opt.id())) 
	    			removeOption(opt.id());
	    	}
	    	for (Map.Entry<String, String> entry: values.entrySet()) {
	    		String newVal = entry.getKey();
	    		boolean found = false; 
	    		for (DropdownOption opt : opts) {
	    			if (opt.id().equals(newVal)) {
	    				found = true;
	    				break;
	    			}
	    		}
	    		if (!found) {
	    			addOption(newVal, entry.getValue(), false);
	    		}
	    	}
    	} finally {
    		writeUnlock();
    	}
    	
    }

    public void clear() {
    	writeLock();
    	try {
    		options.clear();    		
    	} finally {
    		writeUnlock();
    	}
    	
    }
    
	public void setSelectByUrlParam(String param) {
		this.urlParam = param;
	}

	public String getSelectByUrlParam() {
		return this.urlParam;
	}

}
