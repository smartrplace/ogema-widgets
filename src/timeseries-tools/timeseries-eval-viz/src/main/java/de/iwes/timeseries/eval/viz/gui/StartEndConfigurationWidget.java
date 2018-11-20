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
package de.iwes.timeseries.eval.viz.gui;

import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;

// TODO trigger update when selections change
public class StartEndConfigurationWidget extends ConfigurationWidget<DateConfiguration> {

	private static final long serialVersionUID = 1L;
	private final Datepicker datepicker;

	public StartEndConfigurationWidget(OgemaWidget parent, String id, OgemaHttpRequest req,
			Configuration<DateConfiguration> config, ConfigurationPopupSnippet configSnippet, boolean startOrEnd) {
		super(parent, id, req, config);
		this.datepicker = init2(configSnippet, startOrEnd, req);
	}
	
	public StartEndConfigurationWidget(WidgetPage<?> page, String id, Configuration<DateConfiguration> config,
			ConfigurationPopupSnippet configSnippet, boolean startOrEnd) {
		super(page, id, config);
		this.datepicker = init2(configSnippet, startOrEnd,  null);
	}
	
	@Override
	protected void init(OgemaHttpRequest req) {}

	protected Datepicker init2(ConfigurationPopupSnippet configSnippet, boolean startOrEnd, OgemaHttpRequest req) {
		final Datepicker datepicker;
		if (isGlobalWidget())
			datepicker = new StartEndPicker(getPage(), getId() + "_value", startOrEnd, configSnippet);
		else
			datepicker = new StartEndPicker(this, getId() + "_value", req, startOrEnd, configSnippet);
		addItem(datepicker, req);
		return datepicker;
	}

	@Override
	public DateConfiguration getSelectedInstance(OgemaHttpRequest req) {
		final long date = datepicker.getDateLong(req);
		if (date == Long.MIN_VALUE)
			return null;
		return new DateConfiguration(date, config);
	}
	
	private static class StartEndPicker extends Datepicker {
		
 		private static final long serialVersionUID = 1L;
 		/**true = start*/
		private final boolean startOrEnd;
		private final ConfigurationPopupSnippet configSnippet;

		public StartEndPicker(WidgetPage<?> page, String id, boolean startOrEnd, ConfigurationPopupSnippet configSnippet) {
			super(page, id);
			this.startOrEnd = startOrEnd;
			this.configSnippet = configSnippet;
		}
		
		public StartEndPicker(OgemaWidget parent, String id, OgemaHttpRequest req, boolean startOrEnd, ConfigurationPopupSnippet configSnippet) {
			super(parent, id, req);
			this.startOrEnd = startOrEnd;
			this.configSnippet = configSnippet;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			if (!configSnippet.isVisible(req))
				return;
			long t = EvaluationUtils.getDefaultStartEndTimeForInput(configSnippet.getSelectedTimeseries(req), startOrEnd);
			setDate(t, req);
		}
	}
}
