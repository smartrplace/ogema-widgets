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

import java.util.ArrayList;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.GenericFloatConfiguration;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.FlexboxData;

// TODO Flexbox configuration
public class ConfigurationWidget<C extends ConfigurationInstance> extends Flexbox {

	private static final long serialVersionUID = 1L;
	protected final Configuration<C> config;
	private final List<OgemaWidget> valueWidgets = new ArrayList<>();
	
	public ConfigurationWidget(WidgetPage<?> page, String id, Configuration<C> config) {
		super(page, id, true);
		this.config = config;
		init((OgemaHttpRequest) null);
	}

	public ConfigurationWidget(OgemaWidget parent, String id, OgemaHttpRequest req, Configuration<C> config) {
		super(parent, id, req);
		this.config = config;
		init(req);
	}
	
	@SuppressWarnings("unchecked")
	protected void init(OgemaHttpRequest req) {
		final Class<? extends ConfigurationInstance> type  =config.configurationType();
		if (GenericFloatConfiguration.class.isAssignableFrom(type)) {
			final ValueInputField<Float> input; 
			if (isGlobalWidget())
				input = new FloatConfigField(getPage(), getId() + "_value", (ConfigurationWidget<GenericFloatConfiguration>) this);
			else	
				input = new FloatConfigField(this, getId()+"_value", req, (ConfigurationWidget<GenericFloatConfiguration>) this);
			valueWidgets.add(input);
		}
		else if (DateConfiguration.class.isAssignableFrom(type)) {
			final Datepicker datepicker;
			if (isGlobalWidget())
				datepicker = new DateConfigPicker(getPage(), getId() + "_value", (ConfigurationWidget<DateConfiguration>) this);
			else
				datepicker = new DateConfigPicker(this, getId() + "_value", req, (ConfigurationWidget<DateConfiguration>) this);
			valueWidgets.add(datepicker);
		}
		for (OgemaWidget widget : valueWidgets) {
			addItem(widget, req);
		}
	}
	
	@Override
	public FlexboxData createNewSession() {
		return new ConfigurationWidgetData<C>(this);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public C getSelectedInstance(OgemaHttpRequest req) {
		return (C) ((ConfigurationWidgetData) getData(req)).instance;
	}
	
	@SuppressWarnings("unchecked")
	private void setSelectedInstance(C instance, OgemaHttpRequest req) {
		((ConfigurationWidgetData<C>) getData(req)).instance = instance;
	}
	
	private static class ConfigurationWidgetData<C extends ConfigurationInstance> extends FlexboxData {

		private C instance;
		
		public ConfigurationWidgetData(ConfigurationWidget<C> flexbox) {
			super(flexbox);
		}
	}
	
	private static class FloatConfigField extends ValueInputField<Float> {
		
		private static final long serialVersionUID = 1L;
		private final ConfigurationWidget<GenericFloatConfiguration> configWidget;
		
		public FloatConfigField(WidgetPage<?> page, String id, 	ConfigurationWidget<GenericFloatConfiguration> configWidget) {
			super(page, id, Float.class);
			this.configWidget = configWidget;
		}

		public FloatConfigField(OgemaWidget parent, String id, OgemaHttpRequest req,
				ConfigurationWidget<GenericFloatConfiguration> configWidget) {
			super(parent, id, Float.class, req);
			this.configWidget = configWidget;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final GenericFloatConfiguration c = configWidget.config.defaultValues();
			if (c != null) 
				setNumericalValue(c.getValue(), req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final Float value = getNumericalValue(req);
			GenericFloatConfiguration config = null;
			if (value != null) {
				config = new GenericFloatConfiguration(value, configWidget.config);
			}
			configWidget.setSelectedInstance(config, req);
		}
		
	}
	
	private static class DateConfigPicker extends Datepicker {
		
		private static final long serialVersionUID = 1L;
		private final ConfigurationWidget<DateConfiguration> configWidget;
		
		public DateConfigPicker(WidgetPage<?> page, String id, ConfigurationWidget<DateConfiguration> configWidget) {
			super(page, id);
			this.configWidget = configWidget;
		}
		
		public DateConfigPicker(OgemaWidget parent, String id, OgemaHttpRequest req, ConfigurationWidget<DateConfiguration> configWidget) {
			super(parent, id, req);
			this.configWidget = configWidget;
		}
		
		// TODO set value from selected schedules (?)
		@Override
		public void onGET(OgemaHttpRequest req) {
			final DateConfiguration c = configWidget.config.defaultValues();
			if (c != null) 
				setDate(c.getValue(), req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final long value = getDateLong(req);
			DateConfiguration config = new DateConfiguration(value, configWidget.config);
			configWidget.setSelectedInstance(config, req);
		}
		
		
	}

}
