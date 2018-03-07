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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetPageImpl;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.popup.PopupData;
import de.iwes.widgets.html.textarea.TextArea;

public class MailPopup extends Popup {

	private static final long serialVersionUID = 1L;
	private final PageSnippet bodyContainer;
	private final Label receiveTimeLabel;
	private final Label receiveTime;
	private final Label titleLabel;
	private final TextArea messageText;
	private final Button deleteButton;
	private final WidgetGroup widgets;

	public MailPopup(WidgetPageImpl<?> page, String id) {
		super(page, id, true);
		this.titleLabel = new Label(page, "mod_titleLabel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				OgemaLocale locale = req.getLocale();
				ReceivedMessage message = getCurrentMessage(req);
				if (message == null) return;
				String title =  message.getOriginalMessage().title(locale);
				setText(title, req);
			}
		};
		this.receiveTime = new Label(page, "mod_receiveTime", "Receival time") { 

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				ReceivedMessage message = getCurrentMessage(req);
				if (message == null) return;
				long time = message.getTimestamp();
				String timeStr = PageBuilder.getTimeString(time);
				setText(timeStr, req);
			}
		};
		this.receiveTimeLabel = new Label(page, "mod_receiveTimeLabel","Received"); // TODO dictionary
		this.messageText = new TextArea(page, "mod_messageText") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				OgemaLocale locale = req.getLocale();
				ReceivedMessage message = getCurrentMessage(req);
				if (message == null) return;
				String msg = message.getOriginalMessage().message(locale);
				setText(msg, req);
			}
		};
		this.deleteButton = new Button(page, "mod_deleteButton","Delete Message");// TODO dictionary
		this.bodyContainer = new PageSnippet(page, "mod_bodyContainer",true);
		List<OgemaWidgetBase<?>> widgetsList = new LinkedList<OgemaWidgetBase<?>>();
		widgetsList.add(titleLabel);widgetsList.add(receiveTimeLabel);widgetsList.add(receiveTime);widgetsList.add(messageText);widgetsList.add(deleteButton);		
		this.widgets = page.registerWidgetGroup("popupWidgets", (Collection) widgetsList); // FIXME does this work?
		setWidgets();
	}
	
	public class MailPopupOptions extends PopupData {

		private ReceivedMessage currentMessage = null;

		public MailPopupOptions(MailPopup widget) {
			super(widget);
		}

		public ReceivedMessage getCurrentMessage() {
			return currentMessage;
		}

		public void setCurrentMessage(ReceivedMessage currentMessage) {
			this.currentMessage = currentMessage;
		}

	}
	
	@Override
	public MailPopupOptions createNewSession() {
		return new MailPopupOptions(this);
	}
	
	@Override
	public MailPopupOptions getData(OgemaHttpRequest req) {
		return (MailPopupOptions) super.getData(req);
	}
	
	public ReceivedMessage getCurrentMessage(OgemaHttpRequest req) {
		return getData(req).getCurrentMessage();
	}

	public void setCurrentMessage(ReceivedMessage currentMessage,OgemaHttpRequest req) {
		getData(req).setCurrentMessage(currentMessage);
	}
	
	void initialize() {
		this.setBody(bodyContainer, null);
		this.triggerAction(widgets, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(this, TriggeringAction.GET_REQUEST, TriggeredAction.SHOW_WIDGET); 
	}
	
	private void setWidgets() {
		bodyContainer.append(receiveTimeLabel,null);
		bodyContainer.append(receiveTime, null).linebreak(null);
		bodyContainer.append(titleLabel, null).linebreak(null).linebreak(null);
		bodyContainer.append(messageText, null).linebreak(null);
		bodyContainer.append(deleteButton, null);
	}
	

	
	
	
}
