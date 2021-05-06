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
package de.iwes.widgets.resource.widget.textfield;

import java.util.Locale;

import org.json.JSONObject;
import org.ogema.core.model.simple.TimeResource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.resource.widget.textfield.TimeResourceTextField.Interval;

public class TimeResourceTextFieldData extends ValueResourceTextFieldData<TimeResource> {

	private Interval interval;


	public TimeResourceTextFieldData(TimeResourceTextField textField) {
		super(textField);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject result = super.retrieveGETData(req);
		TimeResource hRes = getSelectedResource();
//		if (hRes == null) {
//			super.setValue(getNaValue("n/a", interval));  // super.setValue not supported
//		} else {
//			super.setValue(getIntervalString(hRes.getValue(), interval));
//		}
		if (hRes != null) 
			result.put("value", getIntervalString(hRes.getValue(), interval)); // we simply overwrite the json answer instead of setting the value...
		return result;
	}
	
	@Override
	protected void updateOnGET(Locale locale) {
		TimeResource hRes = getSelectedResource();
		String val;
		if (hRes != null) 
			val = getIntervalString(hRes.getValue(), interval);
		else
			val = "n.a.";
		setValue(val);
	}

	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject retval = super.onPOST(data, req);
		/*String value = super.getValue();
		try {
			TimeResource hRes = getSelectedResource();
			hRes.setValue(getTimeValue(value, interval));
		} catch (Exception e) {
			// ignore: we do not want to write user data to the log
		}*/
		return retval;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	/*
	 ******** Internal methods ************
	 */
	
	private static final String getNaValue(String naString, Interval i) {
		switch (i) {
		case minutes:
			return String.format("%s min", naString);
		case hours:
			return String.format("%s h", naString);
		case days:
			return String.format("%s d", naString);
		case timeOfDay:
			return String.format("%s hh:min", naString);
		default:
			return String.format("%s sec", naString);
		}
	}

	public static final String getIntervalString(long value, Interval i) {
		if (value == -1) {
			return getNaValue("--", i);
		}
		switch (i) {
		case minutes:
			return String.format("%d min", value / 60000);
		case hours:
			return String.format("%d h", value / (60 * 60000));
		case days:
			return String.format("%d d", value / (24 * 60 * 60000));
		case timeOfDay:
			return getFormattedTimeOfDay(value);
		default:
			return String.format("%d sec", value / (1000));
		}
	}
	
	private static String getFormattedTimeOfDay(final long timeOfDay) {
    	if(timeOfDay < 0) {
    		return "--";
    	}
    	long hours = timeOfDay / (60*60000);
    	long minutes = (timeOfDay  - hours*(60*60000))/(60000);
   		return String.format("%02d:%02d", hours, minutes);
	}
	
}
