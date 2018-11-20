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
package de.iwes.widgets.html.plotlyjs;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2D;

public class Plotlyjs extends Plot2D<PlotlyjsConfiguration, PlotlyjsDataSet, PlotlyjsOptions> {

	private static final long serialVersionUID = 3719597065026765373L;

	/************* constructor **********************/

	public Plotlyjs(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	public Plotlyjs(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	protected void registerJsDependencies() {
		// FIXME reduced library?
//		registerLibrary(true, "Plotly", "/ogema/widget/plotlibs/plotlyjs-1.38.3/plotly.min.js");
		registerLibrary(true, "Plotly", "/ogema/widget/plotlibs/plotlyjs-1.38.3/plotly-basic.min.js");
		super.registerJsDependencies();
	}

	/******* Inherited methods *****/

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return Plotlyjs.class;
	}

	@Override
	public PlotlyjsOptions createNewSession() {
		PlotlyjsOptions opt = new PlotlyjsOptions(this);
		return opt;
 	}

	@Override
	protected PlotlyjsConfiguration createNewConfiguration() {
		return new PlotlyjsConfiguration();
	}

	/******** public methods ***********/
}
