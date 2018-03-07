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
