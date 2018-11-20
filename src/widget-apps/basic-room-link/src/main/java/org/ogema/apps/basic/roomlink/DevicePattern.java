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
package org.ogema.apps.basic.roomlink;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;

import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.Entry;

public class DevicePattern extends ResourcePattern<PhysicalElement>{

	public DevicePattern(Resource match) {
		super(match);
	}
	
	@Existence(required=CreateMode.OPTIONAL)
	StringResource Name = model.name();
	
	@Entry(show=true)
	@Existence(required=CreateMode.OPTIONAL)
	Room Room = model.location().room();
	
	@Override
	public boolean accept() {
		if (model instanceof Room || model instanceof Building || model instanceof BuildingPropertyUnit  // we do not want to assign rooms to other rooms...
				|| isParentPhysicalElement(model)) 
			return false;
		else
			return true;
	}
	
	private static final boolean isParentPhysicalElement(Resource resource) {
		while (!resource.isTopLevel()) {			
			resource = resource.getParent();
			if (resource instanceof PhysicalElement)
				return true;
		}
		return false;
	}

}
