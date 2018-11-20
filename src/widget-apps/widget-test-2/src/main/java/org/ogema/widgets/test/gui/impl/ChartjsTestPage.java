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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.plot.api.Plot2DOptions;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleplot.plotchartjs.SchedulePlotChartjs;
import de.iwes.widgets.reswidget.scheduleplot.plotlyjs.SchedulePlotlyjs;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultTimeSeriesDisplayTemplate;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.template.DisplayTemplate;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE,
				LazyWidgetPage.RELATIVE_URL + "=chartjs.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Chart.js test page"
		}
)
public class ChartjsTestPage implements LazyWidgetPage {

	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new PlotlyTestPageInit(page, appMan);
	}

	private static class PlotlyTestPageInit {

		private final WidgetPage<?> page;
		private final Header header;
		private final SimpleGrid settingsGrid;
		private final TemplateMultiselect<Schedule> scheduleSelector;
		private final TemplateDropdown<PlotType> plotTypeSelector;
		private final Button apply;
		private final SchedulePlotChartjs schedulePlot;

		@SuppressWarnings("serial")
		public PlotlyTestPageInit(final WidgetPage<?> page, final ApplicationManager appMan) {
			this.page = page;
			this.header = new Header(page, "header", true);
			header.setDefaultText("Chart.js test page");
			header.setDefaultColor("blue");

			this.settingsGrid = new SimpleGrid(page, "settingsGrid", true);
			settingsGrid.setDefaultAppendFillColumn(true);

			this.scheduleSelector = new TemplateMultiselect<Schedule>(page, "scheduleSelector") {

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void onGET(OgemaHttpRequest req) {
					update((List) appMan.getResourceAccess().getResources(Schedule.class), req);
				}

			};
			scheduleSelector.setTemplate(new DefaultTimeSeriesDisplayTemplate<>(null));
			this.plotTypeSelector = new TemplateDropdown<PlotType>(page, "plotTypeSelector") {

				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean doTrigger = page.getTriggeringWidget(req) == this;
					if (!doTrigger)
						removeTriggerAction(schedulePlot, TriggeringAction.GET_REQUEST, Plot2DOptions.adaptPlotType(getSelectedItem(req)), req);
					else
						triggerAction(schedulePlot, TriggeringAction.GET_REQUEST, Plot2DOptions.adaptPlotType(getSelectedItem(req)), req);
				}

			};
			plotTypeSelector.setDefaultItems(Arrays.asList(
					PlotType.LINE,
					PlotType.POINTS,
					PlotType.BAR,
					PlotType.LINE_WITH_POINTS,
					PlotType.STEPS
			));
			plotTypeSelector.selectDefaultItem(PlotType.LINE);
			plotTypeSelector.setTemplate(new DisplayTemplate<PlotType>() {

				@Override
				public String getLabel(PlotType object, OgemaLocale locale) {
					return object.toString();
				}

				@Override
				public String getId(PlotType object) {
					return object.getId();
				}
			});

			this.apply = new Button(page, "apply", "Apply");
			this.schedulePlot = new SchedulePlotChartjs(page, "schedulePlot", false) {

				@Override
				public void onGET(OgemaHttpRequest req) {
					final Map<String, SchedulePresentationData> map = scheduleSelector.getSelectedItems(req).stream()
						.collect(Collectors.toMap(Schedule::getPath, schedule -> new DefaultSchedulePresentationData(schedule,
										schedule.<SingleValueResource> getParent(), schedule.getPath())));
					getScheduleData(req).setSchedules(map);
					getConfiguration(req).setPlotType(plotTypeSelector.getSelectedItem(req));
				}

			};

			buildPage();
			setDependencies();
		}

		private final void buildPage() {
			page.append(header).linebreak().append(settingsGrid
					.addItem("Select schedule", false, null).addItem(scheduleSelector, false, null)
					.addItem("Select line type", true, null).addItem(plotTypeSelector, false, null)
					.addItem((OgemaWidget) null, true, null).addItem(apply, false, null)
			).append(schedulePlot);
		}

		private final void setDependencies() {
			apply.triggerAction(schedulePlot, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			plotTypeSelector.triggerAction(plotTypeSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}

	}

}
