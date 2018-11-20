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
package de.iwes.widgets.api.extended.pattern;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.DisplayTemplate;

public class DefaultPatternTemplate<P extends ResourcePattern<?>> implements DisplayTemplate<P> {

	@Override
	public String getId(P object) {
		return object.model.getPath();
	}

	// FIXME use name service?
	@Override
	public String getLabel(P object, OgemaLocale locale) {
		String name = ResourceUtils.getHumanReadableName(object.model);;
		if (name.trim().isEmpty())
			name = object.model.getLocation();
		return name;
	}

}
