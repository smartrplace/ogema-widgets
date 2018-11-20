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
package de.iwes.util.ressim;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.util.format.StringFormatHelper;

/** 
 * A copy of SimpleResourceDiscreteSimulator, but for IntegerResources and for FloatResources.
 * @see SimpleResourceDiscreteSimulator 
 *
 */
public class SimpleResourceDiscreteSimulatorInt implements TimerListener {
	public final float av;
	public final float maxDeviation;
	public final SingleValueResource res;
	public final SingleValueResource incrementalResource;
	//public final long cycleTime;
	private final ApplicationManager appMan;
	private final Timer timer;
	
	private final int[] potentialValueJumps;
	private final int[] stepsToWait;
	
	public  int counter = 0;
	private int prevStepSize = 0;
	
	/**
	 * @param res resource to take incremental options by simulation
	 * @param incrementalResource integral of res, may be null if no integral shall be
	 * 		provided
	 * @param av average value of res to be set by the simulation
	 * @param maxDeviation maximum deviation from the average value
	 * @param cycleDuration duration of one entire sawtooth cycle including one maximum and one minimum.
	 *        In the current version the value is changed every 5 seconds, so cycle duration should be
	 *        at least 50 seconds.
	 * @param potentialValueJumps potential value changes of the simulated resource
	 * @param millisecondsToWait number of seconds between the respective value jump occurs. The length of this array
	 * must be the same as potentenialValueJumps and corresponding indices are used
	 * @param appMan reference to application manager
	 */
	public SimpleResourceDiscreteSimulatorInt(SingleValueResource res, SingleValueResource incrementalResource, float av, float maxDeviation, 
			int[] potentialValueJumps, final int[] millisecondsToWait, ApplicationManager appMan) {
		super();
		this.av = av;
		this.maxDeviation = maxDeviation;
		this.incrementalResource = incrementalResource;
		this.res = res;
		//this.cycleTime = (cycleDuration-10)/4;
		this.stepsToWait = new int[millisecondsToWait.length];
		for(int i=0; i<millisecondsToWait.length; i++) {
			stepsToWait[i] = (int) (millisecondsToWait[i] / SimpleResourceValueSimulator.UPDATE_TIMESTEP);
		}
		this.potentialValueJumps = potentialValueJumps;
		this.appMan = appMan;
		timer = appMan.createTimer(SimpleResourceValueSimulator.UPDATE_TIMESTEP, this);
		appMan.getLogger().info("Creating simulation timer {}, time: {}",ValueResourceUtils.getFloatValue(res),StringFormatHelper.getTimeOfDayInLocalTimeZone(timer.getExecutionTime()));
		//incrementalResource.setValue(av);
	}
	
	/** Delete simulator*/
	public void delete() {
		timer.destroy();
		//now nobody should have a reference on the object anymore
	}

	@Override
	public void timerElapsed(Timer arg0) {
		int stepSize = 0;
		for(int i=0; i<stepsToWait.length; i++) {
			if(counter%stepsToWait[i] == 0) {
				stepSize += potentialValueJumps[i];
			}
		}
		if(stepSize == 0) {
			if(prevStepSize != 0) {
				ValueResourceUtils.setValue(res, 0);
			}
		} else {
			if((ValueResourceUtils.getFloatValue(incrementalResource) + stepSize > av + maxDeviation) || (ValueResourceUtils.getFloatValue(incrementalResource) + stepSize < av - maxDeviation)) {
				// FIXME stepSize is not used here?
				stepSize = -stepSize;
				appMan.getLogger().debug("Changing direction for resource {}",res);
			} else {
				ValueResourceUtils.setValue(res, stepSize);
				ValueResourceUtils.setValue(incrementalResource, ValueResourceUtils.getFloatValue(incrementalResource) + stepSize);
				appMan.getLogger().debug("New simulated value {}, time: {}",ValueResourceUtils.getFloatValue(res),StringFormatHelper.getTimeOfDayInLocalTimeZone(arg0.getExecutionTime()));
			}
		}
		prevStepSize = stepSize;
		counter++;
	}
}
