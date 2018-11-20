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
package de.iwes.widgets.resource.widget.table;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.complextable.RowTemplate;

/**
 * Displays the content of a resource list. The list can be set indiviudally per session
 * using {@link #setList(ResourceList, OgemaHttpRequest)}, or can be set gloablly using
 * {@link #setDefaultList(ResourceList)}. List elements can be either added manually to the 
 * ResourceListTable, if update mode {@link UpdateMode#MANUAL} is specified, or can be updated 
 * automatically on each GET, if {@link UpdateMode#AUTO_ON_GET} is selected. In the latter case, all
 * elements of the resource list are displayed, otherwise it is possible to select a subset.
 *
 * @param <R>
 * 		the element type of the list(s) displayed
 */
public class ResourceListTable<R extends Resource> extends ResourceTable<R> {

	private static final long serialVersionUID = 1L;
	private ResourceList<R> list;
	
	/**
	 * Update mode AUTO_ON_GET
	 * @param page
	 * @param id
	 * @param template
	 */
	public ResourceListTable(WidgetPage<?> page, String id, RowTemplate<R> template) {
		this(page, id, false, template, UpdateMode.AUTO_ON_GET);
	}

	public ResourceListTable(WidgetPage<?> page, String id, RowTemplate<R> template, ResourceList<R> defaultList) {
		this(page, id, false, template, UpdateMode.AUTO_ON_GET);
		setDefaultList(defaultList);
	}
	
	public ResourceListTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<R> template, UpdateMode updateMode) {
		super(page, id, globalWidget, template, null, null, updateMode); 
	}
	
	public ResourceListTable(OgemaWidget parent, String id, OgemaHttpRequest req, RowTemplate<R> template,UpdateMode updateMode) {
		super(parent, id, req, template, null, null, updateMode);
	}
	
	@Override
	public ResourceListTableData<R> createNewSession() {
		return new ResourceListTableData<>(this, updateMode);
	}
	
	@Override
	public ResourceListTableData<R> getData(OgemaHttpRequest req) {
		return (ResourceListTableData<R>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DynamicTableData<R> opt) {
		super.setDefaultValues(opt);
		ResourceListTableData<R> opt2 = (ResourceListTableData<R>) opt;
		opt2.setList(list);
	}
	
	@Override
	public void setType(Class<? extends R> type, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("not supported by ResourceListTable");
	}
	
	public void setDefaultList(ResourceList<R> list) {
		this.list = list;
	}
	
	public void setList(ResourceList<R> list, OgemaHttpRequest req) {
		getData(req).setList(list);
	}
	
	public ResourceList<R> getList(OgemaHttpRequest req) {
		return getData(req).getList();
	}
	
}
