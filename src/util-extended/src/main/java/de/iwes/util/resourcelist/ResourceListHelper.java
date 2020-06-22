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
package de.iwes.util.resourcelist;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;

import de.iwes.util.resource.ResourceHelper;

public class ResourceListHelper {
	
	/** Create new element in resource list. The elements of the resource list
	 * need to contain the field name (which is the case for all prototypes except
	 * for ValueResources). Note that this method only adapts the content of the
	 * name field, not the resource name of the resource list element created (which
	 * is created automatically by the framework).
	 * @param list
	 * @param standardName if no existing element in the list has the standard name in
	 * its name field the standardName will be set to the new element. Otherwise the
	 * name will be adapted to be unique.
	 * @param activate if true the created element is activated
	 * @return newly created element of the resource list
	 */
	public static <T extends Resource> T createNewNamedElement(ResourceList<T> list, String standardName, boolean activate) {
		T newEl = list.add();
		StringResource name = newEl.getSubResource("name", StringResource.class);
		name.create();
		
		String checkedName = standardName;
		int extension = 0;
		boolean success = false;
		while(!success) {
			success = true;
			for(T oldRes: list.getAllElements()) {
				StringResource oldName = oldRes.getSubResource("name", StringResource.class);
				if(oldName.exists() && oldName.getValue().equals(checkedName)) {
					checkedName = standardName+"_"+extension;
					extension++;
					success = false;
					break;
				}
			}
		}
		name.setValue(checkedName);
		if(activate) {
			newEl.activate(true);
		}
		return newEl;
	}
	
	/** This method is intended to be used for resource lists in which the element names
	 * are not created automatically, but the elements are added as decorators with a
	 * given name.
	 * Get a resource name beginning with baseName that does not exist yet in the
	 * resource list.*/
	public static String createNewDecoratorName(String baseName, ResourceList<?> list) {
		String name = baseName;
		int i=0;
		while((list.getSubResource(name) != null)&& (list.getSubResource(name).exists())) {
			//baseName = baseName + "i";
			i++;
			name = baseName+"_"+i;
		}
		return name;
	}
	
	public static <T extends Resource> T getOrCreateNamedElement(String elementName, ResourceList<T> list) {
		for(T el: list.getAllElements()) {
			StringResource name = el.getSubResource("name", StringResource.class);
			if(name.exists() && name.getValue().equals(elementName))
				return el;
		}
		T result = list.add();
		StringResource name = result.getSubResource("name", StringResource.class);
		name.create();
		name.setValue(elementName);
		result.activate(true);
		return result;
	}

	public static <T extends Resource> List<T> getAllElementsLocation(ResourceList<T> resList) {
		List<T> result = new ArrayList<>();
		for(T r: resList.getAllElements()) {
			result.add(r.getLocationResource());
		}
		return result ;
	}

	public static <T extends Resource> T addReferenceUnique(ResourceList<T> resList, T object) {
		if(ResourceHelper.containsLocation(resList.getAllElements(), object))
			return null;
		boolean isNew = false;
		if(!resList.exists()) {
			isNew = true;
			resList.create();
		}
		T result = resList.add(object);
		if(isNew)
			resList.activate(true);
		return result;
	}
	
	public static <T extends Resource> boolean removeReferenceOrObject(ResourceList<T> resList, T object) {
		for(T res: resList.getAllElements()) {
			if(res.equalsLocation(object)) {
				res.delete();
				return true;
			}
		}
		return false;
	}
}
