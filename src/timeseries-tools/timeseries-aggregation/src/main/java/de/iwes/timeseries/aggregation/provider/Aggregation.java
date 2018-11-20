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
package de.iwes.timeseries.aggregation.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.aggregation.provider.AggregationProvider.AggregationType;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesResultImpl;

public class Aggregation extends EvaluationBaseImpl {
	
	private final List<TimeSeriesData> input;
	private final int inputSize;
	private final String id;
	private final static AtomicLong counter = new AtomicLong(0); // TODO initialize from existing stored eval resources
	// state variables
	// synchronized on this
	private final Map<Integer, Map<AggregationType, FloatTimeSeries>> values;
//	// Map<aggregation type, Map<last timestamp, total integrated value per input time series>>
//	// should contain a single timestamp per aggregation tpye
//	private final Map<AggregationType, Map<Long,float[]>> previousValues = new HashMap<>(requestedResults.size());
	
	private final Map<Integer, Map<AggregationType, Float>> integrationBuffer;
	private final Map<Integer, Map<AggregationType, Long>> intervalBuffer;
	
//	private final Map<Integer, Map<AggregationType, Float>> lastValidValues = new HashMap<>(inputSize); // used?
	private final Map<Integer, Map<AggregationType, Boolean>> nanBuffer;
	
	private Map<Integer,SampledValue> lastValues;

	public Aggregation(List<EvaluationInput> input, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
		super(input, requestedResults, configurations, listener, time);
		this.id = "AggregationEvaluation_" + counter.incrementAndGet();
		final List<TimeSeriesData> list = new ArrayList<>();
		list.addAll(input.iterator().next().getInputData());
		this.input = Collections.unmodifiableList(list);
		this.inputSize = this.input.size();
		values = new HashMap<>(inputSize);
		integrationBuffer = new HashMap<>(inputSize);
		intervalBuffer = new HashMap<>(inputSize);
		nanBuffer = new HashMap<>(inputSize);
		lastValues = new HashMap<>(inputSize + requestedResults.size());
		for (int i=0;i<inputSize;i++) {
			values.put(i, requestedResults.stream()
				.collect(Collectors.toMap(type -> (AggregationType) type, type -> {
					final FloatTimeSeries ts = (FloatTimeSeries) new FloatTreeTimeSeries();
					ts.setInterpolationMode(InterpolationMode.STEPS);
					return ts;
				})));
			integrationBuffer.put(i, new HashMap<>(requestedResults.size()));
			intervalBuffer.put(i, new HashMap<>(requestedResults.size()));	
//			lastValidValues.put(i, new HashMap<>(requestedResults.size()));
			nanBuffer.put(i, new HashMap<>(requestedResults.size()));
		}
	}
	
