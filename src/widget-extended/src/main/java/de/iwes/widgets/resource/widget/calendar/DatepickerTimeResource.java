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
package de.iwes.widgets.resource.widget.calendar;

import java.util.Set;

import org.json.JSONObject;
import org.ogema.core.model.simple.TimeResource;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.calendar.datepicker.DatepickerData;

public class DatepickerTimeResource extends Datepicker implements ResourceSelector<TimeResource> {

	private static final long serialVersionUID = 1L;
	private TimeResource defaultResource = null;

	public DatepickerTimeResource(WidgetPage<?> page, String id) {
		super(page, id);
	}
    
    public DatepickerTimeResource(WidgetPage<?> page, String id, String locale, String format, String defaultDate, String viewMode, Set<Integer> daysOfWeekDisabled) {
    	super(page, id, locale, format, defaultDate, viewMode, daysOfWeekDisabled);
    }
    
    public DatepickerTimeResource(OgemaWidget parent, String id, OgemaHttpRequest req) {
        super(parent, id, req);
    }
    
    @Override
    protected void setDefaultValues(DatepickerData opt) {
    	super.setDefaultValues(opt);
		DatepickerTimeResourceOptions opt2 = (DatepickerTimeResourceOptions) opt;
		opt2.setResource(defaultResource);
    }
    
    /* 
     ***************** options class ************
     */
    
    public static class DatepickerTimeResourceOptions extends DatepickerData {

		private TimeResource selectedResource = null;
    	
    	public DatepickerTimeResourceOptions(DatepickerTimeResource dtr) {
			super(dtr);
		}
    	
    	@Override
    	public JSONObject retrieveGETData(OgemaHttpRequest req) {
    		if (selectedResource != null && selectedResource.exists()) 
    			setDate(selectedResource.getValue());
    		else
    			setDate(""); // FIXME
    		return super.retrieveGETData(req);
    	}
    	
    	@Override
    	public JSONObject onPOST(String data, OgemaHttpRequest req) {
    		JSONObject result = super.onPOST(data, req);
    		if (selectedResource != null && selectedResource.exists()) 
    			selectedResource.setValue(getDateLong());
    		else
    			setDate("");
    		return result;
    	}
		
    	public TimeResource getResource() {
    		return selectedResource;
    	}

    	public void setResource(TimeResource resource) {
    		this.selectedResource = resource;
    	}
    	
    }
    
    /*
     ***************** internal methods *************
     */
    
    @Override
    public DatepickerTimeResourceOptions createNewSession() {
    	return new DatepickerTimeResourceOptions(this);
    }
    
    @Override
    public DatepickerTimeResourceOptions getData(OgemaHttpRequest req) {
    	return (DatepickerTimeResourceOptions) super.getData(req);
    }
    
    
    /*
     ****************** Public methods ************/

	public TimeResource getDefaultResource() {
		return defaultResource;
	}

	public void selectDefaultItem(TimeResource defaultResource) {
		this.defaultResource = defaultResource;
	}
	
	@Override
	public TimeResource getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getResource();
	}

	@Override
	public void selectItem(TimeResource resource, OgemaHttpRequest req) {
		getData(req).setResource(resource);
	}
    
}
