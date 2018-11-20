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

import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.ResourceList;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.messaging.basic.services.config.model.SmsConfiguration;
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

public class SmsTemplate extends RowTemplate<SmsConfiguration> {

	protected final ResourceList<SmsConfiguration> list;
	protected final DynamicTable<SmsConfiguration> table;
	protected final Alert alert;
	protected final WidgetPage<?> page;
	protected final ApplicationManager am;
	protected final OgemaLogger logger;
	protected final ResourceAccess ra;

	public SmsTemplate(ResourceList<SmsConfiguration> list, ApplicationManager am, DynamicTable<SmsConfiguration> table,
			Alert alert, WidgetPage<?> page) {
		this.list = list;
		this.table = table;
		this.alert = alert;
		this.page = page;
		this.am = am;
		this.logger = am.getLogger();
		this.ra = am.getResourceAccess();
	}

	@Override
	public Map<String, Object> getHeader() {
		Map<String, Object> smsHeader = new LinkedHashMap<String, Object>();
		smsHeader.put("nameColumn", "Name:");
		smsHeader.put("smsColumn", "Sms-email-address:");
		smsHeader.put("passwordColumn", "Password:");
		smsHeader.put("hostColumn", "Server-URL:");
		smsHeader.put("portColumn", "Port:");
		smsHeader.put("editPopupColumn", "");
		smsHeader.put("editColumn", "");
		smsHeader.put("deleteColumn", "");
		return smsHeader;

	}

	@Override
	public String getLineId(SmsConfiguration object) {
		return ResourceUtils.getValidResourceName(object.userName().getValue());
	}

	@SuppressWarnings("serial")
	@Override
	public Row addRow(final SmsConfiguration config, OgemaHttpRequest req) {
		Row row = new Row();

		// NEW
		final String id = getLineId(config);

		final Label nameLabel = new Label(page, "nameLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.userName().getValue(), req);
			}
			
		};
		row.addCell("nameColumn", nameLabel);

