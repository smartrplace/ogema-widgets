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

package org.ogema.messaging.basic.services.config.template;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.ResourceList;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.messaging.basic.services.config.model.ReceiverConfiguration;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;

public class ReceiverTemplate extends RowTemplate<ReceiverConfiguration> {

	protected final ResourceList<ReceiverConfiguration> receiverConfigs;
	protected final DynamicTable<ReceiverConfiguration> receiverTable;
	protected final Alert alert;
	protected final WidgetPage<?> page;
	protected final ApplicationManager am;
	protected final OgemaLogger logger;
	protected final ResourceAccess ra;
	protected final ResourceManagement resMan;
	private final String emailRegex = "[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private final String smsRegex = "[0-9]+[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	
	public ReceiverTemplate(ResourceList<ReceiverConfiguration> list, ApplicationManager am , DynamicTable<ReceiverConfiguration> table, Alert alert, WidgetPage<?> page) {
		this.receiverConfigs = list;
		this.receiverTable = table;
		this.alert = alert;
		this.page = page;
		this.am = am;
		this.logger = am.getLogger();
		this.ra = am.getResourceAccess();
		this.resMan = am.getResourceManagement();
	}
	
	@Override
	public Map<String, Object> getHeader() {
		Map<String, Object> receiverHeader = new LinkedHashMap<String, Object>();
		receiverHeader.put("receiverNameColumn", "Name :");
		receiverHeader.put("receiverEMailColumn", "E-Mail-Address :");
		receiverHeader.put("receiverSmsColumn", "Sms-Address :");
		receiverHeader.put("receiverXmppColumn", "Xmpp-Address :");
		receiverHeader.put("editReceiverPopupColumn", "");
		receiverHeader.put("editReceiverButtonColumn", "");
		receiverHeader.put("deleteReceiverButtonColumn", "");
		return receiverHeader;
	}
	
	@Override
	public String getLineId(ReceiverConfiguration object) {
		return ResourceUtils.getValidResourceName(object.userName().getValue());
	}
		
	@SuppressWarnings("serial")
	@Override
	public Row addRow(final ReceiverConfiguration config, OgemaHttpRequest req) {
		Row row = new Row();
		
//NEW
		
		final String id = getLineId(config);

		final Label newReceiverNameLabel = new Label(page, "newReceiverNameLabel_" + id, true);
		newReceiverNameLabel.setDefaultText(config.userName().getValue());
		row.addCell("receiverNameColumn", newReceiverNameLabel);
				
		final Label newEMailLabel = new Label(page, "newEMailLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(config.email().exists()) {
					setText(config.email().getValue(),req);
				} else {
					setText("",req);
				}
			}
		};
		row.addCell("receiverEMailColumn", newEMailLabel);
				
		final Label newSmsLabel = new Label(page, "newSmsLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(config.sms().exists()) {
					setText(config.sms().getValue(),req);
				} else {
					setText("",req);
				}
			}
		};
		row.addCell("receiverSmsColumn", newSmsLabel);
				
		final Label newXmppLabel = new Label(page, "newXmppLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(config.xmpp().exists()) {
					setText(config.xmpp().getValue(),req);
				} else {
					setText("",req);
				}
			}
		};
		newXmppLabel.setDefaultText(config.xmpp().getValue());
		row.addCell("receiverXmppColumn", newXmppLabel);
			
		
