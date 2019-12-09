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
package de.iwes.timeseries.electricitymeter.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

public class ElectricityMeterEvalEvaluation extends EvaluationBaseImpl {
	
	private final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources

	private final String id;
	private final List<Float> integralValues;
	private final List<Long> integralSize;
	private final List<Integer> counter;
	private final List<Boolean> isInGap;

	public ElectricityMeterEvalEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
		super(input, requestedResults, configurations, listener, time);
		this.id = "EnergyEvaluation_" + idcounter.getAndIncrement();
		integralValues = initList(size);
		integralSize = initList(size);
		counter = new ArrayList<>(size);
		isInGap = new ArrayList<>(size);
		for (int i =0; i< size; i++) {
			isInGap.add(true);
			counter.add(0);
		}
	}
	
	@Override
	public String id() {
		return id;
	}
	
	private final static <T> List<T> initList(int size) {
		final List<T> list = new ArrayList<>();
		for (int i=0;i<size;i++) {
			list.add(null);
		}
		return list;
	}
	
	public static final double Jms2kWh = (1.0/(3600000.0 * 1000));
	@Override
	protected Map<ResultType, EvaluationResult> getCurrentResults() {
		final boolean isPowerRequested = requestedResults.contains(ElectricityMeterEvalProvider.ENERGY);
		final boolean isCounterRequested = requestedResults.contains(BasicEvaluationProvider.COUNTER);
		final Map<ResultType,EvaluationResult> results = new LinkedHashMap<>();
		final List<SingleEvaluationResult> integralResults = isPowerRequested ? new ArrayList<SingleEvaluationResult>() : null;
		final List<SingleEvaluationResult> counterResults = isCounterRequested ? new ArrayList<SingleEvaluationResult>() : null;
		synchronized (this) {
			for (int idx=0;idx<size;idx++) {
				final TimeSeriesData t= input.get(idx);
				final List<TimeSeriesData> inputData = Collections.singletonList(t);
				
				if((isPowerRequested)&&(integralValues.get(idx) != null)) {
					Float itgV = (float) (integralValues.get(idx) * Jms2kWh);
					final SingleEvaluationResult itgResult = new SingleValueResultImpl<>(ElectricityMeterEvalProvider.ENERGY, itgV, inputData);
					integralResults.add(itgResult);
				}
				
				if (isCounterRequested) {
					Integer cntV = counter.get(idx);
					if (cntV != null) {
						final SingleEvaluationResult cntResult = new SingleValueResultImpl<>(BasicEvaluationProvider.COUNTER,cntV, inputData);
						counterResults.add(cntResult);
					}
				}
			}
		}
		if (isPowerRequested) 
			results.put(ElectricityMeterEvalProvider.ENERGY, new EvaluationResultImpl(integralResults, ElectricityMeterEvalProvider.ENERGY));
		if (isCounterRequested)
			results.put(BasicEvaluationProvider.COUNTER, new EvaluationResultImpl(counterResults, BasicEvaluationProvider.COUNTER));
		return Collections.unmodifiableMap(results);
	}
	
    @Override
	protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
		SampledValue sv;
		for (Map.Entry<Integer, SampledValue> entry : dataPoint.getElements().entrySet()) {
			final int idx = entry.getKey();
			sv = entry.getValue();
			final boolean quality = sv.getQuality() == Quality.GOOD;
			final float value  = sv.getValue().getFloatValue();
			final long t = sv.getTimestamp();
			if (!quality) {
				isInGap.set(idx, true);
				continue;
			}
			isInGap.set(idx, false);
			final boolean isNaN = Float.isNaN(value);
			if (isNaN)
				continue;
			counter.set(idx, counter.get(idx)+1);
			final SampledValue last = dataPoint.previous(idx);
			if (last == null)
				continue;
			final float itg = integrate(null, last, sv, null, modes[idx]);
			final Float itgOld = integralValues.get(idx);
			final float newVal = itgOld != null ? itgOld + itg : itg;
			integralValues.set(idx, newVal);
			final long diff = t - last.getTimestamp();
			final Long oldSize = integralSize.get(idx);
			final long newSize = oldSize != null ? oldSize+diff : diff;
			integralSize.set(idx, newSize);
		}
	}

/*	static final float integrate(final SampledValue previous, final SampledValue start, final SampledValue end, final SampledValue next, final InterpolationMode mode) {
		if (start == null || end == null)
			return Float.NaN;
		final long startT = start.getTimestamp();
		final long endT = end.getTimestamp();
		if (startT == endT)
			return 0;
		if (endT < startT)
			throw new IllegalArgumentException("Interval boundaries interchanged");
		final float p;
		final float n;
		switch (mode) {
		case STEPS:
			return start.getValue().getFloatValue() * (endT-startT);
		case LINEAR:
			p = start.getValue().getFloatValue();
			n = end.getValue().getFloatValue();
			return (p + (n-p)/2)*(endT-startT);
		case NEAREST:
			p = start.getValue().getFloatValue();
			n = end.getValue().getFloatValue();
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
*/
    
}
