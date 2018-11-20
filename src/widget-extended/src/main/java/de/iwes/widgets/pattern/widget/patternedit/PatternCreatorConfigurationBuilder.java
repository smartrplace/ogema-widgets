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
package de.iwes.widgets.pattern.widget.patternedit;

import java.util.Collection;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

public class PatternCreatorConfigurationBuilder<P extends ResourcePattern<R>, R extends Resource> {
	
	private Collection<Class<? extends R>> allowedTypes;
	private Resource baseResource;
	private Class<?> admissibleParentType;
	private boolean allowNonToplevelParent  =true;
	private String defaultResourceName = null;
	private boolean allowNonDefaultName = true;
	
	private PatternCreatorConfigurationBuilder() {
	}
	
	/**
	 * Get a new builder instance.
	 * @return
	 */
	public static <P extends ResourcePattern<R>, R extends Resource> PatternCreatorConfigurationBuilder<P,R> getInstance() {
		return new PatternCreatorConfigurationBuilder<>();
	}
	
	/**
	 * Create a new pattern creator configuration
	 * @return
	 */
	public PatternCreatorConfiguration<P, R> build() {
		final PatternCreatorConfiguration<P, R> config = 
				new PatternCreatorConfiguration<>(baseResource, allowedTypes, admissibleParentType, allowNonToplevelParent);
		config.setDefaultResourceName(defaultResourceName, allowNonDefaultName);
		return config;
	}

	/**
	 * If the user shall be able to select between different resource types for the DemandedModel of the pattern, provide a list
	 * of the applicable types. May be null or empty, in wich case only the resource type declared in the pattern (the generic parameter)
	 * can be used. Default value: null.
	 * @param allowedTypes
	 */
	public PatternCreatorConfigurationBuilder<P, R> setAllowedTypes(Collection<Class<? extends R>> allowedTypes) {
		this.allowedTypes = allowedTypes;
		return this;
	}

	/**
	 * If this is not null, patterns will be created as subresources, otherwise the DemandedModel will be created as top-level resource.
	 * Default value: null. 
	 * @param baseResource
	 */
	public PatternCreatorConfigurationBuilder<P, R> setBaseResource(Resource baseResource) {
		this.baseResource = baseResource;
		return this;
	}

	/**
	 * Default value: null
	 * @param admissibleParentType
	 * 	 	Must be either of type <code>Class&lt;? extends Resource&gt;</code> or <code>Class&lt;? extends ResourcePattern&lt;?&gt;&gt;</code>
	 * 		Pass <code>Resource.class</code> or null in order to allow for all parent resources.
	 */
	public PatternCreatorConfigurationBuilder<P, R> setAdmissibleParentType(Class<?> admissibleParentType) {
		this.admissibleParentType = admissibleParentType;
		return this;
	}

	/**
	 * If patterns are created as subresources, specify here whether non-toplevel parents are allowed.
	 * Default value: true.
	 * @param allowNonToplevelParent
	 */
	public PatternCreatorConfigurationBuilder<P, R> setAllowNonToplevelParent(boolean allowNonToplevelParent) {
		this.allowNonToplevelParent = allowNonToplevelParent;
		return this;
	}

	/**
	 * ?
	 * @param defaultResourceName
	 */
	public PatternCreatorConfigurationBuilder<P, R> setDefaultResourceName(String defaultResourceName) {
		this.defaultResourceName = defaultResourceName;
		return this;
	}

	/**
	 * ?
	 * @param allowNonDefaultName
	 */
	public PatternCreatorConfigurationBuilder<P, R> setAllowNonDefaultName(boolean allowNonDefaultName) {
		this.allowNonDefaultName = allowNonDefaultName;
		return this;
	}

}
