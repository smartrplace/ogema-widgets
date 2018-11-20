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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.collectionother;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;

import de.iwes.util.resource.ResourceHelper;

/** not really developed to a meaningful functionality
 *	@deprecated
 */
@Deprecated
public class ProgramTypeHelper<C extends Resource> {
	public ResourceList<C> configList;
	Class<? extends Resource> configClass;

	@SuppressWarnings("unchecked")
	public ProgramTypeHelper(Class<? extends Resource> configClass,
			String configResourceDefaultName, ApplicationManager appMan) {
		this.configClass = configClass;
		
		if(configResourceDefaultName == null) {
			configResourceDefaultName = configClass.getSimpleName().substring(0, 1).toLowerCase()+configClass.getSimpleName().substring(1);
		}
		final String name = ResourceHelper.getUniqueResourceName(configResourceDefaultName);
		configList = appMan.getResourceAccess().getResource(name);
		if (configList != null) { // resource already exists (appears in case of non-clean start)
			appMan.getLogger().debug("{} started with previously-existing config resource", getClass().getName());
		}
		else {
			configList = (ResourceList<C>) appMan.getResourceManagement().createResource(name, ResourceList.class);
			configList.setElementType(configClass);
			appMan.getLogger().debug("{} started with new config resource", getClass().getName());
		}
	}
}
