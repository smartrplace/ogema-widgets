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
package de.iee.sema.remote.message.receiver.template;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iee.sema.remote.message.receiver.model.ClientData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Label;

public class ClientDataTemplate extends RowTemplate<ClientData> {
	
	private static final String NAME_COL = "name";
	private static final String DELETE_COL = "delete";
	private static final String POPUP_COL = "popup";

	private final ApplicationManager appMan;
	private final WidgetPage<?> page;
	private final Alert alert;
	private final DynamicTable<ClientData> clientTable;
	
	
	public ClientDataTemplate(final ApplicationManager appMan, final WidgetPage<?> page, final Alert alert, final DynamicTable<ClientData> clientTable) {
		this.appMan = appMan;
		this.page = page;
		this.alert = alert;
		this.clientTable = clientTable;
	}
	
	@Override
	public String getLineId(final ClientData clientData) {
		return ResourceUtils.getValidResourceName(clientData.getName());
	}
	
	@Override
	public Map<String, Object> getHeader() {
		final Map<String,Object> header = new LinkedHashMap<>();
		
		// keys must be chosen in agreement with cells added in addRow method below
		header.put(NAME_COL, "Client name");
		header.put(DELETE_COL, "");
		header.put(POPUP_COL, "");
		
		return header;
	}
	
	@Override
	public Row addRow(final ClientData clientData, final OgemaHttpRequest req) {
		final Row row = new Row();

		final String lineId = getLineId(clientData);
		final String clientName = clientData.userName().getValue();
		
		final Label name = new Label(page, "clientDataName_" + lineId, clientData.userName().getValue());
		row.addCell(NAME_COL, name);
		
		final ButtonConfirm deleteButton = new ButtonConfirm(page, "deleteButton_" + lineId, "Delete") {
			
			private static final long serialVersionUID = 1L;

			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				clientData.delete();
				appMan.getAdministrationManager().removeUserAccount(clientName);
				alert.showAlert("User '" + clientName + "' successfully deleted", true, req);
			}
			
		};
		deleteButton.addDefaultStyle(ButtonData.BOOTSTRAP_RED);
		deleteButton.setDefaultConfirmPopupTitle("Delete client data: " + clientName);
		deleteButton.setDefaultConfirmMsg("Do you really want to delete '" + clientName + "'?");
		deleteButton.triggerAction(clientTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell(DELETE_COL, deleteButton);
		
		return row;
	}

}