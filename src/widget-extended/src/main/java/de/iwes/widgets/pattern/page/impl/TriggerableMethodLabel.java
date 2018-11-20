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
