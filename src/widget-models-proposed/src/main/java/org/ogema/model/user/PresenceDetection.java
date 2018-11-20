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
package org.ogema.model.user;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;

/** put an instance of this resource for each WLAN into a ResourceList*/
public interface PresenceDetection extends PhysicalElement {

	/**Presence detection for individual users*/
	public ResourceList<UserPresenceDetection> userPresence();

	@NonPersistent
	/**If true the system detected presence of at least one person. This may be
	 * one of the known users or an unknown person
	 */
	public BooleanResource presenceDetected();
	
	/**When presence e.g. in room is detected to be available also
	 * presence in the upper level location (building property unit or building) can
	 * be expected. If not presence in a room is detected this does not imply that there
	 * is no presence in the building, though. So the presence in the building may be
	 * indicated for some time after presence is lost in the room.<br>
	 * A negative value indicates that no presence shift to the upper level shall take place.
	 */
	public TimeResource presenceUpwardsDuration();
}
