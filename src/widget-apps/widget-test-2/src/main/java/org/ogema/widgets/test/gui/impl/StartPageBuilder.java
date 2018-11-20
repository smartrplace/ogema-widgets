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
package org.ogema.widgets.test.gui.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=index.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=Start page"
		}
)
public class StartPageBuilder implements LazyWidgetPage {

	private static final Set<DropdownOption> dropdownOptions;
	
	static {
		dropdownOptions = new LinkedHashSet<>();
		dropdownOptions.add(new DropdownOption("opt1", "1-9", true));
		dropdownOptions.add(new DropdownOption("opt2", "10-99", false));
		dropdownOptions.add(new DropdownOption("opt3","100-999",false));
		dropdownOptions.add(new DropdownOption("opt4","1000-9999",false));
		dropdownOptions.add(new DropdownOption("not_selected","not selected",false));
	}
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new StartPageInit(page);
	}
	
	private static class StartPageInit {
	
		private final Alert alert;
		private final TextField startValue;
		private final TextField endValue;
		
		StartPageInit(WidgetPage<?> page) {
			/**
			 * Page header
			 */
			Header header = new Header(page, "header");
			header.setDefaultText("This is a widget test page");
			header.addDefaultStyle(HeaderData.CENTERED);
			page.append(header).linebreak();
			
			/**
			 * Alert: displays messages to the user. Initially this is hidden, but
			 * will be shown if you click the "Submit" button 
			 */
			alert = new Alert(page, "alert", "");
			alert.setDefaultVisibility(false);
			page.append(alert).linebreak();
			
			/**
			 * A table with a fixed number of rows. Since its cells contain widgets (set via table.setContent() below),
			 * hence their content may change.
			 */
			StaticTable table = new StaticTable(4, 2, new int[]{2,2});
			Label selectorLabel = new Label(page, "selectorLabel","Select range");
			final Dropdown dd = new Dropdown(page, "rangeSelector");
			dd.setDefaultOptions(dropdownOptions);
			Label startValueLabel = new Label(page, "startValueLabel","Start value");
			startValue = new TextField(page, "startValue") {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					String label = dd.getSelectedLabel(req);
					if (label == "not selected")
						setValue("", req);
					else 
						setValue(label.split("-")[0],req);
				}
				
				@Override
				public boolean valueAdmissible(String newValue, OgemaHttpRequest req) {
					int endV = -1;
					try {
						endV = Integer.parseInt(endValue.getValue(req));
					} catch (NumberFormatException e) {} 
					try {
						int nr = Integer.parseInt(newValue);
						if (nr >= endV)  
							throw new NumberFormatException();
						return true;
					} catch (NumberFormatException e) {
						alert.showAlert("Please enter a non-negative number, smaller than the end value", false, req);
						return false; 
					}
				}
				
			};
			Label endValueLabel = new Label(page, "endValueLabel","End value");
			endValue = new TextField(page, "endValue") {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					String label = dd.getSelectedLabel(req);
					if (label == "not selected")
						setValue("", req);
					else 
						setValue(label.split("-")[1],req);
				}
				
				@Override
				public boolean valueAdmissible(String newValue, OgemaHttpRequest req) {
					int startV = -1;
					try {
						startV = Integer.parseInt(startValue.getValue(req));
					} catch (NumberFormatException e) {} 
					try {
						int nr = Integer.parseInt(newValue);
						if (nr <= startV)
							throw new NumberFormatException();
						return true;
					} catch (NumberFormatException e) {
						alert.showAlert("Please enter a non-negative number, greater than the start value", false, req);
						return false; 
					}
				}
				
			};
			startValue.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			endValue.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			Label rangeViewLabel =  new Label(page, "rangeViewLabel","Range:");
			Label rangeView = new Label(page, "rangeView", "") {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					String start = startValue.getValue(req);
					String end = endValue.getValue(req);
					if (start == null || start.isEmpty() || end == null || end.isEmpty()) {
						setText("n.a.", req);
						return;
					}
					setText(start + " - " + end, req);
				}
				
			};
			table.setContent(0, 0, selectorLabel).setContent(0, 1, dd)
				.setContent(1, 0, startValueLabel).setContent(1, 1, startValue)
				.setContent(2, 0, endValueLabel).setContent(2, 1, endValue)
				.setContent(3, 0, rangeViewLabel).setContent(3, 1, rangeView);
			
			page.append(table).linebreak();
			
			WidgetGroup rangeGroup = page.registerWidgetGroup("rangeGroup");
			rangeGroup.addWidget(startValue);
			rangeGroup.addWidget(endValue);
			dd.triggerAction(rangeGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			dd.triggerAction(rangeView, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
			startValue.triggerAction(rangeView, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			endValue.triggerAction(rangeView, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			
		}
		
	}
}