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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.extended.plus.SelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleplot.c3.SchedulePlotC3;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;
import de.iwes.widgets.reswidget.scheduleplot.morris2.SchedulePlotMorris;
import de.iwes.widgets.reswidget.scheduleplot.nvd3.SchedulePlotNvd3;
import de.iwes.widgets.reswidget.scheduleplot.plotchartjs.SchedulePlotChartjs;
import de.iwes.widgets.reswidget.scheduleplot.plotlyjs.SchedulePlotlyjs;

/**
 * Select the plot libary, such as Chart.js, Plotly or Flotcharts
 */
@SuppressWarnings("rawtypes")
public class SchedulePlotWidgetSelector extends Dropdown implements InitWidget, SelectorTemplate<Class<? extends TimeSeriesPlot>> {

	private static final String PAGE_PARAMETER= "viewerId";
	private static final long serialVersionUID = 1L;
	private static final List<DropdownOption> options;

	private static DropdownOption getOption(final Class<? extends TimeSeriesPlot> type) {
		String label = type.getSimpleName().replace("SchedulePlot", "");
		if (label.equals("lyjs"))
			label = "Plotlyjs";
		if (label.endsWith("js") && !label.endsWith(".js"))
			label = label.substring(0, label.length()-2) + ".js";
		return new DropdownOption(type.getName(), label, type == SchedulePlotlyjs.class);
	}

	static {
		options = Stream.<Class<? extends TimeSeriesPlot>> builder()
			.add(SchedulePlotChartjs.class)
			.add(SchedulePlotlyjs.class)
			.add(SchedulePlotFlot.class)
			.add(SchedulePlotNvd3.class)
			.add(SchedulePlotMorris.class)
			.add(SchedulePlotC3.class)
			.build()
			.map(SchedulePlotWidgetSelector::getOption)
			.collect(Collectors.toList());
	}

	public SchedulePlotWidgetSelector(WidgetPage<?> page, String id) {
		this(page, id, false);
	}

	public SchedulePlotWidgetSelector(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDefaultOptions(new ArrayList<>(options));
	}

	public SchedulePlotWidgetSelector(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		setDefaultOptions(new ArrayList<>(options));
	}

	public void selectItem(Class<? extends TimeSeriesPlot> type, OgemaHttpRequest req) {
		selectSingleOption(type.getName(), req);
	}

	public void selectDefaultItem(Class<? extends TimeSeriesPlot> type) {
		final String value = type.getName();
		getDefaultOptions().forEach(opt -> opt.select(value.equals(opt.id())));
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends TimeSeriesPlot> getSelectedItem(OgemaHttpRequest req) {
		try {
			return (Class<? extends TimeSeriesPlot>) Class.forName(getSelectedValue(req), true, SchedulePlotWidgetSelector.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unexpected ClassNotFound: ",e);
		}
	}

	@Override
	public void init(OgemaHttpRequest req) {
		final Map<String,String[]> params = getPage().getPageParameters(req);
		if (params == null || !params.containsKey(PAGE_PARAMETER) || params.get(PAGE_PARAMETER).length == 0)
			return;
		final String id = params.get(PAGE_PARAMETER)[0];
		selectSingleOption(id, req);
	}




}
