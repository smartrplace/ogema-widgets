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
