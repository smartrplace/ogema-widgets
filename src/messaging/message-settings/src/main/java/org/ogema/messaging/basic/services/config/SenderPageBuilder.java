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
package org.ogema.messaging.basic.services.config;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceDemandListener;
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.messaging.basic.services.config.localisation.MessageSettingsDictionary;
import org.ogema.messaging.basic.services.config.model.EmailConfiguration;
import org.ogema.messaging.basic.services.config.model.SenderConfiguration;
import org.ogema.messaging.basic.services.config.model.SmsConfiguration;
import org.ogema.messaging.basic.services.config.model.XmppConfiguration;
import org.ogema.messaging.basic.services.config.template.EmailTemplate;
import org.ogema.messaging.basic.services.config.template.SmsTemplate;
import org.ogema.messaging.basic.services.config.template.XmppTemplate;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;

public class SenderPageBuilder {

	private static final String EMAIL_REGEX = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String SMS_REGEX = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String XMPP_REGEX = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String HOST_REGEX = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String PORT_REGEX = "[0-9]{1,5}$";

	private static final int INVALID_PORT = 70000;
	private static final String UNSELECTED = "___unselected___";

	private final ResourceList<EmailConfiguration> emailConfigs;
	private final ResourceList<SmsConfiguration> smsConfigs;
	private final ResourceList<XmppConfiguration> xmppConfigs;
	private final DynamicTable<EmailConfiguration> emailTable;
	private final DynamicTable<SmsConfiguration> smsTable;
	private final DynamicTable<XmppConfiguration> xmppTable;
	private final ResourceDemandListener<EmailConfiguration> emailListener;
	private final ResourceDemandListener<SmsConfiguration> smsListener;
	private final ResourceDemandListener<XmppConfiguration> xmppListener;
	final ResourceManagement resMan;

	@SuppressWarnings({ "unchecked", "serial", "deprecation" })
	public SenderPageBuilder(final WidgetPage<MessageSettingsDictionary> page, ApplicationManager appManager) {

		resMan = appManager.getResourceManagement();

		emailConfigs = resMan.createResource("emailConfigs", ResourceList.class);
		emailConfigs.setElementType(EmailConfiguration.class);
		emailConfigs.activate(false);

		smsConfigs = resMan.createResource("smsConfigs", ResourceList.class);
		smsConfigs.setElementType(SmsConfiguration.class);
		smsConfigs.activate(false);

		xmppConfigs = resMan.createResource("xmppConfigs", ResourceList.class);
		xmppConfigs.setElementType(XmppConfiguration.class);
		xmppConfigs.activate(false);

		emailListener = new ResourceDemandListener<EmailConfiguration>() {

			@Override
			public void resourceAvailable(EmailConfiguration resource) {
				emailTable.addItem(resource, null);
			}

			@Override
			public void resourceUnavailable(EmailConfiguration resource) {
				emailTable.removeItem(resource, null);
			}

		};

		smsListener = new ResourceDemandListener<SmsConfiguration>() {

			@Override
			public void resourceAvailable(SmsConfiguration resource) {
				smsTable.addItem(resource, null);
			}

			@Override
			public void resourceUnavailable(SmsConfiguration resource) {
				smsTable.removeItem(resource, null);
			}

		};

		xmppListener = new ResourceDemandListener<XmppConfiguration>() {

			@Override
			public void resourceAvailable(XmppConfiguration resource) {
				xmppTable.addItem(resource, null);
			}

			@Override
			public void resourceUnavailable(XmppConfiguration resource) {
				xmppTable.removeItem(resource, null);
			}

		};

		final Header header = new Header(page, "header", "Sender configurations") {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(((MessageSettingsDictionary) getPage().getDictionary(req)).headerSenders(), req);
			}

		};
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		page.append(header).linebreak();

