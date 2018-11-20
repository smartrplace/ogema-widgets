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
package de.iwes.timeseries.eval.viewer.gui;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
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
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.template.DisplayTemplate;

class ConfigPopupProfile extends Popup {

	private static final long serialVersionUID = 1L;
	private final TemplateDropdown<Profile> selector;
	private final ValueInputField<Float> scaleField;
	private final ValueInputField<Float> offsetField;
	private final SimpleCheckbox doResampleField;
	private final ValueInputField<Long> resampleDurationField;
	private final TemplateDropdown<TemporalUnit> resampleUnitField;
	private final ProfileSettingsWidget settings;
	private final Button closeBtn;

	ConfigPopupProfile(WidgetPage<?> page, String id, final TemplateMultiselect<Profile> mainProfileSelector, final TimeSeriesPlot<?, ?, ?> schedulePlot) {
		super(page, id, true);
		this.selector = new TemplateDropdown<Profile>(page, "selector_" + id) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final List<Profile> schedules = mainProfileSelector.getSelectedItems(req);
				update(schedules, req);
			}

		};
		selector.setTemplate(mainProfileSelector.getTemplate());
		this.scaleField = new ValueInputField<Float>(page, "scaleField_" + id, Float.class) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null) {
					setNumericalValue(1F, req);
					return;
				}
				setNumericalValue(settings.getScale(profile, req), req);
			}

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final Float val = getNumericalValue(req);
				if (val == null || val <= 0)
					return;
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				final ScheduleData<?> scheduleData = schedulePlot.getScheduleData(req);
				for (SchedulePresentationData spd : scheduleData.getSchedules().values()) {
					if (spd instanceof ProfileSchedulePresentationData && ((ProfileSchedulePresentationData) spd).getProfile() == profile) {
						scheduleData.setScale(spd.getLabel(req.getLocale()), val, req);
					}
				}
				settings.setScale(profile, val, req);
			}

		};

		this.offsetField = new ValueInputField<Float>(page, "offsetField_" + id, Float.class) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null) {
					setNumericalValue(0F, req);
					return;
				}
				setNumericalValue(settings.getOffset(profile, req), req);
			}

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final Float val = getNumericalValue(req);
				if (val == null)
					return;
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				final ScheduleData<?> scheduleData = schedulePlot.getScheduleData(req);
				for (SchedulePresentationData spd : scheduleData.getSchedules().values()) {
					if (spd instanceof ProfileSchedulePresentationData && ((ProfileSchedulePresentationData) spd).getProfile() == profile) {
						scheduleData.setOffset(spd.getLabel(req.getLocale()), val, req);
					}
				}
				settings.setOffset(profile, val, req);
			}

		};

		this.doResampleField = new SimpleCheckbox(page, "doResample_" + id, "") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null) {
					setValue(false, req);
					return;
				}
				final ConfigPopupProfileData data = (ConfigPopupProfileData) settings.getData(req);
				setValue(data.isResampling(profile), req);
			}

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				final boolean resample = getValue(req);
				((ConfigPopupProfileData) settings.getData(req)).setResampling(profile, resample);
			}


		};
		this.resampleDurationField = new ValueInputField<Long>(page, "resampleDurationField_" + id, Long.class) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final Profile profile = selector.getSelectedItem(req);
				final ConfigPopupProfileData data = (ConfigPopupProfileData) settings.getData(req);
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
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				Long duration = getNumericalValue(req);
				if (duration == null)
					duration = 0L;
				((ConfigPopupProfileData) settings.getData(req)).setDuration(profile, duration);
			}

		};
		this.resampleUnitField = new TemplateDropdown<TemporalUnit>(page, "resampleUnitField_" + id) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final ConfigPopupProfileData data = (ConfigPopupProfileData) settings.getData(req);
				final Profile profile = selector.getSelectedItem(req);
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
				final Profile profile = selector.getSelectedItem(req);
				if (profile == null)
					return;
				((ConfigPopupProfileData) settings.getData(req)).setUnit(profile, getSelectedItem(req));
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

		this.closeBtn = new Button(page, "closeBtn_" + id, "Close");
		this.settings = new ProfileSettingsWidget(page, "settings_" + id);

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

		snippet.append(t, null).linebreak(null).append(settings, null);
		this.setBody(snippet, null);
		this.setTitle("Schedule viewer configuration", null);
		this.setFooter(closeBtn, null);

		closeBtn.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		selector.triggerAction(offsetField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		selector.triggerAction(scaleField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		doResampleField.triggerAction(resampleDurationField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		doResampleField.triggerAction(resampleUnitField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}

	/**
	 * Call with opening button as argument
	 * @param widget
	 */
	void trigger(final OgemaWidget widget) {
		widget.triggerAction(selector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(scaleField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(offsetField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(doResampleField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(resampleDurationField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(resampleUnitField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		widget.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
	}

	 float getScale(final Profile profile, OgemaHttpRequest req) {
		 return settings.getScale(profile, req);
	 }

	 float getOffset(final Profile profile, OgemaHttpRequest req) {
		 return settings.getOffset(profile, req);
	 }

	 TemporalUnit getResampleUnit(final Profile profile, final OgemaHttpRequest req) {
		 return settings.getResampleUnit(profile, req);
	 }

	 long getResampleDuration(final Profile profile, final OgemaHttpRequest req) {
		 return settings.getResampleDuration(profile, req);
	 }

	boolean isResampling(final Profile profile, final OgemaHttpRequest req) {
		return ((ConfigPopupProfileData) settings.getData(req)).isResampling(profile);
	}

	void clear(final OgemaHttpRequest req) {
		((ConfigPopupProfileData) settings.getData(req)).profileSettings.clear();
	}

	private static class ProfileSettingsWidget extends EmptyWidget {

		private static final long serialVersionUID = 1L;

		public ProfileSettingsWidget(WidgetPage<?> page, String id) {
			super(page, id);
		}

		@Override
		public EmptyData createNewSession() {
			return new ConfigPopupProfileData(this);
		}

		private void setScale(final Profile profile, final float scale, final OgemaHttpRequest req) {
			((ConfigPopupProfileData) getData(req)).setScale(profile, scale);
		}

		private float getScale(final Profile profile, final OgemaHttpRequest req) {
			return ((ConfigPopupProfileData) getData(req)).getScale(profile);
		}

		private void setOffset(final Profile profile, final float offset, final OgemaHttpRequest req) {
			((ConfigPopupProfileData) getData(req)).setOffset(profile, offset);
		}

		private float getOffset(final Profile profile, final OgemaHttpRequest req) {
			return ((ConfigPopupProfileData) getData(req)).getOffset(profile);
		}

		private TemporalUnit getResampleUnit(final Profile profile, final OgemaHttpRequest req) {
			return ((ConfigPopupProfileData) getData(req)).getUnit(profile);
		}

		private long getResampleDuration(final Profile profile, final OgemaHttpRequest req) {
			return ((ConfigPopupProfileData) getData(req)).getDuration(profile);
		}

	}

	private static class ConfigPopupProfileData extends EmptyData {

		private final Map<Profile, PlotSettings> profileSettings = new HashMap<>(2);

		public ConfigPopupProfileData(EmptyWidget popup) {
			super(popup);
		}

		private PlotSettings getOrCreateSettings(Profile profile) {
			if (profile == null)
				return null;
			PlotSettings settings = profileSettings.get(profile);
			if (settings == null) {
				settings = new PlotSettings();
				profileSettings.put(profile, settings);
			}
			return settings;
		}

		private long getDuration(final Profile profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return 1;
			long duration = profileSettings.get(profile).resampleDuration;
			if (duration <= 0)
				duration = 1;
			return duration;
		}

		private void setDuration(final Profile profile, long duration) {
			if (profile == null)
				return;
			if (duration <= 0)
				duration = 1;
			final PlotSettings settings = getOrCreateSettings(profile);
			settings.resampleDuration = duration;
		}

		private TemporalUnit getUnit(final Profile profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return null;
			return profileSettings.get(profile).resampleUnit;
		}

		private void setUnit(final Profile profile,TemporalUnit unit) {
			if (profile == null)
				return;
			final PlotSettings settings = getOrCreateSettings(profile);
			if (unit == null)
				unit = ChronoUnit.HOURS;
			settings.resampleUnit = unit;
		}

		private void setScale(final Profile profile, final float scale) {
			if (profile == null || scale <= 0)
				return;
			float[] val = getOrCreateSettings(profile).sizeSettings;
			val[0] = scale;
		}

		private float getScale(final Profile profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return 1F;
			return profileSettings.get(profile).sizeSettings[0];
		}

		private void setOffset(final Profile profile, final float offset) {
			if (profile == null)
				return;
			float[] val = getOrCreateSettings(profile).sizeSettings;
			val[1] = offset;
		}

		private float getOffset(final Profile profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return 0F;
			return profileSettings.get(profile).sizeSettings[1];
		}

		private void setResampling(final Profile profile, boolean resample) {
			if (profile == null)
				return;
			getOrCreateSettings(profile).doResample = resample;
		}

		private boolean isResampling(final Profile profile) {
			if (profile == null || !profileSettings.containsKey(profile))
				return false;
			return profileSettings.get(profile).doResample;
		}

	}

	// one instance per profile per session
	private static class PlotSettings {

		// [scale, offset]
		private float[] sizeSettings = new float[2];
		private long resampleDuration = 1;
		private TemporalUnit resampleUnit = ChronoUnit.HOURS;
		private boolean doResample = false;

	}

}
