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
package de.iwes.widgets.html.plotmorris;

import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DConfiguration;

public class MorrisChartConfiguration extends Plot2DConfiguration{
	
	private boolean showTooltip=true;;
	private boolean showLegend=true;

	MorrisChartConfiguration() {
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
	
	JSONObject toJSON() {	  // see http://morrisjs.github.io/morris.js/lines.html
		JSONObject obj =  new JSONObject();
		obj.put("pointSize",getPointSize() ); // TODO
		obj.put("smooth", isSmoothLine());
		obj.put("lineWidth", getLineWidth());
		obj.put("hideHover", !isInteractionsEnabled() || !isHoverable());
		return obj;
	}
	

}
