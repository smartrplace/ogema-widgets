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
import org.ogema.messaging.basic.services.config.model.XmppConfiguration;
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

public class XmppTemplate extends RowTemplate<XmppConfiguration> {

	protected final ResourceList<XmppConfiguration> list;
	protected final DynamicTable<XmppConfiguration> table;
	protected final Alert alert;
	protected final WidgetPage<?> page;
	protected final ApplicationManager am;
	protected final OgemaLogger logger;
	protected final ResourceAccess ra;

	public XmppTemplate(ResourceList<XmppConfiguration> list, ApplicationManager am,
			DynamicTable<XmppConfiguration> table, Alert alert, WidgetPage<?> page) {
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
		Map<String, Object> xmppHeader = new LinkedHashMap<String, Object>();
		xmppHeader.put("xmppNameColumn", "Name:");
		xmppHeader.put("xmppColumn", "Xmpp-address:");
		xmppHeader.put("xmppPasswordColumn", "Password:");
		xmppHeader.put("xmppPortColumn", "Port:");
		xmppHeader.put("emptyXmppColumn3", "");
		xmppHeader.put("editXmppPopupColumn", "");
		xmppHeader.put("editXmppColumn", "");
		xmppHeader.put("deleteXmppColumn", "");
		return xmppHeader;
	}

	@Override
	public String getLineId(XmppConfiguration object) {
		return ResourceUtils.getValidResourceName(object.userName().getValue());
	}

