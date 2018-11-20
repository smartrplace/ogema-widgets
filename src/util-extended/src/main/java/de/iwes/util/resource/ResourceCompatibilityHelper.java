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
package de.iwes.util.resource;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

/**
 * 
 * @author dnestle
 *
 */
public class ResourceCompatibilityHelper {
	/** Check if a simple or complex element exists at the old position, but not yet at
	 * the new position and move the element to the new position (with all elements,
	 * decorators etc.). No data conversion etc. can be done here.
	 * 
	 * @param oldResource
	 * @param newResource if the new resource does not yet exist this is a virtual resource.
	 * @param appMan
	 * @param deleteOldElementAlways if true the old element will be deleted even if the new
	 * 		element already exists and thus no copy process took place
	 * @return
	 */
	public static <T extends Resource> boolean checkOldNetLocation(T oldResource, T newResource,
			ApplicationManager appMan, boolean deleteOldElementAlways) {
		if(oldResource.exists() && !newResource.exists()) {
			OGEMAResourceCopyHelper.copySubResourceIntoDestination(
					newResource, oldResource, appMan, true);
			oldResource.delete();
			return true;
		} else if(deleteOldElementAlways) {
			oldResource.delete();
		}
		return false;
	}
}
