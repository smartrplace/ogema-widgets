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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotC3 extends Plot2D<C3ChartConfiguration, C3DataSet, PlotC3Options> {

	private static final long serialVersionUID = 3719597065026765373L;
	private C3ChartConfiguration defaultConfiguration = null;
	
	/************* constructor **********************/

	public PlotC3(WidgetPage<?> page, String id) {
		this(page, id, false);
	}
	
	public PlotC3(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
//		registerLibrary(true, "d3", "/ogema/jslib/chart/d3.min.js");
		registerLibrary(true, "d3", "/ogema/widget/plotlibs/d3_3.5.17/d3.min.js");
		registerLibrary(true, "c3", "/ogema/widget/plotlibs/c3.min.js");
		registerLibrary(false, "c3-css", "/ogema/widget/plotlibs/c3.min.css");
		registerJsDependencies();
	}
	
	/**** Enum ****/
	


	/******* Inherited methods ******/
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return PlotC3.class;
	}

	@Override
	public PlotC3Options createNewSession() {
		PlotC3Options opt = new PlotC3Options(this);
		return opt;
 	}
	
	@Override
	protected void setDefaultValues(PlotC3Options opt) {
		super.setDefaultValues(opt);
		if (defaultConfiguration != null)
			opt.config = defaultConfiguration;
	}
	
	@Override
	protected C3ChartConfiguration createNewConfiguration() {
		return new C3ChartConfiguration();
	};
	
	/******** public methods ***********/
}
