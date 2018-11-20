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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
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

	}

	public PlotC3(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	protected void registerJsDependencies() {
//		registerLibrary(true, "d3", "/ogema/jslib/chart/d3.min.js");
		registerLibrary(true, "d3", "/ogema/widget/plotlibs/d3-5.0.0/d3.min.js");
		registerLibrary(true, "c3", "/ogema/widget/plotlibs/c3-0.6.2/c3.min.js");
		registerLibrary(false, "c3-css", "/ogema/widget/plotlibs/c3-0.6.2/c3.min.css");
		super.registerJsDependencies();
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
