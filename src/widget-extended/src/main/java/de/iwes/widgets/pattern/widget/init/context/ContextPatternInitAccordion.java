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

package de.iwes.widgets.pattern.widget.init.context;

import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.pattern.widget.accordion.ContextPatternAccordion;
import de.iwes.widgets.template.PageSnippetTemplate;

/**
 * For this to work, context class must have a default constructor
 * TODO
 *
 * @param <P>
 * @param <R>
 * @param <C>
 */
public class ContextPatternInitAccordion<P extends ContextSensitivePattern<?, C>, C> extends ContextPatternAccordion<P, C> implements InitWidget {

	private static final long serialVersionUID = 1L;
	private final Class<C> contextType; 
	
	public ContextPatternInitAccordion(WidgetPage<?> page, String id, Class<? extends P> defaultType, Class<C> contextType, PageSnippetTemplate<P> template, ResourcePatternAccess rpa) {
		super(page, id, defaultType, template, rpa);
		this.contextType = contextType;
	}
	
	public ContextPatternInitAccordion(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> defaultType, Class<C> contextType,
			PageSnippetTemplate<P> template, ResourcePatternAccess rpa, UpdateMode updateMode) {
		super(page, id, globalWidget, defaultType, template, rpa, updateMode);
		this.contextType = contextType;
	}
	
	public ContextPatternInitAccordion(OgemaWidget parent, String id, OgemaHttpRequest req, Class<? extends P> defaultType, Class<C> contextType, 
			PageSnippetTemplate<P> template, ResourcePatternAccess rpa,  UpdateMode updateMode) {
		super(parent, id, req, defaultType, template, rpa, updateMode);
		this.contextType = contextType;
	}

	@Override
	public void init(OgemaHttpRequest req) {
		C context = ContextPatternInitWidgetHelper.createContext(getPage(), contextType, req);
		setContext(context, req);
	}

}
