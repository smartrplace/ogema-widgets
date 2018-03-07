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

