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
	
	public SmsTemplate(ResourceList<SmsConfiguration> list, ApplicationManager am , DynamicTable<SmsConfiguration> table, Alert alert, WidgetPage<?> page) {
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
		smsHeader.put("smsNameColumn", "Name :");
		smsHeader.put("smsEmailColumn", "E-Mail-Adress :");
		smsHeader.put("smsEmailPasswordColumn", "Password :");
		smsHeader.put("smsEmailServerColumn", "Server-URL :");
		smsHeader.put("smsEmailPortColumn", "Port :");
		smsHeader.put("editSmsPopupColumn", "");
		smsHeader.put("editSmsColumn", "");
		smsHeader.put("deleteSmsColumn", "");
		return smsHeader;
		
	}
	
	@Override
	public String getLineId(SmsConfiguration object) {
		return ResourceUtils.getValidResourceName(object.userName().getValue());
	}
	
	@Override
	public Row addRow(final SmsConfiguration config, OgemaHttpRequest req) {
		Row row = new Row();
		
		
//NEW
		final String id = getLineId(config);
		
		final Label smsNameLabel = new Label(page, "smsNameLabel_" + id, true){
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.userName().getValue(), req);
			}
		};
		row.addCell("smsNameColumn", smsNameLabel);
		
		final Label smsEmailLabel = new Label(page, "smsEmailLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.smsEmail().getValue(),req);
			}
		};
		row.addCell("smsEmailColumn", smsEmailLabel);
		
		final Label smsEmailPasswordLabel = new Label(page, "smsEmailPasswordLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.smsEmailPassword().getValue(),req);
			}
		};
		row.addCell("smsEmailPasswordColumn", smsEmailPasswordLabel);
		
		final Label smsEmailServerLabel = new Label(page, "smsEmailServerLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(config.smsEmailServer().getValue(),req);
			}
		};
		row.addCell("smsEmailServerColumn", smsEmailServerLabel);
		
		final Label smsEmailPortLabel = new Label(page, "smsEmailPortLabel_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(config.smsEmailPort().getValue()),req);
			}
		};
		row.addCell("smsEmailPortColumn", smsEmailPortLabel);

		
