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
package de.iwes.widgets.lazy.pages.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.ogema.core.application.Application;
import org.osgi.framework.Bundle;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.LazyWidgetPage.AppDelegate;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

class AppFactory {

	static Application wrapNoSecurity(final Bundle b, final Application delegate) {
		final Unloaded<AppDelegate> unloaded =  new ByteBuddy()
				  .subclass(LazyWidgetPage.AppDelegate.class)
				  .name(getValidClassName(b.getSymbolicName()))
				  .make();
		try {
			return unloaded.load(AppFactory.class.getClassLoader()).getLoaded().getConstructor(Application.class).newInstance(delegate);
		} catch (Exception e) {
			LoggerFactory.getLogger(AppFactory.class).warn("Application subclass generation failed, cannot load class ",e);
			return delegate;
		}
	}
	
	// we need an app delegate with the protection domain of the bundle that registered the page service
	static Application wrap(final Class<?> clazzIn, final Application delegate) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final Unloaded<AppDelegate> unloaded =  new ByteBuddy()
				  .subclass(LazyWidgetPage.AppDelegate.class)
				  .name("delegate." + clazzIn.getName())
				  .make();
		 Class<?> dynamicType = null;
		// https://dzone.com/articles/jdk-11-and-proxies-in-a-world-past-sunmiscunsafe
		if (ClassInjector.UsingLookup.isAvailable()) { // case Java >= v9
			// FIXME this method, even if it worked, probably generates the wrong protection domain -> subclass ClassLoadingStrategy?
			try {
			    final Method privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", 
			        Class.class, 
			        MethodHandles.Lookup.class);
//			    final Object privateLookup = privateLookupIn.invoke(null, AppDelegate.class, MethodHandles.lookup()); // fails  Cannot define a type in de.iwes.widgets.reveal-test-2 [179] with lookup based on de.iwes.widgets.ogema-gui-api [71]
			    final Object privateLookup = privateLookupIn.invoke(null, clazzIn, MethodHandles.lookup()); // fails  java.lang.IllegalArgumentException: class de.iwes.widgets.api.widgets.LazyWidgetPage$AppDelegate$ByteBuddy$LaWofL7c must be defined in the same package as de.iwes.widgets.reveal.test2.RevealTestPage/package
			    
			    final ClassLoadingStrategy<ClassLoader> strategy = ClassLoadingStrategy.UsingLookup.of(privateLookup);
			    dynamicType = unloaded.load(clazzIn.getClassLoader(), strategy)
						.getLoaded();
			} catch (NoSuchMethodException | LinkageError | IllegalStateException | IllegalArgumentException e) {
				LoggerFactory.getLogger(AppFactory.class).warn("Application subclass generation failed... trying fallback method " + e);
			}
//		} else if (ClassInjector.UsingReflection.isAvailable()) { // case Java < v9
		}
		if (dynamicType == null) { // generic fallback
			final ClassLoadingStrategy<ClassLoader> strategy = ClassLoadingStrategy.Default.INJECTION.with(clazzIn.getProtectionDomain());
			try {
				dynamicType = unloaded.load(clazzIn.getClassLoader(), strategy)
				 	.getLoaded();
			} catch (UnsupportedOperationException ignoreJava10Error) {} 
		}
		if (dynamicType == null) { // fallback for Java 10
			final ClassLoadingStrategy<ClassLoader> strategy = ClassLoadingStrategy.Default.WRAPPER.with(clazzIn.getProtectionDomain());
			dynamicType = unloaded.load(clazzIn.getClassLoader(), strategy)
			 	.getLoaded();

		}
		return (Application) dynamicType.getConstructor(Application.class).newInstance(delegate);
	}
	
	private static String getValidClassName(final String bsn) {
		final StringBuilder sb = new StringBuilder();
		sb.append("delegate.");
		Arrays.stream(bsn.replace('-', '.').split("\\."))
			.forEach(string -> {
				if (!Character.isJavaIdentifierStart(string.charAt(0)))
					sb.append('_');
				sb.append(string);
				sb.append('.');
			});
		sb.append("LazyPage");
		return sb.toString();
	}
	
}

