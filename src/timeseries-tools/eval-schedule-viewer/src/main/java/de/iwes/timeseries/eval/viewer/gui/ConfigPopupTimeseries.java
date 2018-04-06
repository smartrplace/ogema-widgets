package de.iwes.timeseries.eval.viewer.gui;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.DataTree;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.SimpleCheckbox;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultTimeSeriesDisplayTemplate;
import de.iwes.widgets.template.DisplayTemplate;

class ConfigPopupTimeseries extends Popup {
	
	private static final long serialVersionUID = 1L;
	private final TemplateDropdown<ReadOnlyTimeSeries> selector;
	private final ValueInputField<Float> scaleField;
	private final ValueInputField<Float> offsetField;
	private final Button closeBtn;
	private final SimpleCheckbox doResampleField;
	private final ValueInputField<Long> resampleDurationField;
	private final TemplateDropdown<TemporalUnit> resampleUnitField;
	private final ReadOnlyTimeSeriesSettingsWidget settings;
	private final DisplayTemplate<ReadOnlyTimeSeries> scheduleTemplate;

	ConfigPopupTimeseries(WidgetPage<?> page, String id, final DataTree dataTree, final TimeSeriesPlot<?, ?> schedulePlot) {
		super(page, id, true);
		this.scheduleTemplate = new DefaultTimeSeriesDisplayTemplate<>(getNameService());
		this.selector = new TemplateDropdown<ReadOnlyTimeSeries>(page, "selector_" + id) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final List<ReadOnlyTimeSeries> schedules = dataTree.getSelectedSchedules(req);
				update(schedules, req);
			}
			
		};
		selector.setTemplate(scheduleTemplate);
		this.scaleField = new ValueInputField<Float>(page, "scaleField_" + id, Float.class) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final ReadOnlyTimeSeries schedule = selector.getSelectedItem(req);
				if (schedule == null) {
					setNumericalValue(1F, req);
					return;
				}
//				final String label = scheduleTemplate.getLabel(schedule, req.getLocale()); // or get id?
				final String label = scheduleTemplate.getId(schedule);
				final float val = schedulePlot.getScheduleData(req).getScale(label, req);
				setNumericalValue(val, req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final Float val = getNumericalValue(req);
				if (val == null || val <= 0)
					return;
				final ReadOnlyTimeSeries schedule = selector.getSelectedItem(req);
				if (schedule == null) 
					return;
				final String label = scheduleTemplate.getId(schedule);
				schedulePlot.getScheduleData(req).setScale(label, val, req);
			}
			
		};
		
		this.offsetField = new ValueInputField<Float>(page, "offsetField_" + id, Float.class) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final ReadOnlyTimeSeries schedule = selector.getSelectedItem(req);
				if (schedule == null) {
					setNumericalValue(1F, req);
					return;
				}
