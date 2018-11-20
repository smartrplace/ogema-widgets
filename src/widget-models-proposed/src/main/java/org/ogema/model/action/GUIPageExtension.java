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
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.action;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** Information for the extension of a certain page, e.g. a room
 *  @deprecated This mechanism is not supported anymore. Use OSGi extension services to extend OGEMA applications.*/
public interface GUIPageExtension extends Data {
	/**The resource for which the page is provided, e.g. a room. If the page is more
	 * general and not connected to a certain resource the element should not be present
	 */
	Resource item();
	
	/**URL of the page into which the children shall be embedded. This may especially be
	 * used if there is more than one page per item allowing for extensions or if item
	 * is not used
	 */
	StringResource pageURL();
	
	/**List of extensions provided by external apps*/
	ResourceList<GUIExtensionElement> children();
}
