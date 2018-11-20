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
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.chart.C3Chart.ChartType;

public abstract class C3SimpleChart {

	protected C3Chart chart;
	private boolean showTooltip = true;
	private boolean showLegend = true;

	public C3SimpleChart(WidgetPageBase<?> page, String id) {
		this(page, id, ChartType.LINE);
	}

	protected C3SimpleChart(WidgetPageBase<?> page, String id, ChartType type) {

		chart = new C3Chart(page, id, type, true) {
			private static final long serialVersionUID = -3229654972698343308L;

			@Override
			public JSONArray getChartConfig(OgemaHttpRequest req) {

				JSONArray array = null;

				if (showTooltip == false) {
					array = (array == null) ? new JSONArray() : array;
					array.put(new JSONObject().put("tooltip",
							new JSONObject().put("show", false)));
				}

				if (showLegend == false) {
					array = (array == null) ? new JSONArray() : array;
					array.put(new JSONObject().put("legend",
							new JSONObject().put("show", showLegend)));
				}

				// return config data to chart
				JSONArray newConfig = getConfig();
				if (array == null && newConfig == null) {
					return null;

				} else if (array != null && newConfig != null) {
					for (int i = 0; i < newConfig.length(); i++) {
						array.put(newConfig.get(i));
					}
				}

				return array == null ? newConfig : array;
			}
		};
	}

	private C3DataRow getRow(String name) {

		for (C3DataRow row : chart.getDataRows(null)) {
			if (row.getName().equals(name)) {
				return row;
			}
		}
		return null;
	}

	private C3DataRow addRow(String name) {

		C3DataRow exist = getRow(name);
		if (exist == null) {
			C3DataRow row = new C3DataRow(name);
			chart.add(row,null);
			row.setChart(chart);
			return row;
		} else {
			return exist;
		}
	}

	public C3SimpleChart withTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
		chart.changed(null);
		return this;
	}

	public C3SimpleChart withLegend(boolean showLegend) {
		this.showLegend = showLegend;
		chart.changed(null);
		return this;
	}

	/**
	 * If C3DataRow already exist override else add new one to chart.
	 * 
	 * @param row
	 * @return chart object
	 */
	public C3SimpleChart add(C3DataRow row) {

		C3DataRow exist = getRow(row.getName());
		if (exist != null) {
			chart.remove(exist,null);
		}
		row.setChart(chart);
		chart.add(row,null);
		chart.changed(null);
		return this;
	}

	/**
	 * 
	 * 
	 * @param name
	 *            of data row
	 * @param y
	 * @return
	 */
	public C3SimpleChart add(String name, Object y) {

		addRow(name).add(y);
		return this;
	}

	public C3SimpleChart add(String name, Object x, Object y) {

		addRow(name).add(x, y);
		return this;
	}

	public C3SimpleChart override(C3DataRow row) {

		chart.changed(null);
		return this;
	}

	public C3SimpleChart override(String name, Object value) {

		chart.changed(null);
		return this;
	}

	public C3SimpleChart override(String name, Object x, Object y) {

		chart.changed(null);
		return this;
	}

	public C3SimpleChart remove(C3DataRow row) {

		chart.getDataRows(null).remove(row);

		// TODO trigger unload event for dataRow

		chart.changed(null);
		return this;
	}

	public C3SimpleChart remove(String name) {

		C3DataRow exist = getRow(name);
		if (exist != null) {
			remove(exist);
		}
		chart.changed(null);
		return this;
	}

	/**
	 * Remove x or y value depend on chart type.
	 * 
	 * @param name
	 * @param xy
	 * @return
	 */
	public C3SimpleChart remove(String name, Object xy) {

		C3DataRow exist = getRow(name);
		if (exist != null) {

			if (exist.getXs().size() > 0) {

				Iterator<?> xter = exist.getXs().iterator();
				Iterator<?> yter = exist.getYs().iterator();

				while (xter.hasNext()) {

					Object x = xter.next();
					Object y = yter.next();

					if (x.equals(xy)) {
						exist.getXs().remove(x);
						exist.getYs().remove(y);
					}
				}

			} else {
				exist.getYs().remove(xy);
			}
		}

		// TODO trigger load event for changed dataRow
		chart.changed(null);
		return this;
	}

	public abstract JSONArray getConfig();

	public void replaceRow(HashSet<C3DataRow> dataRows, C3DataRow oldRow,
			C3DataRow newRow) {
		dataRows.remove(oldRow);
		dataRows.add(newRow);

		chart.changed(null);
	}

}