//EDIT
		final Label editSmsNameLabel = new Label(page, "editSmsNameLabel_" + id, true);
		editSmsNameLabel.setDefaultText("Name :");
		final Label editSmsEmailLabel = new Label(page, "editSmsLabel_" + id, true);
		editSmsEmailLabel.setDefaultText("new E-Mail-Address :");
		final Label editSmsEmailPasswordLabel = new Label(page, "editSmsPasswordLabel_" + id, true);
		editSmsEmailPasswordLabel.setDefaultText("new Password :");
		final Label editSmsEmailServerLabel = new Label(page, "editSmsEmailServerLabel_" + id, true);
		editSmsEmailServerLabel.setDefaultText("new Server-URL :");
		final Label editSmsEmailPortLabel = new Label(page, "editSmsEmailPortLabel_" + id, true);
		editSmsEmailPortLabel.setDefaultText("new Port :");
		
		final TextField editSmsEmailTextField = new TextField(page, "smsEmailTextField_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.smsEmail().getValue(),req);
			}
		};
		
		final TextField editSmsEmailPasswordTextField = new TextField(page, "smsEmailPasswordTextField_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.smsEmailPassword().getValue(),req);
			}
		};
		
		final TextField editSmsEmailServerTextField = new TextField(page, "smsEmailServerTextField_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(config.smsEmailServer().getValue(),req);
			}
		};
		
		final TextField editSmsEmailPortTextField = new TextField(page, "smsEmailPortTextField_" + id, true) {
			@Override
			public void onGET(OgemaHttpRequest req) {
				setValue(String.valueOf(config.smsEmailPort().getValue()),req);
			}
		};
		
		row.addCell("emptySmsColumn4", "");
		
		final Popup editSmsUserPopup = new Popup(page, "editSmsUserPopup_" + id , true);
		editSmsUserPopup.setTitle("Edit User", null);
		row.addCell("editSmsPopupColumn", editSmsUserPopup);
		
		final Button editSmsUserButton = new Button(page, "editSmsUserButton" + id);
		editSmsUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editSmsUserButton.triggerAction(editSmsUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		editSmsUserButton.triggerAction(editSmsEmailTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editSmsUserButton.triggerAction(editSmsEmailPasswordTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editSmsUserButton.triggerAction(editSmsEmailServerTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editSmsUserButton.triggerAction(editSmsEmailPortTextField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editSmsUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		editSmsUserButton.setDefaultText("Edit");
		row.addCell("editSmsColumn", editSmsUserButton);
		
		final ButtonConfirm confirmSmsChangesButton = new ButtonConfirm(page, "confirmSmsChangesButton_" + id){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				
				String emailRegex = "[A-Za-z0-9]+[A-Za-z0-9.-]*[@][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String serverRegex = "[A-Za-z0-9.-]+[.][A-Za-z0-9.-]+[.][a-zA-Z_0-9]+$";
				String portRegex = "[0-9]{1,5}$";
				int port = 70000;
				
				if (editSmsEmailPortTextField.getValue(req).matches(portRegex)) {
					port = Integer.parseInt(editSmsEmailPortTextField.getValue(req));
				}
				
				if((port <= 65535) && editSmsEmailTextField.getValue(req).matches(emailRegex) && editSmsEmailServerTextField.getValue(req).matches(serverRegex) 
						&& !editSmsEmailPasswordTextField.getValue(req).isEmpty()){
					config.smsEmail().setValue(editSmsEmailTextField.getValue(req));
					config.smsEmailPassword().setValue(editSmsEmailPasswordTextField.getValue(req));
					config.smsEmailServer().setValue(editSmsEmailServerTextField.getValue(req));
					config.smsEmailPort().setValue(port);
					alert.showAlert("Changes on User '" + id + "' confirmed", true, req);
				} else {
					if(port > 65535) 
						alert.showAlert("Invalid Port", false, req);
					if(!editSmsEmailTextField.getValue(req).matches(emailRegex))
						alert.showAlert("Invalid Sms-E-mail-Address", false, req);
					if(!editSmsEmailServerTextField.getValue(req).matches(serverRegex))
						alert.showAlert("Invalid Server-URL", false, req);
					if(editSmsEmailPasswordTextField.getValue(req).isEmpty())
						alert.showAlert("No Password entered", false, req);
				}
			}
		};
		confirmSmsChangesButton.triggerAction(smsEmailLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(editSmsUserPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		confirmSmsChangesButton.triggerAction(smsEmailLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(smsEmailPasswordLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(smsEmailServerLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(smsEmailPortLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		confirmSmsChangesButton.setDefaultText("Save Changes");
		confirmSmsChangesButton.setDefaultConfirmPopupTitle("Edit " + id);
		confirmSmsChangesButton.setDefaultConfirmMsg("Accept changes ?");
		confirmSmsChangesButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);
		
		final StaticTable editSmsUserTable = new StaticTable(5, 2);
		editSmsUserTable.setContent(0, 0, editSmsNameLabel);
		editSmsUserTable.setContent(1, 0, editSmsEmailLabel);
		editSmsUserTable.setContent(2, 0, editSmsEmailPasswordLabel);
		editSmsUserTable.setContent(3, 0, editSmsEmailServerLabel);
		editSmsUserTable.setContent(4, 0, editSmsEmailPortLabel);
		editSmsUserTable.setContent(0, 1, config.userName().getValue());
		editSmsUserTable.setContent(1, 1, editSmsEmailTextField);
		editSmsUserTable.setContent(2, 1, editSmsEmailPasswordTextField);
		editSmsUserTable.setContent(3, 1, editSmsEmailServerTextField);
		editSmsUserTable.setContent(4, 1, editSmsEmailPortTextField);
		
		final PageSnippet editSmsUserSnippet = new PageSnippet(page, "editSmsUserSnippet" + id, true);
		editSmsUserSnippet.append(editSmsUserTable, null);
		editSmsUserSnippet.append(confirmSmsChangesButton, null);
		
		editSmsUserPopup.setBody(editSmsUserSnippet, null);

		
//DELETE
		final ButtonConfirm deleteSmsUserButton = new ButtonConfirm(page, "deleteSmsUserButton_" + id){
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				config.delete();
				alert.showAlert("User '" + id + "' successfully deleted", true, req);
			}
		};
		deleteSmsUserButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteSmsUserButton.setDefaultText("Delete");
		deleteSmsUserButton.setDefaultConfirmPopupTitle("Delete Sms-User : " + id);
		deleteSmsUserButton.setDefaultConfirmMsg("Are you sure deleting " + id + " from this list ?");
		deleteSmsUserButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteSmsUserButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("deleteSmsColumn", deleteSmsUserButton);

		return row;
	}

//	private SmsConfiguration getSmsConfiguration(String user) {
//		for (SmsConfiguration config: list.getAllElements()) {
//			StringResource actUser = config.userName();	
//			//System.out.println("------> Act Sms User : " + actUser.getValue());
//			if (!actUser.isActive())  {
//				continue;
//			}
//			if (actUser.getValue().equals(user)) {
//				return config;
//			}
//		}
//		return null;
//	}

}