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
package de.iwes.timeseries.correlation.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIterator;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIteratorBuilder;
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

public class CorrelationEvaluation extends EvaluationBaseImpl {
	
	private final String id;
	private final static AtomicLong counter = new AtomicLong(0); // TODO initialize from existing stored eval resources
	private final List<TimeSeriesData> input1;
	private final List<TimeSeriesData> input2;
	private final int inputSize1;
	private final int inputSize2;
	// state variables
	private final FloatTimeSeries[] timeSeriesBuffer;
	private final float[] integralsValues;
	private final long[] integralsSize;
	private final boolean[] isInGap;

	public CorrelationEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
		super(input, requestedResults, configurations, listener, time);
		this.id = "CorrelationEvaluation_" + counter.getAndIncrement();
		Iterator<EvaluationInput> inputIt = input.iterator();
		final List<TimeSeriesData> list = new ArrayList<>();
		list.addAll(inputIt.next().getInputData());
		this.input1 = Collections.unmodifiableList(list);
		final List<TimeSeriesData> list2 = new ArrayList<>();
		if (inputIt.hasNext())
			list2.addAll(inputIt.next().getInputData());
		this.input2 = Collections.unmodifiableList(list2);
		this.inputSize1 = input1.size();
		this.inputSize2 = input2.size();
		timeSeriesBuffer = new FloatTimeSeries[inputSize1+inputSize2];
		integralsValues = new float[inputSize1+inputSize2];
		integralsSize = new long[inputSize1+inputSize2];
		isInGap = new boolean[inputSize1+inputSize2];
		
