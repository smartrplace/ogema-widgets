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
package de.iwes.timeseries.aggreagtion.provider.test;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.widgets.test.base.WidgetsTestBase;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.timeseries.aggregation.provider.AggregationProvider;
import de.iwes.timeseries.aggregation.provider.AggregationProvider.AggregationType;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.Status.EvaluationStatus;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

// TODO
@ExamReactorStrategy(PerClass.class)
public class AggregationProviderTest extends WidgetsTestBase {
	
	@Inject
	private EvaluationManager evalManager;
	
	@Inject
	private OnlineTimeSeriesCache timeSeriesCache;
	
	@Override
	public Option widgets() {
		return CoreOptions.composite(super.widgets(),
				CoreOptions.mavenBundle("org.ogema.eval", "timeseries-eval-base", widgetsVersion));
	}
	
	private static FloatTimeSeries generatePeriodStepFct(final int stepsPerPeriod, final long stepLength, final int nrPeriods, 
				long t0, float stepSize, float startValue) {
		if (stepLength <= 0 || stepsPerPeriod <= 0 || nrPeriods <= 0)
			throw new IllegalArgumentException();
		final FloatTimeSeries timeseries = new FloatTreeTimeSeries();
		final List<SampledValue> values = new ArrayList<>();
		long t = t0;
		float value;
		for (int i=0;i<nrPeriods;i++) {
			value = startValue;
			for (int k=0;k<stepsPerPeriod;k++) {
				values.add(new SampledValue(new FloatValue(value), t, Quality.GOOD));
				t += stepLength;
				value += stepSize;
			}
		}
		timeseries.addValues(values);
		timeseries.setInterpolationMode(InterpolationMode.STEPS); // override later, if required
		return timeseries;
	}
	
	@Test
	public void aggregationProviderWorksSimple() throws InterruptedException, ExecutionException, TimeoutException {
		final AggregationProvider aggProvider = new AggregationProvider();
		// a periodic step function, period = 60min, steps a 15 min, values 0,10,20,30, duration 2 hours
		final FloatTimeSeries ts1 = generatePeriodStepFct(4, 15 * 60 * 1000, 2, 0, 10, 0);
		ts1.addValue(new SampledValue(FloatValue.NAN, 2 * 60 * 60 * 1000, Quality.BAD)); // marks the end
		final TimeSeriesData dataImpl = new TimeSeriesDataImpl(ts1, "test", "test", null);
		final List<EvaluationInput> input = Collections.singletonList(new EvaluationInputImpl(Collections.singletonList(dataImpl)));
		final AggregationType type15Min = new AggregationType(15, ChronoUnit.MINUTES);
		final AggregationType type1H = new AggregationType(1, ChronoUnit.HOURS);
//		final EvaluationInstance eval  = aggProvider.newEvaluation(input, Arrays.asList(type15Min, type1H), 
//				Collections.<ConfigurationInstance> emptyList());
		final EvaluationInstance eval  = evalManager.newEvaluation(aggProvider, input, 
				Arrays.asList(type15Min, type1H), Collections.<ConfigurationInstance> emptyList());
		Assert.assertFalse("Offline evaluation expected",eval.isOnlineEvaluation());
		final Status status = eval.get(30, TimeUnit.SECONDS);
		Assert.assertNotNull(status);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult>results = eval.getResults();
		Assert.assertEquals(2, results.size());
		final EvaluationResult res15M = results.get(type15Min);
		final EvaluationResult res1H = results.get(type1H);
		Assert.assertNotNull(res15M);
		Assert.assertNotNull(res1H);
		final List<SingleEvaluationResult> singleRes15M = res15M.getResults();
		Assert.assertEquals(1, singleRes15M.size());
		Assert.assertTrue(singleRes15M.get(0) instanceof TimeSeriesResult); 
		final ReadOnlyTimeSeries resultTS15M = ((TimeSeriesResult) singleRes15M.get(0)).getValue();
		
		Assert.assertEquals("Aggregated time series contains unexpected number of values",8, resultTS15M.size());
		int cnt = 0;
		for (SampledValue sv : resultTS15M.getValues(Long.MIN_VALUE)) {
			Assert.assertEquals("Wrong value in aggregated 15 min timeseries", (cnt++ % 4) * 10, sv.getValue().getFloatValue(), 0.5F);
		}
		final List<SingleEvaluationResult> singleRes1H = res1H.getResults();
		Assert.assertEquals(1, singleRes1H.size());
		Assert.assertTrue(singleRes1H.get(0) instanceof TimeSeriesResult); 
		final ReadOnlyTimeSeries resultTS1H = ((TimeSeriesResult) singleRes1H.get(0)).getValue();
		
		Assert.assertEquals("Aggregated time series contains unexpected number of values",2, resultTS1H.size());
		for (SampledValue sv : resultTS1H.getValues(Long.MIN_VALUE)) {
			Assert.assertEquals("Wrong value in aggregated 1H timeseries",15, sv.getValue().getFloatValue(), 0.5F);
		}
		
	}
	
