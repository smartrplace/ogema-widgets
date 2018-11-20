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
package org.ogema.widgets.test.gui.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.resource.widget.multiselect.ResourceMultiselect;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleplot.c3.SchedulePlotC3;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;
import de.iwes.widgets.reswidget.scheduleplot.morris2.SchedulePlotMorris;
import de.iwes.widgets.reswidget.scheduleplot.nvd3.SchedulePlotNvd3;
import de.iwes.widgets.reswidget.scheduleplot.plotlyjs.SchedulePlotlyjs;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=plots.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Schedule plots page"
		}
)
public class PlotPage implements LazyWidgetPage {
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new PlotPageInit(page, appMan);
	}
	
	private static class PlotPageInit {
	
		private final WidgetPage<?> page;
		private final Header header;
		private final Alert infoAlert;
		private final ResourceMultiselect<Schedule> schedulesSelector;
		// the default time series plot widget
		private final SchedulePlotFlot schedulePlotFlot;
		// the other ones are very experimental...
		private final TimeSeriesPlot<?, ?, ?> schedulePlotPlotly;
		private final TimeSeriesPlot<?, ?, ?> schedulePlotMorris;
		private final TimeSeriesPlot<?, ?, ?> schedulePlotNvd3;
		private final TimeSeriesPlot<?, ?, ?> schedulePlotC3;
		
		PlotPageInit(final WidgetPage<?> page, final ApplicationManager am) {
			this.page = page;
			this.header = new Header(page, "header", true);
			header.setDefaultText("Plots page");
			header.setDefaultColor("blue");
			
			this.infoAlert = new Alert(page, "infoAlert", "This page illustrates the basic use of plot widgets for OGEMA time series.<br>"
					+ "See also the SchedulePlotBasic widget and app.");
			infoAlert.setDefaultVisibility(true);
			
			this.schedulesSelector = new ResourceMultiselect<>(page, "schedulesSelector", false, am.getResourceAccess(), 
					UpdateMode.AUTO_ON_GET, Schedule.class);
			
			this.schedulePlotFlot = new SchedulePlotFlot(page, "schedulePlotFlot", false) {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					// these are the schedules which the user has selected for display
					final Collection<Schedule> schedules = schedulesSelector.getSelectedItems(req);
					// update the current set of schedules displayed by the schedulePlotFlot widget
					getScheduleData(req).setSchedules(getSchedulesMap(schedules));
				}
				
			};
			schedulePlotFlot.getDefaultConfiguration().setXUnit("t");
			schedulePlotFlot.getDefaultConfiguration().setYUnit("y"); // just as a demo
			this.schedulePlotC3 = new SchedulePlotC3(page, "schedulePlotC3", false) {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					final Collection<Schedule> schedules = schedulesSelector.getSelectedItems(req);
					getScheduleData(req).setSchedules(getSchedulesMap(schedules));
				}
				
			};
			this.schedulePlotMorris = new SchedulePlotMorris(page, "schedulePlotMorris", false) {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					final Collection<Schedule> schedules = schedulesSelector.getSelectedItems(req);
					getScheduleData(req).setSchedules(getSchedulesMap(schedules));
				}
				
			};
			this.schedulePlotNvd3 = new SchedulePlotNvd3(page, "schedulePlotNvd3", false) {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					final Collection<Schedule> schedules = schedulesSelector.getSelectedItems(req);
					getScheduleData(req).setSchedules(getSchedulesMap(schedules));
				}
				
			};
			this.schedulePlotPlotly = new SchedulePlotlyjs(page, "schedulePlotlyjs", false) {
				
				private static final long serialVersionUID = 1L;
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final Collection<Schedule> schedules = schedulesSelector.getSelectedItems(req);
					getScheduleData(req).setSchedules(getSchedulesMap(schedules));
				}
				
			};
			
			buildPage();
			setDependencies();
		}
	
		/**
		 * add all widgets to the page
		 */
		private final void buildPage() {
			page.append(header).linebreak().append(infoAlert);
			final StaticTable table = new StaticTable(1,3, new int[]{2,3,7})
					.setContent(0, 0, "Select schedules").setContent(0, 1, schedulesSelector);
			page.append(table).linebreak();
			page.append(schedulePlotFlot)
				.append(schedulePlotPlotly)
				.append(schedulePlotMorris)
				.append(schedulePlotC3)
				.append(schedulePlotNvd3);
		}
		
		/**
		 * Update the plot widgets when the schedules selection changes
		 */
		private final void setDependencies() {
			schedulesSelector.triggerAction(schedulePlotFlot, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			schedulesSelector.triggerAction(schedulePlotPlotly, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			schedulesSelector.triggerAction(schedulePlotMorris, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			schedulesSelector.triggerAction(schedulePlotNvd3, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			schedulesSelector.triggerAction(schedulePlotC3, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
	
		/**
		 * Here we use the schedule path as label; we could instead attach a human-readable label
		 * by passing an appropriate argument to the DefaultSchedulePresentationData constructor.
		 * @param schedules
		 * @return
		 */
		private static Map<String, SchedulePresentationData> getSchedulesMap(final Collection<Schedule> schedules) {
			final Map<String,SchedulePresentationData> map = new LinkedHashMap<>();
			for (Schedule schedule : schedules) {
				map.put(schedule.getPath(), new DefaultSchedulePresentationData(schedule, (SingleValueResource) schedule.getParent(), schedule.getPath()));
			}
			return map;
		}
	
	}
}