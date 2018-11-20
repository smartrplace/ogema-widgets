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
package de.iwes.widgets.pattern.widget.dropdown;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.DefaultPatternTemplate;
import de.iwes.widgets.api.extended.pattern.PatternSelector;
import de.iwes.widgets.api.extended.pattern.PatternWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;

/**
 * A Dropdown that displays all pattern matches of the specified type. 
 * @see TemplateDropdown
 * @see ResourcePattern
 * 
 * @param <P>
 */
public class PatternDropdown<P extends ResourcePattern<?>> extends TemplateDropdown<P> implements PatternWidget<P>, PatternSelector<P> {
	
	private static final long serialVersionUID = 1L;
	protected final ResourcePatternAccess rpa;
	protected Class<? extends P> defaultType = null;
	protected P defaultSelectedPattern = null;
	protected final UpdateMode updateMopde;
	
	/**
	 * Update mode {@link UpdateMode#MANUAL}
	 * @param page
	 * @param id
	 */
	public PatternDropdown(WidgetPage<?> page, String id) {
		this(page, id, false);
	}
	
	/**
	 * Update mode {@link UpdateMode#MANUAL}
	 * @param page
	 * @param id
	 */
	public PatternDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, UpdateMode.MANUAL, null, null);
	}
	
	/**
	 * Default update mode: {@link UpdateMode#AUTO_ON_GET}
	 * @param page
	 * @param id
	 * @param defaultType
	 * @param rpa
	 */
	public PatternDropdown(WidgetPage<?> page, String id, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		this(page, id, false, UpdateMode.AUTO_ON_GET, defaultType, rpa);
	}
	
	/**
	 * @param page
	 * @param id
	 * @param globalWidget
	 * 		Note: a dropdown should not normally be global (different users can make different seelctions), unless the selection
	 * 		is stored persistently. An example could be a dropdown that allows to select a resource reference target.
	 * @param updateMode
	 * @param defaultType
	 * @param rpa
	 */
	public PatternDropdown(WidgetPage<?> page, String id, boolean globalWidget, UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(page, id, globalWidget);
		this.rpa = rpa;
		this.defaultType = defaultType;
		this.updateMopde = updateMode;
		setTemplate(new DefaultPatternTemplate<P>());
	}
	
	public PatternDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(parent, id, req);
		this.rpa = rpa;
		this.defaultType = defaultType;
		this.updateMopde = updateMode;
		setTemplate(new DefaultPatternTemplate<P>());
	}
	
	
	@Override
	public PatternDropdownData<P> getData(OgemaHttpRequest req) {
		PatternDropdownData<P> opt = (PatternDropdownData<P>) super.getData(req);
		return opt;
	}

	@Override
	public PatternDropdownData<P> createNewSession() {
		return new PatternDropdownData<P>(this, rpa);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		PatternDropdownData<P> options = (PatternDropdownData<P>) opt;
		if (defaultType != null)
			options.setType(defaultType);
		if (defaultSelectedPattern != null)
			options.selectItem(defaultSelectedPattern);
	}

	@Override
	public void selectDefaultItem(P instance) throws UnsupportedOperationException {
		this.defaultSelectedPattern = instance;
	}

	@Override
	public UpdateMode getUpdateMode() {
		return updateMopde;
	}

	@Override
	public void setType(Class<? extends P> type, OgemaHttpRequest req) {
		getData(req).setType(type);
	}

	@Override
	public Class<? extends P> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}


	
}
