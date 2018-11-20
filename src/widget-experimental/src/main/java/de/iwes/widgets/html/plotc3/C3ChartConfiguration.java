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
package de.iwes.widgets.html.plotc3;

import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DConfiguration;
import de.iwes.widgets.html.plot.api.PlotType;

public class C3ChartConfiguration extends Plot2DConfiguration {

	private boolean showTooltip=true;;
	private boolean showLegend=true;

	C3ChartConfiguration() {
	}

	public boolean isShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	JSONObject toJSON() {	  // see http://c3js.org/reference.html ; many more possibilities
		JSONObject obj =  new JSONObject();
		obj.put("interaction", new JSONObject("{enabled:" + isInteractionsEnabled() + "}"));
		obj.put("grid", new JSONObject("{x:{show:" + isShowXGrid() + "},y:{show:" + isShowYGrid() + "}}"));
		obj.put("tooltip",new JSONObject("{show:" + showTooltip + "}"));
		obj.put("zoom",new JSONObject("{enabled:" + isZoomEnabled() + "}"));
		boolean showPoints = false;
		if (getPlotType().equals(PlotType.LINE_WITH_POINTS) || getPlotType().equals(PlotType.POINTS))
			showPoints = true;
		obj.put("point", new JSONObject("{show:" + showPoints + ",r:"+ getPointSize() +"}"));
		obj.put("legend", new JSONObject("{show:" + showLegend + "}"));
		return obj;
	}

}
