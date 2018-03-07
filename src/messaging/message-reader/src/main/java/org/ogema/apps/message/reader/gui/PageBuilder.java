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

package org.ogema.apps.message.reader.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.apps.message.reader.dictionary.MessagesDictionary;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.WidgetPageImpl;
import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
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
	private final WidgetPageImpl<MessagesDictionary> page;
	private final MessageReader mr;
	private final Header header;
	private final DataTable dataTable;
	private final MailPopup popup;

	public PageBuilder(final WidgetPageImpl<MessagesDictionary> page, final MessageReader mr) {
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
		page.append(dataTable);
		page.append(popup);
		dataTable.triggerAction(popup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		dataTable.setDefaultPollingInterval(5000L); // TODO only makes sense once ordering and paging options are kept through updates
	}

	class MessageTable extends DataTable {

		private static final long serialVersionUID = 1L;

		public MessageTable(WidgetPageBase<?> page, String id) {
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
