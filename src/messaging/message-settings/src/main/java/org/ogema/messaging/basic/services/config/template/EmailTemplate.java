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
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.messaging.basic.services.config.model.EmailConfiguration;
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

public class EmailTemplate extends RowTemplate<EmailConfiguration> {

	protected final ResourceList<EmailConfiguration> list;
	protected final DynamicTable<EmailConfiguration> table;
	protected final Alert alert;
	protected final WidgetPage<?> page;
	protected final ApplicationManager am;
	protected final OgemaLogger logger;
	protected final ResourceAccess ra;
	protected final ResourceManagement resMan;

	public EmailTemplate(ResourceList<EmailConfiguration> list, ApplicationManager am,
			DynamicTable<EmailConfiguration> table, Alert alert, WidgetPage<?> page) {
		this.list = list;
		this.table = table;
		this.alert = alert;
		this.page = page;
		this.am = am;
		this.logger = am.getLogger();
		this.ra = am.getResourceAccess();
		this.resMan = am.getResourceManagement();
	}

	@Override
	public Map<String, Object> getHeader() {
		Map<String, Object> emailHeader = new LinkedHashMap<String, Object>();
		emailHeader.put("nameColumn", "Name:");
		emailHeader.put("emailColumn", "Email-address:");
		emailHeader.put("passwordColumn", "Password:");
		emailHeader.put("serverColumn", "Server-URL: ");
		emailHeader.put("portColumn", "Port: ");
		emailHeader.put("editPopupColumn", "");
		emailHeader.put("editColumn", "Edit:");
		emailHeader.put("saveColumn", "Save:");
		emailHeader.put("deleteColumn", "Delete:");
		return emailHeader;
	}

	@Override
	public String getLineId(EmailConfiguration object) {
		return ResourceUtils.getValidResourceName(object.userName().getValue());
	}

	@SuppressWarnings("serial")
	@Override
	public Row addRow(final EmailConfiguration config, OgemaHttpRequest req) {

		Row row = new Row();

		// NEW
		final String id = getLineId(config);
		final Label nameLabel = new Label(page, "nameLabel_" + id, true);
		nameLabel.setDefaultText(config.userName().getValue());
		row.addCell("nameColumn", nameLabel);

		final Label emailLabel = new Label(page, "emailLabel_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.email().getValue(), req);
			}
		};
		row.addCell("emailColumn", emailLabel);

