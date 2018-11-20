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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.CompoundResourceEvent;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.PatternChangeListener;
import org.ogema.core.resourcemanager.pattern.PatternListener;

class ResourceData<P extends ContextSensitivePattern<?, R>, R extends Resource> implements PatternListener<P>, PatternChangeListener<P> {
	
	private final Set<P> patterns = Collections.synchronizedSet(new LinkedHashSet<P>());
	private final R linkingResource;
	private final ExpLinkingResourceManagementListenerBased<R, P> management;
	
	ResourceData(R linkingResource, ExpLinkingResourceManagementListenerBased<R, P> management) {
		this(linkingResource, management, Collections.<P> emptyList());
	}
	
	ResourceData(R linkingResource, ExpLinkingResourceManagementListenerBased<R, P> management, Collection<P> initialPatterns) {
		for (P pattern: initialPatterns) {
			patternAvailable(pattern);
		}
		this.linkingResource = linkingResource;
		this.management = management;
	}

	@Override
	public void patternAvailable(P pattern) {
		final boolean empty;
		synchronized (patterns) {
			empty = patterns.isEmpty();
			patterns.add(pattern);
		}
		management.newElement(linkingResource, pattern, empty);
		if (management.activateChangeListener)
			management.rpa.addPatternChangeListener(pattern, this, management.pattern);
	}

	@Override
	public void patternUnavailable(P pattern) {
		final boolean empty;
		synchronized (patterns) {
			boolean contained = patterns.remove(pattern);
			if (!contained)
				return;
			empty = patterns.isEmpty();
		}
		management.elementRemoved(linkingResource, pattern, empty);
		if (management.activateChangeListener)
			management.rpa.removePatternChangeListener(pattern, this);
	}
	
	// we assume here that the linking resource did not change
	@Override
	public void patternChanged(P instance, List<CompoundResourceEvent<?>> changes) {
		management.elementChanged(linkingResource, instance, changes);
	}
	
	List<P> drain() {
		synchronized (patterns) {
			if (management.activateChangeListener) {
				for (P p : patterns) {
					management.rpa.removePatternChangeListener(p, this);
				}
			}
			return new ArrayList<>(patterns);
		}
	}
	
	List<P> getElements() {
		synchronized (patterns) {
			return new ArrayList<>(patterns);
		}
	}
	
	P getFirst() {
		synchronized (patterns) {
			return (patterns.isEmpty() ? null : patterns.iterator().next());
		}
	}
	
	int getSize() {
		return patterns.size();
	}
	
	boolean isEmpty() {
		return patterns.isEmpty();
	}
		
	boolean contains(P pattern) {
		return patterns.contains(pattern);
	}
	
}
