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

package de.iwes.widgets.api.services;

import java.util.List;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/** 
 * Provides human-readable names for resource types and concrete resources.  / currently in experimental status.
 * A typical name service contains predefined aliases for relevant resource types in relevant languages. A typical
 * name service can also determine a user-friendly name for a resource path based on aliases for typical sub
 * resources (e.g. "reading" in sensors) and based on resource name information stored in the resource database.<br>
 * 
 * Note that this service definition does not define any mechanisms to extend the alias database of a name service.
 * The idea is to provide an updated implementation of name service to extend the alias database.
 */
public interface NameService {
    
	/**
	 * See {@link #getName(Class, OgemaHttpRequest, boolean)}. Uses default setting
	 * useDefaultLanguage = true, i.e. if a name in the language provided is not available, a default language is selected. 
	 * If no name could be determined, null is returned.
	 */
	public String getName(Class<? extends Resource> resourceType, OgemaLocale locale);
	
	/**
	 * Returns a human-friendly name of the resource type provided, in the given language.<br>
	 * @param useDefaultLanguage 
	 * 		true: if a name is not found in the given language, a default name is provided (depends on implementation, English if available in default implementation)<br>
	 * 		false: if a name is not found in the given language null is returned 
	 * @return either a name or null, if a name has not been registered for the given resource type
	 */
	public String getName(Class<? extends Resource> resourceType, OgemaLocale locale, boolean useDefaultLanguage);	
	
	/**
	 * Get a list of resource types with available names.<br>
	 * Return empty list if no dictionary for the requested language is available.
	 */
	public List<Class<? extends Resource>> getAvailableTypes(OgemaLocale locale);
	
	/**
	 * Check whether a name is available for the given type
	 */
	public boolean isTypeAvailable(Class<? extends Resource> resourceType, OgemaLocale locale);
	
	/**
	 * See {@link #getName(Resource, OgemaLocale, boolean, boolean)}. Default values useRelativePathAsAlias = false and 
	 * useDefaultLanguage = true are used.
	 */
	public String getName(Resource resource, OgemaLocale locale);	
	/**
	 * Tries to determine a human-friendly name for the given resource with the strategy defined below.
	 * If none can be determined, null is returned. 
	 * The strategy of the default name service is as follows:
	 * <ul>
	 * 	<li>If the resource has a <code>name()</code>-subresource of type StringResource, the value of the latter is returned.
	 *       So for resources having a direct name subresource the argument locale is irrelevant</li>
	 *  <li>If any parent resource has a <code>name()</code>-subresource, the returned String is a concatenation of this name
	 *  	plus an alias for the relativ path from the parent to the original resource. If no alias is known for this, the behaviour 
	 *  	depends on the flag useRelativePathAsAlias. If it is true, the relative resource path is appended, if it is false (default), null is returned.</li>
	 *  <li>otherwise null is returned</li>	 
	 * </ul>
	 * @param locale language for which the resource name shall be returned
	 */
	public String getName(Resource resource, OgemaLocale locale, boolean useRelativePathAsAlias, boolean useDefaultLanguage);	
	
	//would require much overhead to implement this
//    public List<OgemaLocale> getAvailableLanguages(Class<? extends Resource> resourceType);
    
    public String getServletUrl() throws UnsupportedOperationException;
}
