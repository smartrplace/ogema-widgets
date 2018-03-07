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
