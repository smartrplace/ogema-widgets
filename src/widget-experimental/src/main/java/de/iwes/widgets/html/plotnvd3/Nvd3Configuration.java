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
