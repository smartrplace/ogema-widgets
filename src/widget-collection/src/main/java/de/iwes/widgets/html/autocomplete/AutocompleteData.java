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
package de.iwes.widgets.html.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class AutocompleteData extends WidgetData {
    
	private String value = null;
	private String placeholder;
	private int minLength = 1;
	private final List<String> options = new ArrayList<String>(); // sync?
	
	/*********** Constructor **********/
	
	public AutocompleteData(Autocomplete autocomplete) {
		super(autocomplete);
	}
	
	/******* Inherited methods ******/
	

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        readLock();
        try {
	        result.put("states", options);
	        result.put("minLength", minLength);
	        if (value !=null && !value.isEmpty()) {
	        	result.put("value", value);
	        } else {
	        	if (placeholder == null)
	        		placeholder = "";
	        	result.put("placeholder", placeholder);
	        }
        } finally {
        	readUnlock();
        }
        return result;
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) {
    	JSONObject request = new JSONObject(json);
		String result;
		try {
			result = request.getString("data");
		} catch (Exception e) {
			result = "SyntaxError; POST request in wrong format";
		}
		setValue(result);
    	return request;
    }

	
	/********** Public methods **********/
	
	public String getValue() {
		readLock();
		try {
			return value;
		} finally {
			readUnlock();
		}
	}
	protected void setValue(String value) {
		writeLock();
		try {
			this.value = value;
		} finally {
			writeUnlock();
		}
	}
	public String getPlaceholder() {
		readLock();
		try {
			return placeholder;
		} finally {
			readUnlock();
		}
	}
	public void setPlaceholder(String placeholder) {
		writeLock();
		try {
			this.placeholder = placeholder;
		} finally {
			writeUnlock();
		}
	}
	
	public void setOptions(List<String> options) {
		writeLock();
		try {
			this.options.clear();
			if (options == null)
				return;
			this.options.addAll(options);
		} finally {
			writeUnlock();
		}
	}

	public void addOption(String option) {
		writeLock();
		try {
			options.add(option);
		} finally {
			writeUnlock();
		}
	}
	
	public void removeOption(String option) {
		writeLock();
		try {
			options.remove(option);
		} finally {
			writeUnlock();
		}
	}
	
	public List<String> getOptions() {
		readLock();
		try {
			return new ArrayList<String>(options);
		} finally {
			readUnlock();
		}
	}
	
	public int getMinLength() {
		readLock();
		try {
			return minLength;
		} finally {
			readUnlock();
		}
	}

	public void setMinLength(int minLength) {
		writeLock();
		try {
			if (minLength < 0)
				throw new IllegalArgumentException("minLength must be non-negative; got " + minLength);
			this.minLength = minLength;
		} finally {
			writeUnlock();
		}
	}
	
	public void clear() {
		writeLock();
		try {
			value = null;
		} finally {
			writeUnlock();
		}
	}

	
}
