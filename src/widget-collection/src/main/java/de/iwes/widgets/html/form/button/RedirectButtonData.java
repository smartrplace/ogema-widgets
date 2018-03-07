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

package de.iwes.widgets.html.form.button;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
 
public class RedirectButtonData extends ButtonData {
	
	private boolean enabled = true;
	private String url = "#";
	private Map<String,String[]> parameters = null; 
	private boolean openInNewTab = true;
	
	public RedirectButtonData(RedirectButton button) {
		super(button);
	}
	
	@Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
    	JSONObject result = new JSONObject();
    	readLock();
    	try {
	    	if (!enabled) return result;
	   		String urlToSend = attachParams();
	   		if (urlToSend != null)
	    		result.put("url", urlToSend);
	   		result.put("newTab", openInNewTab);
    	} finally {
    		readUnlock();
    	}
    	return result;
    }
	
	public void enable(boolean doEnable) {
		writeLock();
		try {
			this.enabled = doEnable;
		} finally {
			writeUnlock();
		}
	}
	
	public boolean isEnabled() {
		readLock();
		try {
			return enabled;
		} finally {
			readUnlock();
		}
	}
	
	public String getUrl() {
		readLock();
		try {
			return url;
		} finally {
			readUnlock();
		}
	}
	
	public void setUrl(String url) {
		writeLock();
		try {
			this.url = url;
		} finally {
			writeUnlock();
		}
	}
	
	public void addParameter(String key, String... value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		writeLock();
		try {
			if (parameters == null)
				parameters = new LinkedHashMap<>();
			parameters.put(key, value);
		} finally {
			writeUnlock();
		}
	}
	
	public void setParameters(Map<String,String[]> parameters) {
		writeLock();
		try {
			this.parameters = parameters;
		} finally {
			writeUnlock();
		}
	}
	
	public boolean removeParameter(String key) {
		if (key == null)
			return false;
		writeLock();
		try {
			if (parameters == null)
				return false;
			return parameters.remove(key) != null;
		} finally {
			writeUnlock();
		}
	}
	
	public Map<String,String[]> getParameters() {
		readLock();
		try {
			return new LinkedHashMap<>(parameters);
		} finally {
			readUnlock();
		}
	}
	
	private final String attachParams() {
		if (url == null || parameters == null)
			return url;
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		boolean paramsInitialized = url.contains("?");
		for (Map.Entry<String, String[]> entry: parameters.entrySet()) {
			char init = paramsInitialized ? '&' : '?';	
			sb.append(init).append(encode(entry.getKey())).append('=').append(serialize(entry.getValue()));
			paramsInitialized = true;
		}
		return sb.toString();
	}
	
	private static final String serialize(String[] arr) {
		int length = arr.length;
		if (length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<length-1;i++) {
			sb.append(encode(arr[i])).append(',');
		}
		sb.append(arr[length-1]);
		return sb.toString();
	}
	
	private final static String encode(final String in) {
		try {
			return URLEncoder.encode(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Very unexpected encoding exception.",e);
		}
	}

	public boolean isOpenInNewTab() {
		return openInNewTab;
	}

	public void setOpenInNewTab(boolean openInNewTab) {
		this.openInNewTab = openInNewTab;
	}
}

