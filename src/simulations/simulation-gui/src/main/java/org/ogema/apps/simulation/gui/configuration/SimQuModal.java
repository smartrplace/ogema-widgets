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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.apps.simulation.gui.Utils;
import org.ogema.core.model.Resource;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.popup.PopupData;
	
public class SimQuModal extends Popup {

	private static final long serialVersionUID = 1L;
	private final Alert alert;
	private final Dropdown dropdown;
	private final Label simQuId;
	private final Label simQuValue;
	private final Label simQuDescription;
	private final Checkbox loggingCheckbox;
	private final TextField loggingInterval;
	private final Dropdown loggingType;
	private final Label loggingIntervalLabel;
	private final Label loggingTypeLabel;
	private final Label loggingSinceLabel;
	private final Label loggingSince;
	private final Label loggingNumbersLabel;
	private final Label loggingNumbers;
	private static final String CHECKBOX_ID ="log values"; 
	private final WidgetGroup subWidgets;
	
	/************ Constructor ***************/
	
	public SimQuModal(WidgetPageBase<?> page, String id,final Alert alert) {
		super(page, id, true);
		this.alert = alert;
		this.dropdown = new SimQuDropdown(page, "simQuDropDown");
		this.simQuId = new Label(page,"simQuId","simulated quantity") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				setText(sessionData.getSimQuId(),req);
				String id = getSessionData(req).getSimQuId();
				setText(id, req);
			}
		};
		this.simQuValue = new Label(page, "simQuValue", "current value") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				setText(sessionData.getSimQuValue(),req);
				String value = getSessionData(req).getSimQuValue();
				setText(value, req);

			}
		};
		this.simQuDescription = new Label(page, "simQuDescription", "value description") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				setText(sessionData.getSimQuDescription(),req);
				String description = getSessionData(req).getDescription();
				setText(description, req);			
			}
		};
//		this.loggingCheckbox = new Checkbox(page, "simQuLoggingCheckbox", "", new HashMap<String,Boolean>()) {
		this.loggingCheckbox = new Checkbox(page, "simQuLoggingCheckbox") {

			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				SimQuModalOptions options =  getSessionData(req);
				String resPath = options.getSimQuPath();
				if (resPath.isEmpty()) {
					getCheckboxList(req).clear();
//					logConfigsVisible = false;
					options.setLogConfigsVisible(false);
				}
				else {
//					logConfigsVisible =  Utils.getInstance().getSimQuLoggingStatus(resPath);
					boolean visible = Utils.getInstance().getSimQuLoggingStatus(resPath);
					options.setLogConfigsVisible(visible);
					getCheckboxList(req).put(CHECKBOX_ID,visible);
				}
			}	
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				SimQuModalOptions options =  getSessionData(req);
				String resPath = options.getSimQuPath();
				Boolean bool = getCheckboxList(req).get(CHECKBOX_ID);
				if (bool == null ) bool = false; 
				options.setLogConfigsVisible(bool);
				Utils.getInstance().setSimQuLoggingStatus(resPath,bool);
			}
		};
		this.loggingIntervalLabel = new Label(page,"loggingIntervalLabel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				setWidgetVisibility(getSessionData(req).isLogConfigsVisible(),req);
			}
		};
		loggingIntervalLabel.setDefaultText("Interval (in s)");
		this.loggingTypeLabel = new Label(page,"loggingTypeLabel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				setWidgetVisibility(getSessionData(req).isLogConfigsVisible(),req);
			}
		};
		loggingTypeLabel.setDefaultText("Logging type");
		this.loggingInterval = new TextField(page, "loggingIntervalField") {
			
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				SimQuModalOptions options =  getSessionData(req);
				setWidgetVisibility(options.isLogConfigsVisible(),req);	
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				String resPath = options.getSimQuPath();
				long itv = Utils.getInstance().getLoggingInterval(resPath)/1000L;
				if (itv <= 0) setValue("",req);
				else setValue(String.valueOf(itv),req);
			}
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				String resPath = getSessionData(req).getSimQuPath();
				JSONObject obj = new JSONObject(data);
				if (!obj.has("data")) return;
				String itv = obj.getString("data");
				Utils.getInstance().setLoggingInterval(resPath, itv);
			}
			
		};
		loggingInterval.setDefaultPlaceholder("Logging interval");
		this.loggingType = new Dropdown(page, "loggingTypeDropdown") {
			
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				SimQuModalOptions options =  getSessionData(req);
				setWidgetVisibility(options.isLogConfigsVisible(),req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				String resPath = options.getSimQuPath();
				String selected = Utils.getInstance().getSelectedLoggingType(resPath);
				if (selected == null) {
					// what to do?
				}
				else selectSingleOption(Utils.getInstance().getSelectedLoggingType(resPath),req);
			}
			@Override
			public void onPrePOST(String json, OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
				SimQuModalOptions options =  getSessionData(req);
//				String resPath = sessionData.getSimQuPath();
				String resPath = options.getSimQuPath();
				JSONObject obj = new JSONObject(json);
				if (!obj.has("data")) return;
				String type = obj.getJSONArray("data").getString(0);
				Utils.getInstance().setLoggingType(resPath,type);
			}
			
		};
		Set<DropdownOption> dropdownOptions = Utils.getLoggingTypes();
		loggingType.setDefaultOptions(dropdownOptions);
	
		this.loggingSinceLabel = new Label(page,"loggingSinceLabel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				setWidgetVisibility(getSessionData(req).isLogConfigsVisible(),req);
			}
		};
		loggingSinceLabel.setDefaultText("Logging since");
		this.loggingSince = new Label(page,"loggingSince") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				SimQuModalOptions options =  getSessionData(req);
				setWidgetVisibility(options.isLogConfigsVisible(),req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				String resPath = options.getSimQuPath();
				setText(Utils.getInstance().loggingSince(resPath),req);
			}
		};
		
		this.loggingNumbersLabel = new Label(page,"loggingNumbersLabel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				setWidgetVisibility(getSessionData(req).isLogConfigsVisible(),req);
			}
		};
		loggingNumbersLabel.setDefaultText("# log data");
		this.loggingNumbers = new Label(page,"loggingNumbers") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				SimQuModalOptions options =  getSessionData(req);
				setWidgetVisibility(options.isLogConfigsVisible(),req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String resPath = sessionData.getSimQuPath();
				String resPath = options.getSimQuPath();
				setText(Utils.getInstance().numberLogPoints(resPath),req);
			}
		};
		
		List<OgemaWidgetBase<?>> sw = new LinkedList<OgemaWidgetBase<?>>();
		sw.add(dropdown);sw.add(simQuId);sw.add(simQuDescription);sw.add(simQuValue);sw.add(loggingCheckbox);
