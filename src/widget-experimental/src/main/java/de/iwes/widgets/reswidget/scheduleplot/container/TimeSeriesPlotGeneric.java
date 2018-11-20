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
package de.iwes.widgets.reswidget.scheduleplot.container;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.ogema.tools.resource.util.ResourceUtils;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration.AxisType;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleplot.plotchartjs.SchedulePlotChartjs;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class TimeSeriesPlotGeneric extends PageSnippet implements TimeSeriesPlot<Plot2DConfiguration, GenericDataset, GenericScheduleData> {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	Class<? extends TimeSeriesPlot> defaultType = SchedulePlotChartjs.class;
	private Map<String, SchedulePresentationData> defaultSchedules = null;
	private Plot2DConfiguration defaultConfig = new Plot2DConfiguration();
	private String defaultHeight;
	private String defaultWidth;

	{
		defaultConfig.setXAxisType0(AxisType.TIME);
	}

	public TimeSeriesPlotGeneric(WidgetPage<?> page, String id) {
		super(page, id);
	}

	@SuppressWarnings("rawtypes")
	TimeSeriesPlot<?, ?, ?> getNew(final Class<? extends TimeSeriesPlot> type, final OgemaHttpRequest req) throws PrivilegedActionException {
		final TimeSeriesPlot<?, ?, ?> plot = AccessController.doPrivileged(new PrivilegedExceptionAction<TimeSeriesPlot<?, ?, ?>>() {

			@Override
			public TimeSeriesPlot<?, ?, ?> run() throws Exception {
				if (isGlobalWidget()) {
					TimeSeriesPlot<?, ?, ?> widget;
					try {
						widget = type.getConstructor(WidgetPage.class, String.class).newInstance(getPage(), getId() + "_plot_"
							+ ResourceUtils.getValidResourceName(type.getName()));
					} catch (Exception e) {
						widget = type.getConstructor(WidgetPage.class, String.class, boolean.class).newInstance(getPage(), getId() + "_plot_"
								+ ResourceUtils.getValidResourceName(type.getName()), false);
					}
					triggerAction(widget, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
					return widget;

				}
				else {
					final TimeSeriesPlot<?, ?, ?> widget =  type.getConstructor(OgemaWidget.class, String.class, OgemaHttpRequest.class).newInstance(TimeSeriesPlotGeneric.this, getId() + "_plot_"
							+ ResourceUtils.getValidResourceName(type.getName()), req);
					return widget;
				}
			}
		});
		if (defaultSchedules != null)
			plot.setDefaultSchedules(defaultSchedules);
		Plot2DConfiguration.copyValues(defaultConfig, plot.getDefaultConfiguration());
		if (defaultHeight != null)
			plot.setDefaultHeight(defaultHeight);
		if (defaultWidth != null)
			plot.setDefaultWidth(defaultWidth);
		return plot;
	}


	@Override
	public void setDefaultSchedules(final Map<String, SchedulePresentationData> schedules) {
		this.defaultSchedules = schedules == null || schedules.isEmpty() ? null : new LinkedHashMap<String, SchedulePresentationData>(schedules);
	}

	@Override
	public GenericScheduleData getScheduleData(OgemaHttpRequest req) {
		return new GenericScheduleData(getData(req), req);
	}

	@Override
	public void setInterval(long startTime, long endTime, OgemaHttpRequest req) {
		getPlotWidget(req).setInterval(startTime, endTime, req);
	}

	@Override
	public TimeSeriesPlotData createNewSession() {
		return new TimeSeriesPlotData(this);
	}

	@Override
	public TimeSeriesPlotData getData(OgemaHttpRequest req) {
		return (TimeSeriesPlotData) super.getData(req);
	}

	public void setDefaultPlotWidget(@SuppressWarnings("rawtypes") final Class<? extends TimeSeriesPlot> type) {
		this.defaultType = Objects.requireNonNull(type);
	}

	@SuppressWarnings("rawtypes")
	public void setPlotWidget(final Class<? extends TimeSeriesPlot> type, final OgemaHttpRequest req) {
		getData(req).setPlotWidget(type, req);
	}

	/**
	 * It is recommended to use {@link #setPlotWidget(Class, OgemaHttpRequest)} instead.
	 * @param plot
	 * @param req
	 */
	public void setPlotWidget(final TimeSeriesPlot<?, ?, ?> plot, final OgemaHttpRequest req) {
		getData(req).setPlotWidget(plot, req);
	}

	public TimeSeriesPlot<?, ?, ?> getPlotWidget(final OgemaHttpRequest req) {
		return getData(req).getPlotWidget(req);
	}

	@Override
	public Plot2DConfiguration getDefaultConfiguration() {
		return defaultConfig;
	}

	@Override
	public Plot2DConfiguration getConfiguration(OgemaHttpRequest req) {
		return getPlotWidget(req).getConfiguration(req);
	}

	//

	@Override
	public void setDefaultHeight(String height) {
		this.defaultHeight = height;
	}

	@Override
	public void setDefaultWidth(String width) {
		this.defaultWidth = width;
	}

	@Override
	public void setHeight(String height, OgemaHttpRequest req) {
		getPlotWidget(req).setHeight(height, req);
	}

	@Override
	public void setWidth(String width, OgemaHttpRequest req) {
		getPlotWidget(req).setWidth(width, req);
	}

}
