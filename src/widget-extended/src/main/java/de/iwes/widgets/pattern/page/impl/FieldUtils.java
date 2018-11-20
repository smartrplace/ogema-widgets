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
