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
package de.iwes.widgets.resource.widget.init;

import java.util.Map;
import java.util.Objects;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

/**
 * A widget that does not provide any Html, but can extract parameters from the page URL, and select a corresponding resource
 * of the specified type. This can then be used to initialize other widgets on the page.
 *
 * @param <R>
 */
public class ResourceInitSingleEmpty<R extends Resource> extends EmptyWidget implements InitWidget, ResourceSelector<R> {

	private static final long serialVersionUID = 1L;
	private final ApplicationManager am;
	private R defaultSelected = null;
	private final boolean allowInactive;
	
	public ResourceInitSingleEmpty(WidgetPage<?> page, String id, boolean allowInactive, ApplicationManager am) {
		this(page, id, false, allowInactive,am);
	}

	public ResourceInitSingleEmpty(WidgetPage<?> page, String id, boolean globalWidget, boolean allowInactive, ApplicationManager am) {
		super(page, id, globalWidget);
		Objects.requireNonNull(am);
		this.am=am;
		this.allowInactive = allowInactive;
	}
	
	public ResourceInitSingleEmpty(OgemaWidget parent, String id, boolean allowInactive, OgemaHttpRequest req, ApplicationManager am) {
		super(parent,id, req);
		Objects.requireNonNull(am);
		this.am =am;
		this.allowInactive = allowInactive;
	}
	
	public class PatternInitSingleEmptyOptions extends EmptyData {
		
		private volatile R selectedResource;

		public PatternInitSingleEmptyOptions(ResourceInitSingleEmpty<R> empty) {
			super(empty);
		}

		public R getSelectedItem() {
			return selectedResource;
		}

		public void selectItem(R item) {
			this.selectedResource = item;
		}
		
	}

	@Override
	public PatternInitSingleEmptyOptions createNewSession() {
		return new PatternInitSingleEmptyOptions(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PatternInitSingleEmptyOptions getData(OgemaHttpRequest req) {
		return (PatternInitSingleEmptyOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(EmptyData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		PatternInitSingleEmptyOptions opt2= (PatternInitSingleEmptyOptions) opt;
		opt2.selectItem(defaultSelected);
	}
	
	@Override
	public R getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}

	@Override
	public void selectItem(R item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}

	@Override
	public void selectDefaultItem(R item) {
		this.defaultSelected = item;
	}

	// select the resource
	@Override
	public void init(OgemaHttpRequest req) {
		Map<String,String[]> params = getPage().getPageParameters(req);
		if (params == null || params.isEmpty())
			return;
		String[] patterns = params.get(TemplateRedirectButton.PAGE_CONFIG_PARAMETER);
		if (patterns == null || patterns.length == 0)
			return;
//		final String selected = patterns[0].replace('_', '/');
		final String selected = patterns[0];
		R res = null;
		try {
			res = am.getResourceAccess().getResource(selected); // may return null or throw an exception
		} catch (Exception e) { // if the type does not match
			am.getLogger().info("Empty resource widget could not be initialized with the selected value {}",selected,e);
		}
		if (res == null || (!allowInactive && !res.isActive()) || !res.exists())
			return;
		am.getLogger().debug("Initializing empty resource widget with resource {}",res);
		getData(req).selectItem(res);
	}
	
	
	
}
