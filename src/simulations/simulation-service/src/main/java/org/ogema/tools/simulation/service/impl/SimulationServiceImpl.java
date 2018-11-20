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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.model.Resource;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.SimulationProviderListener;
import org.ogema.tools.simulation.service.api.SimulationService;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;
import org.slf4j.LoggerFactory;

/**
 * @author cnoelle
 */
@Component(specVersion = "1.2")
@Service(SimulationService.class)
public class SimulationServiceImpl implements SimulationService {
		
	@Reference
	private SimulationServiceAdmin simulationServiceAdmin;
	
	private Map<String, SimulationProvider<?>> simProviders;
	private List<SimulationProviderListener<? extends Resource>> listeners;
	
	@Activate
	protected void start() {
		this.listeners = ((SimulationServiceAdminImpl) simulationServiceAdmin).listeners;
		this.simProviders = ((SimulationServiceAdminImpl) simulationServiceAdmin).simProviders;
	}
	
	public synchronized void registerSimulationProvider(final SimulationProvider<? extends Resource> provider) {
		if (provider  == null) return;
		synchronized(simProviders) {
			if (simProviders.containsKey(provider.getProviderId()))
				return;
			simProviders.put(provider.getProviderId(), provider);
		}
		final ExecutorService exec = Executors.newSingleThreadExecutor();
		final List<Future<?>> results = new ArrayList<>();
		synchronized(listeners) {
			Iterator<SimulationProviderListener<? extends Resource>> itl = listeners.iterator();
			while(itl.hasNext()) {
				SimulationProviderListener<?> listener = itl.next();
				Class<? extends Resource> type = listener.getType();
				if (type.isAssignableFrom(provider.getSimulatedType())) {
					results.add(exec.submit(new SimListenerTask(true, listener, provider)));
//					listener.simulationAvailable(provider);
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
		((SimulationServiceAdminImpl) simulationServiceAdmin).shutdownExec(exec);
	}
	
	public synchronized void unregisterSimulationProvider(SimulationProvider<? extends Resource> provider) {
		if (provider  == null) return;
		if (simProviders.remove(provider.getProviderId()) == null)
			return;
		final ExecutorService exec = Executors.newSingleThreadExecutor();
		final List<Future<?>> results = new ArrayList<>();
		synchronized(listeners) {
			Iterator<SimulationProviderListener<? extends Resource>> itl = listeners.iterator();
			while(itl.hasNext()) {
				SimulationProviderListener<?> listener = itl.next();
				Class<? extends Resource> type = listener.getType();
				if (type.isAssignableFrom(provider.getSimulatedType())) {
					results.add(exec.submit(new SimListenerTask(false, listener, provider)));
//					listener.simulationUnavailable(provider);
				}
			}
		}
		for (Future<?> f: results) {
			try {
				f.get(3, TimeUnit.SECONDS);
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
		((SimulationServiceAdminImpl) simulationServiceAdmin).shutdownExec(exec);
	}
	
	static class SimListenerTask implements Callable<Void> {
		
		private final boolean available;
		private final SimulationProviderListener<?> listener;
		private final SimulationProvider<?> provider;
		
		public SimListenerTask(boolean available, SimulationProviderListener<?> listener, SimulationProvider<?> provider) {
			this.available = available;
			this.listener = listener;
			this.provider = provider;
		}

		@Override
		public Void call() throws Exception {
			if (available)
				listener.simulationAvailable(provider);
			else
				listener.simulationUnavailable(provider);
			return null;
		}
		
	}
	
	

}