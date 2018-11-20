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
package org.ogema.apps.device.configuration.localisation;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class DevicePageDictionary_fr implements DevicePageDictionary {
	
	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.FRENCH;
	};

	@Override
	public String deviceType() {
		return "Type appareil";
	};
	
	@Override
	public String room() {
		return "Chambre";
	};
	
	@Override
	public String name() {
		return "Nom";
	}

	@Override
	public String patternSelectLabel() {
		return "Choisis un appareil";
	}

	@Override
	public String pageTitle() {
		return "Configuration appareil";
	}	
	
	@Override
	public String path() {
		return "Resource path";
	}
	
}
