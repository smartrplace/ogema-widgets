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
