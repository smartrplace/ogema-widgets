/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.pattern.widget.patternedit;

import java.util.Collection;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

public class PatternCreatorConfiguration<P extends ResourcePattern<R>, R extends Resource>  {
	
	private final Collection<Class<? extends R>> allowedTypes;
	private final Resource baseResource;
	private final Class<?> admissibleParentType;
	private final boolean allowNonToplevelParent;
	private String defaultResourceName = null;
	private boolean allowNonDefaultName = true;

	/**
	 * Uses only default values. Patterns will be created as top-level resources.
	 * @deprecated use {@link PatternCreatorConfigurationBuilder} instead
	 */
	@Deprecated
	public PatternCreatorConfiguration() {
		this(null, null);
	}
	
	/**
	 * @param baseResource
	 * 			if this is not null, patterns will be created as subresources, otherwise the DemandedModel will be created as top-level resource
	 * @deprecated use {@link PatternCreatorConfigurationBuilder} instead 
	 */
	@Deprecated
	public PatternCreatorConfiguration(Resource baseResource) {
		this(baseResource, null);
	}

	/**
	 * @param allowedTypes
	 * 			if the user shall be able to select between different resource types for the DemandedModel of the pattern, provide a list
	 * 			of the applicable types. May be null or empty, in wich case only the resource type declared in the pattern (the generic parameter)
	 * 			can be used.
	 * @deprecated use {@link PatternCreatorConfigurationBuilder} instead 
	 */
	@Deprecated
	public PatternCreatorConfiguration(Collection<Class<? extends R>> allowedTypes) {
		this(null, allowedTypes);
	}
	
	/**
	 * 
	 * @param baseResource
	 * 			if this is not null, patterns will be created as subresources, otherwise the DemandedModel will be created as top-level resource 
	 * @param allowedTypes
	 * 			if the user shall be able to select between different resource types for the DemandedModel of the pattern, provide a list
	 * 			of the applicable types. May be null or empty, in wich case only the resource type declared in the pattern (the generic parameter)
	 * 			can be used. 
	 * @deprecated use {@link PatternCreatorConfigurationBuilder} instead
	 */
	@Deprecated
	public PatternCreatorConfiguration(Resource baseResource, Collection<Class<? extends R>> allowedTypes) {
		this(baseResource, allowedTypes, null, true);
	}
	
	/**
	 * @param admissibleParentType
	 * 		Must be either of type <code>Class<? extends Resource></code> or <code>Class<? extends ResourcePattern<?>></code>
	 * 		Pass <code>Resource.class</code> in order to allow for all parent resources.
	 * @param allowNonToplevelParent
	 * @deprecated use {@link PatternCreatorConfigurationBuilder} instead
	 */
	@Deprecated
	public PatternCreatorConfiguration(Class<?> admissibleParentType, boolean allowNonToplevelParent) {
		this(null, null, admissibleParentType, allowNonToplevelParent);
	}
	
	/**
	 * @param admissibleParentType
	 * 		Must be either of type <code>Class<? extends Resource></code> or <code>Class<? extends ResourcePattern<?>></code>
	 * 		Pass <code>Resource.class</code> in order to allow for all parent resources.
	 * @param allowNonToplevelParent
	 * @param allowedTypes
	 * 			if the user shall be able to select between different resource types for the DemandedModel of the pattern, provide a list
	 * 			of the applicable types. May be null or empty, in wich case only the resource type declared in the pattern (the generic parameter)
	 * 			can be used. 
	 * @deprecated use {@link PatternCreatorConfigurationBuilder} instead
	 */
	@Deprecated
	public PatternCreatorConfiguration(Class<?> admissibleParentType, boolean allowNonToplevelParent, Collection<Class<? extends R>> allowedTypes) {
		this(null, null, admissibleParentType, allowNonToplevelParent);
	}
	
	/**
	 * 
	 * @param baseResource
	 * @param allowedTypes
	 * 			if the user shall be able to select between different resource types for the DemandedModel of the pattern, provide a list
	 * 			of the applicable types. May be null or empty, in wich case only the resource type declared in the pattern (the generic parameter)
	 * 			can be used. 
	 * @param admissibleParentType
	 * 		Must be either of type <code>Class<? extends Resource></code> or <code>Class<? extends ResourcePattern<?>></code>
	 * 		Pass <code>Resource.class</code> in order to allow for all parent resources.
	 * @param allowNonToplevelParent
	 */
	PatternCreatorConfiguration(Resource baseResource, Collection<Class<? extends R>> allowedTypes, 
			Class<?> admissibleParentType, boolean allowNonToplevelParent) {
		this.allowedTypes = allowedTypes;
		this.baseResource = baseResource;
		this.admissibleParentType = admissibleParentType;
		this.allowNonToplevelParent = allowNonToplevelParent;
	}
	
	public Collection<Class<? extends R>> getAllowedTypes() {
		return allowedTypes;
	}

	public Resource getBaseResource() {
		return baseResource;
	}

	public Class<?> getAdmissibleParentType() {
		return admissibleParentType;
	}

	public boolean isAllowNonToplevelParent() {
		return allowNonToplevelParent;
	}

	public void setDefaultResourceName(String defaultResourceName, boolean allowNonDefaultName) {
		this.defaultResourceName = defaultResourceName;
		this.allowNonDefaultName= allowNonDefaultName;
	}
	
	/**
	 * @return
	 * 		null, if a default name has not been set
	 */
	public String getDefaultResourceName() {
		return defaultResourceName;
	}
	
	public boolean isAllowNonDefaultName() {
		return allowNonDefaultName;
	}
	
}
