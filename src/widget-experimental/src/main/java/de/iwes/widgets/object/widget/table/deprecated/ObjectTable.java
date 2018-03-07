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
	 * Select update mode
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param template
	 * @param type
	 * @param am
	 * @param updateMode
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
