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

package de.iwes.widgets.reswidget.scheduleviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.plot.api.PlotType;
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
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.pattern.MultiPatternScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.pattern.PatternScheduleViewer;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A schedule viewer, consisting of a {@link TemplateMultiselect} widget that lets the user choose
 * from a set of schedules/time series, {@link Datepicker}s for the start and end time, and
 * a {@link SchedulePlotFlot SchedulePlot} widget, that displays the selected time series.<br>
 * Note that there are specific versions of this widget available, which can show all 
 * {@link Schedule}s of a specific type, or time series corresponding to ResourcePattern 
 * matches, and determine the list of schedules to be displayed autonomously. 
 * The present version of the ScheduleViewer, on the other hand, requires the time series to be set 
 * explicitly, via the methods {@link #setSchedules(Collection, OgemaHttpRequest)} 
 * or {@link #setDefaultSchedules(Collection)}.<br>
 * 
 * Note: this is always a global widget, in particular it cannot be added as a subwidget
 * to a non-global widget. Several of the subwidgets are non-global, though, such as the
 * time series selector.
 * 
 * @param <T> the type of time series to be displayed. Schedules, RecordedData (resource log data),
 * 		and {@link SchedulePresentationData} are supported, but MemoryTimeSeries are not (directly). 
 * 		The SchedulePresentationData can wrap any kind of ReadOnlyTimeSeries, so in order 
 * 		to display a memory time series, wrap it into a DefaultSchedulePresentationData object. 
 *  
 * @see ResourceScheduleViewer
 * @see PatternScheduleViewer
 * @see MultiPatternScheduleViewer
 * 
 * @author cnoelle
 */
public class ScheduleViewerBasic<T extends ReadOnlyTimeSeries> extends PageSnippet implements ScheduleViewer<T> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Configuration options 
	 */
	public final ScheduleViewerConfiguration configuration;
	protected final DisplayTemplate<T> displayTemplate;
		
	protected final ApplicationManager am;
	private final Alert alert;
	protected final List<Label> programSelectorLabels;
	protected final List<Label> filterSelectorLabels;
	protected final Label scheduleSelectorLabel;
	protected final Label scheduleStartLabel;
	protected final Label scheduleEndLabel;
	protected final Label nrDataPointsLabel;
	protected final Label lineTypeLabel;
	protected final Label downsampleLabel;
	protected final Label triggerIndividualConfigLabel;
	protected final List<TemplateMultiselect<TimeSeriesFilter>> programSelectors;
	protected final List<ConditionalProgramSelector> filterSelectors;
	protected final ScheduleSelector scheduleSelector;
	protected final ViewerDatepicker scheduleStartPicker;
	protected final ViewerDatepicker scheduleEndPicker;
	protected final Label nrDataPoints;
	protected final Button updateButton;
	protected final SchedulePlotFlot schedulePlot; // TODO generic interface
	protected final ScheduleManipulator manipulator;
	protected final StaticHeader manipulatorHeader;
	protected final ScheduleCsvDownload<T> csvDownload;
	protected final StaticHeader downloadHeader;
	protected final Label optionsLabel;
	protected final Checkbox optionsCheckbox;
	protected final Button triggerIndividualConfigPopupButton;
	protected final ConfigPopup<T> individualConfigPopup;
	protected final TemplateDropdown<PlotType> lineTypeSelector;
	protected final Checkbox2 doDownsample;
	protected final ValueInputField<Long> downsampleInterval;
	protected final Flexbox downsampleFlexbox;
	private final static String FIX_INTERVAL_OPT = "Fix interval on schedule switch";
	private final static String SHOW_EMPTY_OPT = "Show empty schedules";
	
	/**
	 * Extended options 
	 */
//	protected ValueResourceDropdown<IntegerResource> intervalDrop = null;
	protected final Label intervalDropLabel;
	protected final TemplateDropdown<Long> intervalDrop;
//	protected Button updateIntervalButton = null;

	/*
	 ************* Methods to be overridden in derived class ******* 
	 */
	
	/**
	 * Determine the items that shall be displayed in the multi-select (items that are available to be
	 * chosen by user). Note that these items are not selected automatically.
	 * By default, the method returns those schedules 
	 * that have been set via the methods
	 * {@link #setDefaultSchedules(Collection)} or {@link #setSchedules(List, OgemaHttpRequest)}.<br> 
	 * 
	 * Override in subclass to specify a different behaviour and to perform other operations that
	 * would be placed in the onGET method of a widget. 
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
	 * @param schedules
	 * @param req
	 */
	public void setSchedules(Collection<T> schedules, OgemaHttpRequest req) {
		scheduleSelector.update(schedules, req);
	}
	
	/**
	 * Set the selectable schedules globally.
	 * @param items
	 */
	public void setDefaultSchedules(Collection<T> items) {
		scheduleSelector.selectDefaultItems(items); // XXX misleading method name
	}
	
	/**
	 * Get all schedules for a particular session.
	 * @param req
	 * @return
	 */
	public List<T> getSchedules(OgemaHttpRequest req) {
		return scheduleSelector.getItems(req);
	}
	
	/**
	 * Get all selected schedules in a particular session.
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
	 * @return
	 */
	@Override
	public FlotConfiguration getDefaultPlotConfiguration() {
		return schedulePlot.getDefaultConfiguration();
	}
	
	/**
	 * Set session-specific plot configurations
	 * @param req
	 * @return
	 */
	@Override
	public FlotConfiguration getPlotConfiguration(OgemaHttpRequest req) {
		return schedulePlot.getConfiguration(req);
	}
	
	/**Extended methods (added functionality by DN)
	 */
//	public void setDefaultIntervalConfigurationResource(IntegerResource intervalToDisplay) {
//		if(intervalDrop != null) {
//			intervalDrop.selectDefaultItem(intervalToDisplay);
//		}
//	}
//	public void setIntervalConfigurationResource(IntegerResource intervalToDisplay, OgemaHttpRequest req) {
//		if(intervalDrop != null) {
//			intervalDrop.selectItem(intervalToDisplay, req);
//		}
//	}
	
	/**
	 * May be null
	 * @return
	 */
	public TemplateDropdown<Long> getIntervalDropdown() {
		return intervalDrop;
	}
	
	/*
	 ****** Constructor and internal methods ****** 
	 */
	
	/**
	 * Create a schedule viewer with default configuration. This means, in particular, that 
	 * no schedule manipulator is shown, and the widget name service is used to determine 
	 * the schedule labels.
	 *  
	 * @param page
	 * @param id
	 * @param am 
	 */
	public ScheduleViewerBasic(WidgetPage<?> page, String id, final ApplicationManager am) {
		this(page, id, am, null, null);
	}
	
	/**
	 * Create a schedule viewer with custom configuration.
	 * 
	 * @param page
	 * @param id
	 * @param am 
	 * @param config
	 * 		Configuration object. May be null, in which case a default configuration is used.
	 * @param displayTemplate
	 * 		Display template for the time series. May be null, in which case a DefaultTimeSeriesDisplayTemplate is used.
	 */
	public ScheduleViewerBasic(WidgetPage<?> page, String id, final ApplicationManager am, ScheduleViewerConfiguration config, DisplayTemplate<T> displayTemplate) {
		super(page, id, true);
		//this.page = page;
		this.am= am;
		if (config == null)
			config = ScheduleViewerConfiguration.DEFAULT_CONFIGURATION;
		this.configuration = config;
		if (displayTemplate == null)
			displayTemplate = new DefaultTimeSeriesDisplayTemplate<>((config.useNameService ? getNameService(): null));
		this.displayTemplate = displayTemplate;
		
		this.alert = new Alert(page,  id + "_alert", "");
		alert.setDefaultVisibility(false);
		this.append(alert,null);

		final boolean showProgramSelector = (config.programs != null);
		final boolean showFilterSelector = (config.filters != null);
		//configDrop.addDefaultOption("all Schedules", "allSchedules", true);
		
		this.scheduleSelectorLabel = new Label(page, id + "_scheduleSelectorLabel", "Select schedule");
		this.scheduleStartLabel = new Label(page,  id + "_scheduleStartLabel", "Select start time");
		this.scheduleEndLabel = new Label(page,  id + "_scheduleEndLabel", "Select end time");
		
		if (!showProgramSelector) {
			this.programSelectorLabels = null;
			this.programSelectors = null;
		} else {
			this.programSelectorLabels = new ArrayList<>();
			this.programSelectors = new ArrayList<>();
			Label programSelectorLabel;
			TemplateMultiselect<TimeSeriesFilter> programSelector;
			int cnt = 0;
			for (Map<String,TimeSeriesFilter> filters : config.programs) {
			
				programSelectorLabel = new Label(page, id + "_programSelectorLabel_" + cnt, "Select a program");
				programSelector = new TemplateMultiselect<TimeSeriesFilter>(page, id + "_programSelector" + cnt++);
				programSelector.selectDefaultItems(filters.values());
				programSelector.setTemplate(new DisplayTemplate<TimeSeriesFilter>() {
	
					@Override
					public String getId(TimeSeriesFilter object) {
						return object.id();
					}
	
					@Override
					public String getLabel(TimeSeriesFilter object, OgemaLocale locale) {
						return object.label(locale);
					}
				});
				programSelectorLabels.add(programSelectorLabel);
				programSelectors.add(programSelector);
			}
		}
		if (!showFilterSelector) {
			this.filterSelectorLabels = null;
			this.filterSelectors = null;
		} else {
			this.filterSelectorLabels = new ArrayList<>();
			this.filterSelectors = new ArrayList<>();
			Label filterSelectorLabel;
			ConditionalProgramSelector filterSelector;
			int cnt = 0;
			for (Map<String,ConditionalTimeSeriesFilter<?>> filters : config.filters) {
				filterSelectorLabel = new Label(page, id + "_filterSelectorLabel_" + cnt, "Select a filter");
				filterSelector = new ConditionalProgramSelector(page, id + "_filterSelector" + cnt++, filters.values(), am.getResourcePatternAccess());
				filterSelectorLabels.add(filterSelectorLabel);
				filterSelectors.add(filterSelector);
			}
		}
		
		this.scheduleSelector = new ScheduleSelector(page, id + "_scheduleSelector");
		if (configuration.showOptionsSwitch) {
			this.optionsLabel = new Label(page,  id + "_fixIntervalLabel", "Options");
			this.optionsCheckbox = new Checkbox(page,  id + "_optionsCheckbox");
			Map<String,Boolean> opts = new LinkedHashMap<String, Boolean>();
			opts.put(FIX_INTERVAL_OPT, false);
			opts.put(SHOW_EMPTY_OPT, true);
			optionsCheckbox.setDefaultList(opts);
		}
		else {
			this.optionsCheckbox = null;
			this.optionsLabel = null;
		}
		if (config.isShowPlotTypeSelector()) {
			this.lineTypeSelector = new TemplateDropdown<PlotType>(page, id + "_lineTypeSelector");
			lineTypeSelector.setDefaultItems(Arrays.asList(
					PlotType.LINE,
					PlotType.LINE_WITH_POINTS,
					PlotType.POINTS,
					PlotType.STEPS,
					PlotType.BAR
			));
			this.lineTypeLabel = new Label(page, id + "_lineTypeLabel", "Select the line type");
		} else {
			this.lineTypeSelector = null;
			this.lineTypeLabel = null;
		}
		if (config.isShowDownsampleInterval()) {
			this.downsampleLabel = new Label(page, id + "_downsampleLabel", "Downsampling interval (ms)");
			this.doDownsample = new Checkbox2(page, id + "_doDownsample");
			this.doDownsample.setDefaultCheckboxList(Collections.singletonList(new DefaultCheckboxEntry("0", "", false)));
			this.downsampleInterval = new ValueInputField<Long>(page, id + "_downsampleInterval", Long.class) {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean enabled = doDownsample.isChecked("0", req);
					if (!enabled)
						disable(req);
					else
						enable(req);
				}
				
			};
			downsampleInterval.setDefaultLowerBound(0);
			downsampleInterval.setDefaultNumericalValue(60000L);
			downsampleFlexbox = new Flexbox(page, id + "_downsampleFlex", true);
			downsampleFlexbox.addItem(doDownsample, null).addItem(downsampleInterval, null);
			doDownsample.setDefaultMargin("1em", false, false, false, true);
		} else {
			this.downsampleInterval = null;
			this.doDownsample = null;
			this.downsampleLabel = null;
			this.downsampleFlexbox = null;
		}
		
//		final BooleanValueCheckBox fixInterval = new BooleanValueCheckBox(page, "fixInterval", "", SendValue.TRUE);
		
		
		this.scheduleStartPicker =  new ViewerDatepicker(page,  id + "_scheduleStartPicker", true);
		this.scheduleEndPicker = new ViewerDatepicker(page,  id + "_scheduleEndPicker", false);
		if(config.showNrPointsPreview) {
			this.nrDataPointsLabel = new Label(page,  id + "_nrDataPointsLabel","Number of data points");
			this.nrDataPoints = new Label(page,  id + "_nrDataPoints" ,"0") {
		
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					List<T> schedules = scheduleSelector.getSelectedItems(req);
					long t0 = System.currentTimeMillis();
					long startTime = t0;
					long endTime = t0;
					try {
						startTime = scheduleStartPicker.getDateLong(req);
						endTime = scheduleEndPicker.getDateLong(req);
					} catch (Exception e) {
					} // may happen as long as no dates are selected
					if (schedules == null || schedules.isEmpty() || startTime > endTime) {
						setText("0", req);
						return;
					}
					int size = 0;
					final Long itv = downsampleInterval != null ? downsampleInterval.getNumericalValue(req) : null;
					if (itv != null && doDownsample.isChecked("0", req) && itv > 0) {
						size = (int) ((endTime - startTime) / itv);
					} else {
						for (ReadOnlyTimeSeries sched: schedules) {
							size += sched.size(startTime, endTime);
						}
					}
					setText(String.valueOf(size), req);
				}
				
			};
		}
		else {
			this.nrDataPoints =null;
			this.nrDataPointsLabel =null;
		}
		if (config.showIndividualConfigBtn) {
			this.triggerIndividualConfigLabel = new Label(page, "triggerIndvConfigLabel", "Configure display settings");
			this.triggerIndividualConfigPopupButton = new Button(page, "triggerIndividualConfigPopup", "Open settings");
			this.individualConfigPopup = new ConfigPopup<>(page, "individualConfigPopup", this);
		}
		else {
			this.triggerIndividualConfigLabel = null;
			this.triggerIndividualConfigPopupButton = null;
			this.individualConfigPopup = null;
		}
		
		this.updateButton = new Button(page,  id + "_updateButton", "Apply");
		
		final boolean showCheckboxes = configuration.showOptionsSwitch;
		
		schedulePlot = new SchedulePlotFlot(page,  id + "_schedulePlot", false, config.bufferWindow) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
//				Schedule schedule = scheduleSelector.getSelectedResource(req);
				final List<T> selectedSchedules = scheduleSelector.getSelectedItems(req);
				long startTime = scheduleStartPicker.getDateLong(req);
				long endTime = scheduleEndPicker.getDateLong(req);
				boolean showEmpty = showCheckboxes ? optionsCheckbox.getCheckboxList(req).get(SHOW_EMPTY_OPT) : true;
				if (startTime > endTime)
					startTime = endTime;
				setInterval(startTime, endTime, req);
				Map<String, SchedulePresentationData> schedules = new LinkedHashMap<String, SchedulePresentationData>();
				for (T sched: selectedSchedules) {
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
					String label = ScheduleViewerBasic.this.displayTemplate.getLabel(sched, req.getLocale());
					schedules.put(label, new DefaultSchedulePresentationData(sched, type, label));
				}
				 final ScheduleDataFlot data = getScheduleData(req);
				 data.setSchedules(schedules);
				 if (lineTypeSelector != null) {
					 final PlotType type = lineTypeSelector.getSelectedItem(req);
					 if (type != null)
						 getConfiguration(req).setPlotType(type);
				 }
				 final Long itv = downsampleInterval == null ? null : downsampleInterval.getNumericalValue(req);
				 if (itv != null && doDownsample.isChecked("0", req) && itv > 0)
					data.setDownsamplingInterval(itv);
				 else
					data.setDownsamplingInterval(-1);
			}
			
		};
		schedulePlot.getDefaultConfiguration().doScale(true); // can be overwritten in app
		
		if (configuration.showCsvDownload) {
			downloadHeader = new StaticHeader(3, "Download CSV data");
			downloadHeader.addStyle(HtmlStyle.ALIGNED_CENTER);
			this.csvDownload = new ScheduleCsvDownload<T>(page, "csvDownload", am.getWebAccessManager()) {

				private static final long serialVersionUID = 1L;
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					this.setSchedules(scheduleSelector.getSelectedItems(req), req);
				}
				
			};
		}
		else {
			this.csvDownload = null;
			this.downloadHeader = null;
		}
		
		if (configuration.showManipulator) {
			manipulatorHeader = new StaticHeader(3, "Manipulate schedule");
			manipulatorHeader.addStyle(HtmlStyle.ALIGNED_CENTER);
			ScheduleManipulatorConfiguration smc = configuration.manipulatorConfiguration;
			ScheduleManipulatorConfiguration newConfig = new ScheduleManipulatorConfiguration(alert, smc.isShowInterpolationMode(),smc.isShowQuality()); // TODO
			this.manipulator = new ScheduleManipulator(page, "manipulator", newConfig) {
	
				private static final long serialVersionUID = 1L;
				
				@Override
				public void onGET(OgemaHttpRequest req) {
	//				Schedule schedule = scheduleSelector.getSelectedResource(req);
					List<T> selectedSchedules = scheduleSelector.getSelectedItems(req);
					if (selectedSchedules.size() != 1 || !(selectedSchedules.get(0) instanceof Schedule)) { // we display the schedule manipulator only if exactly one schedule is selected
						setSchedule(null, req);
						return;
					}
					setSchedule((Schedule) selectedSchedules.get(0), req);
					long startTime = scheduleStartPicker.getDateLong(req);
					setStartTime(startTime, req);
				}
			};
		} else {
			manipulator = null;
			manipulatorHeader = null;
		}
		
		/**New functionality added by DN*/
		if (!config.showStandardIntervals) {
			this.intervalDrop = null;
			this.intervalDropLabel = null;
//			this.updateIntervalButton = null;
		} else {
			this.intervalDropLabel = new Label(page, "intervalDropLabel","Select interval");
			// TODO configurable
			/*List<String> displayedValues = new ArrayList<>();
			displayedValues.add("all");
			displayedValues.add("last 10 minutes");
			displayedValues.add("last hour");
			displayedValues.add("last day");
			displayedValues.add("last two days");
			displayedValues.add("last week");
			displayedValues.add("last month");*/
			final List<Long> defaultValues = Arrays.asList(
					0L, 
					10*60*1000L, 
					60*60*1000L,
					24*60*60*1000L,
					2*24*60*60*1000L,
					7*24*60*60*1000L,
					30*24*60*60*1000L,
					365*24*60*60*1000L);
//			this.intervalDrop = new ValueResourceDropdown<IntegerResource>(page, "intervalDrop", null, displayedValues);
			this.intervalDrop = new TemplateDropdown<Long>(page, "intervalDrop") {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					scheduleStartPicker.getData(req).fixedInterval = true;
					scheduleEndPicker.getData(req).fixedInterval = true;
				}
			};
			intervalDrop.setTemplate(intervalDisplayTemplate);
			intervalDrop.setComparator(defaultLongComparator);
			intervalDrop.setDefaultItems(defaultValues);