	@Test
	public void aggregationProviderUpsamplingWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final AggregationProvider aggProvider = new AggregationProvider();
		// a periodic step function, period = 60min, steps a 15 min, values 0,10,20,30, duration 2 hours
		final FloatTimeSeries ts1 = generatePeriodStepFct(4, 15 * 60 * 1000, 2, 0, 10, 0);
		ts1.addValue(new SampledValue(FloatValue.NAN, 2 * 60 * 60 * 1000, Quality.BAD)); // marks the end
		final TimeSeriesData dataImpl = new TimeSeriesDataImpl(ts1, "test", "test", null);
		final List<EvaluationInput> input = Collections.singletonList(new EvaluationInputImpl(Collections.singletonList(dataImpl)));
		final AggregationType type1Min = new AggregationType(1, ChronoUnit.MINUTES);
//		final EvaluationInstance eval  = aggProvider.newEvaluation(input, Arrays.asList(type1Min), 
//				Collections.<ConfigurationInstance> emptyList());
		final EvaluationInstance eval  = evalManager.newEvaluation(aggProvider, input, 
				Arrays.asList(type1Min), Collections.<ConfigurationInstance> emptyList());
		Assert.assertFalse("Offline evaluation expected",eval.isOnlineEvaluation());
		final Status status = eval.get(30, TimeUnit.SECONDS);
		Assert.assertNotNull(status);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult>results = eval.getResults();
		Assert.assertEquals(1, results.size());
		final EvaluationResult res1M = results.get(type1Min);
		Assert.assertNotNull(res1M);
		
