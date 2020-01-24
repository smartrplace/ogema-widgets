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
package org.ogema.widgets.pushover.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.widgets.pushover.model.PushoverConfiguration;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.template.DisplayTemplate;

public class ConfigPage {

	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final TextField newSenderField;
	private final TextField newReceiverField;
	private final Button newSenderTrigger;
	private final Button newReceiverTrigger;
	private final TemplateDropdown<StringResource> senderSelector;
	// TODO list existing and offer possibility to delete
	private final Header sendersHeader;
	private final Header receiversHeader;
	private final PushoverConfigTable senders;
	private final PushoverConfigTable receivers;
	
	public ConfigPage(final WidgetPage<?> page, final PushoverConfiguration config) {
		this.page = page;
		this.header = new Header(page, "header", true);
		header.setDefaultText("Pushover connector configuration");
		header.setDefaultColor("blue");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		
		this.newSenderField = new TextField(page, "newsenderField");
		this.newReceiverField = new TextField(page, "newReceiverFiedl");
		this.newSenderTrigger = new Button(page, "newSenderTrigger", "Create") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String sender = newSenderField.getValue(req);
				if (sender == null || sender.trim().length() < 5) {
					alert.showAlert("Invalid sender id " + sender, false, req);
					return;
				}
				sender = sender.trim();
				if (isStringContained(config.userTokens(), sender)) {
					alert.showAlert("Sender id already exists: " + sender, false, req);
					return;
				}
				final StringResource res = addEntry(config.userTokens(), sender);
				if (config.userTokens().size() == 1) {
					res.activate(false);
				}
			}
		};
		
		this.newReceiverTrigger = new Button(page, "newReceiverTrigger", "Create") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String receiver = newReceiverField.getValue(req);
				if (receiver == null || receiver.trim().length() < 5) {
					alert.showAlert("Invalid receiver id " + receiver, false, req);
					return;
				}
				receiver = receiver.trim();
				if (isStringContained(config.applicationTokens(), receiver)) {
					alert.showAlert("Receiver id already exists: " + receiver, false, req);
					return;
				}
				final StringResource res = addEntry(config.applicationTokens(), receiver);
				res.activate(false);
			}
		};
		this.senderSelector = new TemplateDropdown<StringResource>(page, "senderSelector") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final List<StringResource> senders = config.userTokens().getAllElements();
				update(senders,req);
				for (StringResource s : senders) {
					if (s.isActive()) {
						selectItem(s, req);
						break;
					}
				}
				
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				StringResource str = getSelectedItem(req);
				str.activate(false);
				for (StringResource s : config.userTokens().getAllElements()) {
					if (!s.equalsLocation(str)) 
						s.deactivate(false);
				}
			}
			
		};
		senderSelector.setTemplate(new DisplayTemplate<StringResource>() {
			
			@Override
			public String getLabel(StringResource object, OgemaLocale locale) {
				return object.getValue();
			}
			
			@Override
			public String getId(StringResource object) {
				return object.getValue();
			}
		});
		this.sendersHeader = new Header(page, "sendersHeader", true);
		sendersHeader.setDefaultHeaderType(3);
		sendersHeader.setDefaultText("Senders");
		sendersHeader.setDefaultColor("blue");
		sendersHeader.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		this.senders = new PushoverConfigTable(page, "sendersTable", config.userTokens());
		senders.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		this.receiversHeader = new Header(page, "receiversHeader", true);
		receiversHeader.setDefaultHeaderType(3);
		receiversHeader.setDefaultText("Receivers");
		receiversHeader.setDefaultColor("blue");
		receiversHeader.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		this.receivers = new PushoverConfigTable(page, "receiverTable", config.applicationTokens());
		receivers.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(alert);
		int row = 0;
		StaticTable tab = new StaticTable(3, 3, new int[]{2,2,2})
				.setContent(row, 0, "New receiver (pushover application token)").setContent(row, 1, newReceiverField).setContent(row++, 2, newReceiverTrigger)
				.setContent(row, 0, "New sender (pushover user / group key)").setContent(row, 1, newSenderField).setContent(row++, 2, newSenderTrigger)
				.setContent(row, 0, "Senders").setContent(row++, 1, senderSelector);
		page.append(tab).linebreak();
		row = 0;
		StaticTable existingTables = new StaticTable(3, 2, new int[]{6,6})
				.setContent(row, 0, sendersHeader).setContent(row++, 1, receiversHeader)
				.setContent(row, 0, senders).setContent(row++, 1, receivers);
		page.append(existingTables);
	}
	
	private void setDependencies() {
		newReceiverTrigger.triggerAction(newReceiverField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newReceiverTrigger.triggerAction(receivers, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newSenderTrigger.triggerAction(newSenderField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newReceiverTrigger.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newSenderTrigger.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newSenderTrigger.triggerAction(senderSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newSenderTrigger.triggerAction(senders, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	private static boolean isStringContained(final ResourceList<StringResource> list, final String string) {
		for (StringResource r : list.getAllElements()) {
			if (string.equals(r.getValue()))
				return true;
		}
		return false;
	}
	
	private static StringResource addEntry(final ResourceList<StringResource> list, final String string) {
		final StringResource newR = list.add();
		newR.setValue(string);
		return newR;
	}
	
	private static class DeleteButton extends Button {

		private static final long serialVersionUID = 1L;
		final StringResource target;
		
		public DeleteButton(WidgetPage<?> page, String id, StringResource target) {
			super(page, id, "Delete");
			this.target = target;
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			target.delete();
		}
		
	}
	
	private static class PushoverConfigRowTemplate extends RowTemplate<StringResource> {
		
		final static Map<String,Object> HEADER;
		private final WidgetPage<?> page;
		private final DynamicTable<?> table;
		
		public PushoverConfigRowTemplate(WidgetPage<?> page, DynamicTable<?> table) {
			this.page=  page;
			this.table = table;
		}
		
		static {
			HEADER = new LinkedHashMap<>();
			HEADER.put("id", "Id");
			HEADER.put("delete", "Delete");
		}

		@Override
		public Row addRow(StringResource object, OgemaHttpRequest req) {
			final Row row = new Row();
			row.addCell("id", object.getValue());
			final DeleteButton btn = new DeleteButton(page, "deleteBtn_" + ResourceUtils.getValidResourceName(object.getValue()), object);
			row.addCell("delete",btn);
			btn.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			return row;
		}

		@Override
		public String getLineId(StringResource object) {
			return object.getValue();
		}

		@Override
		public Map<String, Object> getHeader() {
			return HEADER;
		}
		
	}
	
	private static class PushoverConfigTable extends DynamicTable<StringResource> {

                private static final long serialVersionUID = 1L;

		private final ResourceList<StringResource> configurations;
		private final Semaphore sema = new Semaphore(1);
		
		public PushoverConfigTable(WidgetPage<?> page, String id, ResourceList<StringResource> configurations) {
			super(page, id, true);
			this.configurations = configurations;
			setRowTemplate(new PushoverConfigRowTemplate(page, this));
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			if (!sema.tryAcquire())
				return;
			try {
				updateRows(configurations.getAllElements(), req);
			} finally {
				sema.release();
			}
		}
		
	}
	
}
