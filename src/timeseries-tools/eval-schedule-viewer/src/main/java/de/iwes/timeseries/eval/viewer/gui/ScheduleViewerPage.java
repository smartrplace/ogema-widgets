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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.aggregation.api.StandardIntervalTimeseriesBuilder;
import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileCategory;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.DataTree;
import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.LabelledItemSelectorMulti;
import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.LabelledItemSelectorSingle;
import de.iwes.timeseries.eval.viewer.impl.profile.PresentationDataImpl;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.plus.MultiSelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.plot.api.Plot2DOptions;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.schedulecsvdownload.ScheduleCsvDownload;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleplot.container.PlotTypeSelector;
import de.iwes.widgets.reswidget.scheduleplot.container.SchedulePlotWidgetSelector;
import de.iwes.widgets.reswidget.scheduleplot.container.TimeSeriesPlotGeneric;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.template.DisplayTemplate;

// TODO
// - fix interval checkbox
// - downsampling to standard interval, such 1h, 1d, 1w
// - config popup: partial transparency for plot; colors?
// - configure minimum time interval between points (alternative downsampling)
// - change plot style client-side (needs more general trigger action concept, that allows to
// 		pass parameters, such as plot style and height) -> done; add individual trigger buttons?
// - individual line types for plots?
public class ScheduleViewerPage {

	private final WidgetPage<?> page;
	private final Header header;
	private final LabelledItemSelectorSingle<DataProvider<?>> dataProviderSelector;
//	private final LabelledItemSelectorSingle<ProfileCategory> profileCategorySelector;
	private final LabelledItemSelectorMulti<ProfileCategory> profileCategorySelector;
	private final TemplateMultiselect<Profile> profilesSelector;
	private final DataTree dataTree;
	private final ViewerDatepicker startTimePicker;
	private final ViewerDatepicker endTimePicker;
	private final Label sizeLabel;
	private final ValueInputField<Integer> heightField;
	private final SchedulePlotWidgetSelector plotLibSelector;
	private final TemplateDropdown<PlotType> lineTypeSelector;
	private final EmptyWidget clientSideHeightSetter;
	private final Checkbox2 aggregationCheckbox;
	private final TemplateDropdown<LinkingOption> aggregationLevel;
	private final ConfigPopupProfile profileConfig;
	private final ConfigPopupTimeseries timeSeriesConfig;
	private final Button profileConfigTrigger;
	private final Button timeSeriesConfigTrigger;
	private final Button configClearBtn;
	private final Button apply;
	private final TimeSeriesPlot<?, ?, ?> schedulePlot;
	private final Header downloadHeader;
	private final ScheduleCsvDownload<ReadOnlyTimeSeries> csvDownload;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ScheduleViewerPage(
			final WidgetPage<?> page,
			final LabelledItemProvider<DataProvider<?>> dataProviders,
			final LabelledItemProvider<ProfileCategory> profileProviders,
			final ApplicationManager am) {
		this.page = page;
		this.header = new Header(page, "header", true);
		header.setDefaultText("Schedule viewer");
//		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		header.setDefaultColor("blue");
		dataProviderSelector = new LabelledItemSelectorSingle<DataProvider<?>>(page, "dataProviderSelector", dataProviders);
		profileCategorySelector = new LabelledItemSelectorMulti<>(page, "profileCategorySelector", profileProviders);
		profilesSelector = new TemplateMultiselect<Profile>(page, "profilesSelector") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final List<ProfileCategory> category = (List) profileCategorySelector.getSelectedItems(req);
				if (category == null || category.isEmpty()) {
					update(Collections.<Profile> emptyList(), req);
					return;
				}
				if (category.size() == 1)
					update(category.get(0).getProfiles(), req);
				else {
					List<Profile> profiles = new ArrayList<>();
					for (ProfileCategory cat : category)
						profiles.addAll(cat.getProfiles());
					update(profiles, req);
				}
				profilesSelector.clearSessionTriggers(req);
				final OgemaWidget lastTerminal = dataTree.getTerminalSelectWidget(req);
				if (lastTerminal != null)
					profilesSelector.triggerAction(lastTerminal, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			}

		};
		profilesSelector.setTemplate((DisplayTemplate) LabelledItemUtils.LABELLED_ITEM_TEMPLATE);
		dataTree = new DataTree(page, "dataTree", dataProviderSelector) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTerminalFieldGetComplete(OgemaHttpRequest req) {
				setSchedulesByProfiles(req);
			}

		};
		startTimePicker =  new ViewerDatepicker(page, "startTimePicker", true, dataTree, this);
		endTimePicker = new ViewerDatepicker(page, "endTimePicker", false, dataTree, this);
		sizeLabel = new Label(page, "sizeLabel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final long start = startTimePicker.getDateLong(req);
				final long end = endTimePicker.getDateLong(req);
				if (start > end) {
					setText("0", req);
					return;
				}
				final List<ReadOnlyTimeSeries> schedules = dataTree.getSelectedSchedules(req);
				int sz = 0;
				for (ReadOnlyTimeSeries ts : schedules)
					sz += ts.size(start, end);
				setText(String.valueOf(sz), req);
			}

		};

		heightField = new ValueInputField<>(page, "heightField", Integer.class);
		heightField.setDefaultNumericalValue(500);
		heightField.setDefaultLowerBound(0);
		// FIXME can probably replace this widget by the height fiel onGET
		clientSideHeightSetter = new EmptyWidget(page,"clientSideHeightSetter") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final Integer height = heightField.getNumericalValue(req);
				if (height == null || height < 0) {
					removeTriggerAction(schedulePlot, TriggeringAction.GET_REQUEST, Plot2DOptions.adaptHeight(0), req);
					return;
				}
				triggerAction(schedulePlot, TriggeringAction.GET_REQUEST, Plot2DOptions.adaptHeight(height), req);
			}

		};

		this.aggregationCheckbox = new Checkbox2(page, "aggregationCheckbox");
		aggregationCheckbox.setDefaultCheckboxList(Arrays.asList(
				new DefaultCheckboxEntry("fixTime", "Fix interval on schedule switch", false),
				new DefaultCheckboxEntry("aggregate", "Aggregate time series", false)
		));


