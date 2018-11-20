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
package de.iwes.widgets.api.services;

import java.util.Set;
import javax.servlet.Servlet;
import org.ogema.core.model.Resource;

/**
 * Get an icon associated to a {@link Resource} or a Resource type.
 */
public interface IconService extends Servlet {
    
    /**
     * Returns the relative webpath to an icon for an OGEMA class. If there
     * is no icon available, the icon for the parent class will be used. Returns
     * an icon for Resource.class on worst-case.
     * @param res OGEMA resource.
     * @return relative webpath to the icon.
     */
    public String getIcon(Resource res);
    
    /**
     * Returns the relative webpath to an icon for an OGEMA class. If there
     * is no icon available, the icon for the parent class will be used. Returns
     * an icon for Resource.class on worst-case.
     * @param resourceClass OGEMA class.
     * @return relative webpath to the icon.
     */
    public String getIcon(Class<? extends Resource> resourceClass);
    
    /**
     * Returns provides a set of all OGEMA classes that were registered as a set .
     * @return a set with all registered classes.
     */
    public Set<Class<? extends Resource>> getAvailableTypes(); 
    
    /**
     * Checks if there is an icon registered for a given OGEMA class.
     * @param resourceType OGEMA class.
     * @return true if there was an icon registered, false otherwise.
     */
    public boolean isTypeAvailable(Class<? extends Resource> resourceType);
    
    /**
     * Returns an url that can be used to get icons through a servlet.
     * @return Servlet string
     * @throws UnsupportedOperationException 
     */
    public String getServletUrl() throws UnsupportedOperationException;

}
