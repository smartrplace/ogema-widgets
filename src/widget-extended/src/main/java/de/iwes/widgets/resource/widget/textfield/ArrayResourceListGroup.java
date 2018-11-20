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
package de.iwes.widgets.resource.widget.textfield;

import java.util.Collection;

import org.ogema.core.model.array.ArrayResource;
import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.listgroup.ListGroup;
import de.iwes.widgets.html.listgroup.ListGroupData;
import de.iwes.widgets.template.WidgetTemplate;

public class ArrayResourceListGroup<A extends ArrayResource> extends ListGroup<String> implements ResourceSelector<A> {

	private static final long serialVersionUID = 1L;
	private A defaultSelectedArray;
	private final WidgetGroup arrayEntries;

	// we do not use the PageSnippetTemplate here
	public ArrayResourceListGroup(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget, (WidgetTemplate<String>) null);
		this.arrayEntries = page.registerWidgetGroup("arrayGroup_" + id);
	}
	
	@Override
	public ArrayResourceListGroupData<A> createNewSession() {
		return new ArrayResourceListGroupData<>(this, arrayEntries);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayResourceListGroupData<A> getData(OgemaHttpRequest req) {
		return (ArrayResourceListGroupData<A>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(ListGroupData<String> opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ArrayResourceListGroupData<A> opt2 = (ArrayResourceListGroupData<A>) opt;
		opt2.selectItem(defaultSelectedArray);
	}
	
	@Override
	public A getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}

	@Override
	public void selectItem(A item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}

	@Override
	public void selectDefaultItem(A item) {
		this.defaultSelectedArray = item;
	}
	
	/**
	 * Not supported.
	 */
	@Override
	public boolean addItem(String item, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("add item not supported by ArrayResourceListGroup");
	}
	
	/**
	 * Not supported.
	 */
	@Override
	public boolean removeItem(String resource, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("remove item not supported by ArrayResourceListGroup");
	}
	
	/**
	 * Not supported.
	 */
	@Override
	public void update(Collection<String> items, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("update not supported by ArrayResourceListGroup");
	}
	
}