		Alert info = new Alert(page, "description", "Explanation") {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setHtml(((MessageSettingsDictionary) getPage().getDictionary(req)).descriptionSenders(), req);
				allowDismiss(true, req);
				autoDismiss(-1, req);
			}

		};
		page.append(info);
		info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
		info.setDefaultVisibility(true);

		final Alert alert = new Alert(page, "myAlert", "");
		alert.setDefaultVisibility(false);
		page.append(alert).linebreak();

		// create test resources
		if (Boolean.getBoolean("org.ogema.apps.createtestresources")) {
			createEmailUser("EmailTester_1", "testtransmitter@web.de", "123456789", "smtp.web.de", 587, true);
			createSmsUser("SmsTester_1", "testtransmitter@web.de", "123456789", "smtp.web.de", 587, true);
			createXmppUser("XmppTester_1", "testtransmitter@jabber.de", "123456789", 5222, true);
		}
		

		// Email Table
		final SenderDropdown<EmailConfiguration> emailSendersDD = 
				new SenderDropdown<>(page, "emailSenderDropDown", emailConfigs);

		final Label emailSendersLabel = new Label(page, "emailSendersLabel_", true);
		emailSendersLabel.setDefaultText("Selected Email-forwarder");

		final StaticTable emailSenders = new StaticTable(1, 2);
		emailSenders.setContent(0, 0, emailSendersLabel);
		emailSenders.setContent(0, 1, emailSendersDD);

		emailTable = new DynamicTable<EmailConfiguration>(page, "emailTable", true);
		EmailTemplate emailTemplate = new EmailTemplate(emailConfigs, appManager, emailTable, alert, page);
		emailTable.setRowTemplate(emailTemplate);
		emailTable.setDefaultRowIdComparator(null);

		List<WidgetStyle<?>> styles = new ArrayList<>();
		styles.add(WidgetData.TEXT_ALIGNMENT_CENTERED);
		emailTable.setDefaultStyles(styles);

		final Label newEmailNameLabel = new Label(page, "newEmailNameLabel");
		newEmailNameLabel.setDefaultText("Name: ");

		final Label newEmailLabel = new Label(page, "newEmailLabel");
		newEmailLabel.setDefaultText("Email-address: ");

		final Label newEmailPasswordLabel = new Label(page, "newEmailPasswordLabel");
		newEmailPasswordLabel.setDefaultText("Password: ");

		final Label newEmailUrlLabel = new Label(page, "newEmailUrlLabel");
		newEmailUrlLabel.setDefaultText("Host: ");

		final Label newEmailPortLabel = new Label(page, "newEmailPortLabel");
		newEmailPortLabel.setDefaultText("Port: ");

		final TextField newEmailNameTextField = new TextField(page, "newEmailNameTextField");
		final TextField newEmailTextField = new TextField(page, "newEmailTextField");
		final TextField newEmailPasswordTextField = new TextField(page, "newEmailPasswordTextField");
		final TextField newEmailUrlTextField = new TextField(page, "newEmailUrlTextField");
		final TextField newEmailPortTextField = new TextField(page, "newEmailPortTextField");

		final StaticTable newEmailUserTable = new StaticTable(5, 2);
		newEmailUserTable.setContent(0, 0, newEmailNameLabel);
		newEmailUserTable.setContent(1, 0, newEmailLabel);
		newEmailUserTable.setContent(2, 0, newEmailPasswordLabel);
		newEmailUserTable.setContent(3, 0, newEmailUrlLabel);
		newEmailUserTable.setContent(4, 0, newEmailPortLabel);
		newEmailUserTable.setContent(0, 1, newEmailNameTextField);
		newEmailUserTable.setContent(1, 1, newEmailTextField);
		newEmailUserTable.setContent(2, 1, newEmailPasswordTextField);
		newEmailUserTable.setContent(3, 1, newEmailUrlTextField);
		newEmailUserTable.setContent(4, 1, newEmailPortTextField);

		final Popup newEmailUserPopup = new Popup(page, "newUserPopup", true);
		newEmailUserPopup.setTitle("New email-user", null);

		final Button acceptNewEmailUserButton = new Button(page, "acceptNewEmailUserButton") {

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String name = newEmailNameTextField.getValue(req).trim();
				String email = newEmailTextField.getValue(req).trim();
				String pw = newEmailPasswordTextField.getValue(req).trim();
				String host = newEmailUrlTextField.getValue(req).trim();
				String serverPort = newEmailPortTextField.getValue(req).trim();

				int port = INVALID_PORT;

				if (serverPort.matches(PORT_REGEX)) {
					port = Integer.parseInt(serverPort);
				}

				if (email.matches(EMAIL_REGEX) && host.matches(HOST_REGEX) && !name.isEmpty() && (port <= 65535)
						&& !pw.isEmpty()) {

					Boolean userExists;
					userExists = checkIfUserExists(emailConfigs, name);

					if (userExists == false) {
						String currentUser = emailSendersDD.getSelectedValue(req);
						boolean active = currentUser.equals(UNSELECTED);
						createEmailUser(name, email, pw, host, port, active);
						alert.showAlert("User '" + name + "' successfully created", true, req);
					} else {
						alert.showAlert("User '" + name + "' already exists", false, req);
					}
				} else {
					if (!email.matches(EMAIL_REGEX))
						alert.showAlert("Invalid values: Invalid email", false, req);
					if (!host.matches(HOST_REGEX))
						alert.showAlert("Invalid values: Invalid host", false, req);
					if (name.isEmpty())
						alert.showAlert("Invalid values: No name entered", false, req);
					if ((port > 65535))
						alert.showAlert("Invalid values: Invalid port", false, req);
					if (pw.isEmpty())
						alert.showAlert("Invalid values: No password entered", false, req);
				}
			}

		};
		acceptNewEmailUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.triggerAction(acceptNewEmailUserButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.triggerAction(newEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewEmailUserButton.triggerAction(emailTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.triggerAction(emailSendersDD, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.setDefaultText("Accept");
		acceptNewEmailUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newEmailUserSnippet = new PageSnippet(page, "newEmailUserSnippet", true);
		newEmailUserSnippet.append(newEmailUserTable, null);
		newEmailUserSnippet.append(acceptNewEmailUserButton, null);

		newEmailUserPopup.setBody(newEmailUserSnippet, null);

		final Button createNewEmailButton = new Button(page, "newEmailButton");
		createNewEmailButton.triggerAction(createNewEmailButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewEmailButton.triggerAction(newEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewEmailButton.setDefaultText("create new sender");
		createNewEmailButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		PageSnippet emailSnippet = new PageSnippet(page, "emailSnippet", true);
		emailSnippet.append(emailTable, null);
		emailSnippet.append(createNewEmailButton, null);
		emailSnippet.append(emailSenders, null);

		
		// SMS Table
		final SenderDropdown<SmsConfiguration> smsSendersDD = 
				new SenderDropdown<SmsConfiguration>(page, "smsSenderDropDown", smsConfigs);

		final Label smsSendersLabel = new Label(page, "smsSendersLabel_", true);
		smsSendersLabel.setDefaultText("Selected sms-forwarder");

		final StaticTable smsSenders = new StaticTable(1, 2);
		smsSenders.setContent(0, 0, smsSendersLabel);
		smsSenders.setContent(0, 1, smsSendersDD);

		smsTable = new DynamicTable<SmsConfiguration>(page, "smsTable", true);
		SmsTemplate smsTemplate = new SmsTemplate(smsConfigs, appManager, smsTable, alert, page);
		smsTable.setRowTemplate(smsTemplate);
		smsTable.setDefaultRowIdComparator(null);
		smsTable.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);

		smsTable.setDefaultStyles(styles);

		final Label newSmsNameLabel = new Label(page, "newSmsNameLabel");
		newSmsNameLabel.setDefaultText("Name:");
		final Label newSmsEmailLabel = new Label(page, "newSmsEmailLabel");
		newSmsEmailLabel.setDefaultText("Email-address:");
		final Label newSmsEmailPasswordLabel = new Label(page, "newSmsPasswordLabel");
		newSmsEmailPasswordLabel.setDefaultText("Password:");
		final Label newSmsEmailServerLabel = new Label(page, "newSmsEmailServerLabel");
		newSmsEmailServerLabel.setDefaultText("Host:");
		final Label newSmsEmailPortLabel = new Label(page, "newSmsEmailPortLabel");
		newSmsEmailPortLabel.setDefaultText("Port:");

		final TextField newSmsNameTextField = new TextField(page, "newSmsNameTextField");
		final TextField newSmsEmailTextField = new TextField(page, "newSmsEmailTextField");
		final TextField newSmsEmailPasswordTextField = new TextField(page, "newSmsEmailPasswordTextField");
		final TextField newSmsEmailServerTextField = new TextField(page, "newSmsEmailServerTextField");
		final TextField newSmsEmailPortTextField = new TextField(page, "newSmsEmailPortTextField");

		final StaticTable newSmsUserTable = new StaticTable(5, 2);
		newSmsUserTable.setContent(0, 0, newSmsNameLabel);
		newSmsUserTable.setContent(1, 0, newSmsEmailLabel);
		newSmsUserTable.setContent(2, 0, newSmsEmailPasswordLabel);
		newSmsUserTable.setContent(3, 0, newSmsEmailServerLabel);
		newSmsUserTable.setContent(4, 0, newSmsEmailPortLabel);
		newSmsUserTable.setContent(0, 1, newSmsNameTextField);
		newSmsUserTable.setContent(1, 1, newSmsEmailTextField);
		newSmsUserTable.setContent(2, 1, newSmsEmailPasswordTextField);
		newSmsUserTable.setContent(3, 1, newSmsEmailServerTextField);
		newSmsUserTable.setContent(4, 1, newSmsEmailPortTextField);

		final Popup newSmsUserPopup = new Popup(page, "newSmsUserPopup", true);
		newSmsUserPopup.setTitle("New sms-user", null);

		final Button acceptNewSmsUserButton = new Button(page, "acceptNewSmsUserButton") {

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String name = newSmsNameTextField.getValue(req).trim();
				String sms = newSmsEmailTextField.getValue(req).trim();
				String host = newSmsEmailServerTextField.getValue(req).trim();
				String pw = newSmsEmailPasswordTextField.getValue(req);
				String serverPort = newSmsEmailPortTextField.getValue(req).trim();
				int port = INVALID_PORT;

				if (serverPort.matches(PORT_REGEX)) {
					port = Integer.parseInt(serverPort);
				}

				if (!name.isEmpty() && (port <= 65535) && sms.matches(SMS_REGEX) && host.matches(HOST_REGEX) && !pw.isEmpty()) {
					Boolean userExists;
					userExists = checkIfUserExists(smsConfigs, name);
					if (userExists == false) {
						String currentUser = smsSendersDD.getSelectedValue(req);
						boolean active = currentUser.equals(UNSELECTED);
						createSmsUser(name, sms, pw, host, port, active);
						alert.showAlert("User '" + name + "' successfully created", true, req);
					} else {
						alert.showAlert("User '" + name + "' already exists", false, req);
					}
				} else {
					if (!sms.matches(SMS_REGEX)) {
						alert.showAlert("Invalid sms-email-address", false, req);
					} else if (!host.matches(HOST_REGEX)) {
						alert.showAlert("Invalid server-URL", false, req);
					} else if (name.isEmpty()) {
						alert.showAlert("No name entered", false, req);
					} else if ((port > 65535)) {
						alert.showAlert("Invalid port", false, req);
					} else if (pw.isEmpty()) {
						alert.showAlert("No password entered", false, req);
					}
				}
			}

		};
		acceptNewSmsUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewSmsUserButton.triggerAction(acceptNewSmsUserButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewSmsUserButton.triggerAction(newSmsUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewSmsUserButton.triggerAction(smsTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewSmsUserButton.setDefaultText("Accept");
		acceptNewSmsUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newSmsUserSnippet = new PageSnippet(page, "newSmsUserSnippet", true);
		newSmsUserSnippet.append(newSmsUserTable, null);
		newSmsUserSnippet.append(acceptNewSmsUserButton, null);

		newSmsUserPopup.setBody(newSmsUserSnippet, null);

		final Button createNewSmsButton = new Button(page, "newSmsButton");
		createNewSmsButton.triggerAction(createNewSmsButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewSmsButton.triggerAction(newSmsUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewSmsButton.setDefaultText("create new sender");
		createNewSmsButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet smsSnippet = new PageSnippet(page, "smsSnippet", true);
		smsSnippet.append(smsTable, null);
		smsSnippet.append(createNewSmsButton, null);
		smsSnippet.append(smsSenders, null);

		
		// XMPP Table
		final SenderDropdown<XmppConfiguration> xmppSendersDD = 
				new SenderDropdown<>(page, "xmppSenderDropDown", xmppConfigs);

		final Label xmppSendersLabel = new Label(page, "xmppSendersLabel_", true);
		xmppSendersLabel.setDefaultText("Select xmpp-forwarder");

		final StaticTable xmppSenders = new StaticTable(1, 2);
		xmppSenders.setContent(0, 0, xmppSendersLabel);
		xmppSenders.setContent(0, 1, xmppSendersDD);

		xmppTable = new DynamicTable<XmppConfiguration>(page, "xmppTable", true);
		XmppTemplate xmppTemplate = new XmppTemplate(xmppConfigs, appManager, xmppTable, alert, page);
		xmppTable.setRowTemplate(xmppTemplate);
		xmppTable.setDefaultRowIdComparator(null);
		xmppTable.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);

		xmppTable.setDefaultStyles(styles);

		final Label newXmppNameLabel = new Label(page, "newXmppNameLabel");
		newXmppNameLabel.setDefaultText("Name: ");
		final Label newXmppLabel = new Label(page, "newXmppLabel");
		newXmppLabel.setDefaultText("Xmpp address: ");
		final Label newXmppPasswordLabel = new Label(page, "newXmppPasswordLabel");
		newXmppPasswordLabel.setDefaultText("Password: ");
		final Label newXmppPortLabel = new Label(page, "newXmppPortLabel");
		newXmppPortLabel.setDefaultText("Port: ");

		final TextField newXmppNameTextField = new TextField(page, "newXmppNameTextField");
		final TextField newXmppTextField = new TextField(page, "newXmppTextField");
		final TextField newXmppPasswordTextField = new TextField(page, "newXmppPasswordTextField");
		final TextField newXmppPortTextField = new TextField(page, "newXmppPortTextField");

		final StaticTable newXmppUserTable = new StaticTable(4, 2);
		newXmppUserTable.setContent(0, 0, newXmppNameLabel);
		newXmppUserTable.setContent(1, 0, newXmppLabel);
		newXmppUserTable.setContent(2, 0, newXmppPasswordLabel);
		newXmppUserTable.setContent(3, 0, newXmppPortLabel);
		newXmppUserTable.setContent(0, 1, newXmppNameTextField);
		newXmppUserTable.setContent(1, 1, newXmppTextField);
		newXmppUserTable.setContent(2, 1, newXmppPasswordTextField);
		newXmppUserTable.setContent(3, 1, newXmppPortTextField);

		final Popup newXmppUserPopup = new Popup(page, "newXmppUserPopup", true);
		newXmppUserPopup.setTitle("New xmpp-user", null);

		final Button acceptNewXmppUserButton = new Button(page, "acceptNewXmppUserButton") {

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String name = newXmppNameTextField.getValue(req).trim();
				String xmpp = newXmppTextField.getValue(req).trim();
				String pw = newXmppPasswordTextField.getValue(req);
				String serverPort = newXmppPortTextField.getValue(req).trim();
				
				int port = INVALID_PORT;
				
				if (serverPort.matches(PORT_REGEX)) {
					port = Integer.parseInt(serverPort);
				}
				if (xmpp.matches(XMPP_REGEX) && !name.isEmpty() && !pw.isEmpty() && (port <= 65535)) {
					Boolean userExists;
					userExists = checkIfUserExists(xmppConfigs, name);
					if (userExists == false) {
						String currentUser = xmppSendersDD.getSelectedValue(req);
						boolean active = currentUser.equals(UNSELECTED);
						createXmppUser(name, xmpp, pw, port, active);
						alert.showAlert("User '" + name + "' successfully created", true, req);
					} else {
						alert.showAlert("User '" + name + "' already exists", false, req);
					}
				} else {
					if (!xmpp.matches(XMPP_REGEX)) {
						alert.showAlert("Invalid xmpp-address", false, req);
					} else if (name.isEmpty()) {
						alert.showAlert("No name entered", false, req);
					} else if (pw.isEmpty()) {
						alert.showAlert("No password entered", false, req);
					} else if (port > 65535) {
						alert.showAlert("Invalid port", false, req);
					}
				}
			}

		};
		acceptNewXmppUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewXmppUserButton.triggerAction(acceptNewXmppUserButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewXmppUserButton.triggerAction(newXmppUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewXmppUserButton.triggerAction(xmppTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewXmppUserButton.setDefaultText("Accept");
		acceptNewXmppUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newXmppUserSnippet = new PageSnippet(page, "newXmppUserSnippet", true);
		newXmppUserSnippet.append(newXmppUserTable, null);
		newXmppUserSnippet.append(acceptNewXmppUserButton, null);

		newXmppUserPopup.setBody(newXmppUserSnippet, null);

		final Button createNewXmppButton = new Button(page, "newXmppButton");
		createNewXmppButton.triggerAction(createNewXmppButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewXmppButton.triggerAction(newXmppUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewXmppButton.setDefaultText("create new sender");
		createNewXmppButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet xmppSnippet = new PageSnippet(page, "xmppSnippet", true);
		xmppSnippet.append(xmppTable, null);
		xmppSnippet.append(createNewXmppButton, null);
		xmppSnippet.append(xmppSenders, null);

		// Overview
		final Accordion accordion = new Accordion(page, "allAccordion", true);
		accordion.addItem("Email", emailSnippet, null);
		accordion.addItem("Sms", smsSnippet, null);
		accordion.addItem("Xmpp", xmppSnippet, null);

		page.append(accordion);
		page.append(newEmailUserPopup);
		page.append(newSmsUserPopup);
		page.append(newXmppUserPopup);

	}

	private void createEmailUser(String name, String email, String password, String host, int port, boolean createActive) {

		EmailConfiguration newEmailUser = emailConfigs.add();
		
		newEmailUser.userName().<StringResource>create().setValue(name);
		newEmailUser.email().<StringResource>create().setValue(email);
		newEmailUser.password().<StringResource>create().setValue(password);
		newEmailUser.serverURL().<StringResource>create().setValue(host);
		newEmailUser.port().<IntegerResource>create().setValue(port);
		newEmailUser.active().<BooleanResource>create().setValue(createActive);

		newEmailUser.activate(true);

	}

	private void createSmsUser(String name, String sms, String password, String host, int port, boolean createActive) {

		SmsConfiguration newSmsUser = smsConfigs.add();
		
		newSmsUser.userName().<StringResource>create().setValue(name);
		newSmsUser.smsEmail().<StringResource>create().setValue(sms);
		newSmsUser.smsEmailPassword().<StringResource>create().setValue(password);
		newSmsUser.smsEmailServer().<StringResource>create().setValue(host);
		newSmsUser.smsEmailPort().<IntegerResource>create().setValue(port);
		newSmsUser.active().<BooleanResource>create().setValue(createActive);

		newSmsUser.activate(true);

	}

	private void createXmppUser(String name, String xmpp, String password, int port, boolean createActive) {

		XmppConfiguration newXmppUser = xmppConfigs.add();
		
		newXmppUser.userName().<StringResource>create().setValue(name);
		newXmppUser.xmpp().<StringResource>create().setValue(xmpp);
		newXmppUser.password().<StringResource>create().setValue(password);
		newXmppUser.port().<IntegerResource>create().setValue(port);
		newXmppUser.active().<BooleanResource>create().setValue(createActive);

		newXmppUser.activate(true);

	}

	private boolean checkIfUserExists(ResourceList<? extends SenderConfiguration> configs, String user) {
		for (SenderConfiguration cfg : configs.getAllElements()) {
			if (cfg.userName().getValue().equals(user)) {
				return true;
			}
		}
		return false;
	}

	public ResourceDemandListener<EmailConfiguration> getEmailListener() {
		return emailListener;
	}

	public ResourceDemandListener<SmsConfiguration> getSmsListener() {
		return smsListener;
	}

	public ResourceDemandListener<XmppConfiguration> getXmppListener() {
		return xmppListener;
	}

	private static class SenderDropdown<S extends SenderConfiguration> extends Dropdown {

		private static final long serialVersionUID = 1L;
		private final ResourceList<S> entries;

		public SenderDropdown(WidgetPage<?> page, String id, ResourceList<S> entries) {
			super(page, id);
			this.entries = entries;
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			String activeUser = UNSELECTED;
			for (S emailConfig : entries.getAllElements()) {
				String userName = emailConfig.userName().getValue();
				if (emailConfig.active().isActive() && emailConfig.active().getValue()) {
					activeUser = userName;
				}
				if (!this.containsValue(userName, req)) {
					this.addOption(userName, userName, false, req);
				}
			}

			for (DropdownOption opt : getDropdownOptions(req)) {
				Boolean isAvailable = false;
				String userName = opt.id();
				if (userName.equals(UNSELECTED) && activeUser.equals(UNSELECTED))
					continue;
				for (S config : entries.getAllElements()) {
					if (config.userName().getValue().equals(userName)) {
						isAvailable = true;
						break;
					}
				}
				if (!isAvailable) {
					this.removeOption(userName, req);
				}
			}
			// we only need an unselected option if none of the other users is selected
			if (activeUser.equals(UNSELECTED) && !this.containsValue(UNSELECTED, req)) {
				this.addOption("Select a sender", UNSELECTED, false, req);
			}
			selectSingleOption(activeUser, req);

		}

		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			String name = this.getSelectedValue(req);
			for (S conf : entries.getAllElements()) {
				conf.active().<BooleanResource>create().setValue(conf.userName().getValue().equals(name));
				conf.active().activate(false);
			}
		}

	}

}
