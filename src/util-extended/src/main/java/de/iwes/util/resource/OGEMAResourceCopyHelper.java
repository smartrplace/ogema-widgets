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

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.array.BooleanArrayResource;
import org.ogema.core.model.array.FloatArrayResource;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.OpaqueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.Transaction;

@SuppressWarnings("deprecation")
/** Allows to copy entire resources and sub resources with their sub resources and
 * allows to set a reference without the risk to delete any data.
 * @author dnestle
 */
public class OGEMAResourceCopyHelper {
	/** Operates like {@link Resource#setAsReference(Resource)}, but if the sub resource overwritten exists and is not a
	 * reference and other references point there the method makes sure that the sub resource tree is copied into a new
	 * top-level resource and existing references to the element are re-directed to the
	 * new top-level resource. If no references exist also no copy is made
	 * 
	 * @param res parent resource of setAsReference
	 * @param reference
	 */
	public static void setAsReferenceSafe(Resource res, Resource reference, ApplicationManager appMan) {
		setAsReferenceSafe(res, reference, appMan, false);
	}
	/** Operates like {@link #setAsReferenceSafe(Resource, Resource, ApplicationManager)},
	 * but makes a copy even if no references exist on the sub resource. It does not make a copy if
	 * res is a reference already, though
	 * @param copyAlwaysIfSubResource if false the method exactly behaves like
	 * 		link{setAsReferenceSafe(Resource res, Resource reference, ApplicationManager appMan)}
	 */
	public static void setAsReferenceSafe(Resource res, Resource reference, ApplicationManager appMan, boolean copyAlwaysIfSubResource) {
		if(!res.isReference(false)) {
    		List<Resource> usageList = res.getReferencingResources(null);
			if((usageList.size() > 0)||copyAlwaysIfSubResource) {
				//we have to make a copy
				Resource newRes = copySubResource(res, appMan);
				//re-direct references
				for(Resource u: usageList) {
					for(Resource s: u.getSubResources(false)) {
						if(s.isReference(false) && s.getLocation().equals(res.getLocation())) {
							s.setAsReference(newRes);
						}
					}
				}
			}
		}
		res.setAsReference(reference);
	}
	
	/** Copy subresource into a new top-level resource. All sub resources are also copied, but
	 * resources referenced are not copied, just the references pointing to the resources are also
	 * available in the copy destination.
	 * 
	 * @param source sub resource to copy
	 * @param newName name of new top-level resource
	 * @param copyActiveStatus if true the new top-level resource and the sub
	 * resources are set active if the corresponding source resources are also
	 * active. The respective activates will be done for each sub branch that is
	 * completely active after it is finished - so if the entire source resource
	 * is active, the activate will be performed at the end. If copyActiveStatus
	 * is false, the result will be completely inactive
	 */
	public static  <T extends Resource> T copySubResource(T source, String newName, ApplicationManager appMan,
			boolean copyActiveStatus) {
		@SuppressWarnings("unchecked")
		T dest = (T)appMan.getResourceManagement().createResource(newName, source.getResourceType());
		return copySourceToDest(source, dest, new CopyParams(appMan, copyActiveStatus, 0));
	}
	/**see {@link #copySubResource(Resource, String, ApplicationManager, boolean)}
	 */
	public static  <T extends Resource> T copySubResourceWithValuesReferenced(T source, String newName, ApplicationManager appMan,
			boolean copyActiveStatus) {
		@SuppressWarnings("unchecked")
		T dest = (T)appMan.getResourceManagement().createResource(newName, source.getResourceType());
		return copySourceToDest(source, dest, new CopyParams(appMan, copyActiveStatus, 1));
	}
	/**see {@link #copySubResource(Resource, String, ApplicationManager, boolean)}. The additional parameters
	 * and some more are grouped in the argument copyParameters.
	 */
	public static  <T extends Resource> T copySubResourceWithValuesReferenced(T source, String newName, CopyParams copyParams) {
		@SuppressWarnings("unchecked")
		T dest = (T)copyParams.appMan.getResourceManagement().createResource(newName, source.getResourceType());
		return copySourceToDest(source, dest, copyParams);
	}

	
	/**see {@link #copySubResource(Resource, String, ApplicationManager, boolean)}. The destination is not
	 * a top-level resource, but the element of a resource list
	 */
	public static  <T extends Resource> T copySubResourceIntoResourceList(ResourceList<T> list, T source, ApplicationManager appMan,
			boolean copyActiveStatus) {
		T dest = list.add();
		return copySourceToDest(source, dest, new CopyParams(appMan, copyActiveStatus, 0));
	}
	/**see {@link #copySubResource(Resource, String, ApplicationManager, boolean)}. The destination is not
	 * a top-level resource, but a virtual resource that is created or overwritten as destination
	 */
	public static  <T extends Resource> T copySubResourceIntoDestination(T virtualDestination, T source, ApplicationManager appMan,
			boolean copyActiveStatus) {
		return copySubResourceIntoDestination(virtualDestination, source,
				new CopyParams(appMan, copyActiveStatus, 0));
	}
	/**see {@link #copySubResourceIntoDestination(Resource, Resource, ApplicationManager, boolean)}.
	 * The additional parameters
	 * and some more are grouped in the argument copyParameters.
	 */
	public static  <T extends Resource> T copySubResourceIntoDestination(T virtualDestination, T source,
			CopyParams copyParams) {
		T dest = virtualDestination.create();
		return copySourceToDest(source, dest, copyParams);
	}
	
