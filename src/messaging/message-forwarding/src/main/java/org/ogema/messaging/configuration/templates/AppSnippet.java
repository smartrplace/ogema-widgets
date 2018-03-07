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
		
//updating Services
		
//		Map<String,MessageListener> messageListeners = messageReader.getMessageListeners();
//		List<MessagingService> oldServices = serviceList.getAllElements();
//		for (MessagingService oldService: oldServices) {
//			if (!messageListeners.containsKey(oldService.serviceId().getValue())) {
//				oldService.delete();
//			}
//		}
		
//		oldServices = serviceList.getAllElements();
//		Set<String> allUsers = new HashSet<>();
		
//updating Users for each Service
		
//		for (Map.Entry<String, MessageListener> entry: messageListeners.entrySet()) {
//			String serviceId = ResourceUtils.getValidResourceName(entry.getKey());
//			MessagingService serviceRes;
//			try {
//				serviceRes = serviceList.getSubResource(serviceId,MessagingService.class).create();
//			} catch (Exception e) {
//				logger.warn("Could not add messaging service " + serviceId,e);
//				continue;
//			}
//			serviceRes.serviceId().<StringResource> create().setValue(entry.getKey());
//			serviceRes.users().create();
//			
//			MessageListener listener = entry.getValue();
//			allUsers.addAll(listener.getKnownUsers());
//		}
		
//refreshUserTable
		
//		List<String> oldUsers = userTable.getItems(null);
//		oldUsers.remove(DynamicTable.HEADER_ROW_ID);
//		
//		for (String oldUser: oldUsers) {
//			if (!allUsers.contains(oldUser))
//				userTable.removeItem(oldUser, null);
//		}
//		
//		for (String newUser: allUsers) {
//			if (!oldUsers.contains(newUser)) 
//				userTable.addItem(newUser, null);
//		}
		
		//TODO Refreshing Select Connector if a User from Message-Settings was deleted. Updating Select Connector does not work atm
//		for(String user : userTable.getRows(null)) {
//			if(user.equals(DynamicTable.HEADER_ROW_ID))
//				continue;
//			String validUserId = ResourceUtils.getValidResourceName(user);
//			for(String column : userTable.getColumns(null)) {
//				if(column.equals("userNameColumn"))
//					continue;
//				if(userTable.getCellContent(validUserId, column, null) == null /*&& ReceiverConfig got subResource*/) {
//					userTable.removeItem(validUserId, null);
//					userTable.addItem(validUserId, null);
//				}
//				if(!userTable.getCellContent(user, column, null).equals(null) /*&& ReceiverConfig don't got subResource*/) {
//					//TODO delete existing Resources
//					String validServiceId = ResourceUtils.getValidResourceName((String)userTable.getCellContent(DynamicTable.HEADER_ROW_ID, column, null));
//					System.out.println("service Id = " + validServiceId);
//					
//					MessagingService serviceRes;
//					try {
//						serviceRes = serviceList.getSubResource(validServiceId,MessagingService.class).create();
//					} catch (Exception e) {
//						logger.warn("Could not add messaging service " + validServiceId,e);
//						continue;
//					}
//					serviceRes.serviceId().<StringResource> create().setValue(validServiceId);
//					serviceRes.users().create();
//					System.out.println("serviceRes : " + serviceRes.serviceId().getValue());
//					System.out.println("serviceRes.users().size() : " + serviceRes.users().size());
//					for(UserConfig uc : serviceRes.users().getAllElements()) {
//						System.out.println("Comparison : " + uc.userName().getValue() + " with " + user);
//						if(uc.userName().getValue().equals(user))
//							uc.delete();
//						System.out.println("UserConfig : " + uc.userName().getValue() + " deleted from Service " + validServiceId);
//					}
//					userTable.removeItem(validUserId, null);
//					userTable.addItem(validUserId, null);
//				}
//			}
//		}

	
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
