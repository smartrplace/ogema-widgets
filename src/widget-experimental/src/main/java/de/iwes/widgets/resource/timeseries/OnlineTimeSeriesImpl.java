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
package de.iwes.widgets.resource.timeseries;

import java.util.Iterator;
import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.InterpolationMode;

class OnlineTimeSeriesImpl implements OnlineTimeSeries {
	
	// keep a reference to the proxy to prevent it from being collected, 
	// as long as the online time series is in use.
	private final OnlineTimeSeriesProxy proxy;
	private final SingleValueResource resource;
	
	OnlineTimeSeriesImpl(OnlineTimeSeriesProxy proxy, SingleValueResource resource) {
		this.proxy = proxy;
		this.resource = resource;
	}
	
	@Override
	public SingleValueResource getResource() {
		return resource;
	}
	
	@Override
	public OnlineTimeSeriesConfiguration getConfiguration() {
		return proxy.config;
	}
	
	@Override
	public SampledValue getCurrentValue() {
		return proxy.generateValue();
	}

	@Override
	public SampledValue getValue(long time) {
		return proxy.timeSeries.getValue(time);
	} 

	@Override
	public SampledValue getNextValue(long time) {
		return proxy.timeSeries.getNextValue(time);
	}

	@Override
	public SampledValue getPreviousValue(long time) {
		return proxy.timeSeries.getPreviousValue(time);
	}

	@Override
	public List<SampledValue> getValues(long startTime) {
		return proxy.timeSeries.getValues(startTime);
	}

	@Override
	public List<SampledValue> getValues(long startTime, long endTime) {
		return proxy.timeSeries.getValues(startTime,endTime);
	}

	@Override
	public InterpolationMode getInterpolationMode() {
		return proxy.timeSeries.getInterpolationMode();
	}

	@Override
	public boolean isEmpty() {
		return proxy.timeSeries.isEmpty();
	}

	@Override
	public boolean isEmpty(long startTime, long endTime) {
		return proxy.timeSeries.isEmpty(startTime, endTime);
	}

	@Override
	public int size() {
		return proxy.timeSeries.size();
	}

	@Override
	public int size(long startTime, long endTime) {
		return proxy.timeSeries.size(startTime, endTime);
	}

	@Override
	public OnlineIterator onlineIterator(boolean blocking) {
		return proxy.getOnlineIterator(blocking, Long.MAX_VALUE);
	}

	@Override
	public OnlineIterator onlineIterator(boolean blocking, long endTime) {
		return proxy.getOnlineIterator(blocking, endTime);
	}
	
	// here we need to copy the values set, otherwise the iterator could throw ConcurrentModificationException
	// typically, it should be small anyway, so copying the values list is fine
	@Override
	public Iterator<SampledValue> iterator() {
		return proxy.timeSeries.getValues(Long.MIN_VALUE).iterator(); 
	}
	
	// here we need to copy the values set, otherwise the iterator could throw ConcurrentModificationException
	// typically, it should be small anyway, so copying the values list is fine
	@Override
	public Iterator<SampledValue> iterator(long startTime, long endTime) {
		return proxy.timeSeries.getValues(startTime, endTime).iterator();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Long getTimeOfLatestEntry() {
		return proxy.timeSeries.getTimeOfLatestEntry();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof OnlineTimeSeries))
			return false;
		return resource.equals(((OnlineTimeSeries) obj).getResource());
	}
	
	@Override
	public int hashCode() {
		return resource.hashCode()*23;
	}
	
}
