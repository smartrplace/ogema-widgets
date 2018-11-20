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
package de.iwes.widgets.test3.template.impl;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.geomap.MapTemplate;

public class MarkerTemplate implements MapTemplate<MarkerType> {

	@Override
	public String getId(MarkerType object) {
		return object.id;
	}

	@Override
	public String getLabel(MarkerType object, OgemaLocale locale) {
		return object.title;
	}

	@Override
	public double getLatitude(MarkerType instance) {
		return instance.lat;
	}

	@Override
	public double getLongitude(MarkerType instance) {
		return instance.lng;
	}

	@Override
	public String getIconUrl(MarkerType instance) {
		return instance.iconUrl;
	}
	
	@Override
	public int[] getIconSize(MarkerType instance) {
		return new int[]{50,50}; 
	}

}
