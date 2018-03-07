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

package org.ogema.tools.simulation.service.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.tools.simulation.service.api.SimulationService;
import org.slf4j.LoggerFactory;

public class Util {
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Resource> Class<M> getDemandedModel(final Class<? extends ResourcePattern<M>> radClass) {
		// copied from ResourcePatternAccess
		Class<M> resourceType;
		try {
			resourceType = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<M>>() {

				@Override
				public Class<M> run() throws Exception {
					Type genericSupertype = radClass.getGenericSuperclass();
					while (!(genericSupertype instanceof ParameterizedType)) {
						genericSupertype = ((Class) genericSupertype).getGenericSuperclass();
					}
					final ParameterizedType radtype = (ParameterizedType) genericSupertype;
					return (Class<M>) radtype.getActualTypeArguments()[0];
				}
				
			});
		} catch (PrivilegedActionException e) {
			LoggerFactory.getLogger(SimulationService.class).error("Could not create new configuration resource; permission missing",e);
			return null;
		}
		return resourceType;

	}

}
