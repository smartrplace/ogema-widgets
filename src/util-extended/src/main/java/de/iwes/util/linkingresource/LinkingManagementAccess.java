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
package de.iwes.util.linkingresource;

import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

/**
 * Enables access to the linked patterns for a single linking resource only. Does not allow to
 * add or remove entries.
 * @param <R>
 * @param <P>
 */
public interface LinkingManagementAccess<R extends Resource, P extends ResourcePattern<?>> {
	
	/**
	 * The linking resource
	 * @return
	 */
	R getLinkingResource();
	
	/**
	 * The linked patterns
	 * @return
	 */
	List<P> getElements();
	
	/**
	 * is empty?
	 * @return
	 */
	boolean isEmpty();
	
	/**
	 * Number of linked patterns
	 * @return
	 */
	int size();
	
}
