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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.model.gateway.init.InitStatus;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;
import org.ogema.tools.resourcemanipulator.timer.CountDownTimer;

import de.iwes.util.resource.ValueResourceHelper;

/**
 * Implementation of a Count-Down timer: An OGEMA timer that after creation calls delayedExecution and
 * afterwards is destroyed. In contrast to {@link CountDownTimer} it does not require an TimerListener.
 * 
 * @author Timo Fischer, Fraunhofer IWES
 */
// FIXME where is the timer destroyed, as promised in the documentation?
public abstract class SimulationDelayInit {

	public CountDownDelayedExecutionTimer timer = null;
	public ResourceValueListener<BooleanResource> initListener = null;
	
	public abstract void init();

	/** Note: The timer is not started automatically after construction, only after call of start*/
	public SimulationDelayInit(ApplicationManager appMan) {
		this(appMan, null);
	}
	public SimulationDelayInit(ApplicationManager appMan, Long defaultDelay) {
		// FIXME a global property for a tool that can be used in all kinds of applications?
		//String delayProp = System.getProperty("org.ogema.sim.simulationdelay");
		String delayProp = AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				return System.getProperty("org.ogema.sim.simulationdelay");
			}
		});
		long delay;
		if(delayProp == null) {
			if(defaultDelay != null) delay = defaultDelay;
			else {
				init();
				return;
			}
		}
		else delay = Long.parseLong(delayProp);
		if(delay <= 0) {
			BooleanResource replayStatus = getReplayInit(appMan);
			if(replayStatus.getValue())
				init();
			else {
				initListener = new ResourceValueListener<BooleanResource>() {
					@Override
					public void resourceChanged(BooleanResource resource) {
						if(!replayStatus.getValue()) return;
						init();
						replayStatus.removeValueListener(this);
					}
				};
				replayStatus.addValueListener(initListener, true);
			}
			return;
		}
		timer = new CountDownDelayedExecutionTimer(appMan, delay) {
			@Override
			public void delayedExecution() {
				init();
			}
		};
	}
	
	public static BooleanResource getReplayInit(ApplicationManager appMan) {
		return AccessController.doPrivileged(new PrivilegedAction<BooleanResource>() {
			@Override
			public BooleanResource run() {
				InitStatus init =  appMan.getResourceManagement().createResource("initStatus", InitStatus.class);
				BooleanResource result = init.replayOnClean();
				if(ValueResourceHelper.setIfNew(result, false))
					init.activate(true);
				return result;
			}
		});
	}
}
