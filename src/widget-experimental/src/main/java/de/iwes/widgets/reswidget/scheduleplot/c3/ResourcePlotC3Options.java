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
package de.iwes.widgets.reswidget.scheduleplot.c3;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotc3.C3DataSet;
import de.iwes.widgets.html.plotc3.PlotC3Options;

public class ResourcePlotC3Options extends PlotC3Options {
	
	private final ResourceDataC3 resourceData = new ResourceDataC3();

	/***** Constructor ****/
	
	public ResourcePlotC3Options(ResourcePlotC3 plot) {
		super(plot);
	}

	/*** Inherited methods ***/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,C3DataSet> backup = new LinkedHashMap<String, C3DataSet>(dataSets);
		dataSets.putAll(resourceData.getAllDataSets());
		JSONObject result = super.retrieveGETData(req);
		dataSets.clear();
		dataSets.putAll(backup);
		return result;
	}
	
	
	/**** Public methods ***/
	
	public ResourceDataC3 getResourceData() {
		return resourceData;
	}
	
	
	
}
