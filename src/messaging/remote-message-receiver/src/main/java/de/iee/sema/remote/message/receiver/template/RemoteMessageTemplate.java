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

import org.ogema.tools.resource.util.ResourceUtils;

import de.iee.sema.remote.message.receiver.model.RemoteMessage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.label.Label;

public class RemoteMessageTemplate extends RowTemplate<RemoteMessage> {
	
	private static final String SENDER_COL = "sender";
	private static final String SUBJECT_COL = "subject";
	private static final String PRIO_COL = "prio";
	private static final String BODY_COL = "body";

	private final WidgetPage<?> page;
	
	
	public RemoteMessageTemplate(final WidgetPage<?> page, DynamicTable<RemoteMessage> messageTable) {
		this.page = page;
		messageTable.setColumnSize(SENDER_COL, 1, null);
//		messageTable.setColumnSize(SUBJECT_COL, 1, null);
//		messageTable.setColumnSize(PRIO_COL, 1, null);
//		messageTable.setColumnSize(BODY_COL, 3, null);
	}
	
	@Override
	public String getLineId(final RemoteMessage remoteMessage) {
		return ResourceUtils.getValidResourceName(remoteMessage.getName());
	}
	
	@Override
	public Map<String, Object> getHeader() {
		final Map<String,Object> header = new LinkedHashMap<>();
		
		// keys must be chosen in agreement with cells added in addRow method below
		header.put(SENDER_COL, "Sender");
		header.put(SUBJECT_COL, "Subject");
		header.put(PRIO_COL, "Prio");
		header.put(BODY_COL, "Body");
		
		return header;
	}
	
	@Override
	public Row addRow(final RemoteMessage remoteMessage, final OgemaHttpRequest req) {
		final Row row = new Row();

		final String lineId = getLineId(remoteMessage);
		
		final Label sender = new Label(page, "sender_" + lineId, remoteMessage.sender().getValue());
		row.addCell(SENDER_COL, sender);
		
		final Label subject = new Label(page, "subject_" + lineId, remoteMessage.subject().getValue());
		row.addCell(SUBJECT_COL, subject);
		
		final Label prio = new Label(page, "prio_" + lineId, remoteMessage.priority().getValue());
		row.addCell(PRIO_COL, prio);
		
		final Label body = new Label(page, "body_" + lineId, remoteMessage.body().getValue());
		row.addCell(BODY_COL, body);
		
		return row;
	}

}