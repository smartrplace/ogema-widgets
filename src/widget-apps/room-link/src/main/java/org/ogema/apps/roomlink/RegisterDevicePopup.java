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
package org.ogema.apps.roomlink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary;
import org.ogema.apps.roomlink.pattern.PhysicalElementPattern;
import org.ogema.apps.roomlink.pattern.RoomPattern;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.driverconfig.LLDriverInterface;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.pattern.widget.dragdropassign.PatternDragDropAssign;

public class RegisterDevicePopup extends Popup {

	private static final long serialVersionUID = 1L;
	final Label pairingModeActiveLabel;
	final Button submit;
	private final WidgetPage<RoomLinkDictionary> page;
	private final ConcurrentMap<String, LLDriverInterface> llDrivers;
	private final ResourceAccess ra;

	public RegisterDevicePopup(final WidgetPage<RoomLinkDictionary> page, String id, final ConcurrentMap<String, LLDriverInterface> llDrivers, 
			final PatternDragDropAssign<PhysicalElementPattern, RoomPattern> ddAssign, final ResourceAccess ra) {
		super(page, id, true);
		this.page = page;
		this.llDrivers = llDrivers;
		this.ra=ra;
		final Alert registerDevAlert = new Alert(page, "pairingMode_alert", "");
		registerDevAlert.setDefaultVisibility(false);
		
		final Dropdown driverSelector = new Dropdown(page, "registerDevice_driverSelector") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				Map<String,String> entries = new HashMap<>();
				for (Map.Entry<String, LLDriverInterface> entry: llDrivers.entrySet()) {
					entries.put(entry.getKey(), entry.getValue().whichTech());
				}
				// special homematic driver; optional dependency
				try {
					List<org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface> homematicConfig = ra.getResources(org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface.class);
					boolean addLocation = (homematicConfig.size() > 1);
					for (org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface config: homematicConfig) {
						if (config.isActive() && config.installationMode().isActive()) {
//						homematicSwitches.add(config.installationMode());
							if(addLocation)
								entries.put("homematic__" + config.getLocation(), "Homematic XML-RPC ("+config.getLocation()+")"); 
							else
								entries.put("homematic__" + config.getLocation(), "Homematic XML-RPC");						}
					}
				} catch (NoClassDefFoundError ignore) {}
				update(entries, req);
			}
			
		};
		submit = new Button(page, "registerDevice_submitBtn", "Scan for devices") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				String sel = driverSelector.getSelectedValue(req);
				LLDriverInterface driver = null;
				if (sel != null && !sel.startsWith("homematic__")) {
					driver = llDrivers.get(sel);
				}
				if ((driver == null && (sel == null || !sel.startsWith("homematic__"))) || isPairingModeActive(sel)) 
					disable(req);
				else 
					enable(req);
				setText(page.getDictionary(req).registerDeviceHeader(), req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String sel = driverSelector.getSelectedValue(req);
				LLDriverInterface driver = null;
				if (sel != null) {
					if (sel.startsWith("homematic__")) {
						String loc = sel.substring("homematic__".length());
						try {
							org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface homematicConfig = ra.getResource(loc);
							if (!homematicConfig.installationMode().stateFeedback().isActive() || !homematicConfig.installationMode().stateFeedback().getValue()) { 
								homematicConfig.installationMode().stateControl().<BooleanResource> create().setValue(true); 
								homematicConfig.installationMode().stateControl().activate(true);
							}
						} catch (NoClassDefFoundError e) { // should not happen
							registerDevAlert.showAlert(e.toString(), false, req);
							return;
						}
						
					}
					else 
						driver = llDrivers.get(sel);
				}
				if (driver == null && (sel == null || !sel.startsWith("homematic__"))) {
					registerDevAlert.showAlert("No driver selected", false, req);
					return;
				}
				// FIXME we need some status information from the driver, whether pairing is still running, whether devices are found, etc.
				if (driver != null)
					driver.scanForDevices();  // XXX not working for ZWave, which requires an additional name
				ddAssign.setPollingInterval(5000, req); 
				registerDevAlert.showAlert(page.getDictionary(req).pairingModeStartedAlert(sel.replace("homematic__", "")), true, req);
			}
			
			
		};
		driverSelector.triggerAction(submit, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		submit.triggerAction(registerDevAlert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		submit.triggerAction(submit, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		submit.triggerAction(ddAssign, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		Label driverSelectorLabel = new Label(page, "registerDevice_driverSelectroLabel", "Select a driver") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req).selectDriverLabel(), req);
			}
			
		};
		
		pairingModeActiveLabel = new Label(page, "registerDevice_pairingModeActive", "") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				String selected = driverSelector.getSelectedValue(req);
				if (isPairingModeActive(selected))
					setText(page.getDictionary(req).pairingModeActive(selected), req);
				else
					setText("", req);
			}
			
			
		};
		pairingModeActiveLabel.setDefaultColor("green");
		
		PageSnippet body = new PageSnippet(page, "registerDevice_Body", true);
		StaticTable tab = new StaticTable(2, 2);
		tab.setContent(0, 0, driverSelectorLabel).setContent(0, 1, driverSelector)
			.setContent(1, 1, pairingModeActiveLabel);
	
		driverSelector.triggerAction(pairingModeActiveLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		body.append(registerDevAlert, null).linebreak(null).append(tab, null);
//		.append(driverSelector, null).linebreak(null).append(submit, null);
		
		this.setBody(body, null);
		this.setFooter(submit, null);
	}

	@Override
	public void onGET(OgemaHttpRequest req) {
		@SuppressWarnings("unchecked")
		WidgetPage<RoomLinkDictionary> page = (WidgetPage<RoomLinkDictionary>) getPage();
		RoomLinkDictionary dict = page.getDictionary(req);
		setTitle(dict.registerDeviceTitle(), req);
		setHeader(dict.registerDeviceHeader(), req);
	}
	
	private boolean isPairingModeActive(String driverId) {
		if (driverId == null || driverId.equals(DropdownData.EMPTY_OPT_ID)) {
			return false;
		}
		for (Map.Entry<String, LLDriverInterface> entry: llDrivers.entrySet()) {
			if (entry.getKey().equals(driverId)) {
				// TODO no information available from the interface :(
				return false;
			}

		}
		// special homematic driver; optional dependency
		try {
			List<org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface> homematicConfig = ra.getResources(org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface.class);
			for (org.ogema.drivers.homematic.xmlrpc.hl.types.HmLogicInterface config: homematicConfig) {
				if (config.isActive() && config.installationMode().isActive() && ("homematic__" + config.getLocation()).equals(driverId)) {
					return config.installationMode().stateFeedback().getValue();
				}
			}
		} catch (NoClassDefFoundError ignore) {}
		return false;
	}
	
	
}
