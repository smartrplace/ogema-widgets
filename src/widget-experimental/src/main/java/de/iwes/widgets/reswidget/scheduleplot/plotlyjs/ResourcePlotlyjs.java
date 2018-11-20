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
package de.iwes.widgets.reswidget.scheduleplot.plotlyjs;

import java.util.Collection;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration.AxisType;
import de.iwes.widgets.html.plotlyjs.Plotlyjs;
import de.iwes.widgets.html.plotlyjs.PlotlyjsDataSet;
import de.iwes.widgets.html.plotlyjs.PlotlyjsOptions;
import de.iwes.widgets.reswidget.scheduleplot.api.VolatileResourceLogger;

public class ResourcePlotlyjs extends Plotlyjs implements VolatileResourceLogger<PlotlyjsDataSet, ResourceDataPlotlyjs>{

	private static final long serialVersionUID = 1L;
	private Collection<Resource> defaultResources = null;

	/****** Constructor *******/

	public ResourcePlotlyjs(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		getDefaultConfiguration().setXAxisType0(AxisType.TIME);
		setDefaultPollingInterval(5000);
	}

	/***** Inherited methods ******/

	@Override
	public ResourcePlotlyjsOptions createNewSession() {
		return new ResourcePlotlyjsOptions(this);
	}

	@Override
	public ResourcePlotlyjsOptions getData(OgemaHttpRequest req) {
		return (ResourcePlotlyjsOptions) super.getData(req);
	}

	@Override
	protected void setDefaultValues(PlotlyjsOptions opt) {
		super.setDefaultValues(opt);
		ResourcePlotlyjsOptions opt2 = (ResourcePlotlyjsOptions) opt;
		if (defaultResources != null)
			opt2.getResourceData().setResources(defaultResources);
	}


	/****** Public methods ********/

	@Override
	public void setDefaultResources(Collection<Resource> resources) {
		this.defaultResources = resources;
	}

	@Override
	public ResourceDataPlotlyjs getResourceData(OgemaHttpRequest req) {
		return getData(req).getResourceData();
	}


}
