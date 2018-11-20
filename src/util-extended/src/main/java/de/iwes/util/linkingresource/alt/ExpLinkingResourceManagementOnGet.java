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

import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

/**
 * Implements {@link ExperimentalLinkingResourceManagement} based on instantaneous pattern requests. 
 */
public class ExpLinkingResourceManagementOnGet<R extends Resource, P extends ContextSensitivePattern<?,R>> 
																						implements ExperimentalLinkingResourceManagement<R,P> {

	private final ResourcePatternAccess rpa;
	private final Class<P> pattern;
	
	public ExpLinkingResourceManagementOnGet(ResourcePatternAccess rpa, Class<P> pattern) {
		this.rpa = rpa;
		this.pattern = pattern;
	}
	
	@Override
	public List<P> getElements(R key) {
		return rpa.getPatterns(pattern, AccessPriority.PRIO_LOWEST);
	}

	@Override
	public P getFirstElement(R key) {
		final List<P> elements = getElements(key);
		return (elements.isEmpty() ? null : elements.get(0));
	}

	@Override
	public P getPatternForResourceLocation(R key, Resource res) {
		for (P p: getElements(key)) {
			if (p.model.equalsLocation(res))
				return p;
		}
		return null;
	}

	@Override
	public P getFirstAndOnlyElement(R key) throws IllegalStateException {
		final List<P> elements = getElements(key);
		if (elements.isEmpty())
			return null;
		if (elements.size() != 1)
			throw new IllegalStateException("List for " + key + " contains more than one element of type " + pattern.getSimpleName());
		return elements.get(0);
	}
	
	@Override
	public int getSize(R key) {
		return getElements(key).size();
	}
	
	@Override
	public boolean isEmpty(R key) {
		return getElements(key).isEmpty();
	}
	
}