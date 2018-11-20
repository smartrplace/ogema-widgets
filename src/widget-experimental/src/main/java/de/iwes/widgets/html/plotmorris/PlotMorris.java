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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotMorris extends Plot2D<MorrisChartConfiguration, MorrisDataSet, PlotMorrisOptions> {

	private static final long serialVersionUID = 3719597065026765373L;

	/************* constructor **********************/

	public PlotMorris(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);

	}

	public PlotMorris(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	protected void registerJsDependencies() {
		registerLibrary(true, "Raphael", "/ogema/widget/plotlibs/raphael-2.1.0.js");
		registerLibrary(true, "Morris", "/ogema/widget/plotlibs/morris.js-0.5.1/morris.js");
		registerLibrary(false, "Morris-css", "/ogema/widget/plotlibs/morris.js-0.5.1/morris.css");
		super.registerJsDependencies();
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
