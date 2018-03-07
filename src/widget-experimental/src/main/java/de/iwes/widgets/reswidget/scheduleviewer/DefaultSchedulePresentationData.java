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

package de.iwes.widgets.reswidget.scheduleviewer;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

/**
 * Use to display MemoryTimeSeries in a ScheduleViewer, or to provide a custom label
 * to Schedules and/or RecordedData elements in a ScheduleViewer. 
 */
public class DefaultSchedulePresentationData implements SchedulePresentationData {
	
	protected final ReadOnlyTimeSeries rots;
	protected final SingleValueResource parent;
	protected final String label;
	protected final Class<?> type;
	// can be used to overwrite the mode of the time series, but can be null as well
	protected final InterpolationMode mode;
	// avoid hard links to session-specific objects
	
	/**
	 * @param schedule
	 * 		The schedule to be displayed.
	 * @param parent
	 * 		Associated resource, which determines the schedule's values type.<br>
	 * 		May be null, in which case a generic float type is assumed.  
	 * @param label
	 * 		The label to be displayed by a schedule viewer. In order to provide a locale-dependent label instead,
	 * 		override the method {@link #getLabel(OgemaLocale)} in a subclass.
	 */
	public DefaultSchedulePresentationData(ReadOnlyTimeSeries schedule, SingleValueResource parent, String label) {
		this(schedule, parent, label, schedule.getInterpolationMode());
	}
	
	public DefaultSchedulePresentationData(ReadOnlyTimeSeries schedule, Class<?> type, String label) {
		this(schedule, type, label, schedule.getInterpolationMode());
	}
	
	public DefaultSchedulePresentationData(ReadOnlyTimeSeries schedule, SingleValueResource parent, String label, InterpolationMode mode) {
		Objects.requireNonNull(schedule);
		Objects.requireNonNull(label);
		if (label.trim().isEmpty())
			throw new IllegalArgumentException("Label empty");
		this.rots = schedule;
		this.parent = parent;
		this.label = label;
		if (parent != null)
			type = parent.getResourceType();
		else
			type = Float.class; // default
		this.mode = mode;
	}
	
	public DefaultSchedulePresentationData(ReadOnlyTimeSeries schedule, Class<?> type, String label, InterpolationMode mode) {
		Objects.requireNonNull(schedule);
		Objects.requireNonNull(label);
		if (label.trim().isEmpty())
			throw new IllegalArgumentException("Label empty");
		this.rots = schedule;
		this.parent = null;
		this.label = label;
		if (type != null)
			this.type = type;
		else
			this.type = Float.class; // defualt
		this.mode = mode;
	}

	/**
	 * Override in subclass, if localisation is required.
	 */
	@Override
	public String getLabel(OgemaLocale locale) {
		return label;
	}

	@Override
	public Class<?> getScheduleType() {
		return type;
	}
	

	@Override
	public SampledValue getValue(long time) {
		return rots.getValue(time);
	}

	@Override
	public SampledValue getNextValue(long time) {
		return rots.getNextValue(time);
	}
	
//	@Override
	public SampledValue getPreviousValue(long time) {
		return rots.getPreviousValue(time);
	}

	@Override
	public List<SampledValue> getValues(long startTime) {
		return rots.getValues(startTime);
	}

	@Override
	public List<SampledValue> getValues(long startTime, long endTime) {
		return rots.getValues(startTime, endTime);
	}

	@Override
	public InterpolationMode getInterpolationMode() {
		return mode;
	}

	@Override
	public Long getTimeOfLatestEntry() {
		return getTimeOfLatestEntry();
	}

//	@Override
	public boolean isEmpty() {
		return rots.isEmpty();
	}

//	@Override
	public boolean isEmpty(long startTime, long endTime) {
		return rots.isEmpty(startTime, endTime);
	}

//	@Override
	public int size() {
		return rots.size();
	}

//	@Override
	public int size(long startTime, long endTime) {
		return rots.size(startTime, endTime);
	}

//	@Override
	public Iterator<SampledValue> iterator() {
		return rots.iterator();
	}

//	@Override
	public Iterator<SampledValue> iterator(long startTime, long endTime) {
		return rots.iterator(startTime, endTime);
	}

	@Override
	public String toString() {
		return "DefaultSchedulePresentationData: " + rots;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof DefaultSchedulePresentationData))
			return false;
		final DefaultSchedulePresentationData other = (DefaultSchedulePresentationData) obj;
		if (!this.rots.equals(other.rots))
			return false;
		if (this.label == null && other.label == null)
			return true;
		if (this.label == null || other.label == null)
			return false;
		return this.label.equals(other.label);
	}
	
	@Override
	public int hashCode() {
		return rots.hashCode() * 11 + (label != null ? label.hashCode() : 7); 
	}
	
}
