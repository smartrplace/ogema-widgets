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
package de.iwes.widgets.reveal.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;

public class EcoModeState {

	private List<BooleanResource> toggled;
	boolean active = false;
	
	void toggle(final ApplicationManager appMan) {
		final boolean newState = active;
		if (!newState) { // we are activating the eco mode
			this.toggled = getResources(appMan);
		}
		this.toggled.forEach(r -> RevealTestPage.setBooleanSafe(r, newState));
		if (newState) // we are deactivating the eco mode
			this.toggled = null;
		active = !active;
	}
	
	private static List<BooleanResource> getResources(final ApplicationManager appMan) {
		Stream<BooleanResource> toggles = Stream.concat(
				// switch boxes
				appMan.getResourceAccess().getResources(SingleSwitchBox.class).stream()
			    	.map(box -> box.onOffSwitch().stateControl()),
			    // lights
			    appMan.getResourceAccess().getResources(ElectricLight.class).stream()
					.map(light -> light.onOffSwitch().stateControl())    
			)
			.filter(Resource::exists);
		// hack for Smartrplace heat control...
		try {
			final Resource eco = appMan.getResourceAccess().getResource("smartrplaceHeatcontrolConfig/ecoModeActive"); // XXX hard coded path
			if (eco instanceof BooleanResource)
				toggles = Stream.concat(toggles, Stream.<BooleanResource> builder().add((BooleanResource) eco).build());
		} catch (SecurityException expected) {} // if permission is missing
		toggles = toggles.filter(EcoModeState::filter); 
		return toggles.collect(Collectors.toList());
	}
	
	private static boolean filter(final BooleanResource r) {
		try {
			if ("ecoModeActive".equals(r.getName()))
				return !r.getValue();
			return r.getValue();
		} catch (SecurityException e) {
			return false;
		}
	}
	
}
