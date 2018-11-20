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
package de.iwes.widgets.html.plotnvd3;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotNvd3 extends Plot2D<Nvd3Configuration, Nvd3DataSet, PlotNvd3Options> {

	private static final long serialVersionUID = 3719597065026765373L;

	/************* constructor **********************/

	public PlotNvd3(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDefaultHeight("500px");
	}

	public PlotNvd3(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		setDefaultHeight("500px");
	}

	@Override
	protected void registerJsDependencies() {
		registerLibrary(true, "d3", "/ogema/widget/plotlibs/d3_3.5.17/d3.min.js");
		registerLibrary(false, "nv-css", "/ogema/widget/plotnvd3/1.8.5-dev/nv.d3.min.css");
		registerLibrary(true, "nv", "/ogema/widget/plotnvd3/1.8.5-dev/nv.d3.min.js");
		super.registerJsDependencies();
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
