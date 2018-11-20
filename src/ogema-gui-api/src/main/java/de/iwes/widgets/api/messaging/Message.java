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
