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

import java.util.Collection;
import java.util.List;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public interface MultiSelectorTemplate<T> extends OgemaWidget {

	/**
	 * Get the currently selected items. Returns null if no item is selected.
	 * 
	 * @param req
	 * 		identifies the user session
	 * @return
	 */
	List<T> getSelectedItems(OgemaHttpRequest req);

	/**
	 * Set the selected items server-side.  
	 * 
	 * @param items
	 * @param req
	 * 		identifies the user session
	 */
	void selectItems(Collection<T> items, OgemaHttpRequest req); 
	
	/**
	 * Set a default selected items server-side. This applies to all new session that
	 * will be created after calling this method.
	 *   
	 * @param items
	 */
	void selectDefaultItems(Collection<T> items); 

}
