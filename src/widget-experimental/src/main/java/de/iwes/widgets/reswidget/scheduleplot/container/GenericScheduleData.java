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
package de.iwes.widgets.reswidget.scheduleplot.container;

import java.util.Map;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.core.timeseries.TimeSeries;
import org.ogema.humread.valueconversion.SchedulePresentationData;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;

public class GenericScheduleData extends ScheduleData<GenericDataset> {

	private final TimeSeriesPlotData sessionData;
	private final OgemaHttpRequest req;

	public GenericScheduleData(TimeSeriesPlotData sessionData, OgemaHttpRequest req) {
		this.sessionData = sessionData;
		this.req = req;
	}

	@Override
	protected GenericDataset getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime,
			int maxNrPoints, float scale, float offset, long downsamplingItv) {
		return new GenericDataset(id);
	}

	@Override
	protected GenericDataset getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime,
			int maxNrPoints, float scale, float offset, float ymin, float ymax, long downsamplingIntv)
			throws UnsupportedOperationException {
		return new GenericDataset(id);
	}

	@Override
	public Map<String, SchedulePresentationData> getSchedules() {
		return sessionData.getTarget(req).getScheduleData(req).getSchedules();
	}

	@Override
	public void setSchedules(Map<String, SchedulePresentationData> schedules) {
		 sessionData.getTarget(req).getScheduleData(req).setSchedules(schedules);
	}

	@Override
	public void addSchedule(String id, TimeSeries schedule) {
		 sessionData.getTarget(req).getScheduleData(req).addSchedule(id, schedule);
	}

	@Override
	public void addSchedule(String id, TimeSeries schedule, SingleValueResource parent) {
		 sessionData.getTarget(req).getScheduleData(req).addSchedule(id, schedule);
	}

	@Override
	public boolean removeSchedule(String id) {
		 return sessionData.getTarget(req).getScheduleData(req).removeSchedule(id);
	}

	@Override
	public float getScale(String label, OgemaHttpRequest req) {
		return sessionData.getTarget(req).getScheduleData(req).getScale(label, req);
	}

	@Override
	public PlotType getCurrentType() {
		return sessionData.getTarget(req).getScheduleData(req).getCurrentType();
	}

	@Override
	public long getStartTime() {
		return sessionData.getTarget(req).getScheduleData(req).getStartTime();
	}

	@Override
	public long getEndTime() {
		return sessionData.getTarget(req).getScheduleData(req).getEndTime();
	}

	@Override
	public long getDownsamplingItv() {
		return sessionData.getTarget(req).getScheduleData(req).getDownsamplingItv();
	}

	@Override
	public float getOffset(String label, OgemaHttpRequest req) {
		return sessionData.getTarget(req).getScheduleData(req).getOffset(label, req);
	}

	@Override
	public void setScale(String label, float scale, OgemaHttpRequest req) {
		sessionData.getTarget(req).getScheduleData(req).setScale(label, scale, req);
	}

	@Override
	public void setDownsamplingInterval(long downsamplingItv) {
		sessionData.getTarget(req).getScheduleData(req).setDownsamplingInterval(downsamplingItv);
	}

	@Override
	public void setOffset(String label, float offset, OgemaHttpRequest req) {
		sessionData.getTarget(req).getScheduleData(req).setOffset(label, offset, req);
	}

	@Override
	public void setStartTime(long startTime) {
		sessionData.getTarget(req).getScheduleData(req).setStartTime(startTime);
	}

	@Override
	public void setEndTime(long endTime) {
		sessionData.getTarget(req).getScheduleData(req).setEndTime(endTime);
	}

}
