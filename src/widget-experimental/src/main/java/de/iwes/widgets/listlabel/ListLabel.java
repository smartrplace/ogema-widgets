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
package de.iwes.widgets.listlabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.label.LabelData;

public class ListLabel extends Label {
	
	private static final long serialVersionUID = 1L;
	private List<String> defaultValues = null;

	public ListLabel(WidgetPage<?> page, String id) {
		super(page, id);
	}

	public ListLabel(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	public ListLabel(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	@Override
	public ListLabelData createNewSession() {
		return new ListLabelData(this);
	}
	
	@Override
	public ListLabelData getData(OgemaHttpRequest req) {
		return (ListLabelData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(LabelData opt) {
		super.setDefaultValues(opt);
		if (defaultValues != null)
			((ListLabelData) opt).setValues(defaultValues);
	}
	
	public void setDefaultValues(List<String> values) {
		this.defaultValues = values == null ? null : new ArrayList<>(values);
	}
	
	public void setValues(List<String> values, OgemaHttpRequest req) {
		getData(req).setValues(values);
	}
	
	public void addValue(String value, OgemaHttpRequest req) {
		getData(req).addValue(value);
	}
	
	public void removeValue(String value, OgemaHttpRequest req) {
		getData(req).removeValue(value);
	}
	
	public void clear(OgemaHttpRequest req) {
		getData(req).clear();
	}
	
	public void setTooltips(Map<String,String> values, OgemaHttpRequest req) {
		getData(req).setTooltips(values);
	}
	
	public void addTooltip(String key, String tooltip, OgemaHttpRequest req) {
		getData(req).addTooltip(key, tooltip);
	}
	
	@Override
	public void setDefaultText(String defaultText) {
		throw new UnsupportedOperationException("Not supported by ListLabel");
	}
	
	@Override
	public void setText(String text, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ListLabel");
	}
	
}
