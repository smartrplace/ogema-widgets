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

package de.iwes.widgets.name.service.impl;


import java.util.List;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public interface TypeDictionary  {

	public OgemaLocale getLocale();
	
	public String getName(Class<? extends Resource> type);
	
	public boolean isTypeAvailable(Class<? extends Resource> type);
	
	public List<Class<? extends Resource>> getAvailableTypes();
	
	public String getName(String relativePath, Class<? extends Resource> parentType);
    
}