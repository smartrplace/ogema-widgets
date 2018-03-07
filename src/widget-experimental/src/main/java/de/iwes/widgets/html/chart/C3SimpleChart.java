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
	 * @param value
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
