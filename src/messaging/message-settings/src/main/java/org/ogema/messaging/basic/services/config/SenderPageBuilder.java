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
	private final static String UNSELECTED = "___unselected___";

	
	@SuppressWarnings({ "unchecked", "serial" })
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

			public void resourceAvailable(EmailConfiguration resource) {
				emailTable.addItem(resource, null);
			}
			
			public void resourceUnavailable(EmailConfiguration resource) {
				emailTable.removeItem(resource, null);
			}
			
		};
		
		smsListener = new ResourceDemandListener<SmsConfiguration>() {

			public void resourceAvailable(SmsConfiguration resource) {
				smsTable.addItem(resource, null);
			}
			
			public void resourceUnavailable(SmsConfiguration resource) {
				smsTable.removeItem(resource, null);
			}
			
		};
		
		xmppListener = new ResourceDemandListener<XmppConfiguration>() {

			public void resourceAvailable(XmppConfiguration resource) {
				xmppTable.addItem(resource, null);
			}
			
			public void resourceUnavailable(XmppConfiguration resource) {
				xmppTable.removeItem(resource, null);
			}
			
		};
	    
	    final Header header = new Header(page, "header","Sender configurations") {
	    	
	    	@Override
	    	public void onGET(OgemaHttpRequest req) {
	    		setText(((MessageSettingsDictionary) getPage().getDictionary(req)).headerSenders(), req);
	    	}
	    	
	    };
	    header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
	    page.append(header).linebreak();
	    
	    Alert info = new Alert(page, "description","Explanation") {
	    	
	    	@Override
	    	public void onGET(OgemaHttpRequest req) {
	    		setHtml(((MessageSettingsDictionary)getPage().getDictionary(req)).descriptionSenders(), req);
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
			createEmailSenderTestResource(1,appManager);
			createSmsSenderTestResource(1,appManager);
			createXmppSenderTestResource(1,appManager);
		}
		
//Email Table
		final SenderDropdown<EmailConfiguration> emailSendersDD = new SenderDropdown<>(page, "emailSenderDropDown", emailConfigs);
		
		
		final Label emailSendersLabel = new Label(page, "emailSendersLabel_", true);
		emailSendersLabel.setDefaultText("Selected Email-forwarder");
		
		final StaticTable emailSenders = new StaticTable(1, 2);
		emailSenders.setContent(0, 0,emailSendersLabel);
		emailSenders.setContent(0, 1, emailSendersDD);

		emailTable = new DynamicTable<EmailConfiguration>(page, "emailTable", true);
		EmailTemplate emailTemplate = new EmailTemplate(emailConfigs, appManager, emailTable, alert, page);
		emailTable.setRowTemplate(emailTemplate);
		emailTable.setDefaultRowIdComparator(null);

		List<WidgetStyle<?>> styles = new ArrayList<>();
		styles.add(WidgetData.TEXT_ALIGNMENT_CENTERED);
		emailTable.setDefaultStyles(styles);
		
		final Label newEmailNameLabel = new Label(page, "newEmailNameLabel");
		newEmailNameLabel.setDefaultText("Name : ");
		
		final Label newEmailLabel = new Label(page, "newEmailLabel");
		newEmailLabel.setDefaultText("Email-address : ");
		
		final Label newEmailPasswordLabel = new Label(page, "newEmailPasswordLabel");
		newEmailPasswordLabel.setDefaultText("Password : ");
		
		final Label newEmailUrlLabel = new Label(page, "newEmailUrlLabel");
		newEmailUrlLabel.setDefaultText("Url : ");
		
		final Label newEmailPortLabel = new Label(page, "newEmailPortLabel");
		newEmailPortLabel.setDefaultText("Port : ");
		
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
		
		final Popup newEmailUserPopup = new Popup(page, "newUserPopup",true);
		newEmailUserPopup.setTitle("New Email-User", null);
		
		final Button acceptNewEmailUserButton = new Button(page, "acceptNewEmailUserButton"){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				
				String serverRegex = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";
				String emailRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				int port = 70000;
				if(newEmailPortTextField.getValue(req).matches(portRegex)) {
					 port = Integer.parseInt(newEmailPortTextField.getValue(req));
				}
				
				if(newEmailTextField.getValue(req).matches(emailRegex) && newEmailUrlTextField.getValue(req).matches(serverRegex) &&
						!newEmailNameTextField.getValue(req).isEmpty() && (port <= 65535) && !newEmailPasswordTextField.getValue(req).isEmpty()){
					Boolean userExists;
					userExists = checkIfEmailUserExists(emailConfigs, newEmailNameTextField.getValue(req));
					if(userExists == false){
						String currentUser = emailSendersDD.getSelectedValue(req);
						boolean active = currentUser.equals(UNSELECTED);
						addNewEmailUser(newEmailNameTextField.getValue(req), newEmailTextField.getValue(req), 
								newEmailPasswordTextField.getValue(req), newEmailUrlTextField.getValue(req), port, active);
						alert.showAlert("User '" + newEmailNameTextField.getValue(req) + "' successfully created", true, req);
					} else {
						alert.showAlert("User '" + newEmailNameTextField.getValue(req) + "' already exists", false, req);
					}
				} else {
					if(!newEmailTextField.getValue(req).matches(emailRegex))
						alert.showAlert("Invalid values : Invalid E-Mail-Address", false, req);
					if(!newEmailUrlTextField.getValue(req).matches(serverRegex))
						alert.showAlert("Invalid values : Invalid Server-URL", false, req);
					if(newEmailNameTextField.getValue(req).isEmpty()) 
						alert.showAlert("Invalid values : No Name entered", false, req);
					if((port > 65535)) 
						alert.showAlert("Invalid values : Invalid Port", false, req);
					if(newEmailPasswordTextField.getValue(req).isEmpty()) 
						alert.showAlert("Invalid values : No Password entered", false, req);
				}
			}
		};
		acceptNewEmailUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.triggerAction(acceptNewEmailUserButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.triggerAction(newEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewEmailUserButton.triggerAction(emailTable,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewEmailUserButton.setDefaultText("Accept");
		acceptNewEmailUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		acceptNewEmailUserButton.triggerAction(emailSendersDD, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		
		final PageSnippet newEmailUserSnippet = new PageSnippet(page, "newEmailUserSnippet", true);
		newEmailUserSnippet.append(newEmailUserTable, null);
		newEmailUserSnippet.append(acceptNewEmailUserButton, null);
		
		newEmailUserPopup.setBody(newEmailUserSnippet, null);

		final Button createNewEmailButton = new Button(page, "newEmailButton");
		createNewEmailButton.triggerAction(createNewEmailButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewEmailButton.triggerAction(newEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewEmailButton.setDefaultText("create new User");
		createNewEmailButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		
		PageSnippet emailSnippet = new PageSnippet(page, "emailSnippet", true);
		emailSnippet.append(emailTable, null);
		emailSnippet.append(createNewEmailButton, null);
		emailSnippet.append(emailSenders, null);


//SMS Table
		final SenderDropdown<SmsConfiguration> smsSendersDD = new SenderDropdown<SmsConfiguration>(page, "smsSenderDropDown", smsConfigs);
			
//			@Override
//			public void onGET(OgemaHttpRequest req) {
//				//TODO get active sms Sender
//				ResourceList<SmsConfiguration> smsConfigs = appMan.getResourceAccess().getResource("smsConfigs");
//				for(SmsConfiguration c : smsConfigs.getAllElements()) {
//					if(c.active())
//						this.selectSingleOption(, req);
//				}
//			}
//			
//			@Override
//			public void onPOSTComplete(String data, OgemaHttpRequest req) {
//				//TODO set active sms Sender
//			}
			
//		};
		
		final Label smsSendersLabel = new Label(page, "smsSendersLabel_", true);
		smsSendersLabel.setDefaultText("Selected Sms-forwarder");
		
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
		newSmsNameLabel.setDefaultText("Name :");
		final Label newSmsEmailLabel = new Label(page, "newSmsEmailLabel");
		newSmsEmailLabel.setDefaultText("E-Mail-Address :");
		final Label newSmsEmailPasswordLabel = new Label(page, "newSmsPasswordLabel");
		newSmsEmailPasswordLabel.setDefaultText("Password :");
		final Label newSmsEmailServerLabel = new Label(page, "newSmsEmailServerLabel");
		newSmsEmailServerLabel.setDefaultText("Server-URL :");
		final Label newSmsEmailPortLabel = new Label(page, "newSmsEmailPortLabel");
		newSmsEmailPortLabel.setDefaultText("Port :");
		
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
		
		final Popup newSmsUserPopup = new Popup(page, "newSmsUserPopup",true);
		newSmsUserPopup.setTitle("New Sms-User", null);
		
		final Button acceptNewSmsUserButton = new Button(page, "acceptNewSmsUserButton"){
			public void onPOSTComplete(String data, OgemaHttpRequest req){
				
				String emailRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String serverRegex = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";
				int port = 70000;
				
				if (newSmsEmailPortTextField.getValue(req).matches(portRegex)) {
					port = Integer.parseInt(newSmsEmailPortTextField.getValue(req));
				}
				
				if(!newSmsNameTextField.getValue(req).isEmpty() && (port <= 65535) && newSmsEmailTextField.getValue(req).matches(emailRegex) 
						&& newSmsEmailServerTextField.getValue(req).matches(serverRegex) && !newSmsEmailPasswordTextField.getValue(req).isEmpty()){
					Boolean userExists;
					userExists = checkIfSmsUserExists(smsConfigs, newSmsNameTextField.getValue(req));
					if(userExists == false){
						String currentUser = smsSendersDD.getSelectedValue(req);
						boolean active = currentUser.equals(UNSELECTED);
						addNewSmsUser(newSmsNameTextField.getValue(req), newSmsEmailTextField.getValue(req), 
								newSmsEmailPasswordTextField.getValue(req), newSmsEmailServerTextField.getValue(req), port,active);
						alert.showAlert("User '" + newSmsNameTextField.getValue(req) + "' successfully created", true, req);
					} else {
						alert.showAlert("User '" +  newSmsNameTextField.getValue(req) + "' already exists", false, req);
					}
				} else {
					if(!newSmsEmailTextField.getValue(req).matches(emailRegex))
						alert.showAlert("Invalid Sms-E-Mail-Address", false, req);
					if(!newSmsEmailServerTextField.getValue(req).matches(serverRegex))
						alert.showAlert("Invalid Server-URL", false, req);
					if(newSmsNameTextField.getValue(req).isEmpty()) 
						alert.showAlert("No Name entered", false, req);
					if((port > 65535)) 
						alert.showAlert("Invalid Port", false, req);
					if(newSmsEmailPasswordTextField.getValue(req).isEmpty()) 
						alert.showAlert("No Password entered", false, req);
				}
			}
		};
		acceptNewSmsUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewSmsUserButton.triggerAction(acceptNewSmsUserButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewSmsUserButton.triggerAction(newSmsUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewSmsUserButton.triggerAction(smsTable,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewSmsUserButton.setDefaultText("Accept");
		acceptNewSmsUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newSmsUserSnippet = new PageSnippet(page, "newSmsUserSnippet", true);
		newSmsUserSnippet.append(newSmsUserTable, null);
		newSmsUserSnippet.append(acceptNewSmsUserButton, null);
		
		newSmsUserPopup.setBody(newSmsUserSnippet, null);

		final Button createNewSmsButton = new Button(page, "newSmsButton");
		createNewSmsButton.triggerAction(createNewSmsButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewSmsButton.triggerAction(newSmsUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewSmsButton.setDefaultText("create new User");
		createNewSmsButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		
		final PageSnippet smsSnippet = new PageSnippet(page, "smsSnippet", true);
		smsSnippet.append(smsTable, null);
		smsSnippet.append(createNewSmsButton, null);
		smsSnippet.append(smsSenders, null);
		
		
//XMPP Table
		final SenderDropdown<XmppConfiguration> xmppSendersDD = new SenderDropdown<>(page, "xmppSenderDropDown", xmppConfigs);
		
		final Label xmppSendersLabel = new Label(page, "xmppSendersLabel_", true);
		xmppSendersLabel.setDefaultText("Select Xmpp-forwarder");
		
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
		newXmppNameLabel.setDefaultText("Name : ");
		final Label newXmppLabel = new Label(page, "newXmppLabel");
		newXmppLabel.setDefaultText("Xmpp Address : ");
		final Label newXmppPasswordLabel = new Label(page, "newXmppPasswordLabel");
		newXmppPasswordLabel.setDefaultText("Password : ");
		final Label newXmppPortLabel = new Label(page, "newXmppPortLabel");
		newXmppPortLabel.setDefaultText("Port : ");
		
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
		
		final Popup newXmppUserPopup = new Popup(page, "newXmppUserPopup",true);
		newXmppUserPopup.setTitle("New Xmpp-User", null);
		
		final Button acceptNewXmppUserButton = new Button(page, "acceptNewXmppUserButton"){
			public void onPOSTComplete(String data, OgemaHttpRequest req){
				String xmppRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";
				int port = 70000;
				if(newXmppPortTextField.getValue(req).matches(portRegex)) {
					port = Integer.parseInt(newXmppPortTextField.getValue(req));
				}
				if(newXmppTextField.getValue(req).matches(xmppRegex) && !newXmppNameTextField.getValue(req).isEmpty() && 
						!newXmppPasswordTextField.getValue(req).isEmpty() && (port <= 65535) ){
					Boolean userExists;
					userExists = checkIfXmppUserExists(xmppConfigs, newXmppNameTextField.getValue(req));
					if(userExists == false){
						String currentUser = xmppSendersDD.getSelectedValue(req);
						boolean active = currentUser.equals(UNSELECTED);
						addNewXmppUser(newXmppNameTextField.getValue(req), newXmppTextField.getValue(req), 
								newXmppPasswordTextField.getValue(req), Integer.parseInt(newXmppPortTextField.getValue(req)),active);
						alert.showAlert("User '" + newXmppNameTextField.getValue(req) + "' successfully created", true, req);
					} else {
						alert.showAlert("User '" +  newXmppNameTextField.getValue(req) + "' already exists", false, req);
					}
				} else {
					if(!newXmppTextField.getValue(req).matches(xmppRegex))
						alert.showAlert("Invalid Xmpp-Address", false, req);
					if(newXmppNameTextField.getValue(req).isEmpty())
						alert.showAlert("No Name entered", false, req);
					if(newXmppPasswordTextField.getValue(req).isEmpty())
						alert.showAlert("No Password Entered", false, req);
					if(port > 65535)
						alert.showAlert("Invalid Port", false, req);
				}
			}
		};
		acceptNewXmppUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewXmppUserButton.triggerAction(acceptNewXmppUserButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewXmppUserButton.triggerAction(newXmppUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewXmppUserButton.triggerAction(xmppTable,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewXmppUserButton.setDefaultText("Accept");
		acceptNewXmppUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newXmppUserSnippet = new PageSnippet(page, "newXmppUserSnippet", true);
		newXmppUserSnippet.append(newXmppUserTable, null);
		newXmppUserSnippet.append(acceptNewXmppUserButton, null);
		
		newXmppUserPopup.setBody(newXmppUserSnippet, null);

		final Button createNewXmppButton = new Button(page, "newXmppButton");
		createNewXmppButton.triggerAction(createNewXmppButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewXmppButton.triggerAction(newXmppUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewXmppButton.setDefaultText("create new User");
		createNewXmppButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		
		final PageSnippet xmppSnippet = new PageSnippet(page, "xmppSnippet", true);
		xmppSnippet.append(xmppTable, null);
		xmppSnippet.append(createNewXmppButton, null);
		xmppSnippet.append(xmppSenders, null);
		
		
//Overview
		final Accordion accordion = new Accordion(page, "allAccordion",true);
		accordion.addItem("E-Mail", emailSnippet, null);
		accordion.addItem("Sms", smsSnippet, null);
		accordion.addItem("Xmpp", xmppSnippet, null);

		page.append(accordion);
		page.append(newEmailUserPopup);
		page.append(newSmsUserPopup);
		page.append(newXmppUserPopup);

	}
	
	private void createEmailSenderTestResource(int id, ApplicationManager am) {
		addNewEmailUser("EmailTester_" + id, "testtransmitter@web.de", "123456789", "smtp.web.de", 587, id==1);
	}
	
	private void createSmsSenderTestResource(int id, ApplicationManager am) {
		addNewSmsUser("SmsTester_" + id, "testtransmitter@web.de",  "123456789", "smtp.web.de", 587, id==1);
	}
	
	private void createXmppSenderTestResource(int id, ApplicationManager am) {
		addNewXmppUser("XmppTester_" + id, "testtransmitter@jabber.de", "123456789",5222, id==1);
	}

	private void addNewEmailUser(String emailName, String emailAddress, String pw, String serverUrl, int serverPort, boolean createActive){
		
		EmailConfiguration newEmailUser = emailConfigs.add();
		StringResource user = newEmailUser.userName().create();
		user.setValue(emailName);
		StringResource email = newEmailUser.email().create();
		email.setValue(emailAddress);
		StringResource password = newEmailUser.password().create();
		password.setValue(pw);
		StringResource serverURL = newEmailUser.serverURL().create();
		serverURL.setValue(serverUrl);
		IntegerResource port = newEmailUser.port().create();
		port.setValue(serverPort);
		BooleanResource active = newEmailUser.active().create();
		active.setValue(createActive);
		
		newEmailUser.activate(true);

	}
	
	private void addNewSmsUser(String newSmsName, String newSmsEmail, 
			String newSmsEmailPassword, String newSmsEmailServer, int newSmsEmailPort, boolean createActive){
		
		SmsConfiguration newSmsUser = smsConfigs.add();
		StringResource name = newSmsUser.userName().create();
		name.setValue(newSmsName);
		StringResource smsEmail = newSmsUser.smsEmail().create();
		smsEmail.setValue(newSmsEmail);
		StringResource smsEmailPassword = newSmsUser.smsEmailPassword().create();
		smsEmailPassword.setValue(newSmsEmailPassword);
		StringResource smsEmailServer = newSmsUser.smsEmailServer().create();
		smsEmailServer.setValue(newSmsEmailServer);
		IntegerResource smsEmailPort = newSmsUser.smsEmailPort().create();
		smsEmailPort.setValue(newSmsEmailPort);
		BooleanResource active = newSmsUser.active().create();
		active.setValue(createActive);
		
		newSmsUser.activate(true);

	}
	
	private void addNewXmppUser(String xmppName, String xmppAddress, String password, int port, boolean createActive){
		
		XmppConfiguration newXmppUser = xmppConfigs.add();
		StringResource user = newXmppUser.userName().create();
		user.setValue(xmppName);
		StringResource xmpp = newXmppUser.xmpp().create();
		xmpp.setValue(xmppAddress);
		StringResource pw = newXmppUser.password().create();
		pw.setValue(password);
		IntegerResource prt = newXmppUser.port().create();
		prt.setValue(port);
		BooleanResource active = newXmppUser.active().create();
		active.setValue(createActive);
			
		newXmppUser.activate(true);

	}
	
	private boolean checkIfEmailUserExists(ResourceList<EmailConfiguration> emailConfigs, String user){
		for (EmailConfiguration cfg: emailConfigs.getAllElements()) {
			if(cfg.userName().getValue().equals(user)){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIfSmsUserExists(ResourceList<SmsConfiguration> smsConfigs, String user){
		for (SmsConfiguration cfg: smsConfigs.getAllElements()) {
			if(cfg.userName().getValue().equals(user)){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIfXmppUserExists(ResourceList<XmppConfiguration> xmppConfigs, String user){
		for (XmppConfiguration cfg: xmppConfigs.getAllElements()) {
			if(cfg.userName().getValue().equals(user)){
				return true;
			}
		}
		return false;
	}
	
	public ResourceDemandListener<EmailConfiguration> getEmailListener (){
		return emailListener;
	}
	public ResourceDemandListener<SmsConfiguration> getSmsListener (){
		return smsListener;
	}
	public ResourceDemandListener<XmppConfiguration> getXmppListener (){
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
			for(S emailConfig : entries.getAllElements()) {
				String userName = emailConfig.userName().getValue();
				if (emailConfig.active().isActive() && emailConfig.active().getValue()) {
					activeUser = userName;
				}
				if(!this.containsValue(userName, req)) {
					this.addOption(userName, userName , false, req);
				}
			}
			
			for (DropdownOption opt: getDropdownOptions(req))  {
				Boolean isAvailable = false;
				String userName = opt.id();
				if (userName.equals(UNSELECTED) && activeUser.equals(UNSELECTED))
					continue;
				for(S config : entries.getAllElements()) {
					if(config.userName().getValue().equals(userName)){
						isAvailable = true;
						break;
					} 
				}
				if(!isAvailable) {
					this.removeOption(userName, req);
				}
			}
			// we only need an unselected option if none of the other users is selected
			if(activeUser.equals(UNSELECTED) && !this.containsValue(UNSELECTED, req)) {
				this.addOption("Select a Sender",UNSELECTED, false, req);
			}
			selectSingleOption(activeUser, req);
			
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			String name = this.getSelectedValue(req);
			for(S conf : entries.getAllElements()) {
				conf.active().<BooleanResource> create().setValue(conf.userName().getValue().equals(name));
				conf.active().activate(false);
			}
		}

	}
	
}
