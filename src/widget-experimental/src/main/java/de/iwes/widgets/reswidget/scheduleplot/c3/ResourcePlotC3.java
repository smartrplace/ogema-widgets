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
