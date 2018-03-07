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
