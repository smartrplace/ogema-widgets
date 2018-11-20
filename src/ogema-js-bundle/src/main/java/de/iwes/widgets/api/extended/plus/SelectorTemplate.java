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
package de.iwes.widgets.api.extended.plus;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that allows to select an object of a specified type. For instance,
 * this can be a dropdown whose entries correspond to T objects.  
 *
 * @param <T>
 */
public interface SelectorTemplate<T> extends OgemaWidget {

	/**
	 * Get the currently selected item. Returns null if no item is selected.
	 * 
	 * @param req
	 * 		identifies the user session
	 * @return
	 */
	T getSelectedItem(OgemaHttpRequest req);

	/**
	 * Set the selected item server-side.  
	 * 
	 * @param item
	 * @param req
	 * 		identifies the user session
	 */
	void selectItem(T item, OgemaHttpRequest req); 
	
	/**
	 * Set a default selected item server-side. This applies to all new session that
	 * will be created after calling this method.
	 *   
	 * @param item
	 */
	void selectDefaultItem(T item); 
	
}
