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
package de.iwes.widgets.api.extended.html.fragment;

import java.util.Collection;
import java.util.List;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget whose content
 */
public class FragmentWidget extends OgemaWidgetBase<FragmentData> implements PageSnippetI {
	
	private static final long serialVersionUID = 1L;

	{
		addDefaultCssStyle("display", "none");
		setDynamicWidget(true);
	}
	
	public FragmentWidget(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public FragmentWidget(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	public FragmentWidget(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	@Override
	public FragmentData createNewSession() {
		return new FragmentData(this);
	}
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return FragmentWidget.class;
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "FragmentWidget", "/ogema/widget/fragment/FragmentWidget.js");
	}
	
	public void updateItems(Collection<Object> items, OgemaHttpRequest req) {
		getData(req).updateItems(items);
	}
	
	public void addItem(Object item, OgemaHttpRequest req) {
		getData(req).addItem(item);
	}
	
	public void removeItem(Object item, OgemaHttpRequest req) {
		getData(req).removeItem(item);
	}

	@Override
	public PageSnippetI append(String text, OgemaHttpRequest req) {
		addItem(text, req);
		return this;
	}

	@Override
	public PageSnippetI append(HtmlItem html, OgemaHttpRequest req) {
		addItem(html, req);
		return this;
	}

	@Override
	public PageSnippetI append(OgemaWidget widget, OgemaHttpRequest req) {
		addItem(widget, req);
		return this;
	}

	@Override
	public PageSnippetI linebreak(OgemaHttpRequest req) {
		addItem(HtmlItem.LINEBREAK, req);
		return this;
	}
	
	public List<OgemaWidget> getSubwidgets(OgemaHttpRequest req) {
		return getData(req).getSubwidgets();
	}
	
	@Override
	public PageSnippetI remove(HtmlItem item,OgemaHttpRequest req) {
		getData(req).removeItem(item);
		return this;
	}
	
	@Override
	public PageSnippetI remove(OgemaWidget widget,OgemaHttpRequest req) {
		getData(req).removeItem(widget);
		return this;
	}

}
