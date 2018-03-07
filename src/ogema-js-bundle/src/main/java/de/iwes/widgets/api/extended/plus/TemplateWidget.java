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

import java.util.List;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that is modeled on some class T. For instance, this can be
 * table whose rows represent objects of type T, or a dropdown menu whose
 * items correspond to T objects.
 *
 * @param <T>
 */
public interface TemplateWidget<T> extends OgemaWidget {
	
	/**
	 * @param resource
	 * @param req
	 * @return
	 */
	boolean addItem(T resource, OgemaHttpRequest req);
	
	/**
	 * 
	 * @param resource
	 * @param req
	 * @return
	 */
	boolean removeItem(T resource, OgemaHttpRequest req);
	
	List<T> getItems(OgemaHttpRequest req);
	
//	UpdateMode getUpdateMode();
//	
//	void setType(Class<? extends R> type, OgemaHttpRequest req);
//
//	Class<? extends R> getType(OgemaHttpRequest req);

}
