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
package de.iwes.widgets.api.extended.resource;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.plus.SelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A {@link OgemaWidget} which allows the user to select a {@link Resource} 
 * of the specified type R.
 * 
 * @param <R>
 * 		the resource type
 */
public interface ResourceSelector<R extends Resource> extends SelectorTemplate<R> {
	
//	/**
//	 * Get the currently selected resource. Returns null if no resource is selected.
//	 * 
//	 * @param req
//	 * 		identifies the user session
//	 * @return
//	 */
//	R getSelectedItem(OgemaHttpRequest req);
//
//	/**
//	 * Set the selected resource server-side.  
//	 * 
//	 * @param resource
//	 * @param req
//	 * 		identifies the user session
//	 */
//	void selectItem(R resource, OgemaHttpRequest req); 
//	
//	/**
//	 * Set a default selected resource server-side. This applies to all new session that
//	 * will be created after calling this method.
//	 *   
//	 * @param resource
//	 */
//	void selectDefaultItem(R resource); 
	
	
}
