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
package de.iwes.util.linkingresource.alt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.CompoundResourceEvent;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

/**
 * Implements {@link ExperimentalLinkingResourceManagement} based on a PatternListener for {@link ContextSensitivePattern}.
 * Patterns are added and removed automatically.
 */
// TODO add patternChanged listener
public class ExpLinkingResourceManagementListenerBased<R extends Resource, P extends ContextSensitivePattern<?,R>> 
																						implements ExperimentalLinkingResourceManagement<R,P> {

	final ResourcePatternAccess rpa;
	final Class<P> pattern;
	private final Map<R, ResourceData<P, R>> resources = Collections.synchronizedMap(new LinkedHashMap<R, ResourceData<P, R>>());
	private final boolean setPatternsOnResourceInit = false; // TODO configurable 
	final boolean activateChangeListener = true; // TODO configurable
	
	public ExpLinkingResourceManagementListenerBased(ResourcePatternAccess rpa, Class<P> pattern) {
		this.rpa = rpa;
		this.pattern = pattern;
	}
	
	/**
	 * Callback, override in derived class
	 * @param linkingResource
	 * @param pattern
	 * @param isFirst
	 * 		true if there is currently no other matching pattern for linkingResource, false otherwise
	 */
	protected void newElement(R linkingResource, P pattern, boolean isFirst) {
	}
	
	/**
	 * Callback, override in derived class
	 * @param linkingResource
	 * @param pattern
	 * @param isLast
	 * 		true if there is no other matching pattern for linkingResource left, false otherwise
	 */
	protected void elementRemoved(R linkingResource, P pattern, boolean isLast) {
	}
	
	/**
	 * Only relevant if a pattern change listener is activated. 
	 * @param linkingResource
	 * @param pattern
	 * @param changes
	 */
	public void elementChanged(R linkingResource, P pattern, List<CompoundResourceEvent<?>> changes) {
	}
	
	/**
	 * Callback, override in derived class, if required. Unregister listeners here.
	 * @param linkingResource
	 * @param pattern
	 */
	protected void onClose(R linkingResource, P pattern) {
	}
	
	public R getFirstLinkingResource(P pattern) {
		synchronized (resources) {
			for (Map.Entry<R, ResourceData<P, R>> entry: resources.entrySet()) {
				if (entry.getValue().contains(pattern))
					return entry.getKey();
			}
		}
		return null;
	}
	
	public List<R> getLinkingResources(P pattern) {
		final List<R> result = new ArrayList<>();
		synchronized (resources) {
			for (Map.Entry<R, ResourceData<P, R>> entry: resources.entrySet()) {
				if (entry.getValue().contains(pattern))
					result.add(entry.getKey());
			}
		}
		return result;
	}
	
	public boolean addResource(R resource) {
		synchronized (resources) {
			if (resources.containsKey(resource))
				return false;
			// it will take a while until all pattern callbacks have been received. The application who registered the resource may 
			// want to access the pattern immediately, however, so we better add all patterns known at this time (??)
			final ResourceData<P, R> data;
			if (setPatternsOnResourceInit)
				data = new ResourceData<P,R>(resource, this, rpa.getPatterns(pattern, AccessPriority.PRIO_LOWEST, resource));
			else
				data = new ResourceData<P,R>(resource, this);
			resources.put(resource, data);
			rpa.addPatternDemand(pattern, data, AccessPriority.PRIO_LOWEST, resource);
			return true;
		}
	}
	
	public boolean removeResource(R resource) {
		final ResourceData<P, R> data = resources.remove(resource);
		if (data == null)
			return false;
		rpa.removePatternDemand(pattern, data);
		return true;
	}

	@Override
	public List<P> getElements(R key) {
		final ResourceData<P, R> data = resources.get(key);
		if (data == null)
			return Collections.emptyList();
		return data.getElements();
	}

	@Override
	public P getFirstElement(R key) {
		final ResourceData<P, R> data = resources.get(key);
		if (data == null)
			return null;
		return data.getFirst();
	}

	@Override
	public P getPatternForResourceLocation(R key, Resource res) {
		final ResourceData<P, R> data = resources.get(key);
		if (data == null)
			return null;
		for (P p: data.getElements()) {
			if (p.model.equalsLocation(res))
				return p;
		}
		return null;
	}

	@Override
	public P getFirstAndOnlyElement(R key) throws IllegalStateException {
		final ResourceData<P, R> data = resources.get(key);
		if (data == null || data.isEmpty())
			return null;
		if (data.getSize() != 1)
			throw new IllegalStateException("List for " + key + " contains more than one element of type " + pattern.getSimpleName());
		return data.getFirst();
	}
	
	@Override
	public int getSize(R key) {
		final ResourceData<P, R> data = resources.get(key);
		if (data == null)
			return 0;
		return data.getSize();
	}
	
	@Override
	public boolean isEmpty(R key) {
		final ResourceData<P, R> data = resources.get(key);
		return data == null || data.isEmpty();
	}
	
	public List<R> getLinkingResourceList() {
		synchronized (resources) {
			return new ArrayList<>(resources.keySet());
		}
	}
	
	public List<R> getNonEmptyLinkingResourceList() {
		final List<R> copy = new ArrayList<>();
		synchronized (resources) {
			for (Map.Entry<R, ResourceData<P, R>> entry: resources.entrySet()) {
				if (!entry.getValue().isEmpty())
					copy.add(entry.getKey());
			}
			return copy;
		}
	}
	
	public final void close() {
		R resource;
		ResourceData<P, R> data;
		synchronized (resources) {
			for (Map.Entry<R, ResourceData<P, R>> entry: resources.entrySet()) {
				data = entry.getValue();
				resource = entry.getKey();
				rpa.removePatternDemand(pattern, data);
				for (P pattern: data.drain()) {
					onClose(resource, pattern);
				}
			}
			resources.clear();
		}
	}

	
}