//			this.updateIntervalButton = new Button(page, "updateInputButton", "Updata Start Time") {
//				private static final long serialVersionUID = 1L;
//				@Override
//				public void onPrePOST(String data, OgemaHttpRequest req) {
//					scheduleStartPicker.getData(req).updateIntervalButtonPressed = true;
//					scheduleSelector.updateDependentWidgets(req);
//				}
//			};
//			registerDependentWidget(in, scheduleStartPicker);
			//updateIntervalButton.registerDependentWidget(scheduleStartPicker);
			//updateIntervalButton.registerDependentWidget(scheduleEndPicker);
//			registerDependentWidget(updateIntervalButton, schedulePlot);
//			if (configuration.showNrPointsPreview) 
//				registerDependentWidget(updateIntervalButton, nrDataPoints);
			//updateIntervalButton.registerDependentWidget(schedulePlot);
//			if(manipulator != null) registerDependentWidget(updateIntervalButton, manipulator);
		}
		
		
		finishBuildingPage(showProgramSelector, showFilterSelector);
		setDependencies();
		
		/*new PageBuilderFinalizer() {
			@Override
			public void declareDependencies() {
				scheduleSelector.makeInitWidget();
				scheduleSelector.registerDependentWidget(scheduleStartPicker);
				scheduleSelector.registerDependentWidget(scheduleEndPicker);
				scheduleSelector.registerDependentWidget(nrDataPoints);
				//scheduleSelector.triggerAction(scheduleStartPicker.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				//scheduleSelector.triggerAction(scheduleEndPicker.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				//scheduleSelector.triggerAction(nrDataPoints.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				
				scheduleStartPicker.registerDependentWidget(nrDataPoints);
				scheduleEndPicker.registerDependentWidget(nrDataPoints);
				//scheduleStartPicker.triggerAction(nrDataPoints.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				//scheduleEndPicker.triggerAction(nrDataPoints.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				
				updateButton.registerDependentWidget(schedulePlot);
				updateButton.registerDependentWidget(manipulator);
				//updateButton.triggerAction(schedulePlot.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				//updateButton.triggerAction(manipulator.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				scheduleSelector.buildDependencyProcessingLists();
			}
		};*/
	}
	
	protected void finishBuildingPage(boolean showProgramSelector, boolean showFilterSelector) {
		int diff = 0;
		if (configuration.showOptionsSwitch)
			diff++;
		if (configuration.showNrPointsPreview)
			diff++;
		if (configuration.showStandardIntervals)
			diff++;
		if (configuration.showIndividualConfigBtn)
			diff++;
		if (configuration.isShowPlotTypeSelector())
			diff++;
		if (configuration.isShowDownsampleInterval())
			diff++;
		if (showProgramSelector)
			diff += programSelectors.size();
		if (showFilterSelector)
			diff += filterSelectors.size();
		StaticTable table = new StaticTable(4+diff, 2, new int[]{2,4});
		int row = 0;
		if (showProgramSelector) {
			for (int i=0;i<programSelectors.size();i++) {
				table.setContent(row, 0, programSelectorLabels.get(i)).setContent(row++, 1, programSelectors.get(i));
			}
		}
		if (showFilterSelector) {
			for (int i=0;i<filterSelectors.size();i++) {
				table.setContent(row, 0, filterSelectorLabels.get(i)).setContent(row++, 1, filterSelectors.get(i));
			}
		}
		table.setContent(row, 0, scheduleSelectorLabel).setContent(row++, 1, scheduleSelector);
		table.setContent(row, 0, scheduleStartLabel).setContent(row++, 1, scheduleStartPicker);
		table.setContent(row, 0, scheduleEndLabel).setContent(row++, 1, scheduleEndPicker);
		if (configuration.showStandardIntervals)
			table.setContent(row, 0, intervalDropLabel).setContent(row++, 1, intervalDrop);
		if (configuration.showNrPointsPreview)
			table.setContent(row, 0, nrDataPointsLabel).setContent(row++, 1, nrDataPoints);
		if (configuration.isShowDownsampleInterval())
			table.setContent(row, 0, downsampleLabel).setContent(row++, 1, downsampleFlexbox);
		if (configuration.isShowPlotTypeSelector())
			table.setContent(row, 0, lineTypeLabel).setContent(row++, 1, lineTypeSelector);
		if (configuration.showOptionsSwitch)
			table.setContent(row, 0, optionsLabel).setContent(row++, 1, optionsCheckbox);
		if (configuration.showIndividualConfigBtn)
			table.setContent(row, 0, triggerIndividualConfigLabel).setContent(row++, 1, triggerIndividualConfigPopupButton);
		table.setContent(row++, 1, updateButton);

		this.append(table, null);
		this.append(schedulePlot,null).linebreak(null);
		if(csvDownload != null) {
			this.append(downloadHeader, null).linebreak(null).append(csvDownload, null).linebreak(null);
		}
		if(manipulator != null) {
			this.append(manipulatorHeader, null).linebreak(null).append(manipulator,null);			
		}		
		if (individualConfigPopup != null)
			this.append(individualConfigPopup, null);
	}
	
	// XXX we cannot use registerDependentWidget here, because this would make scheduleSelector a governing widget, and
	// hence we could not use it anymore to trigger any other widgets on the page
	private void setDependencies() {
//		scheduleSelector.registerDependentWidget(scheduleStartPicker);
//		scheduleSelector.registerDependentWidget(scheduleEndPicker);
		scheduleSelector.triggerAction(scheduleStartPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		scheduleSelector.triggerAction(scheduleEndPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		if (configuration.showNrPointsPreview) {
			scheduleSelector.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
			
	//		scheduleStartPicker.registerDependentWidget(nrDataPoints);
	//		scheduleEndPicker.registerDependentWidget(nrDataPoints);
			scheduleStartPicker.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			scheduleEndPicker.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
		updateButton.triggerAction(schedulePlot, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		if (manipulator != null)
			updateButton.triggerAction(manipulator, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		if (csvDownload != null) 
			updateButton.triggerAction(csvDownload, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		if (programSelectors != null) {
			for (OgemaWidget programSelector: programSelectors) {
				programSelector.triggerAction(scheduleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				programSelector.triggerAction(scheduleStartPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
				programSelector.triggerAction(scheduleEndPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
				if (configuration.showNrPointsPreview) 
					programSelector.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,2);
			}
		}
		if (filterSelectors != null) {
			for (ConditionalProgramSelector filterSelector: filterSelectors) {
				filterSelector.instanceSelector.triggerAction(scheduleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				filterSelector.instanceSelector.triggerAction(scheduleStartPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
				filterSelector.instanceSelector.triggerAction(scheduleEndPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
				filterSelector.filterSelector.triggerAction(scheduleSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				filterSelector.filterSelector.triggerAction(scheduleStartPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
				filterSelector.filterSelector.triggerAction(scheduleEndPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
				if (configuration.showNrPointsPreview) {
					filterSelector.instanceSelector.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,2);
					filterSelector.filterSelector.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,2);
				}
			}
		}
		if (intervalDrop != null) {
			intervalDrop.triggerAction(scheduleStartPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			intervalDrop.triggerAction(scheduleEndPicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			// must be updated after start and end picker
			if (nrDataPoints != null)
				intervalDrop.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
		}
		
		// trigger subwidgets 
		this.triggerAction(scheduleSelector, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(scheduleStartPicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		this.triggerAction(scheduleEndPicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		if (configuration.showNrPointsPreview)
			this.triggerAction(nrDataPoints, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,2);
		this.triggerAction(schedulePlot, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,2);
		if (triggerIndividualConfigPopupButton != null) 
			individualConfigPopup.trigger(triggerIndividualConfigPopupButton);
		if (downsampleInterval != null) {
			doDownsample.triggerAction(downsampleInterval, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			doDownsample.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			downsampleInterval.triggerAction(nrDataPoints, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
			
	}
	
	protected class ScheduleSelector extends TemplateMultiselect<T> {

		private static final long serialVersionUID = 1L;

		public ScheduleSelector(WidgetPage<?> page, String id) {
			super(page, id);
			setTemplate(displayTemplate);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void onGET(OgemaHttpRequest req) {
			update(ScheduleViewerBasic.this.update(req),req); // only required because the inner method can be overwritten
			// select all schedules matching the selected filters
			if (programSelectors != null || filterSelectors != null) {
				boolean ok;
				boolean filterSet = false;
				List<T> items = getItems(req);
				if (programSelectors != null) {
					for (TemplateMultiselect<TimeSeriesFilter> programSelector: programSelectors) {
						List<TimeSeriesFilter> filters = programSelector.getSelectedItems(req);
						if (filters.isEmpty())
							continue;
						filterSet = true;
						Iterator<T> it = items.iterator();
						T item;
						while (it.hasNext()) {
							ok = false;
							item = it.next();
							for (TimeSeriesFilter filter: filters) {
								if (filter.accept(item)) {
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
					for (ConditionalProgramSelector filterSelector: filterSelectors) {
						ConditionalTimeSeriesFilter filter = filterSelector.filterSelector.getSelectedItem(req);
						if (filter == null)
							continue;
						ResourcePattern<?> pattern = filterSelector.instanceSelector.getSelectedItem(req);
						if (pattern == null)
							continue;
						filterSet = true;
						Iterator<T> it = items.iterator();
						while (it.hasNext()) {
							if (!filter.accept(it.next(), pattern))
								it.remove();
						}
						
					}
				}
				if (filterSet)
					selectMultipleItems(items, req);
			}
		}
	}

	protected class ViewerDatepicker extends Datepicker {

		private static final long serialVersionUID = 1L;
		private final boolean isStart;

		public ViewerDatepicker(WidgetPage<?> page, String id, boolean isStart) {
			super(page, id);
			this.isStart = isStart;
		}
		
		@Override
		public ViewerDatepickerData createNewSession() {
			return new ViewerDatepickerData(this);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ViewerDatepickerData getData(OgemaHttpRequest req) {
			return (ViewerDatepickerData) super.getData(req);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			if(configuration.showOptionsSwitch && optionsCheckbox.getCheckboxList(req).get(FIX_INTERVAL_OPT)) {
				return;
			}
			if (intervalDrop != null && getData(req).fixedInterval) {
				final long now = am.getFrameworkTime();
				final long duration = intervalDrop.getSelectedItem(req);
				final long target;
				if (!isStart)
					target = now;
				else
					target = now-duration;
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
			if ((isStart && configuration.startTime != null) || (!isStart && configuration.endTime != null)) {
				setDate(System.currentTimeMillis()  + (isStart ? -configuration.startTime: configuration.endTime), req);
				return;
			}
			final List<T> schedules = scheduleSelector.getSelectedItems(req);
			if (schedules == null || schedules.isEmpty()) {
				setDate(am.getFrameworkTime(), req); // irrelevant
				return;
			}
			long startTime = (isStart ? Long.MAX_VALUE: Long.MIN_VALUE);
			for (ReadOnlyTimeSeries sched: schedules) {
				SampledValue sv = (isStart ? sched.getNextValue(Long.MIN_VALUE): sched.getPreviousValue(Long.MAX_VALUE));
				if (sv != null) {
					long start0 = sv.getTimestamp();
					if ((isStart && start0 < startTime) || (!isStart && start0 > startTime)) 
						startTime = start0;
				}
			}
			if (isStart) {
				if (startTime == Long.MAX_VALUE)
					startTime = System.currentTimeMillis();
			} else {
				if (startTime == Long.MIN_VALUE)
					startTime = System.currentTimeMillis();
				if (startTime < Long.MAX_VALUE - 10000)
					startTime += 1001; // ensure all data points are really shown
			}
			setDate(startTime, req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			getData(req).fixedInterval = false;
		}
		
	}
	
	protected class ViewerDatepickerData extends DatepickerData {
		
		// overrides default date
		private Long explicitDate = null; 
		private boolean fixedInterval = false;

		public ViewerDatepickerData(ViewerDatepicker datepicker) {
			super(datepicker);
		}
		
	}
	
	
	protected final static Comparator<DropdownOption> defaultLongComparator = new Comparator<DropdownOption>() {

		@Override
		public int compare(DropdownOption d1, DropdownOption d2) {
			long o1 = 0;
			long o2 = 0;
			try {
				o1 = Long.parseLong(d1.id());
			} catch (Exception e) {}
			try {
				o2 = Long.parseLong(d2.id());
			} catch (Exception e) {}
			return (int) (o1-o2);
		}
	};
	protected final static DisplayTemplate<Long> intervalDisplayTemplate = new DisplayTemplate<Long>() {

		@Override
		public String getId(Long object) {
			return String.valueOf(object);
		}

		@Override
		public String getLabel(Long object, OgemaLocale locale) {
			return object == 0 ? "all":
				object == 10*60*1000L ? "last 10 minutes":
				object == 60*60*1000L ? "last hour":
				object == 24*60*60*1000L ? "last day":
				object == 2*24*60*60*1000L ? "last two days":
				object == 7*24*60*60*1000L ? "last week":
				object == 30*24*60*60*1000L ? "last month":
				object == 365*24*60*60*1000L ? "last year":
				String.valueOf(object) + "ms"; // should not occur
		}
	}; 
	
//	private static List<Schedule> convertListToScheduls(List<Resource> resList) {
//		List<Schedule> retval = new ArrayList<>();
//		for(Resource r: resList) {
//			ReadOnlyTimeSeries ts = getTimeSeries(r);
//			if(ts instanceof Schedule)
//			retval.add((Schedule)ts);
//		}
//		return retval;
//	}
//	private static List<ReadOnlyTimeSeries> convertListToTimeSeries(List<Resource> resList) {
//		List<ReadOnlyTimeSeries> retval = new ArrayList<>();
//		for(Resource r: resList) {
//			retval.add(getTimeSeries(r));
//		}
//		return retval;
//	}
//	private static ReadOnlyTimeSeries getTimeSeries(Resource res) {
//		if(res instanceof Schedule) {
//			return (Schedule)res;
//		} else if(res instanceof TimeSeriesPresentationData) {
//			TimeSeriesPresentationData tsp = (TimeSeriesPresentationData)res;
//			Resource r = am.getResourceAccess().getResource(tsp.scheduleLocation().getValue());
//			return getTimeSeries(r);
//		} else if(res instanceof FloatResource) {
//			return ((FloatResource)res).getHistoricalData();
//		}  else if(res instanceof IntegerResource) {
//			return ((IntegerResource)res).getHistoricalData();
//		} else if(res instanceof TimeResource) {
//			return ((TimeResource)res).getHistoricalData();
//		}  else if(res instanceof BooleanResource) {
//			return ((BooleanResource)res).getHistoricalData();
//		} else return null;
//	}
	
}