	@Override
	public List<ReadOnlyTimeSeries> addedInput() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final List<AggregationType> aggs = new ArrayList<>((List) requestedResults);
		Collections.sort(aggs);
		final List<ReadOnlyTimeSeries> list = new ArrayList<>();
		for (AggregationType aggType: aggs) {
			list.add(new StandardTimeSeries(aggType));
		}
		return list;
	}
	
	@Override
	public String id() {
		return id;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map<ResultType, EvaluationResult> getCurrentResults() {
		final Map<AggregationType,EvaluationResult> results = new TreeMap<>();
		final Map<AggregationType, List<SingleEvaluationResult>> prelimMap = new HashMap<>();
		for (ResultType type : requestedResults) {
			prelimMap.put((AggregationType) type, new ArrayList<>());
		}
		synchronized (this) {
			for (Map.Entry<Integer, Map<AggregationType,FloatTimeSeries>> entry : values.entrySet()) {
				for (Map.Entry<AggregationType, FloatTimeSeries> typeEntry: entry.getValue().entrySet()) {
					final AggregationType type = typeEntry.getKey();
					TimeSeriesResult t = new TimeSeriesResultImpl(
							type, 
//							isFinal ? TimeSeriesUtils.unmodifiableTimeSeries(typeEntry.getValue()) : typeEntry.getValue().clone(),
							typeEntry.getValue().clone(),
							Collections.<TimeSeriesData> singletonList(input.get(entry.getKey())));
					prelimMap.get(type).add(t);
				}
			}
			for (ResultType type : requestedResults) {
				results.put((AggregationType) type, new EvaluationResultImpl(prelimMap.get(type), type));
			}
		}				
		return (Map) Collections.unmodifiableMap(results);
	}
	
	@Override
	protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
		final long t = dataPoint.getTimestamp();
		final Map<Integer,SampledValue> elements = dataPoint.getElements();
		AggregationType type;
		for (int idx=0;idx<inputSize;idx++) {
			// time series specific states
			final SampledValue current = dataPoint.getElement(idx);
			if (current == null)
				continue;
			final SampledValue previous = lastValues.get(idx);
			final Map<AggregationType, Float> intBuffer = integrationBuffer.get(idx);
			final Map<AggregationType, Long> lengthBuffer = intervalBuffer.get(idx);
			final Map<AggregationType, Boolean> nanBuffer = this.nanBuffer.get(idx);
			final Map<AggregationType, FloatTimeSeries> values = this.values.get(idx);
//				final Map<AggregationType, Float> lastValidValues = this.lastValidValues.get(idx);
			
			for (int typeKey=0; typeKey<requestedResults.size();typeKey++) {
				type = (AggregationType) requestedResults.get(typeKey);
				// fill buffer
				if (previous != null) {
					if (intBuffer.containsKey(type)) {
						final float result = integrateInternal(previous, previous, current, current, modes[idx]);
						final boolean ok = !Float.isNaN(result);
						if (ok) {
							intBuffer.put(type, intBuffer.get(type) + result);
							lengthBuffer.put(type, lengthBuffer.get(type) + current.getTimestamp()-previous.getTimestamp());
							nanBuffer.remove(type);
						} 
					} else {
						final SampledValue start = lastValues.get(typeKey + inputSize);
						final float result;
						if (start != null) {
							result = integrateInternal(previous, start, current, current, modes[idx]);
						} else {
							result = 0;
						}
						final boolean ok = !Float.isNaN(result);
						if (ok) {
							intBuffer.put(type, result);
							lengthBuffer.put(type, start != null ? current.getTimestamp()-start.getTimestamp() : 0);
						} else {
							nanBuffer.put(type, true);
						}
						
					}
				}
				if (elements.containsKey(typeKey + inputSize)) {
					Float value = intBuffer.remove(type);
					final Long duration = lengthBuffer.remove(type);
					final FloatTimeSeries timeSeries = values.get(type);
					final long timeoffset = type.getDurationMs();
					if (value != null) {
						if (duration != null && duration > 0)
							value = value/duration;
						timeSeries.addValue(new SampledValue(new FloatValue(value), t-timeoffset, Quality.GOOD));
					} else if (nanBuffer.remove(type) != null) {
						timeSeries.addValue(new SampledValue(FloatValue.NAN, t-timeoffset, Quality.BAD));
					} 
				} 
			}
//					if (current.getQuality() == Quality.GOOD)
//						lastValidValues.put(type, current);
			lastValues.put(idx, current);
		}
		for (int typeKey=0; typeKey<requestedResults.size();typeKey++) {
			if (elements.containsKey(typeKey + inputSize)) {
				lastValues.put(typeKey+inputSize, elements.get(typeKey+inputSize));
			}
		}
	} 
	
	static final float integrateInternal(final SampledValue previous, SampledValue start, final SampledValue end, final SampledValue next, final InterpolationMode mode) {
		if (start == null || end == null || previous == null)
			return Float.NaN;
		if (previous.getTimestamp() < start.getTimestamp())
			start = previous;
		final long startT = start.getTimestamp();
		final long endT = end.getTimestamp();
		if (startT == endT)
			return 0;
		if (endT < startT)
			throw new IllegalArgumentException("Interval boundaries interchanged");
		final float p;
		final float n;
		if (mode == null)
			throw new NullPointerException("Interpolation mode is null, integration not possible");
		switch (mode) {
		case STEPS:
			return previous.getValue().getFloatValue() * (endT-startT);
		case LINEAR:
			p = previous.getValue().getFloatValue();
			n = end.getValue().getFloatValue();
			return (endT-startT)*(p+(n-p)*(startT+endT-2*previous.getTimestamp())/2/(next.getTimestamp()-previous.getTimestamp()));
		case NEAREST:
			p = previous.getValue().getFloatValue();
			n = next.getValue().getFloatValue();
			Objects.requireNonNull(previous);
			Objects.requireNonNull(next);
			final long boundary = (next.getTimestamp() + previous.getTimestamp())/2;
			if (boundary <= startT)
				return n*(endT-startT);
			if (boundary >= endT)
				return p*(endT-startT);
			return p*(boundary-startT) + n*(endT-boundary);
		default:
			return Float.NaN;
		}
	}

	
	private static class StandardTimeSeries implements ReadOnlyTimeSeries {
		
		private final AggregationType agg;
		
		public StandardTimeSeries(AggregationType agg) {
			this.agg = agg;
		}

		@Override
		public InterpolationMode getInterpolationMode() {
			return InterpolationMode.STEPS;
		}
		
		@Override
		public Iterator<SampledValue> iterator(long startTime, long endTime) {
			return new StandardIntervalIterator(agg, startTime, endTime);
		}
		
		@Override
		public Iterator<SampledValue> iterator() {
			return iterator(Long.MIN_VALUE, Long.MAX_VALUE);
		}

		@Override
		public SampledValue getValue(long time) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SampledValue getNextValue(long time) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SampledValue getPreviousValue(long time) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<SampledValue> getValues(long startTime) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<SampledValue> getValues(long startTime, long endTime) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty(long startTime, long endTime) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int size(long startTime, long endTime) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Long getTimeOfLatestEntry() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		
	}
 	
	
}
