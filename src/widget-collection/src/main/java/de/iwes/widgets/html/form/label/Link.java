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
package de.iwes.widgets.html.form.label;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A link of the form
 * <code>
 * 	&lt;a href="https://localhost:8443/ogema/index.html"&gt; OGEMA start page &lt;/a&gt;
 * </code>
 */
public class Link extends OgemaWidgetBase<LinkData> {

	private static final long serialVersionUID = 1L;
	private String defaultText;
	private String defaultUrl;
	private boolean defaultNewTab;

	public Link(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public Link(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public Link(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return Link.class;
	}

	@Override
	public LinkData createNewSession() {
		return new LinkData(this);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "Link", "/ogema/widget/label/Link.js");
	}
	
	@Override
	protected void setDefaultValues(LinkData opt) {
		super.setDefaultValues(opt);
		opt.setText(defaultText);
		opt.setUrl(defaultUrl);
		opt.setNewTab(defaultNewTab);
	}
	
	/*
	 * Public methods below
	 */

	public String getText(OgemaHttpRequest req) {
		return getData(req).getText();
	}

	public void setText(String text, OgemaHttpRequest req) {
		getData(req).setText(text);
	}

	public String getUrl(OgemaHttpRequest req) {
		return getData(req).getUrl();
	}

	public void setUrl(String url, OgemaHttpRequest req) {
		getData(req).setUrl(url);
	}
	
	public void setNewTab(boolean newTab, OgemaHttpRequest req) {
		getData(req).setNewTab(newTab);
	}
	
	public boolean isNewTab(OgemaHttpRequest req) {
		return getData(req).isNewTab();
	}
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}
	
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
	
	public void setDefaultNewTab(boolean defaultNewTab) {
		this.defaultNewTab = defaultNewTab;
	}
}