//		sw.add(this); // not nice... but necessary, since onGET method is used to set some values -> ugly!
		subWidgets = page.registerWidgetGroup("simQuModalWidgets",(Collection) sw);
		
/*		this.triggerAction(dropdown.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(simQuId.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(simQuDescription.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(simQuValue.getId(),TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(loggingCheckbox.getId(),TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST); */
		this.triggerAction(alert,TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(simQuId, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(simQuValue, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(simQuDescription, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(loggingCheckbox,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(alert,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		// FIXME
//		setHeader("<br><div class=\"row\"><div class=\"col col-sm-4\"><span>Select a quantity: </span></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-8\"><div id= " + dropdown.getId() + "></div></div></div>",null);
		PageSnippet headerSnippet = new PageSnippet(page, "simQuHeaderSnippet",true);
		headerSnippet.linebreak(null);
		StaticTable tab = new StaticTable(1, 2, new int[]{4,8});
		tab.setContent(0, 0, "Select a quantity").setContent(0, 1, dropdown);
		headerSnippet.append(tab, null);
		setHeader(headerSnippet, null);
		
		PageSnippet bodySnippet = new PageSnippet(page, "simQuBodySnippet",true);
		StaticTable tab2 = new StaticTable(4, 2, new int[]{4,8});
		tab2.setContent(0,0,"SimulatedQuantity").setContent(0, 1, simQuId).setContent(1, 0, "Value: ").setContent(1, 1, simQuValue)
			.setContent(2, 0, "Description").setContent(2, 1, simQuDescription).setContent(3, 0, "Logging configuration").setContent(3, 1, loggingCheckbox);
		tab2.removeBorder();
		bodySnippet.append(tab2, null);
		StaticTable tab3 = new StaticTable(4, 3, new int[]{4,3,5});
		tab3.setContent(0, 1, loggingIntervalLabel).setContent(0, 2, loggingInterval).setContent(1, 1, loggingTypeLabel).setContent(1, 2, loggingType)
		    .setContent(2, 1, loggingSinceLabel).setContent(2, 2, loggingSince).setContent(3, 1, loggingNumbersLabel).setContent(3, 2, loggingNumbers);
		tab3.removeBorder();
		bodySnippet.append(tab3, null);
		setBody(bodySnippet, null);
		
/*		setBody("<div class=\"row\"><div class=\"col col-sm-4\"><span>Simulated quantity: </span></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-8\"><div id= " + simQuId.getId() + "></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-4\"><span>Value: </span></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-8\"><div id= " + simQuValue.getId() + "></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-4\"><span>Description: </span></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-8\"><div id= "+simQuDescription.getId()+"></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-4\"><span>Logging configuration: </span></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-8\"><div id= "+loggingCheckbox.getId()+"></div></div></div>"  
				+ "<div class=\"row\"><div class=\"col col-sm-4\"></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-3\"><div id= "+loggingIntervalLabel.getId()+"></div></div><div class=\"col col-sm-5\"><div id= "+loggingInterval.getId()+"></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-4\"></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-3\"><div id= "+loggingTypeLabel.getId()+"></div></div><div class=\"col col-sm-5\"><div id= "+loggingType.getId()+"></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-4\"></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-3\"><div id= "+loggingSinceLabel.getId()+"></div></div><div class=\"col col-sm-5\"><div id= "+loggingSince.getId()+"></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-4\"></div><!--<div class=\"col col-sm-1\"/>--><div class=\"col col-sm-3\"><div id= "+loggingNumbersLabel.getId()+"></div></div><div class=\"col col-sm-5\"><div id= "+loggingNumbers.getId()+"></div></div></div>",null); */
	/*	setFooterHTML("<iframe src=\"/ogema/simulationsgui/individualPlots/index.html#/dashboard/script/scripted_async.js?panelId=1&fullscreen&from=now-5m&to=now\" "
				+ "height=\"100%\" width=\"100%\" name=\"plotiframe\">Plot could not be rendered</iframe>"); */
		this.triggerAction(loggingInterval, TriggeringAction.GET_REQUEST, TriggeredAction.HIDE_WIDGET);
		this.triggerAction(loggingType, TriggeringAction.GET_REQUEST, TriggeredAction.HIDE_WIDGET);
		this.triggerAction(loggingIntervalLabel, TriggeringAction.GET_REQUEST, TriggeredAction.HIDE_WIDGET);
		this.triggerAction(loggingTypeLabel, TriggeringAction.GET_REQUEST, TriggeredAction.HIDE_WIDGET);
/*		dropdown.triggerAction(loggingInterval, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);  // already triggered via GET of loggingCheckbox
		dropdown.triggerAction(loggingType, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(loggingIntervalLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(loggingTypeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); */  
		loggingCheckbox.triggerAction(loggingType, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingInterval, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingTypeLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingIntervalLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingSinceLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingSince, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingNumbersLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingNumbers, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		
		loggingCheckbox.triggerAction(loggingType, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingInterval, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingTypeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingIntervalLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingSinceLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingSince, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingType.triggerAction(loggingType, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingInterval.triggerAction(loggingInterval, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingNumbersLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		loggingCheckbox.triggerAction(loggingNumbers, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	/********* Public *******/
	
	public WidgetGroup getSubWidgets() {
		return subWidgets;
	}
	
	/************ Options ***************/
	
	public class SimQuModalOptions extends PopupData {

		private String simQuId = "";
		private boolean logConfigsVisible = false;
		private String simQuValue = "";
		private String description = "";
		private String simQuPath = "";
		private SimulationProvider<? extends Resource> provider = null;
		private String deviceId = "";

		public SimQuModalOptions(SimQuModal popup) {
			super(popup);
		}
		
		public String getSimQuId() {
			return simQuId;
		}

		public void setSimQuId(String simQuId) {
			this.simQuId = simQuId;
		}

		public boolean isLogConfigsVisible() {
			return logConfigsVisible;
		}

		public void setLogConfigsVisible(boolean logConfigsVisible) {
			this.logConfigsVisible = logConfigsVisible;
		}
		
		public String getSimQuValue() {
			return simQuValue;
		}

		public void setSimQuValue(String simQuValue) {
			this.simQuValue = simQuValue;
		}
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}		

		public String getSimQuPath() {
			return simQuPath;
		}

		public void setSimQuPath(String simQuPath) {
			this.simQuPath = simQuPath;
		}
		
		public SimulationProvider<? extends Resource> getProvider() {
			return provider;
		}

		public void setProvider(SimulationProvider<? extends Resource> provider) {
			this.provider = provider;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		
	}
	
	public void setDeviceId(String deviceId, OgemaHttpRequest req) {
		getSessionData(req).setDeviceId(deviceId);
	}
	
	public void setProvider(SimulationProvider<? extends Resource> provider, OgemaHttpRequest req) {
		getSessionData(req).setProvider(provider);
	}
	
	/************ Inherited methods ***************/
	
	@Override
	public PopupData createNewSession() {
		return new SimQuModalOptions(this);
	}
	
/*	
	@Override
	public void onGET(OgemaHttpRequest req) {
		SimQuModalOptions options = getSessionData(req);
//		String activeDevice = getActiveDevice(req);
		String activeDevice = options.getDeviceId();
		if (activeDevice == null) return;
		StringBuilder titleHTML = new StringBuilder("Active device: ");
		titleHTML.append(activeDevice);
		setTitle(titleHTML.toString(),req);
//		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//		sessionData.setSimQuDescription("");
//		sessionData.setSimQuId("");
//		sessionData.setSimQuValue("");
//		sessionData.setSimQuPath("");	
		options.setDescription("");
		options.setSimQuId("");
		options.setSimQuValue("");
		options.setSimQuPath("");
	} */
	
	/************ Internal classes & methods ***************/
	
	private class SimQuDropdown extends Dropdown {

		public SimQuDropdown(WidgetPageBase<?> page, String id) {
			super(page, id);
		}
		
		private static final long serialVersionUID = 1L;
		@Override
		public void onGET(OgemaHttpRequest req) {
			setOptions(getSimQuOptions(req),req);
		}
		@Override
		public void onPrePOST(String json, OgemaHttpRequest req) {
			JSONObject obj = new JSONObject(json);
			if (obj.has("data")) { 	
				JSONArray arr = obj.getJSONArray("data");
				String crId = arr.getString(0);
				SimQuModalOptions options = getSessionData(req);
				if (crId.isEmpty()) {					
					options.setSimQuId("");
					options.setSimQuValue("");
					options.setDescription("");
					options.setSimQuPath("");
					return;
				}
				
		/*		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
				if (crId.isEmpty()) {					
					sessionData.setSimQuId("");
					sessionData.setSimQuValue("");
					sessionData.setSimQuDescription("");
					sessionData.setSimQuPath("");
					return;
				} */
				SimulatedQuantity qu = getSimQu(crId, req);
				if (qu == null) {
					alert.setWidgetVisibility(true,req);
					alert.setText("Could not find value " + crId,req);
//					alert.setColor(BootstrapColor.RED);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER,req);
					return;
				}
//				sessionData.setSimQuId(qu.getId());
				String value = Utils.getInstance().getValue(qu.value());
	/*			sessionData.setSimQuValue(value);
				sessionData.setSimQuDescription(qu.getDescription());
				sessionData.setSimQuPath(qu.value().getLocation()); */				
				options.setSimQuId(qu.getId());
				options.setSimQuValue(value);
				options.setDescription(qu.getDescription());
				options.setSimQuPath(qu.value().getLocation());
			}
			return;
		}
		
	}
	
	private SimulatedQuantity getSimQu(String valId,OgemaHttpRequest req) {
		List<SimulatedQuantity> quantities = getCurrentSimQus(req);
		if (quantities == null) return null;
		Iterator<SimulatedQuantity> it = quantities.iterator();
		SimulatedQuantity cfg = null;
		while(it.hasNext()) {
			cfg = it.next();
			if (cfg.getId().equals(valId)) {
				break;
			}
		}
		return cfg;
	}

	private List<SimulatedQuantity> getCurrentSimQus(OgemaHttpRequest req) {
		SimQuModalOptions options =getSessionData(req);
//		String activeDevice = getActiveDevice(req);
		String activeDevice = options.getDeviceId();
		if (activeDevice == null) return null;
//		SimulationProvider<? extends Resource> provider = getSimulationProvider(req);
		SimulationProvider<? extends Resource> provider = options.getProvider();
		if (provider == null) {
			List<SimulatedQuantity> empty = Collections.emptyList();
			return empty;
		}
		return provider.getSimulatedQuantities(activeDevice);
	}
	
	private Set<DropdownOption> getSimQuOptions(OgemaHttpRequest req) {
		List<SimulatedQuantity> listConfigs = getCurrentSimQus(req);
		if (listConfigs == null) return new LinkedHashSet<DropdownOption>();
		Set<DropdownOption> set = new LinkedHashSet<DropdownOption>();
		Iterator<SimulatedQuantity> it = listConfigs.iterator();
		DropdownOption empty = new DropdownOption("","",true);
		set.add(empty);
		while(it.hasNext())  {
			SimulatedQuantity config = it.next();
			String id = config.getId();
			if (id == null) continue;
			DropdownOption option = new DropdownOption(id, id, false);
			set.add(option);
		}
		return set;
	}
	
/*	private String getActiveDevice(OgemaHttpRequest req) {
		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
		return sessionData.getDeviceId();
	}
	
	private SimulationProvider<? extends Resource> getSimulationProvider(OgemaHttpRequest req) {
		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
		return sessionData.getProvider();
	} */
	
	public SimQuModalOptions getSessionData(OgemaHttpRequest req) {
		return (SimQuModalOptions) getData(req);

	}
	
}

