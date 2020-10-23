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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.gateway.EvalCollection;
import org.ogema.model.gateway.LocalGatewayInformation;
import org.ogema.model.locations.Location;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;
import org.smartrplace.gateway.device.GatewayDevice;

import de.iwes.util.logconfig.EvalHelper;
import de.iwes.util.logconfig.LogHelper;

/** Collection of extensions to the interface Resource
 * see also {@link OGEMAResourceCopyHelper}
 */
public class ResourceHelper {
	/** Provide resource access object for which path equals location
	 * Note: In most cases {@link Resource#getLocationResource()} should be used.
	 * @param res resource for which path might be different from location
	 * @param resAcc
	 * @return resource object that has the same location as the input resource res,
	 * but the path of the result equals the location
	 * 
	 * @deprecated use {@link Resource#getLocationResource()} instead
	 */
	@Deprecated
	public static <T extends Resource> T localizeResource(T res, ResourceAccess resAcc) {
		String loc = res.getLocation();
		T newRes = resAcc.getResource(loc);
		return newRes;
	}
	
	/** Return parent going up a defined number of levels. Returns null if it is not
	 * possible to go up this number of levels for the resource. For levelUp == 1 the
	 * method equals r.getLocationResource().getParent().
	 */
	public static Resource getParentLevelsAbove(Resource r, int levelUp) {
		Resource res = r.getLocationResource();
		for(int i=0; i<levelUp; i++) {
			res = res.getParent();
			if(res == null) return null;
		}
		return res;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Resource> T getParentLevelsAbove(Resource r, int levelUp, Class<T> type) {
		Resource res = getParentLevelsAbove(r, levelUp);
		if((res == null)||(!type.isAssignableFrom(res.getResourceType()))) return null;
		return (T) res;
	}

	/**Get top-level resource based on the location
	 * @return top-level resource or resource itself if resource is top-level*/
	public static Resource getToplevelResource(Resource r) {
		Resource res = r.getLocationResource();
		while(!res.isTopLevel()) {
			res = res.getParent();
			if(res == null) throw new IllegalStateException("This should never occur!");
		}
		return res;
	}
	
	public static boolean hasParentLevelsAboveType(Resource r, Class<? extends Resource> type, int levelUp) {
		return hasParentLevelsAboveType(r, type, levelUp, false);
	}
	/**Checks whether a certain element in the location path above a resource has a 
	 * certain resource type
	 * @param r resource for which location hierarchy shall be checked
	 * @param type resource type expected/searched for as (super-)parent. Note that the type has
	 * 		to match exactly here, no inherited types are accepted
	 * @param levelUp number of levels to step up in the resource hierarchy
	 * @return true if the (super-)parent exists and has the type specified
	 */
	public static boolean hasParentLevelsAboveType(Resource r, Class<? extends Resource> type, int levelUp, boolean typeMustMatchExactly) {
		Resource superParent = getParentLevelsAbove(r, levelUp);
		if(superParent == null) return false;
		if(typeMustMatchExactly) {
			return (superParent.getResourceType().equals(type));
		} else {
			return(type.isAssignableFrom(superParent.getResourceType()));				
		}
	}
	
	public static int hasParentAboveType(Resource r, Class<? extends Resource> type) {
		return hasParentAboveType(r, type, false);
	}
	/** Checks whether the resource has a parent, grant-parent etc. of the type
	 * given.
	 * @param r resource for which location hierarchy shall be checked
	 * @param type resource type expected/searched for as (super-)parent. Note that the type has
	 * 		to match exactly here, no inherited types are accepted
	 * @param typeMustMatchExactly if true only exact types are accepted, otherwise any resource that
	 * 		is an instanceof the type requested is accepted (false is default)
	 * @return If so the number of levels above the resource the first time such a parent
	 * is found. Otherwise returns a negative number
	 */
	public static int hasParentAboveType(Resource r, Class<? extends Resource> type, boolean typeMustMatchExactly) {
		Resource res = r.getLocationResource();
		for(int i=1; i<100; i++) {
			res = res.getParent();
			if(res == null) return -1;
			if(typeMustMatchExactly) {
				if(res.getResourceType().equals(type)) return i;
			} else {
				if(type.isAssignableFrom(res.getResourceType())) return i;				
			}
		}
		return -2;
	}
	/** Checks whether the resource has a parent, grant-parent etc. of the type
	 * given. This method does not require that the resource type is available in the Java
	 * classpath of the calling application as the name is just given as String.
	 * @param r resource for which location hierarchy shall be checked
	 * @param simpleOrFullTypeClassName name of the resource type, which must be a class extending
	 * 		resource. The first type in the hierarchy either mathing as simple or as full class
	 * 		name is accepted.
	 * @return If so the number of levels above the resource the first time such a parent
	 * is found. Otherwise returns a negative number
	 */
	public static int hasParentAboveType(Resource r, String simpleOrFullTypeClassName) {
		Resource res = r.getLocationResource();
		for(int i=1; i<100; i++) {
			res = res.getParent();
			if(res == null) return -1;
			if(res.getResourceType().getSimpleName().equals(simpleOrFullTypeClassName) ||
					res.getResourceType().getName().equals(simpleOrFullTypeClassName)) return i;
		}
		return -2;
	}
	
	@SuppressWarnings("unchecked")
	/** Checks whether the resource has a parent, grant-parent etc. of the type
	 * given and returns it.
	 * @param r resource for which location hierarchy shall be checked
	 * @param type resource type expected/searched for as (super-)parent. Note that the type has
	 * 		to match exactly here, no inherited types are accepted
	 * @return (super-)parent of the type requested or null if no such (super-)parent was found
	 * @see ResourceUtils#getFirstParentOfType(), which is slightly different: it does not localize,
	 * and it only requires the resource type of the parent to be derived from the target type,
	 * not equal to.
	 */
	public static <R extends Resource> R getFirstParentOfType(Resource r, Class<? extends R> type) {
		int lvl = hasParentAboveType(r, type);
		if(lvl < 0) return null;
		return (R) getParentLevelsAbove(r, lvl);
	}

	/** Checks whether the resource has a parent, grant-parent etc. of the type
	 * given and returns it. This method does not require that the resource type is available in the Java
	 * classpath of the calling application as the name is just given as String.
	 * @param r resource for which location hierarchy shall be checked
	 * @param simpleOrFullTypeClassName name of the resource type, which must be a class extending
	 * 		resource. The first type in the hierarchy either mathing as simple or as full class
	 * 		name is accepted.
	 * @return (super-)parent of the type requested or null if no such (super-)parent was found
	 */
	public static Resource getFirstParentOfType(Resource r, String simpleOrFullTypeClassName) {
		int lvl = hasParentAboveType(r, simpleOrFullTypeClassName);
		if(lvl < 0) return null;
		return getParentLevelsAbove(r, lvl);
	}
	
	/** For now this is just a placeholder that could be used to improve name finding for
	 * config resources
	 * @deprecated just use the argument without calling the method
	 */
	public static String getUniqueResourceName(String appResourceName) {
		return appResourceName;
	}
	
	@SuppressWarnings("unchecked")
	/**Get top level resource with certain name and type
	 * 
	 * @param name name of the top-level resource
	 * @param type note that here also inheriting types are accepted
	 * @param resAcc
	 * @return top-level resource specified or null if no top-level resource with the given name
	 * 		exists or the respective top-level resource has no fitting type
	 */
	public static <T extends Resource> T getTopLevelResource(String name, Class<T> type, ResourceAccess resAcc) {
		Resource r = resAcc.getResource(name);
		if((r != null) && type.isAssignableFrom(r.getResourceType())) {
			return (T)r;
		}
		return null;
	}
	/** Get top-level resource with standard name*/
	public static <T extends Resource> T getTopLevelResource(Class<T> type, ResourceAccess resAcc) {
		String name = type.getSimpleName().substring(0, 1).toLowerCase()+type.getSimpleName().substring(1);
		return getTopLevelResource(name, type, resAcc);
	}
	
	/** Get top-level resource with standard name*/
	public static <T extends Resource> T getOrCreateTopLevelResource(Class<T> type,
			ApplicationManager appMan) {
		String name = type.getSimpleName().substring(0, 1).toLowerCase()+type.getSimpleName().substring(1);
		return getOrCreateTopLevelResource(name, type, appMan);		
	}
	public static <T extends Resource> T getOrCreateTopLevelResource(String name, Class<T> type,
			ApplicationManager appMan) {
		T result = getTopLevelResource(name, type, appMan.getResourceAccess());
		if(result != null)
			return result;
		result = appMan.getResourceManagement().createResource(name, type);
		result.activate(true);
		return result;
	}
	
	/** Get the resource of type {@link LocalGatewayInformation} that specifies the
	 * respective information for the OGEMA system
	 */
	public static LocalGatewayInformation getLocalGwInfo(ResourceAccess resAcc) {
		return getTopLevelResource("OGEMA_Gateway", LocalGatewayInformation.class, resAcc);
	}
	public static LocalGatewayInformation getLocalGwInfo(ApplicationManager appMan) {
		return getOrCreateTopLevelResource("OGEMA_Gateway", LocalGatewayInformation.class, appMan);
	}
	
	public static GatewayDevice getLocalDevice(ResourceAccess resAcc) {
		return getTopLevelResource("Gateway_Device", GatewayDevice.class, resAcc);
	}
	public static GatewayDevice getLocalDevice(ApplicationManager appMan) {
		return getOrCreateTopLevelResource("Gateway_Device", GatewayDevice.class, appMan);
	}

	public static EvalCollection getEvalCollection(ApplicationManager appMan) {
		return EvalHelper.getEvalCollection(appMan);
	}
	
	/**Works like {@link Resource#getSubResource(String, Class<? extends Resource>)}, but
	 * allows to specify a subPath over several sub resources. This resource preserves
	 * path information of the parent
	 * @param subPath path using separator '/'
	 * @param type type requested. If null then an existing resource of any type is returned, but no virtual resource
	 * @return resource of requested type (also virtual resource) or null if the path specified
	 * 		does not exist on the intermediate elements 
	 */
	public static <T extends Resource> T getSubResource(Resource parent, String subPath, Class<T> type) {
		String[] els = subPath.split("/");
		Resource cr = parent;
		for(int i=0; i<els.length; i++) {
			if(cr == null) return null;
			if(i == (els.length-1)) {
				if(type == null)
					return cr.getSubResource(els[i]);
				else
					return cr.getSubResource(els[i], type);
			}
			cr = cr.getSubResource(els[i]);
		}
		throw new IllegalStateException("we should never get here");		
	}
	/**Works like {@link Resource#getSubResource(String)}, but works over several sub resources even if
	 * the path contains references
	 * @param parent
	 * @param subPath path using separator '/'
	 * @return
	 */
	public static <T extends Resource> T getSubResource(Resource parent, String subPath) {
		return getSubResource(parent, subPath, null);
	}
	
	/**Provide list of model element resources from a list of patter objects*/
	public static <R extends Resource, P extends ResourcePattern<R>> List<R> patternToResourceList(List<P> patternList) {
		List<R> result = new ArrayList<>();
		for(ResourcePattern<R> p: patternList) {
			result.add(p.model);
		}
		return result;
	}

	/** Checks whether the resource has a parent, grant-parent etc. of the type
	 * given and returns it. This method does not require that the resource type is available in the Java
	 * classpath of the calling application as the name is just given as String.
	 * Below the parent a resource with the given subpath is searched.
	 * @param r resource for which location hierarchy shall be checked
	 * @param simpleOrFullTypeClassName name of the resource type, which must be a class extending
	 * 		resource. The first type in the hierarchy either mathing as simple or as full class
	 * 		name is accepted.
	 * @param subPath path using separator '/'
	 * @param type type requested. If null then an existing resource of any type is returned, but no virtual resource
	 * @return resource found or null if no such resource was found. A virtual resource may be returned if possible, but this
	 * 		is not guaranteed for non-existing resources
	 */
	public static <T extends Resource> T getSubResourceOfParent(Resource r, String simpleOrFullTypeClassName, String subPath, Class<T> type) {
		Resource parent = getFirstParentOfType(r, simpleOrFullTypeClassName);
		if(parent == null) return null;
		return getSubResource(parent, subPath, type);
	}
	/** See {@link #getSubResourceOfParent(Resource, String, String, Class)}, but we do not search for a parent of the given type, but for a
	 * sibbling below the direct parent here.
	 * 
	 * @param r
	 * @param simpleOrFullTypeClassName
	 * @param subPath
	 * @param type resource type of the sibbling requested
	 * @return
	 */
	public static <T extends Resource> T getSubResourceOfSibbling(Resource r, String simpleOrFullTypeClassName, String subPath, Class<T> type) {
		Resource parent = r.getParent();
		if(parent == null) return null;
		for(Resource sib: parent.getSubResources(false)) {
			if(sib.getResourceType().getSimpleName().equals(simpleOrFullTypeClassName) ||
					sib.getResourceType().getName().equals(simpleOrFullTypeClassName)) {
				return getSubResource(sib, subPath, type);
			}
		}
		return null;
	}
	
	public static boolean haveSameParentOfType(Resource resource1, Resource resource2, Class<? extends Resource> resType) {
		
		ApplicationManager appManPriv = UtilExtendedApp.getApplicationManager();
		final Resource res1ToUse;
		final Resource res2ToUse;
		if(appManPriv != null) {
			res1ToUse = appManPriv.getResourceAccess().getResource(resource1.getPath());
			res2ToUse = appManPriv.getResourceAccess().getResource(resource2.getPath());
		} else {
			res1ToUse = resource1;
			res2ToUse = resource2;
		}
		return (ResourceHelper.getFirstParentOfType(res1ToUse, resType)
				.equals(ResourceHelper.getFirstParentOfType(res2ToUse, resType)));
	}
	
	/** Information on the device to which a resource or a timeSeries belongs*/
	public static class DeviceInfo {
		/** Human readable device name. Note that this information may be null if no such name
		 * is provided by the source*/
		String deviceName;
		/** Device location or another unique deviceID. Required to be provided by the source.*/
		String deviceResourceLocation;
		/** Device type. Note that this information may be null if not be provided by the source*/
		Class<? extends PhysicalElement> deviceType;
		/** Reference to the Location object of the device. Note that this information may be null
		 * if not provided by the source. The Location resource may be accessible by applications even
		 * if the device resource may not be accessible based on the application permissions.
		 */
		Location deviceLocation;
		
		// getters and setters
		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public String getDeviceResourceLocation() {
			return deviceResourceLocation;
		}
		public void setDeviceResourceLocation(String deviceResourceLocation) {
			this.deviceResourceLocation = deviceResourceLocation;
		}
		public Class<? extends PhysicalElement> getDeviceType() {
			return deviceType;
		}
		public void setDeviceType(Class<? extends PhysicalElement> deviceType) {
			this.deviceType = deviceType;
		}
		public Location getDeviceLocation() {
			return deviceLocation;
		}
		public void setDeviceLocation(Location deviceLocation) {
			this.deviceLocation = deviceLocation;
		}
	}

	/** Find device to be used as primary device to which subResource shall be attached
	 * for user interaction etc. (see {@link #getDeviceResource()}). Here we do not return
	 * the resource, but the information usually required for user interaction, device
	 * identification etc.
	 * This method does not required resource permissions for the application on the
	 * device resource itself and thus should be used in preference to getDeviceResource.
	 * 
	 * @param subResource
	 * @return
	 */
	public static DeviceInfo getDeviceInformation(Resource subResource) {
		return getDeviceInformation(subResource, true);
	}
	public static DeviceInfo getDeviceInformation(Resource subResource, boolean locationRelevant) {
		return AccessController.doPrivileged(new PrivilegedAction<DeviceInfo>() {
			@SuppressWarnings("unchecked")
			public DeviceInfo run() {
				ApplicationManager appManPriv = UtilExtendedApp.getApplicationManager();
				final Resource inputResourceToUse;
				if(appManPriv != null) {
					inputResourceToUse = appManPriv.getResourceAccess().getResource(subResource.getPath());
				} else {
					inputResourceToUse = subResource;
					LoggerFactory.getLogger("UtilExtended").warn("Could not use Application Manager of util-extended for resource permissions");
				}
				PhysicalElement device = LogHelper.getDeviceResource(inputResourceToUse, locationRelevant);
				if(device == null) return null;
				DeviceInfo result = new DeviceInfo();
				result.deviceName = ResourceUtils.getHumanReadableShortName(device);
				result.deviceType = (Class<? extends PhysicalElement>) device.getResourceType();
				result.deviceLocation = device.location();
				result.deviceResourceLocation = device.getLocation();
				return result;
			}
		});		
	}
	
	public static <T extends Resource> T getSampleResource(Class<T> resourceType) {
		return AccessController.doPrivileged(new PrivilegedAction<T>() {
			@Override
			public T run() {
				ApplicationManager myAppMan = UtilExtendedApp.getApplicationManager();
				EvalCollection ec = EvalHelper.getEvalCollection(myAppMan);
				T sampleResource = ec.getSubResource("sampleForInit_"+resourceType.getSimpleName(), resourceType);
				return sampleResource;
			}
		});
	}
	
	public static <T extends Resource> boolean containsLocation(Collection<T> resList, T object) {
		for(T res: resList) {
			if(res.equalsLocation(object))
				return true;
		}
		return false;
	}
	
	/** Find resource that has the same relative resource structure path towards parentDestination as childTemplate has to parentTemplate and
	 * which has the same resource type as childTemplate. For the choice of each element the definition of
	 * {@link #getSubResourceBest(Resource, String, Class)} is used<br>
	 * types as the resource names may be different when stepping up from the parent resources.
	 * @return null if no such resource exists*/
	public static <T extends Resource> T getRelativeResource(Resource parentTemplate, T childTemplate, Resource parentDestination) {
		String parentPath = parentTemplate.getLocation();
		String childPath = childTemplate.getLocation();
		if(childPath.startsWith(parentPath)) {
			List<String> orgNames = new ArrayList<>();
			List<Class<? extends Resource>> resourceTypes = new ArrayList<>();
			Resource pathResource = childTemplate.getLocationResource();
			while(!pathResource.equalsLocation(parentTemplate)) {
				orgNames.add(0, pathResource.getName());
				resourceTypes.add(0, pathResource.getResourceType());
				pathResource = pathResource.getParent();
				if(pathResource == null)
					throw new IllegalStateException("Path resource does not meet parent:"+parentTemplate.getLocation()+" Child:"+childTemplate);
			}
			return getSubResourceBest(parentDestination, orgNames, resourceTypes);
		}
		while(!childPath.startsWith(parentPath)) {
			parentTemplate = parentTemplate.getLocationResource().getParent();
			parentDestination = parentDestination.getLocationResource().getParent();
			if(parentTemplate == null || parentDestination == null)
				return null;
			parentPath = parentTemplate.getLocation();
		}
		return getRelativeResource(parentTemplate, childTemplate, parentDestination);
	}

	/** Get sub resource of a certain type. The sub resource shall either be defined by the model
	 * or exist as a decorator. No virtual decorator shall be created
	 * @param <T>
	 * @param parent
	 * @param preferredName if a suitable sub resource with the preferred name exists or is defined in
	 * 		the model, it shall be used, otherwise any other sub resource of the type requested shall be
	 * 		returned
	 * @param resourceType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Resource> T getSubResourceBest(Resource parent, String preferredName,
			Class<T> resourceType) {
		Resource destRelRes = parent.getSubResource(preferredName);
		if(destRelRes != null && resourceType.isAssignableFrom(destRelRes.getResourceType())) {
			return (T) destRelRes;
		}
		List<? extends Resource> sameTypeResources = parent.getSubResources(resourceType, false);
		if(sameTypeResources.isEmpty())
			return null;
		return (T) sameTypeResources.get(0);	
	}
	
	/** Get sub resource below parent with a path from parent to the result where each element type
	 * is defined by one element of resourceTypes. The choice of the sub resource of each step going
	 * down from parent to the result is done according to {@link #getSubResourceBest(Resource, String, Class)}
	 * @param <T>
	 * @param parent
	 * @param preferredNames the size of preferredNames and resourceTypes must be equal
	 * @param resourceTypes
	 * @return a resource of the type of resourceTypes.get(resourceTypes.size()-1) or null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Resource> T getSubResourceBest(Resource parent, List<String> preferredNames,
			List<Class<? extends Resource>> resourceTypes) {
		Resource result = parent;
		for(int i=0; i<preferredNames.size(); i++) {
			result = getSubResourceBest(result, preferredNames.get(i), resourceTypes.get(i));
			if(result == null)
				return null;
		}
		return (T) result;
	}
	
	/** Get sub resource if existing without creation of unnessary virtual resources*/
	@SuppressWarnings("unchecked")
	public static <T extends Resource> T getSubResourceIfExisting(Resource parent, String name,
			Class<T> resourceType) {
		Resource destRelRes = parent.getSubResource(name);
		if(destRelRes != null && resourceType.isAssignableFrom(destRelRes.getResourceType())) {
			return (T) destRelRes;
		}
		return null;
	}

}