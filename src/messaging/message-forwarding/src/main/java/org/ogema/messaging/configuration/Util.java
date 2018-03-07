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

package org.ogema.messaging.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.messaging.model.MessagingApp;
import de.iwes.widgets.messaging.model.MessagingService;
import de.iwes.widgets.messaging.model.UserConfig;

public class Util {

	/**
	 * Needed for table...
	 * @param listeners
	 * @return
	 * 		Map<User id, List<Service>>
	 */
	public static Map<String,List<String>> getServicesByUsers(Collection<MessageListener> listeners) {
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		for (MessageListener listener: listeners) {
			try {
				List<String> users = listener.getKnownUsers();
				String id = listener.getId();
				for (String user: users) {
					List<String> servicesPerUser = map.get(user);
					if (servicesPerUser == null) {
						servicesPerUser= new ArrayList<String>();
						map.put(user, servicesPerUser);
					}
					servicesPerUser.add(id);
				}
			} catch (Throwable e) {
				LoggerFactory.getLogger(Util.class).error("",e);
			}
		}
		return map;
		
	}

	// note: this can return null even for known users... if they have priority NONE, i.e. messages are not forwarded to them
	public static final UserConfig getUserConfig(String userName, String serviceId, MessagingApp app) {

		try {
			String validServiceId = ResourceUtils.getValidResourceName(serviceId);
			MessagingService serviceRes = app.services().getSubResource(validServiceId,MessagingService.class);
			if (!serviceRes.isActive())
				return null;
			
			for(UserConfig uc : serviceRes.users().getAllElements()) {
				StringResource name = uc.userName();
				if(name.isActive() && name.getValue().equals(userName)) {
					return uc;
				}
			}
			return null;
		} catch (Exception e) {
			LoggerFactory.getLogger(Util.class).warn("Exception trying to extract user config " + userName + ", service " + serviceId);
			return null;
		}
	}
	
	public static final UserConfig createUserConfig(String userName, String serviceId, MessagingApp app) {
		String validServiceId = ResourceUtils.getValidResourceName(serviceId);
		MessagingService serviceRes = app.services().getSubResource(validServiceId,MessagingService.class);
		if (!serviceRes.isActive())
			return null;
		if(serviceRes.users().getElementType() == null) {
			serviceRes.users().create();
			serviceRes.users().setElementType(UserConfig.class);
			serviceRes.users().activate(false);
		}
		UserConfig uc = serviceRes.users().add();
		uc.userName().<StringResource> create().setValue(userName);
		uc.priority().create();
		return uc;
	}
	
	// FIXME avoid creating unnecessary resources
	public static final void refreshUsersFromService(List<String> knownUsers, MessagingService serviceRes) {
		
//		adding new users
		for(String userName : knownUsers) {
			boolean userFound = false;
			for(UserConfig uc : serviceRes.users().getAllElements()) {
				String configUserName = uc.userName().getValue();
				if(configUserName.equals(userName)) {
					userFound = true;
					break;
				}
			}
			if(userFound)
				continue;
			UserConfig uc = serviceRes.users().add();
			StringResource userN = uc.userName().create();
			userN.setValue(userName);
			IntegerResource prio = uc.priority().create();
			prio.setValue(MessagePriority.NONE.getPriority());
			uc.activate(true);
		}
		
		//deleting old users
		for(UserConfig uc : serviceRes.users().getAllElements()) {
			String configUserName = uc.userName().getValue();
			if(!knownUsers.contains(configUserName)) {
				uc.delete();
			}
		}
				
	}

}