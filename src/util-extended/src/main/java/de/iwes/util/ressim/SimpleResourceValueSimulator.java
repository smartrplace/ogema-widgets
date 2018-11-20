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
import org.ogema.core.model.simple.FloatResource;

import de.iwes.util.format.StringFormatHelper;

/** Changes the value of a float resource periodically to simulate a sensor or something similar. The
 * value is increased and decreased as a sawtooth shaped signal.
 * @author dnestle
 *
 */
public class SimpleResourceValueSimulator implements TimerListener {
	public static final long UPDATE_TIMESTEP = 5000;
	public final float av;
	public final float maxDeviation;
	public final FloatResource res;
	public final long cycleTime;
	private final ApplicationManager appMan;
	private final Timer timer;
	
	public float stepSize;
	
	/**
	 * @param res resource to control for simulation
	 * @param av average value of res to be set by the simulation
	 * @param maxDeviation maximum deviation from the average value
	 * @param cycleDuration duration of one entire sawtooth cycle including one maximum and one minimum.
	 *        In the current version the value is changed every 5 seconds, so cycle duration should be
	 *        at least 50 seconds. 
	 * @param appMan reference to application manager
	 */
	public SimpleResourceValueSimulator(FloatResource res, float av, float maxDeviation, long cycleDuration,  ApplicationManager appMan) {
		super();
		this.av = av;
		this.maxDeviation = maxDeviation;
		this.res = res;
		this.cycleTime = (cycleDuration-10)/4;
		this.appMan = appMan;
		timer = appMan.createTimer(UPDATE_TIMESTEP, this);
		appMan.getLogger().info("Creating simulation timer {}, time: {}",res.getValue(),StringFormatHelper.getTimeOfDayInLocalTimeZone(timer.getExecutionTime()));
		res.setValue(av);
		stepSize = maxDeviation * timer.getTimingInterval()/cycleTime;
	}
	
	/** Delete simulator*/
	public void delete() {
		timer.destroy();
		//now nobody should have a reference on the object anymore
	}

	@Override
	public void timerElapsed(Timer arg0) {
		if((res.getValue() + stepSize > av + maxDeviation) || (res.getValue() + stepSize < av - maxDeviation)) {
			stepSize = -stepSize;
			appMan.getLogger().debug("Changing direction for resource {}",res);
		} else {
			res.setValue(res.getValue() + stepSize);
			appMan.getLogger().debug("New simulated value {}, time: {}",res.getValue(),StringFormatHelper.getTimeOfDayInLocalTimeZone(arg0.getExecutionTime()));
		}
	}
}
