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
package de.iwes.widgets.reswidget.scheduleplot.c3;

import java.util.Collection;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotc3.C3DataSet;
import de.iwes.widgets.html.plotc3.PlotC3;
import de.iwes.widgets.html.plotc3.PlotC3Options;
import de.iwes.widgets.reswidget.scheduleplot.api.VolatileResourceLogger;

public class ResourcePlotC3 extends PlotC3 implements VolatileResourceLogger<C3DataSet, ResourceDataC3>{
	
	private static final long serialVersionUID = 1L;
	private Collection<Resource> defaultResources = null;
	
	/****** Constructor *******/
	
	public ResourcePlotC3(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDefaultPollingInterval(5000);
	}

	/***** Inherited methods ******/
	
	@Override
	public ResourcePlotC3Options createNewSession() {
		return new ResourcePlotC3Options(this);
	}
	
	@Override
	public ResourcePlotC3Options getData(OgemaHttpRequest req) {
		return (ResourcePlotC3Options) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(PlotC3Options opt) {
		super.setDefaultValues(opt);
		ResourcePlotC3Options opt2 = (ResourcePlotC3Options) opt;
		if (defaultResources != null)
			opt2.getResourceData().setResources(defaultResources);
	}
	
	
	/****** Public methods ********/
	
	@Override
	public void setDefaultResources(Collection<Resource> resources) {
		this.defaultResources = resources;
	}

	@Override
	public ResourceDataC3 getResourceData(OgemaHttpRequest req) {
		return getData(req).getResourceData();
	}


}
