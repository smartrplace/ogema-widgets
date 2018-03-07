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
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.pattern.widget.table.ContextPatternTable;

public class ContextPatternInitTable<P extends ContextSensitivePattern<?, C>, C> extends ContextPatternTable<P, C> implements InitWidget {

	private static final long serialVersionUID = 1L;
	private final Class<C> contextType; 

	/**
	 * Default constructor, update mode {@link UpdateMode#AUTO_ON_GET}
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param patternType
	 * @param contextType
	 * @param template
	 * @param rpa
	 */
	public ContextPatternInitTable(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> patternType, Class<C> contextType, RowTemplate<P> template, ResourcePatternAccess rpa) {
		this(page, id, globalWidget, patternType, contextType, template, rpa, UpdateMode.AUTO_ON_GET);
	}
	
	/**
	 * Generic constructor
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param patternType
	 * @param contextType
	 * @param template
	 * @param rpa
	 * @param updateMode
	 */
	public ContextPatternInitTable(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> patternType, Class<C> contextType, RowTemplate<P> template, ResourcePatternAccess rpa, 
			UpdateMode updateMode) {
		super(page, id, globalWidget, patternType, template, rpa, updateMode);
		this.contextType = contextType;
	}

	/**
	 * Session-specific widget
	 * @param parent
	 * @param id
	 * @param req
	 * @param patternType
	 * @param contextType
	 * @param template
	 * @param rpa
	 * @param updateMode
	 */
	public ContextPatternInitTable(OgemaWidget parent, String id, OgemaHttpRequest req, Class<? extends P> patternType,Class<C> contextType, RowTemplate<P> template, ResourcePatternAccess rpa, 
			UpdateMode updateMode) {
		super(parent, id, req, patternType, template, rpa, updateMode);
		this.contextType = contextType;
	}

	@Override
	public void init(OgemaHttpRequest req) {
		C context = ContextPatternInitWidgetHelper.createContext(getPage(), contextType, req);
		setContext(context, req);
	}

}
