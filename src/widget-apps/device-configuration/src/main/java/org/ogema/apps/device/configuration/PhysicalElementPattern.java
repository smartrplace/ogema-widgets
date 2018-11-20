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
