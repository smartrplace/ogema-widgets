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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.timer;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.alignedinterval.AlignedTimeIntervalLength;
import org.ogema.tools.resourcemanipulator.timer.CountDownAbsoluteTimer;

/** Provides a timer that generates a callback on the beginning of an aligned interval, e.g. at beginning
 * of an hour, a day etc.
 */
public class AbsolutePersistentTimer implements TimerListener {
	private static List<TimeResource> registered = new ArrayList<>();

	private CountDownAbsoluteTimer myTimer;
	private List<AbsoluteTimerListener> listeners = new ArrayList<>();
	private ApplicationManager appMan;
	
	private TimeResource lastCallbackIdealized;
	private AlignedTimeIntervalLength interval;
	private int intervalType;
	
	private long nextCallbackIdealized;
	private boolean isRunning = false;
	
	/** Call constructor on every start-up of the respective application
	 * 
	 * @param lastCallback resource to use for persistent storage of the last callback executed.
	 * 		The respective TimeResource must not be written outside AbsolutePersistentTimer and
	 * 		for each object of type AbsolutePersistentTimer a separate TimeResource has to be provided.
	 * @param interval aligned interval type. If no aligned interval type is specified by the resource,
	 * 		but an interval duration the AbsolutePersistentTimer compared to a normal {@link Timer}
	 * 		has the additional functionality to maintain the timer interval even when the system/app is
	 * 		restarted due to saving the callback time persistently.
	 * @param listener see {@link AbsoluteTimerListener}
	 * @param appMan
	 */
	public AbsolutePersistentTimer(TimeResource lastCallback,
			AlignedTimeIntervalLength interval, AbsoluteTimerListener listener,
			ApplicationManager appMan) {
		this.lastCallbackIdealized = lastCallback;
		this.interval = interval;
		this.appMan = appMan;
		
		init(listener);
	}
	/** Call constructor on every start-up of the respective application
	 * 
	 * @param lastCallback resource to use for persistent storage of the last callback executed.
	 * 		The respective TimeResource must not be written outside AbsolutePersistentTimer and
	 * 		for each object of type AbsolutePersistentTimer a separate TimeResource has to be provided.
	 * @param intervalType specification of aligned interval to be used. The aligned interval will be
	 * 		evaluated based on the local default time zone, see {@link AbsoluteTiming}
	 * @param listener see {@link AbsoluteTimerListener}
	 * @param appMan
	 */
	public AbsolutePersistentTimer(TimeResource lastCallback,
			int intervalType, AbsoluteTimerListener listener,
			ApplicationManager appMan) {
		this.lastCallbackIdealized = lastCallback;
		this.interval = null;
		this.intervalType = intervalType;
		this.appMan = appMan;
		
		init(listener);
	}
	
	private void init(AbsoluteTimerListener listener) {
		addListener(listener);
		
		if(!lastCallbackIdealized.exists()) {
			lastCallbackIdealized.create();
			lastCallbackIdealized.activate(false);
		}
		start();		
	}
	
	/**Call this after the timer has been stopped, the timer is started from the constructor automatically*/
	public void start() {
		if(isRunning) return;
		isRunning = true;
		long currentTime = appMan.getFrameworkTime();
		if(lastCallbackIdealized.getValue() <= 0) {
			if(interval != null) {
				nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(currentTime, interval);
			} else {
				nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(currentTime, intervalType);
			}
			lastCallbackIdealized.setValue(currentTime);
		} else {
			if(interval != null) {
				nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(lastCallbackIdealized.getValue(), interval);
			} else {
				nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(lastCallbackIdealized.getValue(), intervalType);
			}
		}
//if(lastCallbackIdealized.getLocation().equals("gatewayInfos/gatewayInfos/gatewayInfos_0/evaluations/evaluations_0/destinationStat/lastUpdate")) {
//	new IllegalStateException().printStackTrace();
//}	
		if(registered.contains(lastCallbackIdealized)) {
			throw new IllegalStateException("TimeResource "+lastCallbackIdealized.getLocation()+" used twice for AbsolutePersistentTimer!");
		} else {
			registered.add(lastCallbackIdealized);
		}
		if(interval != null) {
			appMan.getLogger().info("Started AbsoluteTimer on "+lastCallbackIdealized.getLocation()+" for "+interval.getLocation());
		} else {
			appMan.getLogger().info("Started AbsoluteTimer on "+lastCallbackIdealized.getLocation()+" for "+intervalType);
		}
		startNewTimer();
	}

