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