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
package de.iwes.widgets.reswidget.scheduleplot.plotchartjs;

import java.util.Collection;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration.AxisType;
import de.iwes.widgets.html.plotchartjs.ChartjsDataSet;
import de.iwes.widgets.html.plotchartjs.PlotChartjs;
import de.iwes.widgets.html.plotchartjs.PlotChartjsOptions;
import de.iwes.widgets.reswidget.scheduleplot.api.VolatileResourceLogger;

public class ResourcePlotChartjs extends PlotChartjs implements VolatileResourceLogger<ChartjsDataSet, ResourceDataPlotChartjs>{

	private static final long serialVersionUID = 1L;
	private Collection<Resource> defaultResources = null;

	/****** Constructor *******/

	public ResourcePlotChartjs(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		getDefaultConfiguration().setXAxisType0(AxisType.TIME);
		setDefaultPollingInterval(5000);
	}

	/***** Inherited methods ******/

	@Override
	public ResourcePlotChartjsOptions createNewSession() {
		return new ResourcePlotChartjsOptions(this);
	}

	@Override
	public ResourcePlotChartjsOptions getData(OgemaHttpRequest req) {
		return (ResourcePlotChartjsOptions) super.getData(req);
	}

	@Override
	protected void setDefaultValues(PlotChartjsOptions opt) {
		super.setDefaultValues(opt);
		ResourcePlotChartjsOptions opt2 = (ResourcePlotChartjsOptions) opt;
		if (defaultResources != null)
			opt2.getResourceData().setResources(defaultResources);
	}


	/****** Public methods ********/

	@Override
	public void setDefaultResources(Collection<Resource> resources) {
		this.defaultResources = resources;
	}

	@Override
	public ResourceDataPlotChartjs getResourceData(OgemaHttpRequest req) {
		return getData(req).getResourceData();
	}


}
