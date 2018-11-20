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
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;

/** resets a resource to a certain with a certain interval*/
public class ValueResetter {
	public long interval;
	public FloatResource res = null;
	public IntegerResource resInt = null;
	public Float defaultValue = null;
	public Integer defaultValueInt = null;
	public Timer timer;

	/**
	 * 
	 * @param interval
	 * @param res
	 * @param defaultValue if null nothing is written when no schedule value is available
	 */
	public ValueResetter(final FloatResource res, long interval, final Float defaultValue,
			ApplicationManager appMan) {
		this.interval = interval;
		this.res = res;
		this.defaultValue = defaultValue;
		
		timer = appMan.createTimer(interval, new TimerListener() {
			@Override
			public void timerElapsed(Timer timer) {
				res.setValue(defaultValue);
			}
		});
	}
	public ValueResetter(final IntegerResource resInteger, long interval, final Integer defaultValue,
			ApplicationManager appMan) {
		this.interval = interval;
		this.resInt = resInteger;
		this.defaultValueInt = defaultValue;
		
		timer = appMan.createTimer(interval, new TimerListener() {
			@Override
			public void timerElapsed(Timer timer) {
				resInteger.setValue(defaultValueInt);
			}
		});
	}
	
}
