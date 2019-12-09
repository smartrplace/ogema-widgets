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
package de.iwes.timeseries.room.provider.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.ogema.core.channelmanager.measurements.BooleanValue;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.timeseries.implementations.TreeTimeSeries;
import org.ogema.tools.widgets.test.base.WidgetsTestBase;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

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
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.roomeval.provider.RoomBaseEvalProvider;

@Ignore // cannot activate this on CI server due to external dependencies; should be useful for local execution nevertheless
@ExamReactorStrategy(PerClass.class)
public class RoomProviderTest extends WidgetsTestBase {
	
	private final static FloatValue ONE = new FloatValue(1);
	private final static long HOUR = 60 * 60 * 1000;
	
	@Inject
	private EvaluationManager evalManager;
	
	@Override
	public Option widgets() {
		return CoreOptions.composite(super.widgets(),
				CoreOptions.mavenBundle("de.iwes.tools", "timeseries-eval-base", widgetsVersion),
				CoreOptions.mavenBundle("org.smartrplace.external", "slotsdb-standalone", widgetsVersion),
				CoreOptions.mavenBundle("org.smartrplace.external", "backup-parser", widgetsVersion),
				CoreOptions.mavenBundle("org.smartrplace.analysis", "server-backup-analysis", widgetsVersion),
				CoreOptions.mavenBundle("de.iwes.tools", "server-timeseries-source", widgetsVersion));
	}
	
