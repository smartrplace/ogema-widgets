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
 * A {@see org.ogema.tools.widget.html.form.dropdown.Dropdown} that displays all pattern matches of the specified type. 
 * @see TemplateDropdown
 * @see ResourcePattern
 * 
 * @param <P>
 * @param <R>
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
