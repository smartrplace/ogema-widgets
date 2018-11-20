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
