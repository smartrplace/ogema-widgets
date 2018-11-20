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
package org.ogema.apps.messaging.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.textarea.TextArea;

public class PageBuilder {

//	private final ApplicationManager am;
//	private final MessagingService service;
	private final Header header;
	private final Alert alert;
	private final Label subjectLabel;
	private final Label bodyLabel;
	private final Label prioLabel;
	private final TextField subject;
	private final TextArea body;
	private final Dropdown prio;
	private final Button send;
	private final WidgetGroup dataGroup;
	
	public PageBuilder(WidgetPage<?> page, final MessagingService service, final ApplicationManager am) {
//		this.service = service;
//		this.am = am;
		this.header = new Header(page, "header","Messaging Test");
		header.addDefaultStyle(HeaderData.CENTERED);
		page.append(header).linebreak();
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		page.append(alert).linebreak();
		
		this.subjectLabel = new Label(page, "subjectLabel","Subject");
		this.bodyLabel = new Label(page, "bodyLabel","Body");
		this.prioLabel = new Label(page, "prioLabel","Priority");
		
		this.subject = new TextField(page, "subject");
		this.body = new TextArea(page, "body");
		this.prio = new Dropdown(page, "prio");
		List<DropdownOption> opts = new ArrayList<DropdownOption>();
		for (MessagePriority pr : MessagePriority.values()) {
			opts.add(getOpt(pr.name()));
		}
		opts.get(0).select(true);
		prio.setDefaultOptions(opts);
		Comparator<DropdownOption> cmp = new Comparator<DropdownOption>() {

			@Override
			public int compare(DropdownOption o1, DropdownOption o2) {
				String a = o1.id();
				String b = o2.id();
				if (a.equals(b))
					return 0;
				MessagePriority prio1 = MessagePriority.valueOf(a);
				MessagePriority prio2 = MessagePriority.valueOf(b);
				return prio1.compareTo(prio2);
			}
		};
		prio.setComparator(cmp);
		send = new Button(page, "send", "Send") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String subj = subject.getValue(req);
				String bod = body.getText(req);
				MessagePriority pr = MessagePriority.LOW;
				try {
					pr = MessagePriority.valueOf(prio.getSelectedValue(req));
				} catch (Exception e) {}
				Message msg = new MessageImpl(subj, bod, pr);
				try {
					service.sendMessage(am, msg);
					alert.showAlert("Message sent", true, req);
				} catch (RejectedExecutionException e) {
					alert.showAlert("Message could not be sent: "+ e, false, req);
				}
			}
			
		};
		List<OgemaWidget> datawidgets = new ArrayList<OgemaWidget>();
		datawidgets.add(subject);datawidgets.add(body);datawidgets.add(prio);
		dataGroup = page.registerWidgetGroup("dataGroup", datawidgets);
		dataGroup.setDefaultSendValueOnChange(false);
		
		StaticTable table = new StaticTable(4, 2, new int[]{2,2});
		table.setContent(0, 0, subjectLabel).setContent(0, 1, subject)
			.setContent(1, 0, bodyLabel).setContent(1, 1, body)
			.setContent(2, 0, prioLabel).setContent(2, 1, prio)
										.setContent(3, 1, send);
		page.append(table);
		
		setDependencies();
	}
	
	private void setDependencies() {
		send.triggerAction(dataGroup, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST);
		send.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	private static DropdownOption getOpt(String value) {
		return new DropdownOption(value, value, false);
	}
	
}
