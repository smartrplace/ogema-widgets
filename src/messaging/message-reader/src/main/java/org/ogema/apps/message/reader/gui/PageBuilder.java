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
package org.ogema.apps.message.reader.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.ogema.apps.message.reader.dictionary.MessagesDictionary;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.datatable.DataTable;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.messaging.MessageReader;

public class PageBuilder {

	private static final int MAX_MSG_LENGTH = 60;
	private static final int MAX_TITLE_LENGTH = 50;
	private final WidgetPage<MessagesDictionary> page;
	private final MessageReader mr;
	private final Header header;
	private final DataTable dataTable;
	private final MailPopup popup;

	public PageBuilder(final WidgetPage<MessagesDictionary> page, final MessageReader mr) {
		this.page = page;
		this.mr = mr;
		this.header = new Header(page, "mainPageHeader") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				MessagesDictionary dict = (MessagesDictionary) page.getDictionary(req); // FIXME why is cast necessary?
				setText(dict.getTitle(),req);
			}
		};
		header.addDefaultStyle(HeaderData.CENTERED);;
		page.append(header);
		this.popup = new MailPopup(page, "mailPopup");
		popup.initialize();
		this.dataTable = new MessageTable(page, "messagesTable") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String currentId = getSelectedRow(req);
				ReceivedMessage currentMessage = null;
				if (currentId != null) {
					currentMessage = mr.getMessage(Long.parseLong(currentId));
				}
				popup.setCurrentMessage(currentMessage, req);
			}
		};
		// order entries by descending age -> newest first
		dataTable.sortDefault(0, false);
		page.append(dataTable);
		page.append(popup);
		dataTable.triggerAction(popup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		dataTable.setDefaultPollingInterval(5000L); // TODO only makes sense once ordering and paging options are kept through updates
	}

	class MessageTable extends DataTable {

		private static final long serialVersionUID = 1L;

		public MessageTable(WidgetPage<?> page, String id) {
			super(page, id);
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			OgemaLocale locale = req.getLocale();
			clear(req);
			Map<Long, ReceivedMessage> messages = mr.getMessages(0); // TODO add configurable start time and message status
			Map<String,Map<String,String>> rows = getMessagesMap(messages, locale); // TODO set link, sort by status, etc
			addRows(rows, req);
			MessagesDictionary dict = (MessagesDictionary) page.getDictionary(req);
			Map<String, String> columns = getColumnTitles(dict, locale);
			setColumnTitles(columns , req);
		}

	}

	private Map<String,Map<String,String>> getMessagesMap(Map<Long,ReceivedMessage> originalMessages, OgemaLocale locale) {
		Map<String,Map<String,String>> result = new LinkedHashMap<String, Map<String,String>>();
		Iterator<Map.Entry<Long,ReceivedMessage>> it = originalMessages.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long,ReceivedMessage> entry = it.next();
			long time = entry.getKey();
			ReceivedMessage msg = entry.getValue();
			Message omsg = msg.getOriginalMessage();
			Map<String,String> columns = new LinkedHashMap<String, String>();
			columns.put("time", getTimeString(msg.getTimestamp()));
			columns.put("title",cutMsg(omsg.title(locale),MAX_TITLE_LENGTH));
			columns.put("msg", cutMsg(omsg.message(locale),MAX_MSG_LENGTH));
			columns.put("prio",omsg.priority().name());
			columns.put("app", msg.getAppId().getIDString()); // TODO adapt
			columns.put("status",msg.getStatus().name()); // TODO use dict?
			result.put(String.valueOf(time), columns);
		}
		return result;
	}

	private String cutMsg(String msg, int length) {
		if (msg == null ||msg.length() < length) return msg;
		return msg.substring(0, length);
	}

	private Map<String, String> getColumnTitles(MessagesDictionary dict, OgemaLocale locale) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("time", dict.getColTitleTime());
		map.put("title", dict.getColTitleAbstract());
		map.put("msg", dict.getColTitleFull());
		map.put("prio", dict.getColTitlePrio());
		map.put("app", dict.getColTitleApp());
		map.put("status", dict.getColTitleStatuts());
		return map;
	}

	static String getTimeString(long tm) {
		final Date date = new Date(tm);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

}