	/**Parameters for copying*/
	public static class CopyParams {
		public CopyParams(ApplicationManager appMan, boolean copyActiveStatus,
				int copyMode) {
			super();
			this.appMan = appMan;
			this.copyActiveStatus = copyActiveStatus;
			this.copyMode = copyMode;
		}
		public ApplicationManager appMan;
		public boolean copyActiveStatus;
		/**if true all resources copied (sources) are deactivated. This is automatically
		 * set true for copyMode == 1
		 */
		public boolean deactivateCopiedResources;
		/** copyMode:
		 * 0: full copy
		 * 1: withValuesReferenced
		 */
		public int copyMode;
		/** Resource is newly created resource that is also returned from the copy method
		 * Object argument/return not used => make null
		 * The method is called before the destination resource is activated*/
		public ResourceOperationCallback callback = null;
		
		/**sub resources of a type with type name ending on this are ignored
		 * (not checked for top-level of copy call)*/
		public List<String> noCopyTypes = new ArrayList<String>();
		
		//only visible in package
		Transaction trans = null;
		Transaction deactivateTrans = null;		
	}
	
	
	private static <T extends Resource> T copySourceToDest(T source, T dest, CopyParams cp) {
		cp.trans = null;
		cp.deactivateTrans = null;
		if(cp.copyActiveStatus) {
			cp.trans = cp.appMan.getResourceAccess().createTransaction();
		}
		if(cp.copyMode == 1) {
			cp.deactivateCopiedResources = true;
		}
		if(cp.deactivateCopiedResources) {
			cp.deactivateTrans = cp.appMan.getResourceAccess().createTransaction();
			cp.deactivateTrans.addResource(source);
		}
		if(ValueResource.class.isAssignableFrom(source.getResourceType())) {
			copyValue((ValueResource)source, (ValueResource)dest);
		}
		for(Resource sub: source.getSubResources(false)) {
			copySub2Sub(sub, dest, cp);
		}
		if(cp.copyActiveStatus) {
			if(cp.callback != null) {
				cp.callback.callback(dest, null);
			}
			prepareActivateCopy(source, dest, cp.trans);
			cp.trans.activate();
		}
		if(cp.deactivateTrans != null) {
			cp.deactivateTrans.deactivate();
		}
		return dest;
	}
	
