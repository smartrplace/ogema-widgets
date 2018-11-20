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
package de.iwes.util.linkingresource.alt;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

/**
 * A sample pattern that can be used to track the device-room association by means of 
 * a {@link ExperimentalLinkingResourceManagement}. The pattern matches, if and only if the device is 
 * located in the room specified by the pattern context. 
 */
public class ExamplePattern extends ContextSensitivePattern<PhysicalElement, Room> {

	public ExamplePattern(Resource match) {
		super(match);
	}
	
	public final StringResource name = model.name();

	@Override
	public boolean accept() {
		if (context == null)
			return true;
		Room room = ResourceUtils.getDeviceRoom(model);
		return context.equalsLocation(room);
	}
	
}
