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
package de.iwes.tools.system.supervision;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

@Descriptor("OGEMA system supervision commands")
class ShellCommandsInitial {

	private final ServiceRegistration<ShellCommandsInitial> ownReg;
	private final ApplicationManager appMan;
	
	
	ShellCommandsInitial(ApplicationManager appMan, BundleContext ctx) {
		this.appMan = appMan;
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("osgi.command.scope", "ogmsys");
		props.put("osgi.command.function", new String[] { 
				"addSubResource",
				"createResource",
				"getResource",
				"getResources",
				"nrResources", 
				"nrSubresources",
				"subResources",
				"toplevelResources",
				"resourcesBySize"
		});
		this.ownReg = ctx.registerService(ShellCommandsInitial.class, this, props);
	}
	
	public void close() {
		ForkJoinPool.commonPool().submit(() ->  { try { ownReg.unregister(); } catch (Exception ignore) {} });
	}

	@Descriptor("Count the number of resources, optionally for a specific resource type")
	public int nrResources(
			@Descriptor("Resource type (full class name)")
			@Parameter(names= { "-t", "--type"}, absentValue = "")
			String type,
			@Descriptor("Count only toplevel resources") 
			@Parameter(names = { "-top" }, absentValue = "false", presentValue="true") 
			boolean toplevelOnly
			) {
		Class<? extends Resource> clazz = null;
		if (!type.isEmpty()) {
			clazz = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
			if (clazz == null)
				return 0;
		}
		if (toplevelOnly)
			return appMan.getResourceAccess().getToplevelResources(clazz).size();
		else
			return appMan.getResourceAccess().getResources(clazz).size();
	}
	
	@Descriptor("Count the number of subresources for a specific resource")
	public int nrSubresources(
			@Descriptor("Resource type (full class name)")
			@Parameter(names= { "-t", "--type"}, absentValue = "")
			String type,
			@Descriptor("Resource path") String path) {
		final Resource base = appMan.getResourceAccess().getResource(path);
		if (base == null) {
			System.out.println("Resource not found: " + path);
			return 0;
		}
		Class<? extends Resource> clazz = null;
		if (!type.isEmpty()) {
			clazz = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
			if (clazz == null)
				return 0;
		}
		if (clazz == null)
			return base.getDirectSubResources(true).size();
		else {
			final AtomicInteger cnt = new AtomicInteger(0);
			countSubresources(base, clazz, cnt);
			return cnt.get();
		}
	}

	@Descriptor("Create a new resource")
	public Resource createResource(
			@Descriptor("Resource path")
			String path,
			@Descriptor("Full resource type, such as 'org.ogema.model.locations.Room'")
			String type
			) {
		if (type.isEmpty() || path.isEmpty()) {
			System.out.println("Type and path must not be empty");
			return null;
		}
		final Resource existing = appMan.getResourceAccess().getResource(path);
		if (existing != null) {
			if (existing.exists()) {
				System.out.println("Resource " + path + " already exists: " + existing);
				return null;
			}
		}
		final Class<? extends Resource> resType = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
		if (resType == null) 
			return null;
		return appMan.getResourceManagement().createResource(path, resType);
	}
	
