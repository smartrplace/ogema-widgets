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

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that displays a collection of resources of a specified type
 * 
 */
public interface ResourceWidget<R extends Resource> extends TemplateWidget<R> {
	 
//	/**
//	 * This method should only be supported if the widget has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 * @throws UnsupportedOperationException if the widget does not have update mode MANUAL.
//	 */
//	boolean addItem(R resource, OgemaHttpRequest req) throws UnsupportedOperationException;
//	
//	/**
//	 * This method should only be called if this widget has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 * @throws UnsupportedOperationException if the widget does not have update mode MANUAL.
//	 */
//	boolean removeItem(R resource, OgemaHttpRequest req) throws UnsupportedOperationException;
//	
//	List<R> getItems(OgemaHttpRequest req);
	
	UpdateMode getUpdateMode();
	
	void setType(Class<? extends R> type, OgemaHttpRequest req);

	Class<? extends R> getType(OgemaHttpRequest req);
}
