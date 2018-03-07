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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.Converter;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.ogema.core.model.Resource;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.SimulationProviderListener;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;
import org.ogema.tools.simulation.service.api.model.SimulationResourceConfiguration;

@Component(specVersion = "1.2")
@Properties( { 
	@Property(name = "osgi.command.scope", value = "sim"),
	@Property(name = "osgi.command.function", value = { "getSimulatedObjects", "getSimulatedTypes", "getSimulationProviders",
			"getSimulationConfigs", "getSimulationValues", "startSimulation", "stopSimulation", "getSimulationListeners" }) 
})
@Service({ShellCommands.class,Converter.class})
@Descriptor("OGEMA simulation commands")
public class ShellCommands implements Converter {
	
	@Reference
	private SimulationServiceAdmin admin;
	
	@Descriptor("Get all simulation providers")
	public List<SimulationProvider<?>> getSimulationProviders() {
		return admin.getAllSimulationProviders();
	}
	
	@Descriptor("Get all simulated resources")
	public List<? extends Resource> getSimulatedObjects(
			@Descriptor("The simulation provider id")
			@Parameter(names= {"-p","--provider"},absentValue="")
			String simulationProviderId) {
		if (simulationProviderId.isEmpty())
			return admin.getAllSimulatedObjects();
		final Optional<SimulationProvider<?>> opt = admin.getAllSimulationProviders().stream()
			.filter(provider -> provider.id().equals(simulationProviderId))
			.findAny();
		if (!opt.isPresent()) {
			System.out.println("Provider " + simulationProviderId + " not found");
			return null;
		}
		return opt.get().getSimulatedObjects();
	}
	
	@Descriptor("Get all simulated resource types")
	public List<Class<? extends Resource>> getSimulatedTypes() {
		return admin.getSimulatedTypes();
	}
	
	@Descriptor("Stop a simulation")
	public boolean stopSimulation(
			@Descriptor("Location of the simulated resource")
			String resourceLocation) {
		final SimulationProvider<?> provider = getProviderByResourceLocation(resourceLocation);
		if (provider == null)
			return false;
		return provider.stopSimulation(resourceLocation);
	}
	
	@Descriptor("Start a simulation")
	public boolean startSimulation(
			@Descriptor("Location of the simulated resource")
			String resourceLocation) {
		final SimulationProvider<?> provider = getProviderByResourceLocation(resourceLocation);
		if (provider == null)
			return false;
		return provider.startSimulation(resourceLocation);
	}
	
	@Descriptor("Get the configurations for a specific simulation")
	public List<SimulationConfiguration> getSimulationConfigs(
			@Descriptor("Location of the simulated resource")
			String resourceLocation) {
		final SimulationProvider<?> provider = getProviderByResourceLocation(resourceLocation);
		if (provider == null)
			return null;
		return provider.getConfigurations(resourceLocation);
	}
	
	@Descriptor("Get the simulated quantities for a specific simulation")
	public List<SimulatedQuantity> getSimulationValues(
			@Descriptor("Location of the simulated resource")
			String resourceLocation) {
		final SimulationProvider<?> provider = getProviderByResourceLocation(resourceLocation);
		if (provider == null)
			return null;
		return provider.getSimulatedQuantities(resourceLocation);
	}
	
	@Descriptor("Get simulation provider listeners")
	public List<SimulationProviderListener<?>> getSimulationListeners() {
		SimulationServiceAdminImpl impl = (SimulationServiceAdminImpl) admin;
		final List<SimulationProviderListener<?>> listeners = impl.listeners;
		synchronized (listeners) {
			return new ArrayList<>(listeners);
		}
	}
	
	private SimulationProvider<?> getProviderByResourceLocation(final String resourceLocation) {
		final Optional<SimulationProvider<?>> opt0 =  admin.getAllSimulationProviders().stream()
				.filter(provider -> provider.getSimulatedObjects().stream().filter(resource -> resource.getLocation().equals(resourceLocation)).findAny().isPresent())
				.findAny();
		if (opt0.isPresent())
			return opt0.get();
		return admin.getAllSimulationProviders().stream()
				.filter(provider -> provider.getSimulatedObjects().stream().filter(resource -> resource.getPath().equals(resourceLocation)).findAny().isPresent())
				.findAny().orElse(null);
	}
	
	
	@Override
	public Object convert(Class<?> desiredType, Object in) throws Exception {
		return null;
	}

	@Override
	public CharSequence format(Object target, int level, Converter escape) throws Exception {
		if (target instanceof SimulationProvider<?>) {
			final SimulationProvider<?> provider = (SimulationProvider<?>) target;
			return "Simulation provider " + provider.id() + " for simulation objects " + provider.getSimulatedType().getSimpleName();
		}
		if (target instanceof SimulatedQuantity) {
			final SimulatedQuantity quantity = (SimulatedQuantity) target;
			return quantity.getDescription() + ": " + ValueResourceUtils.getValue(quantity.value());
		}
		if (target instanceof SimulationConfiguration) {
			if (target instanceof SimulationComplexConfiguration) {
				final SimulationComplexConfiguration config = (SimulationComplexConfiguration) target;
				return config.getDescription() + ": " + config.getValue();
			}
			if (target instanceof SimulationResourceConfiguration) {
				final SimulationResourceConfiguration config = (SimulationResourceConfiguration) target;
				return config.getDescription() + ": " + ValueResourceUtils.getValue(config.value());
			}
		}
		return null;
	}
	
}