		final Label smsLabel = new Label(page, "smsLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.smsEmail().getValue(), req);
			}
			
		};
		row.addCell("smsColumn", smsLabel);

		final Label passwordLabel = new Label(page, "passwordLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.smsEmailPassword().getValue(), req);
			}
			
		};
		row.addCell("smsEmailPasswordColumn", passwordLabel);

		final Label hostLabel = new Label(page, "hostLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.smsEmailServer().getValue(), req);
			}
			
		};
		row.addCell("hostColumn", hostLabel);

		final Label portLabel = new Label(page, "portLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(config.smsEmailPort().getValue()), req);
			}
			
		};
		row.addCell("portColumn", portLabel);

		// EDIT
		final Label editNameLabel = new Label(page, "editNameLabel_" + id, true);
		editNameLabel.setDefaultText("Name:");
		final Label editSmsLabel = new Label(page, "editSmsLabel_" + id, true);
		editSmsLabel.setDefaultText("New sms-email-address:");
		final Label editPasswordLabel = new Label(page, "editPasswordLabel_" + id, true);
		editPasswordLabel.setDefaultText("New password:");
		final Label editHostLabel = new Label(page, "editHostLabel_" + id, true);
		editHostLabel.setDefaultText("New server-URL:");
		final Label editPortLabel = new Label(page, "editPortLabel_" + id, true);
		editPortLabel.setDefaultText("New port:");

		final TextField editSmsTextField = new TextField(page, "smsTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.smsEmail().getValue(), req);
			}
			
		};

		final TextField editPasswordTextField = new TextField(page, "passwordTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.smsEmailPassword().getValue(), req);
			}
			
		};

		final TextField editHostTextField = new TextField(page, "hostTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.smsEmailServer().getValue(), req);
			}
			
		};

		final TextField editPortTextField = new TextField(page, "portTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(String.valueOf(config.smsEmailPort().getValue()), req);
			}
			
		};

		final Popup editUserPopup = new Popup(page, "editUserPopup_" + id, true);
		editUserPopup.setTitle("Edit User", null);
		row.addCell("editPopupColumn", editUserPopup);

		final Button editUserButton = new Button(page, "editSmsUserButton" + id);
		editUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserButton.triggerAction(editUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editUserButton.triggerAction(editSmsTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserButton.triggerAction(editPasswordTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserButton.triggerAction(editHostTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserButton.triggerAction(editPortTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editUserButton.setDefaultText("Edit");
		row.addCell("editColumn", editUserButton);

		final ButtonConfirm confirmSmsChangesButton = new ButtonConfirm(page, "confirmSmsChangesButton_" + id) {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String smsRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String serverRegex = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";

				String serverPort = editPortTextField.getValue(req);
				String sms = editSmsTextField.getValue(req);
				String host = editHostTextField.getValue(req);
				String pw = editPasswordTextField.getValue(req);
				
				
				int port = 70000;

				if (serverPort.matches(portRegex)) {
					port = Integer.parseInt(serverPort);
				}

				if ((port <= 65535) && sms.matches(smsRegex) && host.matches(serverRegex) && !pw.isEmpty()) {
					config.smsEmail().setValue(sms);
					config.smsEmailPassword().setValue(pw);
					config.smsEmailServer().setValue(host);
					config.smsEmailPort().setValue(port);
					alert.showAlert("Changes on User '" + id + "' confirmed", true, req);
				} else {
					if (port > 65535)
						alert.showAlert("Invalid Port", false, req);
					if (!sms.matches(smsRegex))
						alert.showAlert("Invalid sms-email-Address", false, req);
					if (!host.matches(serverRegex))
						alert.showAlert("Invalid server-URL", false, req);
					if (pw.isEmpty())
						alert.showAlert("No password entered", false, req);
				}
			}
			
		};
		confirmSmsChangesButton.triggerAction(smsLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(editUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		confirmSmsChangesButton.triggerAction(smsLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(passwordLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(hostLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(portLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.setDefaultText("Save Changes");
		confirmSmsChangesButton.setDefaultConfirmPopupTitle("Edit " + id);
		confirmSmsChangesButton.setDefaultConfirmMsg("Accept changes ?");
		confirmSmsChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final StaticTable editUserTable = new StaticTable(5, 2);
		editUserTable.setContent(0, 0, editNameLabel);
		editUserTable.setContent(1, 0, editSmsLabel);
		editUserTable.setContent(2, 0, editPasswordLabel);
		editUserTable.setContent(3, 0, editHostLabel);
		editUserTable.setContent(4, 0, editPortLabel);
		editUserTable.setContent(0, 1, config.userName().getValue());
		editUserTable.setContent(1, 1, editSmsTextField);
		editUserTable.setContent(2, 1, editPasswordTextField);
		editUserTable.setContent(3, 1, editHostTextField);
		editUserTable.setContent(4, 1, editPortTextField);

		final PageSnippet editUserSnippet = new PageSnippet(page, "editUserSnippet" + id, true);
		editUserSnippet.append(editUserTable, null);
		editUserSnippet.append(confirmSmsChangesButton, null);

		editUserPopup.setBody(editUserSnippet, null);

		// DELETE
		final ButtonConfirm deleteSmsUserButton = new ButtonConfirm(page, "deleteSmsUserButton_" + id) {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				alert.showAlert("User '" + id + "' successfully deleted", true, req);
			}
			
		};
		deleteSmsUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteSmsUserButton.setDefaultText("Delete");
		deleteSmsUserButton.setDefaultConfirmPopupTitle("Delete sms-user: " + id);
		deleteSmsUserButton.setDefaultConfirmMsg("Do you really want to delete '" + id + "' from this list ?");
		deleteSmsUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteSmsUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteColumn", deleteSmsUserButton);

		return row;
	}

}