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

package de.iwes.widgets.pattern.widget.table;

import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.ContextPatternWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.complextable.RowTemplate;

/**
 * A table that displays one row per pattern match (see {@link ContextSensitivePattern}). 
 * Besides the pattern class, also the context for the pattern type can be set individually per session.
 * However, all pattern types must support the same context class.
 * <br> 
 * The table can be updated automatically (see {@link UpdateMode#AUTO_ON_GET}), in which case all matches for the
 * selected pattern type and context are shown, or can be updated manually (see {@link UpdateMode#MANUAL}).  
 *
 * @param <P>
 * @param <R>
 * @param <C>
 */
public class ContextPatternTable<P extends ContextSensitivePattern<?, C>, C> extends PatternTable<P>
		implements ContextPatternWidget<P,C> {

	private static final long serialVersionUID = 1L;
	private C defaultContext = null;
	
	/**
	 * Default constructor, set update mode {@link UpdateMode#AUTO_ON_GET}, and widget is session-specific. 
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param patternType
	 * @param template
	 * @param rpa
	 */
	public ContextPatternTable(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> patternType, RowTemplate<P> template, ResourcePatternAccess rpa) {
		super(page, id, globalWidget, patternType, template, rpa);
	}
	
	/**
	 * Generic constructor
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param patternType
	 * @param template
	 * @param rpa
	 * @param updateMode
	 */
	public ContextPatternTable(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> patternType, RowTemplate<P> template, ResourcePatternAccess rpa, 
			UpdateMode updateMode) {
		super(page, id, globalWidget, patternType, template, rpa, updateMode);
	}

	public ContextPatternTable(OgemaWidget parent, String id, OgemaHttpRequest req, Class<? extends P> patternType, RowTemplate<P> template, ResourcePatternAccess rpa, 
			UpdateMode updateMode) {
		super(parent, id, req, patternType, template, rpa, updateMode);
	}
	
	@Override
	public ContextPatternTableData<P,C> getData(OgemaHttpRequest req) {
		@SuppressWarnings("unchecked")
		ContextPatternTableData<P,C> opt = (ContextPatternTableData<P,C>) super.getData(req);
		return opt;
	}
	
	@Override
	public ContextPatternTableData<P,C> createNewSession() {
		return new ContextPatternTableData<P,C>(this, rpa);
	}
	
	@Override
	protected void setDefaultValues(DynamicTableData<P> opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ContextPatternTableData<P,C> opt2 = (ContextPatternTableData<P, C>) opt;
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