	private static <T extends Resource> void prepareActivateCopy(T source, T dest, Transaction trans) {
		if(isEntireSubTreeActive(source)) {
			trans.addTree(dest, false);
			//dest.activate(true);
			return;
		}
		if(source.isActive()) {
			trans.addResource(dest);
			//dest.activate(false);
			for(Resource r: source.getDirectSubResources(false)) {
				if(r.isActive()) {
					prepareActivateCopy(r, dest.getSubResource(r.getName()), trans);
				}
			}
		}
	}
	private static <T extends Resource> boolean isEntireSubTreeActive(T res) {
		if(!res.isActive()) return false;
		for(Resource r: res.getDirectSubResources(false)) {
			if(!isEntireSubTreeActive(r)) return false;
		}
		return true;
	}
	/** Copy the value from one ValueResource to another of the same type*/
	public static <T extends ValueResource> void copyValue(T source, T dest) {
		if(FloatResource.class.isAssignableFrom(source.getResourceType())) {
			((FloatResource)dest).setValue(((FloatResource)source).getValue());
			return;
		}
		if(FloatArrayResource.class.isAssignableFrom(source.getResourceType())) {
			((FloatArrayResource)dest).setValues(((FloatArrayResource)source).getValues());
			return;
		}
		if(StringResource.class.isAssignableFrom(source.getResourceType())) {
			((StringResource)dest).setValue(((StringResource)source).getValue());
			return;
		}
		if(StringArrayResource.class.isAssignableFrom(source.getResourceType())) {
			((StringArrayResource)dest).setValues(((StringArrayResource)source).getValues());
			return;
		}
		if(TimeResource.class.isAssignableFrom(source.getResourceType())) {
			((TimeResource)dest).setValue(((TimeResource)source).getValue());
			return;
		}
		if(TimeArrayResource.class.isAssignableFrom(source.getResourceType())) {
			((TimeArrayResource)dest).setValues(((TimeArrayResource)source).getValues());
			return;
		}
		if(IntegerResource.class.isAssignableFrom(source.getResourceType())) {
			((IntegerResource)dest).setValue(((IntegerResource)source).getValue());
			return;
		}
		if(IntegerArrayResource.class.isAssignableFrom(source.getResourceType())) {
			((IntegerArrayResource)dest).setValues(((IntegerArrayResource)source).getValues());
			return;
		}
		if(BooleanResource.class.isAssignableFrom(source.getResourceType())) {
			((BooleanResource)dest).setValue(((BooleanResource)source).getValue());
			return;
		}
		if(BooleanArrayResource.class.isAssignableFrom(source.getResourceType())) {
			((BooleanArrayResource)dest).setValues(((BooleanArrayResource)source).getValues());
			return;
		}
		if(OpaqueResource.class.isAssignableFrom(source.getResourceType())) {
			((OpaqueResource)dest).setValue(((OpaqueResource)source).getValue());
			return;
		}
		if(Schedule.class.isAssignableFrom(source.getResourceType())) {
			((Schedule)dest).addValues(((Schedule)source).getValues(0));
			return;
		}
	}
	
	private static void copySub2Sub(Resource source, Resource destParent, CopyParams cp) {
		for(String s: cp.noCopyTypes) {
			if(source.getResourceType().getName().endsWith(s)) {
				return;
			}
		}
		if(source.isReference(false) || ((cp.copyMode==1)&&(ValueResource.class.isAssignableFrom(source.getResourceType())))) {
			if(source.isDecorator()) {
				destParent.addDecorator(source.getName(), source);
			} else {
				destParent.setOptionalElement(source.getName(), source);
			}
		} else {
			Resource newDest;
			if(source.isDecorator()) {
				newDest = destParent.addDecorator(source.getName(), source.getResourceType());
			} else {
				newDest = destParent.addOptionalElement(source.getName());
			}
			if(ValueResource.class.isAssignableFrom(source.getResourceType())) {
				copyValue((ValueResource)source, (ValueResource)newDest);
			}
			if((cp.deactivateTrans != null) && source.isActive()) {
				cp.deactivateTrans.addResource(source);
			}
			for(Resource newSource: source.getSubResources(false)) {
				copySub2Sub(newSource, newDest, cp);
				if(!source.isActive() && (cp.trans != null)) {
					prepareActivateCopy(newSource, newDest, cp.trans);
				}
			}	
		}
	}
	
	/** See copySubResource(Resource res, String newName). The name of the new top-level resource
	 * will be determined based on the resource type. The method will automatically choose a name not
	 * available before the execution of the method
	 */
	public static <T extends Resource> T copySubResource(T res, ApplicationManager appMan) {
		return copySubResource(res, getnewResourceName(res.getResourceType().getSimpleName(), appMan), appMan, true);
	}
	/** See copyResource(Resource res, ApplicationManager appMan). The resource is copied, but all
	 * ValueResources are set as references. This means, of course, that also schedules and decorators
	 * below ValueResources are the same for source and copy. The source will be entirely deactivated
	 * except for value resources and decorators (shadow resource).
	 */
	public static <T extends Resource> T copySubResourceWithValuesReferenced(T res, ApplicationManager appMan) {
		return copySubResourceWithValuesReferenced(res, getnewResourceName(res.getResourceType().getSimpleName(), appMan), appMan, true);
	}
	
	/** Get a top-level resource name beginning with baseName that does not exist yet. If the name already
	 * exists as many "i" characters are added as required to get a new name.*/
	public static String getnewResourceName(String baseName, ApplicationManager appMan) {
		String name = appMan.getResourceManagement().getUniqueResourceName(baseName);
		int i=0;
		while(appMan.getResourceAccess().getResource(name) != null) {
			i++;
			name = appMan.getResourceManagement().getUniqueResourceName(baseName+"_"+i);
		}
		return name;
	}
}
