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

package de.iwes.widgets.reswidget.scheduleplot.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.core.timeseries.TimeSeries;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest.WrappedSession;
import de.iwes.widgets.html.plot.api.Plot2DDataSet;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public abstract class ScheduleData<D extends Plot2DDataSet> {

	// order not preserved! ConcurrentMap should not be necessary here (TODO tbc)
//	protected final Map<String,SchedulePresentationData> schedules = new ConcurrentHashMap<String, SchedulePresentationData>();
	protected final Map<String,SchedulePresentationData> schedules = new LinkedHashMap<>();
	// XXX this can be session specific, although the ScheduleData object may be associated to a global widget data object
	// therefore we need to manage session data in here explicitly; soft values in cache
	// note: these values can be set before the schedule is actually added to the schedules map,
	// but when a schedule is removed from schedules, the session data is removed as well
	// Map<schedule label, session data>
	protected final Map<String, Cache<WrappedSession, ScheduleSettings>> sessionData = new HashMap<>();
	// NOTE: this is set in getAllDataSets method, should be queried immediately after calling this method (FIXME?)
	// avoids unnecessary iterations over schedules set
	protected InterpolationMode im = null; 
	private long startTime = Long.MIN_VALUE;
	private long endTime = Long.MAX_VALUE;
	// downsampling disabled if <= 0
	private long downsamplingItv = -1;
	/**
	 * null: do not use buffer,
	 * >= 0: use buffer
	 */
	private final Long bufferWindow;
	// key: schedule id, value: buffer
	private final Cache<String, MaxValBuffer> maxValues;
	
	protected ScheduleData() {
		this(null, null);
	}
	
	protected ScheduleData(Cache<String, MaxValBuffer> maxValues, Long bufferWindow) {
		if (maxValues != null && (bufferWindow == null || bufferWindow < 0)) 
			throw new IllegalArgumentException("bufferWindow must be non-negative");
		this.maxValues = maxValues;
		this.bufferWindow = bufferWindow;
	}
	
	/**** Methods to be implemented in derived class ****/
	
	/**
	 * convert time series data (in variable {@link #schedules}) to the respective widget's data format
	 * @param offset
	 * 		will be added to all values
	 */
	protected abstract D getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, 
			int maxNrPoints, float scale, float offset, long downsamplingItv);
	
	/**
	 * @param id
	 * @param schedule
	 * @param startTime
	 * @param endTime
	 * @param maxNrPoints
	 * @param scale
	 * @param offset
	 * @param ymin
	 * 		Skip points with y < ymin
	 * @param ymax
	 * 		Skip points with y > ymax
	 * @return
	 * @throws UnsupportedOperationException 
	 * 		If filtering of y-values is not implemented yet
	 */
	protected abstract D getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, 
			int maxNrPoints, float scale, float offset, float ymin, float ymax, long downsamplingIntv) throws UnsupportedOperationException;
	
	/**** Public methods  ****/
	
	public final void setSchedules(Map<String, SchedulePresentationData> schedules) {
		if (schedules == null) {
			this.schedules.clear();
			return;
		}
		Iterator<String> oldIt = this.schedules.keySet().iterator();
		while (oldIt.hasNext()) {
			String s = oldIt.next();
			if (!schedules.containsKey(s)) {
				oldIt.remove();
				sessionData.remove(s);
			}
		}
		oldIt = this.sessionData.keySet().iterator();
		while (oldIt.hasNext()) {
			String s = oldIt.next();
			if (!schedules.containsKey(s)) {
				oldIt.remove();
			}
		}
		final Iterator<Map.Entry<String, SchedulePresentationData>> newIt = schedules.entrySet().iterator();
		while (newIt.hasNext()) {
			final Map.Entry<String, SchedulePresentationData> entry = newIt.next();
			if (!this.schedules.containsKey(entry.getKey()))
				this.schedules.put(entry.getKey(),entry.getValue());
		}
	}

	// FIXME here we cannot determine the type, unless it is a schedule
	public final void addSchedule(String id, TimeSeries schedule) {
		Class<?> type = null;
		if (schedule instanceof Schedule) 
			type = ((Schedule) schedule).getParent().getResourceType();
		schedules.put(id, new DefaultSchedulePresentationData(schedule, type, id));
	}

	public final void addSchedule(String id, TimeSeries schedule, SingleValueResource parent) {
		schedules.put(id, new DefaultSchedulePresentationData(schedule, parent, parent.getPath()));
	}
	
	public final boolean removeSchedule(String id) {
		sessionData.remove(id);
		return schedules.remove(id) != null;
	}
	
	public final Map<String,SchedulePresentationData> getSchedules() {
		return new LinkedHashMap<String, SchedulePresentationData>(schedules);
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private ScheduleSettings getSessionData(String label, OgemaHttpRequest req) {
		Cache<WrappedSession, ScheduleSettings> map = sessionData.get(label);
		if (map == null) {
			map = CacheBuilder.newBuilder().initialCapacity(2).softValues().build();
			sessionData.put(label, map);
		}
		final WrappedSession wrap = req.getWrapper();
		ScheduleSettings settings = map.getIfPresent(wrap);
		if (settings == null) {
			settings = new ScheduleSettings();
			map.put(wrap, settings);
		}
		return settings;
	}
	
	public float getScale(String label, OgemaHttpRequest req) {
		final ScheduleSettings s = getInidividualConfigInternal(label, req);
		if (s == null)
			return 1;
		return s.scale;
	}
	
	public float getOffset(String label, OgemaHttpRequest req) {
		final ScheduleSettings s = getInidividualConfigInternal(label, req);
		if (s == null)
			return 0;
		return s.offset;
	}
	
	public void setScale(String label, float scale, OgemaHttpRequest req) {
		getSessionData(label, req).scale = scale;
	}
	
	public void setOffset(String label, float offset, OgemaHttpRequest req) {
		getSessionData(label, req).offset = offset; 
	}
	
	private ScheduleSettings getInidividualConfigInternal(String id, OgemaHttpRequest req) {
		final Cache<WrappedSession, ScheduleSettings> map = sessionData.get(id);
		if (map == null) 
			return null;
		return map.getIfPresent(req.getWrapper());
	}
	
	/****** Internal methods ****/
	
	public final Map<String,D> getAllDataSets(OgemaHttpRequest req) {
		return getAllDataSets(startTime, endTime, Integer.MAX_VALUE, false, req);
	}
	
	public final Map<String,D> getAllDataSets(long startTime, long endTime, int maxNrPoints, OgemaHttpRequest req) {
		return getAllDataSets(startTime, endTime, maxNrPoints, false, req);
	}
	
	public final Map<String,D> getAllDataSets(boolean doScale, OgemaHttpRequest req) {
		return getAllDataSets(startTime, endTime, Integer.MAX_VALUE, doScale, req);
	}
	
	public final Map<String,D> getAllDataSets(boolean doScale, float yminFilter, float ymaxFilter, OgemaHttpRequest req) {
		return getAllDataSets(startTime, endTime, Integer.MAX_VALUE, doScale, yminFilter, ymaxFilter, req);
	}
	
	public final Map<String,D> getAllDataSets(long startTime, long endTime, int maxNrPoints, boolean doScale, OgemaHttpRequest req) {
		return getAllDataSets(startTime, endTime, maxNrPoints, doScale, Float.NaN, Float.NaN, req);
	}
	
	/**
	 * for use in respective Widget's options' retrieveGET method
	 * @param yminFilter
	 * 		Points with values below this threshold will be filtered out.
	 * 		If NaN, it will be ignored.
	 * @param ymaxFilter
	 * 		Points with values above this threshold will be filtered out.
	 * 		If NaN, it will be ignored.
	 */
	public final Map<String,D> getAllDataSets(final long startTime, final long endTime, final int maxNrPoints, 
				final boolean doScale, final float yminFilter, final float ymaxFilter, final OgemaHttpRequest req) {
		final Map<String,D> map = new LinkedHashMap<String, D>();
		//find maximum
		Iterator<Map.Entry<String, SchedulePresentationData>> it = schedules.entrySet().iterator();
		final Map<String, Float> maxValues = new HashMap<>();
		float overallMax = 0;
		String key;
		SchedulePresentationData schedule;
		while (it.hasNext()) {
			Map.Entry<String, SchedulePresentationData> entry = it.next();
			// inefficient
//				ReadOnlyTimeSeries schedule = handleTemperatureSchedules(entry.getValue(), entry.getValue().getScheduleType());
			key = entry.getKey();
			schedule = entry.getValue();
			Float maxVal = null;
			if (this.maxValues != null) {
				MaxValBuffer buffer = this.maxValues.getIfPresent(key);
				if (buffer == null && bufferWindow != null) {
					buffer = new MaxValBuffer(schedule, bufferWindow);
					this.maxValues.put(key, buffer);
				}
				
				if (buffer != null)
					maxVal = buffer.getMaxValue(startTime, endTime);
			}
			if (maxVal == null) {
				Object[] result = getMaxValueAndPoint(entry.getValue().iterator(startTime, endTime), 1, true, true);
				if (result == null) // empty
					continue;
//					maxVal = ScheduleHelper.getMaxValue(entry.getValue().getValues(startTime, endTime), 1, true, true);
				maxVal = (float) result[1];
//					long time = (long) result[0];
				
			}
			Class<?> type = schedule.getScheduleType();
			if (type == TemperatureResource.class)
				maxVal = maxVal - 273.15F;
			maxValues.put(key, maxVal);
			if ((overallMax < maxVal) && (!Float.isNaN(maxVal))) {
				overallMax = maxVal;
			}
		}
		it = schedules.entrySet().iterator();
		float offset;
		while (it.hasNext()) {
			Map.Entry<String, SchedulePresentationData> entry = it.next();
//				System.out.println("Sched:"+((Schedule)entry.getValue()).getName()+" overall:"+overallMax+" max:"+maxValues.get(entry.getKey()));
			// inefficient; better scale values when writing them to the request response
//				ReadOnlyTimeSeries schedule = handleTemperatureSchedules(entry.getValue(), entry.getValue().getScheduleType());
			key = entry.getKey();
			schedule = entry.getValue();
			if (schedule.getScheduleType() == TemperatureResource.class) 
				offset = -273.15F;
			else if (schedule.getScheduleType() == Float.class && schedule.getLabel(OgemaLocale.ENGLISH).toLowerCase().contains("temperature")) 
				offset = -273.15F;
			else 
				offset = 0;
			float scale = 1;
			final String id;
			final ScheduleSettings settings = getInidividualConfigInternal(key, req);
			if (settings != null) {
				scale = settings.scale;
				if (scale <= 0)
					scale = 1;
				offset += settings.offset;
				String lab = String.format("%sx%.2f",key,scale);
				if (settings.offset != 0F)
					lab += String.format("+%.2f", settings.offset);
				id = Utils.getValidJSName(lab); 
			}
			else if (doScale && maxValues.containsKey(key)) {
				//perform scaling
				float maxVal = maxValues.get(key);
				float maxRatio = overallMax / maxVal;
//				System.out.println("Sched:"+((Schedule)entry.getValue()).getName()+" ratio:"+maxRatio);
				if((maxRatio < 10)||(Float.isNaN(maxRatio))) {
					id = Utils.getValidJSName(key); 
//						map.put(id, getData(id, schedule, startTime,endTime,maxNrPoints));
				} else {
					String scaleName;
					if(maxRatio < 100) {
						scaleName = "x10";
						scale = 10;
					} else if(maxRatio < 1000) {
						scaleName = "x100";
						scale = 100;
					} else if(maxRatio < 10000) {
						scaleName = "x1000";
						scale = 1000;
					} else {
						scaleName = "x1e4";
						scale = 10000;
					}
					id = Utils.getValidJSName(key+scaleName); 
//						ReadOnlyTimeSeries scaledSchedule = ScheduleHelper.affineTransformation(schedule, scale, 0);
//						map.put(id, getData(id, scaledSchedule, startTime,endTime,maxNrPoints));
				}
			}
			else  {
				id = Utils.getValidJSName(key);
//					map.put(id, getData(id, schedule, startTime,endTime,maxNrPoints));
			}
			if ((Float.isNaN(yminFilter) || Float.isInfinite(yminFilter)) && (Float.isNaN(ymaxFilter) || Float.isInfinite(ymaxFilter)))
				map.put(id, getData(id, schedule, startTime,endTime,maxNrPoints,scale, offset, downsamplingItv));
			else
				map.put(id, getData(id, schedule, startTime,endTime,maxNrPoints,scale, offset, yminFilter, ymaxFilter, downsamplingItv));
			InterpolationMode ims = schedule.getInterpolationMode();
			if (ims != null)
				im = ims;
		}
		return map;
	}

	// NOTE: interpolation mode is set in getAllDataSets method, should be queried immediately after calling this method (FIXME?)
	// avoids unnecessary iterations over schedules set
	public PlotType getCurrentType() {
		if (im == null) 
			return PlotType.LINE_WITH_POINTS; // default
		switch (im) {
		case LINEAR:
			return PlotType.LINE_WITH_POINTS;
		case STEPS:
			return PlotType.STEPS;
		case NEAREST:
			return PlotType.STEPS; // TODO not really covered! This shows STEPS...
		case NONE:
			return PlotType.POINTS;
		default:
			return null;
		}
		
	}
	
	public long getDownsamplingItv() {
		return downsamplingItv;
	}

	/**
	 * 
	 * @param downsamplingItv
	 * 		interval in ms. Set to non-positive value to disable downsampling
	 */
	public void setDownsamplingInterval(long downsamplingItv) {
		this.downsamplingItv = downsamplingItv;
	}

	// public method availabel in util-extended ScheduleHelper
	private static Object[] getMaxValueAndPoint(Iterator<SampledValue> values, int mode, boolean omitBadQuality, boolean omitInfinity) {
		if (!values.hasNext()) 
			return null;
		float maxValue = -Float.MAX_VALUE;
		long timestamp = Long.MIN_VALUE;
		SampledValue sv;
		while (values.hasNext()) {
			sv = values.next();
			if (omitBadQuality && Quality.BAD.equals(sv.getQuality())) continue;
			float val = sv.getValue().getFloatValue();
			if (Float.isNaN(val) || (Float.isInfinite(val) && omitInfinity))
				continue;
			if (mode == 1)
				val = Math.abs(val);
			if (val > maxValue) {
				maxValue = val;
				timestamp = sv.getTimestamp();
			}
		}
		return new Object[]{timestamp, maxValue};
	}
	
}
