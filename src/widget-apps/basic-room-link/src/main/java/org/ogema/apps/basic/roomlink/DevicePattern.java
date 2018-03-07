/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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
