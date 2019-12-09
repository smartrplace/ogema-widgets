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
package de.iee.sema.remote.message.receiver.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.osgi.service.component.annotations.Component;

import de.iee.sema.remote.message.receiver.model.ClientData;
import de.iee.sema.remote.message.receiver.model.RemoteMessage;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.datatable.DataTable;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.multiselect.extended.MultiSelectExtended;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=/de/iwes/ogema/apps/remotemessagereceiver", 
				LazyWidgetPage.RELATIVE_URL + "=messageoverview.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=Message Overview"
		}
)
// FIXME retrieve messages in GET method, do not store globally
public class MessageOverviewPage implements LazyWidgetPage {
	
	static final String TIMESTAMP = "timestamp";
	static final String SENDER_GW = "senderGW";
	static final String SENDER_APP = "senderApp";
	static final String SUBJECT = "subject";
	static final String BODY = "body";
	static final String PRIO = "priority";
	static final String TIMESTAMP_HEADLINE = "Timestamp";
	static final String SENDER_GW_HEADLINE = "Sender Gateway";
	static final String SENDER_APP_HEADLINE = "Sender App";
	static final String SUBJECT_HEADLINE = "Subject";
	static final String BODY_HEADLINE = "Message Body";
	static final String PRIO_HEADLINE = "Priority";
	
	private ApplicationManager appMan;
	private HashMap<Long, RemoteMessage> messages = new HashMap<Long, RemoteMessage>();
	private HashMap<Long, RemoteMessage> filteredMessages = new HashMap<Long, RemoteMessage>();
	private ArrayList<String> allGws = new ArrayList<>();
	
	private long startTime;
	private long endTime;
	
	@Override
	public void init(final ApplicationManager appMan, final WidgetPage<?> page) {
		
		this.appMan = appMan;
		
		getAllRemoteMessages();
		getStartEndTimestamps();
		
		// Get all GWs for MultiSelect
		ResourceList<ClientData> clientDatas = appMan.getResourceAccess().getResource("clientData");
		if(clientDatas != null)
			clientDatas.getAllElements().forEach(element -> allGws.add(element.userName().getValue()));
		
		final Header header = new Header(page, "header", "Remote Receiver App - Messages");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		
		final RemoteMessagePopup popup = new RemoteMessagePopup(page, "msgPopup");
		popup.initialize();
		
		final DataTable table = new MessageTable(page, "messagesTable") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String currentId = getSelectedRow(req);
				RemoteMessage currentMessage = null;
				if (currentId != null) {
					currentMessage = messages.get(Long.parseLong(currentId));
				}
				popup.setCurrentMessage(currentMessage, req);
			}
		};
		table.triggerAction(popup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		//gateway multi-selection
		final TemplateMultiselect<String> gwMultiSelect = new TemplateMultiselect<String>(page, "gwMultiSelect") {

			private static final long serialVersionUID = 1L;
					
			@Override
			public void onGET(OgemaHttpRequest req) {
				allGws.forEach(gw -> this.addItem(gw, req));
			}
		};
		gwMultiSelect.setDefaultWidth("100%");
		
		gwMultiSelect.selectDefaultItems(/*Select all*/null);
		
		final MultiSelectExtended<String> gwSelection = 
				new MultiSelectExtended<String>(page, "gwSelection", gwMultiSelect, true, "", true, false);
		
		// For filtering the messages
		final Datepicker startDatePicker = new Datepicker(page, "startDatePicker");
		startDatePicker.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		final Datepicker endDatePicker = new Datepicker(page, "endDatePicker");
		endDatePicker.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		// build page
		//final PageSnippet filterSnippet = new PageSnippet(page, "filterSnippet");
		//filterSnippet.append(startDatePicker).append(endDatePicker, null);
		
		page.append(header).linebreak().append(gwSelection).append(startDatePicker).append(endDatePicker).append(table).linebreak().append(popup);
	}
	
	private void getStartEndTimestamps() {
		startTime = messages.keySet().stream().min(Long::compare).get();
		endTime = messages.keySet().stream().max(Long::compare).get();
	}

	private void getAllRemoteMessages() {
		
		for(RemoteMessage remoteMessage : appMan.getResourceAccess().getResources(RemoteMessage.class)) {
			messages.put(remoteMessage.timestamp().getValue(), remoteMessage);
		}
		
	}
	
	private HashMap<Long, RemoteMessage> filterMessages() {
		
		
		return filteredMessages;
	}

	class MessageTable extends DataTable {

		private static final long serialVersionUID = 1L;

		public MessageTable(WidgetPage<?> page, String id) {
			super(page, id);
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			clear(req);
//			
//			for(RemoteMessage remoteMessage : appMan.getResourceAccess().getResources(RemoteMessage.class)) {
//				messages.put(remoteMessage.timestamp().getValue(), remoteMessage);
//			}
//			
			//private HashMap<Long, RemoteMessage> filteredMessages = filterMessages(messages);
			
			Map<String, Map<String, String>> rows = getMessagesMap(messages);
			addRows(rows, req);
			Map<String, String> columns = getColumnTitles();
			setColumnTitles(columns , req);
		}

	}
	
	// Returns a map: Key: Timestamp of the messages, Value: Map with message Information
	private Map<String, Map<String, String>> getMessagesMap(Map<Long, RemoteMessage> originalMessages) {
		Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
		if(originalMessages == null)
			return result;
		
		Iterator<Map.Entry<Long, RemoteMessage>> it = originalMessages.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long, RemoteMessage> entry = it.next();
			long time = entry.getKey();
			RemoteMessage msg = entry.getValue();
			
			// For the resource structure in sema fieldtest, the parent of the parent from the RemoteMessage
			// contains information about the gateway
			Resource parentParent = msg.getParent() != null ? msg.getParent().getParent() : null;
			String gw = parentParent != null ? parentParent.getName() : "Unknown";
			Map<String, String> columns = new LinkedHashMap<String, String>();
			columns.put(TIMESTAMP, getTimeString(time));
			columns.put(SENDER_GW, gw);
			columns.put(SENDER_APP, msg.sender().getValue());
			columns.put(SUBJECT, msg.subject().getValue());
			columns.put(PRIO, String.valueOf(msg.priority().getValue()));

			result.put(String.valueOf(time), columns);
		}
		return result;
	}
	
	private Map<String, String> getColumnTitles() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(TIMESTAMP, "Timestamp");
		map.put(SENDER_GW, "Sender Gateway");
		map.put(SENDER_APP, "Sender App");
		map.put(SUBJECT, "Subject");
		map.put(PRIO, "Priority");
		
		return map;
	}
	
	static String getTimeString(long tm) {
		final Date date = new Date(tm);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
}
