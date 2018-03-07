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

package de.iwes.widgets.api.extended.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.WidgetAdminService;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.services.IconService;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetApp;

@Component(specVersion = "1.2")
@Service(OgemaGuiService.class)
public class WidgetServiceImpl implements OgemaGuiService {
	
	private final List<NameService> nameServices = new ArrayList<NameService>();
	private final List<IconService> iconServices = new ArrayList<IconService>();
	
	@Reference
	public WidgetAdminService widgetServiceInternal;
	
	@Reference(cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			   bind="addNameService",
			   unbind="removeNameService",
			   policy=ReferencePolicy.DYNAMIC)
	private NameService nameService;
	
	@Reference(cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			   bind="addIconService",
			   unbind="removeIconService",
			   policy=ReferencePolicy.DYNAMIC)
	private IconService iconService;
	
	@Reference
	private MessagingService messagingService;
	
	@Override
	public WidgetApp createWidgetApp(String url, ApplicationManager am) {
		return new WidgetAppImpl(url, this, am);
	}
	
	@Override
	@Deprecated
	public WidgetApp createWidgetApp(String url, ApplicationManager am, boolean pageSpecificId) {
		return new WidgetAppImpl(url, this, am, pageSpecificId);
	}


	@Override
	public IconService getIconService() {
		synchronized (iconServices) {
			if (iconServices.isEmpty())
				return null;
			return iconServices.get(0);
		}
	}

	@Override
	public NameService getNameService() {
		synchronized (nameServices) {
			if (nameServices.isEmpty())
				return null;
			return nameServices.get(0);
		}
	}
	
	@Override
	public MessagingService getMessagingService() {
		return messagingService;
	}

	public void addNameService(NameService ns) {
		synchronized (nameServices) {
			if (!nameServices.contains(ns))
				nameServices.add(ns);
		}
	}
	
	public synchronized void removeNameService(NameService ns) {
		synchronized (nameServices) {
			nameServices.remove(ns);
		}
	}
	
	public void addIconService(IconService ns) {
		synchronized (iconServices) {
			if (!iconServices.contains(ns))
				iconServices.add(ns);
		}
	}
	
	public void removeIconService(IconService ns) {
		synchronized (iconServices) {
			iconServices.remove(ns);
		}  
	}
	
	
}
