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

import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.autocomplete.Autocomplete;
import de.iwes.widgets.html.autocomplete.AutocompleteData;

/**
 * An input text field, with autocomplete functionality for resources of a specific type
 */
public class ResourcePathAutocomplete extends Autocomplete {

	private static final long serialVersionUID = 1L;
	private Class<? extends Resource> defaultResourceType = null;
	private boolean defaultShowOnlyToplevel = false;
	private final boolean showDirectReferences;

	private final ResourceAccess ra;

	public ResourcePathAutocomplete(WidgetPage<?> page, String id, ResourceAccess ra) {
		this(page, id, ra, false);
	}
	
	public ResourcePathAutocomplete(WidgetPage<?> page, String id, ResourceAccess ra, boolean showDirectReferences) {
		super(page, id);
		this.ra = ra;
		this.showDirectReferences  = showDirectReferences;
	}
	
	public ResourcePathAutocomplete(OgemaWidget parent, String id, ResourceAccess ra, OgemaHttpRequest req) {
		this(parent, id, ra, false, req);
	}
	
	public ResourcePathAutocomplete(OgemaWidget parent, String id, ResourceAccess ra, boolean showDirectReferences, OgemaHttpRequest req) {
		super(parent, id, req);
		this.ra = ra;
		this.showDirectReferences = showDirectReferences;
	}
	
	/*
	 ************* Inherited methods *****************
	 */
	
	/**
	 * Override to filter out resources
	 * @param resource
	 * @return
	 * 		true: keep resource; false: dismiss resource
	 */
	protected boolean filter(Resource resource) {
		return true;
	}

	@Override
	public ResourcePathAutocompleteData createNewSession() {
		return new ResourcePathAutocompleteData(this,ra, showDirectReferences);
	}
	
	@Override
	public ResourcePathAutocompleteData getData(OgemaHttpRequest req) {
		return (ResourcePathAutocompleteData) super.getData(req);
	}

	@Override
	protected void setDefaultValues(AutocompleteData opt) {
		super.setDefaultValues(opt);
		ResourcePathAutocompleteData opt2= (ResourcePathAutocompleteData) opt;
		if (defaultResourceType != null)
			opt2.setResourceType(defaultResourceType);
		opt2.setShowOnlyToplevel(defaultShowOnlyToplevel);
	}
	
	/*
	 ************* Public methods *****************
	 */
	
	public void setDefaultResourceType(Class<? extends Resource> defaultResourceType) {
		this.defaultResourceType = defaultResourceType;
	}

	public void setDefaultShowOnlyToplevel(boolean showOnlyToplevel) {
		this.defaultShowOnlyToplevel = showOnlyToplevel;
	}
	
	public boolean showOnlyToplevel(OgemaHttpRequest req) {
		return getData(req).showOnlyToplevel();
	}

	public void setShowOnlyToplevel(boolean showOnlyToplevel,OgemaHttpRequest req) {
		getData(req).setShowOnlyToplevel(showOnlyToplevel);
	}

	public Class<? extends Resource> getResourceType(OgemaHttpRequest req) {
		return getData(req).getResourceType();
	}

	public void setResourceType(Class<? extends Resource> resourceType,OgemaHttpRequest req) {
		getData(req).setResourceType(resourceType);
	}
	
	/**
	 * Returns the currently selected resource, or null, if no resource has been selected
	 * @param req
	 * @return
	 */
	public Resource getSelectedResource(OgemaHttpRequest req) {
		return getData(req).getSelectedResource();
	}
	
	/*
	 * not supported
	 */
	
	@Override
	public void setDefaultAutocompleteOptions(List<String> options) {
		throw new UnsupportedOperationException("Not admissible; use method #setDefaultResourceType instead");
	}
	
	@Override
	public void setAutocompleteOptions(List<String> options, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not admissible; use method #setResourceType instead");
	}
	
}
