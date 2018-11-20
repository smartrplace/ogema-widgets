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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.plot.api.Plot2DOptions;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A dropdown widget which can be used to change the line type (e.g. "LINE", "BAR", "POINTS" or "STEPS", see
 * {@link PlotType}) of a schedule plot client-side.
 */
public class PlotTypeSelector extends TemplateDropdown<PlotType> {

	private static final long serialVersionUID = 1L;
	private final TimeSeriesPlot<?, ?, ?> plot;

	private static final List<PlotType> allTypes = Arrays.asList(
			PlotType.LINE,
			PlotType.LINE_WITH_POINTS,
			PlotType.LINE_STACKED,
			PlotType.STEPS,
			PlotType.POINTS,
			PlotType.BAR,
			PlotType.BAR_STACKED
	);

	public PlotTypeSelector(WidgetPage<?> page, String id, TimeSeriesPlot<?, ?, ?> plot) {
		this(page, id, plot, allTypes);
	}

	public PlotTypeSelector(WidgetPage<?> page, String id, TimeSeriesPlot<?, ?, ?> plot, Collection<PlotType> supportedTypes) {
		super(page, id);
		this.plot = Objects.requireNonNull(plot);
		this.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		setDefaultItems(supportedTypes);
		setDefaultAddEmptyOption(true, "Automatic");
		setTemplate(TEMPLATE);
	}

	@Override
	public void onGET(OgemaHttpRequest req) {
		final PlotType selected = getSelectedItem(req);
		final boolean doTrigger = selected != null && getPage().getTriggeringWidget(req) == this;
		final OgemaWidget target = plot instanceof TimeSeriesPlotGeneric ? ((TimeSeriesPlotGeneric) plot).getPlotWidget(req) : plot;
		if (!doTrigger)
			removeTriggerAction(target, TriggeringAction.GET_REQUEST, Plot2DOptions.adaptPlotType(PlotType.LINE), req);
		else
			triggerAction(target, TriggeringAction.GET_REQUEST, Plot2DOptions.adaptPlotType(selected), req);
	}

	private static final DisplayTemplate<PlotType> TEMPLATE = new DisplayTemplate<PlotType>() {

		@Override
		public String getId(PlotType object) {
			return object.getId();
		}

		@Override
		public String getLabel(PlotType object, OgemaLocale locale) {
			return object.toString();
		}
	};


}
