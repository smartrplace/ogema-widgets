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

package org.ogema.widgets.trigger.level.test.gui;

import java.util.LinkedHashSet;
import java.util.Set;
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

public class StartPageBuilder {

	private static final Set<DropdownOption> dropdownOptions;
	
	static {
		dropdownOptions = new LinkedHashSet<>();
		dropdownOptions.add(new DropdownOption("opt1", "1-9", true));
		dropdownOptions.add(new DropdownOption("opt2", "10-99", false));
		dropdownOptions.add(new DropdownOption("opt3","100-999",false));
		dropdownOptions.add(new DropdownOption("opt4","1000-9999",false));
		dropdownOptions.add(new DropdownOption("not_selected","not selected",false));
	}
	
	private final Alert alert;
	private final TextField startValue;
	private final TextField endValue;
	
	public StartPageBuilder(WidgetPage<?> page) {
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