	@Descriptor("Add a subresource")
	public Resource addSubResource(
			@Descriptor("Parent resource path")
			String path,
			@Descriptor("New resource name")
			String name,
			@Descriptor("Full resource type, such as 'org.ogema.model.locations.Room'")
			String type
			) {
		if (type.isEmpty() || path.isEmpty() || name.isEmpty()) {
			System.out.println("Type, path and name must not be empty");
			return null;
		}
		if (path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		if (path.startsWith("/"))
			path = path.substring(1);
		final Resource parent = appMan.getResourceAccess().getResource(path);
		if (parent == null) {
			System.out.println("Parent " + path + " does not exist");
			return null;
		}
		final Class<? extends Resource> resType = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
		if (resType == null) 
			return null;
		if (!parent.exists())
			parent.create();
		return parent.addDecorator(name, resType);
	}
	
	
	@SuppressWarnings("unchecked")
	@Descriptor("Get toplevel resources")
	public List<Resource> toplevelResources(
			@Descriptor("Resource type (full class name)")
			@Parameter(names= { "-t", "--type"}, absentValue = "")
			String type) {
		Class<? extends Resource> clazz = null;
		if (!type.isEmpty()) {
			clazz = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
			if (clazz == null)
				return Collections.emptyList();
		}
		return (List<Resource>) appMan.getResourceAccess().getToplevelResources(clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Descriptor("Get sub resources of a specified resource")
	public List<Resource> subResources(
			@Descriptor("Resource type (full class name)")
			@Parameter(names= { "-t", "--type"}, absentValue = "")
			String type,
			@Descriptor("Search for recursive subresources?")
			@Parameter(names= { "-r", "--recursive"}, absentValue = "false", presentValue="true")
			boolean recursive,
			@Descriptor("Resource path") String path
			) {
		final Resource base = appMan.getResourceAccess().getResource(path);
		if (base == null) {
			System.out.println("Resource not found: " + path);
			return Collections.emptyList();
		}
		Class<? extends Resource> clazz = null;
		if (!type.isEmpty()) {
			clazz = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
			if (clazz == null)
				return Collections.emptyList();
		}
		if (clazz == null)
			return base.getDirectSubResources(recursive);
		else
			return (List<Resource>) base.getSubResources(clazz, recursive);
	}
	
	@Descriptor("Get the resource at the specified path")
	public Resource getResource(@Descriptor("Resource path") String path) {
		return appMan.getResourceAccess().getResource(path);
	}
	
	@SuppressWarnings("unchecked")
	@Descriptor("Get resources of a specified type.")
	public List<Resource> getResources(
			@Descriptor("Resource type (full class name)")
			@Parameter(names= { "-t", "--type"}, absentValue = "")
			String type) {
		Class<? extends Resource> clazz = null;
		if (!type.isEmpty()) {
			clazz = loadResourceClass(appMan.getAppID().getBundle().getBundleContext(), type);
			if (clazz == null)
				return Collections.emptyList();
		}
		return (List<Resource>) appMan.getResourceAccess().getResources(clazz);
	}
	
	@Descriptor("Get toplevel resources sorted by the number of subresources")
	public LinkedHashMap<Resource, Integer> resourcesBySize() {
		final LinkedHashMap<Resource, Integer> resourcesBySize = new LinkedHashMap<>();
		final List<Resource> resources = appMan.getResourceAccess().getToplevelResources(null);
		final Map<Resource, Integer> map = resources.stream()
			.collect(Collectors.toMap(Function.identity(), resource -> resource.getDirectSubResources(true).size()));
		if (map.isEmpty())
			return resourcesBySize;
		// XXX inefficient sorting...
		Map.Entry<Resource, Integer> nextCandidate = null;
		int candidateSize = -1;
		while (resourcesBySize.size() < map.size()) {
			for (Map.Entry<Resource, Integer> entry : map.entrySet()) {
				if (entry.getValue() > candidateSize && !resourcesBySize.containsKey(entry.getKey())) {
					nextCandidate = entry;
					candidateSize = entry.getValue();
				}
			}
			resourcesBySize.put(nextCandidate.getKey(), nextCandidate.getValue());
			nextCandidate = null;
			candidateSize = -1;
		}
		return resourcesBySize;
	}
	
	private static void countSubresources(final Resource r, final Class<? extends Resource> type, final AtomicInteger cnt) {
		for (final Resource sub : r.getDirectSubResources(false)) {
			if (type.isAssignableFrom(sub.getResourceType()))
				cnt.incrementAndGet();
			if (sub.isReference(false))
				return;
			countSubresources(sub, type, cnt);
		}
	}
	
    @SuppressWarnings("unchecked")
    private static Class<? extends Resource> loadResourceClass(final BundleContext ctx, final String className) {
    	try {
	 	    final Class<?> clzz = Class.forName(className);
		    if (Resource.class.isAssignableFrom(clzz))
			    return (Class<? extends Resource>) clzz;
	    } catch (ClassNotFoundException expected) {}
	    for (Bundle b : ctx.getBundles()) {
		    final ClassLoader loader = b.adapt(BundleWiring.class).getClassLoader();
		    if (loader == null)
		    	continue;
		    try {
			    final Class<?> clzz = loader.loadClass(className);
			    if (Resource.class.isAssignableFrom(clzz))
				    return (Class<? extends Resource>) clzz;
		    } catch (ClassNotFoundException expected) {}
	    } 
	    System.out.println("Resource type " + className + " not found.");
	    return null;
    }
	
}
