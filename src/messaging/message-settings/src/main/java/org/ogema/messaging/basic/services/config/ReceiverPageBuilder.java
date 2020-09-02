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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceDemandListener;
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.messaging.basic.services.config.localisation.MessageSettingsDictionary;
import org.ogema.messaging.basic.services.config.model.ReceiverConfiguration;
import org.ogema.messaging.basic.services.config.template.ReceiverTemplate;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate.Row;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;

public class ReceiverPageBuilder implements ResourceDemandListener<ReceiverConfiguration> {

	private static final String EMAIL_REGEX = "[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String SMS_REGEX = "[0-9]+[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String XMPP_REGEX = "[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	
	private final ResourceList<ReceiverConfiguration> receiverConfigs;
	public final DynamicTable<ReceiverConfiguration> receiverTable;

	/** overwrite to add additional widgets in row before edit popup/button
	 * @param row 
	 * @param id 
	 * @param config 
	 * @param req 
	 */
	protected void addAdditionalRowWidgets(ReceiverConfiguration config, String id, Row row, OgemaHttpRequest req) {};
	protected void addAdditionalColumns(Map<String, Object> receiverHeader) {};
		
	@SuppressWarnings({ "serial", "unchecked" })
	public ReceiverPageBuilder(final WidgetPage<MessageSettingsDictionary> page, ApplicationManager appMan) {

		ResourceManagement resMan = appMan.getResourceManagement();

		// New Receiver Table
		receiverConfigs = resMan.createResource("receiverConfigurations", ResourceList.class);
		receiverConfigs.setElementType(ReceiverConfiguration.class);
		receiverConfigs.activate(false);
		receiverTable = new DynamicTable<ReceiverConfiguration>(page, "receiverTable", true);

		// create test resources if system property is set (see rundir file config/ogema.properties)
		if (Boolean.getBoolean("org.ogema.apps.createtestresources")) {
			createTestReceiverResource(receiverConfigs, appMan);
		}
		
		final Header header = new Header(page, "header", "Receiver configurations") {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(((MessageSettingsDictionary) getPage().getDictionary(req)).headerReceivers(), req);
			}

		};
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_LEFT);
		page.append(header).linebreak();

		Alert info = new Alert(page, "description", "Explanation") {

			@Override
			public void onGET(OgemaHttpRequest req) {
				setHtml(((MessageSettingsDictionary) getPage().getDictionary(req)).descriptionReceivers(), req);
				allowDismiss(true, req);
				autoDismiss(-1, req);
			}

		};
		page.append(info).linebreak();
		info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
		info.setDefaultVisibility(true);

		final Alert alert = new Alert(page, "myAlert", "");
		alert.setDefaultVisibility(false);
		page.append(alert).linebreak();

		ReceiverTemplate receiverTemplate = new ReceiverTemplate(receiverConfigs, appMan, receiverTable, alert, page) {
			@Override
			protected void addAdditionalRowWidgets(ReceiverConfiguration config, String id, Row row,
					OgemaHttpRequest req) {
				ReceiverPageBuilder.this.addAdditionalRowWidgets(config, id, row, req);
			}
			
			@Override
			protected void addAdditionalColumns(Map<String, Object> receiverHeader) {
				ReceiverPageBuilder.this.addAdditionalColumns(receiverHeader);
			}
		};
		receiverTable.setRowTemplate(receiverTemplate);
		receiverTable.setDefaultRowIdComparator(null);

		List<WidgetStyle<?>> styles = new ArrayList<>();
		styles.add(WidgetData.TEXT_ALIGNMENT_CENTERED);
		receiverTable.setDefaultStyles(styles);

		final Label nameLabel = new Label(page, "nameLabel");
		final Label emailLabel = new Label(page, "emailLabel");
		final Label smsLabel = new Label(page, "smsLabel");
		final Label xmppLabel = new Label(page, "xmppLabel");
		final Label restLabel = new Label(page, "restLabel");
		final Label restUserLabel = new Label(page, "restUserLabel");
		final Label restPwLabel = new Label(page, "restPwLabel");
		
