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
