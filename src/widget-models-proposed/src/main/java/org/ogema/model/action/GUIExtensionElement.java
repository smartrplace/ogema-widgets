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

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** Intended to be provided by an application that offers an GUI into which other applicatins can embed some content
 *  @deprecated This mechanism is not supported anymore. Use OSGi extension services to extend OGEMA applications.*/
public interface GUIExtensionElement extends Data {
	/** Application that provides the child element, e.g. tile information*/
	StringResource providingApplication();
	
	/**First line (title) of tile to be displayed in parent app GUI*/
	StringResource tileName();
	/**Path to icon to be displayed on the tile*/
	StringResource iconPath();
	IntegerResource bundleId();
	/**Additional text to be displayed on the tile. Typically this text may be dynamic content providing
	 * some status information on the child app
	 */
	StringResource tileText();
	
	/**Destination URL opened when a click on the tile is performed. If not active no
	 * click action is performed
	 */
	StringResource clickUrl();
	
	/**If true the tile shall (usually temporarily) not be shown. As the GUIExtensionElement
	 * may still be used by applications it usually should not be de-activated as a resource,
	 * though.
	 */
	BooleanResource disableElement();
}
