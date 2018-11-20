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
