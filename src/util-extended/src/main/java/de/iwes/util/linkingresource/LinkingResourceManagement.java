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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

/** 
 * A LinkingResourceManagement is used if an application collects resources of a certain type
 * (e.g. heating valves), but needs to sort them by a linking resource (e.g. by the rooms in
 * which the heating valves are situated). So for each room all heating valves in the room
 * shall be stored. It is even possible to add ResourcePatterns of various types, if e.g.
 * not only heating valves, but also electrical heaters shall be included.
 * 
 * This class is thread-safe.
 * 
 * This implementation does not allow for a single pattern to be linked to multiple linking resources.
 * Furthermore, it stores linking resources by location, not by path, therefore it is not possible to 
 * distinguish between resources of different path, but equal location.
 * 
 * @author dnestle
 *
 * @param <R> linking resource that is referenced by the pattern object
 * @param <P> pattern object the application wants to use
 */
public class LinkingResourceManagement<R extends Resource, P extends ResourcePattern<?>> {

	// not much use of a synchronized map here, we need to explicitly synchronize all the time anyway
	protected final Map<R, List<P>> data = new HashMap<>();
	
	/** 
	 * Add pattern with its linkingResource
	 * @param pattern
	 * 		not null
	 * @param linkingResource
	 * 		not null
	 * @return true if linkingResource was added for the first time
	 */
	public boolean addElement(P pattern, R linkingResource) {
		Objects.requireNonNull(pattern);
		Objects.requireNonNull(linkingResource);
		final R link = linkingResource.getLocationResource();
		boolean res = false;
		synchronized (data) {
			if (getPatternForResourceLocation(linkingResource, pattern.model) != null) {
				throw new IllegalStateException("Resource "+pattern.model.getLocation()+
						" already registered for "+linkingResource.getLocation()+ "!");
			}
			List<P> patternList = data.get(link);
			if(patternList == null) {
				patternList = new ArrayList<>();
				data.put(link, patternList);
				//linkingResourceList.add(linkingResource.getLocation());
				res = true;
			}
			patternList.add(pattern);
		}
		return res;
	}

	/**Remove element.
	 * @param pattern
	 * @return true if all elements for this linkingResource are removed afterwards (linkingResource is
	 * not used anymore)
	 * */
	public boolean removeElement(final P pattern, R linkingResource) {
		Objects.requireNonNull(pattern);
		Objects.requireNonNull(linkingResource);
		final R link = linkingResource.getLocationResource();
		synchronized (data) {
			List<P> patternList = data.get(link);
			if(patternList == null) {
				return true;
			}
			patternList.remove(pattern);
			if (patternList.isEmpty()) {
				data.remove(link);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param pattern
	 * @return
	 * 		The linking resource (of type R) to which pattern was associated, or null, if none
	 */
	public R removeElement(P pattern) {
		if (pattern == null)
			return null;
		synchronized (data) {
			Iterator<Map.Entry<R, List<P>>> mapIt = data.entrySet().iterator();
			while (mapIt.hasNext()) {
				Map.Entry<R, List<P>> entry = mapIt.next();
				List<P> list = entry.getValue();
				Iterator<P> it = list.iterator();
				while (it.hasNext()) {
					P p = it.next();
					if (p.model.equalsLocation(pattern.model)) { // pattern equality may not be well defined 
						it.remove();
						if (list.isEmpty()) 
							mapIt.remove();
						return entry.getKey();
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @param keyLocation
	 * @return
	 * @deprecated use {@link #isEmpty(Resource)} instead
	 */
	@Deprecated
	public boolean isEmpty(final String keyLocation) {
		if (keyLocation == null) 
			return true;
		synchronized (data) {
			for (Map.Entry<R, List<P>> entry: data.entrySet()) {
				if (entry.getKey().getLocation().equals(keyLocation)) {
					return entry.getValue().isEmpty();
				}
			}
		}
		return true;
	}
	
	public boolean isEmpty(R key) {
		if (key == null) 
			return true;
		synchronized (data) {
			final List<P> list = data.get(key.getLocationResource());
			return (list == null) || list.isEmpty();
		}
	}
	
	public List<P> getElements(R key) {
		if (key == null)
			return Collections.emptyList();
		synchronized (data) {
			final List<P> l = data.get(key.getLocationResource());
			if (l == null || l.isEmpty())
				return Collections.emptyList();
			return new ArrayList<P>(l);
		}
	}
	
	public P getFirstElement(R key) {
		if (key == null)
			return null;
		synchronized (data) {
			List<P> list = data.get(key.getLocationResource());
			if ((list == null) || list.isEmpty()) 
				return null;
			return list.get(0);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 * 		null if there is no matching pattern, the matching pattern if there is exactly one.
	 * @throws IllegalStateException
	 * 		If more than one matching pattern exists
	 */
	@Deprecated
	public P getFirstAndOnlyElement(R key) throws IllegalStateException {
		if (key == null)
			return null;
		synchronized (data) {
			final List<P> list = data.get(key.getLocationResource());
			if ((list == null) || list.isEmpty()) 
				return null;
			if (list.size() > 1) 
				throw new IllegalStateException("LinkingResourceManagement on which AndOnly is called may not contain more than one element per linking resource!! Found "+list.size()+" elements for "+key.getLocation()+
						" First:"+list.get(0).model.getLocation()+" Second:"+list.get(1).model.getLocation());
			return list.get(0);
		}
	}
	
	/**
	 * Returns a pattern with model equal (modulo localisation) to <tt>res</tt>, if there is
	 * one associated to <tt>key</tt> in the linking resource management. Otherwise, null is returned.<br>
	 * 
	 * Note that the pattern corresponding to a resource <tt>res</tt> can also be constructed 
	 * directly, by passing <tt>res</tt> to the pattern constructor. This method informs you
	 * in addition about whether a pattern for the resource is associated to <tt>key</tt> 
	 * in the linking room management.
	 * @param key
	 * @param res
	 * @return
	 */
	public P getPatternForResourceLocation(R key, Resource res) {
		if (key == null || res == null)
			return null;
		synchronized (data) {
			List<P> list = data.get(key.getLocationResource());
			if((list == null) || list.isEmpty()) 
				return null;
			for(P pattern: list) {
				if (pattern.model.equalsLocation(res)) {
					return pattern;
				}
			}
		}
		return null;
	}
	
	public List<R> getLinkingResourceList() {
		synchronized (data) {
			return new ArrayList<>(data.keySet());
		}
	}
	
	public List<P> getAllPatterns() {
		List<P> result = new ArrayList<>();
		for (List<P> p : data.values()) {
			result.addAll(p);
		}
		return result;
	}
	
	public LinkingManagementAccess<R,P> getSingleResourceManagement(R key) {
		return new SingleResourceMgmtImpl(key);
	}
	
	protected class SingleResourceMgmtImpl implements LinkingManagementAccess<R, P> {
		
		protected final R key;
		
		public SingleResourceMgmtImpl(R key) {
			this.key = key.getLocationResource();
		}

		@Override
		public R getLinkingResource() {
			return key;
		}

		@Override
		public List<P> getElements() {
			return LinkingResourceManagement.this.getElements(key);
		}

		@Override
		public boolean isEmpty() {
			return LinkingResourceManagement.this.isEmpty(key);
		}

		@Override
		public int size() {
			synchronized (LinkingResourceManagement.this) {
				final List<P> l = data.get(key);
				if (l == null)
					return 0;
				return l.size();
			}
		}
		
	}
	
}
