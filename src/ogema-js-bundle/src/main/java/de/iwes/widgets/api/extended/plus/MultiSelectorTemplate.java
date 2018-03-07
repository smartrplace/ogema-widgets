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
