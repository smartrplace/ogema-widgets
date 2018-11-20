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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.apps.simulation.gui.Utils;
import org.ogema.core.model.Resource;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;
import org.ogema.tools.simulation.service.api.model.SimulationResourceConfiguration;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.popup.PopupData;
	
public class ConfigModal extends Popup {

	private static final long serialVersionUID = 1L;
	//private String activeDevice = null; // session dependent
	// private final List<SimulationProvider<? extends Resource>> providers; // session dependent
	private final Alert alert;
	private final Dropdown dropdown;
	private final Label configId;
	private final TextField configValue;
	private final Label configDescription;
	private final Dropdown valueDropdown;
	private final Map<String,String> emptyL = Collections.emptyMap();
	private final WidgetGroup subWidgets;
	
	/****** Constructor *****/
	
	public ConfigModal(WidgetPageBase<?> page, String id, Alert alert) {
		super(page, id,true);
		this.alert = alert;
		this.dropdown = new ConfigDropdown(page, "modalDropDown");
		this.configId = new Label(page,"modalConfigId","configurationId");

		this.configValue = new TextField(page, "modalConfigValue", "current value") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				setValue(sessionData.getConfigValue(),req);
//				setWidgetVisibility(!sessionData.showValueDropdown,req);
			}
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				String config = sessionData.getConfigId();
				String config = configId.getText(req);
				SimulationConfiguration cfg = getConfiguration(config, req);
				JSONObject obj = new JSONObject(data);
				String newValue = obj.getString("data");
				if(cfg instanceof SimulationResourceConfiguration) {
					Utils.getInstance().setValue(((SimulationResourceConfiguration)cfg).value(),newValue);
				} else if (cfg instanceof SimulationComplexConfiguration) {
					((SimulationComplexConfiguration) cfg).setValue(newValue);
				}
				try {
					Thread.sleep(100); // make sure a potential resource listener has time to readjust the resource value, if necessary,
										//  before the following GET is triggered
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				dropdown.onPOST("{data:["+ sessionData.getConfigId() +"]}", req); 
				dropdown.onPrePOST("{data:["+ configId.getText(req) +"]}", req); 
			}
		};
		this.configDescription = new Label(page, "modalConfigDescription", "Configuration description") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				setText(sessionData.getConfigDescription(),req);
			}
		};
		this.valueDropdown = new Dropdown(page, "modalConfigValueDropdown") {
			
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				setWidgetVisibility(sessionData.showValueDropdown,req);	
//				setOptions(sessionData.getValueOptions(),req);
			}
			@Override
			public void onPOSTComplete(String json, OgemaHttpRequest req) {
				JSONObject obj = new JSONObject(json);
				if (obj.has("data")) {
					JSONArray arr = obj.getJSONArray("data");		
					String selection = arr.getString(0);
//					SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//					String config = sessionData.getConfigId();
					String config = configId.getText(req);
					SimulationConfiguration cfg = getConfiguration(config, req);
					if(cfg instanceof SimulationResourceConfiguration) {
						Utils.getInstance().setValue(((SimulationResourceConfiguration)cfg).value(),selection);
					} 
					else if (cfg instanceof SimulationComplexConfiguration) {
						((SimulationComplexConfiguration) cfg).setValue(selection);
					}
					
					try {
						Thread.sleep(100); // make sure a potential resource listener has time to readjust the resource value, if necessary,
											//  before the following GET is triggered // -> probably no longer necessary
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					dropdown.onPrePOST("{data:["+ configId.getText(req) +"]}", req); // update session data!
				}
			}
			
		};
		valueDropdown.setDefaultVisibility(false);
		
		List<OgemaWidgetBase<?>> sw = new LinkedList<OgemaWidgetBase<?>>();
		sw.add(dropdown);sw.add(configId);sw.add(configValue);sw.add(configDescription);sw.add(valueDropdown);
//		sw.add(this); 
		subWidgets = page.registerWidgetGroup("configModalWidgets", (Collection) sw);
