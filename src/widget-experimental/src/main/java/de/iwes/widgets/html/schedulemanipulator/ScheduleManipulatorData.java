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
package de.iwes.widgets.html.schedulemanipulator;

import org.json.JSONObject;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.TimeSeries;

import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ScheduleManipulatorData extends PageSnippetData {
	
	private TimeSeries schedule = null;
	private final boolean showQuality;
	private boolean allowPointAddition = true;
	private int nrValuesDisplayed = 10;
	// choose MIN_VALUE, so it is displayed first
	protected final static long NEW_LINE_ID = Long.MIN_VALUE;
	protected final static long HEADER_LINE_ID = Long.MIN_VALUE;

	/*
	 ************************* constructors **********************
	 */
    
    public ScheduleManipulatorData(ScheduleManipulator manipulator, boolean showQuality) {
    	super(manipulator);
    	this.showQuality = showQuality;
	}
    
    /*
     * ****** Inherited methods *****
     */
    
    /*
     * (non-Javadoc)
     * @see de.iwes.widgets.api.extended.html.bricks.PageSnippetData#retrieveGETData(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest)
     * 
     * The startTime date picker must be set externally, so we can use the "Next" button to change its value (paging)
     */
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {	
		JSONObject obj = super.retrieveGETData(req);
		TimeSeries schedule = getSchedule();
		long startTime = System.currentTimeMillis();
		if (schedule != null)  {
			SampledValue sv = schedule.getNextValue(Long.MIN_VALUE);
			if (sv != null) 
				startTime = sv.getTimestamp();
		}
		getWidget().startTimePicker.setDate(startTime, req);
		return obj;
	}

	/*
	 ************************** public methods ***********************/
	

	public TimeSeries getSchedule() {
		return schedule;
	}

	public void setSchedule(TimeSeries schedule) {
		this.schedule = schedule;
	}

	public int getNrValuesDisplayed() {
		return nrValuesDisplayed;
	}

	public void setNrValuesDisplayed(int nrValuesDisplayed) {
		this.nrValuesDisplayed = nrValuesDisplayed;
	}
	
	public boolean isAllowPointAddition() {
		return allowPointAddition;
	}

	public void setAllowPointAddition(boolean allowPointAddition) {
		this.allowPointAddition = allowPointAddition;
	}
	
	
	/*
	 ************************** internal methods ***********************
	*/

	protected ScheduleManipulator getWidget() {
		return (ScheduleManipulator) widget;
	}
	
}
