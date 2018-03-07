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
