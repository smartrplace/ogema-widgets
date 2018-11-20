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
package de.iwes.widgets.pattern.widget.init.context;

import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.pattern.widget.dropdown.ContextPatternDropdown;

/**
 * For this to work, context class must have a default constructor
 * TODO
 *
 * @param <P>
 * @param <C>
 */
public class ContextPatternInitDropdown<P extends ContextSensitivePattern<?, C>, C> extends ContextPatternDropdown<P, C> implements InitWidget {

	private static final long serialVersionUID = 1L;
	private final Class<C> contextType; 

	/**
	 * Default update mode: {@link UpdateMode#AUTO_ON_GET}
	 * @param page
	 * @param id
	 * @param defaultType
	 * @param contextType
	 * @param rpa
	 */
	public ContextPatternInitDropdown(WidgetPage<?> page, String id, Class<? extends P> defaultType, Class<C> contextType, ResourcePatternAccess rpa) {
		super(page, id, defaultType, rpa);
		this.contextType = contextType;
	}
	
	public ContextPatternInitDropdown(WidgetPage<?> page, String id, boolean globalWidget, UpdateMode updateMode, Class<? extends P> defaultType, Class<C> contextType, ResourcePatternAccess rpa) {
		super(page, id, globalWidget, updateMode, defaultType, rpa);
		this.contextType = contextType;
	}
	
	public ContextPatternInitDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, UpdateMode updateMode, Class<? extends P> defaultType, Class<C> contextType, ResourcePatternAccess rpa) {
		super(parent, id, req, updateMode, defaultType, rpa);
		this.contextType = contextType;
	}

	@Override
	public void init(OgemaHttpRequest req) {
		C context = ContextPatternInitWidgetHelper.createContext(getPage(), contextType, req);
		setContext(context, req);
		String selected = ContextPatternInitWidgetHelper.getSelected(getPage(), req);
		if (selected != null)
			getData(req).selectSingleOption(selected);
	}
	
}
