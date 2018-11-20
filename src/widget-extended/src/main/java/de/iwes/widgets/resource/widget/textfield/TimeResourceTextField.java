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

import org.ogema.core.model.simple.TimeResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextFieldData;


/** 
 * Textfield that represents the value of a TimeResource and allows to edit it.
 * Note that this is only applicable to resources that represent a time interval.
 * For absolute time stamps it is recommended to use a 
 * org.ogema.tools.reswidget.calendar.DatepickerTimeResourcee.
 */
public class TimeResourceTextField extends ValueResourceTextField<TimeResource> {

    private static final long serialVersionUID = 550713654103033621L;
 
    public enum Interval {
    	seconds,
    	minutes,
    	hours,
    	days,
    	timeOfDay
    }
    
    private Interval defaultInterval;
    
    public TimeResourceTextField(WidgetPage<?> page, String id) {
    	this(page, id, Interval.seconds);
    }
    
    public TimeResourceTextField(WidgetPage<?> page, String id, Interval defaultInterval) {
    	super(page, id);
    	setDefaultInterval(defaultInterval);
    }

    public TimeResourceTextField(OgemaWidget parent, String id, Interval defaultInterval, TimeResource resource, OgemaHttpRequest req) {
    	super(parent, id, resource, req);
    	setDefaultInterval(defaultInterval);
    }

    @Override
    protected final String format(TimeResource resource, Locale locale) {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public TimeResourceTextFieldData createNewSession() {
     	return new TimeResourceTextFieldData(this);
    }
    
    @Override
    public TimeResourceTextFieldData getData(OgemaHttpRequest req) {
    	return (TimeResourceTextFieldData) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(TextFieldData opt) {
    	super.setDefaultValues(opt);
    	TimeResourceTextFieldData opt2 = (TimeResourceTextFieldData) opt;
    	opt2.setInterval(defaultInterval);
    }
    

	public Interval getInterval(OgemaHttpRequest req) {
		return getData(req).getInterval();
	}

	public void setInterval(Interval interval, OgemaHttpRequest req) {
		getData(req).setInterval(interval);
	}
 
	public void setDefaultInterval(Interval interval) {
		this.defaultInterval = interval;
	}
	
	@Override
	protected void setResourceValue(TimeResource resource, String value, OgemaHttpRequest req) {
		try {
			resource.setValue(getTimeValue(value, getData(req).getInterval()));
		} catch (Exception e) {
			// ignore: we do not want to write user data to the log
		}
	}
	
	protected static final long getTimeValue(String value, Interval i) {
		if (i == Interval.timeOfDay) {
			String[] parts = value.split(":");
			long hours = Long.valueOf(parts[0].replaceAll("[^\\d.]", ""));
			long mins = Long.valueOf(parts[1].replaceAll("[^\\d.]", ""));
			return hours * (60 * 60000) + mins * 60000;
		}
		value = value.replaceAll("[^\\d.]", "");
		switch (i) {
		case minutes:
			return Long.valueOf(value) * 60000;
		case hours:
			return Long.valueOf(value) * (60 * 60000);
		case days:
			return Long.valueOf(value) * (24 * 60 * 60000);
		default:
			return Long.valueOf(value) * 1000;
		}
	}
}
