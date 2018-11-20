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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iee.sema.remote.message.receiver.model.RemoteMessage;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.datatable.DataTable;
import de.iwes.widgets.html.form.label.Header;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=/de/iwes/ogema/apps/remotemessagereceiver", 
				LazyWidgetPage.RELATIVE_URL + "=messageoverview.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=Message Overview"
		}
)
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
	private DataTable table;
	private RemoteMessagePopup popup;
	private HashMap<Long, RemoteMessage> messages = new HashMap<Long, RemoteMessage>();
	
	@Override
	public void init(final ApplicationManager appMan, final WidgetPage<?> page) {
		
		this.appMan = appMan;
		
		final Header header = new Header(page, "header", "Remote Receiver App - Messages");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		
		this.popup = new RemoteMessagePopup(page, "msgPopup");
		popup.initialize();
		
		this.table = new MessageTable(page, "messagesTable") {

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
		
		// build page
		page.append(header).linebreak().append(table).linebreak().append(popup);
	}
	
	class MessageTable extends DataTable {

		private static final long serialVersionUID = 1L;

		public MessageTable(WidgetPage<?> page, String id) {
			super(page, id);
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			clear(req);
			
			for(RemoteMessage remoteMessage : appMan.getResourceAccess().getResources(RemoteMessage.class)) {
				messages.put(remoteMessage.timestamp().getValue(), remoteMessage);
			}
			Map<String, Map<String, String>> rows = getMessagesMap(messages);
			addRows(rows, req);
			Map<String, String> columns = getColumnTitles();
			setColumnTitles(columns , req);
		}

	}
	
	private Map<String, Map<String, String>> getMessagesMap(Map<Long, RemoteMessage> originalMessages) {
		Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
		if(originalMessages == null)
			return result;
		
		Iterator<Map.Entry<Long, RemoteMessage>> it = originalMessages.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long, RemoteMessage> entry = it.next();
			long time = entry.getKey();
			RemoteMessage msg = entry.getValue();
			Map<String, String> columns = new LinkedHashMap<String, String>();
			columns.put(TIMESTAMP, getTimeString(time));
			columns.put(SENDER_GW, msg.getParent().getParent().getName());
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
