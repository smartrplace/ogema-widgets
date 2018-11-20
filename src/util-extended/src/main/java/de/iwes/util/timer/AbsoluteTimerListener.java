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

import org.ogema.tools.resourcemanipulator.timer.CountDownAbsoluteTimer;


public interface AbsoluteTimerListener {
	/**
	 * @param myTimer see {@link org.ogema.core.application.TimerListener}
	 * @param absoluteTime time for which the callback was requested. This may differ from the real time
	 * due to some time lag of the thread or due to an interruption of system operation at the the
	 * real time when the call was expected
	 * @param timeStep difference to absoluteTime of last callback
	 */
	 public abstract void timerElapsed(CountDownAbsoluteTimer myTimer, long absoluteTime, long timeStep);
}
