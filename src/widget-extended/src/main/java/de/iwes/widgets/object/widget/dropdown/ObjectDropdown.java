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
package de.iwes.widgets.object.widget.dropdown;

import java.util.LinkedHashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.template.DisplayTemplate;

public class ObjectDropdown<T> extends TemplateDropdown<T> {
	private static final long serialVersionUID = 1L;
	
	private final Map<T, String> valuesDisplayed = new LinkedHashMap<T, String>();

	public ObjectDropdown(WidgetPage<?> page, String id, Map<T, String> valuesDisplayed) {
		super(page, id);
		this.valuesDisplayed.putAll(valuesDisplayed);
		initWidget();
	}

	public ObjectDropdown(WidgetPage<?> page, String id, boolean globalWidget, Map<T, String> valuesDisplayed) {
		super(page, id, globalWidget);
		this.valuesDisplayed.putAll(valuesDisplayed);
		initWidget();
	}
	
	public ObjectDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, Map<T, String> valuesDisplayed) {
		super(parent, id, req);
		this.valuesDisplayed.putAll(valuesDisplayed);
		initWidget();
	}
	
	/**Add all values of an additional map to the potential values that can be displayed by the widget.
	 * Perform this operation before the widget is accessed by a client as this is not thread-safe
	 * 
	 * @param valuesDisplayedOption options that shall be avilable to be displayed
	 */
	public void addOptionalMap(Map<T, String> valuesDisplayedOption) {
		valuesDisplayed.putAll(valuesDisplayedOption);
	}
	/**Update the values displayed to the map. Note that all values in the map need to be registered
	 * in the constructor or in a call to {@link #addOptionalMap(Map)}
	 * @param valuesDisplayed values to be displayed for the session
	 */
	public void updateValuesDisplayed(Map<T, String> valuesDisplayed, OgemaHttpRequest req) {
		update(valuesDisplayed.keySet(), req);		
	}
	
	private void initWidget() {
		setComparator(null);
		final DisplayTemplate<T> intervalDisplayTemplate = new DisplayTemplate<T>() {
	
			@Override
			public String getId(T object) {
				return String.valueOf(object);
			}
	
			@Override
			public String getLabel(T object, OgemaLocale locale) {
				String result = valuesDisplayed.get(object);
				if(result != null) return result;
				return "should not occur";
			}
		};
		setTemplate(intervalDisplayTemplate);
		setDefaultItems(valuesDisplayed.keySet());
	}
}
