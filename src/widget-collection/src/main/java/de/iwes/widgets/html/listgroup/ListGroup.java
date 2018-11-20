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
package de.iwes.widgets.html.listgroup;

import java.util.Collection;
import java.util.List;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.PageSnippetTemplate;
import de.iwes.widgets.template.WidgetTemplate;

/**
 * Displays a list of items of type T. 
 *
 * @param <T>
 */
public class ListGroup<T> extends OgemaWidgetBase<ListGroupData<T>> implements TemplateWidget<T> {

	private static final long serialVersionUID = 1L;
	@Deprecated
	protected final PageSnippetTemplate<T> template;
	protected final WidgetTemplate<T> widgetTemplate;
	
	{
		setDynamicWidget(true);
		addDefaultStyle(ListGroupData.BOOTSTRAP_LIST_GROUP);
	}

	public ListGroup(WidgetPage<?> page, String id, WidgetTemplate<T> template) {
		this(page, id, false, template);
	}
	
	public ListGroup(WidgetPage<?> page, String id, boolean globalWidget, WidgetTemplate<T> template) {
		super(page, id, globalWidget);
		this.template = null;
		this.widgetTemplate = template;
	}
	
	public ListGroup(OgemaWidget parent, String id, OgemaHttpRequest req, WidgetTemplate<T> template) {
		super(parent, id, req);
		this.widgetTemplate = template;
		this.template = null;
	}
	
	@Deprecated
	public ListGroup(WidgetPage<?> page, String id, PageSnippetTemplate<T> template) {
		this(page, id, false, template);
	}
	
	@Deprecated
	public ListGroup(WidgetPage<?> page, String id, boolean globalWidget, PageSnippetTemplate<T> template) {
		super(page, id, globalWidget);
		this.template = template;
		this.widgetTemplate = null;
	}
	
	@Deprecated
	public ListGroup(OgemaWidget parent, String id, PageSnippetTemplate<T> template, OgemaHttpRequest req) {
		super(parent,id,req);
		this.template = template;
		this.widgetTemplate = null;
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
		return new ListGroupData<T>(this,template, widgetTemplate);
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
