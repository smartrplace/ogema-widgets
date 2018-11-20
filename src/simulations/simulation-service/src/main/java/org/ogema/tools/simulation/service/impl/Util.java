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