		nameLabel.setDefaultText("Name: ");
		emailLabel.setDefaultText("Email-address: ");
		smsLabel.setDefaultText("Sms-number: ");
		xmppLabel.setDefaultText("Xmpp-address: ");
		restLabel.setDefaultText("Remote-message-address: ");
		restUserLabel.setDefaultText("Remote-message-user: ");
		restPwLabel.setDefaultText("Remote-message-password: ");

		final TextField nameInput = new TextField(page, "nameInput");
		final TextField emailInput = new TextField(page, "emailInput");
		final TextField smsInput = new TextField(page, "smsInput");
		final TextField xmppInput = new TextField(page, "xmppInput");
		final TextField restInput = new TextField(page, "restInput");
		final TextField restUserInput = new TextField(page, "restUserInput");
		final TextField restPwInput = new TextField(page, "restPwInput");

		final StaticTable newReceiverTable = new StaticTable(7, 2);
		newReceiverTable.setContent(0, 0, nameLabel);
		newReceiverTable.setContent(1, 0, emailLabel);
		newReceiverTable.setContent(2, 0, smsLabel);
		newReceiverTable.setContent(3, 0, xmppLabel);
		newReceiverTable.setContent(4, 0, restLabel);
		newReceiverTable.setContent(5, 0, restUserLabel);
		newReceiverTable.setContent(6, 0, restPwLabel);
		newReceiverTable.setContent(0, 1, nameInput);
		newReceiverTable.setContent(1, 1, emailInput);
		newReceiverTable.setContent(2, 1, smsInput);
		newReceiverTable.setContent(3, 1, xmppInput);
		newReceiverTable.setContent(4, 1, restInput);
		newReceiverTable.setContent(5, 1, restUserInput);
		newReceiverTable.setContent(6, 1, restPwInput);

		final Popup newReceiverPopup = new Popup(page, "newReceiverPopup", true);
		newReceiverPopup.setTitle("New receiver", null);