//EDIT		
		final Label editNameLabel = new Label(page, "editNameLabel_" + id, true);
		editNameLabel.setDefaultText("Name : ");
		final Label editEMailLabel = new Label(page, "editEMailLabel_" + id, true);
		editEMailLabel.setDefaultText("new E-Mail-Address : ");
		final Label editSmsLabel = new Label(page, "editSmsLabel_" + id, true);
		editSmsLabel.setDefaultText("new Sms-Number : ");
		final Label editXmppLabel = new Label(page, "editXmppLabel_" + id, true);
		editXmppLabel.setDefaultText("new Xmpp-Address : ");
		
		final TextField editEMailTextField = new TextField(page, "editEMailTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(config.email().exists()) {
					this.setValue(config.email().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
		};
		
		final TextField editSmsTextField = new TextField(page, "editSmsTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(config.sms().exists()) {
					this.setValue(config.sms().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
		};
		
		final TextField editXmppTextField = new TextField(page, "editXmppTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(config.xmpp().exists()) {
					this.setValue(config.xmpp().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
		};	
		
		final Popup editReceiverPopup = new Popup(page, "ediReceiverPopup_" + id , true);
		editReceiverPopup.setTitle("Edit Receiver ", null);
		row.addCell("editReceiverPopupColumn", editReceiverPopup);
		
		final StaticTable editReceiverTable = new StaticTable(4, 2);
		editReceiverTable.setContent(0, 0, editNameLabel);
		editReceiverTable.setContent(1, 0, editEMailLabel);
		editReceiverTable.setContent(2, 0, editSmsLabel);
		editReceiverTable.setContent(3, 0, editXmppLabel);
		editReceiverTable.setContent(0, 1, config.userName().getValue());
		editReceiverTable.setContent(1, 1, editEMailTextField);
		editReceiverTable.setContent(2, 1, editSmsTextField);
		editReceiverTable.setContent(3, 1, editXmppTextField);
				
		final Button editReceiverButton = new Button(page, "editReceiverButton" + id);
		editReceiverButton.triggerAction(editEMailTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(editSmsTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(editXmppTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(editReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editReceiverButton.setDefaultText("Edit");
		row.addCell("editReceiverButtonColumn", editReceiverButton);
				
		final ButtonConfirm confirmReceiverChangesButton = new ButtonConfirm(page, "confirmReceiverChangesButton_" + id){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {			
				
				if(changesAreValid(editEMailTextField.getValue(req), editSmsTextField.getValue(req), editXmppTextField.getValue(req), req)){
					if(!editEMailTextField.getValue(req).equals("")) {
						if(!config.email().exists()) {
							config.email().create();
						}
						config.email().setValue(editEMailTextField.getValue(req));
						config.email().activate(true);
					} else {
						config.email().delete();
					}
					if(!editSmsTextField.getValue(req).equals("")) {
						if(!config.sms().exists()) {
							config.sms().create();
						}
						config.sms().setValue(editSmsTextField.getValue(req));
						config.sms().activate(true);
					} else {
						config.sms().delete();
					}
					if(!editXmppTextField.getValue(req).equals("")) {
						if(!config.xmpp().exists()) {
							config.xmpp().create();
						}
						config.xmpp().setValue(editXmppTextField.getValue(req));
						config.xmpp().activate(true);
					} else {
						config.xmpp().delete();
					}
					alert.showAlert("Changes on Receiver '" + id + "' confirmed", true, req);
				}
			}
			
			public boolean changesAreValid(String emailValue, String smsValue, String xmppValue, OgemaHttpRequest req) {
				boolean emailAccepted = false;
				boolean smsAccepted = false;
				boolean xmppAccepted = false;
				
				if((emailValue.trim().length() == 0) || emailValue.matches(emailRegex)) emailAccepted = true;
				if((smsValue.trim().length() == 0) || smsValue.matches(smsRegex)) smsAccepted = true;
				if((xmppValue.trim().length() == 0) || xmppValue.matches(emailRegex)) xmppAccepted = true;
				
				if(emailAccepted && smsAccepted && xmppAccepted){
					if(emailValue.trim().length() != 0 || smsValue.trim().length() != 0 || xmppValue.trim().length() != 0) {
						return true;
					}
				}
				
				if(!emailAccepted) {
					alert.showAlert("Invalid E-Mail-Address", false, req);
					return false;
				}
				if(!smsAccepted) {
					alert.showAlert("Invalid Sms-E-Mail-Address. The addess must have the format <SMS-with-country-code-without beginng + or 0 signs>.<email-address of SMS-gateway>", false, req);
					return false;
				}
				if(!xmppAccepted) {
					alert.showAlert("Invalid Xmpp-Address", false, req);
					return false;
				}
				alert.showAlert("Please enter atleast one Address", false, req);
				
	
				return false;
			}
			
		};
		confirmReceiverChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		confirmReceiverChangesButton.setDefaultText("Save Changes");
		confirmReceiverChangesButton.setDefaultConfirmPopupTitle("Edit " + id);
		confirmReceiverChangesButton.setDefaultConfirmMsg("Accept changes ?");
		confirmReceiverChangesButton.triggerAction(newEMailLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newSmsLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newXmppLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(editReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
				
		final PageSnippet editReceiverSnippet = new PageSnippet(page, "editReceiverSnippet" + id, true);
		editReceiverSnippet.append(editReceiverTable, null);
		editReceiverSnippet.append(confirmReceiverChangesButton, null);
				
		editReceiverPopup.setBody(editReceiverSnippet, null);
			
		
//DELETE		
		final ButtonConfirm deleteReceiverButton = new ButtonConfirm(page, "deleteReceiverButton_" + id){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				receiverTable.removeRow(id, req);
				alert.showAlert("Receiver " + id + " successfully deleted", true, req);
			}
		};
		deleteReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteReceiverButton.setDefaultText("Delete");
		deleteReceiverButton.setDefaultConfirmPopupTitle("Delete Receiver : " + id);
		deleteReceiverButton.setDefaultConfirmMsg("Are you sure deleting " + id + " from your list ?");
		deleteReceiverButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteReceiverButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteReceiverButtonColumn", deleteReceiverButton);
				
		return row;
	}
	
//	private ReceiverConfiguration getReceiverConfiguration(String newName) {
//		for (ReceiverConfiguration config: receiverConfigs.getAllElements()) {
//			StringResource actUser = config.userName();	
//			//System.out.println("------> Act Receiver : " + actUser.getValue());
//			if (!actUser.isActive())  {
//				continue;
//			}
//			if (actUser.getValue().equals(newName)) {
//				
//				return config;
//			}
//		}
//		return null;
//	}
		
}