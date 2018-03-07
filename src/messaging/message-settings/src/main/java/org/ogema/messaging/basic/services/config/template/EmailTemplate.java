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

public class EmailTemplate extends RowTemplate<EmailConfiguration>{

	protected final ResourceList<EmailConfiguration> list;
	protected final DynamicTable<EmailConfiguration> table;
	protected final Alert alert;
	protected final WidgetPage<?> page;
	protected final ApplicationManager am;
	protected final OgemaLogger logger;
	protected final ResourceAccess ra;
	protected final ResourceManagement resMan;
	
	public EmailTemplate(ResourceList<EmailConfiguration> list, ApplicationManager am , DynamicTable<EmailConfiguration> table, Alert alert, WidgetPage<?> page) {
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
		emailHeader.put("emailNameColumn", "Name :");
		emailHeader.put("emailColumn", "E-Mail-Address :");
		emailHeader.put("emailPasswordColumn", "Password :");
		emailHeader.put("emailServerColumn", "Server-URL : ");
		emailHeader.put("emailPortColumn", "Port : ");
		emailHeader.put("editEmailPopupColumn", "");
		emailHeader.put("editEmailColumn", "");
		emailHeader.put("deleteEmailColumn", "");
		return emailHeader;
	}
	
	@Override
	public String getLineId(EmailConfiguration object) {
		return ResourceUtils.getValidResourceName(object.userName().getValue());
	}
	
	@Override
	public Row addRow(final EmailConfiguration config, OgemaHttpRequest req) {
		
		Row row = new Row();
		
//NEW
		final String id = getLineId(config);
		final Label emailNameLabel = new Label(page, "emailNameLabel_" + id, true);
		emailNameLabel.setDefaultText(config.userName().getValue());
		row.addCell("emailNameColumn", emailNameLabel);
		
		final Label emailLabel = new Label(page, "emailLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.email().getValue(),req);
			}
		};
		row.addCell("emailColumn", emailLabel);
		
		final Label pwLabel = new Label(page, "pwLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.password().getValue(),req);
			}
		};
		row.addCell("emailPasswordColumn", pwLabel);
		
		final Label serverLabel = new Label(page, "serverLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.serverURL().getValue(),req);
			}
		};
		row.addCell("emailServerColumn", serverLabel);
		
		final Label portLabel = new Label(page, "portLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(config.port().getValue()),req);
			}
		};
		portLabel.setDefaultText(String.valueOf(config.port().getValue()));
		row.addCell("emailPortColumn", portLabel);
		
		config.activate(true);
		
		
//EDIT
		final Label editEmailNameLabel = new Label(page, "editEmailNameLabel_" + id, true);
		editEmailNameLabel.setDefaultText("Name : ");
		final Label editEmailLabel = new Label(page, "editEmailLabel_" + id, true);
		editEmailLabel.setDefaultText("new E-Mail-Address : ");
		final Label editPwLabel = new Label(page, "editPwLabel_" + id, true);
		editPwLabel.setDefaultText("new Password : ");
		final Label editServerLabel = new Label(page, "editServerLabel_" + id, true);
		editServerLabel.setDefaultText("new Server-URL : ");
		final Label editPortLabel = new Label(page, "editPortLabel_" + id, true);
		editPortLabel.setDefaultText("new Port : ");
		
		final TextField editEmailTextField = new TextField(page, "editEmailTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.email().getValue(), req);
			}
		};
		
		final TextField editPwTextField = new TextField(page, "editPwTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.password().getValue(), req);
			}
		};
		
		final TextField editServerTextField = new TextField(page, "editServerTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.serverURL().getValue(), req);
			}
		};
		final TextField editPortTextField = new TextField(page, "editPortTextField_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(String.valueOf(config.port().getValue()),req);
			}
		};
		
		final Popup editEmailUserPopup = new Popup(page, "editEmailUserPopup_" + id , true);
		editEmailUserPopup.setTitle("Edit User ", null);
		row.addCell("editEmailPopupColumn", editEmailUserPopup);
		
		final Button editEmailUserButton = new Button(page, "editEmailUserButton" + id);
		editEmailUserButton.triggerAction(editEmailTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editPwTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editServerTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editPortTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editEmailUserButton.triggerAction(editEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editEmailUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editEmailUserButton.setDefaultText("Edit");
		row.addCell("editEmailColumn", editEmailUserButton);
		
		final ButtonConfirm confirmEmailChangesButton = new ButtonConfirm(page, "confirmEmailChangesButton_" + id){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				
				String emailRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String serverRegex = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";
				int port = 70000;
				
				if (editPortTextField.getValue(req).matches(portRegex)) {
					port = Integer.parseInt(editPortTextField.getValue(req));
				}
				
				if((port <= 65535) && editEmailTextField.getValue(req).matches(emailRegex) && editServerTextField.getValue(req).matches(serverRegex) 
						&& !editPwTextField.getValue(req).isEmpty()){
					config.email().setValue(editEmailTextField.getValue(req));
					config.password().setValue(editPwTextField.getValue(req));
					config.serverURL().setValue(editServerTextField.getValue(req));
					config.port().setValue(port);
					alert.showAlert("Changes on User '" + id + "' confirmed", true, req);
				} else {
					if(!editEmailTextField.getValue(req).matches(emailRegex))
						alert.showAlert("Invalid E-Mail-Address", false, req);
					if(!editServerTextField.getValue(req).matches(serverRegex))
						alert.showAlert("Invalid Server-URL", false, req);
					if((port > 65535)) 
						alert.showAlert("Invalid Port", false, req);
					if(editPwTextField.getValue(req).isEmpty()) 
						alert.showAlert("No Password entered", false, req);
				}
				
			}
		};
		confirmEmailChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		confirmEmailChangesButton.setDefaultText("Save Changes");
		confirmEmailChangesButton.setDefaultConfirmPopupTitle("Edit " + id);
		confirmEmailChangesButton.setDefaultConfirmMsg("Accept changes ?");
		confirmEmailChangesButton.triggerAction(emailLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(pwLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(portLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(serverLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmEmailChangesButton.triggerAction(editEmailUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		confirmEmailChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		final StaticTable editEmailUserTable = new StaticTable(5, 2);
		editEmailUserTable.setContent(0, 0, editEmailNameLabel);
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
	
		
//DELETE
		final ButtonConfirm deleteEmailUserButton = new ButtonConfirm(page, "deleteEmailUserButton_" + id){
			
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				table.removeRow(id, req);
				alert.showAlert("User " + id + " successfully deleted", true, req);
			}
			
		};
		deleteEmailUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteEmailUserButton.setDefaultText("Delete");
		deleteEmailUserButton.setDefaultConfirmPopupTitle("Delete Email-User : " + id);
		deleteEmailUserButton.setDefaultConfirmMsg("Are you sure deleting " + id + " from your list ?");
		deleteEmailUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteEmailUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteEmailColumn", deleteEmailUserButton);
		
		return row;
	}

}