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
package de.iwes.util.resource;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.tools.resourcemanipulator.configurations.ProgramEnforcer;
import org.ogema.tools.resourcemanipulator.timer.CountDownAbsoluteTimer;

import de.iwes.util.collectionother.LogicProvider;

/**Automatically picks the current value from the {@link Schedule} program and writes it
 * into the respective FloatResource. This is a simple-to-use version of {@link ProgramEnforcer}.
 * No additional information is stored by ProgramEnforcerSimple persistently, so you have to
 * call the same constructor on each start-up of the respective application.
 */
// TODO listener based version, instead of timer
public class ProgramEnforcerSimple implements LogicProvider, TimerListener {
	public final long interval;
	public final FloatResource res;
	public final Schedule sched;
	public boolean useLastValue = false;
	public boolean rewriteSameValue = true;
	
	public final Float defaultValue;
	public Timer timer = null;
	public ResourceValueListener<Schedule> listener = null;

	private final ApplicationManager appMan;
	private CountDownAbsoluteTimer countDowntimer = null;
	
	/** Create a ProgramEnforcerSimple that listens to changes in the schedule program
	 * 
	 * @param res
	 * @param defaultValue
	 * @param appMan
	 */
	public ProgramEnforcerSimple(final FloatResource res, Float defaultValue, ApplicationManager appMan) {
		this.res = res;
		this.sched = res.program();
		this.defaultValue = defaultValue;
		this.interval = -1;
		this.appMan = appMan;
		initListener();
	}
	/** Create a ProgramEnforcerSimple that listens to changes in an arbitrary schedule that is a child of a FloatResource
	 * 
	 * @param res
	 * @param defaultValue
	 * @param useLastValue if true the last available value is used if no current value is available. The
	 * 		default value when using other constructors is false
	 * @param rewriteSameValue if false the value will only be written if changed. This should be
	 * 		set to false for historicalData to avoid a loop between log data written to schedule and
	 * 		write operations from the ProgramEnforcert that trigger more logging. The default value when
	 * 		using other constructors is true
	 * @param appMan
	 */
	public ProgramEnforcerSimple(final Schedule sched, Float defaultValue,
			boolean useLastValue, boolean rewriteSameValue, ApplicationManager appMan) {
		this.sched = sched;
		Resource pr = sched.getParent();
		if(!(pr instanceof FloatResource)) throw new IllegalArgumentException("ProgramEnforcerSimple can only write to FloatResources!");
		this.res = (FloatResource)pr;
		this.defaultValue = defaultValue;
		this.interval = -1;
		this.appMan = appMan;
		this.useLastValue = useLastValue;
		this.rewriteSameValue = rewriteSameValue;
		initListener();
	}
	
	private void initListener() {
		listener = new ResourceValueListener<Schedule>() {

			@Override
			public void resourceChanged(Schedule resource) {
				performUpdate();
			}
		};
		sched.addValueListener(listener);		
		performUpdate();
	}

	/**
	 * Create a ProgramEnforcerSimple without default value.
	 * @param interval
	 * @param res
	 * @param defaultValue if null nothing is written when no schedule value is available
	 */
	public ProgramEnforcerSimple(final FloatResource res, long interval, ApplicationManager appMan) {
		this(res, interval, null, appMan);
	}
	
	/**
	 * Create a ProgramEnforcerSimple with default value.
	 * @param interval
	 * @param res
	 * @param defaultValue if null nothing is written when no schedule value is available
	 */
	public ProgramEnforcerSimple(final FloatResource res, long interval, Float defaultValue, ApplicationManager appMan) {
		this.interval = interval;
		this.res = res;
		this.sched = res.program();
		this.defaultValue = defaultValue;
		this.appMan = appMan;
		
		timer = appMan.createTimer(interval, this);
		performUpdate();
	}
	
	private void performUpdate() {
		if(sched.exists()) {
			long currentTime = appMan.getFrameworkTime();
			SampledValue sv = sched.getValue(currentTime);
			if((sv == null || sv.getQuality() == Quality.BAD) && useLastValue) {
				sv = sched.getPreviousValue(Long.MAX_VALUE);
			}
			if(sv == null || sv.getQuality() == Quality.BAD) {
				if(ProgramEnforcerSimple.this.defaultValue != null) setValue(ProgramEnforcerSimple.this.defaultValue);
			} else {
				setValue(sv.getValue().getFloatValue());
			}

			if(interval < 0) {
				sv = sched.getNextValue(currentTime+1);
				if(sv == null) return;
				if(!((countDowntimer != null)&&countDowntimer.isRunning()))
						countDowntimer  = new CountDownAbsoluteTimer(appMan, sv.getTimestamp(), true, this);
			}
		}
	}
	
	private void setValue(float value) {
		if((!rewriteSameValue)&&(res.getValue() == value)) return;
		res.setValue(value);
	}

	@Override
	public void close() {
		if(timer != null) timer.destroy();
		timer = null;
	}

	@Override
	public void timerElapsed(Timer timer) {
		performUpdate();
	}
}
