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

import de.iwes.widgets.api.extended.plus.TemplateData;

public interface ResourceWidgetData<R extends Resource> extends TemplateData<R> {

//	/**
//	 * This method should only be called directly if the {@link ResourceWidget}
//	 * this belongs to has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 */
//	boolean addItem(R resource);
//	
//	/**
//	 * This method should only be called directly if the {@link ResourceWidget}
//	 * this belongs to has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 */
//	boolean removeItem(R resource);
//	
//	List<R> getItems();
	
	void setType(Class<? extends R> type);
	
	Class<? extends R> getType();
	
}
