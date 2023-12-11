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
package de.iwes.widgets.html.schedulemanipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.TimeSeries;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;

/**
 * Displays the data points of a schedule in tabular form. Mostly suitable to small
 * size schedules. Supports paging.
 * 
 * This is always a global widget, but the table displaying the data may show session-specific data
 */
public class ScheduleManipulator extends PageSnippet {

	/**
	 * Use this to trigger an update of other widgets upon changes made to a schedule via this widget.
	 */
	public static final TriggeringAction SCHEDULE_CHANGED = new TriggeringAction("scheduleChanged");
	
	private static final long serialVersionUID = 550753654103033620L;   
	private TimeSeries defaultSchedule = null;
	protected final DynamicTable<Long> table;  // TODO make this an actual subwidget; must be destroyed upon destruction of the manipulator?
	private final boolean showQuality;
	private final WidgetGroup valueGroup;
	private final Label interpolationLabel;
	protected final Dropdown interpolationDropdown;
	private final Label startTimeLabel;
	protected final Datepicker startTimePicker;
	private final Label nrItemsLabel;
	protected final TemplateDropdown<Integer> nrItemsDropdown;
	private final Button nextButton;
	private final Button firstButton;
	
	private boolean defaultAllowPointAddition = true;
	
	public final ApplicationManager appMan;
	
	/** Last chart interval choice */
    public static long lastPlotStart = -1;
    public static long lastPlotEnd = -1;    
	
	/*
	 ************************** constructors ***********************/
    
    /** 
     * Default constructor: session dependent 
     */
    public ScheduleManipulator(WidgetPage<?> page, String id) {
        this(page, id, null);
	}
    
    public ScheduleManipulator(WidgetPage<?> page, String id, ScheduleManipulatorConfiguration config,
    		ApplicationManager appMan) {
        this(page, id, false, config, appMan);
	}
    public ScheduleManipulator(WidgetPage<?> page, String id, ScheduleManipulatorConfiguration config) {
        this(page, id, false, config);
	}
    
