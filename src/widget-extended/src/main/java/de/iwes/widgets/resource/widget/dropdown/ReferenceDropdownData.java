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

package de.iwes.widgets.resource.widget.dropdown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.DropdownData;

public class ReferenceDropdownData extends DropdownData {
	
	private Resource resource = null;
	private Resource referenceTarget = null;
	private Class<?> type = null;
	private final ApplicationManager am;
	public static final String EMPTY_OPTION_ID = "empty__option__id";
	private final Alert alert; // may be null

	public ReferenceDropdownData(ReferenceDropdown dropdown, ApplicationManager am, Alert alert) {
		super(dropdown);
		this.am = am;
		this.alert = alert;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		setOptions(getDDOptions());
		return super.retrieveGETData(req);
	}
	
	@Override
	public JSONObject onPOST(String json, OgemaHttpRequest req) {
		JSONObject result =  super.onPOST(json, req);
		String newLocation = getSelectedValue();
		if (newLocation == null || newLocation.equals(EMPTY_OPTION_ID)) {
			referenceTarget = null;
			return result;
		}
		referenceTarget = am.getResourceAccess().getResource(newLocation);
		if (resource != null) {
			try {
				if (!resource.equals(referenceTarget)) 
					resource.setAsReference(referenceTarget);
			} catch (Exception e) {
				if (alert != null)
					alert.showAlert("Could not set resource " + resource.getPath() + " as a reference: " + e, false, req);
			}
		}
		return result;
	}


	public Resource getResource() {
		return resource;
	}
	
	public Resource getReferenceTarget() {
		return referenceTarget;
	}

	public void setResource(Resource resource, Class<?> targetType) {
		if (resource != null && targetType == null)
			targetType = resource.getResourceType();
		if (resource != null && resource.isTopLevel())
			throw new IllegalArgumentException("Cannot set a toplevel resource as a reference.");
		if (targetType != null && !Resource.class.isAssignableFrom(targetType) && !ResourcePattern.class.isAssignableFrom(targetType))
			throw new IllegalArgumentException("Class must be either a resource type or resource pattern type");
		this.resource = resource;
		this.type = targetType;
	}

	public Class<?> getType() {
		return type;
	}
	
	@SuppressWarnings("unchecked")
	private List<DropdownOption> getDDOptions() {
		if (resource == null && type == null) {
			return Collections.emptyList();
		}
		String resLocation;
		if (resource != null)
			resLocation = resource.getLocation();
		else
			resLocation = EMPTY_OPTION_ID; // previous value
		boolean selectedFound = false;
		List<DropdownOption> opts = new ArrayList<DropdownOption>();
		if (resource == null || !resource.isReference(true)) {
			opts.add(new DropdownOption(EMPTY_OPTION_ID, "", true));
		}
		if (Resource.class.isAssignableFrom(type)) {
			List<? extends Resource> resources = am.getResourceAccess().getResources((Class<? extends Resource>) type);
			for (Resource res: resources) {
				if (res.equals(resource))
					continue;
				String path = res.getPath();
				boolean sel = path.equals(resLocation);
				if (sel)
					selectedFound = true;
				DropdownOption opt = new DropdownOption(path, path, sel);
				opts.add(opt);
			}
		}
		else if (ResourcePattern.class.isAssignableFrom(type)) {
			@SuppressWarnings("rawtypes")
			List<? extends ResourcePattern<?>> patterns = (List<? extends ResourcePattern<?>>)
				am.getResourcePatternAccess().getPatterns((Class<? extends ResourcePattern>) type, AccessPriority.PRIO_LOWEST);
			for (ResourcePattern<?> pattern: patterns) {
				String path = pattern.model.getPath();
				boolean sel = path.equals(resLocation);
				if (sel)
					selectedFound = true;
				DropdownOption opt = new DropdownOption(path, path, sel);
				opts.add(opt);
			}
		}
		if (!selectedFound && !opts.isEmpty()) {
			opts.get(0).select(true);
		}
		return opts;
	}
	
}
