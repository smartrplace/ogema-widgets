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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotMorris extends Plot2D<MorrisChartConfiguration, MorrisDataSet, PlotMorrisOptions> {

	private static final long serialVersionUID = 3719597065026765373L;

	/************* constructor **********************/

	public PlotMorris(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		registerLibrary(true, "Raphael", "/ogema/widget/plotlibs/raphael-2.1.0.js");
		registerLibrary(true, "Morris", "/ogema/widget/plotlibs/morris.js-0.5.1/morris.js");
		registerLibrary(false, "Morris-css", "/ogema/widget/plotlibs/morris.js-0.5.1/morris.css");
		registerJsDependencies();
	}
	
	/******* Inherited methods *****/
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return PlotMorris.class;
	}

	@Override
	public PlotMorrisOptions createNewSession() {
		PlotMorrisOptions opt = new PlotMorrisOptions(this);
		return opt;
 	}
	
	@Override
	protected MorrisChartConfiguration createNewConfiguration() {
		return new MorrisChartConfiguration();
	}
	
	/******** public methods ***********/
}