		final Button acceptNewReceiverButton = new Button(page, "acceptNewReceiverButton") {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				String name = nameInput.getValue(req).trim();
				String email = emailInput.getValue(req).trim();
				String sms = smsInput.getValue(req).trim();
				String xmpp = xmppInput.getValue(req).trim();
				String rest = restInput.getValue(req).trim();
				String restUser = restUserInput.getValue(req).trim();
				String restPw = restPwInput.getValue(req);
				
				try {
					
					Logger.getGlobal().info(rest + ", " + restUser + ", " + restPw);					
					filter(name, email, sms, xmpp, rest, restUser, restPw, req);
					addNewReceiver(receiverConfigs, name, email, sms, xmpp, rest, restUser, restPw);
					alert.showAlert("Receiver '" + name + "' successfully created",	true, req);
				} catch (Exception e) {
					alert.showAlert("Could not create new user: " + e.getMessage(), false, req);
				}
			}

		};
		acceptNewReceiverButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewReceiverButton.triggerAction(acceptNewReceiverButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewReceiverButton.triggerAction(newReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewReceiverButton.triggerAction(receiverTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewReceiverButton.setDefaultText("Accept");
		acceptNewReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newReceiverSnippet = new PageSnippet(page, "newReceiverSnippet", true);
		newReceiverSnippet.append(newReceiverTable, null);
		newReceiverSnippet.append(acceptNewReceiverButton, null);

		newReceiverPopup.setBody(newReceiverSnippet, null);

		final Button createNewReceiverButton = new Button(page, "newReceiverButton");
		createNewReceiverButton.triggerAction(newReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewReceiverButton.setDefaultText("create new receiver");
		createNewReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		PageSnippet receiverSnippet = new PageSnippet(page, "receiverSnippet", true);
		receiverSnippet.append(receiverTable, null);
		receiverSnippet.append(createNewReceiverButton, null);

		page.append(receiverSnippet);
		page.append(newReceiverPopup);

	}

	private void createTestReceiverResource(ResourceList<ReceiverConfiguration> receiverConfigs,
			ApplicationManager appMan) {

		ReceiverConfiguration testConfig = receiverConfigs.add();
		StringResource name = testConfig.userName().create();
		name.setValue("XAll");
		StringResource email = testConfig.email().create();
		email.setValue("testtransmitter@web.de");
		StringResource sms = testConfig.sms().create();
		sms.setValue("49157123456789.testtransmitter@tmsg.de");
		StringResource xmpp = testConfig.xmpp().create();
		xmpp.setValue("testtransmitter2@jabber.de");
		StringResource rest = testConfig.remoteMessageRestUrl().create();
		rest.setValue("https://localhost:8444/rest/resources");
		StringResource restUser = testConfig.remoteMessageRestUrl().create();
		restUser.setValue("rest");
		StringResource restPw = testConfig.remoteMessagePassword().create();
		restPw.setValue("rest");
		
		testConfig.activate(true);
	}

	public static boolean checkIfReceiverExists(ResourceList<ReceiverConfiguration> receiverConfigs, String userName) {
		
		for (ReceiverConfiguration receiver : receiverConfigs.getAllElements()) {
			if (receiver.userName().getValue().equals(userName)) {
				return true;
			}
		}
		
		return false;
	}

	public static void addNewReceiver(ResourceList<ReceiverConfiguration> receiverConfigs, String name,
			String email, String sms, String xmpp, String rest, String restUser, String restPw) {

		ReceiverConfiguration newReceiver = receiverConfigs.add();

		newReceiver.userName().<StringResource>create().setValue(name);
		if(!email.isEmpty())
			newReceiver.email().<StringResource>create().setValue(email);
		if(!sms.isEmpty())
			newReceiver.sms().<StringResource>create().setValue(sms);
		if(!xmpp.isEmpty())
			newReceiver.xmpp().<StringResource>create().setValue(xmpp);
		if(!rest.isEmpty())
			newReceiver.remoteMessageRestUrl().<StringResource>create().setValue(
					rest.endsWith("/") ? rest : rest+"/");
		if(!restUser.isEmpty())
			newReceiver.remoteMessageUser().<StringResource>create().setValue(restUser);
		if(!restPw.isEmpty())
			newReceiver.remoteMessagePassword().<StringResource>create().setValue(restPw);
		
		newReceiver.activate(true);
	}

	@Override
	public void resourceAvailable(ReceiverConfiguration receiver) {
		receiverTable.addItem(receiver, null);
	}

	@Override
	public void resourceUnavailable(ReceiverConfiguration receiver) {
		receiverTable.removeItem(receiver, null);
	}

	private void filter(String name, String email, String sms, String xmpp, String rest, String restUser, 
			String restPw, OgemaHttpRequest req) throws IllegalArgumentException {
		
		atLeastOneAddress(email, sms, xmpp, rest);
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Empty username");
		}
		if (!rest.isEmpty()) {
			if (restUser.isEmpty())  {
				throw new IllegalArgumentException("Empty REST-user");
			} else if (restPw.isEmpty())  {
				throw new IllegalArgumentException("Empty REST-password");
			}
		} else {
			if(!restUser.isEmpty() || !restPw.isEmpty()) {
				throw new IllegalArgumentException("REST-user/REST-password without REST-server");
			}
		}
		
		if (checkIfReceiverExists(receiverConfigs, name)) {
			throw new IllegalArgumentException("The entered username " + name + " already exists, please choose a different one");
		}
	}

	private static void atLeastOneAddress(String email, String sms, String xmpp, String rest) throws IllegalArgumentException {

		boolean emailMatchesRegex = (!email.isEmpty() && email.matches(EMAIL_REGEX));
		boolean smsMatchesRegex = (!sms.isEmpty() && sms.matches(SMS_REGEX));
		boolean xmppMatchesRegex = (!xmpp.isEmpty() && xmpp.matches(XMPP_REGEX));

		if (!emailMatchesRegex && !email.isEmpty()) {
			throw new IllegalArgumentException("Invalid email-address");
		} else if (!smsMatchesRegex && !sms.isEmpty()) {
			throw new IllegalArgumentException("Invalid sms-address");
		} else if (!xmppMatchesRegex && !xmpp.isEmpty()) {
			throw new IllegalArgumentException("Invalid xmpp-address");
		} else if (!rest.isEmpty())  {
			try {
				// Checking if the entered rest string is a valid URL
				new URI(rest);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid REST-address");
			}
		} else if ((email.isEmpty()) && (sms.isEmpty())
				&& (xmpp.isEmpty()) && (rest.isEmpty())) {
			throw new IllegalArgumentException("Please enter atleast one address");
		}

	}

}
