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
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package com.example.app.template.gui;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ResourceUtils;

import com.example.app.template.pattern.TemplatePattern;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.pattern.widget.dropdown.PatternDropdown;
import de.iwes.widgets.resource.widget.textfield.ValueResourceTextField;

public class MainPagePattern {
	
	public final long UPDATE_RATE = 5*1000;
	private final WidgetPage<?> page; 
	
	public MainPagePattern(final WidgetPage<?> page, final ApplicationManager appMan) {
		this.page = page;

		Header header = new Header(page, "header", "Template Page");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);

		//init all widgets
		final ValueResourceTextField<StringResource> editDestinationTemp = new  ValueResourceTextField<>(page, "editDestinationTemp");
		final Label roomName = new Label(page, "roomName");
		//final ResourceLabel<TemperatureResource> currentTemperature = new ResourceLabel<>(page, "currentTemperature");
		//currentTemperature.setDefaultPollingInterval(UPDATE_RATE);
		
		final PatternDropdown<TemplatePattern> dropProgram =
    			new PatternDropdown<TemplatePattern>(page, "dropProgram", TemplatePattern.class, appMan.getResourcePatternAccess()) {
 			private static final long serialVersionUID = 8696145677385119466L;

 			@Override
    		public void updateDependentWidgets(OgemaHttpRequest req) {
 				TemplatePattern c = getSelectedItem(req);
 	     		if(c == null) {
 	 				editDestinationTemp.selectItem(null, req);
 	 	     		roomName.setText("No device selected", req);
 	 				//currentTemperature.selectItem(null, req);
 				} else {
 					editDestinationTemp.selectItem(c.name, req);
 					roomName.setText("Room name: " + ResourceUtils.getHumanReadableName(ResourceUtils.getDeviceLocationRoom(c.model)), req);
 	 				//currentTemperature.selectItem(c.model.reading(), req);
 				}
    		}
    	};
		page.append(header);
    	dropProgram.registerDependentWidget(editDestinationTemp);
		page.append(dropProgram);
		StaticTable table1 = new StaticTable(2, 2);
		page.append(table1);
		table1.setContent(0, 0, roomName);
		table1.setContent(0, 1, editDestinationTemp);
		//table1.setContent(1, 0, "Measurement value :");
		//table1.setContent(1, 1, currentTemperature);
	}
	
	public WidgetPage<?> getPage() {
		return page;
	}
}
