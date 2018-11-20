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
package de.iwes.timeseries.eval.onlineIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.BooleanValue;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.IntegerValue;
import org.ogema.core.channelmanager.measurements.LongValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;

/**Uses ResourceValueListeners and works as InterpolationMode.STEPS*/
public class OnlineNonBlockingIterator {
	protected final EvaluationInstance evaluationInstance;
	private class ListenerData {
		ResourceValueListener<?> listener;
		SingleValueResource resource;
		int index;
	}
	protected final List<ListenerData> valueListeners = new ArrayList<>();
	protected final int size;
	protected final ApplicationManager appMan;
	
	final Map<Integer, SampledValue> currentValues;
	// equal time stamps, only defined for a subset of iterators
	//final Map<Integer, SampledValue> comingValues;
	int comingIndex;
	SampledValue comingValue = null;
	// Map<iterator index, next value>; includes the comingValues
	// different time stamps
	final Map<Integer,SampledValue> nextValues;
	// different time stamps
	final Map<Integer,SampledValue> previousValues;
	protected long lastTime = 0;
	protected long currentTime = 0;
	protected long nextTime = 0;


	public OnlineNonBlockingIterator(EvaluationInstance evaluationInstance,
			List<EvaluationInput> allItems, ApplicationManager appMan) {
		this.evaluationInstance = evaluationInstance;
		this.appMan = appMan;
		int i = 0;
		for(EvaluationInput ei: allItems) {
			for(TimeSeriesData input : ei.getInputData()) {
				if(!(input instanceof TimeSeriesDataOnline)) throw new IllegalStateException("OnlineNBI requires TimeSeriesDataOnline as input");
				ListenerData ld = new ListenerData();
				ld.resource = ((TimeSeriesDataOnline)input).getResource();
				ld.index = i;
				valueListeners.add(ld);
				i++;
			}
		}
		size = i;
		this.currentValues = new HashMap<>(size);
		//this.comingValues = new HashMap<>(size);
		this.nextValues = new HashMap<>(size);
		this.previousValues = new HashMap<>(size);
	}
	
	public void start() {
		for(ListenerData ld: valueListeners) {
			ld.listener = new ResourceValueListener<SingleValueResource>() {

				@Override
				public void resourceChanged(SingleValueResource arg0) {
					Value val = getValue(arg0);
					advance(ld.index, new SampledValue(val, appMan.getFrameworkTime(), Quality.GOOD));
					//only the third callback will lead to an evaluation step
					if(lastTime > 0) {
						SampledValueDataPoint dataPoint = new SampledValueDataPointImplBase(
								currentValues, previousValues, nextValues, size,
								lastTime, currentTime, nextTime);
						try {
							evaluationInstance.step(dataPoint);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			ld.resource.addValueListener(ld.listener, true);
		}
	}
	
	public static Value getValue(SingleValueResource resource) {
		if (resource instanceof FloatResource) {
			return new FloatValue(((FloatResource) resource).getValue());
		}
		else if (resource instanceof IntegerResource) {
			return new IntegerValue(((IntegerResource) resource).getValue());
		}
		else if (resource instanceof BooleanResource) {
			return new BooleanValue(((BooleanResource) resource).getValue());
		}
		else if (resource instanceof TimeResource) {
			return new LongValue(((TimeResource) resource).getValue());
		}
		else
			throw new RuntimeException();
	}

	protected void advance(int index, SampledValue newValue) {
		//if (comingValues.isEmpty())
		//	throw new NoSuchElementException("No further element");
		final Iterator<Map.Entry<Integer, SampledValue>> currentValsIt = currentValues.entrySet().iterator();
		while (currentValsIt.hasNext()) {
			final Map.Entry<Integer, SampledValue> entry = currentValsIt.next();
			currentValsIt.remove();
			previousValues.put(entry.getKey(), entry.getValue());
		}
		// TODO more efficient algorithm?
		//currentValues.putAll(comingValues);
		currentValues.put(comingIndex, comingValue);
		comingIndex = index;
		comingValue = newValue;
		nextValues.put(index, newValue);
		lastTime = currentTime;
		currentTime = nextTime;
		nextTime = newValue.getTimestamp();
		
		/*comingValues.clear();
		for (int key : currentValues.keySet()) {
			//final Iterator<SampledValue> it = iterators.get(key);
			final Iterator<SampledValue> it = iterators.get(key);
			boolean done = false;
			if (it.hasNext()) { 
				done = true;
				nextValues.put(key, it.next());
			} 
			if (!done)
				nextValues.remove(key);
		}
		
		if (nextValues.isEmpty())
			return;
		SampledValue sampleNext = null;
		// determine next time stamp
		for (SampledValue t : nextValues.values()) {
			if (sampleNext == null || t.compareTo(sampleNext) < 0)
				sampleNext = t;
		}
		SampledValue t;
		Map.Entry<Integer, SampledValue> entry;
		// set nextValues and comingValues
		final Iterator<Map.Entry<Integer, SampledValue>> entriesIt = nextValues.entrySet().iterator();
		while (entriesIt.hasNext()) {
			entry = entriesIt.next();
			t = entry.getValue();
			if (t.compareTo(sampleNext) == 0) {
				final int n = entry.getKey();
				comingValues.put(n, t);
			}
			
		}*/
	}
	
	/** Interrupt data collection, but evaluation is maintained and can be restarted*/
	public void interrupt() {
		for(ListenerData ld: valueListeners) {
			if(ld.listener != null) ld.resource.removeValueListener(ld.listener);
		}		
	}
	
	/**After calling stop the evaluationInstance is finished. For this reason the object cannot be
	 * restarted anymore.
	 */
	public void stop() {
		interrupt();
		evaluationInstance.finish();
	}
	
	public EvaluationInstance getEvaluationInstance() {
		return evaluationInstance;
	}

}
