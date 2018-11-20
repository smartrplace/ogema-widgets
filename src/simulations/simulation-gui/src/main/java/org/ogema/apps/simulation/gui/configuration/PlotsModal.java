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
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur F�rderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.apps.simulation.gui.configuration;

import java.util.Map;

import org.ogema.core.model.Resource;
import org.ogema.tools.grafana.base.InfluxFake;
import org.ogema.tools.simulation.service.api.SimulationProvider;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.popup.PopupData;
	
public class PlotsModal extends Popup {

	private static final long serialVersionUID = 1L;
	private final InfluxFake grafanaPlot;
	private final Alert alert;
	private boolean toggle = true;

	
	public PlotsModal(WidgetPageBase<?> page, String id, InfluxFake grafanaPlot, final Alert alert) {
		super(page, id, true);
		this.alert = alert;
		this.grafanaPlot = grafanaPlot;
		setForceUpdate(true);  // TODO this is missing in current widgets framework; possible to avoid?
		setHeader("Logged simulation data",null);
		setBody("<iframe src=\"/de/iwes/simulations/simulationsgui/singlePlots/index.html#/dashboard/script/scripted_async.js\" "
				+ "height=\"100%\" width=\"100%\" name=\"plotiframe\">Plot could not be rendered</iframe>",null);
	}
	
	/********* Public *******/
	
	public void setPanels(Map<String,Map> panels) {
		grafanaPlot.setPanels(panels);
	}
	
	/********* Options ******/
	
	public class PlotModalOptions extends PopupData {

		private SimulationProvider<? extends Resource> provider;
		private String deviceId;
		
		public PlotModalOptions(PlotsModal popup) {
			super(popup);
		}

		public SimulationProvider<? extends Resource> getProvider() {
			return provider;
		}

		public void setProviderId(SimulationProvider<? extends Resource> provider) {
			this.provider = provider;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		
	}
	
	public SimulationProvider<? extends Resource>  getProvider(OgemaHttpRequest req) {
		return getData(req).getProvider();
	}

	public void setProvider(SimulationProvider<? extends Resource>  provider, OgemaHttpRequest req) {
		getData(req).setProviderId(provider);
	}

	public String getDeviceId(OgemaHttpRequest req) {
		return getData(req).getDeviceId();
	}

	public void setDeviceId(String deviceId, OgemaHttpRequest req) {
	    getData(req).setDeviceId(deviceId);
	}
	
	/******** Inherited methods ****/
	
	@Override
	public PopupData createNewSession() {
		return new PlotModalOptions(this);
	}
	
	@Override
	public PlotModalOptions getData(OgemaHttpRequest req) {
		return (PlotModalOptions) super.getData(req);
	}
	
/*	@Override
	public void onGET(OgemaHttpRequest req) {
		String activeDevice = getDeviceId(req);
		if (activeDevice == null) return;
		StringBuilder titleHTML = new StringBuilder("Active device: ");
		titleHTML.append(activeDevice);
		setTitle(titleHTML.toString(),req);
		Map<String,Map> map = Utils.getInstance().getLoggedDataPanels(getProvider(req),activeDevice);
		// System.out.println("  logged data panels for " + activeDevice + ": " + map);
		grafanaPlot.setPanels(map);			
		return;
	} */
	
/*	private String getActiveDevice(OgemaHttpRequest req) {
		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
		return sessionData.getDeviceId();
	}
	
	private SimulationProvider<? extends Resource> getSimulationProvider(OgemaHttpRequest req) {
		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
		return sessionData.getProvider();
	} */
	
}