	/**Add a listener to be called together with the listener specified with constructor*/
	public void addListener(AbsoluteTimerListener arg0) {
		listeners.add(arg0);
	}

	public long getExecutionTime() {
		return myTimer.getExecutionTime();
	}

	public List<AbsoluteTimerListener> getListeners() {
		return listeners;
	}

	public long getTimingInterval() {
		if(interval != null) {
			return AbsoluteTimeHelper.getStandardInterval(interval.timeIntervalLength().type().getValue());
		} else {
			return AbsoluteTimeHelper.getStandardInterval(intervalType);
		}
	}

	public boolean isTimerRunning() {
		return myTimer.isRunning();
	}

	public boolean removeListener(AbsoluteTimerListener timer0) {
		return listeners.remove(timer0);
	}

	/**Call this on shutdown/stop of the creating application.*/
	public void stop() {
		registered.remove(lastCallbackIdealized);
		if(myTimer != null) myTimer.destroy();
		if(!isRunning) return;
		isRunning = false;
	}


	@Override
	/**Do not overwrite this*/
	public void timerElapsed(Timer arg0) {
//System.out.println("Elapsed abs-timer "+(nextCallbackIdealized-appMan.getFrameworkTime())+" ms ahead for "+interval.getLocation());
		callListeners();
		startNewTimer();
	}
	
	private void startNewTimer() {
		long currentTime = appMan.getFrameworkTime();
		long firstInterval = nextCallbackIdealized - currentTime;
		//perform callbacks that have been missed due to an interruption of the system operation
		while(firstInterval <= 200) {
			callListeners();
			firstInterval = nextCallbackIdealized - currentTime;
		}
//System.out.println("Starting abs-timer "+(nextCallbackIdealized-appMan.getFrameworkTime())+" ms ahead for "+interval.getLocation());
		if(!isRunning) return;
		if(nextCallbackIdealized-appMan.getFrameworkTime() < 5000) {
			System.out.println("Starting abs-timer "+(nextCallbackIdealized-appMan.getFrameworkTime())+" ms ahead for "+lastCallbackIdealized.getLocation());
		}
		myTimer = new CountDownAbsoluteTimer(appMan, nextCallbackIdealized, this);
//System.out.println("Started abs-timer "+(nextCallbackIdealized-appMan.getFrameworkTime())+" ms ahead");
	}
	
	private void callListeners() {
		long timeStep = nextCallbackIdealized - lastCallbackIdealized.getValue();
		if(timeStep <= 0) {
			/**Note: This may occur when the same TimeResource is used by more than one AbsoluteTimer*/
			appMan.getLogger().warn("Abs-Timer time-step "+timeStep+" in "+lastCallbackIdealized.getLocation());
			if(interval != null) {
				nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(nextCallbackIdealized, interval);
			} else {
				nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(nextCallbackIdealized, intervalType);				
			}
			return;
		}
		for(AbsoluteTimerListener l:listeners) {
			l.timerElapsed(myTimer, nextCallbackIdealized, timeStep);
		}
		lastCallbackIdealized.setValue(nextCallbackIdealized);
		if(interval != null) {
			nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(nextCallbackIdealized, interval);
		} else {
			nextCallbackIdealized = AbsoluteTimeHelper.getNextStepTime(nextCallbackIdealized, intervalType);			
		}
	}
}
