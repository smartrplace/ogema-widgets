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
package de.iwes.widgets.pattern.widget.table;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.complextable.RowTemplate;

/**
 * A table that displays one row per pattern match (see {@link ResourcePattern}). The pattern class
 * can be set individually per session (see {@link #setType(Class, OgemaHttpRequest)}), or can be set globally via the constructor.
 * <br>
 * The table can be updated automatically (see {@link UpdateMode#AUTO_ON_GET}), in which case all matches for the
 * selected pattern type are shown, or can be updated manually (see {@link UpdateMode#MANUAL}).  
 *
 * @param <P>
 */
public class PatternTable<P extends ResourcePattern<?>> extends DynamicTable<P> implements PatternWidget<P> {

	private static final long serialVersionUID = 1L;
	protected final UpdateMode updateMode;
	protected Class<? extends P> defaultPatternType; 
	protected final ResourcePatternAccess rpa;
	
	/**
	 * Update mode MANUAL
	 * @param page
	 * @param id
	 * @param template
	 */
	public PatternTable(WidgetPage<?> page, String id, RowTemplate<P> template) {
		this(page, id, false, template);
	}
	
	/**
	 * Update mode MANUAL
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param template
	 */
	public PatternTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<P> template) {
		this(page, id, globalWidget, null, template, null, UpdateMode.MANUAL);
	}
	
	/**
	 * Default constructor, set update mode {@link UpdateMode#AUTO_ON_GET}.
	 * @param page
	 * @param id
	 * @param patternType
	 * @param template
	 * @param rpa
	 */
	public PatternTable(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> patternType, RowTemplate<P> template, ResourcePatternAccess rpa) {
		this(page, id, false, patternType, template, rpa, UpdateMode.AUTO_ON_GET);
	}
	
	public PatternTable(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> patternType, RowTemplate<P> template, ResourcePatternAccess rpa, 
			UpdateMode updateMode) {
		super(page, id, globalWidget);
		setDefaultStyles();
		setRowTemplate(template);
		this.updateMode = updateMode;
		this.defaultPatternType = patternType;
		this.rpa = rpa;
	}
	
	public PatternTable(OgemaWidget parent, String id, OgemaHttpRequest req, Class<? extends P> patternType, RowTemplate<P> template, ResourcePatternAccess rpa, 
			UpdateMode updateMode) {
		super(parent, id, req);
		setDefaultStyles();
		setRowTemplate(template);
		this.updateMode = updateMode;
		this.defaultPatternType = patternType;
		this.rpa = rpa;
	}

	@Override
	public PatternTableData<P> createNewSession() {
		return new PatternTableData<P>(this,rpa);
	}
	
	@Override
	public PatternTableData<P> getData(OgemaHttpRequest req) {
		@SuppressWarnings("unchecked")
		PatternTableData<P> opt = (PatternTableData<P>) super.getData(req);
		return opt;
	}
	
	@Override
	protected void setDefaultValues(DynamicTableData<P> opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		PatternTableData<P> opt2 = (PatternTableData<P>) opt;
		opt2.setType(defaultPatternType);
	}
	
	private void setDefaultStyles() {
		List<WidgetStyle<?>> list = new ArrayList<WidgetStyle<?>>();
		list.add(DynamicTableData.CELL_ALIGNMENT_CENTERED);
		list.add(DynamicTableData.BOLD_HEADER);
		setDefaultStyles(list);
	}

	@Override
	public UpdateMode getUpdateMode() {
		return updateMode;
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
