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

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleplot.container.PlotTypeSelector;
import de.iwes.widgets.reswidget.scheduleplot.container.SchedulePlotWidgetSelector;
import de.iwes.widgets.reswidget.scheduleplot.container.TimeSeriesPlotGeneric;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultTimeSeriesDisplayTemplate;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE,
				LazyWidgetPage.RELATIVE_URL + "=plots2.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Schedule plots page2"
		}
)
public class PlotPage2 implements LazyWidgetPage {

	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new PlotPage2Init(page, appMan);
	}

	private static class PlotPage2Init {

		private final WidgetPage<?> page;
		private final Header header;
		private final Alert infoAlert;
		private final SimpleGrid settingsGrid;
		private final TemplateMultiselect<Schedule> schedulesSelector;
		private final SchedulePlotWidgetSelector librarySelector;
		private final TemplateDropdown<PlotType> lineTypeSelector;
		private final Button applyBtn;
		private final TimeSeriesPlotGeneric container;

		@SuppressWarnings("serial")
		PlotPage2Init(final WidgetPage<?> page, final ApplicationManager am) {
			this.page = page;
			this.header = new Header(page, "header", true);
			header.setDefaultText("Plots page 2");
			header.setDefaultColor("blue");

			this.infoAlert = new Alert(page, "infoAlert", "This page illustrates the basic use of plot widgets for OGEMA time series.<br>"
					+ "See also the SchedulePlotBasic widget and app.");
			infoAlert.setDefaultVisibility(true);
			infoAlert.setDefaultTextAsHtml(true);
			infoAlert.addDefaultStyle(AlertData.BOOTSTRAP_INFO);

			this.settingsGrid = new SimpleGrid(page, "settingsGrid", true);
			settingsGrid.setDefaultAppendFillColumn(true);
			this.schedulesSelector = new TemplateMultiselect<Schedule>(page, "scheduleSelector") {

				@Override
				public void onGET(OgemaHttpRequest req) {
					update(am.getResourceAccess().getResources(Schedule.class), req);
				}

			};
			schedulesSelector.setTemplate(new DefaultTimeSeriesDisplayTemplate<>(null));
			this.librarySelector = new SchedulePlotWidgetSelector(page, "librarySelector");

			this.applyBtn = new Button(page, "applyButton", "Submit");
			this.container = new TimeSeriesPlotGeneric(page, "container") {

				@Override
				public void onGET(OgemaHttpRequest req) {
					setPlotWidget(librarySelector.getSelectedItem(req), req);
					getScheduleData(req).setSchedules(getSchedulesMap(schedulesSelector.getSelectedItems(req)));
					getConfiguration(req).setPlotType(lineTypeSelector.getSelectedItem(req));
				}

			};
			this.lineTypeSelector = new PlotTypeSelector(page, "plotTypeSelector", container);
			lineTypeSelector.selectDefaultItem(PlotType.LINE);

			buildPage();
			setDependencies();
		}

		/**
		 * add all widgets to the page
		 */
		private final void buildPage() {
			page.append(header).linebreak().append(infoAlert).linebreak().append(settingsGrid
					.addItem("Select plot library", false, null).addItem(librarySelector, false	, null)
					.addItem("Select schedules", true, null).addItem(schedulesSelector, false, null)
					.addItem("Select plot type", true, null).addItem(lineTypeSelector, false, null)
					.addItem((String) null, true, null).addItem(applyBtn, false, null)
			).linebreak().append(container);
		}

		/**
		 * Update the plot widgets when the schedules selection changes
		 */
		private final void setDependencies() {
			librarySelector.triggerAction(container, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			applyBtn.triggerAction(container, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			lineTypeSelector.triggerAction(lineTypeSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
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