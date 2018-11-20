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
package de.iwes.widgets.html.plotchartjs;

import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DConfiguration;
import de.iwes.widgets.html.plot.api.PlotType;

public class ChartjsConfiguration extends Plot2DConfiguration {

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		JSONObject series = new JSONObject();
		json.put("clickable", isClickable());
		json.put("hoverable", isHoverable());
		PlotType type = getPlotType();
		JSONObject lines = new JSONObject();
		JSONObject points = new JSONObject();
		JSONObject bars  =new JSONObject();
		boolean showlines = false;
		boolean showbars = false;
		boolean showpoints = false;
		if (type.equals(PlotType.LINE_WITH_POINTS)) {
			showlines = true;
			showpoints = true;
		}
		else if (type.equals(PlotType.LINE) || type.equals(PlotType.STEPS) || type.equals(PlotType.LINE_STACKED)) {
			showlines = true;
		}
		else if (type.equals(PlotType.POINTS)) {
			showpoints = true;
		}
		else if (type.equals(PlotType.BAR) || type.equals(PlotType.BAR_STACKED)) {
			showbars = true;
		}
		final boolean stacked = type.equals(PlotType.BAR_STACKED) || type.equals(PlotType.LINE_STACKED);
		lines.put("show", showlines);
		if (showlines) {
			lines.put("lineWidth", getLineWidth());
			if (type.equals(PlotType.STEPS))
				lines.put("steps", true);
		}
		points.put("show", showpoints);
		if (showpoints) {
			points.put("radius", getPointSize());
		}
		bars.put("show", showbars);
		if (showbars) {
			bars.put("barWidth", getLineWidth());
		}

		if(asStackedVersion()) {
			series.put("stack", 0);
		}
		series.put("lines", lines);
		series.put("points", points);
		series.put("bars", bars);

		JSONObject grid = new JSONObject();
		grid.put("show", isShowYGrid());
		json.put("grid", grid);
		json.put("series", series);
		float xmin = getXmin();
		float xmax = getXmax();
		float ymin = getYmin();
		float ymax = getYmax();
		if (!Float.isNaN(xmin) || !Float.isNaN(xmax)) {
			JSONObject xaxis = new JSONObject();
			if (!Float.isNaN(xmin))
				xaxis.put("min", xmin);
			if (!Float.isNaN(xmax))
				xaxis.put("max", xmax);
			json.put("xaxis", xaxis);
		}
		if (!Float.isNaN(ymin) || !Float.isNaN(ymax)) {
			JSONObject yaxis = new JSONObject();
			if (!Float.isNaN(ymin))
				yaxis.put("min", ymin);
			if (!Float.isNaN(ymax))
				yaxis.put("max", ymax);
			json.put("yaxis", yaxis);
		}
//		http://www.chartjs.org/docs/latest/axes/cartesian/time.html
		/*
		scales: {
            xAxes: [{
                type: 'time',
                time: {
                    displayFormats: {
                        quarter: 'MMM YYYY'
                    }
                }
            }]
        }
        */
		final JSONObject scales = new JSONObject();
		final JSONArray xAxes = new JSONArray();
		final JSONObject x = new JSONObject();
		x.put("type", mapAxis(getXAxisType0()));
		xAxes.put(x);
		scales.put("xAxes", xAxes);
		if (stacked) {
			final JSONArray arr = new JSONArray();
			arr.put(Collections.singletonMap("stacked", true));
			scales.put("yAxes", arr);
		}
		json.put("scales", scales);
		return json;

	}

	private static String mapAxis(Plot2DConfiguration.AxisType type) {
		return type == Plot2DConfiguration.AxisType.DEFAULT ? "x" : "time";
	}

}
