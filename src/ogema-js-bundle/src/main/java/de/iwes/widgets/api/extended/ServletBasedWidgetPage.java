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
package de.iwes.widgets.api.extended;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

public interface ServletBasedWidgetPage<D extends LocaleDictionary> extends WidgetPage<D> {
	
	public final static boolean DEBUG  = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
		
		@Override
		public Boolean run() {
			return Boolean.getBoolean("org.ogema.widgets.debug");
		}
		
	});

	
	// we need not add these widget scripts to the html, since they are preloaded in minified form anyway (unless DEBUG is true)
	public static final Map<String,String> PRELOADED_WIDGET_SCRIPTS = Arrays.stream(new String[] {
			"Alert=/ogema/widget/alert/Alert.js",
			"Accordion=/ogema/widget/accordion/Accordion.js",
			"EmptyWidget=/ogema/widget/emptywidget/EmptyWidget.js",
			"Button=/ogema/widget/button/Button.js",
			"Checkbox=/ogema/widget/checkbox/Checkbox.js",
			"Dropdown=/ogema/widget/dropdown/Dropdown.js",
			"Label=/ogema/widget/label/Label.js",
			"TextField=/ogema/widget/textfield/TextField.js",
			"Flexbox=/ogema/widget/html5/Flexbox.js",
			"Icon=/ogema/widget/icon/Icon.js",
			"Multiselect=/ogema/widget/multiselect/Multiselect.js",
			"Popup=/ogema/widget/popup/Popup.js"
		})
		.map(string -> string.split("="))
		.collect(Collectors.toMap(a -> a[0], a -> a[1]));

	HttpServlet getServlet();
	void registerLibrary(HtmlLibrary lib);
	
}
