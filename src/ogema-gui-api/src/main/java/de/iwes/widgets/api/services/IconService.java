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
