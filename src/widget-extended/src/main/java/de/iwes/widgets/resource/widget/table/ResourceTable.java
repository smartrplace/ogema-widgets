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

package de.iwes.widgets.resource.widget.table;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.resource.ResourceWidget;
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
 */
public class ResourceTable<R extends Resource> extends DynamicTable<R> implements ResourceWidget<R> {

	private static final long serialVersionUID = 1L;
	protected final UpdateMode updateMode;
	private final Class<R> type;
	private final ApplicationManager am;
	
	/*
	 *********** Constructors *********** 
	 */

	/**
	 * Requires resources to be set manually
	 * @param page
	 * @param id
	 * @param template
	 */
	public ResourceTable(WidgetPage<?> page, String id, RowTemplate<R> template) {
		this(page, id, false, template);
	}
	
	/**
	 * Requires resources to be set manually
	 * @param page
	 * @param id
	 * @param template
	 */
	public ResourceTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<R> template) {
		super(page,id, globalWidget);
		this.type = null;
		this.am = null;
		this.updateMode = UpdateMode.MANUAL;
		setRowTemplate(template);
	}
	
	/**
	 * Uses update mode {@link UpdateMode#AUTO_ON_GET}.
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param template
	 * @param type
	 * @param am
	 */
	public ResourceTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<R> template, Class<R> type, ApplicationManager am) {
		this(page, id, globalWidget, template, type, am, UpdateMode.AUTO_ON_GET);
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
	public ResourceTable(WidgetPage<?> page, String id, boolean globalWidget, RowTemplate<R> template, Class<R> type, ApplicationManager am, UpdateMode updateMode) {
		super(page, id, globalWidget);
		this.updateMode = updateMode;
		this.am = am;
		this.type = type;
		setRowTemplate(template);
	}

	public ResourceTable(OgemaWidget parent, String id, OgemaHttpRequest req, RowTemplate<R> template, Class<R> type, ApplicationManager am, UpdateMode updateMode) {
		super(parent, id, req);
		this.updateMode = updateMode;
		this.am = am;
		this.type = type;
		setRowTemplate(template);
	}
	
	/*
	 *********** inherited methods *********** 
	 */
	
	@Override
	public ResourceTableData<R> createNewSession() {
		return new ResourceTableData<>(this, type, updateMode, am);
	}
	
	@Override
	public ResourceTableData<R> getData(OgemaHttpRequest req) {
		return (ResourceTableData<R>) super.getData(req);
	}
	
	/*
	 *********** public methods *********** 
	 */

	@Override
	public UpdateMode getUpdateMode() {
		return updateMode;
	}

	@Override
	public void setType(Class<? extends R> type, OgemaHttpRequest req) {
		getData(req).setType(type);
	}
	
	@Override
	public Class<? extends R> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}
}
