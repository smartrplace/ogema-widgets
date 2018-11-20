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
package de.iwes.timeseries.correlation.provider.test;

import java.util.Arrays;
import java.util.Collections;
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
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.widgets.test.base.WidgetsTestBase;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.timeseries.correlation.provider.CorrelationProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.Status.EvaluationStatus;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;

@ExamReactorStrategy(PerClass.class)
public class CorrelationProviderTest extends WidgetsTestBase {
	
	@Inject
	private EvaluationManager evalManager;
	
	@Override
	public Option widgets() {
		return CoreOptions.composite(super.widgets(),
				CoreOptions.mavenBundle("org.ogema.eval", "timeseries-eval-base", widgetsVersion));
	}
	
	// average and std deviation of step function (0-10: 10, 10-20: 20)
	@Test
	public void varianceWorksForSimpleStepTimeseries() throws InterruptedException, ExecutionException, TimeoutException {
		final FloatTimeSeries t = new FloatTreeTimeSeries();
		t.setInterpolationMode(InterpolationMode.STEPS);
		t.addValue(0, new FloatValue(10));
		t.addValue(10, new FloatValue(20));
		t.addValue(new SampledValue(FloatValue.NAN, 20, Quality.BAD)); // marks the end of the time series
		final CorrelationProvider provider = new CorrelationProvider();
		final List<EvaluationInput> inputs = Collections.<EvaluationInput> singletonList(
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(t, "", "", null))));
//		final EvaluationInstance instance = provider.newEvaluation(inputs, Arrays.asList(BasicEvaluationProvider.AVERAGE, CorrelationProvider.STD_DEV), Collections.<ConfigurationInstance> emptyList());
		final EvaluationInstance instance  = evalManager.newEvaluation(provider, inputs, 
				Arrays.asList(BasicEvaluationProvider.AVERAGE, CorrelationProvider.STD_DEV), Collections.<ConfigurationInstance> emptyList());

		final Status status = instance.get(5, TimeUnit.SECONDS);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		Map<ResultType, EvaluationResult> results = instance.getResults();
		Assert.assertTrue("Result type missing",results.keySet().contains(BasicEvaluationProvider.AVERAGE));
		Assert.assertTrue("Result type missing",results.keySet().contains(CorrelationProvider.STD_DEV));
		@SuppressWarnings("unchecked")
		final SingleValueResult<Float> average = (SingleValueResult<Float>) results.get(BasicEvaluationProvider.AVERAGE).getResults().iterator().next();
		Assert.assertEquals("Unexpected average value", 15, average.getValue().floatValue(), 0.5F);
		@SuppressWarnings("unchecked")
		final SingleValueResult<Float> variance = (SingleValueResult<Float>) results.get(CorrelationProvider.STD_DEV).getResults().iterator().next();
		Assert.assertEquals("Unexpected variance", 5, variance.getValue().floatValue(), 0.2F);
	}

	// average and std deviation of linear function t->t on interval [0,20]
	@Test
	public void varianceWorksForSimpleLinearTimeseries() throws InterruptedException, ExecutionException, TimeoutException {
		final FloatTimeSeries t = new FloatTreeTimeSeries();
		t.setInterpolationMode(InterpolationMode.LINEAR);
		t.addValue(0, new FloatValue(0));
		t.addValue(10, new FloatValue(10));
		t.addValue(20, new FloatValue(20));
		t.addValue(new SampledValue(FloatValue.NAN, 21, Quality.BAD)); // marks the end of the time series
		final CorrelationProvider provider = new CorrelationProvider();
		final List<EvaluationInput> inputs = Collections.<EvaluationInput> singletonList(
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(t, "", "", null))));
//		final EvaluationInstance instance = provider.newEvaluation(inputs, Arrays.asList(BasicEvaluationProvider.AVERAGE, CorrelationProvider.STD_DEV), Collections.<ConfigurationInstance> emptyList());
		final EvaluationInstance instance  = evalManager.newEvaluation(provider, inputs, 
				Arrays.asList(BasicEvaluationProvider.AVERAGE, CorrelationProvider.STD_DEV), Collections.<ConfigurationInstance> emptyList());
		final Status status = instance.get(5, TimeUnit.SECONDS);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		Map<ResultType, EvaluationResult> results = instance.getResults();
		Assert.assertTrue("Result type missing",results.keySet().contains(BasicEvaluationProvider.AVERAGE));
		Assert.assertTrue("Result type missing",results.keySet().contains(CorrelationProvider.STD_DEV));
		@SuppressWarnings("unchecked")
		final SingleValueResult<Float> average = (SingleValueResult<Float>) results.get(BasicEvaluationProvider.AVERAGE).getResults().iterator().next();
		Assert.assertEquals("Unexpected average value", 10, average.getValue().floatValue(), 0.5F);
		@SuppressWarnings("unchecked")
		final SingleValueResult<Float> variance = (SingleValueResult<Float>) results.get(CorrelationProvider.STD_DEV).getResults().iterator().next();
		Assert.assertEquals("Unexpected variance", 10/Math.sqrt(3), variance.getValue().floatValue(), 0.1F);
	}
	
	// correlation of step function (0-10: 10, 10-20: 20) and linear function t->t on the interval [0,20]
	@Test
	public void correlationWorksForSimpleTimeseries() throws InterruptedException, ExecutionException, TimeoutException {
		final FloatTimeSeries t1 = new FloatTreeTimeSeries();
		t1.setInterpolationMode(InterpolationMode.STEPS);
		t1.addValue(0, new FloatValue(10));
		t1.addValue(10, new FloatValue(20));
		t1.addValue(new SampledValue(FloatValue.NAN, 20, Quality.BAD)); // marks the end of the time series
		final FloatTimeSeries t2 = new FloatTreeTimeSeries();
		t2.setInterpolationMode(InterpolationMode.LINEAR);
		t2.addValue(0, new FloatValue(0));
		t2.addValue(10, new FloatValue(10));
		t2.addValue(19, new FloatValue(19));
		t2.addValue(new SampledValue(FloatValue.NAN, 20, Quality.BAD)); // marks the end of the time series
		final CorrelationProvider provider = new CorrelationProvider();
		final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(t1, "", "", null))), 
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(t2, "", "", null))));
//		final EvaluationInstance instance = provider.newEvaluation(inputs, Arrays.asList(CorrelationProvider.CORRELATION_TYPE), Collections.<ConfigurationInstance> emptyList());
		final EvaluationInstance instance  = evalManager.newEvaluation(provider, inputs, 
				Arrays.asList(CorrelationProvider.CORRELATION_TYPE), Collections.<ConfigurationInstance> emptyList());

		final Status status = instance.get(10, TimeUnit.SECONDS);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		Map<ResultType, EvaluationResult> results = instance.getResults();
		Assert.assertTrue("Result type missing",results.keySet().contains(CorrelationProvider.CORRELATION_TYPE));
		@SuppressWarnings("unchecked")
		final SingleValueResult<Float> variance = (SingleValueResult<Float>) results.get(CorrelationProvider.CORRELATION_TYPE).getResults().iterator().next();
		Assert.assertEquals("Unexpected correlation", Math.sqrt(3)/2, variance.getValue().floatValue(), 0.1F);
	}
	
}