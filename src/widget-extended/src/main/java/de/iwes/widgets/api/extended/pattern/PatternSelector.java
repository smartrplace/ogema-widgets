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
package de.iwes.widgets.api.extended.pattern;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.plus.SelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;

/**
 * A {@link OgemaWidget} which allows the user to select a {@link ResourcePattern} 
 * instance of the specified type P.
 * 
 * @param <P>
 * 		the pattern type
 */
public interface PatternSelector<P extends ResourcePattern<?>> extends SelectorTemplate<P> {
	
//	/**
//	 * Get the currently selected pattern. Returns null if no pattern is selected.
//	 * 
//	 * @param req
//	 * 		identifies the user session
//	 * @return
//	 */
//	P getSelectedItem(OgemaHttpRequest req);
//
//	/**
//	 * Set the selected pattern server-side.  
//	 * 
//	 * @param instance
//	 * @param req
//	 * 		identifies the user session
//	 */
//	void selectItem(P instance, OgemaHttpRequest req); 
//	
//	/**
//	 * Set a default selected pattern server-side. This applies to all new session that
//	 * will be created after calling this method.
//	 *   
//	 * @param instance
//	 */
//	void selectDefaultItem(P instance); 
	
	
}
