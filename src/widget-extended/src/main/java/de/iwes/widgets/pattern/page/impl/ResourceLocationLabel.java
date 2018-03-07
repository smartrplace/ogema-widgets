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

package de.iwes.widgets.pattern.page.impl;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.PreferredName;

/**
 * Marker widget, to indicate that this widget is to display the location of a resource.
 * Used to display referencing ReadOnly fields 
 */
// FIXME replace by ResourceLabel?
public class ResourceLocationLabel extends Label {

	private static final long serialVersionUID = 1L;
	private final PreferredName preferredName;
	private final NameService nameService;
	private final ResourceAccess ra;

	public ResourceLocationLabel(WidgetPage<?> page, String id, PreferredName preferredName,  ResourceAccess ra) {
		super(page, id);
		this.preferredName = preferredName;
		this.nameService = getNameService();
		this.ra = ra;
	}
	
	void setValue(Resource resource, OgemaHttpRequest req) {
		if (resource == null || !resource.isActive()) { 
			setText("", req);
			return;
		}
		String location = resource.getLocation();
		resource = ra.getResource(location);
		String value;
		if (preferredName == PreferredName.RESOURCE_PATH || nameService == null) {
			value = location;
		}
		else {
			value = nameService.getName(resource, req.getLocale(), true,true);
			if (value==null) value=location;
		}		
		setText(value, req);
	}

}
