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

package de.iwes.widgets.api.messaging;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * A message to the user for display in a user interface
 */
public interface Message {
	
	/**
	 * short summary, no more than 50 letters; return null or a default value if requested locale is not available.
	 * @param locale 
	 *      may be null, indicating default locale 
	 */
	String title(OgemaLocale locale);
	
	/**
	 * Actual message; return a default value if requested locale is not available.
	 * @param locale 
	 * 		may be null, indicating default locale 
	 */
	String message(OgemaLocale locale);
	
	/**
	 * Link to a user page, where more information about the message can be obtained. May return null.
	 */
	String link();
	
	/** 
	 * Priority
	 */
	MessagePriority priority();
	
}
