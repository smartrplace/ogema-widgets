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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.multiselect.Multiselect;
import de.iwes.widgets.html.multiselect.MultiselectData;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.PreferredName;

// FIXME replace by generic ResourceListMultiselect?
class ResourceListMultiselect<P extends ResourcePattern<?>> extends Multiselect {

	private static final long serialVersionUID = 1L;
	private final Class<P> targetType;
	private final ApplicationManager am;
	private boolean initialActiveStatus = true;
	private final PreferredName preferredName;
	
	public ResourceListMultiselect(WidgetPage<?> page, String id, Class<P> targetType,PreferredName preferredName, ApplicationManager am) {
		super(page, id);
		this.targetType = targetType;
		this.am = am;
		this.preferredName = preferredName;
		setDefaultWidth("100%");
	}
	
	
	private class MyOptions extends MultiselectData {

		private boolean isActive = true;
		
		public MyOptions(ResourceListMultiselect<P> rlm) {
			super(rlm);
		}
		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			JSONObject result = super.retrieveGETData(req);
			if (!isActive) {	       
		        JSONArray array = new JSONArray();
				result.put("options", array);
				return result;
			}
			return result;
		}

		public void setActive(boolean status) {
			isActive = status;
		}

	}
	
	@Override
	public MultiselectData createNewSession() {
		return new MyOptions(this);
	}
	
	@Override
	public MyOptions getData(OgemaHttpRequest req) {
		return (ResourceListMultiselect<P>.MyOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(MultiselectData opt) {
		super.setDefaultValues(opt);
		((MyOptions) opt).setActive(initialActiveStatus);
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		Collection<DropdownOption> opt = getSelected(req);
		Set<String> selected  = new HashSet<String>();
		if (opt != null) {
			for (DropdownOption op : opt) {
				selected.add(op.id());
			}
		}
		Set<DropdownOption> options;
		List patterns = am.getResourcePatternAccess().getPatterns(targetType, AccessPriority.PRIO_LOWEST); // cannot work with type arguments here...
		options = getPatternOptions(patterns,selected,req);
		setOptions(options, req);
	}
	
	private final Set<DropdownOption> getPatternOptions(Collection<ResourcePattern> patterns,Collection<String> selected, OgemaHttpRequest req) {
		if (selected == null) selected = new HashSet<String>();
		Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
		for (ResourcePattern<?> res: patterns ) {
			String path  = res.model.getPath();
			String value;
			if (preferredName == PreferredName.RESOURCE_PATH || getNameService() == null) {
				value = path;
			}
			else {
				value = getNameService().getName(res.model, req.getLocale(), true,true);
				if (value==null) value=path;
			}	
			
			DropdownOption opt = new DropdownOption(path,value, selected.contains(path) );
			options.add(opt);
		}
		return options;
	}
	
	public void setActive(boolean status, OgemaHttpRequest req) {
		getData(req).setActive(status);
	}
	
	public void setInitialActiveStatus(boolean status) {
		initialActiveStatus = status;
	}
}