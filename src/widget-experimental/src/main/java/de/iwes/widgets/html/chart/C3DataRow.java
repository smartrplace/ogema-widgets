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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class C3DataRow {

	private String name;
	private Collection<?> xs = new ArrayList<Object>();
	private Collection<?> ys = new ArrayList<Object>();
	private DataRowType type = null;
	private C3Chart chart;
	private int temp = 0;

	public enum DataRowType {

		LINE("line"), AREA_LINE("area"), SPLINE("spline"), AREA_SPLINE(
				"area-spline"), STEP("step"), AREA_STEP("area-step"), BAR("bar");

		public final String name;

		DataRowType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private C3DataRow addValue(Collection xy, Object y) {

//		ArrayList<Object> array = (ArrayList<Object>) xy; // ??
		xy.add(y); 

		if (chart == null) {
			temp++;
		} else {
			chart.changed(null);
		}

		return this;
	}

	public C3DataRow(String name) {
		this.name = name;
	}

	public void setChart(C3Chart chart) {
		this.chart = chart;

		for (int i = 0; i < temp; i++) {
			chart.changed(null);
		}
		temp = 0;
	}

	public C3DataRow withType(DataRowType type) {
		this.type = type;
		return this;
	}

	public C3DataRow withList(Collection<?> list) {
		this.ys = list;
		return this;
	}

	// TODO
	public C3DataRow withMap(Map<?, ?> map) {
		this.xs = map.keySet();
		this.ys = map.values();
		return this;
	}

	public C3DataRow add(Object y) {
		return addValue(this.ys, y);
	}

	public C3DataRow add(Object x, Object y) {
		addValue(this.xs, x);
		return addValue(this.ys, y);
	}

	public JSONObject toJSON() {
		JSONObject result = new JSONObject();

		// generate ys
		JSONArray coly = new JSONArray();
		result.put("ys", coly);
		coly.put(name);
		for (Object val : ys) {
			coly.put(val);
		}

		// generate xs
		if (xs.size() > 0) {
			JSONArray colx = new JSONArray();
			result.put("xs", colx);
			colx.put(name + "_x");
			for (Object val : xs) {
				colx.put(val);
			}
		}

		// generate types
		if (type != null) {
			result.put("type", type);
		}

		// generate groups

		return result;
	}

	public String getName() {
		return name;
	}

	public Collection<?> getXs() {
		return xs;
	}

	public Collection<?> getYs() {
		return ys;
	}
}
