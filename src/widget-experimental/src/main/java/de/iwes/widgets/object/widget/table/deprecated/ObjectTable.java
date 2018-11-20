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
package de.iwes.widgets.object.widget.table.deprecated;

import java.util.List;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;

/**
 * A table that displays one row per resource. The list of resources to be displayed can be either
 * determined by the table itself (then it shows all resources of a given type), or can be set explicitly.
 * See {@link UpdateMode}.
 *  
 * @param <R>
 * 		the resource type
 * @deprecated use DynamicTable instead
 */
@Deprecated
public class ObjectTable<R> extends DynamicTable<R> {

	private static final long serialVersionUID = 1L;
	List<R> objectList;
	
	/*
	 *********** Constructors *********** 
	 */

	/**
	 * Requires resources to be set manually
	 * @param page
	 * @param id
	 * @param template
	 */
	public ObjectTable(WidgetPage<?> page, String id, RowTemplate<R> template) {
		this(page, id, false, template);
	}
	
	/**
	 * Requires resources to be set manually
	 * @param page
	 * @param id
	 * @param template
	 */
	public ObjectTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<R> template) {
		super(page,id, globalWidget);
		setRowTemplate(template);
	}
	/**
	 * 
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param template
	 * @param objectList
	 */
	public ObjectTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<R> template, List<R> objectList) {
		super(page, id, globalWidget);
		this.objectList = objectList;
		setRowTemplate(template);
	}

	public ObjectTable(OgemaWidget parent, String id, OgemaHttpRequest req, RowTemplate<R> template, List<R> objectList) {
		super(parent, id, req);
		this.objectList = objectList;
		setRowTemplate(template);
	}
	
	/*
	 *********** inherited methods *********** 
	 */
	
	@Override
	public ObjectTableData<R> createNewSession() {
		return new ObjectTableData<>(this, objectList);
	}
	
	@Override
	public ObjectTableData<R> getData(OgemaHttpRequest req) {
		return (ObjectTableData<R>) super.getData(req);
	}
	
	 /*********** public methods *********** 
	 */

	public void setList(List<? extends R> objectList, OgemaHttpRequest req) {
		getData(req).setList(objectList);
	}
	
	public List<? extends R> setList(OgemaHttpRequest req) {
		return getData(req).getList();
	}
}
