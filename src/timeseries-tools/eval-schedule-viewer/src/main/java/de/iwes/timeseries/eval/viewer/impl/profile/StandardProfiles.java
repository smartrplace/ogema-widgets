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
package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Arrays;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileCategory;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Service(ProfileCategory.class)
@Component
public class StandardProfiles implements ProfileCategory {

	private final static List<Profile> PROFILES = Arrays.asList(
		 new AllLogdata(),
		 new AllSchedules(),
		 new AllOnline(),
		 new TemperatureProfile(),
		 new HumidityProfile(),
		 new PowerProfile(),
		 new ThermostatSetpointProfile(),
		 new ValvePositionProfile(),
		 new WindowSensorProfile(),
		 new PresenceProfile()
	);

	@Override
	public String id() {
		return "stdProfiles";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Standard profiles"; 
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Standard profiles"; 
	}

	@Override
	public List<Profile> getProfiles() {
		return PROFILES;
	}
	
}