	@SuppressWarnings("unchecked")
	public void checkEval(final ReadOnlyTimeSeries windowSensor, final ReadOnlyTimeSeries valvePosition, 
				final float expectedValveHours, final long expectedWindowDuration, final int expectedWindowOpenNr, final float expectedValveWindowOpenHours) throws InterruptedException, ExecutionException, TimeoutException {
		final RoomBaseEvalProvider provider = new RoomBaseEvalProvider();
		// need three input time series, but temp sensor is irrelevant
		final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(new FloatTreeTimeSeries(), "", "", InterpolationMode.LINEAR))),
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(windowSensor, "", "", InterpolationMode.STEPS))),
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(valvePosition, "", "", InterpolationMode.STEPS))));
		final EvaluationInstance instance  = evalManager.newEvaluation(provider, inputs, provider.resultTypes(), Collections.<ConfigurationInstance> emptyList());

		final Status status = instance.get(5, TimeUnit.SECONDS);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult> results = instance.getResults();
		Assert.assertNotNull(results);
		for (ResultType type : provider.resultTypes())
			Assert.assertTrue("Result type missing",results.keySet().contains(type));
		final SingleValueResult<Float> resultVhours = (SingleValueResult<Float>) results.get(RoomBaseEvalProvider.VALVE_HOURS_TOTAL).getResults().iterator().next();
		Assert.assertEquals("Unexpected valve hours", expectedValveHours, resultVhours.getValue().floatValue(), 0.5F);
		final SingleValueResult<Integer> resultWopen = (SingleValueResult<Integer>) results.get(RoomBaseEvalProvider.WINDOWPEN_DURATION_NUM).getResults().iterator().next();
		Assert.assertEquals("Unexpected nr of window opening", expectedWindowOpenNr, resultWopen.getValue().intValue());
		final SingleValueResult<Long> resultWopenDuration = (SingleValueResult<Long>) results.get(RoomBaseEvalProvider.WINDOWPEN_DURATION_AV).getResults().iterator().next();
		Assert.assertEquals("Unexpected average duration of window openings", expectedWindowDuration, resultWopenDuration.getValue().longValue() * resultWopen.getValue().intValue());
		final SingleValueResult<Float> resultVWhours = (SingleValueResult<Float>) results.get(RoomBaseEvalProvider.VALVE_HOURS_WINDOWPEN).getResults().iterator().next();
		Assert.assertEquals("Unexpected valve/window hours", expectedValveWindowOpenHours, resultVWhours.getValue().floatValue(), 0.5F);
	}
	
	@SuppressWarnings("unchecked")
	public void checkEval(final List<ReadOnlyTimeSeries> windowSensors, final List<ReadOnlyTimeSeries> valvePositions, 
				final float expectedValveHours, final long expectedWindowDuration, final int expectedWindowOpenNr, final float expectedValveWindowOpenHours) throws InterruptedException, ExecutionException, TimeoutException {
		final RoomBaseEvalProvider provider = new RoomBaseEvalProvider();
		final List<TimeSeriesData> windowData = new ArrayList<>();
		final List<TimeSeriesData> valveData = new ArrayList<>();
		for (ReadOnlyTimeSeries t: windowSensors)
			windowData.add(new TimeSeriesDataImpl(t, "", "", InterpolationMode.STEPS));
		for (ReadOnlyTimeSeries t: valvePositions)
			valveData.add(new TimeSeriesDataImpl(t, "", "", InterpolationMode.STEPS));
		// need three input time series, but temp sensor is irrelevant
		final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(
				new EvaluationInputImpl(Collections.<TimeSeriesData> singletonList(new TimeSeriesDataImpl(new FloatTreeTimeSeries(), "", "", InterpolationMode.LINEAR))),
				new EvaluationInputImpl(windowData),
				new EvaluationInputImpl(valveData));
		final EvaluationInstance instance  = evalManager.newEvaluation(provider, inputs, provider.resultTypes(), Collections.<ConfigurationInstance> emptyList());

		final Status status = instance.get(5, TimeUnit.SECONDS);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult> results = instance.getResults();
		Assert.assertNotNull(results);
		for (ResultType type : provider.resultTypes())
			Assert.assertTrue("Result type missing",results.keySet().contains(type));
		final SingleValueResult<Float> resultVhours = (SingleValueResult<Float>) results.get(RoomBaseEvalProvider.VALVE_HOURS_TOTAL).getResults().iterator().next();
		Assert.assertEquals("Unexpected valve hours", expectedValveHours, resultVhours.getValue().floatValue(), 0.5F);
		final SingleValueResult<Integer> resultWopen = (SingleValueResult<Integer>) results.get(RoomBaseEvalProvider.WINDOWPEN_DURATION_NUM).getResults().iterator().next();
		Assert.assertEquals("Unexpected nr of window opening", expectedWindowOpenNr, resultWopen.getValue().intValue());
		final SingleValueResult<Long> resultWopenDuration = (SingleValueResult<Long>) results.get(RoomBaseEvalProvider.WINDOWPEN_DURATION_AV).getResults().iterator().next();
		Assert.assertEquals("Unexpected average duration of window openings", expectedWindowDuration, resultWopenDuration.getValue().longValue());
		final SingleValueResult<Float> resultVWhours = (SingleValueResult<Float>) results.get(RoomBaseEvalProvider.VALVE_HOURS_WINDOWPEN).getResults().iterator().next();
		Assert.assertEquals("Unexpected valve/window hours", expectedValveWindowOpenHours, resultVWhours.getValue().floatValue(), 0.5F);
	}
	
	@Test
	public void windowClosedWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(3 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 1, 0, 0, 0);
	}
	
	@Test
	public void valveAndWindowConincidentallyWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(1 * HOUR, ONE);
		valve.addValue(4 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 3, 3 * HOUR, 1, 3);
	}
	
	@Test
	public void doubleValveAndWindowConincidentallyWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		windows.addValue(6 * HOUR, BooleanValue.TRUE);
		windows.addValue(9 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(1 * HOUR, ONE);
		valve.addValue(4 * HOUR, FloatValue.ZERO);
		valve.addValue(6 * HOUR, ONE);
		valve.addValue(9 * HOUR, FloatValue.ZERO);		
		checkEval(windows, valve, 6, 6 * HOUR, 2, 6);
	}
	
	@Test
	public void valveInWindowWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(3 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 1, 3 * HOUR, 1, 1);
	}
	
	@Test
	public void valveWindowOverlapWorks1() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(5 * HOUR, FloatValue.ZERO);
		valve.addValue(6 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 3, 3 * HOUR, 1, 2);
	}
	
	@Test
	public void valveWindowOverlapWorks2() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(3 * HOUR, BooleanValue.TRUE);
		windows.addValue(5 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(4 * HOUR, FloatValue.ZERO);
		valve.addValue(6 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 2, 2 * HOUR, 1, 1);
	}
	
	@Test
	public void windowInValveWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(3 * HOUR, BooleanValue.TRUE);
		windows.addValue(5 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(6 * HOUR, FloatValue.ZERO);
		valve.addValue(7 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 4, 2 * HOUR, 1, 2);
	}
	
	@Test
	public void doubleWindowInValveWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(3 * HOUR, BooleanValue.TRUE);
		windows.addValue(5 * HOUR, BooleanValue.FALSE);
		windows.addValue(9 * HOUR, BooleanValue.TRUE);
		windows.addValue(11 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(6 * HOUR, FloatValue.ZERO);
		valve.addValue(8 * HOUR, ONE);
		valve.addValue(12 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 8, 4 * HOUR, 2, 4);
	}
	
	@Test
	public void doubleValveInWindowWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		windows.addValue(5 * HOUR, BooleanValue.TRUE);
		windows.addValue(8 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(3 * HOUR, FloatValue.ZERO);
		valve.addValue(6 * HOUR, ONE);
		valve.addValue(7 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 2, 6 * HOUR, 2, 2);
	}
	
	@Test
	public void doubleValveWindowOverlapWorks1() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		windows.addValue(7 * HOUR, BooleanValue.TRUE);
		windows.addValue(10 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(5 * HOUR, FloatValue.ZERO);
		valve.addValue(8 * HOUR, ONE);
		valve.addValue(11 * HOUR, FloatValue.ZERO);
		valve.addValue(12 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 6, 6 * HOUR, 2, 4);
	}
	
	@Test
	public void doubleValveWindowOverlapWorks2() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(3 * HOUR, BooleanValue.TRUE);
		windows.addValue(5 * HOUR, BooleanValue.FALSE);
		windows.addValue(7 * HOUR, BooleanValue.TRUE);
		windows.addValue(9 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(4 * HOUR, FloatValue.ZERO);
		valve.addValue(6 * HOUR, ONE);
		valve.addValue(8 * HOUR, FloatValue.ZERO);
		valve.addValue(10 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 4, 4 * HOUR, 2, 2);
	}
	
	@Test
	public void doubleValveWindowOverlapWorksMixed1() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		windows.addValue(8 * HOUR, BooleanValue.TRUE);
		windows.addValue(11 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(5 * HOUR, FloatValue.ZERO);
		valve.addValue(7 * HOUR, ONE);
		valve.addValue(10 * HOUR, FloatValue.ZERO);
		valve.addValue(12 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 6, 6 * HOUR, 2, 4);
	}
	
	@Test
	public void doubleValveWindowOverlapWorksMixed2() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(3 * HOUR, BooleanValue.TRUE);
		windows.addValue(5 * HOUR, BooleanValue.FALSE);
		windows.addValue(6 * HOUR, BooleanValue.TRUE);
		windows.addValue(8 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve = new FloatTreeTimeSeries();
		valve.addValue(0, FloatValue.ZERO);
		valve.addValue(2 * HOUR, ONE);
		valve.addValue(4 * HOUR, FloatValue.ZERO);
		valve.addValue(7 * HOUR, ONE);
		valve.addValue(9 * HOUR, FloatValue.ZERO);
		valve.addValue(10 * HOUR, FloatValue.ZERO);
		checkEval(windows, valve, 4, 4 * HOUR, 2, 2);
	}

	@Test
	public void twoValvesWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows = new TreeTimeSeries(BooleanValue.class);
		windows.addValue(0, BooleanValue.FALSE);
		windows.addValue(1 * HOUR, BooleanValue.TRUE);
		windows.addValue(4 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve1 = new FloatTreeTimeSeries();
		valve1.addValue(0, FloatValue.ZERO);
		valve1.addValue(2 * HOUR, ONE);
		valve1.addValue(3 * HOUR, FloatValue.ZERO);
		final FloatTreeTimeSeries valve2 = new FloatTreeTimeSeries();
		valve2.addValue(0, FloatValue.ZERO);
		valve2.addValue(1 * HOUR, ONE);
		valve2.addValue(2 * HOUR, FloatValue.ZERO);
		checkEval(Collections.<ReadOnlyTimeSeries> singletonList(windows), Arrays.<ReadOnlyTimeSeries> asList(valve1, valve2), 1, 3 * HOUR, 1, 1);
	}
	
	@Test
	public void twoWindowSensorsWorks() throws InterruptedException, ExecutionException, TimeoutException {
		final TreeTimeSeries windows1 = new TreeTimeSeries(BooleanValue.class);
		windows1.addValue(0, BooleanValue.FALSE);
		windows1.addValue(1 * HOUR, BooleanValue.TRUE);
		windows1.addValue(3 * HOUR, BooleanValue.FALSE);
		final TreeTimeSeries windows2 = new TreeTimeSeries(BooleanValue.class);
		windows2.addValue(0, BooleanValue.FALSE);
		windows2.addValue(2 * HOUR, BooleanValue.TRUE);
		windows2.addValue(4 * HOUR, BooleanValue.FALSE);
		final FloatTreeTimeSeries valve1 = new FloatTreeTimeSeries();
		valve1.addValue(0, FloatValue.ZERO);
		valve1.addValue(2 * HOUR, ONE);
		valve1.addValue(3 * HOUR, FloatValue.ZERO);
		checkEval(Arrays.<ReadOnlyTimeSeries> asList(windows1, windows2), Collections.<ReadOnlyTimeSeries> singletonList(valve1), 1, 3 * HOUR, 1, 1);
	}
	
}