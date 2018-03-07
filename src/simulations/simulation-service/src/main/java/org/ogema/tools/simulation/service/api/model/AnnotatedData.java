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

package org.ogema.tools.simulation.service.api.model;


/**
 * Super type for data that can be displayed in a User Interface. <br>
 * Use the fields {@link #getId()} to attach a meaningful ID to your data, and 
 * {@link #getDescription()} for a somewhat more extensive explanation. 
 * @author cnoelle
 *
 */
public interface AnnotatedData {

	/**
	 * A short ID for this setting, that can be displayed e.g. in a drop-down menu
	 * Must be a valid Java identifier, i.e. must not contain spaces, slashes, etc.
	 */
	String getId();
	
	/**
	 * A human-readable description of the setting.
	 */
	String getDescription();

}