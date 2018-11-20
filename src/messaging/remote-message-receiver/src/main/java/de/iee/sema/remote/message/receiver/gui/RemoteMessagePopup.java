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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iee.sema.remote.message.receiver.model.RemoteMessage;
import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.popup.PopupData;

public class RemoteMessagePopup extends Popup {

	private static final long serialVersionUID = 1L;
	private final PageSnippet bodyContainer;
	private final Label timestampLabel;
	private final Label senderGWLabel;
	private final Label senderAppLabel;
	private final Label subjectLabel;
	private final Label bodyLabel;
	private final Label prioLabel;
	private final WidgetGroup widgets;
	
	public RemoteMessagePopup(WidgetPage<?> page, String id) {
		super(page, id, true);
		
		this.setTitle("Detailed Message Information", null);
		
		this.timestampLabel = new MessageLabel(page, "timestampLabel", (message) -> message.timestamp());
		this.senderGWLabel = new Label(page, "senderGWLabel") {
		
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				RemoteMessage rm = getCurrentMessage(req);
				setText(rm == null ? "" : rm.getParent().getParent().getName(), req);
			}
		};
		this.senderAppLabel = new MessageLabel(page, "senderAppLabel", (message) -> message.sender());
		this.subjectLabel = new MessageLabel(page, "subjectLabel", (message) -> message.subject());
		this.bodyLabel = new MessageLabel(page, "bodyLabel", (message) -> message.body());
		this.prioLabel = new MessageLabel(page, "prioLabel", (message) -> message.priority());
		
		this.bodyContainer = new PageSnippet(page, "bodyContainer",true);
		
		List<OgemaWidgetBase<?>> widgetsList = new LinkedList<OgemaWidgetBase<?>>();
		widgetsList.add(timestampLabel);
		widgetsList.add(senderGWLabel);
		widgetsList.add(senderAppLabel);
		widgetsList.add(subjectLabel);
		widgetsList.add(bodyLabel);
		widgetsList.add(prioLabel);
		this.widgets = page.registerWidgetGroup("popupWidgets", (Collection) widgetsList);
		setWidgets();
	}
	
	@SuppressWarnings("serial")
	private class MessageLabel extends Label {
		
		private final Function<RemoteMessage, SingleValueResource> valueProvider;
		
		public MessageLabel(WidgetPage<?> page, String id, Function<RemoteMessage, SingleValueResource> valueProvider) {
			super(page, id);
			this.valueProvider = valueProvider;
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			final RemoteMessage msg = getCurrentMessage(req);
			this.setText(msg == null ? "" : convert(valueProvider.apply(msg)), req);
		}
		
		private String convert(SingleValueResource value) {
			if ("priority".equals(value.getName())) {
				final int prio = ((IntegerResource) value).getValue();
				return Arrays.stream(MessagePriority.values())
					.filter(pr -> pr.getPriority() == prio)
					.findAny().orElse(MessagePriority.LOW).name();
			}
			if(value instanceof TimeResource) {
				return MessageOverviewPage.getTimeString(((TimeResource) value).getValue());
			} else 
				return ValueResourceUtils.getValue(value);
		}
		
	}
	
	public class PopupOptions extends PopupData {

		private RemoteMessage currentMessage = null;

		public PopupOptions(RemoteMessagePopup widget) {
			super(widget);
		}

		public RemoteMessage getCurrentMessage() {
			return currentMessage;
		}

		public void setCurrentMessage(RemoteMessage currentMessage) {
			this.currentMessage = currentMessage;
		}

	}
	
	@Override
	public PopupOptions createNewSession() {
		return new PopupOptions(this);
	}
	
	@Override
	public PopupOptions getData(OgemaHttpRequest req) {
		return (PopupOptions) super.getData(req);
	}
	
	public RemoteMessage getCurrentMessage(OgemaHttpRequest req) {
		return getData(req).getCurrentMessage();
	}

	public void setCurrentMessage(RemoteMessage currentMessage, OgemaHttpRequest req) {
		getData(req).setCurrentMessage(currentMessage);
	}
	
	void initialize() {
		this.setBody(bodyContainer, null);
		this.triggerAction(widgets, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(this, TriggeringAction.GET_REQUEST, TriggeredAction.SHOW_WIDGET); 
	}
	
	private void setWidgets() {
		StaticTable table = new StaticTable(6, 2);
		table.setContent(0, 0, MessageOverviewPage.TIMESTAMP_HEADLINE);
		table.setContent(1, 0, MessageOverviewPage.SENDER_GW_HEADLINE);
		table.setContent(2, 0, MessageOverviewPage.SENDER_APP_HEADLINE);
		table.setContent(3, 0, MessageOverviewPage.SUBJECT_HEADLINE);
		table.setContent(4, 0, MessageOverviewPage.BODY_HEADLINE);
		table.setContent(5, 0, MessageOverviewPage.PRIO_HEADLINE);
		
		table.setContent(0, 1, timestampLabel);
		table.setContent(1, 1, senderGWLabel);
		table.setContent(2, 1, senderAppLabel);
		table.setContent(3, 1, subjectLabel);
		table.setContent(4, 1, bodyLabel);
		table.setContent(5, 1, prioLabel);
		bodyContainer.append(table, null);
	}
	
}
