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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.resource.util.TimeUtils;
import org.ogema.tools.resource.util.ValueResourceUtils;

public class StringFormatHelper {
	/**Convert interval duration to a flexible string giving seconds, minutes, hours, days,
	 * 		months or years
	 * as the most readable choice
	 * @param deltaT interval duration in milliseconds
	 * @return a one or two digit value plus the time unit chosen by the method
	 */
	public static String getFormattedValue(long deltaT) {
    	if(deltaT < 0) {
    		return "--";
    	}
		deltaT = deltaT / 1000;
		if(deltaT < 100) {
			return String.format("%d sec", deltaT);
		}
		deltaT /= 60;
		if(deltaT < 100) {
			return String.format("%d min", deltaT);
		}
		deltaT /= 60;
		if(deltaT < 100) {
			return String.format("%d h", deltaT);
		}
		deltaT /= 24;
		if(deltaT < 100) {
			return String.format("%d d", deltaT);
		}
		float deltaTf = deltaT / (365.25f/12f);
		if(deltaTf < 60) {
			return String.format("%d month", Math.round(deltaTf));
		}
		deltaTf /= 12;
		if(deltaTf < 100) {
			return String.format("%d a", Math.round(deltaTf));
		}
		return (">99a");
	}
	
	/** Get representation of absolute time value as relative to the current framework time
	 * 		assuming the value in the resource is smaller than the framework time.
	 * If the value of the time resource is in the future "--" is returned.
	 * The String representation is created as defined in {@link #getFormattedValue(long)}
	 * @param appMan
	 * @param res resource to be evaluated compared to current framework time
	 * */
	public static String getFormattedAgoValue(ApplicationManager appMan, TimeResource res) {
		return getFormattedAgoValue(appMan, res.getValue());
	}
	public static String getFormattedAgoValue(ApplicationManager appMan, long value) {
		return getFormattedValue(appMan.getFrameworkTime() - value);
	}
	/** Get representation of absolute time value as relative to the current framework time
	 * 		assuming the value in the resource is greater than the framework time.
	 * If the value of the time resource is in the past "--" is returned.
	 * The String representation is created as defined in {@link #getFormattedValue(long)}
	 * @param appMan
	 * @param res resource to be evaluated compared to current framework time
	 * */
	public static String getFormattedFutureValue(ApplicationManager appMan, TimeResource res) {
		return getFormattedFutureValue(appMan, res.getValue());
	}
	public static String getFormattedFutureValue(ApplicationManager appMan, long value) {
		return getFormattedValue(value - appMan.getFrameworkTime());
	}
	
	/**Get string representation for a time value relative to the beginning of a day
	 * @param timeOfDay time compared to beginning of a day in milliseconds
	 * @return String representation in the format HH:MM
	 */
	public static String getFormattedTimeOfDay(long timeOfDay) {
		return getFormattedTimeOfDay(timeOfDay, false);
	}

	public static String getFormattedTimeOfDay(long timeOfDay, boolean printSeconds) {
    	if(timeOfDay < 0) {
    		return "--";
    	}
    	long hours = timeOfDay / (60*60000);
    	long minutes = (timeOfDay  - hours*(60*60000))/(60000);
    	if(printSeconds) {
    		long seconds = (timeOfDay  - hours*(60*60000) - minutes*60000)/(1000);
       		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    	}
   		return String.format("%02d:%02d", hours, minutes);
	}
	
	/**Get string representation for an absolute time value that is most suitable
	 * for most debugging purposes. If you also need the date information, use
	 * {@link #getFullTimeDateInLocalTimeZone(long)}. See {@link TimeUtils} for more flexible methods
	 * to get absolute time values as Strings.
	 * @param millisUTCSinceEpoc time to be printed
	 * @return String representation in the format HH:mm:ss:SSS
	 */
	public static String getTimeOfDayInLocalTimeZone(long millisUTCSinceEpoc) {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
		return formatter.format(millisUTCSinceEpoc);
	}
	/**See {@link getTimeOfDayInLocalTimeZone}.*/
	public static String getFullTimeDateInLocalTimeZone(long millisUTCSinceEpoc) {
		//Date date = new Date(millisUTCSinceEpoc-100);
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
		return formatter.format(millisUTCSinceEpoc);
	}
	public static String getTimeDateInLocalTimeZone(long millisUTCSinceEpoc) {
		//Date date = new Date(millisUTCSinceEpoc-100);
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return formatter.format(millisUTCSinceEpoc);
	}
	/**Get string date representation for an absolute time value.
	 * See {@link TimeUtils} for more flexible methods
	 * to get absolute time values as Strings.
	 * @param millisUTCSinceEpoc time to be printed
	 * @return String representation in the format dd.MM.yyyy
	 */
	public static String getDateInLocalTimeZone(long millisUTCSinceEpoc) {
		//Date date = new Date(millisUTCSinceEpoc-100);
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		return formatter.format(millisUTCSinceEpoc);
	}
	
	/**See {@link ValueResourceUtils#getValue(FloatResource, int)}*/
	public static String getValue(FloatResource resource, int maxDecimals) {
		return ValueResourceUtils.getValue(resource, maxDecimals);
	}
	
	/**Get standard date and time representation to be used as a file or directory name
	 * @return representation based on current framework time*/
	public static String getCurrentDateForPath(ApplicationManager appMan) {
	   	long curTime = appMan.getFrameworkTime();
	   	return getDateForPath(curTime);
	}
	/**Get standard date and time representation to be used as a file or directory name
	 * @return representation based on current framework time*/
	public static String getDateForPath(long timeStamp) {
    	Date date = new Date(timeStamp);
    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    	String strDate = formatter.format(date);
    	return strDate;
	}
	
	/**Return float value as per cent value
	 * @return String.format("%.0f %%", value*100)*/
	public static String getPerCent(float value) {
		return String.format("%.0f %%", value*100);
	}

	public static String getListToPrint(Collection<String> list) {
		if(list == null || list.isEmpty()) return "";
		String result = null;
		for(String s: list) {
			if(result == null) result = s;
			else result += ", "+s;
		}
		return result;
	}
	
	public static interface StringProvider<T> {
		String label(T object);
	}
	
	public static <T> String getListToPrint(Collection<T> list, StringProvider<T> strProv) {
		return getListToPrint(list, strProv, null);
	}
	
	public static <T> String getListToPrint(Collection<T> list, StringProvider<T> strProv, Integer maxEl) {
		if(list == null) return "";
		String result = null;
		int count = 0;
		for(T s: list) {
			if(result == null) result = strProv.label(s);
			else result += ", "+strProv.label(s);
			if(maxEl != null && (count >= maxEl))
				break;
			count++;
		}
		return result;
	}

	public static List<String> getListFromString(String serialized) {
		List<String> result = new ArrayList<String>();
		if(serialized == null) return result;
		int idx = 0;
		while(idx >= 0) {
			int newIdx = serialized.indexOf(',', idx);
			if(newIdx < 0) {
				result.add(serialized.substring(idx).trim());
				idx = -1;
				break;
			}
			result.add(serialized.substring(idx, newIdx).trim());
			idx = newIdx+1;
		}
		return result;
	}
}