		final List<SingleEvaluationResult> singleRes1M = res1M.getResults();
		Assert.assertEquals(1, singleRes1M.size());
		Assert.assertTrue(singleRes1M.get(0) instanceof TimeSeriesResult); 
		final ReadOnlyTimeSeries resultTS1M = ((TimeSeriesResult) singleRes1M.get(0)).getValue();
		Assert.assertEquals("Aggregated time series contains unexpected number of values",120, resultTS1M.size());
		int cnt = 0;
		for (SampledValue sv : resultTS1M.getValues(Long.MIN_VALUE)) {
			Assert.assertEquals("Wrong value in aggregated 1 min timeseries", ((cnt++/15) % 4) * 10, sv.getValue().getFloatValue(), 0.5F);
		}
		
	}
	
	@Test
	public void aggregationProviderWorksOffline() throws InterruptedException, ExecutionException, TimeoutException {
		final AggregationProvider aggProvider = new AggregationProvider();
		// a periodic step function, period = 15min, steps a 5 min, values 10,20,30, duration 1 week
		final FloatTimeSeries ts1 = generatePeriodStepFct(3, 5 * 60 * 1000, 7 * 24 * 4, 0, 10, 10);
		// FIXME
		System.out.println(" oo time series generated, with " + ts1.size() + " values");
		final float avg = 
				ValueResourceUtils.getAverage(ts1, ts1.getNextValue(Long.MIN_VALUE).getTimestamp(), ts1.getPreviousValue(Long.MAX_VALUE).getTimestamp());
		final int size = ts1.size();
		Assert.assertEquals("Value resource utils error", 20, avg, 0.2F);
		TimeSeriesData dataImpl = new TimeSeriesDataImpl(ts1, "test", "test", null);
		final List<EvaluationInput> input = new ArrayList<>();
		EvaluationInput in1 = new EvaluationInputImpl(Collections.singletonList(dataImpl));
		input.add(in1);
//		EvaluationInstance eval  = aggProvider.newEvaluation(input, aggProvider.resultTypes(), Collections.<ConfigurationInstance> emptyList());
		final EvaluationInstance eval  = evalManager.newEvaluation(aggProvider, input, 
				aggProvider.resultTypes(), Collections.<ConfigurationInstance> emptyList());
		Assert.assertFalse("Offline evaluation expected",eval.isOnlineEvaluation());
		final Status status = eval.get(60, TimeUnit.SECONDS);
		Assert.assertNotNull(status);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult> results = eval.getResults();
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		aggProvider.resultTypes().stream().forEach(type -> Assert.assertTrue("Missing result type " + type, results.containsKey(type)));
		final AggregationType maxDuration = new AggregationType(1, ChronoUnit.WEEKS);
		results.values().stream()
			.filter(value -> ((AggregationType) value.getResultType()).compareTo(maxDuration) <= 0) // skip months and years
			.flatMap(evalR -> evalR.getResults().stream())
			.forEach(singleResult -> {
				Assert.assertTrue(singleResult instanceof TimeSeriesResult);
				Assert.assertTrue(singleResult.getResultType() instanceof AggregationType);
				final ReadOnlyTimeSeries aggregatedTS = ((TimeSeriesResult) singleResult).getValue();
				System.out.println(" oo Aggregated time series for " + singleResult.getResultType() + " has " + aggregatedTS.size() + " values");
				Assert.assertFalse("Empty aggregated time series for type " + singleResult.getResultType(), aggregatedTS.isEmpty());
				final AggregationType type = (AggregationType) singleResult.getResultType();
				final long duration = type.getDuration();
				final TemporalUnit unit = type.getUnit();
				final int expectedNrPoints;
				SampledValue sv;
				if (duration == 1 && unit == ChronoUnit.MINUTES) { 
					expectedNrPoints = 5 * size;
					final Iterator<SampledValue> it = aggregatedTS.iterator();
					for (int i=0;i<60;i++) {
						Assert.assertTrue("Timeseries seems to lack points", it.hasNext());
						sv = it.next();
						int j = i % 15;
						float expectedValue;
						if (j < 5) 
							expectedValue = 10;
						else if (j < 10)
							expectedValue = 20;
						else 
							expectedValue = 30;
						Assert.assertEquals("Unexpected value in minutes aggregation", expectedValue, sv.getValue().getFloatValue(), 0.2F);
					}
				}
				else if (duration == 5 && unit == ChronoUnit.MINUTES) {
					expectedNrPoints = size;
					final Iterator<SampledValue> it = aggregatedTS.iterator();
					for (int i=0;i<12;i++) {
						Assert.assertTrue("Timeseries seems to lack points", it.hasNext());
						sv = it.next();
						int j = i % 3;
						float expectedValue;
						if (j == 1) 
							expectedValue = 10;
						else if (j == 2)
							expectedValue = 20;
						else 
							expectedValue = 30;
						Assert.assertEquals("Unexpected value in 5 minutes aggregation", expectedValue, sv.getValue().getFloatValue(), 0.2F);
					}
				}
				else if (duration == 15 && unit == ChronoUnit.MINUTES) {
					expectedNrPoints = size/3;
					final Iterator<SampledValue> it = aggregatedTS.iterator();
					for (int i=0;i<4;i++) {
						Assert.assertTrue("Timeseries seems to lack points", it.hasNext());
						sv = it.next();
						Assert.assertEquals("Unexpected value in 15 minutes aggregation", 20, sv.getValue().getFloatValue(), 0.2F);
					}
					
				}
				else if (duration == 1 && unit == ChronoUnit.HOURS) {
					expectedNrPoints = size/12;
					final Iterator<SampledValue> it = aggregatedTS.iterator();
					for (int i=0;i<4;i++) {
						Assert.assertTrue("Timeseries seems to lack points", it.hasNext());
						sv = it.next();
						Assert.assertEquals("Unexpected value in hour aggregation", 20, sv.getValue().getFloatValue(), 0.2F);
					}
				}
				else if (duration == 1 && unit == ChronoUnit.DAYS)
					expectedNrPoints = size/(24*12);
				else if (duration == 1 && unit == ChronoUnit.WEEKS)
					expectedNrPoints = size/(7*24*12);
//				else if (duration == 1 && unit == ChronoUnit.MONTHS)
//					expectedNrPoints = size/(30*24*12);
				else
					return;
				// FIXME
				System.out.println("   tt expecting " + expectedNrPoints + " points in aggregation, got " + aggregatedTS.size());
				Assert.assertEquals("Unexpected number of data points in aggregated time series", expectedNrPoints, aggregatedTS.size(), Math.max(1, expectedNrPoints/1000));
				
			});
	}
	
	
	
}