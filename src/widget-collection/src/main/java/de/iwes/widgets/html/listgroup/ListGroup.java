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

package de.iwes.widgets.html.listgroup;

import java.util.Collection;
import java.util.List;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.PageSnippetTemplate;

/**
 * Displays a list of items of type T. 
 *
 * @param <T>
 */
public class ListGroup<T> extends OgemaWidgetBase<ListGroupData<T>> implements TemplateWidget<T> {

	private static final long serialVersionUID = 1L;
	protected final PageSnippetTemplate<T> template;

	public ListGroup(WidgetPage<?> page, String id, PageSnippetTemplate<T> template) {
		this(page, id, false, template);
	}
	
	public ListGroup(WidgetPage<?> page, String id, boolean globalWidget, PageSnippetTemplate<T> template) {
		super(page, id, globalWidget);
		this.template = template;
		setDynamicWidget(true);
		addDefaultStyle(ListGroupData.BOOTSTRAP_LIST_GROUP);
	}
	
	public ListGroup(OgemaWidget parent, String id, PageSnippetTemplate<T> template, OgemaHttpRequest req) {
		super(parent,id,req);
		this.template = template;
		setDynamicWidget(true);
		addDefaultStyle(ListGroupData.BOOTSTRAP_LIST_GROUP);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return (Class) ListGroup.class;
	}

	@Override
	public ListGroupData<T> createNewSession() {
		return new ListGroupData<T>(this,template);
	}

	@Override
	public boolean addItem(T item, OgemaHttpRequest req) {
		return getData(req).addItem(item);
	}

	@Override
	public boolean removeItem(T resource, OgemaHttpRequest req) {
		return getData(req).removeItem(resource);
	}

	@Override
	public List<T> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}
	
	public void update(Collection<T> items, OgemaHttpRequest req) {
		getData(req).update(items);
	}

	/**
	 * Last selection by the user; may be null
	 * @return
	 */
	public T getSelected(OgemaHttpRequest req) {
		return getData(req).getSelected();
	}
	
}