	@SuppressWarnings("serial")
	@Override
	public Row addRow(final XmppConfiguration config, OgemaHttpRequest req) {
		Row row = new Row();

		// NEW
		final String id = getLineId(config);

		final Label xmppNameLabel = new Label(page, "xmppNameLabel_" + id, true);
		xmppNameLabel.setDefaultText(config.userName().getValue());
		row.addCell("xmppNameColumn", xmppNameLabel);

		final Label xmppLabel = new Label(page, "xmppLabel_" + id, true) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.xmpp().getValue(), req);
			}
			
		};
		row.addCell("xmppColumn", xmppLabel);

		final Label xmppPasswordLabel = new Label(page, "xmppPasswordLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.password().getValue(), req);
			}
			
		};
		row.addCell("xmppPasswordColumn", xmppPasswordLabel);

		final Label xmppPortLabel = new Label(page, "xmppPortLabel_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(config.port().getValue()), req);
			}
			
		};
		row.addCell("xmppPortColumn", xmppPortLabel);

		// EDIT
		final Label editXmppNameLabel = new Label(page, "editXmppNameLabel_" + id, true);
		editXmppNameLabel.setDefaultText("Name: ");
		final Label editXmppLabel = new Label(page, "editXmppLabel_" + id, true);
		editXmppLabel.setDefaultText("New xmpp-address: ");
		final Label editXmppPasswordLabel = new Label(page, "editXmppPasswordLabel_" + id, true);
		editXmppPasswordLabel.setDefaultText("New password: ");
		final Label editXmppPortLabel = new Label(page, "editXmppPortLabel_" + id, true);
		editXmppPortLabel.setDefaultText("New port: ");

		final TextField editXmppTextField = new TextField(page, "xmppTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.xmpp().getValue(), req);
			}
			
		};

		final TextField editXmppPasswordTextField = new TextField(page, "xmppPasswordTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.password().getValue(), req);
			}
			
		};

		final TextField editXmppPortTextField = new TextField(page, "xmppPortTextField_" + id, true) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(String.valueOf(config.port().getValue()), req);
			}
			
		};

		row.addCell("emptyXmppColumn1", "");
		row.addCell("emptyXmppColumn2", "");

		final Popup editXmppUserPopup = new Popup(page, "editXmppUserPopup_" + id, true);
		editXmppUserPopup.setTitle("Edit User", null);
		row.addCell("editXmppPopupColumn", editXmppUserPopup);

		final Button editXmppUserButton = new Button(page, "editXmppUserButton" + id);
		editXmppUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editXmppUserButton.triggerAction(editXmppUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editXmppUserButton.triggerAction(editXmppTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editXmppUserButton.triggerAction(editXmppPasswordTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editXmppUserButton.triggerAction(editXmppPortTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editXmppUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editXmppUserButton.setDefaultText("Edit");
		row.addCell("editXmppColumn", editXmppUserButton);

		final ButtonConfirm confirmXmppChangesButton = new ButtonConfirm(page, "confirmXmppChangesButton_" + id) {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String xmppRegex = "[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";
				int port = 70000;
				if (editXmppPortTextField.getValue(req).matches(portRegex)) {
					port = Integer.parseInt(editXmppPortTextField.getValue(req));
				}
				if (editXmppTextField.getValue(req).matches(xmppRegex)
						&& !editXmppPasswordTextField.getValue(req).isEmpty() && (port <= 65535)) {
					config.xmpp().setValue(editXmppTextField.getValue(req));
					config.password().setValue(editXmppPasswordTextField.getValue(req));
					config.port().setValue(port);
					alert.showAlert("Changes on User '" + id + "' confirmed", true, req);
				} else {
					if (!editXmppTextField.getValue(req).matches(xmppRegex))
						alert.showAlert("Invalid Xmpp-Address", false, req);
					if (editXmppPasswordTextField.getValue(req).isEmpty())
						alert.showAlert("No Password entered", false, req);
					if (port > 65535)
						alert.showAlert("Invalid Port", false, req);
				}
			}
			
		};
		confirmXmppChangesButton.triggerAction(xmppLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmXmppChangesButton.triggerAction(editXmppUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		confirmXmppChangesButton.triggerAction(xmppLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmXmppChangesButton.triggerAction(xmppPasswordLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmXmppChangesButton.triggerAction(xmppPortLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmXmppChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmXmppChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		confirmXmppChangesButton.setDefaultText("Save Changes");
		confirmXmppChangesButton.setDefaultConfirmPopupTitle("Edit " + id);
		confirmXmppChangesButton.setDefaultConfirmMsg("Accept changes ?");

		final StaticTable editXmppUserTable = new StaticTable(4, 2);
		editXmppUserTable.setContent(0, 0, editXmppNameLabel);
		editXmppUserTable.setContent(0, 1, config.userName().getValue());
		editXmppUserTable.setContent(1, 0, editXmppLabel);
		editXmppUserTable.setContent(1, 1, editXmppTextField);
		editXmppUserTable.setContent(2, 0, editXmppPasswordLabel);
		editXmppUserTable.setContent(2, 1, editXmppPasswordTextField);
		editXmppUserTable.setContent(3, 0, editXmppPortLabel);
		editXmppUserTable.setContent(3, 1, editXmppPortTextField);

		final PageSnippet editXmppUserSnippet = new PageSnippet(page, "editXmppUserSnippet" + id, true);
		editXmppUserSnippet.append(editXmppUserTable, null);
		editXmppUserSnippet.append(confirmXmppChangesButton, null);

		editXmppUserPopup.setBody(editXmppUserSnippet, null);

		// DELETE
		final ButtonConfirm deleteXmppUserButton = new ButtonConfirm(page, "deleteXmppUserButton_" + id) {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				table.removeRow(id, req);
				alert.showAlert("User '" + id + "' successfully deleted", true, req);
			}
			
		};
		deleteXmppUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteXmppUserButton.setDefaultText("Delete");
		deleteXmppUserButton.setDefaultConfirmPopupTitle("Delete xmpp-user: " + id);
		deleteXmppUserButton.setDefaultConfirmMsg("Do you really want to delete '" + id + "' from your list ?");
		deleteXmppUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteXmppUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteXmppColumn", deleteXmppUserButton);

		return row;
	}

}