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
package de.iwes.widgets.html.chart;

import java.util.HashSet;

import org.json.JSONArray;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public abstract class C3Chart extends OgemaWidgetBase<C3ChartData> {

	private static final long serialVersionUID = 3719597065026765373L;
	private final ChartType type;
	
	/************* constructor **********************/

	public C3Chart(WidgetPage<?> page, String id) {
		this(page, id, null);
	}

	public C3Chart(WidgetPage<?> page, String id, ChartType type) {
		super(page, id);
		this.type = type;
	}
	
	/** session independent */
	public C3Chart(WidgetPage<?> page, String id, ChartType type, boolean globalWidget) {
		super(page, id, globalWidget);
		this.type = type;
	}
	

	/******* Inherited methods ******/
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return C3Chart.class;
	}

	@Override
	public C3ChartData createNewSession() {
		C3ChartData opt = new C3ChartData(this,type);
		return opt;
 	}
	
	/******** public methods ***********/

	public JSONArray getChartConfig(OgemaHttpRequest req) {
		return getData(req).getChartConfig();
	}

	public HashSet<C3DataRow> getDataRows(OgemaHttpRequest req) {
		return getData(req).getDataRows();
	}
	
	public enum ChartType {
		
		LINE("line"), 
		GAUGE("gauge"),
		PIE("pie"),
		DONUT("donut"),
		BAR("bar");
		
		public final String name;
		
		ChartType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	public void changed(OgemaHttpRequest req) {
		getData(req).changed();
	}

	public void add(C3DataRow row, OgemaHttpRequest req) {
		getData(req).add(row);
	}

	public void remove(C3DataRow row, OgemaHttpRequest req) {
		getData(req).remove(row);
	}
}
