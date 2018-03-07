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

package de.iwes.widgets.html.plotflot;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotFlot extends Plot2D<FlotConfiguration, FlotDataSet, PlotFlotOptions> {

	private static final long serialVersionUID = 3719597065026765373L;
	
	/************* constructor **********************/

	public PlotFlot(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		registerLibrary(true, "$.fn.plot", "/ogema/jslib/jquery/flot-0.8.3/jquery.flot.min.js"); // identifier $.fn.plot
//		registerLibrary(true, "$.fn.flot", "/ogema/jslib/jquery/jquery.flot.selection.min.js"); // entry in array $.plot.plugins 
//		registerLibrary(true, "$.fn.flot", "/ogema/jslib/jquery/jquery.flot.time.min.js"); // entry in array $.plot.plugins 
		registerLibrary(true, "$.ui", "/ogema/jslib/jquery/ui-1.12.1/jquery-ui.min.js");
		registerJsDependencies();
	}
	
	/******* Inherited methods *****/
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return PlotFlot.class;
	}

	@Override
	public PlotFlotOptions createNewSession() {
		PlotFlotOptions opt = new PlotFlotOptions(this);
		return opt;
 	}
	
	@Override
	protected FlotConfiguration createNewConfiguration() {
		return new FlotConfiguration();
	}
	
	/******** public methods ***********/
}
