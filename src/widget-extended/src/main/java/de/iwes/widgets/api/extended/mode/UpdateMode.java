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

package de.iwes.widgets.api.extended.mode;

/**
 * Determines how a resource or pattern widget is updated.<br> 
 * <ul>
 * 	<li>case MANUAL: the resources or patterns are set explicitly in the application</li>
 *  <li>case AUTO_ON_GET: the resources or patterns are reloaded whenever a GET request is sent.
 *  		This should be a good choice if the retrieval of all elements is expected to be not
 *  		very expensive, and not too many parallel requests are expected.</li>
 *  <li>case AUTO_USE_LISTENER: not implemented yet! This is reserved for a future extension, where the 
 *  		widget registers a resource or pattern listener internally.
 * </ul>
 */
public enum UpdateMode {
	
	AUTO_ON_GET, AUTO_USE_LISTENER, MANUAL

}
