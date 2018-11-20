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
package org.ogema.tools.simulation.service.apiplus;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.ResourceAlreadyExistsException;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.core.resourcemanager.Transaction;
import org.ogema.core.resourcemanager.pattern.PatternListener;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;
import org.ogema.tools.simulation.service.impl.Util;


/**
 * Super class for a {@link SimulationProvider}. Core implementation for SimulationProviders,
 * e.g. {@link SimulationBase} and domain-dependent simulation like in-room-simulations based on
 * RoomInsideSimBase.
 *  
 * @author cnoelle, dnestle
 */
@SuppressWarnings("rawtypes")
public abstract class SimulationBase<T extends SimulationPattern<?>, R extends ResourcePattern<?>>
		implements SimulationProvider, PatternListener<T> {
	
	public static final long DEFAULT_UPDATE_INTERVAL = 10000;
	/** override this to change update interval*/
	protected long getDefaultUpdateInterval(){return DEFAULT_UPDATE_INTERVAL;}
	private static final String BASE_RESOURCE = "OGEMASimulationConfiguration";
	
	protected final ApplicationManager appManager;
	protected final ResourcePatternAccess resourcePatternAccess;
	protected final OgemaLogger logger;
	
	protected final ResourceList<SimulationConfigurationModel> simConfigList;
	protected final Map<T, R> simulatedDevices = new HashMap<>(); 
	private final Map<T,SimulationPatternTimerListener> timerListeners = new HashMap<>();
	
	private final Class<T> simPatternClass;
	protected final Class<R> targetPatternClass;
	
	// to be filled by inheriting provider
	protected final Map<String,List<SimulationConfiguration>> configs = new LinkedHashMap<String, List<SimulationConfiguration>>();   
	protected final Map<String,List<SimulatedQuantity>> values = new LinkedHashMap<String, List<SimulatedQuantity>>();  
	
	private final boolean useIntervalListener;
	private final ResourceValueListener<TimeResource> updateIntervalListener;
	private final PatternListener<R> targetListener = new PatternListener<R>() {

		@Override
		public void patternAvailable(R pattern) {}

		@Override
		public void patternUnavailable(R pattern) {
			// System.out.println("     ??? patternUnavailable for target pattern " + pattern.model);
			/*T config = null;
			for (Map.Entry<T, R> entry: simulatedDevices.entrySet()) {
				if (entry.getValue().model.equalsLocation(pattern.model)) {
					config = entry.getKey();
					break;
				}
			}
			// FIXME
			System.out.println("       xxx config pattern not found ");

			if (config != null) {  // if the target no longer exists, we remove also the configuration resource
				config.model.delete();
			}*/
			resourcePatternAccess.removeIndividualPatternDemand(targetPatternClass, pattern, this);
		}
	};
	
	
	/*
	 ***************** To be implemented in derived class *******************
	 */
	
	/**
	 * Perform the actual simulation step in this method. 
	 * @param targetPattern
	 * @param configPattern
	 * @param t
	 * @param timeStep
	 */
	public abstract void simTimerElapsed(R targetPattern, T configPattern, Timer t, long timeStep);
	/**
	 * Called once before the simulation starts
	 * @param targetPattern
	 * @param configPattern
	 */
	protected abstract void initSimulation(R targetPattern, T configPattern);
	/**
	 * Stop this simulation (usually does not require any particular action) and remove all listeners registered for it.
	 * @param targetPattern
	 * @param configPattern
	 */
	protected abstract void removeSimulation(R targetPattern, T configPattern);
	
	/**
	 * Add configurations to cfgs for a pattern (for a device)
	 * @param pattern
	 * @param cfgs
	 * @param simPattern
	 */
	abstract public void buildConfigurations(R pattern, List<SimulationConfiguration> cfgs, T simPattern); 
	/**
	 * Add SimulationQuantities to quantities for a pattern (for a device)
	 * @param pattern
	 * @param quantities
	 * @param simPattern
	 */
	abstract public void buildQuantities(R pattern, List<SimulatedQuantity> quantities, T simPattern);

	/*
	 ***********************************************************************
	 */
	
	// FIXME naming conflict in Java8!
	public final Timer getTimer(T configPattern) {
		if (configPattern == null)
			return null;
		SimulationPatternTimerListener listener = timerListeners.get(configPattern);
		if (listener == null) 
			return null;
		return listener.getTimer();
	}
	
	public final Timer getTimer(Resource targetResource) {
		T configPattern = null;
		for (T config: simulatedDevices.keySet()) {
			if (config.target.equalsLocation(targetResource)) {
				configPattern = config;
				break;
			}
		}
		return getTimer(configPattern);
	}
	
	@SuppressWarnings("unchecked")
	public SimulationBase(ApplicationManager am, Class<R> targetPatternClass) {
		this(am, targetPatternClass, true, (Class<T>) SimulationPatternStd.class);
	}
	
	/** Constructor:
	 * @param am
	 * @param targetPatternClass providing the patterns representing each simulated device. The demand for these
	 * patterns will be registered by SimulationBase
	 * @param useIntervalListener if true the simulation interval will be added as configuration to the provider
	 * 		(should usually be set except for simulations not using the timer)
	 */
	@SuppressWarnings("unchecked")
	public SimulationBase(final ApplicationManager am, Class<R> targetPatternClass, boolean useIntervalListener,
			final Class<T> simPatternClass) {
		this.useIntervalListener = useIntervalListener;
		if (useIntervalListener) 
			updateIntervalListener = new UpdateIntervalListener();
		else
			updateIntervalListener = null;
		this.appManager = am;
		this.resourcePatternAccess = am.getResourcePatternAccess();
		this.logger = am.getLogger();
		this.simPatternClass = simPatternClass;
		this.targetPatternClass = targetPatternClass;
		
		//Make sure list of simulation configurations exists
		
		simConfigList = AccessController.doPrivileged(new PrivilegedAction<ResourceList<SimulationConfigurationModel>>() {
			@Override
			public ResourceList<SimulationConfigurationModel> run() {
				ResourceList<SimulationConfigurationModel> simConfigListL = am.getResourceManagement().createResource(BASE_RESOURCE,ResourceList.class);
				simConfigListL.setElementType(SimulationConfigurationModel.class);

				resourcePatternAccess.addPatternDemand(simPatternClass, SimulationBase.this, AccessPriority.PRIO_LOWEST);
				return simConfigListL;
			}
		});
	}
	
	/**
	 * Called when the simulation provider is shut down.
	 * If necessary, override this. Don't forget to call <code>super.stop()</code> in this case.
	 */
	public void stop() {
		resourcePatternAccess.removePatternDemand(simPatternClass, this);
		Iterator<Entry<T, R>> it = simulatedDevices.entrySet().iterator();
		while (it.hasNext()) {
			Entry<T,R> entry = it.next();
			T configPattern = entry.getKey();
			configPattern.active = false;
			it.remove();
			if (useIntervalListener) {
				configPattern.updateInterval.removeValueListener(updateIntervalListener);
				SimulationPatternTimerListener tl = timerListeners.remove(configPattern);
				if (tl != null)
					tl.getTimer().destroy();
			}
			try { // user implemented, need to catch exceptions
				removeSimulation(entry.getValue(), entry.getKey());
			} catch (Exception e) {
				logger.warn("Error stopping simulation",e);
			}
		}

	}

	@Override
	public final List<Resource> getSimulatedObjects() {
		List<Resource> list = new LinkedList<>();
		Iterator<Entry<T, R>> it = simulatedDevices.entrySet().iterator();
		while (it.hasNext()) {
			R pattern = it.next().getValue();
			list.add(pattern.model);
		}
		return list;
	}
	
	protected final T getSimPattern(String deviceLocation) {
		Iterator<Entry<T, R>> it = simulatedDevices.entrySet().iterator();
		while (it.hasNext()) {
			Entry<T, R> ee = it.next();
			R target = ee.getValue();
			if (target.model.getLocation().equals(deviceLocation)) {
				return ee.getKey();
			}
		}
		return null;
	}

	protected final R getTargetPattern(String deviceLocation) {
		Iterator<Entry<T,R>> it = simulatedDevices.entrySet().iterator();
		while (it.hasNext()) {
			Entry<T, R> ee = it.next();
			R target = ee.getValue();
			if (target.model.getLocation().equals(deviceLocation)) {
				return target;
			}
		}
		return null;
	}
	
	/**Works like getTargetPattern, but tries to generate a new pattern if deviceLocation is null or
	 * empty string
	 * @param deviceLocation
	 * @return
	 */
	protected final R getAnyTargetPatternIfEmpty(String deviceLocation) {
		R result;
		if((deviceLocation != null) && (!(deviceLocation.equals("")))) {
			result = getTargetPattern(deviceLocation);
			if(result != null) return result;
			Resource targetResource = appManager.getResourceAccess().getResource(deviceLocation);
			if(targetResource == null) return null;
			List<R> patternList = resourcePatternAccess.getPatterns(targetPatternClass, AccessPriority.PRIO_LOWEST);
			for (R found: patternList) {
				if (found.model.equalsLocation(targetResource)) {
					return found;
				}
			}
			return null;
		}
		List<R> plist = appManager.getResourcePatternAccess().getPatterns(targetPatternClass, AccessPriority.PRIO_LOWEST);
		if(plist.isEmpty()) {
			return null;
		}
		else return plist.get(0);
	}

//	protected <RVAL extends Resource> void registerValueListener(T pattern, ResourceValueListener<RVAL> listener, RVAL res) {
//		res.addValueListener(listener);
//		String id = pattern.model.getLocation();
//		listeners.get(id).put(res, listener);
//	}

	@Override
	public void patternAvailable(final T pattern) {
		logger.debug(" +++ SimulationBase#patternAvailable {}", pattern);
		if(!pattern.simProviderId.getValue().equals(getProviderId())) {
			logger.warn("Got a pattern callback for a configuration resource of the correct type, "
					+ "but with invalid provider ID " + pattern.simProviderId.getValue() + " instead of " + getProviderId() + ". This might be an error");
			return;
		}
		//if(!targetPatternClass.isAssignableFrom(pattern.target.getResourceType())) {
		//	return;
		//}
		R targetPattern;
		try {
			targetPattern = AccessController.doPrivileged(new PrivilegedExceptionAction<R>() {

				@Override
				public R run() throws Exception {
//System.out.println("Privileged Constructor for:"+pattern.model.getLocation()+" / "+pattern.target+ "  targetPClass: "+targetPatternClass.getSimpleName()+"  X");
					Constructor<R> construct = targetPatternClass.getConstructor(Resource.class);
					return construct.newInstance(pattern.target);
				}
				
			});
			
			if(!resourcePatternAccess.isSatisfied(targetPattern, targetPatternClass)) {
				logger.warn("Got a simulation configuration with target pattern not satisfied {}", targetPattern);
				// this case has to be handled by the simulation provider; otherwise the configuration is lost completely
//				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		simulatedDevices.put(pattern,targetPattern);

		String id = pattern.model.getLocation();
		if (!configs.containsKey(id)) {
			List<SimulationConfiguration> cfgs = new LinkedList<SimulationConfiguration>();
			buildConfigurations(targetPattern, cfgs, pattern);
			configs.put(id,cfgs);
		}
		if (!values.containsKey(id)) {
			List<SimulatedQuantity> quantities = new LinkedList<SimulatedQuantity>();
			buildQuantities(targetPattern, quantities, pattern);
			values.put(id,quantities);
		}

		List<SimulationConfiguration> cfgs = configs.get(id); //must be available
		if(useIntervalListener) {
			cfgs.add(new SimulationInterval(pattern.updateInterval));
		}
//System.out.println("Activating simulation: "+pattern.model.getLocation()+" isSimActive:"+isSimulationActive(pattern));
		if (isSimulationActive(pattern)) return;
		//activateSimulation(pattern);
		//initSimulation(targetPattern, pattern);
		//Note: startSimulation contains activateSimulation, which calls initSimulation
		startSimulation(pattern); // FIXME if useIntervalListener == false, should this be called as well?
		// System.out.println("      +++ starting simulation "+getProviderId()+"; active " + isSimulationActive(pattern));
		if (isSimulationActive(pattern)) { // register a callback for the case that the target pattern disappears
			// System.out.println("    --- registering individual pattern listener");
			resourcePatternAccess.addIndividualPatternDemand(targetPatternClass, targetPattern, targetListener, AccessPriority.PRIO_LOWEST);
		}
	}
	
	@Override
	public void patternUnavailable(T pattern) {
		if (useIntervalListener) 
			pattern.updateInterval.removeValueListener(updateIntervalListener);
		R targetPattern = simulatedDevices.remove(pattern);
		resourcePatternAccess.removeIndividualPatternDemand(targetPatternClass, targetPattern, targetListener);
//		removeDeviceDemands(pattern.model.getLocation());
		if (useIntervalListener) {
			SimulationPatternTimerListener tl = timerListeners.remove(pattern);
			if (tl != null)
				tl.getTimer().destroy();
		}
		pattern.active = false;
		removeSimulation(targetPattern, pattern);
	}
	
	protected List<SimulationConfiguration> getConfigs(T pattern) {
		return configs.get(pattern.model.getLocation());
	}
	
	protected List<SimulatedQuantity> getValues(T pattern) {
		return values.get(pattern.model.getLocation());
	}
	
	@Override
	public List<SimulatedQuantity> getSimulatedQuantities(String deviceLocation) {
		T pattern = getSimPattern(deviceLocation);
		if (pattern == null) return null;
		return getValues(pattern);
	}
	
	@Override
	public List<SimulationConfiguration> getConfigurations(String deviceLocation) {
		T pattern = getSimPattern(deviceLocation);
		// FIXME it is problematic here that after creation of the simulation we need to wait for the listnere callback
		if (pattern == null) return null;
		return getConfigs(pattern);
	}
	
	@Override
	public boolean stopSimulation(String deviceLocation) {
		T pattern = getSimPattern(deviceLocation);
		return stopSim(pattern);
	}

	@Override
	public boolean isSimulationActive(String deviceLocation) {
		T pattern = getSimPattern(deviceLocation);
		return isSimulationActive(pattern);
	}
	@Override
	public boolean isSimulationActivatable(String deviceLocation) {
		return true;
	}
	
	protected void startSimulation(final T pattern) {
		if (pattern == null) {
			logger.warn("Tried to call startSimulation null for "+getProviderId());
			return;
		}
		if (useIntervalListener) {
			SimulationPatternTimerListener  timerListener = timerListeners.get(pattern);
			if (timerListener == null) {
				long itv = pattern.updateInterval.getValue();
				if (itv < 1000 ) itv = DEFAULT_UPDATE_INTERVAL;
				timerListener = new SimulationPatternTimerListener(pattern, itv);
				timerListeners.put(pattern, timerListener);
			}
		}
		activateSimulation(pattern);
		if (useIntervalListener) 
			pattern.updateInterval.addValueListener(updateIntervalListener, true);
	}
	
	@Override
	public boolean startSimulation(String deviceLocation) {
		T pattern = getSimPattern(deviceLocation);
		if (pattern == null) return false;
		startSimulation(pattern);
		return true;
	}
	
	protected boolean stopSim(T pattern) {
		if (pattern == null || !isSimulationActive(pattern)) return false;
		deactivateSimulation(pattern);
		return true;
	}
	
	/**
	 * Activates configuration together with newTarget model
	 */
	protected SimulationConfigurationModel addConfigResource(R newTargetPattern, long updateInterval)  {
		//
		@SuppressWarnings("unchecked")
		Class<? extends SimulationConfigurationModel> configModel = Util.getDemandedModel((Class) simPatternClass);
		
		SimulationConfigurationModel cr = simConfigList.add(configModel);
		String name = cr.getName();
		resourcePatternAccess.addDecorator(simConfigList, name, simPatternClass); // creates all non-optional resource fields in pattern type T
		cr.simulationProviderId().setValue(getProviderId());
		cr.target().setAsReference(newTargetPattern.model);
		if(updateInterval <= 0) {
			cr.updateInterval().setValue(DEFAULT_UPDATE_INTERVAL);
		} else {
			cr.updateInterval().setValue(updateInterval);			
		}
		Transaction trans = appManager.getResourceAccess().createTransaction();
		trans.addTree(cr, false);
		trans.addTree(newTargetPattern.model, false); 
		trans.activate();
		return cr;
	}
	
	public void activateSimulation(T configPattern) {
		if (useIntervalListener) {
			SimulationPatternTimerListener timerListener = timerListeners.get(configPattern);
			if (timerListener == null) {
				logger.warn("activateSimulation called before timer listener was set. Should probably not happen. Pattern: {}", configPattern.model);
				return;
			}
			Timer timer = timerListener.getTimer();
			if (!timer.isRunning()) timer.resume();
		}
//System.out.println("In activateSimulation for "+configPattern.target.getLocation()+" already active:"+configPattern.active);		
		if (!configPattern.active) {
			configPattern.active = true;
			initSimulation(simulatedDevices.get(configPattern), configPattern);
		} else {
			logger.warn("Called activateSimulation that was active already, skipped init for "+getProviderId());
		}
	}
	
	// this behaves slightly differently from patternUnavailable
	// in particular, the configuration is kept in the simulatedDevice list
	public void deactivateSimulation(T configPattern) {
		if (useIntervalListener) { 
			Timer timer = getTimer(configPattern);
			if (timer != null)
				timer.stop();
		}
		R val = simulatedDevices.get(configPattern);
		if(val != null) {
			removeSimulation(val, configPattern);
		}
		configPattern.active = false;
		logger.info("Deactivated simulation pattern {}",configPattern);
		//model.deactivate(true); 
	}
	
	public boolean isSimulationActive(T configPattern) {
		if (configPattern == null) 
			return false;
		if (useIntervalListener) {
			Timer timer = getTimer(configPattern);
			if (timer == null)
				return false;
			return timer.isRunning();
		}
		else { 
			return configPattern.active;
		}
	}
		
	private class UpdateIntervalListener implements ResourceValueListener<TimeResource> {
	
		@Override
		public void resourceChanged(TimeResource resource) {
			long interval = resource.getValue();
			if (interval <= 0) {
				resource.setValue(DEFAULT_UPDATE_INTERVAL); // triggers renewed callback
				return; 
			}
			final Resource configResource = resource.getParent(); // type SimulationConfigurationModel
			T pattern;
			try {
				pattern = AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {

					@Override
					public T run() throws Exception {
						Constructor<T> construct = simPatternClass.getConstructor(Resource.class);
						return construct.newInstance(configResource);
					}
					
				});
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Timer timer = getTimer(pattern);
			timer.setTimingInterval(interval);
		}
	}
	
	private class SimulationPatternTimerListener implements TimerListener {

		private final T configPattern;
		private final Timer timer;
		long lastTime = appManager.getFrameworkTime();
		
		public SimulationPatternTimerListener(T configPattern, long interval) {
			this.configPattern = configPattern;
			timer = appManager.createTimer(interval, this);
		}

		@Override
		public void timerElapsed(Timer timer) {
			long now = appManager.getFrameworkTime();
			try {
				simTimerElapsed(simulatedDevices.get(configPattern), configPattern, timer, now-lastTime);
			} catch (Exception e) {
				logger.warn("Exception in simulation timer",e);
			}
			lastTime = now;
		}

		public Timer getTimer() {
			return timer;
		}
		
	}
	
	@Override
	public Resource createSimulatedObject(String deviceLocation) {
		R pattern = getAnyTargetPatternIfEmpty(deviceLocation);		
		if (pattern == null) {
			try {
				if (deviceLocation.indexOf('/') > 0) {
					final int i = deviceLocation.lastIndexOf('/');
					Resource parent = appManager.getResourceAccess().getResource(deviceLocation.substring(0, i));
					if (parent == null || !parent.exists()) 
						throw new IllegalArgumentException("Specified parent resource " +deviceLocation.substring(0, i) + " does not exist");
					pattern = resourcePatternAccess.addDecorator(parent, deviceLocation.substring(i+1), targetPatternClass);
				}
				else {
					pattern = resourcePatternAccess.createResource(deviceLocation, targetPatternClass);
				}
				if(pattern.model instanceof PhysicalElement) {
					PhysicalElement modelPh = (PhysicalElement)(pattern.model);
					modelPh.name().create();
					modelPh.name().setValue(convertCamelToHumanReadble(deviceLocation));
				}
				logger.info("New "+targetPatternClass.getSimpleName()+" created "+ pattern.model.getLocation());
				// this activates the pattern and its configuration resource
			} catch (ResourceAlreadyExistsException e) {
				logger.warn("Could not create new simulated object " + deviceLocation, e);
				return null;
			}
		}
		// FIXME what if pattern configuration already exists? 
		addConfigResource(pattern, getDefaultUpdateInterval());
		return pattern.model;
	}
	
	// copied from util-extended NameFinder
	/**Converts a camel case String into a String that looks a bit nicer to non-programmers
	 * by inserting spaces and some other measures
	 * @param camelString
	 * @return human readable string
	 */
	private static String convertCamelToHumanReadble(String camelString) {
	    StringBuilder sb = new StringBuilder();
	    boolean init = false;
	    boolean previousWasUpper = false;
	    for (char c : camelString.toCharArray()) {
 	        if(!init) {
	        	init = true;
	        	if(Character.isLowerCase(c)) {
		        	sb.append(String.valueOf(c).toUpperCase());
	        		previousWasUpper = true;
	        	} else {
	    	        sb.append(c);
	        		previousWasUpper = false;
	        	}
	        	continue;
 	        }
	        if((!previousWasUpper)&&Character.isUpperCase(c)) {
	            sb.append(" ");
	        } else if((c == '_') || (c == '$')) {
	            sb.append(" ");
        		previousWasUpper = true;
        		continue;
	        }	        		
	        if(Character.isUpperCase(c)) {
        		previousWasUpper = true;
        	} else {
        		previousWasUpper = false;
        	}
	        sb.append(c);
	    }
	    return sb.toString();
	}
}
