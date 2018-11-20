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
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

/**
 * This interface defines the basic functions required to keep track of patterns associated to a resource.
 * E.g. devices associated to a room, etc.
 * 
 * @param <R> linking resource that is referenced by the pattern object
 * @param <P> pattern object the application wants to use
 */
public interface ExperimentalLinkingResourceManagement<R extends Resource, P extends ResourcePattern<?>> {

	List<P> getElements(R key);
	int getSize(R key);
	boolean isEmpty(R key);
	P getFirstElement(R key);
	P getPatternForResourceLocation(R key, Resource res);
	// FIXME use getSize() instead to verify the expected size?
	@Deprecated
	P getFirstAndOnlyElement(R key) throws IllegalStateException;
	
}
