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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class Datepicker extends OgemaWidgetBase<DatepickerData> {

    private static final long serialVersionUID = 550713654103033621L;
//    private final Set<String> defaultDisabledDates = new HashSet<>();
//    private final Set<Integer> defaultDaysOfWeekDisabled = new HashSet<>();
	private String defaultLocale  = null;
	private String defaultFormat = DatepickerData.DEFAULT_FORMAT;
	private String defaultViewMode = null;
    private String defaultDate = null;
    private final Set<Integer> defaultDaysOfWeekDisabled = new HashSet<Integer>();

	/*********** Constructor **********/
    
    public Datepicker(WidgetPage<?> page, String id) {
        super(page, id);
    }
    public Datepicker(WidgetPage<?> page, String id, SendValue sendValuesOnChange) {
        this(page, id);
        setDefaultSendValueOnChange(sendValuesOnChange == SendValue.TRUE);
    }

    /**
     * @param page
     * @param id
     * @param locale de/en/fr/ru/cn
     * @param format YYYY-MM-DD HH:mm:ss
     * @param defaultDate 2/15/2015
     * @param viewMode years/months/days
     * @param daysOfWeekDisabled 0/1/2/3/4/5/6 
     */
    public Datepicker(WidgetPage<?> page, String id, String locale, String format, String defaultDate, String viewMode, Set<Integer> daysOfWeekDisabled) {
        super(page, id);
//        this.defaultDaysOfWeekDisabled.addAll(daysOfWeekDisabled);
         if (locale != null && !locale.isEmpty()) 
            defaultLocale = locale;
         if (format != null && !format.isEmpty()) 
            defaultFormat = format;
         if (defaultDate != null && !defaultDate.isEmpty()) 
           this.defaultDate = defaultDate;
         if (viewMode != null && !viewMode.isEmpty()) 
            this.defaultViewMode = viewMode;
         if (daysOfWeekDisabled != null && !daysOfWeekDisabled.isEmpty()) 
        	this.defaultDaysOfWeekDisabled.addAll(daysOfWeekDisabled);
    }
    
    public Datepicker(OgemaWidget parent, String id, OgemaHttpRequest req) {
        super(parent, id, req);
    }
    
	/******* Inherited methods ******/
    
    @Override
    protected void registerJsDependencies() {
    	registerLibrary(true, "moment", "/ogema/widget/datepicker/lib/moment-with-locales_2.18.1.min.js"); // FIXME global moment variable will be removed in some future version
        registerLibrary(true, "jQuery.fn.datetimepicker", "/ogema/widget/datepicker/lib/bootstrap-datetimepicker_4.17.47.min.js"); 
    	super.registerJsDependencies();
    }

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Datepicker.class;
    }
    
    @Override
	public DatepickerData createNewSession() {
    	return new DatepickerData(this);
    }
    
    @Override
    protected void setDefaultValues(DatepickerData opt) {
/*    	opt.setGlobalProperties(globalProperties); // on GET requests, the elements of globalProperties will be included in the response
    	Iterator<Entry<String, String>> it = defaultAttributes.entrySet().iterator();
    	while (it.hasNext()) {
    		Entry<String, String> entry = it.next();
    		opt.addAttribute(entry.getKey(), entry.getValue());
    	}
*/
    	if (defaultDate != null && !defaultDate.isEmpty())
    		opt.setDate(defaultDate);
    	if (defaultDaysOfWeekDisabled != null && !defaultDaysOfWeekDisabled.isEmpty()) 
    		opt.daysOfWeekDisabled.addAll(getDefaultDaysOfWeekDisabled());
    	if (defaultFormat != null && !defaultFormat.isEmpty())
    		opt.setFormat(defaultFormat);
    	if (defaultLocale != null && !defaultLocale.isEmpty())
    		opt.setLocale(defaultLocale);
    	if (defaultViewMode != null && !defaultViewMode.isEmpty())
    		opt.setViewMode(defaultViewMode);
    	super.setDefaultValues(opt);
//    	if (opt.getDate() == null) 
//    		opt.setDate(System.currentTimeMillis());
    }
    
	/********** Public methods **********/
    
    /** Set date and time of the date picker in the String format specified with the constructor.
     * For the default format see {@link #Datepicker(WidgetPage, String, String, String, String, String, Set)}
     */
    public void setDate(String date, OgemaHttpRequest req) {
    	getData(req).setDate(date);
    }
    /** Set date and time of the date picker 
     * @param dateMillis milliseconds since epoch UTC
     * @param req
     */
    public void setDate(long dateMillis, OgemaHttpRequest req) {
    	getData(req).setDate(dateMillis);
    }
    
    /**Get the date and time selected in the String format specified with the constructor.
     * For the default format see {@link #Datepicker(WidgetPage, String, String, String, String, String, Set)}
     */
    public String getDate(OgemaHttpRequest req){
        return getData(req).getDate();
    }
    
    /**
     * 
     * @param req
     * @return
     * 		null if date is not set
     */
    public Date getDateObject(OgemaHttpRequest req) {
    	return getData(req).getDateObject();
    }
    
    /** Get the date and time selected
     * 
     * @param req
     * @return milliseconds since epoch UTC or 
     * 		Long.MIN_VALUE if date is not set
     */
    public long getDateLong(OgemaHttpRequest req) {
    	return getData(req).getDateLong();
    }
  
	public String getDefaultLocale() {
		return defaultLocale;
	}
	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	public String getDefaultFormat() {
		return defaultFormat;
	}
	/** Set date and time format to be used. If not set the format YYYY-MM-DD HH:mm:ss is used (Javascript format)*/
	public void setDefaultFormat(String defaultFormat) {
		this.defaultFormat = defaultFormat;
	}
	public String getDefaultViewMode() {
		return defaultViewMode;
	}
	/** Set view mode
	 * 
	 * @param defaultViewMode use one of the values specified in {@link #Datepicker(WidgetPage, String, String, String, String, String, Set)}
	 *      or null to set back to default view mode
	 */
	public void setDefaultViewMode(String defaultViewMode) {
		this.defaultViewMode = defaultViewMode;
	}
	public String getDefaultDate() {
		return defaultDate;
	}
	public void setDefaultDate(String defaultDate) {
		this.defaultDate = defaultDate;
	}
	public Set<Integer> getDefaultDaysOfWeekDisabled() {
		return defaultDaysOfWeekDisabled;
	}

	public String getLocale(OgemaHttpRequest req) {
		return getData(req).getLocale();
	}

	public void setLocale(String locale,OgemaHttpRequest req) {
		getData(req).setLocale(locale);
	}

	public String getFormat(OgemaHttpRequest req) {
		return getData(req).getFormat();
	}

	public void setFormat(String format,OgemaHttpRequest req) {
		getData(req).setFormat(format);
	}

	public String getViewMode(OgemaHttpRequest req) {
		return getData(req).getViewMode();
	}

	public void setViewMode(String viewMode,OgemaHttpRequest req) {
		getData(req).setViewMode(viewMode);
	}

	public Set<Integer> getDaysOfWeekDisabled(OgemaHttpRequest req) {
		return getData(req).getDaysOfWeekDisabled();
	}
	
	public void addDayOfWeekDisabled(int day, OgemaHttpRequest req) {
		getData(req).addDayOfWeekDisabled(day);
	}
	
}
