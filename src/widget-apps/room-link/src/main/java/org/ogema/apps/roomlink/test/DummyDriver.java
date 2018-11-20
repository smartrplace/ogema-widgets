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
package org.ogema.apps.roomlink.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.driverconfig.LLDriverInterface;
import org.ogema.model.devices.buildingtechnology.Thermostat;

public class DummyDriver implements LLDriverInterface, TimerListener {
	
	private final ApplicationManager am;
	
	public DummyDriver(ApplicationManager am) {
		this.am = am;
	}

	@Override
	public void addConnection(String hardwareIdentifier) {
	}

	@Override
	public void addConnectionViaPort(String portName) {
	}

	@Override
	public JSONObject showClusterDetails(String interfaceId, String device, String endpoint, String clusterId) {
		return new JSONObject();
	}

	@Override
	public JSONArray showDeviceDetails(String interfaceId, String deviceAddress) {
		return new JSONArray();
	}

	@Override
	public JSONArray showAllCreatedChannels() {
		return new JSONArray();
	}

	@Override
	public JSONObject showHardware() {
		return new JSONObject();
	}

	@Override
	public JSONObject showNetwork(String option) {
		return new JSONObject();
	}

	@Override
	public JSONObject scanForDevices() {
		synchronized (this) {
			if (scanRunning)
				return new JSONObject();
			scanRunning = true;
		}
		am.createTimer(5000,this);
		return new JSONObject();
	}

	@Override
	public String whichTech() {
		return "Room link test";
	}

	@Override
	public String whichID() {
		return "room-link-test";
	}

	@Override
	public JSONObject cacheDevices() {
		return new JSONObject();
	}

	boolean scanRunning = false;
	
	@Override
	public void timerElapsed(Timer timer) {
		try {
			timer.destroy();
			String s = getNewDummyPath();
			am.getLogger().debug("Room link test driver creating new dummy device");
			Thermostat th = am.getResourceManagement().createResource(s, Thermostat.class);
			th.temperatureSensor().reading().<TemperatureResource> create().setValue(299.52F);
			th.name().<StringResource> create().setValue("Dummy device");
			th.activate(true);
		} finally {
			synchronized (this) {
				scanRunning = false;
			}
		}
	}
	
	private final static String path = "roomLinkDummyDriverTestDevice";
	
	private String getNewDummyPath() {
		String s;
		Resource r;
		int cnt = 0;
		ResourceAccess ra = am.getResourceAccess();
		while (true) {
			s = path + cnt++;
			r = ra.getResource(s);
			if (r== null)
				return s;
		}
	}
	
	
	
}
