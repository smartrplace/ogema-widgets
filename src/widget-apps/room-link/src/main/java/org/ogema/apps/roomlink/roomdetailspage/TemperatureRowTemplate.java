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
package org.ogema.apps.roomlink.roomdetailspage;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.apps.roomlink.localisation.roomdetails.RoomDetailsDictionary;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.sensors.TemperatureSensor;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.pattern.widget.table.DefaultPatternRowTemplate;

public class TemperatureRowTemplate extends DefaultPatternRowTemplate<TemperatureSensorPattern> {
	
	private final WidgetPage<RoomDetailsDictionary> page;
	private final NameService nameService;
	
	public TemperatureRowTemplate(WidgetPage<RoomDetailsDictionary> page, NameService nameService) {
		this.page = page;
		this.nameService = nameService;
	}

	@Override
	public Row addRow(final TemperatureSensorPattern pattern, OgemaHttpRequest req) {
		String id = pattern.model.getLocation();
		Row row = new Row();
		
		String name = nameService.getName(pattern.model, req.getLocale(),true,true);
		if (name == null) 
			name = pattern.model.getLocation();
		Label tempSensName = new Label(page, "tempSensName_" + id,name,req);
		row.addCell("tempSensNameCol", tempSensName, 2);
		
		Label tempSensValue = new Label(page, "tempSensValue_" + id,"current temperature",req) {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				TemperatureResource tr = ((TemperatureSensor) pattern.model).reading();
				if (!tr.isActive()) 
					setText("n.a.", req);
				else
					setText(tr.getCelsius() + "°C", req);
			};
			
		};
		tempSensValue.setDefaultPollingInterval(5000);
		row.addCell("tempSensValueCol", tempSensValue, 1);
		
		return row;
	}

	@Override
	public Map<String, Object> getHeader() {
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		RoomDetailsDictionary dict = page.getDictionary("en");
		map.put("tempSensNameCol",dict.tempSensorNameTableHeader());
		map.put("tempSensValueCol",dict.tempSensorValueTableHeader());
		return map;
	}
	

}
