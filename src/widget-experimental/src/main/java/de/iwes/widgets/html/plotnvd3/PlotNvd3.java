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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotNvd3 extends Plot2D<Nvd3Configuration, Nvd3DataSet, PlotNvd3Options> {

	private static final long serialVersionUID = 3719597065026765373L;
	
	/************* constructor **********************/

	public PlotNvd3(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		registerLibrary(true, "d3", "/ogema/widget/plotlibs/d3_3.5.17/d3.min.js");
		registerLibrary(false, "nv-css", "/ogema/widget/plotnvd3/1.8.5-dev/nv.d3.min.css");
		registerLibrary(true, "nv", "/ogema/widget/plotnvd3/1.8.5-dev/nv.d3.min.js");
		registerJsDependencies();
		setDefaultHeight("500px");
	}
	
	/******* Inherited methods *****/
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return PlotNvd3.class;
	}

	@Override
	public PlotNvd3Options createNewSession() {
		PlotNvd3Options opt = new PlotNvd3Options(this);
		return opt;
 	}
	
	@Override
	protected Nvd3Configuration createNewConfiguration() {
		return new Nvd3Configuration();
	}
	
	/******** public methods ***********/
}