		final Label pwLabel = new Label(page, "pwLabel_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.password().getValue(), req);
			}
		};
		row.addCell("passwordColumn", pwLabel);

		final Label serverLabel = new Label(page, "serverLabel_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.serverURL().getValue(), req);
			}
		};
		row.addCell("serverColumn", serverLabel);

		final Label portLabel = new Label(page, "portLabel_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(config.port().getValue()), req);
			}
		};
		portLabel.setDefaultText(String.valueOf(config.port().getValue()));
		row.addCell("portColumn", portLabel);

		config.activate(true);

		// EDIT
		final Label editNameLabel = new Label(page, "editNameLabel_" + id, true);
		editNameLabel.setDefaultText("Name: ");
		final Label editEmailLabel = new Label(page, "editEmailLabel_" + id, true);
		editEmailLabel.setDefaultText("New email-address: ");
		final Label editPwLabel = new Label(page, "editPwLabel_" + id, true);
		editPwLabel.setDefaultText("New password: ");
		final Label editServerLabel = new Label(page, "editServerLabel_" + id, true);
		editServerLabel.setDefaultText("New server-URL: ");
		final Label editPortLabel = new Label(page, "editPortLabel_" + id, true);
		editPortLabel.setDefaultText("New port: ");

		final TextField editEmailTextField = new TextField(page, "editEmailTextField_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.email().getValue(), req);
			}
		};

		final TextField editPwTextField = new TextField(page, "editPwTextField_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.password().getValue(), req);
			}
		};

		final TextField editServerTextField = new TextField(page, "editServerTextField_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.serverURL().getValue(), req);
			}
		};
		final TextField editPortTextField = new TextField(page, "editPortTextField_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(String.valueOf(config.port().getValue()), req);
			}
		};

		final Popup editEmailUserPopup = new Popup(page, "editEmailUserPopup_" + id, true);
		editEmailUserPopup.setTitle("Edit user ", null);
		//row.addCell("editEmailPopupColumn", editEmailUserPopup);
		row.addCell("editPopupColumn", editEmailUserPopup);

		final Button editEmailUserButton = new Button(page, "editEmailUserButton" + id);
		editEmailUserButton.triggerAction(editEmailTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editPwTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editServerTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editPortTextField, TriggeringAction.POST_REQUEST,	TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editEmailUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editEmailUserButton.setDefaultText("Edit");
		//row.addCell("editEmailColumn", editEmailUserButton);
		row.addCell("editColumn", editEmailUserButton);

		final ButtonConfirm confirmEmailChangesButton = new ButtonConfirm(page, "confirmEmailChangesButton_" + id) {

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String emailRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String serverRegex = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";

				final String email = editEmailTextField.getValue(req);
				final String server = editServerTextField.getValue(req);
				final String serverPort = editPortTextField.getValue(req);
				final String pw = editPwTextField.getValue(req);
				int port = 70000;

				if (serverPort.matches(portRegex)) {
					port = Integer.parseInt(serverPort);
				}

				if ((port <= 65535) && email.matches(emailRegex) && server.matches(serverRegex) && !pw.isEmpty()) {
					config.email().setValue(email);
					config.password().setValue(pw);
					config.serverURL().setValue(server);
					config.port().setValue(port);
					alert.showAlert("Changes on user '" + id + "' confirmed", true, req);
				} else {
					if (!email.matches(emailRegex))
						alert.showAlert("Invalid email-address", false, req);
					if ((!server.matches(serverRegex)) && (!server.startsWith("localhost")))
						alert.showAlert("Invalid server-URL", false, req);
					if ((port > 65535))
						alert.showAlert("Invalid port", false, req);
					if (pw.isEmpty())
						alert.showAlert("No password entered", false, req);
				}

			}
		};
		row.addCell("saveColumn", confirmEmailChangesButton);
		confirmEmailChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		confirmEmailChangesButton.setDefaultText("Save changes");
		confirmEmailChangesButton.setDefaultConfirmPopupTitle("Edit " + id);
		confirmEmailChangesButton.setDefaultConfirmMsg("Accept changes ?");
		confirmEmailChangesButton.triggerAction(emailLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(pwLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(portLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(serverLabel, TriggeringAction.POST_REQUEST,	TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(editEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		confirmEmailChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		final StaticTable editEmailUserTable = new StaticTable(5, 2);
		editEmailUserTable.setContent(0, 0, editNameLabel);
		editEmailUserTable.setContent(1, 0, editEmailLabel);
		editEmailUserTable.setContent(2, 0, editPwLabel);
		editEmailUserTable.setContent(3, 0, editServerLabel);
		editEmailUserTable.setContent(4, 0, editPortLabel);
		editEmailUserTable.setContent(0, 1, config.userName().getValue());
		editEmailUserTable.setContent(1, 1, editEmailTextField);
		editEmailUserTable.setContent(2, 1, editPwTextField);
		editEmailUserTable.setContent(3, 1, editServerTextField);
		editEmailUserTable.setContent(4, 1, editPortTextField);

		final PageSnippet editEmailUserSnippet = new PageSnippet(page, "editEmailUserSnippet" + id, true);
		editEmailUserSnippet.append(editEmailUserTable, null);
		editEmailUserSnippet.append(confirmEmailChangesButton, null);

		editEmailUserPopup.setBody(editEmailUserSnippet, null);

		// DELETE
		final ButtonConfirm deleteEmailUserButton = new ButtonConfirm(page, "deleteEmailUserButton_" + id) {

			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				table.removeRow(id, req);
				alert.showAlert("User '" + id + "' successfully deleted", true, req);
			}

		};
		deleteEmailUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteEmailUserButton.setDefaultText("Delete");
		deleteEmailUserButton.setDefaultConfirmPopupTitle("Delete email-user: " + id);
		deleteEmailUserButton.setDefaultConfirmMsg("Do you really want to delete '" + id + "' from your list ?");
		deleteEmailUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteEmailUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteColumn", deleteEmailUserButton);

		return row;
	}

}