/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.reswidget.scheduleviewer.clone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.HtmlStyle;
import de.iwes.widgets.api.widgets.html.StaticHeader;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.calendar.datepicker.DatepickerData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.multiselect.TemplateMultiselectData;
import de.iwes.widgets.html.plotflot.FlotConfiguration;
import de.iwes.widgets.html.schedulemanipulator.ScheduleManipulator;
import de.iwes.widgets.html.schedulemanipulator.ScheduleManipulatorConfiguration;
import de.iwes.widgets.reswidget.schedulecsvdownload.ScheduleCsvDownload;
import de.iwes.widgets.reswidget.scheduleplot.flot.ScheduleDataFlot;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;
import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SelectionConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SessionConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SessionConfiguration.PreSelectionControllability;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilterExtended;
import de.iwes.widgets.reswidget.scheduleviewer.clone.helper.DefaultScheduleViewerConfigurationProvider;
import de.iwes.widgets.reswidget.scheduleviewer.clone.helper.DefaultSessionConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.clone.helper.TablePojo;
import de.iwes.widgets.reswidget.scheduleviewer.pattern.MultiPatternScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.pattern.PatternScheduleViewer;
import de.iwes.widgets.template.DefaultDisplayTemplate;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A schedule viewer, consisting of a {@link TemplateMultiselect} widget that
 * lets the user choose from a set of schedules/time series, {@link Datepicker}s
 * for the start and end time, and a {@link SchedulePlotFlot SchedulePlot}
 * widget, that displays the selected time series.<br>
 * Note that there are specific versions of this widget available, which can
 * show all {@link Schedule}s of a specific type, or time series corresponding
 * to ResourcePattern matches, and determine the list of schedules to be
 * displayed autonomously. The present version of the ScheduleViewer, on the
 * other hand, requires the time series to be set explicitly, via the methods
 * {@link #setDefaultSchedules(Collection)}.<br>
 * 
 * Note: this is always a global widget, in particular it cannot be added as a
 * subwidget to a non-global widget. Several of the subwidgets are non-global,
 * though, such as the time series selector.
 * 
 * @param <T>
 *            the type of time series to be displayed. Schedules, RecordedData
 *            (resource log data), and {@link SchedulePresentationData} are
 *            supported, but MemoryTimeSeries are not (directly). The
 *            SchedulePresentationData can wrap any kind of ReadOnlyTimeSeries,
 *            so in order to display a memory time series, wrap it into a
 *            DefaultSchedulePresentationData object.
 * 
 * @see ResourceScheduleViewer
 * @see PatternScheduleViewer
 * @see MultiPatternScheduleViewer
 * 
 * @author cnoelle
 */
@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
public class ScheduleViewerBasic<T extends ReadOnlyTimeSeries> extends PageSnippet implements ScheduleViewer<T> {

	private static final long serialVersionUID = 8360241089115449033L;

	/**
	 * Configuration options
	 */
	public final ScheduleViewerConfiguration configuration;
	protected final DisplayTemplate<T> displayTemplate;

	protected final ApplicationManager am;
	private final Alert alert;
	protected final List<Label> programSelectorLabels;
	protected List<Label> filterSelectorLabels;
	protected final Label scheduleSelectorLabel;
	protected final Label scheduleStartLabel;
	protected final Label dropdownScheduleNameLabel;
	protected final Label updateLabel;
	protected final Label scheduleEndLabel;
	protected Label nrDataPointsLabel;
	protected Label triggerIndividualConfigLabel;
	protected final List<TemplateMultiselect<TimeSeriesFilterExtended>> programSelectors;
	protected List<ConditionalProgramSelector> filterSelectors;
	protected final ScheduleSelector scheduleSelector;
	protected final ViewerDatepicker scheduleStartPicker;
	protected final ViewerDatepicker scheduleEndPicker;
	protected Label nrDataPoints;
	protected final Button updateButton;
	protected SchedulePlotFlot schedulePlot; // TODO generic interface
	protected ScheduleManipulator manipulator;
	protected StaticHeader manipulatorHeader;
	protected ScheduleCsvDownload<T> csvDownload;
	protected StaticHeader downloadHeader;
	protected Label optionsLabel;
	protected Checkbox optionsCheckbox;
	protected Button triggerIndividualConfigPopupButton;
	protected ConfigPopup<T> individualConfigPopup;
	protected final Button saveConfigurationButton;
	protected final Dropdown dropdownScheduleNames;
	public static final String PARAM_SESSION_CONFIG_ID = "configId";
	public static final String PARAM_EXPERT_MODE = "expertMode";
	private final static String FIX_INTERVAL_OPT = "Fix interval on schedule switch";
	private final static String SHOW_EMPTY_OPT = "Show empty schedules";
	public static final String SHORT_NAME = "Shortname";
	public static final String LONG_NAME = "Longname (Devicetype/name, Sensor/Actortype, Room)";
	public static final String LOCATION = "Locaton (Path)";

	private final WidgetPage<?> page;

	private static final TriggeredAction GET_REQUEST = TriggeredAction.GET_REQUEST;
	private static final TriggeringAction GET_REQUEST2 = TriggeringAction.GET_REQUEST;
	private static final TriggeringAction POST_REQUEST = TriggeringAction.POST_REQUEST;

	protected final ScheduleViewerConfigurationProvider configProvider;

	private SessionConfiguration getSessionConfiguration(OgemaHttpRequest req) {
		String configurationId = ScheduleViewerUtil.getPageParameter(req, page, PARAM_SESSION_CONFIG_ID);
		return configProvider.getSessionConfiguration(configurationId);
	}

	/**
	 * Extended options
	 */
	protected Label intervalDropLabel;
	protected TemplateDropdown<Long> intervalDrop;

	private Button selectAllSchedulesButton;

	/*
	 ****** Constructor and internal methods ******
	 */
	public ScheduleViewerBasic() {
		this(null, null, null, null, null, null, null);
	}

	/**
	 * Create a schedule viewer with default configuration. This means, in
	 * particular, that no schedule manipulator is shown, and the widget name
	 * service is used to determine the schedule labels.
	 * 
	 * @param page
	 * @param id
	 * @param am
	 */
	public ScheduleViewerBasic(WidgetPage<?> page, String id, final ApplicationManager am) {
		this(page, id, am, null, null, null, null);
	}

	public ScheduleViewerBasic(WidgetPage<?> page, String id, final ApplicationManager am,
			ScheduleViewerConfiguration config, DisplayTemplate<T> displayTemplate) {
		this(page, id, am, null, null, null, null);
	}

	@Override
	public void onGET(OgemaHttpRequest req) {
		scheduleStartPicker.getData(req).init = true;
		scheduleEndPicker.getData(req).init = true;
	}

	/**
	 * Create a schedule viewer with custom configuration.
	 * 
	 * @param page
	 * @param id
	 * @param am
	 * @param config
	 *            Configuration object. May be null, in which case a default
	 *            configuration is used.
	 * @param displayTemplate
	 *            Display template for the time series. May be null, in which case a
	 *            DefaultTimeSeriesDisplayTemplate is used.
	 */
	public ScheduleViewerBasic(WidgetPage<?> page, String id, final ApplicationManager am,
			ScheduleViewerConfiguration config, DisplayTemplate<T> displayTemplate, SessionConfiguration sessionConfig,
			ScheduleViewerConfigurationProvider configProvider) {
		super(page, id, true);
		ScheduleViewerUtil util = ScheduleViewerUtil.getInstance();
		if (sessionConfig == null) {
			sessionConfig = DefaultSessionConfiguration.DEFAULT_SESSION_CONFIGURATION;
		}

		if (configProvider != null) {
			this.configProvider = configProvider;
		} else {
			this.configProvider = DefaultScheduleViewerConfigurationProvider.DEFAULT_SCHEDULEVIEWER_CONFIGURATION_PROVIDER;
		}

		this.page = page;
		this.am = am;
		if (config == null) {
			config = ScheduleViewerConfiguration.DEFAULT_CONFIGURATION;
		}

		this.configuration = config;
		if (displayTemplate == null) {
			NameService nameService = config.useNameService ? getNameService() : null;
			List<Collection<TimeSeriesFilterExtended>> list = util.parse(sessionConfig.programsPreselected(),
					am.getResourceAccess());
			displayTemplate = new DefaultTimeSeriesDisplayTemplate<>(nameService, list, LONG_NAME);
		}
		this.displayTemplate = displayTemplate;

		this.alert = new Alert(page, id + "_alert", "");
		alert.setDefaultVisibility(false);
		this.append(alert, null);

		final boolean showProgramSelector = (config.programs != null);
		final boolean showFilterSelector = (config.filters != null);

		this.scheduleSelectorLabel = new Label(page, id + "_scheduleSelectorLabel", "Select schedule");
		this.dropdownScheduleNameLabel = new Label(page, id + "_scheduleNameLabel", "Change schedule-naming");
		this.updateLabel = new Label(page, id + "_updateLabel", "");
		this.scheduleStartLabel = new Label(page, id + "_scheduleStartLabel", "Select start time");
		this.scheduleEndLabel = new Label(page, id + "_scheduleEndLabel", "Select end time");

		this.programSelectorLabels = new ArrayList<>();
		this.programSelectors = new ArrayList<>();
		if (showProgramSelector) {
			initProgramSelector(id, config.programs, sessionConfig);
		}
		initSelectFilterRow(page, id, am, config, sessionConfig, showFilterSelector);

		this.scheduleSelector = new ScheduleSelector(page, id + "_scheduleSelector", sessionConfig);

		initOptionsRow(page, id);
		this.scheduleStartPicker = new ViewerDatepicker(page, id + "_scheduleStartPicker", true);
		this.scheduleEndPicker = new ViewerDatepicker(page, id + "_scheduleEndPicker", false);

		initNoOfDataPointsRow(page, id, am);
		initConfigButtonRow(page, id);
		this.updateButton = new Button(page, id + "_updateButton", "Apply");
		final boolean showCheckboxes = configuration.showOptionsSwitch;
		initSchedulePlot(page, id, am, config, showCheckboxes);
		initDownloaddataRow(page, id, am);
		initManipulateScheduleRow(page);
		initSelectIntervallRow(page, id);

		saveConfigurationButton = getSaveConfigurationButton(id);
		selectAllSchedulesButton = scheduleSelector.selectAllOrDeselectAllButton;
		dropdownScheduleNames = scheduleSelector.scheduleNameDropDown;
		finishBuildingPage(id, showProgramSelector, showFilterSelector);
		setDependencies();
	}

	private void initSelectFilterRow(WidgetPage<?> page, String id, final ApplicationManager am,
			ScheduleViewerConfiguration config, SessionConfiguration sessionConfig, final boolean showFilterSelector) {
		if (!showFilterSelector) {
			this.filterSelectorLabels = Collections.emptyList();
			this.filterSelectors = Collections.emptyList();
		} else {
			this.filterSelectorLabels = new ArrayList<>();
			this.filterSelectors = new ArrayList<>();
			Label filterSelectorLabel;
			ConditionalProgramSelector filterSelector;
			int cnt = 0;
			for (Map<String, ConditionalTimeSeriesFilter<?>> filters : config.filters) {
				filterSelectorLabel = new Label(page, id + "_filterSelectorLabel_" + cnt, "Select a filter");
				filterSelector = new ConditionalProgramSelector(page, id + "_filterSelector" + cnt++, filters,
						am.getResourcePatternAccess(), sessionConfig);

				filterSelectorLabels.add(filterSelectorLabel);
				filterSelectors.add(filterSelector);
			}
		}
	}

	private void initDownloaddataRow(WidgetPage<?> page, String id, final ApplicationManager am) {
		downloadHeader = new StaticHeader(3, "Download data");
		downloadHeader.addStyle(HtmlStyle.ALIGNED_CENTER);
		this.csvDownload = new ScheduleCsvDownload<T>(page, id + "_dataDownload", am.getWebAccessManager()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showCsvDownload || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
					return;
				}
				this.setSchedules(scheduleSelector.getSelectedItems(req), req);
			}
		};
	}

	private void initManipulateScheduleRow(WidgetPage<?> page) {
		if (!configuration.showManipulator) {
			return;
		}

		this.manipulatorHeader = new StaticHeader(3, "Manipulate schedule");
		this.manipulatorHeader.addStyle(HtmlStyle.ALIGNED_CENTER);
		ScheduleManipulatorConfiguration smc = configuration.manipulatorConfiguration;
		ScheduleManipulatorConfiguration newConfig = new ScheduleManipulatorConfiguration(alert,
				smc.isShowInterpolationMode(), smc.isShowQuality());
		this.manipulator = new ScheduleManipulator(page, "manipulator", newConfig) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showManipulator || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
					return;
				}
				List<T> selectedSchedules = scheduleSelector.getSelectedItems(req);
				if (selectedSchedules.size() != 1 || !(selectedSchedules.get(0) instanceof Schedule)) {
					// we display the schedule manipulator only if exactly one schedule is selected
					setSchedule(null, req);
					return;
				}
				setSchedule((Schedule) selectedSchedules.get(0), req);
				long startTime = scheduleStartPicker.getDateLong(req);
				setStartTime(startTime, req);
			}
		};
	}

	private void initSelectIntervallRow(WidgetPage<?> page, String id) {
		this.intervalDropLabel = new Label(page, id + "_intervalDropLabel", "Select interval") {

			private static final long serialVersionUID = -793624698242225307L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showStandardIntervals || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
				;
			}
		};
		final List<Long> defaultValues = Arrays.asList(0L, 10 * 60 * 1000L, 60 * 60 * 1000L, 24 * 60 * 60 * 1000L,
				2 * 24 * 60 * 60 * 1000L, 7 * 24 * 60 * 60 * 1000L, 30 * 24 * 60 * 60 * 1000L,
				365 * 24 * 60 * 60 * 1000L);
		this.intervalDrop = new TemplateDropdown<Long>(page, id + "_intervalDrop") {

			private static final long serialVersionUID = 5595511208289921378L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showStandardIntervals || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
				;
			}

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				scheduleStartPicker.getData(req).fixedInterval = true;
				scheduleEndPicker.getData(req).fixedInterval = true;
			}
		};

		intervalDrop.setTemplate(intervalDisplayTemplate);
		intervalDrop.setComparator(defaultLongComparator);
		intervalDrop.setDefaultItems(defaultValues);
	}

	private void initOptionsRow(WidgetPage<?> page, String id) {
		this.optionsLabel = new Label(page, id + "_fixIntervalLabel", "Options") {

			private static final long serialVersionUID = -1032652251225301073L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showOptionsSwitch || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
			}
		};
		this.optionsCheckbox = new Checkbox(page, id + "_optionsCheckbox") {

			private static final long serialVersionUID = 685168574654L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showOptionsSwitch || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
			}
		};
		Map<String, Boolean> opts = new LinkedHashMap<String, Boolean>();
		opts.put(FIX_INTERVAL_OPT, false);
		opts.put(SHOW_EMPTY_OPT, true);
		optionsCheckbox.setDefaultList(opts);
	}

	private void initSchedulePlot(WidgetPage<?> page, String id, final ApplicationManager am,
			ScheduleViewerConfiguration config, final boolean showCheckboxes) {
		schedulePlot = new SchedulePlotFlot(page, id + "_schedulePlot", false, config.bufferWindow) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final List<T> selectedSchedules = scheduleSelector.getSelectedItems(req);
				long startTime = scheduleStartPicker.getDateLong(req);
				long endTime = scheduleEndPicker.getDateLong(req);

				boolean showEmpty = showCheckboxes ? optionsCheckbox.getCheckboxList(req).get(SHOW_EMPTY_OPT) : true;
				if (startTime > endTime)
					startTime = endTime;
				setInterval(startTime, endTime, req);
				Map<String, SchedulePresentationData> schedules = new LinkedHashMap<String, SchedulePresentationData>();
				for (T sched : selectedSchedules) {
					if (!showEmpty && sched.isEmpty(startTime, endTime))
						continue;
					Resource parent = null;
					Class<?> type = null;
					if (sched instanceof SchedulePresentationData) {
						type = ((SchedulePresentationData) sched).getScheduleType();
					} else if (sched instanceof Schedule) {
						parent = ((Schedule) sched).getParent();
					} else if (sched instanceof RecordedData) {
						String path = ((RecordedData) sched).getPath();
						parent = am.getResourceAccess().getResource(path);
					} else
						continue;
					if (parent != null) {
						if (!(parent instanceof SingleValueResource) || parent instanceof StringResource)
							continue;
						type = parent.getResourceType();
					}
					String label = getLabel(req, sched);
					schedules.put(label, new DefaultSchedulePresentationData(sched, type, label));
				}
				ScheduleDataFlot data = getScheduleData(req);
				data.setSchedules(schedules);
			}

			private String getLabel(OgemaHttpRequest req, T sched) {
				String naming = scheduleSelector.scheduleNameDropDown.getSelectedValue(req);
				if (SHORT_NAME.equals(naming)) {
					return scheduleSelector.templateShort.getLabel(sched, req.getLocale());
				} else if (LONG_NAME.equals(naming)) {
					return scheduleSelector.templateLong.getLabel(sched, req.getLocale());
				}

				return scheduleSelector.templateLocation.getLabel(sched, req.getLocale());

			}

		};
		schedulePlot.getDefaultConfiguration().doScale(true); // can be overwritten in app
	}

	private void initNoOfDataPointsRow(WidgetPage<?> page, String id, final ApplicationManager am) {
		this.nrDataPointsLabel = new Label(page, id + "_nrDataPointsLabel", "Number of data points") {

			private static final long serialVersionUID = -793624698242225307L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showNrPointsPreview || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
			}
		};
		this.nrDataPoints = new Label(page, id + "_nrDataPoints", "0") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showNrPointsPreview || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
					return;
				}
				List<T> schedules = scheduleSelector.getSelectedItems(req);
				long t0 = System.currentTimeMillis();
				if (am != null) {
					t0 = am.getFrameworkTime();
				}
				long startTime = t0;
				long endTime = t0;
				try {
					startTime = scheduleStartPicker.getDateLong(req);
					endTime = scheduleEndPicker.getDateLong(req);
				} catch (Exception e) {
				}
				if (schedules == null || schedules.isEmpty() || startTime > endTime) {
					setText("0", req);
					return;
				}
				int size = 0;
				for (ReadOnlyTimeSeries sched : schedules) {
					size += sched.size(startTime, endTime);
				}
				setText(String.valueOf(size), req);
			}
		};
	}

	private void initConfigButtonRow(WidgetPage<?> page, String id) {
		this.triggerIndividualConfigLabel = new Label(page, id + "_triggerIndvConfigLabel",
				"Configure display settings") {

			private static final long serialVersionUID = 4680903101539028489L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showIndividualConfigBtn || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
			}
		};
		this.triggerIndividualConfigPopupButton = new Button(page, id + "_triggerIndividualConfigPopup",
				"Open settings") {

			private static final long serialVersionUID = 175423937072595444L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configuration.showIndividualConfigBtn || isExpertMode(req)) {
					setWidgetVisibility(true, req);
				} else {
					setWidgetVisibility(false, req);
				}
			}
		};
		this.individualConfigPopup = new ConfigPopup<T>(page, id + "_individualConfigPopup", this);
	}

	private void initProgramSelector(String id, List<Map<String, TimeSeriesFilter>> allPrograms,
			SelectionConfiguration sessionConfig) {

		Label programSelectorLabel;
		TemplateMultiselect<TimeSeriesFilterExtended> programSelector;

		int cnt = 0;

		for (Map<String, TimeSeriesFilter> filters : allPrograms) {

			programSelectorLabel = new Label(page, id + "_programSelectorLabel_" + cnt, "Select a program");
			programSelector = new TemplateMultiselect<TimeSeriesFilterExtended>(page, id + "_programSelector" + cnt++) {

				private static final long serialVersionUID = -146823525912151858L;

				@Override
				public void onGET(OgemaHttpRequest req) {
					SessionConfiguration sessionConfig = getSessionConfiguration(req);
					updateProgramsPreselected(req, sessionConfig);
					if (sessionConfig.timeSeriesSelectionControllability() == PreSelectionControllability.FIXED
							|| sessionConfig
									.timeSeriesSelectionControllability() == PreSelectionControllability.MAX_SIZE) {
						// user selection not Possible
						disable(req);
					} else {
						// user selection Possible
						enable(req);
					}
				}

				private void updateProgramsPreselected(OgemaHttpRequest req, SessionConfiguration sessionConfig) {

					Collection<TimeSeriesFilterExtended> preselectedFilters = new ArrayList<>();
					List<Collection<TimeSeriesFilterExtended>> programsPreselected = ScheduleViewerUtil.getInstance()
							.parse(sessionConfig.programsPreselected(), am.getResourceAccess());
					for (Collection<TimeSeriesFilterExtended> outer : programsPreselected) {
						for (TimeSeriesFilterExtended inner : outer) {
							preselectedFilters.add(inner);
						}
					}

					Collection<TimeSeriesFilterExtended> allFilters = getItems(req);

					if (isExpertMode(req)) {
						// Im Expert-Mode werden auch alle Standard-Filter angeboten
						Collection<TimeSeriesFilterExtended> filterToshow = getSubsetwithPreselectedFilters(allFilters,
								preselectedFilters);
						selectItems(filterToshow, req);
					} else {
						// Im Non-Expert-Mode werden nur die gew�hlten Timeseries im Schedule-Selector
						// angeboten, keine weiteren TimeSeries angeboten.
						selectItems(preselectedFilters, req);
					}
				}
			};

			List<TimeSeriesFilterExtended> extendedFilters = ScheduleViewerUtil.getInstance().parse(filters.values(),
					am.getResourceAccess());
			List<Collection<TimeSeriesFilterExtended>> preSelectedExtendedFilters = ScheduleViewerUtil.getInstance()
					.parse(sessionConfig.programsPreselected(), am.getResourceAccess());
			programSelector.selectDefaultItems(extendedFilters);
			for (Collection<TimeSeriesFilterExtended> programmPreselected : preSelectedExtendedFilters) {
				Collection<TimeSeriesFilterExtended> subSet = getSubsetwithPreselectedFilters(extendedFilters,
						programmPreselected);
				if (!subSet.isEmpty()) {
					programSelector.setDefaultSelectedItems(programmPreselected);
				}
			}

			programSelector.setTemplate(new DisplayTemplate<TimeSeriesFilterExtended>() {

				@Override
				public String getId(TimeSeriesFilterExtended object) {
					return object.id();
				}

				@Override
				public String getLabel(TimeSeriesFilterExtended object, OgemaLocale locale) {
					return object.label(locale);
				}
			});

			programSelector.setDefaultWidth("100%");
			programSelectorLabels.add(programSelectorLabel);
			programSelectors.add(programSelector);
		}
	}

	protected void appendDynamicTable(final String id, boolean showProgramSelector, boolean showFilterSelector) {

		final List<TablePojo> EXPERT_LIST = getTableElements(id, showProgramSelector, showFilterSelector, true);
		final List<TablePojo> USERFRIENDLY_LIST = getTableElements(id, showProgramSelector, showFilterSelector, false);

		DynamicTable<TablePojo> table = new DynamicTable<TablePojo>(page, id + "_table") {

			private static final long serialVersionUID = 5879356788178926843L;

			@Override
			public void onGET(OgemaHttpRequest req) {

				if (isExpertMode(req)) {
					updateRows(EXPERT_LIST, req);
				} else {
					updateRows(USERFRIENDLY_LIST, req);
				}
			}
		};
		table.setRowTemplate(new RowTemplate<TablePojo>() {

			@Override
			public Row addRow(TablePojo pojo, OgemaHttpRequest req) {
				final Row row = new Row();
				if (pojo.getLabel() != null) {
					String label = pojo.getLabel().getText(req);
					row.addCell("label", label);
				}

				row.addCell("widget", pojo.getSnippet());
				row.addCell("empty", "");
				return row;
			}

			@Override
			public String getLineId(TablePojo pojo) {
				return pojo.getId();
			}

			@Override
			public Map<String, Object> getHeader() {
				return null;
			}

		});
		this.append(table, null);

	}

	private List<TablePojo> getTableElements(final String mainId, boolean showProgramSelector,
			boolean showFilterSelector, final boolean isExpert) {
		final ArrayList<TablePojo> list = new ArrayList<>();
		String id = mainId + "_expert_" + isExpert;

		if ((showProgramSelector || isExpert) && programSelectors != null) {
			for (int i = 0; i < programSelectors.size(); i++) {
				list.add(new TablePojo(id, programSelectorLabels.get(i), programSelectors.get(i), page));
			}
		}
		if ((showFilterSelector || isExpert) && filterSelectors != null) {
			for (int i = 0; i < filterSelectors.size(); i++) {
				list.add(new TablePojo<>(id, filterSelectorLabels.get(i), filterSelectors.get(i), page));
			}
		}

		list.add(new TablePojo(id, scheduleSelectorLabel, scheduleSelector, selectAllSchedulesButton, page));
		list.add(new TablePojo(id, dropdownScheduleNameLabel, dropdownScheduleNames, page));
		list.add(new TablePojo(id, scheduleStartLabel, scheduleStartPicker, page));
		list.add(new TablePojo(id, scheduleEndLabel, scheduleEndPicker, page));

		if (configuration.showStandardIntervals || isExpert) {
			list.add(new TablePojo(id, intervalDropLabel, intervalDrop, page));
		}

		if (configuration.showNrPointsPreview || isExpert) {
			list.add(new TablePojo(id, nrDataPointsLabel, nrDataPoints, page));
		}

		if (configuration.showOptionsSwitch || isExpert) {
			list.add(new TablePojo(id, optionsLabel, optionsCheckbox, page));
		}

		if (configuration.showIndividualConfigBtn || isExpert) {
			list.add(new TablePojo(id, triggerIndividualConfigLabel, triggerIndividualConfigPopupButton, page));
		}

		list.add(new TablePojo(id, updateLabel, updateButton, page));

		return list;
	}

	@Deprecated
	protected void appendStaticTable(String id, boolean showProgramSelector, boolean showFilterSelector) {
		int diff = 0;
		if (configuration.showOptionsSwitch)
			diff++;
		if (configuration.showNrPointsPreview)
			diff++;
		if (configuration.showStandardIntervals)
			diff++;
		if (configuration.showIndividualConfigBtn)
			diff++;
		if (showProgramSelector)
			diff += programSelectors.size();
		if (showFilterSelector)
			diff += filterSelectors.size();
		StaticTable table = new StaticTable(5 + diff, 2, new int[] { 2, 4 });
		int row = 0;
		if (showProgramSelector) {
			for (int i = 0; i < programSelectors.size(); i++) {
				table.setContent(row, 0, programSelectorLabels.get(i)).setContent(row++, 1, programSelectors.get(i));
			}
		}
		if (showFilterSelector) {
			for (int i = 0; i < filterSelectors.size(); i++) {
				table.setContent(row, 0, filterSelectorLabels.get(i)).setContent(row++, 1, filterSelectors.get(i));
			}
		}

		table.setContent(row, 0, scheduleSelectorLabel).setContent(row, 1, scheduleSelector).setContent(row++, 1,
				selectAllSchedulesButton);
		table.setContent(row, 0, dropdownScheduleNameLabel).setContent(row++, 1, dropdownScheduleNames);

		table.setContent(row, 0, scheduleStartLabel).setContent(row++, 1, scheduleStartPicker);
		table.setContent(row, 0, scheduleEndLabel).setContent(row++, 1, scheduleEndPicker);

		if (configuration.showStandardIntervals) {
			table.setContent(row, 0, intervalDropLabel).setContent(row++, 1, intervalDrop);
		} else if (intervalDrop != null) {
			intervalDrop.setDefaultVisibility(false);
			page.append(intervalDrop);
		}

		if (configuration.showNrPointsPreview) {
			table.setContent(row, 0, nrDataPointsLabel).setContent(row++, 1, nrDataPoints);
		} else if (nrDataPoints != null) {
			nrDataPoints.setDefaultVisibility(false);
			page.append(nrDataPoints);
		}

		if (configuration.showOptionsSwitch) {
			table.setContent(row, 0, optionsLabel).setContent(row++, 1, optionsCheckbox);
		} else if (optionsCheckbox != null) {
			optionsCheckbox.setDefaultVisibility(false);
			page.append(optionsCheckbox);
		}

		if (configuration.showIndividualConfigBtn) {
			table.setContent(row, 0, triggerIndividualConfigLabel).setContent(row++, 1,
					triggerIndividualConfigPopupButton);
		} else if (triggerIndividualConfigPopupButton != null) {
			triggerIndividualConfigPopupButton.setDefaultVisibility(false);
			page.append(triggerIndividualConfigPopupButton);
		}

		table.setContent(row++, 1, updateButton);
		this.append(table, null);
	}

	protected void finishBuildingPage(String tableId, boolean showProgramSelector, boolean showFilterSelector) {
		// appendStaticTable(tableId, showProgramSelector, showFilterSelector);
		appendDynamicTable(tableId, showProgramSelector, showFilterSelector);

		if (saveConfigurationButton != null) {
			this.append(saveConfigurationButton, null).linebreak(null);
		}
		this.append(schedulePlot, null).linebreak(null);
		if (csvDownload != null) {
			this.append(downloadHeader, null).linebreak(null).append(csvDownload, null).linebreak(null);
		}

		if (manipulator != null) {
			this.append(manipulatorHeader, null).linebreak(null).append(manipulator, null);
		}
		if (individualConfigPopup != null)
			this.append(individualConfigPopup, null);
	}

	// we cannot use registerDependentWidget here, because this would make
	// scheduleSelector a governing widget, and
	// hence we could not use it anymore to trigger any other widgets on the page
	private void setDependencies() {
		scheduleSelector.triggerAction(scheduleStartPicker, POST_REQUEST, GET_REQUEST);
		scheduleSelector.triggerAction(scheduleEndPicker, POST_REQUEST, GET_REQUEST);
		if (configuration.showNrPointsPreview) {
			scheduleSelector.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST, 1);
			scheduleStartPicker.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST);
			scheduleEndPicker.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST);
		}

		updateButton.triggerAction(schedulePlot, POST_REQUEST, GET_REQUEST);
		if (manipulator != null)
			updateButton.triggerAction(manipulator, POST_REQUEST, GET_REQUEST);
		if (csvDownload != null)
			updateButton.triggerAction(csvDownload, POST_REQUEST, GET_REQUEST);

		if (programSelectors != null) {
			for (OgemaWidget programSelector : programSelectors) {
				programSelector.triggerAction(scheduleSelector, POST_REQUEST, GET_REQUEST);
				programSelector.triggerAction(scheduleStartPicker, POST_REQUEST, GET_REQUEST, 1);
				programSelector.triggerAction(scheduleEndPicker, POST_REQUEST, GET_REQUEST, 1);
				if (configuration.showNrPointsPreview)
					programSelector.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST, 2);
			}
		}
		if (filterSelectors != null) {
			for (ConditionalProgramSelector filterSelector : filterSelectors) {
				filterSelector.instanceSelector.triggerAction(scheduleSelector, POST_REQUEST, GET_REQUEST);
				filterSelector.instanceSelector.triggerAction(scheduleStartPicker, POST_REQUEST, GET_REQUEST, 1);
				filterSelector.instanceSelector.triggerAction(scheduleEndPicker, POST_REQUEST, GET_REQUEST, 1);
				filterSelector.filterSelector.triggerAction(scheduleSelector, POST_REQUEST, GET_REQUEST);
				filterSelector.filterSelector.triggerAction(scheduleStartPicker, POST_REQUEST, GET_REQUEST, 1);
				filterSelector.filterSelector.triggerAction(scheduleEndPicker, POST_REQUEST, GET_REQUEST, 1);
				if (configuration.showNrPointsPreview) {
					filterSelector.instanceSelector.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST, 2);
					filterSelector.filterSelector.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST, 2);
				}
			}
		}
		if (intervalDrop != null) {
			intervalDrop.triggerAction(scheduleStartPicker, POST_REQUEST, GET_REQUEST);
			intervalDrop.triggerAction(scheduleEndPicker, POST_REQUEST, GET_REQUEST);
			// must be updated after start and end picker
			if (nrDataPoints != null)
				intervalDrop.triggerAction(nrDataPoints, POST_REQUEST, GET_REQUEST, 1);
		}

		// trigger subwidgets
		this.triggerAction(scheduleSelector, GET_REQUEST2, GET_REQUEST);
		if (configuration.showNrPointsPreview)
			this.triggerAction(nrDataPoints, GET_REQUEST2, GET_REQUEST, 1);
		this.triggerAction(schedulePlot, GET_REQUEST2, GET_REQUEST, 1);
		if (triggerIndividualConfigPopupButton != null)
			individualConfigPopup.trigger(triggerIndividualConfigPopupButton);
	}

	protected void updateSelectionConfiguration(OgemaHttpRequest req, String configId) {
		final SessionConfiguration sessionConfig = configProvider.getSessionConfiguration(configId);
		if (sessionConfig != null) {
			scheduleSelector.onGET(req); // --> update for sessionConfig.timeSeriesSelected();
			updateConditionalTimeSeriesFilterCategoryPreselected(req, sessionConfig);
			updateFiltersPreSelected(req, sessionConfig);
			// updateProgramsPreselected(req, sessionConfig); // --> programSelector.onGet()
		}

	}

	private void updateConditionalTimeSeriesFilterCategoryPreselected(OgemaHttpRequest req,
			SessionConfiguration sessionConfig) {
		Integer preselectedFilter = sessionConfig.conditionalTimeSeriesFilterCategoryPreselected();

		if (configuration.filters != null && !configuration.filters.isEmpty()) {
			final List<Map<String, ConditionalTimeSeriesFilter<?>>> providerFilters = configuration.filters;
			for (ConditionalProgramSelector dropdown : filterSelectors) {
				if (preselectedFilter < providerFilters.size()) {
					Map<String, ConditionalTimeSeriesFilter<?>> providerFilter = providerFilters.get(preselectedFilter);
					boolean sameFilterSet = dropdown.sameFilterIds(providerFilter.keySet());
					if (sameFilterSet) {
						if (providerFilter != null && !providerFilter.isEmpty()) {
							dropdown.filterSelector.selectDefaultItem(providerFilter.get("0"));
						}
					} else {
						dropdown.filterSelector.selectDefaultItem(null);
					}
				}
			}
		}

	}

	private void updateFiltersPreSelected(OgemaHttpRequest req, SessionConfiguration sessionConfig) {

		final boolean overwrite = sessionConfig.overwriteConditionalFilters();
		final PreSelectionControllability filterControllability = sessionConfig.filterControllability();
		List<Map<String, ConditionalTimeSeriesFilter<?>>> providerFilters;
		if (configuration.filters != null) {
			providerFilters = configuration.filters;
		} else {
			return;
		}

		if (overwrite) { // show only the filters from the sessionConfig
			for (ConditionalProgramSelector dropdown : filterSelectors) {
				for (Map<String, ConditionalTimeSeriesFilter<?>> providerFilter : providerFilters) {
					boolean sameFilterSet = dropdown.sameFilterIds(providerFilter.keySet());
					if (sameFilterSet) {
						for (ConditionalTimeSeriesFilter<?> preSelectedFilter : sessionConfig.filtersPreSelected()) {
							dropdown.filterSelector.selectDefaultItem(preSelectedFilter);
						}

						boolean show = !sessionConfig.filtersPreSelected().isEmpty();
						// if filters from config isEmpty then hide the dropdown
						dropdown.setDefaultVisibility(show);
					}
				}
			}
		} else {// show all Filters
			for (ConditionalProgramSelector dropdown : filterSelectors) {
				for (Map<String, ConditionalTimeSeriesFilter<?>> providerFilter : providerFilters) {
					boolean sameFilterSet = dropdown.sameFilterIds(providerFilter.keySet());
					if (sameFilterSet) {
						for (ConditionalTimeSeriesFilter<?> preSelectedFilter : sessionConfig.filtersPreSelected()) {
							dropdown.filterSelector.selectDefaultItem(preSelectedFilter);
						}
						dropdown.setDefaultVisibility(true);
					}
				}
			}
		}

		if (filterControllability == PreSelectionControllability.FIXED
				|| filterControllability == PreSelectionControllability.MAX_SIZE) {
			disable(req);
		}
	}

	private Collection<TimeSeriesFilterExtended> getSubsetwithPreselectedFilters(
			Collection<? extends TimeSeriesFilter> allFilters,
			Collection<TimeSeriesFilterExtended> programmPreselected) {

		final TreeSet<String> allFilterIds = new TreeSet<>();
		final Collection<TimeSeriesFilter> result = new ArrayList<>();

		for (final TimeSeriesFilter filter : allFilters) {
			allFilterIds.add(filter.id());
		}

		for (final TimeSeriesFilter filter : programmPreselected) {
			if (allFilterIds.contains(filter.id())) {
				result.add(filter);
			}
		}
		return ScheduleViewerUtil.getInstance().parse(result, am.getResourceAccess());
	}

	public void triggerFilterUpdate(final OgemaWidget trigger) {
		if (programSelectors != null) {
			for (TemplateMultiselect<TimeSeriesFilterExtended> selector : programSelectors) {
				trigger.triggerAction(selector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			}
		}
	}

	/*
	 ************* Methods to be overridden in derived class *******
	 */

	/**
	 * Determine the items that shall be displayed in the multi-select (items that
	 * are available to be chosen by user). Note that these items are not selected
	 * automatically. By default, the method returns those schedules that have been
	 * set via the methods {@link #setDefaultSchedules(Collection)} or
	 * {@link #setSchedules(List, OgemaHttpRequest)}.<br>
	 * 
	 * Override in subclass to specify a different behaviour and to perform other
	 * operations that would be placed in the onGET method of a widget.
	 * 
	 * @param req
	 * @return
	 */
	protected List<T> update(OgemaHttpRequest req) {
		return scheduleSelector.getItems(req);
	}

	/*
	 ************** Public methods *************
	 */

	/**
	 * Get a reference to the schedule selector widget.
	 */
	@Override
	public final TemplateMultiselect<T> getScheduleSelector() {
		return scheduleSelector;
	}

	@Override
	public final SchedulePlotFlot getSchedulePlot() {
		return schedulePlot;
	}

	/**
	 * Set the selectable schedules for a particular user session.
	 * 
	 * @param schedules
	 * @param req
	 */
	public void setSchedules(Collection<T> schedules, OgemaHttpRequest req) {
		scheduleSelector.update(schedules, req);
	}

	/**
	 * Set the selectable schedules globally.
	 * 
	 * @param items
	 */
	public void setDefaultSchedules(Collection<T> items) {
		scheduleSelector.selectDefaultItems(items); // XXX misleading method name
	}

	/**
	 * Get all schedules for a particular session.
	 * 
	 * @param req
	 * @return
	 */
	public List<T> getSchedules(OgemaHttpRequest req) {
		return scheduleSelector.getItems(req);
	}

	/**
	 * Get all selected schedules in a particular session.
	 * 
	 * @param req
	 * @return
	 */
	public List<T> getSelectedSchedules(OgemaHttpRequest req) {
		return scheduleSelector.getSelectedItems(req);
	}

	@Override
	public void selectSchedules(Collection<T> selected, OgemaHttpRequest req) {
		scheduleSelector.selectItems(selected, req);
	}

	@Override
	public List<T> getSelectedItems(OgemaHttpRequest req) {
		return scheduleSelector.getSelectedItems(req);
	}

	@Override
	public void setStartTime(long start, OgemaHttpRequest req) {
		scheduleStartPicker.getData(req).explicitDate = start;
	}

	public long getStartTime(OgemaHttpRequest req) {
		return scheduleStartPicker.getDateLong(req);
	}

	@Override
	public void setEndTime(long end, OgemaHttpRequest req) {
		scheduleEndPicker.getData(req).explicitDate = end;
	}

	public long getEndTime(OgemaHttpRequest req) {
		return scheduleEndPicker.getDateLong(req);
	}

	/**
	 * Set default plot configurations
	 * 
	 * @return
	 */
	@Override
	public FlotConfiguration getDefaultPlotConfiguration() {
		return schedulePlot.getDefaultConfiguration();
	}

	/**
	 * Set session-specific plot configurations
	 * 
	 * @param req
	 * @return
	 */
	@Override
	public FlotConfiguration getPlotConfiguration(OgemaHttpRequest req) {
		return schedulePlot.getConfiguration(req);
	}

	/**
	 * May be null
	 * 
	 * @return
	 */
	public TemplateDropdown<Long> getIntervalDropdown() {
		return intervalDrop;
	}

	private class SelectAllButton extends Button {

		private static final long serialVersionUID = -146823525912151858L;
		private final int maxSelectableSchedules = 42; // FIXME should be 10

		/**
		 * M�glichkeit im Schedule Selector alle Optionen abzuw�hlen und anzuw�hlen.
		 * Ggf. w�re es auch sinnvoller Standard, dass bei mehr als 10 Optionen keine
		 * Option ausgew�hlt ist, sondern nur das Multiselect mit Optionen bef�llt ist;
		 * wenn sehr viele Reihen im Filter sind , dann musste man bisher oft m�hsam
		 * alle Reihen von Hand an/abw�hlen
		 */
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final List<T> allSchedules = getSchedules(req);
			boolean select = getData(req).selectOrDeselect;
			boolean underLimit = allSchedules.size() < maxSelectableSchedules;
			if (select && underLimit) {
				selectSchedules(allSchedules, req);
			} else {
				selectSchedules(Collections.emptyList(), req);

			}
			getData(req).selectOrDeselect = !getData(req).selectOrDeselect;
			getData(req).pushed = true;
		}

		public SelectAllButton(WidgetPage<?> page, String id) {
			super(page, id);
			this.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}

		@Override
		public SelectAllButtonData createNewSession() {
			return new SelectAllButtonData(this);
		}

		@Override
		public SelectAllButtonData getData(OgemaHttpRequest req) {
			return (SelectAllButtonData) super.getData(req);
		}

		boolean hasBeenPushed(OgemaHttpRequest req) {
			final boolean pushed = getData(req).pushed;
			getData(req).pushed = false;
			return pushed;
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			if (getData(req).selectOrDeselect) {
				setText("Select all Schedules  ", req);
				setCss("btn btn-info", req);
			} else {
				setText("Deselect all Schedules", req);
				setCss("btn btn-danger", req);
			}
		}

	}

	private static class SelectAllButtonData extends ButtonData {

		private boolean pushed = false;
		private boolean selectOrDeselect = true;

		public SelectAllButtonData(Button button) {
			super(button);
		}
	}

	private static class TemplateMultiselectDataExtended<T extends ReadOnlyTimeSeries> extends TemplateMultiselectData {

		private DisplayTemplate template;

		public TemplateMultiselectDataExtended(TemplateMultiselect multiselect) {
			super(multiselect);
			template = new DefaultDisplayTemplate<>();
		}

		public void setDisplayTemplate(DisplayTemplate displayTemplate) {
			this.template = displayTemplate;
		}

		@Override
		protected String[] getValueAndLabel(Object item) {
			String label = template.getLabel(item, OgemaLocale.ENGLISH);
			String value = template.getId(item);
			Objects.requireNonNull(label);
			Objects.requireNonNull(value);
			return new String[] { value, label };
		}

	}

	private static class DropdownDataExented extends DropdownData {

		boolean pushed = false;

		public DropdownDataExented(Dropdown dropdown) {
			super(dropdown);
		}

	}

	protected class ScheduleSelector extends TemplateMultiselect<T> {

		private static final long serialVersionUID = 1L;
		public final SelectAllButton selectAllOrDeselectAllButton;
		public final ChangeScheduleNameDropdown scheduleNameDropDown;
		private boolean initializing;
		private final DisplayTemplate<T> templateShort;
		private final DisplayTemplate<T> templateLong;
		private final DisplayTemplate<T> templateLocation;

		public ScheduleSelector(WidgetPage<?> page, String id, SessionConfiguration sessionconfig) {
			super(page, id);
			setTemplate(displayTemplate);
			setDefaultWidth("100%");

			selectAllOrDeselectAllButton = new SelectAllButton(page, id + "_selectScheduleButton");
			scheduleNameDropDown = new ChangeScheduleNameDropdown(page, id + "_scheduleNameChanger");
			List<Collection<TimeSeriesFilter>> programs = sessionconfig.programsPreselected();
			List<Collection<TimeSeriesFilterExtended>> filterCollection = ScheduleViewerUtil.getInstance()
					.parse(programs, am.getResourceAccess());

			NameService nameService = configuration.useNameService ? getNameService() : null;
			templateShort = new DefaultTimeSeriesDisplayTemplate<>(nameService, filterCollection, SHORT_NAME);
			templateLong = new DefaultTimeSeriesDisplayTemplate<>(nameService, filterCollection, LONG_NAME);
			templateLocation = new DefaultTimeSeriesDisplayTemplate<>(nameService, filterCollection, LOCATION);
			selectAllOrDeselectAllButton.triggerAction(this, POST_REQUEST, GET_REQUEST);
			scheduleNameDropDown.triggerAction(this, POST_REQUEST, GET_REQUEST);
			setTemplate(templateLong);
		}

		@Override
		public TemplateMultiselectDataExtended createNewSession() {
			return new TemplateMultiselectDataExtended(this);
		}

		@Override
		public TemplateMultiselectDataExtended getData(OgemaHttpRequest req) {
			return (TemplateMultiselectDataExtended) super.getData(req);
		}

		@Override
		public void onGET(OgemaHttpRequest req) {

			if (selectAllOrDeselectAllButton.hasBeenPushed(req)) {
				return;
			}

			if (scheduleNameDropDown.hasBeenPushed(req)) {
				final List schedules = getSchedules(req);
				final List selected = getSelectedSchedules(req);

				String naming = scheduleNameDropDown.getSelectedValue(req);
				if (SHORT_NAME.equals(naming)) {
					getData(req).setDisplayTemplate(templateShort);
				} else if (LONG_NAME.equals(naming)) {
					getData(req).setDisplayTemplate(templateLong);
				} else {
					getData(req).setDisplayTemplate(templateLocation);
				}
				clear(req);
				setSchedules(schedules, req);
				selectSchedules(selected, req);
				return;
			}

			final SessionConfiguration sessionConfig = getSessionConfiguration(req);

			if (initializing) {
				List<T> selectedTimeSeries = (List<T>) sessionConfig.timeSeriesSelected();
				selectSchedules(selectedTimeSeries, req);
				initializing = false;
				return;
			}

			update(ScheduleViewerBasic.this.update(req), req); // only required because the inner method can be
																// overwritten
			// select all schedules matching the selected filters
			if (programSelectors != null || filterSelectors != null) {
				boolean ok;
				boolean filterSet = false;
				List<T> schedules = getItems(req);
				if (programSelectors != null) {
					for (TemplateMultiselect<TimeSeriesFilterExtended> programSelector : programSelectors) {
						List<TimeSeriesFilterExtended> selectedFilters = programSelector.getSelectedItems(req);
						if (selectedFilters.isEmpty()) {
							continue;
						}
						filterSet = true;
						Iterator<T> it = schedules.iterator();
						T schedule;
						while (it.hasNext()) {
							ok = false;
							schedule = it.next();
							for (TimeSeriesFilterExtended selectedFilter : selectedFilters) {
								if (selectedFilter.accept(schedule)) {
									ok = true;
									break;
								}
							}
							if (!ok)
								it.remove();
						}
					}
				}
				if (filterSelectors != null) {
					for (ConditionalProgramSelector filterSelector : filterSelectors) {
						ConditionalTimeSeriesFilter selectedFilter = filterSelector.filterSelector.getSelectedItem(req);
						if (selectedFilter == null) {
							continue;
						}
						ResourcePattern<?> pattern = filterSelector.instanceSelector.getSelectedItem(req);
						if (pattern == null) {
							continue;
						}
						filterSet = true;
						Iterator<T> it = schedules.iterator();
						while (it.hasNext()) {
							if (!selectedFilter.accept(it.next(), pattern))
								it.remove();
						}
					}
				}
				if (filterSet)
					selectMultipleItems(schedules, req);
			}

			if (sessionConfig.overwritePrograms()) {
				List<T> selectedSchedules = (List<T>) sessionConfig.timeSeriesSelected();
				if (selectedSchedules.isEmpty()) {
					for (TemplateMultiselect<TimeSeriesFilterExtended> programSelector : programSelectors) {
						programSelector.setDefaultVisibility(false);
					}
				} else {
					for (TemplateMultiselect<TimeSeriesFilterExtended> programSelector : programSelectors) {
						programSelector.setDefaultVisibility(true);
					}
					setSchedules(selectedSchedules, req);
					selectSchedules(selectedSchedules, req);
				}
			}

		}
	}

	/**
	 * Dropdown zur Umschaltung der Zeitreihen im Schedule Selector (Location
	 * Longname / Shortname)
	 */

	private class ChangeScheduleNameDropdown extends Dropdown {

		private static final long serialVersionUID = 4525177697823002529L;

		public ChangeScheduleNameDropdown(WidgetPage<?> page, String id) {
			super(page, id);
			final List<DropdownOption> options = new ArrayList<>();
			options.add(new DropdownOption(LONG_NAME, LONG_NAME, false));
			options.add(new DropdownOption(LOCATION, LOCATION, true));			
			options.add(new DropdownOption(SHORT_NAME, SHORT_NAME, false));
			setDefaultOptions(options);
		}

		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			getData(req).pushed = true;
		}
		

		public boolean hasBeenPushed(OgemaHttpRequest req) {
			final boolean pushed = getData(req).pushed;
			getData(req).pushed = false;
			return pushed;
		}

		@Override
		public DropdownDataExented createNewSession() {
			return new DropdownDataExented(this);
		}

		@Override
		public DropdownDataExented getData(OgemaHttpRequest req) {
			return (DropdownDataExented) super.getData(req);
		}

	}

	protected class ViewerDatepickerData extends DatepickerData {

		// overrides default date
		private Long explicitDate = null;
		private boolean fixedInterval = false;
		private boolean init = true;
		private boolean initBug = true;

		public ViewerDatepickerData(ViewerDatepicker datepicker) {
			super(datepicker);
		}
	}

	protected class ViewerDatepicker extends Datepicker {

		private static final long serialVersionUID = 1L;
		private final boolean isStartDatepicker;

		public ViewerDatepicker(WidgetPage<?> page, String id, boolean isStartDatepicker) {
			super(page, id);
			this.isStartDatepicker = isStartDatepicker;
		}

		@Override
		public ViewerDatepickerData createNewSession() {
			return new ViewerDatepickerData(this);
		}

		@Override
		public ViewerDatepickerData getData(OgemaHttpRequest req) {
			return (ViewerDatepickerData) super.getData(req);
		}

		@Override
		public void onGET(OgemaHttpRequest req) {

			if (configuration.showOptionsSwitch && optionsCheckbox.getCheckboxList(req).get(FIX_INTERVAL_OPT)) {
				return;
			}
			final SessionConfiguration sessionConfig = getSessionConfiguration(req);
			
			if (getData(req).init || getData(req).initBug) {
				Long[] time = ScheduleViewerUtil.getStartEndTimeFromParameter(req, page);

				if (time[0] != null && time[1] != null) {

					if (isStartDatepicker) {
						setDate(time[0], req);
					} else {
						setDate(time[1], req);
					}
				}
				
				// FIXME: triggering bug: Widget is calles two times by opening Page - without manualy triggerAction at GET
				if(getData(req).initBug) {
					getData(req).initBug = false;		
					return;
				}
				getData(req).init = false;
				return;
			}

			if (sessionConfig.intervalControllability() == PreSelectionControllability.MAX_SIZE) {
				// use for Time only the Intervall which Data is in the Schedule
				setMaxSize(req);
				getData(req).fixedInterval = false;
				return;
			} else if (sessionConfig.intervalControllability() == PreSelectionControllability.FIXED) {
				getData(req).fixedInterval = true;
			} else {
				getData(req).fixedInterval = false; // PreSelectionControllability.FLEXIBLE
			}

			if (intervalDrop != null && getData(req).fixedInterval) {
				final long now = am.getFrameworkTime();
				final long duration = intervalDrop.getSelectedItem(req);
				final long target;
				if (!isStartDatepicker)
					target = now;
				else
					target = now - duration;
				if (duration > 0) {
					setDate(target, req);
					return;
				}
				getData(req).explicitDate = null;
			}
			final Long explicitDate = getData(req).explicitDate;
			if (explicitDate != null) {
				setDate(explicitDate, req);
				return;
			}
			if ((isStartDatepicker && configuration.startTime != null)
					|| (!isStartDatepicker && configuration.endTime != null)) {
				setDate(System.currentTimeMillis()
						+ (isStartDatepicker ? -configuration.startTime : configuration.endTime), req);
				return;
			}
			final List<T> schedules = scheduleSelector.getSelectedItems(req);
			if (schedules == null || schedules.isEmpty()) {
				setDate(am.getFrameworkTime(), req); // irrelevant
				return;
			}
			long dateTime = (isStartDatepicker ? Long.MAX_VALUE : Long.MIN_VALUE);
			for (ReadOnlyTimeSeries sched : schedules) {
				SampledValue sv = (isStartDatepicker ? sched.getNextValue(Long.MIN_VALUE)
						: sched.getPreviousValue(Long.MAX_VALUE));
				if (sv != null) {
					long timestamp = sv.getTimestamp();
					if ((isStartDatepicker && timestamp < dateTime) || (!isStartDatepicker && timestamp > dateTime))
						dateTime = timestamp;
				}
			}

			if (isStartDatepicker) {
				if (dateTime == Long.MAX_VALUE)
					dateTime = System.currentTimeMillis();
			} else {
				if (dateTime == Long.MIN_VALUE)
					dateTime = System.currentTimeMillis();
				if (dateTime < Long.MAX_VALUE - 10000)
					dateTime += 1001; // ensure all data points are really shown
			}
			setDate(dateTime, req);
		}

		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			getData(req).fixedInterval = false;

		}

		/**
		 * The Date selection may be changed like with schedule viewer without
		 * configuration
		 */
		private void setMaxSize(OgemaHttpRequest req) {
			final List<T> schedules = scheduleSelector.getSelectedItems(req);

			Map<String, Long> possibleDates = ScheduleViewerUtil.getStartEndTime((List<ReadOnlyTimeSeries>) schedules);
			if (possibleDates.isEmpty()) {
				return;
			}
			if (isStartDatepicker) {
				setDate(possibleDates.get(ScheduleViewerUtil.FIRST_POSSIBLE_DATE), req);
			} else {
				setDate(possibleDates.get(ScheduleViewerUtil.LAST_POSSIBLE_DATE), req);
			}

		}

	}

	protected final static Comparator<DropdownOption> defaultLongComparator = new Comparator<DropdownOption>() {

		@Override
		public int compare(DropdownOption d1, DropdownOption d2) {
			long o1 = 0;
			long o2 = 0;
			try {
				o1 = Long.parseLong(d1.id());
			} catch (Exception e) {
			}
			try {
				o2 = Long.parseLong(d2.id());
			} catch (Exception e) {
			}
			return (int) (o1 - o2);
		}
	};

	protected final static DisplayTemplate<Long> intervalDisplayTemplate = new DisplayTemplate<Long>() {

		@Override
		public String getId(Long object) {
			return String.valueOf(object);
		}

		@Override
		public String getLabel(Long object, OgemaLocale locale) {
			return object == 0 ? "all"
					: object == 10 * 60 * 1000L ? "last 10 minutes"
							: object == 60 * 60 * 1000L ? "last hour"
									: object == 24 * 60 * 60 * 1000L ? "last day"
											: object == 2 * 24 * 60 * 60 * 1000L ? "last two days"
													: object == 7 * 24 * 60 * 60 * 1000L ? "last week"
															: object == 30 * 24 * 60 * 60 * 1000L ? "last month"
																	: object == 365 * 24 * 60 * 60 * 1000L ? "last year"
																			: String.valueOf(object) + "ms"; // should
																												// not
																												// occur
		}
	};

	private Button getSaveConfigurationButton(String id) {
		Button button = new Button(page, id + "_saveConfigurationButton", "Save Configuration") {

			private static final long serialVersionUID = -6490863998981034894L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				// TODO: @DN: woher den Integer nehmen?
				Integer conditionalTimeSeriesFilterCategoryPreselected = 0;
				final PersistentSelectionConfiguration currentConfiguration = new PersistentSelectionConfiguration();

				if (filterSelectors != null) {
					for (ConditionalProgramSelector selector : filterSelectors) {
						ConditionalTimeSeriesFilter<?> item = selector.filterSelector.getSelectedItem(req);

						if (item != null) {
							currentConfiguration.filtersPreSelected().add(item);
						}
					}
				}
				currentConfiguration.setConditionalTimeSeriesFilterCategoryPreselected(
						conditionalTimeSeriesFilterCategoryPreselected);

				List<TemplateMultiselect<TimeSeriesFilterExtended>> myProgramSelectors = programSelectors;

				for (TemplateMultiselect<TimeSeriesFilterExtended> selector : myProgramSelectors) {
					List<TimeSeriesFilter> items = ScheduleViewerUtil.reparse(selector.getSelectedItems(req));
					currentConfiguration.programsPreselected().add(items);
				}

				List<T> schedules = getSelectedSchedules(req);
				currentConfiguration.timeSeriesSelected().addAll(schedules);
				configProvider.saveCurrentConfiguration(currentConfiguration, null);
			}
		};

		return button;
	}

	private boolean isExpertMode(OgemaHttpRequest req) {
		String expertMode = ScheduleViewerUtil.getPageParameter(req, page, PARAM_EXPERT_MODE);
		return Boolean.valueOf(expertMode);
	}

}