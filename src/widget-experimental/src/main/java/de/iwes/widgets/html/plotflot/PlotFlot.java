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
package de.iwes.widgets.html.plotflot;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2D;

public class PlotFlot extends Plot2D<FlotConfiguration, FlotDataSet, PlotFlotOptions> {

	private static final long serialVersionUID = 3719597065026765373L;

	/************* constructor **********************/

	public PlotFlot(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	public PlotFlot(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	@Override
	protected void registerJsDependencies() {
		registerLibrary(true, "$.fn.plot", "/ogema/jslib/jquery/flot-0.8.3/jquery.flot.min.js"); // identifier $.fn.plot
		registerLibrary(true, "$.fn.plot.stack", "/ogema/jslib/jquery/flot-0.8.3/jquery.flot.stack.min.js");
//		registerLibrary(true, "$.fn.flot", "/ogema/jslib/jquery/jquery.flot.selection.min.js"); // entry in array $.plot.plugins
//		registerLibrary(true, "$.fn.flot", "/ogema/jslib/jquery/jquery.flot.time.min.js"); // entry in array $.plot.plugins
		registerLibrary(true, "$.ui", "/ogema/jslib/jquery/ui-1.12.1/jquery-ui.min.js");
		super.registerJsDependencies();
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
