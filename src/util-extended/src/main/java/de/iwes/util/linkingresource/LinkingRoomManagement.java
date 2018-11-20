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
package de.iwes.util.linkingresource;

import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

/** A LinkingResourceManagement is used if an application collects resources of a certain type
 * (e.g. heating valves), but needs to sort them by a linking resource (e.g. by the rooms in
 * which the heating valves are situated). So for each room all heating valves in the room
 * shall be stored. It is even possible to add ResourcePatterns of various types, if e.g.
 * not only heating valves, but also electrical heaters shall be included.
 * @author dnestle
 *
 * @param <R> linking resource that is referenced by the pattern object
 * @param <P> pattern object the application wants to use
 */
public class LinkingRoomManagement<P extends ResourcePattern<?>> extends LinkingResourceManagement<Room, P> {
	
	protected final ResourceAccess resAcc;
	protected Room defaultValue = null;
	
	public LinkingRoomManagement(ResourceAccess resAcc) {
		this.resAcc = resAcc;
	}

	/** Add pattern with its room as linkingResource (as found by {@link RoomHelper.getResourceLocationRoom})
	 * @param pattern new pattern to be added
	 * @return true if room was added for the first time, null if no linking resource was found
	 */
	public Boolean addElement(P pattern) {
		ExtendedAddElementResult extRes = addElementExtended(pattern);
		if(extRes.linkingResource == null) return null;
		else return extRes.addedForTheFirstTime;
	}
	
	/** 
	 * Add pattern with its room as linkingResource (as found by {@link RoomHelper.getResourceLocationRoom})
	 * @param pattern new pattern to be added
	 * @return like {@link addElement}, but also the room that was identified as linking resource
	 */
	public ExtendedAddElementResult addElementExtended(P pattern) {
		if(!(pattern.model instanceof PhysicalElement)) 
			throw new IllegalArgumentException("Must be instance of PhysicalElement!");
		PhysicalElement modelPh = (PhysicalElement)(pattern.model);
		Room room = ResourceUtils.getDeviceLocationRoom(modelPh);
		if(room == null) {
			if(defaultValue != null) {
				room = defaultValue;
			} else {
				return new ExtendedAddElementResult(false, null);
			}
		}
		return new ExtendedAddElementResult(super.addElement(pattern, room), room);
	}
	
	/**Remove element.
	 * @param pattern
	 * @return true if all elements for this linkingResource are removed afterwards (linkingResource is
	 * not used anymore)
	 * @deprecated this does not work... the room may have changed in the meantime. Use {@link #removeElementSafe(ResourcePattern)}.
	 * */
	/*@Deprecated
	public boolean removeElement(P pattern) {
		if(!(pattern.model instanceof PhysicalElement)) throw new IllegalArgumentException("Must be instance of PhysicalElement!");
		PhysicalElement modelPh = (PhysicalElement)(pattern.model);
		Room room = ResourceUtils.getDeviceLocationRoom(modelPh);
		if(room == null) return true;
		return super.removeElement(pattern, room);
	}*/
	
	/** Set a default linking resource to be used if no room can be found
	 * 
	 * @param defaultValue to be used. If null no default value is used for
	 * future addElement calls
	 */
	public void setDefaultValue(Room defaultValue) {
		this.defaultValue = defaultValue;
	}
}
