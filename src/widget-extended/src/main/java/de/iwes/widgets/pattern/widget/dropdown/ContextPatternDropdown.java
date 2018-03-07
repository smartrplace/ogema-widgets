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

import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.ContextPatternWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownData;

/**
 * A context-sensitive variant of {@link PatternDropdown}.
 * @see ContextSensitivePattern
 * @see PatternDropdown 
 *
 * @param <P>
 * @param <R>
 * @param <C>
 */
public class ContextPatternDropdown<P extends ContextSensitivePattern<?,C>, C> extends PatternDropdown<P> implements ContextPatternWidget<P,C> {
	
	private static final long serialVersionUID = 1L;
	private C defaultContext = null;
	
	/**
	 * Default update mode: {@link UpdateMode#AUTO_ON_GET}
	 * @param page
	 * @param id
	 * @param defaultType
	 * @param rpa
	 */
	public ContextPatternDropdown(WidgetPage<?> page, String id, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(page, id, defaultType, rpa);
	}
	
	public ContextPatternDropdown(WidgetPage<?> page, String id, boolean globalWidget, UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(page, id, globalWidget, updateMode, defaultType, rpa);
	}
	
	public ContextPatternDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(parent, id, req, updateMode, defaultType, rpa);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ContextPatternDropdownData<P,C> getData(OgemaHttpRequest req) {
		return (ContextPatternDropdownData<P,C>) super.getData(req);
	}
	
	@Override
	public ContextPatternDropdownData<P,C> createNewSession() {
		return new ContextPatternDropdownData<P,C>(this, rpa);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ContextPatternDropdownData<P,C> opt2 = (ContextPatternDropdownData<P, C>) opt;
		opt2.setContext(defaultContext);
	}

	@Override
	public C getContext(OgemaHttpRequest req) {
		return getData(req).getContext();
	}
	
	@Override
	public void setContext(C context, OgemaHttpRequest req) {
		getData(req).setContext(context);
	}
	
	@Override
	public void setDefaultContext(C context) {
		this.defaultContext = context;
	}

}
