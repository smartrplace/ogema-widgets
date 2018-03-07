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

package org.ogema.apps.device.configuration;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.devices.profiles.State;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;

import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DisplayValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.Entry;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.EntryType;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.NamingPolicy;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.PreferredName;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.StringConversion;

public class PhysicalElementPattern extends ResourcePattern<PhysicalElement> {

	public PhysicalElementPattern(Resource match) {
		super(match);
	}
	
	@Existence(required=CreateMode.OPTIONAL)
	@EntryType(setAsReference=true)
	@Entry(show=true)
	@NamingPolicy(policy=PreferredName.USER_GIVEN_NAME)
	Room room = model.location().room();
	
	@Existence(required=CreateMode.OPTIONAL)
	StringResource name = model.name();
	
	@Existence(required=CreateMode.OPTIONAL)	
//	@ListType(type=)
	ResourceList<State> states = model.getSubResource("states", ResourceList.class);
	
	@Override
	public boolean accept() {
		if (model instanceof Room || model instanceof Building || model instanceof BuildingPropertyUnit)
			return false;
		Resource parent = model.getParent();
		while (parent != null) {
			if (parent instanceof PhysicalElement) return false;
			parent = parent.getParent();
		}
		return true;
	}

	@DisplayValue
	public String path() {
		return model.getPath();
	}
	
	@DisplayValue(stringConversion=StringConversion.NAME_SERVICE)
	public Class<? extends Resource> deviceType() {
		return model.getResourceType();
	}
	
}
