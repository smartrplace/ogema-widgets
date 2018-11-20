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
package org.ogema.messaging.basic.services.config.template;

import java.net.URI;
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

	private final String EMAIL_REGEX = "[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private final String SMS_REGEX = "[0-9]+[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	
	protected final ResourceList<ReceiverConfiguration> receiverConfigs;
	protected final DynamicTable<ReceiverConfiguration> receiverTable;
	protected final Alert alert;
	protected final WidgetPage<?> page;
	protected final ApplicationManager am;
	protected final OgemaLogger logger;
	protected final ResourceAccess ra;
	protected final ResourceManagement resMan;

	public ReceiverTemplate(ResourceList<ReceiverConfiguration> list, ApplicationManager am,
			DynamicTable<ReceiverConfiguration> table, Alert alert, WidgetPage<?> page) {
		
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
		receiverHeader.put("receiverNameColumn", "Name:");
		receiverHeader.put("receiverEMailColumn", "Email-address:");
		receiverHeader.put("receiverSmsColumn", "Sms-address:");
		receiverHeader.put("receiverXmppColumn", "Xmpp-address:");
		receiverHeader.put("receiverRESTColumn", "REST-address:");
		receiverHeader.put("receiverRESTUserColumn", "REST-user:");
		receiverHeader.put("receiverRESTPwColumn", "REST-password:");
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

		// NEW
		final String id = getLineId(config);

		final Label newReceiverNameLabel = new Label(page, "newReceiverNameLabel_" + id, true);
		newReceiverNameLabel.setDefaultText(config.userName().getValue());
		row.addCell("receiverNameColumn", newReceiverNameLabel);

		final Label newEMailLabel = new Label(page, "newEMailLabel_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.email().exists()) {
					setText(config.email().getValue(), req);
				} else {
					setText("", req);
				}
			}
			
		};
		row.addCell("receiverEMailColumn", newEMailLabel);

		final Label newSmsLabel = new Label(page, "newSmsLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.sms().exists()) {
					setText(config.sms().getValue(), req);
				} else {
					setText("", req);
				}
			}
			
		};
		row.addCell("receiverSmsColumn", newSmsLabel);

		final Label newXmppLabel = new Label(page, "newXmppLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.xmpp().exists()) {
					setText(config.xmpp().getValue(), req);
				} else {
					setText("", req);
				}
			}
			
		};
		newXmppLabel.setDefaultText(config.xmpp().getValue());
		row.addCell("receiverXmppColumn", newXmppLabel);
		
		final Label newRESTLabel = new Label(page, "newRESTLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.remoteMessageRestUrl().exists()) {
					setText(config.remoteMessageRestUrl().getValue(), req);
				} else {
					setText("", req);
				}
			}
			
		};
		newRESTLabel.setDefaultText(config.remoteMessageRestUrl().getValue());
		row.addCell("receiverRESTColumn", newRESTLabel);
		
		final Label newRESTUserLabel = new Label(page, "newRESTUserLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.remoteMessageUser().exists()) {
					setText(config.remoteMessageUser().getValue(), req);
				} else {
					setText("", req);
				}
			}
			
		};
		newRESTUserLabel.setDefaultText(config.remoteMessageUser().getValue());
		row.addCell("receiverRESTUserColumn", newRESTUserLabel);
		
		final Label newRESTPwLabel = new Label(page, "newRESTPwLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.xmpp().exists()) {
					setText(config.remoteMessagePassword().getValue(), req);
				} else {
					setText("", req);
				}
			}
			
		};
		newRESTPwLabel.setDefaultText(config.remoteMessagePassword().getValue());
		row.addCell("receiverRESTPwColumn", newRESTPwLabel);

		// EDIT
		final Label editNameLabel = new Label(page, "editNameLabel_" + id, true);
		editNameLabel.setDefaultText("Name: ");
		final Label editEMailLabel = new Label(page, "editEMailLabel_" + id, true);
		editEMailLabel.setDefaultText("New email-address: ");
		final Label editSmsLabel = new Label(page, "editSmsLabel_" + id, true);
		editSmsLabel.setDefaultText("New sms-number: ");
		final Label editXmppLabel = new Label(page, "editXmppLabel_" + id, true);
		editXmppLabel.setDefaultText("New xmpp-address: ");
		final Label editRESTLabel = new Label(page, "editRESTLabel_" + id, true);
		editRESTLabel.setDefaultText("New rest-address: ");
		final Label editRESTUserLabel = new Label(page, "editRESTUserLabel_" + id, true);
		editRESTUserLabel.setDefaultText("New rest-user: ");
		final Label editRESTPwLabel = new Label(page, "editRESTPwLabel_" + id, true);
		editRESTPwLabel.setDefaultText("New rest-password: ");

		final TextField editEMailTextField = new TextField(page, "editEMailTextField_" + id, true) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.email().exists()) {
					this.setValue(config.email().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
			
		};

		final TextField editSmsTextField = new TextField(page, "editSmsTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.sms().exists()) {
					this.setValue(config.sms().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
			
		};

		final TextField editXmppTextField = new TextField(page, "editXmppTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.xmpp().exists()) {
					this.setValue(config.xmpp().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
			
		};
		
		final TextField editRESTTextField = new TextField(page, "editRESTTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.remoteMessageRestUrl().exists()) {
					this.setValue(config.remoteMessageRestUrl().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
			
		};
		
		final TextField editRESTUserTextField = new TextField(page, "editRESTUserTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.remoteMessageUser().exists()) {
					this.setValue(config.remoteMessageUser().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
			
		};
		
		final TextField editRESTPwTextField = new TextField(page, "editRESTPwTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (config.remoteMessagePassword().exists()) {
					this.setValue(config.remoteMessagePassword().getValue(), req);
				} else {
					this.setValue("", req);
				}
			}
			
		};

		final Popup editReceiverPopup = new Popup(page, "ediReceiverPopup_" + id, true);
		editReceiverPopup.setTitle("Edit receiver ", null);
		row.addCell("editReceiverPopupColumn", editReceiverPopup);

		final StaticTable editReceiverTable = new StaticTable(7, 2);
		editReceiverTable.setContent(0, 0, editNameLabel);
		editReceiverTable.setContent(1, 0, editEMailLabel);
		editReceiverTable.setContent(2, 0, editSmsLabel);
		editReceiverTable.setContent(3, 0, editXmppLabel);
		editReceiverTable.setContent(4, 0, editRESTLabel);
		editReceiverTable.setContent(5, 0, editRESTUserLabel);
		editReceiverTable.setContent(6, 0, editRESTPwLabel);
		editReceiverTable.setContent(0, 1, config.userName().getValue());
		editReceiverTable.setContent(1, 1, editEMailTextField);
		editReceiverTable.setContent(2, 1, editSmsTextField);
		editReceiverTable.setContent(3, 1, editXmppTextField);
		editReceiverTable.setContent(4, 1, editRESTTextField);
		editReceiverTable.setContent(5, 1, editRESTUserTextField);
		editReceiverTable.setContent(6, 1, editRESTPwTextField);

		final Button editReceiverButton = new Button(page, "editReceiverButton" + id);
		editReceiverButton.triggerAction(editEMailTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(editSmsTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(editXmppTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editReceiverButton.triggerAction(editReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editReceiverButton.setDefaultText("Edit");
		row.addCell("editReceiverButtonColumn", editReceiverButton);

		final ButtonConfirm confirmReceiverChangesButton = new ButtonConfirm(page,
				"confirmReceiverChangesButton_" + id) {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String email = editEMailTextField.getValue(req).trim();
				String sms = editSmsTextField.getValue(req).trim();
				String xmpp = editXmppTextField.getValue(req).trim();
				String rest = editRESTTextField.getValue(req).trim();
				String restUser = editRESTUserTextField.getValue(req).trim();
				String restPw = editRESTPwTextField.getValue(req);
				
				if (changesAreValid(email, sms, xmpp, rest, restUser, restPw, req)) {
					if (!email.isEmpty()) {
						config.email().create();
						config.email().setValue(email);
						config.email().activate(true);
					} else {
						config.email().delete();
					}
					if (!sms.isEmpty()) {
						config.sms().create();
						config.sms().setValue(sms);
						config.sms().activate(true);
					} else {
						config.sms().delete();
					}
					if (!xmpp.isEmpty()) {
						config.xmpp().create();
						config.xmpp().setValue(xmpp);
						config.xmpp().activate(true);
					} else {
						config.xmpp().delete();
					}
					if (!rest.isEmpty()) {
						config.remoteMessageRestUrl().create();
						config.remoteMessageRestUrl().setValue(rest.endsWith("/") ? rest : rest + "/");
						config.remoteMessageRestUrl().activate(true);
					} else {
						config.remoteMessageRestUrl().delete();
					}
					if (!restUser.isEmpty()) {
						config.remoteMessageUser().create();
						config.remoteMessageUser().setValue(restUser);
						config.remoteMessageUser().activate(true);
					} else {
						config.remoteMessageUser().delete();
					}
					if (!restPw.isEmpty()) {
						config.remoteMessagePassword().create();
						config.remoteMessagePassword().setValue(restPw);
						config.remoteMessagePassword().activate(true);
					} else {
						config.remoteMessagePassword().delete();
					}
					alert.showAlert("Changes on receiver '" + id + "' confirmed", true, req);
				}
			}

			public boolean changesAreValid(String email, String sms, String xmpp, 
					String rest, String restUser, String restPw, OgemaHttpRequest req) {
				boolean emailAccepted = false;
				boolean smsAccepted = false;
				boolean xmppAccepted = false;
				boolean restAccepted = false;

				if (email.isEmpty() || email.matches(EMAIL_REGEX))
					emailAccepted = true;
				if (sms.isEmpty() || sms.matches(SMS_REGEX))
					smsAccepted = true;
				if (xmpp.isEmpty() || xmpp.matches(EMAIL_REGEX))
					xmppAccepted = true;
				if ((rest.isEmpty() && restUser.isEmpty() && restPw.isEmpty()) || (!restUser.isEmpty() && !restPw.isEmpty())) {
					restAccepted = true;
					try {
						// Checking if the entered rest string is a valid URL
						new URI(rest);
					} catch (Exception e) {
						restAccepted = false;
					}
				}
					
				
				if (emailAccepted && smsAccepted && xmppAccepted && restAccepted && restAccepted) {
					return true;
				}

				if (!emailAccepted) {
					alert.showAlert("Invalid email-address", false, req);
				} else if (!smsAccepted) {
					alert.showAlert("Invalid sms-email-address. The address must have the format "
							+ "<Phonenumber-with-country-code-without beginning + or 0 signs>."
							+ "<email-address of SMS-gateway>",	false, req);
					
				} else if (!xmppAccepted) {
					alert.showAlert("Invalid xmpp-address", false, req);
				} else if (!restAccepted) {
					alert.showAlert("Invalid REST data. Enter values for all 3 inputfield or leave all empty.", false, req);
				} else {
					alert.showAlert("Please enter atleast one address", false, req);
				}

				return false;
			}

		};
		confirmReceiverChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		confirmReceiverChangesButton.setDefaultText("Save changes");
		confirmReceiverChangesButton.setDefaultConfirmPopupTitle("Edit '" + id + "'");
		confirmReceiverChangesButton.setDefaultConfirmMsg("Accept changes ?");
		confirmReceiverChangesButton.triggerAction(newEMailLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newSmsLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newXmppLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newRESTLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newRESTUserLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(newRESTPwLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmReceiverChangesButton.triggerAction(editReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);

		final PageSnippet editReceiverSnippet = new PageSnippet(page, "editReceiverSnippet" + id, true);
		editReceiverSnippet.append(editReceiverTable, null);
		editReceiverSnippet.append(confirmReceiverChangesButton, null);

		editReceiverPopup.setBody(editReceiverSnippet, null);

		// DELETE
		final ButtonConfirm deleteReceiverButton = new ButtonConfirm(page, "deleteReceiverButton_" + id) {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				receiverTable.removeRow(id, req);
				alert.showAlert("Receiver '" + id + "' successfully deleted", true, req);
			}
			
		};
		deleteReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteReceiverButton.setDefaultText("Delete");
		deleteReceiverButton.setDefaultConfirmPopupTitle("Delete receiver '" + id + "'");
		deleteReceiverButton.setDefaultConfirmMsg("Do you really want to delete '" + id + "' from your list ?");
		deleteReceiverButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteReceiverButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteReceiverButtonColumn", deleteReceiverButton);

		return row;
	}

}