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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.pattern.widget.dropdown.PatternDropdown;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.PreferredName;

// FIXME replace by generic ReferenceDropdown?
class ReferenceDropdown<R extends Resource> extends ControllableDropdown {

	private static final long serialVersionUID = 1L;
	private final Class<R> targetType;
	private final Class<ResourcePattern<? extends R>> patternType;
	private final ApplicationManager am;
	private final PreferredName preferredName;
	private final boolean includeEmptyOption;
	// != null only for Pattern editor
	private final PatternDropdown<?> mainSelector;
	private final Field targetField;

	public ReferenceDropdown(WidgetPage<?> page, String id, Class<R> targetType, PreferredName preferredName, ApplicationManager am, 
			 PatternDropdown<?> mainSelector, Field targetField, boolean includeEmptyOption) {
		this(page, id, targetType, preferredName, am, mainSelector, targetField, includeEmptyOption, null);
	}
	
	public ReferenceDropdown(WidgetPage<?> page, String id, Class<R> targetType, PreferredName preferredName, ApplicationManager am, 
			PatternDropdown<?> mainSelector, Field targetField, boolean includeEmptyOption, Class<ResourcePattern<? extends R>> patternType) {
		super(page, id);
		this.targetType = targetType;
		this.patternType = patternType;
		this.am = am;
		this.preferredName = preferredName;
		this.includeEmptyOption = includeEmptyOption;
		this.mainSelector = mainSelector;
		this.targetField = targetField;
	}
	
	private static DropdownOption getEmptyOption(boolean selected) {
		return new DropdownOption(DropdownData.EMPTY_OPT_ID, "", selected);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onGET(OgemaHttpRequest req) {
//		DropdownOption opt = getSelected(req);
//		String selected  =null;
//		if (opt != null) {
//			selected = opt.getValue();
//		}
		String selected = getSelectedValue(req);
		Set<DropdownOption> options;
		if (patternType == null) {
			List<R> resources = am.getResourceAccess().getResources(targetType);
			options = getTargetOptions((Collection<Resource>) resources,selected,req);
		}
		else {
			List patterns = am.getResourcePatternAccess().getPatterns(patternType, AccessPriority.PRIO_LOWEST); // cannot work with type arguments here...
			options = getPatternOptions(patterns,selected,req);
		}
		setOptions(options, req);
	}
	
	
	private final Set<DropdownOption> getTargetOptions(Collection<Resource> resources, String selected,OgemaHttpRequest req) {
		boolean selectFirst = false;
		if (selected == null && !includeEmptyOption && !resources.isEmpty()) {
			selectFirst = true;
		}
		int cnt = 0;
		Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
		if (includeEmptyOption) {
			boolean selectEmpty = (selected == null || selected == DropdownData.EMPTY_OPT_ID);
			options.add(getEmptyOption(selectEmpty));
		}
		for (Resource res: resources ) {
			if (!res.isActive()) continue;
			final String path =  res.getPath();
			String value;
			if (preferredName == PreferredName.RESOURCE_PATH || getNameService() == null) {
				value = path;
			}
			else {
				value = getNameService().getName(res, req.getLocale(), true,true);
				if (value==null) value=path;
			}		
			DropdownOption opt = new DropdownOption(path,value,(path.equals(selected) || (selectFirst && 0==cnt++)));
			options.add(opt);
		}
		return options;
	}
	
	private final Set<DropdownOption> getPatternOptions(@SuppressWarnings("rawtypes") Collection<ResourcePattern> patterns,String selected,OgemaHttpRequest req) {
		boolean selectFirst = false;
		if (selected == null && !includeEmptyOption && !patterns.isEmpty()) {
			selectFirst = true;
		}
		int cnt = 0;
		Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
		if (includeEmptyOption) {
			boolean selectEmpty = (selected == null || selected == DropdownData.EMPTY_OPT_ID);
			options.add(getEmptyOption(selectEmpty));
		}
		boolean targetFound = mainSelector == null;
		final ResourcePattern<?> currentMainPattern = targetFound ? null : mainSelector.getSelectedItem(req);
		Resource target = null;
		if (currentMainPattern != null) {
			try {
				target = (Resource) targetField.get(currentMainPattern);
				if (target != null)
					selectFirst = false;
			} catch (Exception ignore) {}
		}
		for (ResourcePattern<?> res: patterns ) {
			if (target != null && !targetFound && res.model.equalsLocation(target))
				targetFound = true;
			String path  = res.model.getPath();
			String value;
			if (preferredName == PreferredName.RESOURCE_PATH || getNameService() == null) {
				value = path;
			}
			else {
				value = getNameService().getName(res.model, req.getLocale());
				if (value==null) value=path;
			}
			DropdownOption opt = new DropdownOption(path,value, targetFound && ((path.equals(selected) || (selectFirst && 0==cnt++))));
			options.add(opt);
		}
		if (target != null && !targetFound) {
			String path  = target.getLocation();
			String value;
			if (preferredName == PreferredName.RESOURCE_PATH || getNameService() == null) {
				value = path;
			}
			else {
				value = getNameService().getName(target, req.getLocale());
				if (value==null) value=path;
			}
			DropdownOption opt = new DropdownOption(path,value, true);
			options.add(opt);
		}
		return options;
	}
}