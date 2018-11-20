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
package de.iwes.widgets.html.form.label;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TimeIntervalLabelData extends LabelData {
	
	private long intervalInMs = Long.MIN_VALUE;
	private PeriodType periodType = PeriodType.standard();
    private boolean displayZeros = false;

/*********** Constructor **********/
	
	public TimeIntervalLabelData(TimeIntervalLabel intervalLabel) {
		super(intervalLabel);
	}
    
	/******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	if (intervalInMs == Long.MIN_VALUE) {
    		setText("");
    		return super.retrieveGETData(req);
    	}
    	else if (intervalInMs == 0) {
    		setText("0");
    		return super.retrieveGETData(req);
    	}
    	// FIXME the normalization is not sufficient; it still represents months and years
    	// in terms of weeks and days (1M = 4w 2d), 1y = 52w 1d
    	// Joda time is pretty annoying here... it would make more sense to use Duration instead of Period, but this does not support years and months at all
    	Period period = new Period(intervalInMs).normalizedStandard();  
    	StringBuilder sb = new StringBuilder();
    	boolean started = false;
    	if (periodType.isSupported(DurationFieldType.years())) {
    		int years = period.getYears(); 
        	if (years != 0) {
        		started = true;
        		sb.append(years + "y ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.months())) {
    		int months = period.getMonths();
        	if ((displayZeros && started) || months > 0) {
        		started = true;
        		sb.append(months + "M ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.weeks())) {
        	int weeks = period.getWeeks();
        	if ((displayZeros && started) || weeks > 0) {
        		started = true;
        		sb.append(weeks + "w ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.days())) {
        	int days = period.getDays();
        	if ((displayZeros && started) || days > 0) {
        		started = true;
        		sb.append(days + "d ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.hours())) {
        	int hours = period.getHours();
        	if ((displayZeros && started) || hours > 0) {
        		started = true;
        		sb.append(hours + "h ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.minutes())) {
        	int min = period.getMinutes();
        	if ((displayZeros && started) || min > 0) {
        		started = true;
        		sb.append(min + "m ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.seconds())) {
        	int seconds = period.getSeconds();
        	if ((displayZeros && started) || seconds > 0) {
        		started = true;
        		sb.append(seconds + "s ");
        	}
    	}
    	if (periodType.isSupported(DurationFieldType.millis())) {
        	int millis = period.getMillis();
        	if (displayZeros || millis > 0)
        		sb.append(millis + "ms ");
    	}
    	setText(sb.toString());
    	return super.retrieveGETData(req);
    }
    
    /******* Public methods ******/

 	public long getInterval() {
 		return intervalInMs;
 	}

 	public void setInterval(long interval) {
 		this.intervalInMs = interval;
 	}
 	
 	public void setPeriodType(PeriodType type) {
 		this.periodType = type;
 	}
 	
 	public PeriodType getPeriodType() {
 		return periodType;
 	}
       
 	public boolean isDisplayZeros() {
		return displayZeros;
	}

	public void setDisplayZeros(boolean displayZeros) {
		this.displayZeros = displayZeros;
	}
 	
}
