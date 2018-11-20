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

public class DevicePageDictionary_de implements DevicePageDictionary {
	
	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.GERMAN;
	};

	@Override
	public String deviceType() {
		return "Ger�tetyp";
	};
	
	@Override
	public String room() {
		return "Raum";
	};
	
	@Override
	public String patternSelectLabel() {
		return "Ger�t ausw�hlen";
	}

	@Override
	public String pageTitle() {
		return "Ger�tekonfiguration";
	}
	
	@Override
	public String name() {
		return "Name";
	}	
	
	@Override
	public String path() {
		return "Ressourcenpfad";
	}
	
}
