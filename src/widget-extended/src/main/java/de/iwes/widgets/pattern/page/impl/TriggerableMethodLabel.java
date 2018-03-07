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

package de.iwes.widgets.pattern.page.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.json.JSONObject;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.label.LabelData;

public class TriggerableMethodLabel extends Label {

	private static final long serialVersionUID = 1L;
	private final Method method;

	public TriggerableMethodLabel(WidgetPage<?> page, String id, Method method) {
		super(page, id, "");
		this.method = method;
	}
	
	@Override
	public TriggerableMethodLabelOptions createNewSession() {
		return new TriggerableMethodLabelOptions(this);
	}
	
	@Override
	public TriggerableMethodLabelOptions getData(OgemaHttpRequest req) {
		return (TriggerableMethodLabelOptions) super.getData(req);
	}
	
	private class TriggerableMethodLabelOptions extends LabelData {

		private String message = "";
		private ResourcePattern<?> selectedPattern = null;
		
		public TriggerableMethodLabelOptions(TriggerableMethodLabel tml) {
			super(tml);
		}
		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			setText(message);
			return super.retrieveGETData(req);
		}
		
		public void setMessage(String message) {
			this.message = message;
		}
		
		public void setMessage() {
			String newText = "";
			if (selectedPattern != null || Modifier.isStatic(method.getModifiers())) {
				try {
					newText = method.invoke(selectedPattern).toString(); // TODO other String conversion method
				} catch (Exception e) {
				}
			}
			this.message = newText;
		}
		
		public String getMessage() {
			return message;
		}
		
		public void setPattern(ResourcePattern<?> selectedPattern) {
			this.selectedPattern = selectedPattern;
		}
		
		public ResourcePattern<?> getPattern() {
			return selectedPattern;
		}
	}
	
	public void setMessage(OgemaHttpRequest req) {
		getData(req).setMessage();
	}

	public void setMessage(String message, OgemaHttpRequest req) {
		getData(req).setMessage(message);
	}
	
	public String getMessage(OgemaHttpRequest req) {
		return getData(req).getMessage();
	}
	
	public void setPattern(ResourcePattern<?> selectedPattern, OgemaHttpRequest req) {
		getData(req).setPattern(selectedPattern);
	}
	
	public ResourcePattern<?> getPattern(OgemaHttpRequest req) {
		return getData(req).getPattern();
	}
	
}