//		aggregationCheckbox = new SimpleCheckbox(page, "aggregationCheckbox", "");
//		aggregationCheckbox.setDefaultValue(false);

		aggregationLevel = new TemplateDropdown<LinkingOption>(page, "aggregationLevel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {

				if (!aggregateTimeSeries(req)) {
					update(Collections.<LinkingOption> emptyList(), req);
					disable(req);
					return;
				}
				enable(req);
				final Map<LinkingOption, MultiSelectorTemplate<SelectionItem>> linkingOptions = dataTree.getSelectionOptions(req);
				final LinkingOption terminalOption = dataTree.getTerminalOption(req);
				final List<LinkingOption> admissibleOptions = new ArrayList<>();
				for (Map.Entry<LinkingOption, MultiSelectorTemplate<SelectionItem>> entry : linkingOptions.entrySet()) {
					final LinkingOption lo = entry.getKey();
					if (lo == terminalOption)
						continue;
					if (!entry.getValue().getSelectedItems(req).isEmpty())
						admissibleOptions.add(lo);
				}
				update(admissibleOptions, req);
			}

		};
		aggregationLevel.setDefaultAddEmptyOption(true, "Aggregate by profile");
		aggregationLevel.setTemplate(new DisplayTemplate<LinkingOption>() {

			@Override
			public String getId(LinkingOption object) {
				return object.id();
			}

			@Override
			public String getLabel(LinkingOption object, OgemaLocale locale) {
				return object.label(locale);
			}
		});

		apply = new Button(page, "apply", "Apply");
		schedulePlot = new TimeSeriesPlotGeneric(page, "schedulePlot") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				 setPlotWidget(plotLibSelector.getSelectedItem(req), req);
				 final List<ReadOnlyTimeSeries> schedules = dataTree.getSelectedSchedules(req);
				 final Map<String, SchedulePresentationData> scheduleData = new LinkedHashMap<>();
				 final boolean aggregate = aggregateTimeSeries(req);
				 final List<Profile> profiles = profilesSelector.getSelectedItems(req);
				 if (!aggregate || profiles.isEmpty()) {
					 for (ReadOnlyTimeSeries ts : schedules) {
						 // TODO check
//						 scheduleData.put(getSchedulePath(ts), getSchedulePresentationData(ts, profilesSelector.getSelectedItems(req)));
						 SchedulePresentationData spd = getSchedulePresentationData(ts, profiles);
						 if (timeSeriesConfig.isResampling(ts, req)) {
							 final TemporalUnit unit = timeSeriesConfig.getResampleUnit(ts, req);
							 final long duration = timeSeriesConfig.getResampleDuration(ts, req);
							 spd = resample(spd, duration, unit);
						 }
						 scheduleData.put(spd.getLabel(req.getLocale()), spd);
					 }
				 }
				 else {
					 final LinkingOption aggLevel = aggregationLevel.getSelectedItem(req);
					 final List<SelectionItem> items;
					 final Map<LinkingOption, MultiSelectorTemplate<SelectionItem>> linkingOptions;
					 if (aggLevel!=null) {
						 linkingOptions = dataTree.getSelectionOptions(req);
						 final MultiSelectorTemplate<SelectionItem> selector = linkingOptions.get(aggLevel);
						 if (selector == null) {
							 items = null;
						 } else {
							 items = selector.getSelectedItems(req);
						 }
					 } else {
						 items = null;
						 linkingOptions = null;
					 }
					 if (items == null || items.isEmpty()) { // top level aggregation
						 final Map<Profile, List<ReadOnlyTimeSeries>> tsByProfiles = new LinkedHashMap<>();
						 for (ReadOnlyTimeSeries ts: schedules) {
							 final Profile profile = getApplicableProfile(ts, profiles);
							 if (profile == null) {
//								 scheduleData.put(getSchedulePath(ts), getSchedulePresentationData(ts, (Profile) null));
								 SchedulePresentationData spd = getSchedulePresentationData(ts, (Profile) null);
								 if (timeSeriesConfig.isResampling(ts, req)) {
									 final TemporalUnit unit = timeSeriesConfig.getResampleUnit(ts, req);
									 final long duration = timeSeriesConfig.getResampleDuration(ts, req);
									 spd = resample(spd, duration, unit);
								 }
								 scheduleData.put(spd.getLabel(req.getLocale()), spd);
							 } else {
								 if (!tsByProfiles.containsKey(profile)) {
									 tsByProfiles.put(profile, new ArrayList<ReadOnlyTimeSeries>());
								 }
								 tsByProfiles.get(profile).add(ts);
							 }
						 }
						 for (Map.Entry<Profile, List<ReadOnlyTimeSeries>> entry: tsByProfiles.entrySet()) {
							 final List<ReadOnlyTimeSeries> list = entry.getValue();
							 final Profile profile = entry.getKey();
							 SchedulePresentationData data = profile.aggregate(list, null);
							 if (data != null) {
								 if (profileConfig.isResampling(profile, req)) {
									 final long duration = profileConfig.getResampleDuration(profile, req);
									 final TemporalUnit unit = profileConfig.getResampleUnit(profile, req);
									 data = resample(data, duration, unit);
								 }
								 final String label = data.getLabel(req.getLocale());
								 scheduleData.put(label, data);
								 schedulePlot.getScheduleData(req).setScale(label, profileConfig.getScale(profile, req), req);
								 schedulePlot.getScheduleData(req).setOffset(label, profileConfig.getOffset(profile, req), req);
							 }
							 else {
								 for (ReadOnlyTimeSeries ts : list) {
//									 scheduleData.put(getSchedulePath(ts), getSchedulePresentationData(ts, profile));
									 SchedulePresentationData spd = getSchedulePresentationData(ts, profile);
									 if (timeSeriesConfig.isResampling(ts, req)) {
										 final TemporalUnit unit = timeSeriesConfig.getResampleUnit(ts, req);
										 final long duration = timeSeriesConfig.getResampleDuration(ts, req);
										 spd = resample(spd, duration, unit);
									 }
									 scheduleData.put(spd.getLabel(req.getLocale()), spd);
								 }
							 }
						 }
					 } else {
						 final Map<SelectionItem, Map<Profile, List<ReadOnlyTimeSeries>>> tsByProfiles = new LinkedHashMap<>();
						 final TerminalOption<?> terminalOption = dataTree.getTerminalOption(req);
						 final List<Collection<SelectionItem>> selected = new ArrayList<>();
						 for (Map.Entry<LinkingOption, MultiSelectorTemplate<SelectionItem>> entry : linkingOptions.entrySet()) {
							 if (entry.getKey() == aggLevel)
								 break;
							 selected.add(entry.getValue().getSelectedItems(req));
						 }
						 selected.add(Collections.<SelectionItem> emptyList());
//						 final List<Collection<SelectionItem>> selected = Collections.<Collection<SelectionItem>> singletonList(items);
						 final List<ReadOnlyTimeSeries> unassociated = new ArrayList<>();
						 // these items are at the aggregation level
						 for (SelectionItem item : items) {
							 selected.remove(selected.size()-1);
							 // FIXME does this work? Try to retrieve all time series associated to this particular selection item
							 selected.add(Collections.singletonList(item));
							 final List<SelectionItem> associatedTimeSeries = terminalOption.getOptions(selected);
							 final Iterator<ReadOnlyTimeSeries> it = schedules.iterator();
							 while (it.hasNext()) {
								 final ReadOnlyTimeSeries ts = it.next();
								 for (SelectionItem i : associatedTimeSeries) {
									 final ReadOnlyTimeSeries cand = (ReadOnlyTimeSeries) terminalOption.getElement(i);
									 if (ts.equals(cand)) {
										 it.remove();
										 final Profile p = getApplicableProfile(ts, profiles);
										 if (p == null) {
											 unassociated.add(ts);
											 break;
										 }
										 if (!tsByProfiles.containsKey(item)) {
											 tsByProfiles.put(item, new HashMap<Profile, List<ReadOnlyTimeSeries>>());
										 }
										 final Map<Profile, List<ReadOnlyTimeSeries>> map = tsByProfiles.get(item);
										 if (!map.containsKey(p)) {
											 map.put(p, new ArrayList<ReadOnlyTimeSeries>());
										 }
										 map.get(p).add(ts);
										 break;
									 }
								 }
							 }
						 }
						 // no applicable profile or option found
						 unassociated.addAll(schedules);
						 final OgemaLocale locale = req.getLocale();
						 for (Map.Entry<SelectionItem, Map<Profile, List<ReadOnlyTimeSeries>>> entry: tsByProfiles.entrySet()) {
							 final String itemLabel = entry.getKey().label(locale);
							 for (Map.Entry<Profile, List<ReadOnlyTimeSeries>> innerEntry : entry.getValue().entrySet()) {
								 final Profile p = innerEntry.getKey();
								 SchedulePresentationData presentationData = p.aggregate(innerEntry.getValue(), itemLabel);
								 if (profileConfig.isResampling(p, req)) {
									 final TemporalUnit unit = profileConfig.getResampleUnit(p, req);
									 final long duration = profileConfig.getResampleDuration(p, req);
									 presentationData = resample(presentationData, duration, unit);
								 }
								 final String label = presentationData.getLabel(req.getLocale());
								 scheduleData.put(label, presentationData);
								 schedulePlot.getScheduleData(req).setScale(label, profileConfig.getScale(p, req), req);
								 schedulePlot.getScheduleData(req).setOffset(label, profileConfig.getOffset(p, req), req);
							 }
						 }
						 for (ReadOnlyTimeSeries ts : unassociated) {
//							 scheduleData.put(getSchedulePath(ts), getSchedulePresentationData(ts, (Profile) null));
							 SchedulePresentationData spd = getSchedulePresentationData(ts, (Profile) null);
							 if (timeSeriesConfig.isResampling(ts, req)) {
								 final TemporalUnit unit = timeSeriesConfig.getResampleUnit(ts, req);
								 final long duration = timeSeriesConfig.getResampleDuration(ts, req);
								 spd = resample(spd, duration, unit);
							 }
							 scheduleData.put(spd.getLabel(req.getLocale()), spd);
						 }
					 }
				 }
				 getScheduleData(req).setSchedules(scheduleData);
				 long start = startTimePicker.getDateLong(req);
				 long end = endTimePicker.getDateLong(req);
				 if (start > end)
					 start = end;
				 boolean online = false;
				 for (SchedulePresentationData spd : getScheduleData(req).getSchedules().values()) {
					 if (spd instanceof ProfileSchedulePresentationData && ((ProfileSchedulePresentationData) spd).isOnlineTimeSeries()) {
						 online = true;
						 break;
					 }
				 }
				 if (online)
					 end = Long.MAX_VALUE;
				 setInterval(start, end, req);
				 Integer height = heightField.getNumericalValue(req);
				 if (height == null || height < 0) {
					 height = 500;
					 heightField.setNumericalValue(height, req);
				 }
				 setHeight(height + "px", req);
				 final PlotType type = lineTypeSelector.getSelectedItem(req);
				 getConfiguration(req).setPlotType(type); // type may be null here
				 final String unit = getUnit(getScheduleData(req).getSchedules().values());
				 getConfiguration(req).setYUnit(unit);
				 setPollingInterval(online ? 5000 : -1, req);
			}

		};
		// what about mobile devices?
		schedulePlot.setDefaultHeight("500px");

		plotLibSelector = new SchedulePlotWidgetSelector(page, "plotLibSelector");
		lineTypeSelector = new PlotTypeSelector(page, "lineTypeSelector", schedulePlot);

		profileConfig = new ConfigPopupProfile(page, "profileConfig", profilesSelector, schedulePlot);
		timeSeriesConfig = new ConfigPopupTimeseries(page, "timeSeriesConfig", dataTree, schedulePlot);
		profileConfigTrigger = new Button(page, "profileConfigTrigger", "Configure profiles");
		timeSeriesConfigTrigger = new Button(page, "timeSeriesConfigTrigger", "Configure individual time series");
		configClearBtn = new Button(page, "configClearBtn", "Clear all configurations") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				profileConfig.clear(req);
				timeSeriesConfig.clear(req);
			}

		};

		downloadHeader = new Header(page, "downloadHeader");
		downloadHeader.setDefaultHeaderType(3);
		downloadHeader.setDefaultText("Download CSV data");
		downloadHeader.setDefaultColor("blue");
		downloadHeader.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		csvDownload = new ScheduleCsvDownload<ReadOnlyTimeSeries>(page, "csvDownload", am.getWebAccessManager()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				this.setSchedules((Collection) schedulePlot.getScheduleData(req).getSchedules().values(), req);
			}

		};

		buildPage();
		setDependencies();
	}

	@SuppressWarnings("deprecation")
	private final void buildPage() {
		page.append(header).linebreak();
		final SimpleGrid settings0 = new SimpleGrid(page, "settingsGrid0", true)
				.addItem("Select data source", false, null).addItem(dataProviderSelector, false, null)
				.addItem("Select profile category", true, null).addItem(profileCategorySelector, false, null)
				.addItem("Select profile", true, null).addItem(profilesSelector, false, null);
		settings0.setAppendFillColumn(true, null);
		page.append(settings0).linebreak().append(dataTree).linebreak();


		final Flexbox configFlex = new Flexbox(page, "configFlex", true);
		configFlex.setDefaultJustifyContent(JustifyContent.SPACE_BETWEEN);
		configFlex.setDefaultAlignItems(AlignItems.CENTER);
		configFlex.addItem(profileConfigTrigger, null).addItem(timeSeriesConfigTrigger, null).addItem(configClearBtn, null);

		final SimpleGrid settings1 = new SimpleGrid(page, "settingsGrid1", true)
				.addItem("Start time", false, null).addItem(startTimePicker, false, null)
				.addItem("End time", true, null).addItem(endTimePicker, false, null)
				.addItem("Size", true, null).addItem(sizeLabel, false, null)
				.addItem("Plot height in px", true, null).addItem(heightField, false, null)
				.addItem("Select plot library", true, null).addItem(plotLibSelector, false, null)
				.addItem("Line type", true, null).addItem(lineTypeSelector, false, null)
				.addItem("Options", true, null).addItem(aggregationCheckbox, false, null)
				.addItem("Select aggregation level", true, null).addItem(aggregationLevel, false, null)
				.addItem("Display settings", true, null).addItem(configFlex, false, null);
		settings1.setAppendFillColumn(true, null);

		final Accordion settingsAcc = new Accordion(page, "settingsAccordion", true);
		settingsAcc.addDefaultStyle(AccordionData.BOOTSTRAP_BLUE);
		settingsAcc.addItem("Settings", settings1, null);

		page.append(settingsAcc).append(apply);

		page.linebreak().append(schedulePlot).linebreak().append(downloadHeader).linebreak().append(csvDownload)
			.linebreak().append(profileConfig).linebreak().append(timeSeriesConfig).linebreak()
			.append(clientSideHeightSetter);

	}

	private final void setDependencies() {
		dataProviderSelector.triggerAction(dataTree, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dataProviderSelector.triggerAction(profilesSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
		dataProviderSelector.triggerAction(aggregationLevel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 2);
		profileCategorySelector.triggerAction(profilesSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		profilesSelector.triggerAction(startTimePicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
		profilesSelector.triggerAction(endTimePicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
		profilesSelector.triggerAction(sizeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 2);
		dataTree.triggerAction(startTimePicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dataTree.triggerAction(endTimePicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		dataTree.triggerAction(sizeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
		dataTree.triggerAction(aggregationLevel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		startTimePicker.triggerAction(sizeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		endTimePicker.triggerAction(sizeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		aggregationCheckbox.triggerAction(aggregationLevel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		apply.triggerAction(schedulePlot, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		profileConfig.trigger(profileConfigTrigger);
		timeSeriesConfig.trigger(timeSeriesConfigTrigger);

		schedulePlot.triggerAction(csvDownload, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
//		lineTypeSelector.triggerAction(clientSideConfigSetter, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		heightField.triggerAction(clientSideHeightSetter, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		plotLibSelector.triggerAction(schedulePlot, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}

	private final void setSchedulesByProfiles(final OgemaHttpRequest req) {
		final List<Profile> profiles = profilesSelector.getSelectedItems(req);
		if (profiles.isEmpty())
			return;
		final List<ReadOnlyTimeSeries> allSchedules = dataTree.getSchedules(req);
		final List<ReadOnlyTimeSeries> selected = filterSchedulesByProfiles(profiles, allSchedules);
		dataTree.selectSchedules(selected, req);
	}

	final boolean fixInterval(final OgemaHttpRequest req) {
		return aggregationCheckbox.isChecked("fixTime", req);
	}

	private final boolean aggregateTimeSeries(final OgemaHttpRequest req) {
		return aggregationCheckbox.isChecked("aggregate", req);
	}


	private static String getUnit(Collection<SchedulePresentationData> schedules) {
		Class<?> type = null;
		for (SchedulePresentationData spd : schedules) {
			Class<?> current = spd.getScheduleType();
			if (current == null)
				current = FloatResource.class;
			if (current == type)
				continue;
			if (!Resource.class.isAssignableFrom(current)) {
				if (current == Float.class)
					current = FloatResource.class;
				else if (current == Integer.class)
					current = IntegerResource.class;
				else if (current == Long.class)
					current = TimeResource.class;
				else if (current == Boolean.class)
					current = BooleanResource.class;
			}
			if (current == FloatResource.class) {
				current = estimateType(spd);
			}
			if (type == null)
				type = current;
			else if (type != current)
				return null;
		}
		if (type == null)
			return null;
		if (type == TemperatureResource.class)
			return "°C";
		if (type == PowerResource.class)
			return "W";
		if (type == EnergyResource.class)
			return "J";
		// TODO further; TODO more generic approach
		return null;
	}

	/**
	 * We assume here that the schedule has been checked for Float type already
	 * @param spd
	 * @return
	 */
	// XXX
	private static Class<?> estimateType(SchedulePresentationData spd) {
		final String label = spd.getLabel(OgemaLocale.ENGLISH).toLowerCase();
		if (label.contains("temperature"))
			return TemperatureResource.class;
		if (label.contains("power"))
			return PowerResource.class;
		if (label.contains("energy"))
			return EnergyResource.class;
		return FloatResource.class;
	}

	private static List<ReadOnlyTimeSeries> filterSchedulesByProfiles(final List<Profile> selectedProfiles, final List<ReadOnlyTimeSeries> allSchedules) {
		if (selectedProfiles.isEmpty())
			return Collections.emptyList();
		final List<ReadOnlyTimeSeries> selected = new ArrayList<>();
		for (ReadOnlyTimeSeries ts: allSchedules) {
			for (Profile p : selectedProfiles) {
				if (p.accept(ts)) {
					selected.add(ts);
					break;
				}
			}
		}
		return selected;
	}


	private static String getSchedulePath(final ReadOnlyTimeSeries ts) {
		if (ts instanceof Schedule)
			return ((Schedule) ts).getPath();
		if (ts instanceof RecordedData)
			return ((RecordedData) ts).getPath();
		if (ts instanceof SchedulePresentationData)
			return ((SchedulePresentationData) ts).getLabel(OgemaLocale.ENGLISH);
		return "Unknown timeseries";
	}

	private static Profile getApplicableProfile(final ReadOnlyTimeSeries ts, List<Profile> profiles) {
		Profile p = null;
		for (Profile profile : profiles) {
			if (profile.accept(ts)) {
				p = profile;
				break;
			}
		}
		return p;
	}

	private static SchedulePresentationData getSchedulePresentationData(final ReadOnlyTimeSeries ts, List<Profile> profiles) {
		return getSchedulePresentationData(ts, getApplicableProfile(ts, profiles));
	}

	private static SchedulePresentationData getSchedulePresentationData(final ReadOnlyTimeSeries ts, Profile profile) {
		InterpolationMode explicitMode = profile != null ? profile.defaultInterpolationMode() : null;
		if (explicitMode == null)
			explicitMode = ts.getInterpolationMode();
		if (ts instanceof Schedule) {
			Schedule schedule = (Schedule) ts;
			return new DefaultSchedulePresentationData(schedule, (SingleValueResource) schedule.getParent(), ((Schedule) ts).getPath(), explicitMode);
		}
		if (ts instanceof RecordedData) {
			RecordedData rd = (RecordedData) ts;
			return new DefaultSchedulePresentationData(rd, Float.class, rd.getPath(), explicitMode);
		}
		if (ts instanceof OnlineTimeSeries) {
			final SingleValueResource res = ((OnlineTimeSeries) ts).getResource();
			return new PresentationDataImpl(ts, res.getResourceType(), "Online " + ((OnlineTimeSeries) ts).getResource().getPath(),
						res instanceof BooleanResource ? InterpolationMode.STEPS : InterpolationMode.LINEAR, profile);
		}
		if (ts instanceof SchedulePresentationData)
			return (SchedulePresentationData) ts;
		else {
			return new DefaultSchedulePresentationData(ts, Float.class, ts.toString(), explicitMode);
		}
	}

	private static SchedulePresentationData resample(final SchedulePresentationData input, final long duration, final TemporalUnit unit) {
		final ReadOnlyTimeSeries resampled = StandardIntervalTimeseriesBuilder.newBuilder(input)
				.setInterval(duration, unit)
				.build();
		final String label = input.getLabel(OgemaLocale.ENGLISH) + "_" + duration + getUnit(unit);
		if (input instanceof PresentationDataImpl)
			return new PresentationDataImpl(resampled, input.getScheduleType(), label, input.getInterpolationMode(), ((PresentationDataImpl) input).getProfile());
		else
			return new DefaultSchedulePresentationData(resampled, input.getScheduleType(), label, input.getInterpolationMode());
	}

	private static final String getUnit(final TemporalUnit unit) {
		if (unit == ChronoUnit.DAYS)
			return "d";
		if (unit == ChronoUnit.HOURS)
			return "h";
		if (unit == ChronoUnit.MINUTES)
			return "min";
		if (unit == ChronoUnit.WEEKS)
			return "w";
		if (unit == ChronoUnit.MONTHS)
			return "M";
		if (unit == ChronoUnit.YEARS)
			return "a";
		return unit.toString();
	}

	private List<ReadOnlyTimeSeries> getSelectedSchedules(OgemaHttpRequest req) {
		@SuppressWarnings("unchecked")
		final TemplateMultiselect<SelectionItem> selector = (TemplateMultiselect<SelectionItem>) dataTree.getTerminalSelectWidget(req);
		if (selector == null)
			return Collections.emptyList();
		@SuppressWarnings("unchecked")
		final TerminalOption<ReadOnlyTimeSeries> terminalOpt = (TerminalOption<ReadOnlyTimeSeries>) dataTree.getTerminalOption(req);
		if (terminalOpt == null)
			return Collections.emptyList();
		final List<ReadOnlyTimeSeries> list = new ArrayList<>();
		for (SelectionItem item : selector.getSelectedItems(req)) {
			list.add(terminalOpt.getElement(item));
		}
		return list;
	}

}
