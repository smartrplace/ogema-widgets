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
