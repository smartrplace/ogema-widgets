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
package de.iwes.tools.standard.pattern;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

public class DeviceInRoomPattern extends ContextSensitivePattern<PhysicalElement, Room> {

	public DeviceInRoomPattern(Resource match) {
		super(match);
	}

	@Override
	public boolean accept() {
		Room location = ResourceUtils.getDeviceRoom(model);
		return (location != null && location.equalsLocation(context));
	}
	
}
