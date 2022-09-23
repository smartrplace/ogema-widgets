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
package de.iwes.widgets.template;

import org.ogema.core.model.Resource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class DefaultDisplayTemplate<T> implements DisplayTemplate<T> {
	
	@Override
	public String getLabel(T object, OgemaLocale locale) {
		if(object == null) try {
			return "Null: "+getId(object);
		} catch(NullPointerException e) {
			return "Null: Id: null";			
		}
		if (object instanceof LabelledItem) {
			final String label = ((LabelledItem) object).label(locale);
			if (label != null) // should not return null, but we never know...
				return label;
		} else if(object instanceof Resource) {
			return ResourceUtils.getHumanReadableName((Resource) object);
		}
		return object.toString();
	}
	
	@Override
	public String getId(T object) {
		return object instanceof LabelledItem ? ((LabelledItem) object).id() : object.toString();
	}

}