    public ScheduleManipulator(WidgetPage<?> page, String id, boolean globalWidget, ScheduleManipulatorConfiguration config) {
    	this(page, id, globalWidget, config, null);
    }
    public ScheduleManipulator(WidgetPage<?> page, String id, boolean globalWidget, ScheduleManipulatorConfiguration config,
    		ApplicationManager appMan) {
        super(page, id, true); // this itself is always a global widget
        this.appMan = appMan;
        if (config == null)
        	config = new ScheduleManipulatorConfiguration();
        this.showQuality = config.isShowQuality();
        this.table = new DynamicTable<Long>(page, id + "__XX__table", globalWidget) {

			private static final long serialVersionUID = 1L;
			
			private List<Long> getValues(OgemaHttpRequest req) {
				final TimeSeries schedule = getSchedule(req);
				if (schedule == null)
					return Collections.emptyList();
				final List<Long> values = new ArrayList<>();
				values.add(ScheduleManipulatorData.HEADER_LINE_ID);
				if (isAllowPointAddition(req)) {
					values.add(ScheduleManipulatorData.NEW_LINE_ID);
				}
				final long startTime = startTimePicker.getDateLong(req);
				final Iterator<SampledValue> it = getSchedule(req).iterator(startTime, Long.MAX_VALUE);
				final int nrValues = nrItemsDropdown.getSelectedItem(req);
				int cnt = 0;
				while (cnt++ < nrValues && it.hasNext()) {
					values.add(it.next().getTimestamp());
				}
				return values;
			}
        	
			@Override
			public void onGET(OgemaHttpRequest req) {
				updateRows(getValues(req),req);
			}
        	
        };
        this.valueGroup = page.registerWidgetGroup(id + "_valueWidgets", new HashSet<OgemaWidget>());
        if (!config.isShowInterpolationMode()) {
        	this.interpolationLabel = null;
        	this.interpolationDropdown = null;
        }
        else {
//        	StaticTable st = new StaticTable(1, 3, new int[]{2,2,8});
        	this.interpolationLabel = new Label(page, id + "__XX__interpolLabel","Interpolation Mode: ");
        	this.interpolationDropdown = new InterpolationDropdown(page, id + "__XX__interpolDD", this);
//        	st.setContent(0, 0, interpolationLabel);
//        	st.setContent(0, 1, interpolationDropdown);
//        	page.append(st);
        	this.triggerAction(interpolationDropdown, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
        }
        ScheduleRowTemplate  rowTemplate = new ScheduleRowTemplate(table,this,valueGroup, config.getAlert(), showQuality);
        table.setRowTemplate(rowTemplate);
        table.addDefaultStyle(DynamicTableData.CELL_ALIGNMENT_RIGHT);

        this.nrItemsLabel = new Label(page, id + "__XX__nrItemsLabel",
        		System.getProperty("org.ogema.app.timeseries.viewer.expert.gui.selectendtimelabel", "Number of data points"));
        this.nrItemsDropdown = new TemplateDropdown<Integer>(page, id + "__XX__nrItemsDropdown");
        List<Integer> opts = Arrays.asList(1,5,10,15,25,40,50);
        nrItemsDropdown.setDefaultItems(opts);
        nrItemsDropdown.selectDefaultItem(10);
        
        this.startTimeLabel = new Label(page, id + "__XX__startTimeLabel", "Start time");
        this.startTimePicker = new Datepicker(page, id+ "__XX__startTimePicker");
        this.firstButton = new Button(page, id + "__XX__firstButton", "First") {

        	private static final long serialVersionUID = 1L;
        		
        	@Override
        	public void onGET(OgemaHttpRequest req) {
        		TimeSeries schedule = getSchedule(req);
        		if (schedule == null) 
        			disable(req);
        		else 
        			enable(req);
        	}
        	
        	@Override
        	public void onPOSTComplete(String data, OgemaHttpRequest req) {
        		TimeSeries schedule = getSchedule(req);
        		if (schedule == null) 
        			return;
        		SampledValue sv = schedule.getNextValue(Long.MIN_VALUE);
        		if (sv == null)
        			return;
        		startTimePicker.setDate(sv.getTimestamp(), req);
        	}
        	
        	
        };
        this.nextButton = new Button(page, id + "__XX__nextButton", "Next") {

			private static final long serialVersionUID = 1L;
			
			private Long getNextTimestamp(OgemaHttpRequest req) {
				final TimeSeries schedule = getSchedule(req);
        		if (schedule == null) {
        			return null;
        		}
        		final long startTime = startTimePicker.getDateLong(req);
        		int nrItems = nrItemsDropdown.getSelectedItem(req);
        		final Iterator<SampledValue> it = schedule.iterator(startTime, Long.MAX_VALUE);
        		while (nrItems-- > 0 && it.hasNext()) {
        			it.next();
        		}
        		boolean hasMore = (nrItems == -1 && it.hasNext());
        		if (hasMore)
        			return it.next().getTimestamp();
        		else 
        			return null;
			}

			@Override
        	public void onGET(OgemaHttpRequest req) {
        		if (getNextTimestamp(req) == null) 
        			disable(req);
        		else 
        			enable(req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				Long next = getNextTimestamp(req);
				if (next == null)
					return;
				startTimePicker.setDate(next, req);
			}
        		
        };
        this.triggerAction(startTimePicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
        this.triggerAction(firstButton, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
        this.triggerAction(nextButton, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
        this.triggerAction(table, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
        table.triggerAction(valueGroup, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
        this.triggerAction(this, SCHEDULE_CHANGED, TriggeredAction.GET_REQUEST);
        startTimePicker.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        startTimePicker.triggerAction(nextButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        nextButton.triggerAction(startTimePicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        nextButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
        nextButton.triggerAction(nextButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
        firstButton.triggerAction(startTimePicker, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        firstButton.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
        firstButton.triggerAction(nextButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
        nrItemsDropdown.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        nrItemsDropdown.triggerAction(nextButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        
    	Flexbox fb = new Flexbox(page, id + "__XX__upperflexbox", true); // TODO Flexbox settings
        if (config.isShowInterpolationMode()) {
        	fb.addItem(interpolationLabel,null).addItem(interpolationDropdown, null);
        }
        fb.addItem(nrItemsLabel, null).addItem(nrItemsDropdown, null); // TODO new row?
        fb.addItem(startTimeLabel, null).addItem(startTimePicker, null);
    	this.append(fb, null).linebreak(null);
        this.append(table,null).linebreak(null);
    	fb = new Flexbox(page, id + "__XX__lowerflexbox", true); // TODO Flexbox settings
    	fb.addItem(firstButton, null).addItem(nextButton, null);
    	fb.setDefaultJustifyContent(JustifyContent.FLEX_LEFT);
    	this.append(fb, null);
    }
    
    /*
     ******* Inherited methods ******/   
    
    /*
     * (non-Javadoc)
     * Duplicate of {@see org.ogema.tools.widget.html.calendar.datepicker.Datepicker#registerJsDependencies()} method; 
     * required because the Datepicker subwidgets of this widget are only created upon the first request 
     */
    @Override
    protected void registerJsDependencies() {
    	registerLibrary(true, "moment", "/ogema/widget/datepicker/lib/moment-with-locales_2.10.0.min.js"); // FIXME global moment variable will be removed in some future version
        registerLibrary(true, "jQuery.fn.datetimepicker", "/ogema/widget/datepicker/lib/bootstrap-datetimepicker_4.17.37.min.js"); 
    	super.registerJsDependencies();
    }
	
	@Override
	public ScheduleManipulatorData createNewSession() {
		ScheduleManipulatorData opt = new ScheduleManipulatorData(this,showQuality);
		return opt;
	}
    
	@Override
	public ScheduleManipulatorData getData(OgemaHttpRequest req) {
		return (ScheduleManipulatorData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(PageSnippetData opt) {
		super.setDefaultValues(opt);
		((ScheduleManipulatorData) opt).setSchedule(defaultSchedule);
		((ScheduleManipulatorData) opt).setAllowPointAddition(defaultAllowPointAddition);
	}
	
	@Override
	public void destroy() {
		this.table.destroyWidget();
		super.destroyWidget();
	}
	
	
	/*
	 ************************* public methods ***********************/
	
	public void addPoint(long timestamp, Value value, OgemaHttpRequest req) {
		table.addItem(timestamp, req);
	}
	
	public void removePoint(long timestamp, OgemaHttpRequest req) {
		table.removeRow(String.valueOf(timestamp), req);
	}
	
	public TimeSeries getDefaultSchedule() {
		return defaultSchedule;
	}

	public void setDefaultSchedule(TimeSeries defaultSchedule) {
		this.defaultSchedule = defaultSchedule;
	}

	public TimeSeries getSchedule(OgemaHttpRequest req) {
		return getData(req).getSchedule();
	}

	public void setSchedule(TimeSeries schedule, OgemaHttpRequest req) {
		getData(req).setSchedule(schedule);
	}
	
	public long getStartTime(OgemaHttpRequest req) {
		return startTimePicker.getDateLong(req);
	}

	public void setStartTime(long startTime,OgemaHttpRequest req) {
		startTimePicker.setDate(startTime, req);
	}
	
	public boolean isAllowPointAddition(OgemaHttpRequest req) {
		return getData(req).isAllowPointAddition();
	}

	public void setAllowPointAddition(boolean allowPointAddition, OgemaHttpRequest req) {
		getData(req).setAllowPointAddition(allowPointAddition);
	}
	
	public void setDefaultAllowPointAddition(boolean allowPointAddition) {
		this.defaultAllowPointAddition = allowPointAddition;
	}
	
	/*
	 ************************* internal methods, etc ***********************/
	
	private static class InterpolationDropdown extends Dropdown {

		private static final long serialVersionUID = 1L;
		private final ScheduleManipulator manipulator;
		
		public InterpolationDropdown(WidgetPage<?> page, String id,ScheduleManipulator manipulator) {
			super(page, id);
			this.manipulator = manipulator;
			setDefaultOptions(getDdOptions());
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			TimeSeries schedule = manipulator.getSchedule(req);
			if (schedule == null) return;
			DropdownOption opt = getSelected(req);
			if (opt == null) return;
			String val = opt.id();
			InterpolationMode im = InterpolationMode.valueOf(val);
			if (im == null) return;
			schedule.setInterpolationMode(im);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			TimeSeries schedule = manipulator.getSchedule(req);
			if (schedule == null) {
				selectSingleOption("NONE", req);
				return;
			}
			InterpolationMode im = schedule.getInterpolationMode();
			if (im == null) {
				selectSingleOption("NONE", req);
				return;
			}
			selectSingleOption(im.name(), req);
		}
	}
	
	private static Set<DropdownOption> getDdOptions() {
		Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
		String opt;
		for (InterpolationMode mode: InterpolationMode.values()) {
			opt = mode.name();
			options.add(new DropdownOption(opt,opt,false));
		}
		return options;
	}
	
}