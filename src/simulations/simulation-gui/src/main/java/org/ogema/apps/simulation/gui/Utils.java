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
package org.ogema.apps.simulation.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.recordeddata.RecordedDataConfiguration;
import org.ogema.core.recordeddata.RecordedDataConfiguration.StorageType;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;

import de.iwes.widgets.html.form.dropdown.DropdownOption;

public class Utils {
	
	private final StorageType INITIAL_STORAGE_TYPE = StorageType.FIXED_INTERVAL;
	private final long INITIAL_LOG_INTERVAL = 60000; // 1 min
	protected ApplicationManager am; // initialized by the app's main class
	
	Utils(ApplicationManager am) {
		this.am = am;
	}
	
	public static Utils getInstance() {
		return getInstance(0);
	}
	
	// XXX ugly
	public static Utils getInstance(long delay) {
		if (delay > 0 && SimulationsGUI.utils == null) {
			for (int i=0; i<50; i++) {
				try {
					Thread.sleep(delay/50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				} 
				if (SimulationsGUI.utils != null)
					break;
			}
 		}
		return SimulationsGUI.utils;
	}
	
	@Deprecated
	public String getValidWidgetId(String in) {
		return ResourceUtils.getValidResourceName(in);
	}
	
	@Deprecated
	public String getValidResourceName(String in) {
		return ResourceUtils.getValidResourceName(in);
	}
	
	public String getNextResourceName(SimulationProvider<?> provider, ResourceAccess ra) {
		String prefix = "simulated_" + provider.getSimulatedType().getSimpleName() + "_";
		int counter = 0;
		boolean nameFound = false;
		Resource existing = ra.getResource(prefix + counter);
		if (existing != null)
			nameFound = true;
		while (nameFound) {
			counter++;
			existing = ra.getResource(prefix + counter);
			nameFound = (existing != null);
		}
		return prefix + String.valueOf(counter);
	}
	
	public <T extends Resource> T getLocationResource(T resource) {
		if (am == null || resource == null) return null;
		T locRes = am.getResourceAccess().getResource(resource.getLocation());
		return locRes;
	}
	public Resource getResource(String path) {
		return am.getResourceAccess().getResource(path);
	}
	
	public String getValue(SingleValueResource sv) {
		String result;
		if (sv instanceof StringResource) result = ((StringResource) sv).getValue();
		else if (sv instanceof BooleanResource) result = String.valueOf(((BooleanResource) sv).getValue()); 
		else if (sv instanceof IntegerResource) result = String.valueOf(((IntegerResource) sv).getValue());
		else if (sv instanceof TemperatureResource) result = String.valueOf(((TemperatureResource) sv).getCelsius());
		else if (sv instanceof FloatResource) result = String.valueOf(((FloatResource) sv).getValue());
		else if (sv instanceof TimeResource) result = String.valueOf(((TimeResource) sv).getValue() / 1000);
		else result = "";
		return result;
	}
	
	public boolean setValue(SingleValueResource sv, String value) {
		if (sv == null || value == null) return false;
		if (sv instanceof StringResource) ((StringResource) sv).setValue(value);
		else if (sv instanceof BooleanResource) {
			if (value.trim().toLowerCase().equals("true")) ((BooleanResource) sv).setValue(true);
			else if (value.trim().toLowerCase().equals("false")) ((BooleanResource) sv).setValue(false);
			else return false;
		}
		else if (sv instanceof IntegerResource) {
			try {
				((IntegerResource) sv).setValue(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				return false;
			}
		}
		else if  (sv instanceof TemperatureResource) {
			try {
				((TemperatureResource) sv).setCelsius(Float.parseFloat(value));
			} catch (NumberFormatException e) {
				return false;
			}
		}
		else if  (sv instanceof FloatResource) {
			try {
				((FloatResource) sv).setValue(Float.parseFloat(value));
			} catch (NumberFormatException e) {
				return false;
			}
		}
		else if  (sv instanceof TimeResource) {
			try {
				((TimeResource) sv).setValue(Long.parseLong(value)*1000L);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		else return false;
		return true;	
	}
	
	public boolean isDeviceControlled(SimulationProvider<? extends Resource> provider, String deviceId) {
		if (deviceId == null || provider == null) return false;
		List<? extends Resource> list = provider.getSimulatedObjects();
		Iterator<? extends Resource> it = list.iterator();
		boolean deviceFound = false;
		while(it.hasNext()) {
			if (it.next().getLocation().equals(deviceId)) {
				deviceFound = true;
				break;
			}
		}
		if (!deviceFound) return false;	
		return true;
	}
	
	
/*	public SimulationProvider<? extends Resource> getCreationProvider(WidgetPage<?> page, OgemaHttpRequest req) {
		SimulationAppSessionData sessionData = (SimulationAppSessionData) page.getSession(req);
		return sessionData.getCreationProvider();
	}*/
	
	public boolean isLogging(SingleValueResource sv) {
		RecordedDataConfiguration cfg = getRecordedDataConfiguration(sv);
		if (cfg == null) return false;
		return true;
	}
	
	public RecordedData getHistoricalData(SingleValueResource sv) {
		if (sv == null) return null;
		if (sv instanceof StringResource) return null;
		else if (sv instanceof BooleanResource) {
			return ((BooleanResource) sv).getHistoricalData();
		}
		else if (sv instanceof IntegerResource) {
			return ((IntegerResource) sv).getHistoricalData();
		}
		else if  (sv instanceof FloatResource) {
			return ((FloatResource) sv).getHistoricalData();
		}
		else if  (sv instanceof TimeResource) {
			return ((TimeResource) sv).getHistoricalData();
		}
		else return null;
		
	}
	
	private RecordedDataConfiguration getRecordedDataConfiguration(SingleValueResource sv) {
		RecordedData rd = getHistoricalData(sv);
		if (rd == null) return null;
		RecordedDataConfiguration cfg = rd.getConfiguration();
		return cfg;
	}
	
	public long getLoggingInterval(String resourcePath) {
		if (am == null || resourcePath == null) return -1L;
		SingleValueResource sv = am.getResourceAccess().getResource(resourcePath);
		RecordedDataConfiguration cfg = getRecordedDataConfiguration(getLocationResource(sv)); 
		if (cfg == null) return -1L;
		return cfg.getFixedInterval();
		
	}
	
	public void setLoggingInterval(String resPath,String itv) {
		if (am == null || resPath == null || itv == null) return;
		SingleValueResource sv = am.getResourceAccess().getResource(resPath);
		RecordedDataConfiguration cfg = getRecordedDataConfiguration(getLocationResource(sv));
		if (cfg == null) return;
		try {
			long time = Long.parseLong(itv);
			if (time <= 0) return;
			cfg.setFixedInterval(time * 1000L);
			RecordedData rd = getHistoricalData(getLocationResource(sv));
			rd.setConfiguration(cfg);
			// FIXME 
						System.out.println("  New logging interval selected " + resPath + ", " + cfg.getFixedInterval());
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	public static Set<DropdownOption> getLoggingTypes() {
		Set<DropdownOption> set = new LinkedHashSet<DropdownOption>();
		DropdownOption opt1 = new DropdownOption(StorageType.FIXED_INTERVAL.name(), StorageType.FIXED_INTERVAL.name(), true);
		DropdownOption opt2 = new DropdownOption(StorageType.ON_VALUE_CHANGED.name(), StorageType.ON_VALUE_CHANGED.name(), false);
		DropdownOption opt3 = new DropdownOption(StorageType.ON_VALUE_UPDATE.name(), StorageType.ON_VALUE_UPDATE.name(), false);
		set.add(opt1);set.add(opt2);set.add(opt3);
		return set;
	}
	
	public String getSelectedLoggingType(String resPath) {
		if (am == null || resPath == null) return null;
		SingleValueResource sv = am.getResourceAccess().getResource(resPath);
		RecordedDataConfiguration cfg = getRecordedDataConfiguration(getLocationResource(sv));
		if (cfg == null) return null;
		return cfg.getStorageType().toString();
	}
	
	public void setLoggingType(String resPath, String type) {
		if (am == null || resPath == null) return;
		SingleValueResource sv = am.getResourceAccess().getResource(resPath);
		RecordedDataConfiguration cfg = getRecordedDataConfiguration(getLocationResource(sv));
		if (cfg == null) return;
		try {
			cfg.setStorageType(StorageType.valueOf(type));
			RecordedData rd = getHistoricalData(getLocationResource(sv));
			rd.setConfiguration(cfg);
			// FIXME 
			System.out.println("  New logging type selected " + resPath + ", " + cfg.getStorageType().name());
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public String loggingSince(String resPath) {
		if (am == null || resPath == null) return "";
		SingleValueResource sv = am.getResourceAccess().getResource(resPath);
		RecordedData rd = getHistoricalData(getLocationResource(sv));
		if (rd == null) return "";
		List<SampledValue> values = rd.getValues(0);
		if (values.isEmpty()) return "";
		else {
			long time = values.get(0).getTimestamp();
			Date newDate = new Date(time);
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(newDate);
		}
	}
	
	public String numberLogPoints(String resPath) {
		if (am == null || resPath == null) return "";
		SingleValueResource sv = am.getResourceAccess().getResource(resPath);
		RecordedData rd = getHistoricalData(getLocationResource(sv));
		if (rd == null) return "";
		List<SampledValue> values = rd.getValues(0);
		return String.valueOf(values.size());
	}
	
	
	public boolean getSimQuLoggingStatus(String resourcePath) {
		if (am == null || resourcePath == null) return false;
		SingleValueResource sv = am.getResourceAccess().getResource(resourcePath);
		return isLogging(getLocationResource(sv));
	}
	
	public void setSimQuLoggingStatus(String deviceId, boolean loggingStatus) {
		if (am == null || deviceId == null) return;
		SingleValueResource sv = am.getResourceAccess().getResource(deviceId);
		setLogStatus(sv, loggingStatus);
	}
	
	private void setLogStatus(SingleValueResource sv, boolean loggingStatus) {
		RecordedData rd = getHistoricalData(sv);
		if (rd ==null) return;
		if (!loggingStatus) {
			rd.setConfiguration(null);
		} else if (rd.getConfiguration() == null) {
			RecordedDataConfiguration cfg = new RecordedDataConfiguration();
			cfg.setStorageType(INITIAL_STORAGE_TYPE);
			cfg.setFixedInterval(INITIAL_LOG_INTERVAL);
			rd.setConfiguration(cfg);
		}
	}
	
	public Map<String,Map> getLoggedDataPanels(SimulationProvider<? extends Resource> provider, String activeDevice) {
		if (provider == null || activeDevice == null) return null;
		Map<String,Map> map = new LinkedHashMap<String,Map>();
		List<SimulatedQuantity> list = provider.getSimulatedQuantities(activeDevice);
		if (list == null) return null;
		Iterator<SimulatedQuantity> it = list.iterator();
		while (it.hasNext()) {
			SimulatedQuantity sq = it.next();
			SingleValueResource svr = getLocationResource(sq.value());
			if (!isLogging(svr)) continue;
			Map entry = new LinkedHashMap<String, List<Resource>>();
			List<Resource> entryList = new ArrayList<Resource>();
			entryList.add(svr);
			entry.put(svr.getLocation(),entryList);
			map.put(svr.getLocation(), entry);
		}
		return map;
	}

}
