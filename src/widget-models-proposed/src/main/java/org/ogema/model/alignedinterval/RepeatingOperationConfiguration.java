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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.alignedinterval;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** Configuration for the time instants on which a repeating event shall take place */
public interface RepeatingOperationConfiguration extends Data {
	/**If available the event shall take place at the aligned interval starts plus the time defined.
	 * If both an aligned interval and fixedDuration are configured both configurations shall be
	 * operated in parallel. E.g. to make sure an operation takes place every hour (without a certain
	 * time within the aligned hour) plus exactly on 23:00 of each day, configure:<br>
	 * alignedTimeInterval.timeIntervalLength.fixedDuration = 60*60000<br>
	 * alignedTimeInterval.timeIntervalLength.type = 10<br>
	 * alignedTimeIntervalOffset = 23*60*6000<br>
	 * Note that in most cases the time of last operation is not stored persistently, so in case of
	 * a system restart a fixedDuration operation has no guarantee when to take place.
	 * */
	AlignedTimeIntervalLength alignedTimeInterval();
	
	/**Only relevant if alignedTimeInterval does define an aligned interval and not only a fixedDuration*/
	TimeResource alignedTimeIntervalOffset();
	
	/**If zero or positive the operation shall be executed once after startup with a delayof milliseconds
	 * indicated by this element. The delay can be used to give other components time to start up.
	 */
	TimeResource performOperationOnStartUpWithDelay();
	
	/**The configuration may be temporarily disabled to avoid further operations*/
	BooleanResource disable();
}
