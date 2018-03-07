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
		if (getPlotType().equals(PlotType.LINE_WITH_POINTS))
			showPoints = true;		
		obj.put("point", new JSONObject("{show:" + showPoints + ",r:"+ getPointSize() +"}")); 
		obj.put("legend", new JSONObject("{show:" + showLegend + "}"));  
		return obj;
	}

}
