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
package de.iwes.widgets.html.plotnvd3;

import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DConfiguration;
import de.iwes.widgets.html.plot.api.PlotType;

// TODO this is simply copied from FlotConfiguration
public class Nvd3Configuration extends Plot2DConfiguration {  
	
	private boolean enableOverviewPlot = false;
	private int overviewHeight = 200;

	public Nvd3Configuration enableOverviewPlot(boolean enable) {
		this.enableOverviewPlot  = enable;
		return this;
	}
	
	public boolean isEnableOverviewPlot() {
		return enableOverviewPlot;
	}
	
	public int getOverviewHeight() {
		return overviewHeight;
	}

	public Nvd3Configuration setOverviewHeight(int overviewHeight) {
		this.overviewHeight = overviewHeight;
		return this;
	}

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
		else if (type.equals(PlotType.LINE) || type.equals(PlotType.STEPS)) {
			showlines = true;
		}
		else if (type.equals(PlotType.POINTS)) {
			showpoints = true;
		}
		else if (type.equals(PlotType.BAR)) {
			showbars = true;
		}
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
		return json;
		
	}
 
}
