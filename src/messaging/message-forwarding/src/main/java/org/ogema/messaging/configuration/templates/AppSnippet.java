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
package org.ogema.messaging.configuration.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.messaging.configuration.AllMessagingApps;
import org.ogema.messaging.configuration.SelectConnector;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.model.MessagingApp;
import de.iwes.widgets.messaging.model.MessagingService;
import de.iwes.widgets.messaging.model.UserConfig;
import de.iwes.widgets.resource.widget.textfield.BooleanResourceCheckbox;

public class AppSnippet extends PageSnippet {
	
	private static final long serialVersionUID = 1L;
	
	private final WidgetPage<?> page;
	private final static Logger logger = LoggerFactory.getLogger(SelectConnector.class);
	
	private final DynamicTable<String> userTable;
	private final MessagingApp messagingApp;
	private final MessageReader messageReader;

	public AppSnippet(WidgetPage<?> page, boolean globalPage, MessagingApp messagingApp, MessageReader messageReader, de.iwes.widgets.messaging.MessagingApp ma) {
		super(page, ResourceUtils.getValidResourceName(messagingApp.appId().getValue()), true);
		this.page = page;
		this.messagingApp = messagingApp;
		this.messageReader = messageReader;
		String id = ResourceUtils.getValidResourceName(messagingApp.appId().getValue());
		
		String name = ma.getName();
		if (name == null || name.trim().isEmpty())
			name = "";
		else
			name = name + ": ";
		boolean allApps = ma instanceof AllMessagingApps;
		if (!allApps) {
			Label symbolicName = new Label(page, "symbolicName_" + id, name + ma.getBundleSymbolicName() + ", version " + ma.getVersion());
			this.append(symbolicName,null).linebreak(null);
		}
		String description = ma.getDescription();
		if (description != null && !description.trim().isEmpty()) {
			Label descriptionLabel = new Label(page, "description_" + id, "Description: " + description);
			this.append(descriptionLabel, null).linebreak(null);
		}
		if (!allApps) {
			BooleanResourceCheckbox active = new BooleanResourceCheckbox(page, "activeChkbx_" + id, "active", messagingApp.active());
			this.append(active,null);
		}
		this.userTable = new DynamicTable<String>(page, "userTable_" + id, true);
		createUserTable();
	}
	
	public void updateDropdowns(Map<String,MessageListener> unchangedListeners, Set<String> unchangedUsers) {
		for (Map.Entry<String, MessageListener> entry: unchangedListeners.entrySet()) {
			for (String user: unchangedUsers) {
				try {
					boolean known = entry.getValue().getKnownUsers().contains(user);
					String rowId = ResourceUtils.getValidResourceName(user);
					String colId = ResourceUtils.getValidResourceName(entry.getKey()) + "Column";
					Object content = userTable.getCellContent(rowId, colId, null);
					if (!known && content instanceof Dropdown) {
						Dropdown dd = (Dropdown) content;
						dd.destroyWidget();
						userTable.setCell(rowId, colId, "", null);
					}
					else if (known && !(content instanceof Dropdown)) {
						Dropdown dd = new PriorityDropdown(page, user, entry.getKey(), getId(), messagingApp);
						userTable.setCell(rowId, colId, dd, null);
					}
				} catch (Throwable e) {
					logger.error("Error updating user dropdowns",e);
				}
			}
		}
	}
	
	private void updateListenerResources( Map<String, MessageListener> newListeners, Set<String> listenersToBeRemoved) {
		ResourceList<MessagingService> serviceList = messagingApp.services().create();
		for (String id: listenersToBeRemoved) {
			try {
				String path = ResourceUtils.getValidResourceName(id);
				serviceList.getSubResource(path, MessagingService.class).delete();
			} catch (Exception e) {
				logger.error("Error removing messaging listener resource",e);
			}
		}
		for (String id: newListeners.keySet()) {
			try {
				String path = ResourceUtils.getValidResourceName(id);
				MessagingService serviceRes = serviceList.getSubResource(path, MessagingService.class).create();
				serviceRes.serviceId().<StringResource> create().setValue(id);
				serviceRes.users().create();
			} catch (Exception e) {
				logger.error("Error adding messaging listener resource",e);
			}
		}
	}
	
	// no action required for new users, the resources are only created when the user selects a priority
	private void updateUsersResources(Set<String> usersToBeRemoved) {
		for (MessagingService service: messagingApp.services().getAllElements()) {
			for (UserConfig uc : service.users().getAllElements()) {
				try {
					String user = uc.userName().getValue();
					if (usersToBeRemoved.contains(user))
						uc.delete();
				} catch (Exception e) {
					logger.error("Error removing messaging user configuration resource",e);
				}
			}
		}
	}
	
	private void updateUsersTable(Set<String> newUsers, Set<String> usersToBeRemoved) {
		try {
			for (String user: usersToBeRemoved) {
				userTable.removeItem(user, null);
			}
			for (String user: newUsers) {
				userTable.addItem(user, null);
			}
		} catch (Exception e) {
			logger.error("Error updating users table",e);
		}
	}
	
	private void refreshTable(Set<String> newUsers) {
		try {
			Set<String> rows = userTable.getRows(null);
			userTable.clear(null);
			userTable.refreshHeader(null);
			rows.remove(DynamicTable.HEADER_ROW_ID);
			rows.addAll(newUsers);
			for (String user : rows) {
				userTable.addItem(user, null);
			}
		} catch (Exception e) {
			logger.error("Error updating messaging app table",e);
		}
	}
	
	/**
	 * Returns whether a complete refresh of the table is performed (deleting all rows and recreating them), or
	 * an incremental update only
	 * @param newUsers
	 * @param usersToBeRemoved
	 * @param newListeners
	 * @param listenersToBeRemoved
	 * @return
	 */
	public boolean update(Set<String> newUsers, Set<String> usersToBeRemoved, Map<String, MessageListener> newListeners, Set<String> listenersToBeRemoved) {
		
		boolean anyUsersChanged = !newUsers.isEmpty() || !usersToBeRemoved.isEmpty();
		boolean anyServiceChanged = !newListeners.isEmpty() || !listenersToBeRemoved.isEmpty();
		if (anyServiceChanged) 
			updateListenerResources(newListeners, listenersToBeRemoved);
		if (anyUsersChanged) {
			updateUsersResources(usersToBeRemoved); 
			if (!anyServiceChanged) { // otherwise we need to completely refresh the table anyways
				updateUsersTable(newUsers, usersToBeRemoved);
			}
		}
		if (anyServiceChanged)
			refreshTable(newUsers);
		
		messagingApp.activate(true);
		return anyServiceChanged;
	}
		
	public void createUserTable() {
		
		UserTemplate userTemplate = new UserTemplate(userTable, page, getId(), messagingApp, messageReader);
		userTable.setRowTemplate(userTemplate);
		userTable.setDefaultRowIdComparator(null);
		
		List<WidgetStyle<?>> styles = new ArrayList<>();
		styles.add(DynamicTableData.CELL_ALIGNMENT_CENTERED);
		userTable.setDefaultStyles(styles);
		
		this.append(userTable, null);
	}

	public DynamicTable<String> getUserTable() {
		return userTable;
	}
	
}
