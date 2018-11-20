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
package de.iwes.widgets.test4.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.resource.util.ResourceUtils;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.label.Header;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=/de/fh/iee/widgets/test4",
				LazyWidgetPage.RELATIVE_URL + "=page2.html"
		}
)
public class Page2Builder implements LazyWidgetPage {
	
	@SuppressWarnings("serial")
	@Override
	public void init(final ApplicationManager appMan, final WidgetPage<?> page) {
		final Header header = new Header(page, "header", "Widget test 4 page 2");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		header.setDefaultColor("blue");
		final Alert alert = new Alert(page, "alert", "This page display all available temperature sensors connected to the OGEMA gateway");
		alert.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
		final DynamicTable<TemperatureSensor> table = new DynamicTable<TemperatureSensor>(page, "table") {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				updateRows(appMan.getResourceAccess().getResources(TemperatureSensor.class), req);
			}
			
		};
		table.setRowTemplate(new RowTemplate<TemperatureSensor>() {
			
			final Map<String, Object> header;
			
			{
				final Map<String, Object> header = new LinkedHashMap<>(4);
				header.put("path", "Resource path");
				header.put("name", "Name");
				header.put("loc", "Location");
				this.header = Collections.unmodifiableMap(header);
			}
			
			@Override
			public String getLineId(TemperatureSensor object) {
				return object.getPath();
			}
			
			@Override
			public Map<String, Object> getHeader() {
				return header;
			}
			
			@Override
			public Row addRow(TemperatureSensor object, OgemaHttpRequest req) {
				final Row row = new Row();
				row.addCell("path", object.getPath());
				row.addCell("name", ResourceUtils.getHumanReadableName(object));
				final Room location = ResourceUtils.getDeviceRoom(object);
				row.addCell("loc", location != null ? ResourceUtils.getHumanReadableName(location) : "n.a.");
				return row;
			}
		});
		
		page.append(header).linebreak()
			.append(alert).linebreak()
			.append(table);
		
	}
	
}
