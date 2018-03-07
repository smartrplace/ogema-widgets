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

package de.iwes.widgets.resource.widget.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.autocomplete.AutocompleteData;

public class ResourcePathAutocompleteData extends AutocompleteData {
	
	private boolean showOnlyToplevel = false;
	private Class<? extends Resource> resType = Resource.class;
	private final ResourceAccess ra;
	private final boolean showDirectReferences;

	public ResourcePathAutocompleteData(ResourcePathAutocomplete pathAutocomplete, ResourceAccess ra, boolean showDirectReferences) {
		super(pathAutocomplete);
		this.ra= ra;
		this.showDirectReferences = showDirectReferences;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		List<String> newOptions = getResourcePaths();
		String value = getValue();
		if (value != null && !value.isEmpty() && !newOptions.contains(value)) {
			setValue("");
		}
		setOptions(newOptions);
		return super.retrieveGETData(req);
	}
	
	/*
	 * Public methods
	 */

	public boolean showOnlyToplevel() {
		return showOnlyToplevel;
	}

	public void setShowOnlyToplevel(boolean showOnlyToplevel) {
		this.showOnlyToplevel = showOnlyToplevel;
	}

	public Class<? extends Resource> getResourceType() {
		return resType;
	}

	public void setResourceType(Class<? extends Resource> resType) {
		this.resType = resType;
	}

	/*
	 * Internal
	 */
	
	@SuppressWarnings("unchecked")
	private List<String> getResourcePaths() {
		List<Resource> resources;
		if (showOnlyToplevel)
			resources = (List<Resource>) ra.getToplevelResources(resType);
		else
			resources = (List<Resource>) ra.getResources(resType);
		List<String> paths = new ArrayList<String>();
		for (Resource res: resources) {
			if (((ResourcePathAutocomplete) widget).filter(res))
				addResource(res, paths);
		}
		return paths;
	}
	
	private void addResource(Resource res, List<String> paths) {
		paths.add(res.getPath());
		if (showDirectReferences) {
			List<Resource> references = res.getReferencingResources(null);
			int sz = references.size();
			// this may equal to 1 even though no reference exist... the actual parent is always contained, if it is not a toplevel res
			if (sz < 1) return; 
			Class<? extends Resource> currentType = res.getResourceType();
			for (Resource refParent: references) {
				Resource ref = getReferencingSubresource(refParent, res, currentType);
				if (ref == null || ref.equals(res)) 
					continue; 
				if (ref.isReference(false) && !paths.contains(ref.getPath()))
					addResource(ref, paths);
			}
		}
	}
	
	private Resource getReferencingSubresource(Resource parent, Resource target, Class<? extends Resource> type) {
		List<? extends Resource> subs = parent.getSubResources(type, false);
		for (Resource res: subs) {
			if (res.equalsLocation(target))
				return res;
		}
		return null;
	}
	
	public Resource getSelectedResource() {
		if (getValue() == null)
			return null;
		return ra.getResource(getValue());
	}
	
}