//				final String label = scheduleTemplate.getLabel(schedule, req.getLocale()); // or get id?
				final String label = scheduleTemplate.getId(schedule);
				final float val = schedulePlot.getScheduleData(req).getOffset(label, req);
				setNumericalValue(val, req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final Float val = getNumericalValue(req);
				if (val == null)
					return;
				final ReadOnlyTimeSeries schedule = selector.getSelectedItem(req);
				if (schedule == null) 
					return;
				final String label = scheduleTemplate.getId(schedule);
				schedulePlot.getScheduleData(req).setOffset(label, val, req);
			}
			
		};
	this.doResampleField = new SimpleCheckbox(page, "doResample_" + id, "") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final ReadOnlyTimeSeries profile = selector.getSelectedItem(req);
				if (profile == null) {
					setValue(false, req);
					return;
				}
				final ConfigPopupTimeseriesData data = (ConfigPopupTimeseriesData) settings.getData(req);
				setValue(data.isResampling(profile), req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final ReadOnlyTimeSeries profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				final boolean resample = getValue(req);
				((ConfigPopupTimeseriesData) settings.getData(req)).setResampling(profile, resample);
			}
			
			
		};
		this.resampleDurationField = new ValueInputField<Long>(page, "resampleDurationField_" + id, Long.class) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final ReadOnlyTimeSeries profile = selector.getSelectedItem(req);
				final ConfigPopupTimeseriesData data = (ConfigPopupTimeseriesData) settings.getData(req);
				if (profile == null || !doResampleField.getValue(req)) {
					setNumericalValue(null, req);
					disable(req);
					return;
				}
				enable(req);
				setNumericalValue(data.getDuration(profile), req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final ReadOnlyTimeSeries profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				Long duration = getNumericalValue(req);
				if (duration == null)
					duration = 0L;
				((ConfigPopupTimeseriesData) settings.getData(req)).setDuration(profile, duration);
			}
			
		};
		this.resampleUnitField = new TemplateDropdown<TemporalUnit>(page, "resampleUnitField_" + id) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final ConfigPopupTimeseriesData data = (ConfigPopupTimeseriesData) settings.getData(req);
				final ReadOnlyTimeSeries profile = selector.getSelectedItem(req);
				if (profile == null || !doResampleField.getValue(req)) {
					selectItem(ChronoUnit.HOURS, req);
					disable(req);
					return;
				}
				enable(req);
				final TemporalUnit unit = data.getUnit(profile);
				if (unit == null)
					selectItem(ChronoUnit.HOURS, req);
				else
					selectItem(unit, req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final ReadOnlyTimeSeries profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				((ConfigPopupTimeseriesData) settings.getData(req)).setUnit(profile, getSelectedItem(req));
			}
			
		};
		resampleUnitField.setDefaultItems(Arrays.asList(
				ChronoUnit.MINUTES,
				ChronoUnit.HOURS,
				ChronoUnit.HALF_DAYS,
				ChronoUnit.DAYS,
				ChronoUnit.WEEKS,
				ChronoUnit.MONTHS,
				ChronoUnit.YEARS
		));
		resampleUnitField.selectDefaultItem(ChronoUnit.HOURS);
		resampleUnitField.setTemplate(new DisplayTemplate<TemporalUnit>() {
			
			@Override
			public String getLabel(TemporalUnit object, OgemaLocale locale) {
				return object.toString();
			}
			
			@Override
			public String getId(TemporalUnit object) {
				return object.toString();
			}
		});
		this.settings = new ReadOnlyTimeSeriesSettingsWidget(page, "settings_" + id);
		this.closeBtn = new Button(page, "closeBtn_" + id, "Close");
		
		
		final Flexbox resampleDurationBox = new Flexbox(page, "resampleDurationBox_" + id, true);
		resampleDurationBox.addItem(resampleDurationField, null).addItem(resampleUnitField, null);
		resampleDurationBox.setDefaultJustifyContent(JustifyContent.SPACE_BETWEEN);
		resampleDurationBox.setDefaultAlignContent(AlignContent.CENTER);
		
		PageSnippet snippet = new PageSnippet(page, "snippet_"+id, true);
		int row = 0;
		final StaticTable t = new StaticTable(5, 2, new int[]{4,8})
				.setContent(row, 0, "Select schedule").setContent(row++, 1, selector)
				.setContent(row, 0, "Scale").setContent(row++, 1, scaleField)
				.setContent(row, 0, "Offset").setContent(row++, 1, offsetField)
				.setContent(row, 0, "Resample time series").setContent(row++, 1, doResampleField)
				.setContent(row, 0, "Length").setContent(row++, 1, resampleDurationBox);
		snippet.append(t, null);
		this.setBody(snippet, null);
		this.setTitle("Schedule viewer configuration", null);
		this.setFooter(closeBtn, null);
		
		closeBtn.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		selector.triggerAction(offsetField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		selector.triggerAction(scaleField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		doResampleField.triggerAction(resampleDurationField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		doResampleField.triggerAction(resampleUnitField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	void trigger(final OgemaWidget widget) {
		widget.triggerAction(selector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(scaleField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(offsetField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(doResampleField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(resampleDurationField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(resampleUnitField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
	}
	
	 TemporalUnit getResampleUnit(final ReadOnlyTimeSeries profile, final OgemaHttpRequest req) {
		 return settings.getResampleUnit(profile, req);
	 }
		
	long getResampleDuration(final ReadOnlyTimeSeries profile, final OgemaHttpRequest req) {
		return settings.getResampleDuration(profile, req);
	}
	 
	boolean isResampling(final ReadOnlyTimeSeries profile, final OgemaHttpRequest req) {
		return ((ConfigPopupTimeseriesData) settings.getData(req)).isResampling(profile);
	}
	
	void clear(final OgemaHttpRequest req) {
		((ConfigPopupTimeseriesData) settings.getData(req)).profileSettings.clear();
	}
	 
	private static class ReadOnlyTimeSeriesSettingsWidget extends EmptyWidget {

		private static final long serialVersionUID = 1L;

		public ReadOnlyTimeSeriesSettingsWidget(WidgetPage<?> page, String id) {
			super(page, id);
		}
		
		@Override
		public EmptyData createNewSession() {
			return new ConfigPopupTimeseriesData(this);
		}
		
		private TemporalUnit getResampleUnit(final ReadOnlyTimeSeries profile, final OgemaHttpRequest req) {
			return ((ConfigPopupTimeseriesData) getData(req)).getUnit(profile);
		}
		
		private long getResampleDuration(final ReadOnlyTimeSeries profile, final OgemaHttpRequest req) {
			return ((ConfigPopupTimeseriesData) getData(req)).getDuration(profile);
		}
		
	}
	
	private static class ConfigPopupTimeseriesData extends EmptyData {
		
		private final Map<ReadOnlyTimeSeries, PlotSettings> profileSettings = new HashMap<>(2);

		public ConfigPopupTimeseriesData(EmptyWidget empty) {
			super(empty);
		}
		
		private PlotSettings getOrCreateSettings(ReadOnlyTimeSeries profile) {
			if (profile == null)
				return null;
			PlotSettings settings = profileSettings.get(profile);
			if (settings == null) {
				settings = new PlotSettings();
				profileSettings.put(profile, settings);
			}
			return settings;
		}
		
		private void setResampling(final ReadOnlyTimeSeries profile, boolean resample) {
			if (profile == null)
				return;
			getOrCreateSettings(profile).doResample = resample;
		}
		
		private boolean isResampling(final ReadOnlyTimeSeries profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return false;
			return profileSettings.get(profile).doResample;
		}
		
		private long getDuration(final ReadOnlyTimeSeries profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return 1;
			long duration = profileSettings.get(profile).resampleDuration;
			if (duration <= 0)
				duration = 1;
			return duration;
		}
		
		private void setDuration(final ReadOnlyTimeSeries profile, long duration) {
			if (profile == null)
				return;
			if (duration <= 0)
				duration = 1;
			final PlotSettings settings = getOrCreateSettings(profile);
			settings.resampleDuration = duration;
		}
		
		private TemporalUnit getUnit(final ReadOnlyTimeSeries profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return null;
			return profileSettings.get(profile).resampleUnit;
		}
		
		private void setUnit(final ReadOnlyTimeSeries profile,TemporalUnit unit) {
			if (profile == null)
				return;
			final PlotSettings settings = getOrCreateSettings(profile);
			if (unit == null)
				unit = ChronoUnit.HOURS;
			settings.resampleUnit = unit;
		}
		
		
	}
	
	// one instance per time serise per session
	private static class PlotSettings {		
		// [scale, offset]
		private long resampleDuration = 1; 
		private TemporalUnit resampleUnit = ChronoUnit.HOURS;
		private boolean doResample = false;
		
	}
	
}
