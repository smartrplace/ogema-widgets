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

package de.iwes.widgets.pattern.page.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

public class FieldUtils {

	
	/************** Methods copied more or less from advanced access ****************/
	
	/*
	 * Gets the resource of rad that corresponds to the field required. Returns
	 * null if the field is uninitialized or if the field does not correspond to
	 * a resource.
	 */
	public static Resource getResource(final Field field, ResourcePattern<?> rad) {
        final Resource result;
        try {
            Class<?> type = field.getType();
            if (!Resource.class.isAssignableFrom(type)) return null;
            result = (Resource) field.get(rad);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("Could not access field " + field.toGenericString() + " on a pattern");
        } 
        return result;
    } 

	public static Boolean[] setAccessiblePrivileged(final Field[] fields) {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean[]>() {

			@Override
			public Boolean[] run() {
				final Boolean[] initialState = new Boolean[fields.length];
				for (int i=0;i<fields.length;i++) {
					Field field = fields[i];
					boolean fieldAccessible = field.isAccessible();
					initialState[i] = fieldAccessible;
					if (!fieldAccessible) {
						field.setAccessible(true);
					}
					
				}
				return initialState;
			}
		});
	}
	
	public static void setUnaccessiblePrivileged(final Field[] fields, final Boolean[] targetState) {
		AccessController.doPrivileged(new PrivilegedAction<Void[]>() {

			@Override
			public Void[] run() {
				for (int i=0;i<fields.length;i++) {
					Field field = fields[i];
					boolean fieldAccessible = targetState[i];
					field.setAccessible(fieldAccessible);
					
				}
				return null;
			}
		});
		
	}
	
	// FIXME doPrivileged should no longer be necessary, as the whole constructor is called in a privileged method (tbc)
	static Boolean[] setAccessiblePrivileged(final Method[] methods) {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean[]>() {

			@Override
			public Boolean[] run() {
				final Boolean[] initialState = new Boolean[methods.length];
				for (int i=0;i<methods.length;i++) {
					Method method = methods[i];
					boolean fieldAccessible = method.isAccessible();
					initialState[i] = fieldAccessible;
					if (!fieldAccessible) {
						method.setAccessible(true);
					}
					
				}
				return initialState;
			}
		});
	}
	
	static void setUnaccessiblePrivileged(final Method[] methods, final Boolean[] targetState) {
		AccessController.doPrivileged(new PrivilegedAction<Void[]>() {

			@Override
			public Void[] run() {
				for (int i=0;i<methods.length;i++) {
					Method method = methods[i];
					boolean fieldAccessible = targetState[i];
					method.setAccessible(fieldAccessible);
					
				}
				return null;
			}
		});
		
	}
	 
	
}
