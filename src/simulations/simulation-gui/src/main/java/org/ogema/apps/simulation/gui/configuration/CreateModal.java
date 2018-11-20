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
import java.util.LinkedList;
import java.util.List;

import org.ogema.apps.simulation.gui.Utils;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.SimulationProvider;

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
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.popup.PopupData;
	
public class CreateModal extends Popup {

	private static final long serialVersionUID = 1L;
	private final Label text;
	private final TextField nameField;
	private final Alert alert;
	private final Button confirmBtn;
	private final WidgetPageBase<?> page;
	private final Label headerLabel;
	private final PageSnippet bodySnippet;
	private final WidgetGroup subWidgets;

	/********* Constructor ************/

	public CreateModal(final WidgetPageBase<?> page,final String id,final Alert alert, final ResourceAccess ra) {
		super(page, id,true);
		this.page = page;
		this.alert = alert;
		this.text = new Label(page,"createTextWidget",true);
		text.setText("Enter a name: ",null);
		this.nameField = new TextField(page, "createNameField") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				SimulationProvider<? extends Resource> provider =  CreateModal.this.getProvider(req); // Utils.getInstance().getCreationProvider(page, req);
				if (provider == null ) {
					setValue("no provider selected",req);
				} else {
					setValue(Utils.getInstance().getNextResourceName(provider, ra),req);
				}
			}
		};
		nameField.setDefaultPlaceholder("Enter name");
		
		this.confirmBtn = new Button(page,"createConfirmBtn",true) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				String deviceId = nameField.getValue(req);
				SimulationProvider<? extends Resource> provider = CreateModal.this.getProvider(req);  //Utils.getInstance().getCreationProvider(page, req);
				if (provider == null) {
					alert.setText("Something went wrong... could not create simulated object.",req);
					//alert.setColor(BootstrapColor.RED);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER,req);
					alert.setWidgetVisibility(true,req);
				}
				//Name does not need to be unique, but existing resource must have fitting type
				String resourceName = ResourceUtils.getValidResourceName(deviceId);
				Resource exist = Utils.getInstance().getResource(deviceId); 
				if (exist != null && (!exist.getResourceType().equals(provider.getSimulatedType()))) {
					alert.showAlert("Resource " + resourceName + " already in use.", false, req);
					return;
				}
				Resource res = provider.createSimulatedObject(resourceName);
				if (res == null) {
					alert.setText("Could not create simulated object; possibly the name is in use already.",req);
					//alert.setColor(BootstrapColor.RED);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER,req);
					
				} else {
					alert.setText("New simulated object " + resourceName + " created.",req);
//					alert.setColor(BootstrapColor.GREEN);
					alert.setStyle(AlertData.BOOTSTRAP_SUCCESS,req);
				}
				alert.setWidgetVisibility(true,req);
			}
		};
//		confirmBtn.setCss("btn btn-primary");
		confirmBtn.addDefaultStyle(ButtonData.BOOTSTRAP_BLUE);
		confirmBtn.setDefaultText("Create");
		
		this.headerLabel = new Label(page, "creationHeaderLabel");
		setHeader(headerLabel, null);
		
		this.bodySnippet = new PageSnippet(page, "createBodySnippet", true);
		StaticTable tab = new StaticTable(1, 3, new int[]{3,7,2});
		tab.setContent(0, 0, text).setContent(0, 1, nameField).setContent(0, 2, confirmBtn);
		bodySnippet.append(tab, null);
		setBody(bodySnippet, null);
		
		List<OgemaWidgetBase<?>> sw = new LinkedList<OgemaWidgetBase<?>>();
		sw.add(nameField);sw.add(headerLabel);
//		sw.add(this); // -> would hide modal!
		subWidgets = page.registerWidgetGroup("createModalWidgets",(Collection) sw);
		
/*		this.triggerAction(nameField.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(headerLabel.getId(), TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST); */

		
		confirmBtn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmBtn.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
	}
	
	/********* Public *******/
	
	public WidgetGroup getSubWidgets() {
		return subWidgets;
	}
	
	public void setHeaderLabel(String text, OgemaHttpRequest req) {
		headerLabel.setText(text, req);
	}
	
	/************* Options ************/
	
	class CreateModalOptions extends PopupData {

		private SimulationProvider<? extends Resource> provider;
		
		public CreateModalOptions(CreateModal popup) {
			super(popup);
		}
		
		public SimulationProvider<? extends Resource> getProvider() {
			return provider;
		}

		public void setProvider(SimulationProvider<? extends Resource> provider) {
			this.provider = provider;
		}
	}

	public void setProvider(SimulationProvider<? extends Resource> provider, OgemaHttpRequest req) {
		getData(req).setProvider(provider);
	}
	
	public SimulationProvider<? extends Resource> getProvider(OgemaHttpRequest req) {
		return getData(req).getProvider();
	}
	
	/************ Inherited methods **************/
	
	@Override
	public PopupData createNewSession() {
		return new CreateModalOptions(this);
	}
	
	@Override
	public CreateModalOptions getData(OgemaHttpRequest req) {
		return (CreateModalOptions) super.getData(req);
	}
	
/*	@Override
	public void onGET(OgemaHttpRequest req) {
		SimulationProvider<? extends Resource> provider =  getProvider(req); // Utils.getInstance().getCreationProvider(page, req);
		//if (provider == null) return super.retrieveGETData(req);
		String type;
		if (provider == null) {
			type = "not selected";
		}
		else {
			type = provider.getSimulatedType().getSimpleName();
		}
		setTitle("Create new object",req);
		headerLabel.setText("Type: "  + type, req);
//		setHeader("Type: "  + type,req);
//		setBody("<div class=\"row\"><div class=\"col col-sm-3\"><div id=" + text.getId() + "></div></div>"
//				+ "<div class=\"col col-sm-7\"><div id=" + nameField.getId() + "></div></div>"
//				+ "<div class=\"col col-sm-2\"><div id=" + confirmBtn.getId() + "></div></div>",req);
	} */
	
	public Button getConfirmButton() {
		return confirmBtn;
	}

}

