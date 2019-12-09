package de.iwes.timeseries.testdata.impl;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.tools.resource.util.LoggingUtils;

/**
 * Generates log data and online eval data
 */
public class LogDataGeneration  {

	// clean start or not is irrelevant here
	static void run(final ResourceList<FloatResource> base, final ApplicationManager am) {
		final FloatResource logRandom30s = base.getSubResource("log_random_30s", FloatResource.class).create();
		final FloatResource logSteps30s = base.getSubResource("log_steps_30s", FloatResource.class).create();
		final FloatResource logSine30s = base.getSubResource("log_sine_30s", FloatResource.class).create();
		final FloatResource logRandom5min = base.getSubResource("log_random_5min", FloatResource.class).create();
		final FloatResource logSteps5min = base.getSubResource("log_steps_5min", FloatResource.class).create();
		final FloatResource logSine5min = base.getSubResource("log_sine_5min", FloatResource.class).create();
		
		LoggingUtils.activateLogging(logRandom30s, -2);
		LoggingUtils.activateLogging(logSteps30s, -2);
		LoggingUtils.activateLogging(logSine30s, -2);
		LoggingUtils.activateLogging(logRandom5min, -2);
		LoggingUtils.activateLogging(logSteps5min, -2);
		LoggingUtils.activateLogging(logSine5min, -2);
		
		final long fiveMin = 5 * 60 * 1000;
		am.createTimer(30*1000, new TimerListener() {
			
			@Override
			public void timerElapsed(Timer timer) {
				logRandom30s.setValue((float) Math.random());
				logSteps30s.getAndAdd(1);
				logSine30s.setValue((float) Math.sin((2*Math.PI * am.getFrameworkTime()/fiveMin)));
			}
		});
		am.createTimer(fiveMin, new TimerListener() {
			
			@Override
			public void timerElapsed(Timer timer) {
				logRandom5min.setValue((float) Math.random());
				logSteps5min.getAndAdd(1);
				logSine5min.setValue((float) Math.sin((2*Math.PI * am.getFrameworkTime()/fiveMin/24)));
			}
		});
	}

}
