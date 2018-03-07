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
