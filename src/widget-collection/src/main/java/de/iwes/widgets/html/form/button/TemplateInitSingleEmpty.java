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
package de.iwes.widgets.html.form.button;

import java.util.Map;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

public abstract class TemplateInitSingleEmpty<T> extends EmptyWidget implements InitWidget {
	protected abstract T getItemById(String configId);
	
	private static final long serialVersionUID = 1L;
	private T defaultSelected = null;
	
	public TemplateInitSingleEmpty(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public TemplateInitSingleEmpty(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent,id, req);
	}
	
	public class PatternInitSingleEmptyOptions extends EmptyData {
		
		private volatile T selectedResource;

		public PatternInitSingleEmptyOptions(TemplateInitSingleEmpty<T> empty) {
			super(empty);
		}

		public T getSelectedItem() {
			return selectedResource;
		}

		public void selectItem(T item) {
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
	
	public T getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}

	public void selectItem(T item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}

	public void selectDefaultItem(T item) {
		this.defaultSelected = item;
	}

	// select the object
	@Override
	public void init(OgemaHttpRequest req) {
		Map<String,String[]> params = getPage().getPageParameters(req);
		if (params == null || params.isEmpty())
			return;
		String[] patterns = params.get(TemplateRedirectButton.PAGE_CONFIG_PARAMETER);
		if (patterns == null || patterns.length == 0)
			return;
		final String selected = patterns[0];
		T res = null;
		try {
			res = getItemById(selected); // may return null or throw an exception
		} catch (Exception e) { // if the type does not match
			LoggerFactory.getLogger(TemplateInitSingleEmpty.class).info("Empty template widget could not be initialized with the selected value {}",selected,e);
		}
		if (res == null)
			return;
		getData(req).selectItem(res);
	}
	
	
	
}

