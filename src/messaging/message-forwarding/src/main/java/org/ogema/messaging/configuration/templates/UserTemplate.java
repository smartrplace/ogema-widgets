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

package org.ogema.messaging.configuration.templates;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.model.MessagingApp;

public class UserTemplate extends RowTemplate<String> {

	protected final WidgetPage<?> page;
	
	private final String snippetId;
	protected final DynamicTable<String> userTable;
	
	private final MessagingApp app;
	private final MessageReader messageReader;
	
	public UserTemplate(DynamicTable<String> userTable,WidgetPage<?> page, String snippetId,
			MessagingApp app, MessageReader messageReader) {

		this.page = page;
		this.app = app;
		this.messageReader = messageReader;
		this.snippetId = snippetId;
		this.userTable = userTable;
	}
	
	@Override
	public String getLineId(String object) {
		return ResourceUtils.getValidResourceName(object);
	}

	@Override
	public Map<String, Object> getHeader() {
		Map<String, Object> userHeader = new LinkedHashMap<String, Object>();
		userHeader.put("userNameColumn", "Name :");
		
		Set<String> serviceIds = messageReader.getMessageListeners().keySet();
		for (String serviceId : serviceIds) {
			userHeader.put(ResourceUtils.getValidResourceName(serviceId) + "Column", serviceId);
		}
		return userHeader;
	}
	
	@Override
	public Row addRow(final String userName, OgemaHttpRequest req) {
		Row row = new Row();
		row.addCell("userNameColumn", userName);
		for (Map.Entry<String, MessageListener> entry: messageReader.getMessageListeners().entrySet()) {
			try {
				final String serviceId = entry.getKey();
				final String validServiceId = ResourceUtils.getValidResourceName(serviceId);
				
				boolean knownUser = entry.getValue().getKnownUsers().contains(userName);
				
				if (!knownUser) {
					row.addCell(validServiceId + "Column", "");
					continue;
				}
				Dropdown priority = new PriorityDropdown(page, userName, serviceId, snippetId, app);
				row.addCell(validServiceId + "Column", priority);
			} catch (Throwable e) {
				LoggerFactory.getLogger(getClass()).error("",e);
			}
		}
		
		return row;
	}
	
}