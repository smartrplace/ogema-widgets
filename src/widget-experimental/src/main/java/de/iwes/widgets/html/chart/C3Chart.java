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
