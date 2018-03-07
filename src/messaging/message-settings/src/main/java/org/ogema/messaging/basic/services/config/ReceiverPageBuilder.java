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
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;

public class ReceiverPageBuilder implements ResourceDemandListener<ReceiverConfiguration>{
	
	private final ResourceList<ReceiverConfiguration> receiverConfigs;
	private final DynamicTable<ReceiverConfiguration> receiverTable;
	private final TextField newReceiverNameTextField;
	private final TextField newReceiverEMailTextField;
	private final TextField newReceiverSmsTextField;
	private final TextField newReceiverXmppTextField;
	private static final String emailRegex = "[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
	private static final String smsRegex = "[0-9]+[A-Za-z0-9.-]+[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";

	@SuppressWarnings({ "serial", "unchecked" })
	public ReceiverPageBuilder(final WidgetPage<MessageSettingsDictionary> page, ApplicationManager appMan) {
		
		ResourceManagement resMan = appMan.getResourceManagement();
		
//New Receiver Table
		receiverConfigs = resMan.createResource("receiverConfigurations", ResourceList.class);
	    receiverConfigs.setElementType(ReceiverConfiguration.class);
	    receiverConfigs.activate(false);        
		receiverTable = new DynamicTable<ReceiverConfiguration>(page, "receiverTable", true);
	    
// create test resources if system property is set (see rundir file config/ogema.properties)
		if (Boolean.getBoolean("org.ogema.apps.createtestresources")) 
			createTestReceiverResource(receiverConfigs,appMan);

	    final Header header = new Header(page, "header","Receiver configurations") {
	    	
	    	@Override
	    	public void onGET(OgemaHttpRequest req) {
	    		setText(((MessageSettingsDictionary) getPage().getDictionary(req)).headerReceivers(), req);
	    	}
	    	
	    };
	    header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
	    page.append(header).linebreak();
	    
	    Alert info = new Alert(page, "description","Explanation") {
	    	
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
		
		ReceiverTemplate receiverTemplate = new ReceiverTemplate(receiverConfigs, appMan, receiverTable, alert, page);
		receiverTable.setRowTemplate(receiverTemplate);
		receiverTable.setDefaultRowIdComparator(null);
		
		List<WidgetStyle<?>> styles = new ArrayList<>();
		styles.add(WidgetData.TEXT_ALIGNMENT_CENTERED);
		receiverTable.setDefaultStyles(styles);
		
		final Label newReceiverNameLabel = new Label(page, "newReceiverNameLabel");
		newReceiverNameLabel.setDefaultText("Name : ");
		
		final Label newReceiverEMailLabel = new Label(page, "newReceiverEMailLabel");
		newReceiverEMailLabel.setDefaultText("E-Mail-Address : ");
		
		final Label newReceiverSmsLabel = new Label(page, "newReceiverSmsLabel");
		newReceiverSmsLabel.setDefaultText("Sms-Number : ");
		
		final Label newReceiverXmppLabel = new Label(page, "newReceiverXmppLabel");
		newReceiverXmppLabel.setDefaultText("Xmpp-Adress : ");
		
		newReceiverNameTextField = new TextField(page, "newReceiverNameTextField");
		newReceiverEMailTextField = new TextField(page, "newReceiverEMailTextField");
		newReceiverSmsTextField = new TextField(page, "newReceiverSmsTextField");
		newReceiverXmppTextField = new TextField(page, "newReceiverXmppTextField");
		
		final StaticTable newReceiverTable = new StaticTable(4, 2);
		newReceiverTable.setContent(0, 0, newReceiverNameLabel);
		newReceiverTable.setContent(1, 0, newReceiverEMailLabel);
		newReceiverTable.setContent(2, 0, newReceiverSmsLabel);
		newReceiverTable.setContent(3, 0, newReceiverXmppLabel);
		newReceiverTable.setContent(0, 1, newReceiverNameTextField);
		newReceiverTable.setContent(1, 1, newReceiverEMailTextField);
		newReceiverTable.setContent(2, 1, newReceiverSmsTextField);
		newReceiverTable.setContent(3, 1, newReceiverXmppTextField);
		
		final Popup newReceiverPopup = new Popup(page, "newReceiverPopup",true);
		newReceiverPopup.setTitle("New Receiver", null);
		
		final Button acceptNewReceiverButton = new Button(page, "acceptNewReceiverButton"){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				
				try {
					filter(req);
					addNewReceiver(receiverConfigs, newReceiverNameTextField.getValue(req), newReceiverEMailTextField.getValue(req), 
							newReceiverSmsTextField.getValue(req), newReceiverXmppTextField.getValue(req));
					alert.showAlert("Receiver '" + newReceiverNameTextField.getValue(req) + "' successfully created", true, req);
				} catch (Exception e) {
					alert.showAlert("Could not create new user: " +e.getMessage() ,false, req);
				}
			}
			
		};
		acceptNewReceiverButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewReceiverButton.triggerAction(acceptNewReceiverButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewReceiverButton.triggerAction(newReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		acceptNewReceiverButton.triggerAction(receiverTable,TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		acceptNewReceiverButton.setDefaultText("Accept");
		acceptNewReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		final PageSnippet newReceiverSnippet = new PageSnippet(page, "newReceiverSnippet", true);
		newReceiverSnippet.append(newReceiverTable, null);
		newReceiverSnippet.append(acceptNewReceiverButton, null);
		
		newReceiverPopup.setBody(newReceiverSnippet, null);

		final Button createNewReceiverButton = new Button(page, "newReceiverButton"){
		};
		createNewReceiverButton.triggerAction(newReceiverPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createNewReceiverButton.setDefaultText("create new Receiver");
		createNewReceiverButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		
		PageSnippet receiverSnippet = new PageSnippet(page, "receiverSnippet", true);
		receiverSnippet.append(receiverTable, null);
		receiverSnippet.append(createNewReceiverButton, null);
		
		page.append(receiverSnippet);
		page.append(newReceiverPopup);
		
	}
	
	private void createTestReceiverResource(ResourceList<ReceiverConfiguration> receiverConfigs , ApplicationManager appMan) {
		
		ReceiverConfiguration testConfig = receiverConfigs.add();
		StringResource name = testConfig.userName().create();
		name.setValue("XAll");
		StringResource email = testConfig.email().create();
		email.setValue("testtransmitter@web.de");
		StringResource sms = testConfig.sms().create();
		sms.setValue("49157123456789.testtransmitter@tmsg.de");
		StringResource xmpp = testConfig.xmpp().create();
		xmpp.setValue("testtransmitter2@jabber.de");
		
		testConfig.activate(true);
	}

	public static boolean checkIfReceiverExists(ResourceList<ReceiverConfiguration> receiverConfigs, String userName) {
		for (ReceiverConfiguration receiver: receiverConfigs.getAllElements()) {
			if(receiver.userName().getValue().equals(userName)){
				return true;
			}
		}
		return false;
	}
	
	public static void addNewReceiver(ResourceList<ReceiverConfiguration> receiverConfigs, String newName, String newEMailAddress, String newSmsNumber, String newXmppAddress) {
		
		ReceiverConfiguration newReceiver = receiverConfigs.add();
		
		StringResource userName = newReceiver.userName().create();
		userName.setValue(newName);
		
		if (newEMailAddress != null) {
			newEMailAddress = newEMailAddress.trim();
			if(!newEMailAddress.isEmpty()) {
				StringResource email = newReceiver.email().create();
				email.setValue(newEMailAddress);
			}
		}
		if (newSmsNumber != null) {
			newSmsNumber = newSmsNumber.trim();
			if(!newSmsNumber.isEmpty()) {
				StringResource sms = newReceiver.sms().create();
				sms.setValue(newSmsNumber);
			}
		}
		if (newXmppAddress != null) {
			newXmppAddress = newXmppAddress.trim();
			if(!newXmppAddress.isEmpty()) {
				StringResource xmpp = newReceiver.xmpp().create();
				xmpp.setValue(newXmppAddress);
			}
		}
		
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
	
	private void filter(OgemaHttpRequest req) throws IllegalArgumentException {
		atLeastOneAddress(newReceiverEMailTextField.getValue(req), 
				newReceiverSmsTextField.getValue(req), newReceiverXmppTextField.getValue(req));
		if (newReceiverNameTextField.getValue(req).trim().isEmpty())
			throw new IllegalArgumentException("Please enter a user name");
		String user  = newReceiverNameTextField.getValue(req);
		boolean userExists = checkIfReceiverExists(receiverConfigs, user);
		if (userExists) 
			throw new IllegalArgumentException("The entered user name " + user + " already exists, please choose a different one");
	}
	
	private static void atLeastOneAddress(String emailValue, String smsValue, String xmppValue) throws IllegalArgumentException {
		
		boolean emailAccepted = (!emailValue.trim().isEmpty() && emailValue.matches(emailRegex));
		boolean smsAccepted = (!smsValue.trim().isEmpty() && smsValue.matches(smsRegex));
		boolean xmppAccepted = (!xmppValue.trim().isEmpty() && xmppValue.matches(emailRegex));
		
		if(!emailAccepted && emailValue.length() > 0) {
			throw new IllegalArgumentException("Invalid EMail-Address");
		} else if(!smsAccepted && smsValue.length() > 0) {
			throw new IllegalArgumentException("Invalid Sms-Address");
		} else if(!xmppAccepted && xmppValue.length() > 0) {
			throw new IllegalArgumentException("Invalid Xmpp-Address");
		} else if( (emailValue.trim().length() == 0) && (smsValue.trim().length() == 0) && (xmppValue.trim().length() == 0) ) {
			throw new IllegalArgumentException("You entered less than one Address");
		}
		
	}
	
}
