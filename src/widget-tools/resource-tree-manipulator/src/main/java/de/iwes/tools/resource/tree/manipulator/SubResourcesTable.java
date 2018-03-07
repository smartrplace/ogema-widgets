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

package de.iwes.tools.resource.tree.manipulator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.listselect.ListSelect;
import de.iwes.widgets.resource.widget.autocomplete.ResourcePathAutocomplete;

public class SubResourcesTable extends ListSelect {
	
	private final ResourcePathAutocomplete resourceSelector;
	
	private static final long serialVersionUID = 1L;
	private static final String[] header = 
			new String[] {"Name", "Type", "Value", "Active", "Reference" };

	public SubResourcesTable(WidgetPage<?> page, String id, ResourcePathAutocomplete resourceSelector) {
		super(page, id);
		this.resourceSelector = resourceSelector;
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		clearValues(req);
		Resource res = resourceSelector.getSelectedResource(req);
		if (res == null) 
			return;
		Map<String,String[]> map = getSubresourcesMap(res);
		setArrayValues(map, header, req);
	}
	
	private Map<String,String[]> getSubresourcesMap(Resource res) {
		Map<String,String[]> map = new LinkedHashMap<String, String[]>();
		for (Resource sub: res.getSubResources(false)) {
			String name = sub.getName();
			String type = sub.getResourceType().getSimpleName();
			String value = "";
			if (sub instanceof SingleValueResource) 
				value = ValueResourceUtils.getValue((SingleValueResource) sub);
			String reference = "false";
			if (sub.isReference(false)) 
				reference = sub.getLocation();
			String active = String.valueOf(sub.isActive());
			
			String[] row = new String[] { name, type, value, active, reference };
			map.put(name, row);
		}
		return map;
	}

}
