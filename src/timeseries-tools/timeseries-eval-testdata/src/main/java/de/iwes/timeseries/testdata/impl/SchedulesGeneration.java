package de.iwes.timeseries.testdata.impl;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.TimeSeries;
import org.ogema.tools.resource.util.TimeSeriesUtils;

public class SchedulesGeneration {
	
	static void run(ResourceList<FloatResource> floats, final long startTime) {
		if (floats.getSubResource("future_random") != null)
			return;
		generateRandomSchedules(floats.getSubResource("future_random", FloatResource.class).<FloatResource> create(), startTime, false);
		generateStepFunctionSchedules(floats.getSubResource("future_steps", FloatResource.class).<FloatResource> create(), startTime, false);
		generateSineSchedules(floats.getSubResource("future_sine", FloatResource.class).<FloatResource> create(), startTime, false);
		generateRandomSchedules(floats.getSubResource("past_random", FloatResource.class).<FloatResource> create(), startTime, true);
		generateStepFunctionSchedules(floats.getSubResource("past_steps", FloatResource.class).<FloatResource> create(), startTime, true);
		generateSineSchedules(floats.getSubResource("past_sine", FloatResource.class).<FloatResource> create(), startTime, true);
	}
	
	private static void generateRandomSchedules(final FloatResource base, final long startTime, boolean pastOrFuture) {
		AbsoluteSchedule schedule = base.getSubResource("_5Min_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		int nrPoints = 1000;
		long duration = 5* 60 * 1000;
		long s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, false, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_5Min_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, true, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_1h_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		duration = 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, false, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_1h_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, true, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_1d_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		duration = 24 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, false, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_1d_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, true, Float.valueOf(1), Float.valueOf(0)));
		
		nrPoints = 5000;
		
		schedule = base.getSubResource("_1w_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		duration = 7 * 24 * 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, false, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_1w_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, true, Float.valueOf(1), Float.valueOf(0)));
		
		nrPoints = 15000;
		
		schedule = base.getSubResource("_1y_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		duration = 365 * 24 * 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, false, Float.valueOf(1), Float.valueOf(0)));
		
		schedule = base.getSubResource("_1y_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR);
		schedule.addValues(TimeSeriesUtils.createRandomTimeSeries(nrPoints, s, duration / nrPoints, true, Float.valueOf(1), Float.valueOf(0)));
		
	}
	
	private static void generateStepFunctionSchedules(final FloatResource base, final long startTime, boolean pastOrFuture) {
		AbsoluteSchedule schedule = base.getSubResource("_5Min_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		int nrPoints = 1000;
		long duration = 5* 60 * 1000;
		long s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, Float.valueOf(0), Float.valueOf(1)));
		
		schedule = base.getSubResource("_5Min_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, true, Float.valueOf(0), Float.valueOf(1), true));
		
		schedule = base.getSubResource("_1h_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		duration = 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, Float.valueOf(0), Float.valueOf(1)));
		
		schedule = base.getSubResource("_1h_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, true, Float.valueOf(0), Float.valueOf(1), true));
		
		schedule = base.getSubResource("_1d_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		duration = 24 * 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, Float.valueOf(0), Float.valueOf(1)));
		
		schedule = base.getSubResource("_1d_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, true, Float.valueOf(0), Float.valueOf(1), true));
		
		nrPoints = 5000;
		
		schedule = base.getSubResource("_1w_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		duration = 7 * 24 * 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, Float.valueOf(0), Float.valueOf(1)));
		
		schedule = base.getSubResource("_1w_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, true, Float.valueOf(0), Float.valueOf(1), true));
		
		nrPoints = 15000;
		
		schedule = base.getSubResource("_1y_equidistant", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		duration = 365 * 24 * 60 * 60 * 1000;
		s = pastOrFuture ? startTime-duration : startTime;
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, Float.valueOf(0), Float.valueOf(1)));
		
		schedule = base.getSubResource("_1y_randomized", AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.STEPS);
		schedule.addValues(TimeSeriesUtils.createStepFunction(nrPoints, s, duration/nrPoints, true, Float.valueOf(0), Float.valueOf(1), true));
		
	}
	
	private static void generateSineSchedules(final FloatResource base, final long startTime, boolean pastOrFuture) {
		generateSingleSine(base, "_5Min", 5, startTime, pastOrFuture);
		generateSingleSine(base, "_1h", 60, startTime, pastOrFuture);
		generateSingleSine(base, "_1d", 24 * 60, startTime, pastOrFuture);
		generateSingleSine(base, "_1w", 7*24*60, startTime, pastOrFuture);
		generateSingleSine(base, "_1y", 365*24*60, startTime, pastOrFuture);
	}
	
	private static void generateSingleSine(final FloatResource base, final String id, final long durationFactor, final long startTime, final boolean pastOrFuture) {
		AbsoluteSchedule schedule = base.getSubResource(id, AbsoluteSchedule.class).create();
		schedule.setInterpolationMode(InterpolationMode.LINEAR); // best approx
		int nrPoints = 1000;
		long duration = durationFactor*60*1000;
		generateSine(schedule, startTime, nrPoints, duration/nrPoints, duration/nrPoints*20, pastOrFuture);
	}
	
	private static void generateSine(final TimeSeries schedule, long startTime, final int nrPoints, final long timeStep, final long period, final boolean pastOrFuture) {
		final List<SampledValue> values = new ArrayList<SampledValue>(nrPoints);
		if (pastOrFuture)
			startTime = startTime - nrPoints*timeStep;
		for (int i=0;i<nrPoints;i++) {
			final long t = startTime + i*timeStep;
			final float value = (float) Math.sin(2*Math.PI * t/period);
			values.add(new SampledValue(new FloatValue(value), t, Quality.GOOD));
		}
		schedule.addValues(values);
	}
	

}