//		this.triggerGroupAction(subWidgets, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		
/*		this.triggerAction(dropdown.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(configId.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(configDescription.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(configValue.getId(),TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(valueDropdown.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST); */
		this.triggerAction(alert,TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(configId, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(configValue, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(configDescription, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(valueDropdown, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dropdown.triggerAction(alert,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		configValue.triggerAction(configValue, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		valueDropdown.triggerAction(valueDropdown, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		// TODO valueDropdown should trigger GET for statusLabels and activationButtons; Problem: how to trigger only button  in current row?
		// introduce Widget group?
//		valueDropdown.triggerAction(.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		PageSnippet headerSnippet = new PageSnippet(page, "configHeaderSnippet",true);
		headerSnippet.linebreak(null);
		StaticTable tab = new StaticTable(1, 3, new int[]{3,1,8});
		tab.setContent(0, 0, "Select a setting: ").setContent(0, 2, dropdown);
		headerSnippet.append(tab, null);
		setHeader(headerSnippet, null);
		
		PageSnippet bodySnippet = new PageSnippet(page, "configBodySnippet",true);
		bodySnippet.linebreak(null);
		HtmlItem doubleWidget = new HtmlItem("div");
		doubleWidget.addSubItem(configValue).addSubItem(valueDropdown);
		StaticTable tab2 = new StaticTable(3, 3, new int[]{3,1,8});
		tab2.removeBorder();
		tab2.setContent(0, 0, "Configuration: ").setContent(0, 2, configId)
			.setContent(1, 0, "Value: ")		.setContent(1, 2, doubleWidget)
			.setContent(2, 0, "Explanation: ")	.setContent(2, 2, configDescription);
		bodySnippet.append(tab2, null);
		setBody(bodySnippet, null);
		
//		setHeader("<br><div class=\"row\"><div class=\"col col-sm-3\"><span>Select a setting: </span></div><div class=\"col col-sm-1\"/><div class=\"col col-sm-8\"><div id= " + dropdown.getId() + "></div></div></div>",null);
/*		setBody("<div class=\"row\"><div class=\"col col-sm-3\"><span>Configuration: </span></div><div class=\"col col-sm-1\"/><div class=\"col col-sm-8\"><div id= " + configId.getId() + "></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-3\"><span>Value: </span></div><div class=\"col col-sm-1\"/><div class=\"col col-sm-8\"><div id= " + configValue.getId() + "></div><div id= " + valueDropdown.getId() + "></div></div></div>"
				+ "<br><div class=\"row\"><div class=\"col col-sm-3\"><span>Explanation: </span></div><div class=\"col col-sm-1\"/><div class=\"col col-sm-8\"><div id= "+configDescription.getId()+"></div></div></div>",null); */
		
	}
	
	/********* Public *******/
	
	public WidgetGroup getSubWidgets() {
		return subWidgets;
	}
	
	public void setConfigDescription(String str, OgemaHttpRequest req) {
		configDescription.setText(str, req);
	}
	
	public void setConfigId(String str, OgemaHttpRequest req) {
		configId.setText(str, req);
	}

	public void setConfigValue(String str, OgemaHttpRequest req) {
		configValue.setValue(str, req);
	}
	
	/********* Options ******/
	
	class ConfigModalOptions extends PopupData {

		private SimulationProvider<? extends Resource> provider;
		private String deviceId;
		
		public ConfigModalOptions(ConfigModal popup) {
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
		return new ConfigModalOptions(this);
	}
	
	@Override
	public ConfigModalOptions getData(OgemaHttpRequest req) {
		return (ConfigModalOptions) super.getData(req);
	}
	
/*	@Override
	public void onGET(OgemaHttpRequest req) {
//		String activeDevice = getActiveDevice(req);
		String activeDevice = getDeviceId(req);
		if (activeDevice == null) return;
		StringBuilder titleHTML = new StringBuilder("Active device: ");
		titleHTML.append(activeDevice);
		setTitle(titleHTML.toString(),req);
//		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//		sessionData.setConfigDescription("");
		configDescription.setText("", req);
		configId.setText("", req);
//		sessionData.setConfigId("");
//		sessionData.setConfigValue("");
		configValue.setValue("", req);
	} */
	
	/********** Classes **********/
	
	private class ConfigDropdown extends Dropdown {

		public ConfigDropdown(WidgetPageBase<?> page, String id) {
			super(page, id);
		}
		
		private static final long serialVersionUID = 1L;
		@Override
		public void onGET(OgemaHttpRequest req) {
			setOptions(getConfigurationOptions(req),req);
		}
		@Override
		public void onPrePOST(String json, OgemaHttpRequest req) {
			JSONObject obj = new JSONObject(json);
			if (obj.has("data")) {
				JSONArray arr = obj.getJSONArray("data");		
				String crId = arr.getString(0);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
				if (crId.isEmpty()) {					
//					sessionData.setConfigId("");
					configId.setText("", req);
//					sessionData.setConfigValue("");
					configValue.setValue("", req);
//					sessionData.setConfigDescription("");
					configDescription.setText("", req);
//					sessionData.setValueOptions(emptyL, "");
					valueDropdown.setOptions(getValueOptions(emptyL, ""), req);
//					sessionData.showValueDropdown = false;
					configValue.setWidgetVisibility(true, req);
					valueDropdown.setWidgetVisibility(false, req);
					return;
				}
				SimulationConfiguration cfg = getConfiguration(crId, req);
				if (cfg == null) {
					alert.setWidgetVisibility(true,req);
					alert.setText("Could not find configuration " + crId,req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					return;
				}
				configId.setText(cfg.getId(), req);
//				sessionData.setConfigId(cfg.getId());
				boolean showDropdown = (cfg.getOptions() != null);		
				configValue.setWidgetVisibility(!showDropdown, req);
				valueDropdown.setWidgetVisibility(showDropdown, req);
//				sessionData.showValueDropdown = (cfg.getOptions() != null);
				String newValue;
				if(cfg instanceof SimulationResourceConfiguration) {
					newValue = Utils.getInstance().getValue(((SimulationResourceConfiguration)cfg).value());
				} else {
					newValue = ((SimulationComplexConfiguration)cfg).getValue();
				}
				
				if (!showDropdown) {
//					sessionData.setConfigValue(value);
					configValue.setValue(newValue, req);
//					sessionData.setValueOptions(emptyL, "");
					valueDropdown.setOptions(getValueOptions(emptyL, ""), req);
				} else {
//					sessionData.setValueOptions(cfg.getOptions(), Utils.getInstance().getValue(cfg.value()));
					valueDropdown.setOptions(getValueOptions(cfg.getOptions(), newValue), req);

//					sessionData.setConfigValue("");
					configValue.setValue("", req);
				}
//				sessionData.setConfigDescription(cfg.getDescription());
				configDescription.setText(cfg.getDescription(), req);

			}
		}
		
	}
	
	private SimulationConfiguration getConfiguration(String configId,OgemaHttpRequest req) {
		List<SimulationConfiguration> configs = getCurrentConfigurations(req);
		if (configs == null) return null;
		Iterator<SimulationConfiguration> it = configs.iterator();
		SimulationConfiguration cfg = null;
		while(it.hasNext()) {
			cfg = it.next();
			if (cfg.getId().equals(configId)) {
				break;
			}
		}
		return cfg;
	}

	private List<SimulationConfiguration> getCurrentConfigurations(OgemaHttpRequest req) {
//		String activeDevice = getActiveDevice(req);
		String activeDevice = getDeviceId(req);
		if (activeDevice == null) return null;
		SimulationProvider<? extends Resource> provider = getProvider(req);
		return provider.getConfigurations(activeDevice);
	}
	
	private Set<DropdownOption> getConfigurationOptions(OgemaHttpRequest req) {
		List<SimulationConfiguration> listConfigs = getCurrentConfigurations(req);
		if (listConfigs == null) return new LinkedHashSet<DropdownOption>();
		Set<DropdownOption> set = new LinkedHashSet<DropdownOption>();
		Iterator<SimulationConfiguration> it = listConfigs.iterator();
		DropdownOption empty = new DropdownOption("","",true);
		set.add(empty);
		while(it.hasNext())  {
			SimulationConfiguration config = it.next();
			String id = config.getId();
			if (id == null) continue;
			DropdownOption option = new DropdownOption(id, id, false);
			set.add(option);
		}
		return set;
	}
	
//	private String getActiveDevice(OgemaHttpRequest req) {
//		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//		return sessionData.getDeviceId();
//	}
	
//	private SimulationProvider<? extends Resource> getSimulationProvider(OgemaHttpRequest req) {
//		SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//		return sessionData.getProvider();
//	}
	
	public Set<DropdownOption> getValueOptions(Map<String,String> input, String current) {
		Set<DropdownOption> valueOptions = new LinkedHashSet<DropdownOption>();
		Iterator<Entry<String,String>> it = input.entrySet().iterator();
		DropdownOption empty = new DropdownOption("","",true);
		valueOptions.add(empty);
		while(it.hasNext()) {
			Entry<String,String> entry = it.next();
			boolean selected = entry.getKey().equals(current);
			DropdownOption opt = new DropdownOption(entry.getKey(), entry.getValue(), selected);
			if (selected) empty.select(false);
			valueOptions.add(opt);
		}
		return valueOptions;
	}
	
}

