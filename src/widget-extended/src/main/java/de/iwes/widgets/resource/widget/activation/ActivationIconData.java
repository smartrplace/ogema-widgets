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
package de.iwes.widgets.resource.widget.activation;

import org.ogema.core.model.Resource;

import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconData;

public class ActivationIconData extends IconData {
	
	private Resource resource;

	public ActivationIconData(Icon icon) {
		super(icon);
	}
	
	protected void setResource(Resource resource) {
		this.resource = resource;
	}
	
	protected Resource getResource() {
		return resource;
	}

}
