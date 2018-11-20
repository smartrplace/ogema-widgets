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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.model.Resource;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.SimulationProviderListener;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;
import org.ogema.tools.simulation.service.impl.SimulationServiceImpl.SimListenerTask;
import org.slf4j.LoggerFactory;

/**
 * @author cnoelle
 */
@Component(specVersion = "1.2")
@Service(SimulationServiceAdmin.class)
public class SimulationServiceAdminImpl implements SimulationServiceAdmin {
	
	protected final Map<String, SimulationProvider<?>> simProviders = Collections.synchronizedMap(new LinkedHashMap<String, SimulationProvider<?>>());
	protected final List<SimulationProviderListener<? extends Resource>> listeners = Collections.synchronizedList(new LinkedList<SimulationProviderListener<? extends Resource>>());
	private final boolean secure;
	
	public SimulationServiceAdminImpl() {
		this.secure = System.getSecurityManager() != null;
	}
	
	public List<SimulationProvider<?>> getAllSimulationProviders() {
		synchronized (simProviders) {
			return new LinkedList<SimulationProvider<?>>(simProviders.values());
		}
	}

	public List<SimulationProvider<?>> getSimulationProviders(Class<? extends Resource> simulatedDeviceType) {
		List<SimulationProvider<?>> list = new LinkedList<>();
		synchronized(simProviders) {
			Iterator<SimulationProvider<?>> it = simProviders.values().iterator();
			while (it.hasNext()) {
				SimulationProvider<?> sp = it.next();
				if (simulatedDeviceType.isAssignableFrom(sp.getSimulatedType())) {
					list.add(sp);
				}
			}
		}
		return list;
	}
	
	public List<Resource> getAllSimulatedObjects() {
		return getSimulatedObjects(Resource.class);
	}
	
	public List<Resource> getSimulatedObjects(Class<? extends Resource> simulatedDeviceType) {
		List<Resource> list = new LinkedList<>();
		synchronized(simProviders) {
			Iterator<SimulationProvider<? extends Resource>> it = simProviders.values().iterator();
			while (it.hasNext()) {
				SimulationProvider<? extends Resource> sp = it.next();		
				if (simulatedDeviceType.isAssignableFrom(sp.getSimulatedType())) {			
					list.addAll(sp.getSimulatedObjects());
				}
			}
		}
		return list;
	}

	public List<Class<? extends Resource>> getSimulatedTypes() {
		List<Class<? extends Resource>> list = new LinkedList<>();
		synchronized(simProviders) {
			Iterator<SimulationProvider<?>> it = simProviders.values().iterator();
			while (it.hasNext()) {
				SimulationProvider<?> sp = it.next();
				Class<? extends Resource> type = sp.getSimulatedType();
				if (!list.contains(type)) {
					list.add(type);
				}
			}
		}
		return list;
	}

	@Override
	public void registerListener(SimulationProviderListener<? extends Resource> listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) listeners.add(listener);
		}
		final ExecutorService exec = Executors.newSingleThreadExecutor();
		final List<Future<?>> results = new ArrayList<>();
		synchronized(simProviders) {
			Iterator<SimulationProvider<?>> it = simProviders.values().iterator();
			while (it.hasNext()) {
				SimulationProvider<?> provider = it.next();
				if (listener.getType().isAssignableFrom(provider.getSimulatedType())) {
					results.add(exec.submit(new SimListenerTask(true, listener, provider)));
				}
			}
		}
		for (Future<?> f: results) {
			try {
				f.get(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				LoggerFactory.getLogger(SimulationServiceImpl.class).warn("Error executing listener callback",e.getCause());
			} catch (CancellationException e) {
				LoggerFactory.getLogger(SimulationServiceImpl.class).error("Unexpected cancellation",e);
			} catch (TimeoutException e) {
				LoggerFactory.getLogger(SimulationServiceImpl.class).warn("Simulation listener callback timed out");
			} 
		}
		shutdownExec(exec);
	}

	@Override
	public void deregisterListener(SimulationProviderListener<? extends Resource> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	final void shutdownExec(final ExecutorService exec) {
		if (!secure)
			exec.shutdownNow();
		else {
			AccessController.doPrivileged(new ExecShutdown(exec));
		}
	}

	private static class ExecShutdown implements PrivilegedAction<Void> {
		
		private final ExecutorService exec;
		
		public ExecShutdown(ExecutorService exec) {
			this.exec = exec;
		}

		@Override
		public Void run() {
			exec.shutdownNow();
			return null;
		}
	}
	
}