		for (int i=0;i<inputSize1;i++) {
			FloatTimeSeries fts = new FloatTreeTimeSeries();
			fts.setInterpolationMode(modes[i]);
			timeSeriesBuffer[i] = fts;
			isInGap[i] = true;
		}
		for (int j=0;j<inputSize2;j++) {
			FloatTimeSeries fts = new FloatTreeTimeSeries();
			fts.setInterpolationMode(modes[j+inputSize1]);
			timeSeriesBuffer[j+inputSize1] = fts;
			isInGap[j+inputSize1] = true;
		}
	}
	
	@Override
	public String id() {
		return id;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<ResultType, EvaluationResult> getCurrentResults() {
		final float[] averages = new float[inputSize1+inputSize2];
		final double[][] sigma_XY = new double[inputSize1][inputSize2];
		final double[] sigma_XX = new double[inputSize1+inputSize2];
		synchronized (this) {
			for (int i=0;i<inputSize1;i++) {
				float val = integralsValues[i];
				if (Float.isNaN(val)) {
					averages[i] = Float.NaN;
					continue;
				}
				long duration = integralsSize[i];
				final float av = val/duration;
				averages[i] = av;
			}
			for (int j=0;j<inputSize2;j++) {
				final int k = j+inputSize1;
				float val = integralsValues[k];
				if (Float.isNaN(val)) {
					averages[k] = Float.NaN;
					continue;
				}
				long duration = integralsSize[k];
				final float av = val/duration;
				averages[k] = av;
			}
			if (requestedResults.contains(CorrelationProvider.STD_DEV) || requestedResults.contains(CorrelationProvider.CORRELATION_TYPE))
				getVariances(timeSeriesBuffer, averages, inputSize1, inputSize2, sigma_XY, sigma_XX);
		}
		final List<SingleEvaluationResult> correlations = new ArrayList<>();
		final List<SingleEvaluationResult> variances = new ArrayList<>();
		final List<SingleEvaluationResult> averagesList = new ArrayList<>();
		for (int i=0;i<inputSize1;i++) {
			if (requestedResults.contains(CorrelationProvider.STD_DEV)) {
				final float var = (float) Math.sqrt(sigma_XX[i]);
				final SingleEvaluationResult var_i = new SingleValueResultImpl<Float>(CorrelationProvider.STD_DEV, var, Collections.singletonList(input1.get(i)));
				variances.add(var_i);
			}
			if (requestedResults.contains(BasicEvaluationProvider.AVERAGE)) {
				final SingleEvaluationResult av_i = new SingleValueResultImpl<Float>(BasicEvaluationProvider.AVERAGE, averages[i], Collections.singletonList(input1.get(i)));
				averagesList.add(av_i);
			}
			if (requestedResults.contains(CorrelationProvider.CORRELATION_TYPE)) {
				for (int j=0;j<inputSize2;j++) {
					final float corr_ij = (float) (sigma_XY[i][j] / Math.sqrt(sigma_XX[i] * sigma_XX[j+inputSize1]));
					final SingleEvaluationResult crr_ij = new SingleValueResultImpl<Float>(CorrelationProvider.CORRELATION_TYPE, corr_ij, 
							Collections.unmodifiableList(Arrays.asList(input1.get(i),input2.get(j))));
					correlations.add(crr_ij);
				}
			}
		}
		for (int j=0;j<inputSize2;j++) {
			if (requestedResults.contains(CorrelationProvider.STD_DEV)) {
				final float var = (float) Math.sqrt(sigma_XX[j+inputSize1]);
				final SingleEvaluationResult var_j = new SingleValueResultImpl<Float>(CorrelationProvider.STD_DEV, var, Collections.singletonList(input2.get(j)));
				variances.add(var_j);
			}
			if (requestedResults.contains(BasicEvaluationProvider.AVERAGE)) {
				final SingleEvaluationResult av_j = new SingleValueResultImpl<Float>(BasicEvaluationProvider.AVERAGE, averages[j+inputSize1], Collections.singletonList(input2.get(j)));
				averagesList.add(av_j);
			}
		}
		final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
		if (requestedResults.contains(BasicEvaluationProvider.AVERAGE)) 
			results.put(BasicEvaluationProvider.AVERAGE, new EvaluationResultImpl(averagesList, BasicEvaluationProvider.AVERAGE));
		if (requestedResults.contains(CorrelationProvider.STD_DEV)) 
			results.put(CorrelationProvider.STD_DEV, new EvaluationResultImpl(variances, CorrelationProvider.STD_DEV));
		if (requestedResults.contains(CorrelationProvider.CORRELATION_TYPE)) 
			results.put(CorrelationProvider.CORRELATION_TYPE, new EvaluationResultImpl(correlations, CorrelationProvider.CORRELATION_TYPE));
		return (Map) Collections.unmodifiableMap(results);
	}
	
	@Override
	protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
		// exact calc
		final Map<Integer,SampledValue> elements = dataPoint.getElements();
		SampledValue sv;
		SampledValue previous;
		for (Map.Entry<Integer, SampledValue> entry : elements.entrySet()) {
			final int idx = entry.getKey();
			sv = entry.getValue();
			timeSeriesBuffer[idx].addValue(sv);
			final boolean gap = isInGap[idx];
			if (!gap) {
				previous = dataPoint.previous(idx);
				final float val = integrate(previous, previous, sv, sv, modes[idx]);
				if (!Float.isNaN(val)) {
					integralsValues[idx]=integralsValues[idx]+val;
					long diff = sv.getTimestamp() - previous.getTimestamp();
					integralsSize[idx] = integralsSize[idx]+diff;
				}
			}
			isInGap[idx] = sv.getQuality() == Quality.BAD || Float.isNaN(sv.getValue().getFloatValue());
		}
	}
	
	private static final double integrateSquared(final double start, final double end, final long diff, final InterpolationMode mode) {
		switch (mode) {
		case STEPS:
			return start * start * diff;
		case LINEAR:
			return (start*start + end*end + end+start)*diff/3;
		default:
			return Double.NaN;
		}
	}
	
	private static final double integrateProduct(final long interval,
			final double v1Start, final double v1End, final InterpolationMode mode1,
			final double v2Start, final double v2End, final InterpolationMode mode2) {
		if (mode1 == null || mode2 == null || mode1 == InterpolationMode.NONE || mode2 == InterpolationMode.NONE || mode1 == InterpolationMode.NEAREST ||mode2 == InterpolationMode.NEAREST)
			return Float.NaN;
		if (mode2 == InterpolationMode.STEPS && mode1 != InterpolationMode.STEPS)
			return integrateProduct(interval, v2Start, v2End, mode2, v1Start, v1End, mode1);
		if (mode1 == InterpolationMode.STEPS && mode2 == InterpolationMode.STEPS) 
			return v1Start * v2Start * interval;
		if (mode1 == InterpolationMode.STEPS && mode2 == InterpolationMode.LINEAR) {
			return v1Start * interval /2 * (v2End + v2Start);
		}
		if (mode1 == InterpolationMode.LINEAR && mode2 == InterpolationMode.LINEAR) 
			return interval/6*(v1Start*v2End + v2Start*v1End + 2*v1Start*v2Start + 2*v1End*v2End);
		throw new RuntimeException("case forgotten?");
	}
	
	// writes results to result variables
	private final static void getVariances(
			final ReadOnlyTimeSeries[] timeSeries,
			final float[] averages,
			final int size1, 
			final int size2,
			final double[][] result_sigma_XY,
			final double[] result_sigma_XX) {
		
		final double[][] integralValues = new double[size1][size2];
		final double[] integralValuesOwn = new double[size1+size2];
		final double[] previousValues = new double[size1+size2];
		final long[][] integralSizes = new long[size1][size2];
		final long[] integralSizeOwn = new long[size1+size2];
		final InterpolationMode[] modes = new InterpolationMode[size1+size2];
//		final long[][] previousTimestamps = new long[size][size];
		long previousTimestamp = Long.MIN_VALUE;
		final boolean[][] isInGap = new boolean[size1][size2];
		final boolean[] isInGapOwn = new boolean[size1+size2];
		for (int i=0;i<size1;i++) {
			for (int j=0;j<size2;j++) {
				isInGap[i][j]=true;
				if (i==0) {
					modes[size1+j] = timeSeries[size1+j].getInterpolationMode();
					isInGapOwn[j+size1] = true;
				}
			}
			isInGapOwn[i]=true;
			modes[i] = timeSeries[i].getInterpolationMode();
		}
		final List<Iterator<SampledValue>> iterators = new ArrayList<>();
		for (int i=0;i<size1+size2;i++) {
			iterators.add(timeSeries[i].iterator());
		}
		final MultiTimeSeriesIterator it = MultiTimeSeriesIteratorBuilder.newBuilder(iterators)
				.setIndividualInterpolationModes(Arrays.asList(modes))
				.build();
		while (it.hasNext()) {
			final SampledValueDataPoint dataPoint = it.next();
			final long t = dataPoint.getTimestamp();
			SampledValue sv1;
			SampledValue sv2;
			for (int i=0;i<size1;i++) {
				sv1 = dataPoint.getElement(i);
				final boolean ok1 = sv1 !=null && sv1.getQuality() == Quality.GOOD;
				final double f1 = ok1 ? sv1.getValue().getDoubleValue() : Double.NaN;
				final double av1 = averages[i];
				for (int j=0;j<size2;j++) {
					sv2 = dataPoint.getElement(j+size1);
					final boolean ok2 = sv2 != null && sv2.getQuality() == Quality.GOOD;
					boolean isOk = ok1 && ok2;
					final boolean wasOk = !isInGap[i][j];
					final double f2 = ok2 ? sv2.getValue().getDoubleValue() : Double.NaN;
					final double av2 = averages[j+size1];
					if (sv1 != null && sv2 != null && wasOk) {
						// FIXME this does not work properly for two linear functions!
//						final double itg = integrate((previousValues[i]-av1)*(previousValues[j+size1]-av2),(f1-av1)*(f2-av2), t-previousTimestamp, getMode(modes[i], modes[j+size1])); //?
						final double itg = integrateProduct(t-previousTimestamp, previousValues[i]-av1, f1-av1, modes[i], 
								previousValues[j+size1]-av2, f2-av2, modes[j+size1]); 
						if (!Double.isNaN(itg)) { 
							integralValues[i][j] = integralValues[i][j] + itg;
							integralSizes[i][j] = integralSizes[i][j] + t-previousTimestamp;
						}
						else
							isOk = false;
					}
					isInGap[i][j] = !isOk;
					if (i==size1-1) {
						if (sv2 !=null && !isInGapOwn[j+size1]) {
							double int2 = integrateSquared(previousValues[j+size1]-av2, f2-av2, t-previousTimestamp, modes[j+size1]);
							if (!Double.isNaN(int2)) {
								integralValuesOwn[j+size1] = integralValuesOwn[j+size1]+ int2;
								integralSizeOwn[j+size1] = integralSizeOwn[j+size1] + t-previousTimestamp;
							}
						}
						previousValues[j+size1] = f2;
						isInGapOwn[j+size1] = !ok2;
					}
				}
				if (sv1 != null && !isInGapOwn[i]) { // TODO check
					double int1 = integrateSquared(previousValues[i]-av1, f1-av1, t-previousTimestamp, modes[i]);
					if (!Double.isNaN(int1)) {
						integralValuesOwn[i] = integralValuesOwn[i]+ int1;
						integralSizeOwn[i] = integralSizeOwn[i] + t-previousTimestamp;
					}
				}
 				previousValues[i] = f1;
 				isInGapOwn[i] = !ok1;
			}
			previousTimestamp = t;
		}
		for (int i=0;i<size1;i++) {
			for (int j=0;j<size2;j++) {
				result_sigma_XY[i][j] = integralValues[i][j]/integralSizes[i][j];
				if (i==0) {
					result_sigma_XX[j+size1] = integralValuesOwn[j+size1]/integralSizeOwn[j+size1];
				}
			}
			result_sigma_XX[i] = integralValuesOwn[i]/integralSizeOwn[i];
		}
	}

	
}
