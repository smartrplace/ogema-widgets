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
package de.iwes.widgets.resource.widget.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.ogema.core.model.array.ArrayResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.complextable.RowTemplate;

/**
 * Displays the values of an {@link ArrayResource} in tabular form, but does not allow to edit them.
 * @param <T>
 */
public class ArrayResourceTable<T extends ArrayResource> extends DynamicTable<String> implements ResourceSelector<T> {

	private static final long serialVersionUID = 1L;
	private T defaultSelected = null;

	public ArrayResourceTable(WidgetPage<?> page, String id) {
		super(page, id);
		setRowTemplate(getArrayTemplate());
	}

	@Override
	public ArrayResourceTableData createNewSession() {
		return new ArrayResourceTableData(this);
	}
	
	@Override
	public ArrayResourceTableData getData(OgemaHttpRequest req) {
		return (ArrayResourceTableData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DynamicTableData<String> opt) {
		super.setDefaultValues(opt);
		ArrayResourceTableData opt2 = (ArrayResourceTable<T>.ArrayResourceTableData) opt;
		opt2.selectItem(defaultSelected);
	}
	
	protected class ArrayResourceTableData extends DynamicTableData<String> {

		private T resource;
		
		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			counter.set(0);
			clear();
			if (resource != null) {
				List<String> items = new ArrayList<>();
				Object[] result = (Object[]) ValueResourceUtils.getValue(resource);
				for (Object obj:result) {
					items.add(obj.toString());
				}
				updateRows(items);
				
			}
			return super.retrieveGETData(req);
		}
		
		public ArrayResourceTableData(ArrayResourceTable<T> table) {
			super(table);
		}
		
		public T getSelectedItem() {
			return resource;
		}


		public void selectItem(T item) {
			this.resource = item;
		}
		
	}

	@Override
	public T getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}

	@Override
	public void selectItem(T item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}

	@Override
	public void selectDefaultItem(T item) {
		this.defaultSelected = item;
	}
	
	private final ThreadLocal<Integer> counter = new ThreadLocal<Integer>() {
		
		@Override
		protected Integer initialValue() {
			return 0;
		}
		
	};
	
	protected RowTemplate<String> getArrayTemplate() {
		return new RowTemplate<String>() {
		
			@Override
			public Row addRow(String object, OgemaHttpRequest req) {
				Row row = new Row();
				row.addCell("column",object);
				return row;
			}
	
			@Override
			public String getLineId(String object) {
				int cnt = counter.get();
				counter.set(cnt + 1);
				return cnt + "";
			}
	
			// TODO
			@Override
			public Map<String, Object> getHeader() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
	};
	
}
