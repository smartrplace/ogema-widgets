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
import java.util.Comparator;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.util.resource.ResourceHelper;
import de.iwes.util.resource.ValueResourceHelper;

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
	
	public static <T extends Resource> boolean hasNamedElement(String elementName, ResourceList<T> list) {
		for(T el: list.getAllElements()) {
			StringResource name = el.getSubResource("name", StringResource.class);
			if(name.exists() && name.getValue().equals(elementName))
				return true;
		}
		return false;
	}
	
	/** Create element that has a subresource name containing the elementName*/
	public static <T extends Resource> T getOrCreateNamedElement(String elementName, ResourceList<T> list) {
		list.create();
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
	public static <T extends Resource> T getNamedElementFlex(String elementName, ResourceList<T> list) {
		for(T el: list.getAllElements()) {
			if(el.getName().equals(elementName))
				return el;
			StringResource name = el.getSubResource("name", StringResource.class);
			if(name.exists() && name.getValue().equals(elementName))
				return el;
		}
		return null;
	}
	public static <T extends Resource> T getOrCreateNamedElementFlex(String elementName, ResourceList<T> list) {
		return getOrCreateNamedElementFlex(elementName, list, true);
	}
	public static <T extends Resource> T getOrCreateNamedElementFlex(String elementName, ResourceList<T> list,
			boolean activateNew) {
		return getOrCreateNamedElementFlex(elementName, list, activateNew,true);
	}
	public static <T extends Resource> T getOrCreateNamedElementFlex(String elementName, ResourceList<T> list,
			boolean activateNew, boolean addNameIfNotInName) {
		list.create();
		T result = getNamedElementFlex(elementName, list);
		if(result != null)
			return result;
		if(ResourceUtils.isValidResourceName(elementName))
			result = list.addDecorator(elementName, list.getElementType());
		else {
			result = list.add();
			if(addNameIfNotInName) {
				StringResource name = result.getSubResource("name", StringResource.class);
				name.create();
				name.setValue(elementName);
			}
		}
		if(activateNew)
			result.activate(true);
		return result;
	}
	
	public static <T extends Resource, R extends T> R getOrCreateNamedElementFlex(ResourceList<T> list,
			Class<R> typeToCreate) {
		String elementName = ResourceUtils.getValidResourceName(typeToCreate.getSimpleName());
		elementName = elementName.substring(0, 1).toLowerCase()+elementName.substring(1);
		return getOrCreateNamedElementFlex(elementName , list, typeToCreate);
	}

	/** Create an element in a resource list of a type inherited from the ResourceList type
	 * 
	 * @param <T>
	 * @param <R>
	 * @param elementName if not provided then derived from class name of typeToCreate
	 * @param list
	 * @param typeToCreate
	 * @return
	 */
	public static <T extends Resource, R extends T> R getOrCreateNamedElementFlex(String elementName, ResourceList<T> list,
			Class<R> typeToCreate) {
		return getOrCreateNamedElementFlex(elementName, list, typeToCreate, true);
	}
	@SuppressWarnings("unchecked")
	public static <T extends Resource, R extends T> R getOrCreateNamedElementFlex(String elementName, ResourceList<T> list,
			Class<R> typeToCreate, boolean activateNew) {
		list.create();
		for(T el: list.getAllElements()) {
			if(el.getName().equals(elementName)) {
				if(!typeToCreate.isAssignableFrom(el.getClass()))
					return null;
				return (R)el;
			}
			StringResource name = el.getSubResource("name", StringResource.class);
			if(name.exists() && name.getValue().equals(elementName)) {
				if(!typeToCreate.isAssignableFrom(el.getClass()))
					return null;
				return (R)el;
			}
		}
		R result;
		if(ResourceUtils.isValidResourceName(elementName))
			result = list.addDecorator(elementName, typeToCreate);
		else {
			result = list.addDecorator(getUniqueNameForNewElement(list), typeToCreate);
			StringResource name = result.getSubResource("name", StringResource.class);
			name.create();
			name.setValue(elementName);
		}
		if(activateNew)
			result.activate(true);
		return result;
	}

	public static <T extends Resource> String getNameForElement(T el) {
		StringResource name = el.getSubResource("name", StringResource.class);
		if(name.exists() && !name.getValue().isEmpty())
			return name.getValue();
		return el.getName();
	}
	
	public static <T extends Resource> String getUniqueNameForNewElement(ResourceList<T> resList) {
		int maxExist = -1;
		String listNamePlus = resList.getName()+"_";
		for(T r: resList.getAllElements()) {
			if(!r.getName().startsWith(listNamePlus))
				continue;
			try  {
				int val = Integer.parseInt(r.getName().substring(listNamePlus.length()));
				if(val > maxExist)
					maxExist = val;
			} catch(NumberFormatException e) {}
		}
		return listNamePlus+String.format("%04d", maxExist+1);
	}
	
	public static <T extends Resource> String getUniqueHumanReadableNameForNewElement(ResourceList<T> resList, String baseName) {
		if(getNamedElementFlex(baseName, resList) == null)
			return baseName;
		String baseNamePlus = baseName+"(";
		int maxExist = 1;
		for(T r: resList.getAllElements()) {
			if(!r.getName().startsWith(baseNamePlus))
				continue;
			if(!r.getName().endsWith(")"))
				continue;
			try  {
				int val = Integer.parseInt(r.getName().substring(baseNamePlus.length(), r.getName().length()-1));
				if(val > maxExist)
					maxExist = val;
			} catch(NumberFormatException e) {}
		}
		return baseNamePlus+String.format("%d", maxExist+1)+")";
	}
	public static <T extends Resource> T createAndSetUniqueHumanReadableNameForNewElement(ResourceList<T> resList, String baseName) {
		String name = getUniqueHumanReadableNameForNewElement(resList, baseName);
		T newEl = resList.add();
		ValueResourceHelper.setCreate(newEl.getSubResource("name", StringResource.class), name);
		return newEl;
	}

	public static <T extends Resource> T addWithOrderedName(ResourceList<T> resList) {
		String name = getUniqueNameForNewElement(resList);
		return resList.addDecorator(name, resList.getElementType());
	}
	
	public static <T extends Resource> List<T> getAllElementsLocation(ResourceList<T> resList) {
		List<T> result = new ArrayList<>();
		for(T r: resList.getAllElements()) {
			result.add(r.getLocationResource());
		}
		return result ;
	}

	/** Check if element is already referenced in resource list and add if not so.<br>
	 * Create and activate resource list if element is added and resource list did not exist before
	 * 
	 * @param <T>
	 * @param resList
	 * @param object element to be included as reference in resList
	 * @return reference resource if added
	 */
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
	
	/** Set a ResourceList of references to a list of references
	 * 
	 * @param <T>
	 * @param resList
	 * @param objects
	 * @param deleteNonReferences if true non-reference objects not found in objects argument
	 * 		are deleted (which means totally removed from the database), otherwise these are maintained
	 * @return
	 */
	public static <T extends Resource> int setListToReferences(ResourceList<T> resList, List<T> objects,
			boolean deleteNonReferences) {
		int count = 0;
		boolean isNew = false;
		if((!resList.exists()) && (!objects.isEmpty())) {
			resList.create();
			isNew = true;
		}
		for(T existing: resList.getAllElements()) {
			if((existing.isReference(false) || deleteNonReferences)
					&& (!ResourceHelper.containsLocation(objects, existing)))
				existing.delete();
		}
		for(T object: objects) {
			if(addReferenceUnique(resList, object) != null)
				count++;
		}
		if(isNew)
			resList.activate(true);
		return count;
	}
	
	/** Get element in resource list that has sub resource with name referenceName that is a reference
	 * to object
	 * @param <T>
	 * @param resList
	 * @param object
	 * @param referenceName
	 * @return
	 */
	public static <T extends Resource> T getReferencingElement(ResourceList<T> resList, Resource object, String referenceName) {
		for(T el: resList.getAllElements()) {
			Resource ref = el.getSubResource(referenceName);
			if(ref.exists() && ref.isReference(false) && ref.equalsLocation(object))
				return el;
		}
		return null;
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

	/** Sort elements based on the resource names.
	 * This is usually required when a ResourceList may be re-read via ogj/ogx and the list order is not maintained.<br>
	 * Note that this is NOT necessarily the order provided by getAllElements initially!*/
	public static <T extends Resource>  List<T> getAllElementsSorted(ResourceList<T> resList) {
		List<T> result = resList.getAllElements();
		result.sort(new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return result;
	}

	public static <T extends Resource> void clear(ResourceList<T> intervals) {
		List<T> all = intervals.getAllElements();
		for(T el: all) {
			el.delete();
		}
	}
}
