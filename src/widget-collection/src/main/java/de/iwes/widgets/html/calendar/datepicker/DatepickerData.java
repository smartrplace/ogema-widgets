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
package de.iwes.widgets.html.calendar.datepicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class DatepickerData extends WidgetData {
/*
    if (locale != null && !locale.isEmpty()) {
        defaultAttributes.put("locale", locale);
    }
    if (format != null && !format.isEmpty()) {
        defaultAttributes.put("format", format);
    }
    if (defaultDate != null && !defaultDate.isEmpty()) {
        defaultAttributes.put("defaultDate", defaultDate);
    }
    if (viewMode != null && !viewMode.isEmpty()) {
        defaultAttributes.put("viewMode", viewMode);
    }
    globalProperties.put("daysOfWeekDisabled", daysOfWeekDisabled);
*/   
	
	// Javascript format
	public static final String DEFAULT_FORMAT = "YYYY-MM-DD HH:mm:ss";
    private String locale  = null;
	private String format = null;
	private String viewMode = null;
    private String date = getDateFormat().format(new Date());
    final Set<Integer> daysOfWeekDisabled = new HashSet<Integer>();
//    private JSONObject globalProperties = new JSONObject();


	/*********** Constructor **********/

    public DatepickerData(Datepicker datepicker) {
        super(datepicker);
    }
    
    /******* Inherited methods ******/


    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
//        JSONObject result = globalProperties;   
    	JSONObject result = new JSONObject();
    	if (date == null) {
    		disable();
    	} else {
    		enable();
	    	result.put("daysOfWeekDisabled", daysOfWeekDisabled);
	    	JSONObject attributes = new JSONObject();
	    	attributes.put("locale", locale); 
	    	attributes.put("format", format);
	    	attributes.put("defaultDate", date);
	    	attributes.put("viewMode", viewMode);
	    	result.put("attributes", attributes);
    	}
        return result;
    }
    
    @Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
    	JSONObject request = new JSONObject(data);
        date = request.getString("data");
//        System.out.println("data: " + value);
        return request;
    }
        
    /********** Public methods **********/

    
    public String getDate(){
        return date;
    }
    public void setDate(String date) {
    	this.date = date;
    }
    
    public void setDate(long dateMillis) {
    	Date date = new Date(dateMillis);
    	DateFormat formatter = getDateFormat();
    	String strDate = formatter.format(date);
     	setDate(strDate);
    }
    
    public Date getDateObject() {
    	String dt = getDate();
    	if (dt == null || dt.isEmpty())
    		return null;
    	DateFormat formatter = getDateFormat();
    	try {
    		return formatter.parse(dt);
    	} catch (ParseException e) {
    		throw new RuntimeException("Error parsing date " + dt,e);
    	} catch(Exception e) {
    		throw new RuntimeException("Unexpected Java Classpath Error parsing date " + dt,e);
    	}
    }
    
    public long getDateLong() {
    	Date date = getDateObject();
    	if (date == null)
    		return Long.MIN_VALUE;
    	return date.getTime();
    }
    
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public Set<Integer> getDaysOfWeekDisabled() {
		return new HashSet<Integer>(daysOfWeekDisabled);
	}
	
	public void addDayOfWeekDisabled(int day) {
		daysOfWeekDisabled.add(day);
	}
    
    /********** Internal methods *********/
	
	private DateFormat getDateFormat() {
    	DateFormat formatter;
    	if (format!= null)
    		formatter = new SimpleDateFormat(format.replaceAll("Y", "y").replaceAll("D", "d")); // different capitalization rules in Javascript and Java
    	else // default
    		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	return formatter;
	}
    
//    protected JSONObject getGlobalProperties() {
//		return globalProperties;
//	}
//
//	protected void setGlobalProperties(JSONObject globalProperties) {
//		this.globalProperties = globalProperties;
//	}
}
