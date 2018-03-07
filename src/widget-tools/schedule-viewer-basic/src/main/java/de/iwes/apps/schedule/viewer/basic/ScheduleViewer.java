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

package de.iwes.apps.schedule.viewer.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.recordeddata.DataRecorder;
import org.ogema.recordeddata.RecordedDataStorage;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.schedulemanipulator.ScheduleManipulatorConfiguration;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationBuilder;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;

@Component(specVersion = "1.2")
@Service(Application.class)
public class ScheduleViewer implements Application {

	private final static long MAX_UPDATE_INTERVAL = 30000; // do not update values more often than every 30s...
	private long lastUpdate = System.currentTimeMillis() - 2 * MAX_UPDATE_INTERVAL;
	private final CopyOnWriteArrayList<ReadOnlyTimeSeries> items = new CopyOnWriteArrayList<>();
    private OgemaLogger logger;
    private WidgetApp wApp;
    private ApplicationManager am;
    
    @Reference
    private OgemaGuiService guiService;
    
    @Reference
    private DataRecorder dataRecorder;
    
    @Reference
    private OnlineTimeSeriesCache timeSeriesCache;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void start(ApplicationManager appManager) {
    	this.am = appManager;
        this.logger = appManager.getLogger();
        logger.info("{} started", getClass().getName());
        
        wApp = guiService.createWidgetApp("/de/iwes/tools/schedule/viewer-basic", appManager);
        WidgetPage<?> viewSchedulePage = wApp.createWidgetPage("index.html",true);
        
        // page
        Header header = new Header(viewSchedulePage, "header","Schedule viewer");
		header.addDefaultStyle(HeaderData.CENTERED);
		viewSchedulePage.append(header).linebreak();
        
		List<Collection<TimeSeriesFilter>> programs = new ArrayList<>();
		List<TimeSeriesFilter> programs1 = Arrays.asList( 
				TimeSeriesFilter.ALL_TIME_SERIES,
				TimeSeriesFilter.LOG_DATA_ONLY,
				TimeSeriesFilter.SCHEDULES_ONLY);
		programs.add(programs1);
		programs1 = Arrays.asList( 
				TimeSeriesFilter.ALL_TEMPERATURES, 
				TimeSeriesFilter.ALL_HUMIDITIES,
				TimeSeriesFilter.ALL_POWER,
				TimeSeriesFilter.OTHER_SENSORS, 
				TimeSeriesFilter.ALL_FORECASTS );
		programs.add(programs1);
		List<Collection<ConditionalTimeSeriesFilter>> filters = new ArrayList<>();
		List<ConditionalTimeSeriesFilter> filters1 = Arrays.asList((ConditionalTimeSeriesFilter) ConditionalFilters.roomFilter(appManager.getResourceAccess()));
		filters.add(filters1);
		
		ScheduleManipulatorConfiguration maipulatorConfig = new ScheduleManipulatorConfiguration(null, true, true);
		final ScheduleViewerConfiguration config = ScheduleViewerConfigurationBuilder.newBuilder()
				.setShowManipulator(true)
				.setShowCsvDownload(true)
				.setUseNameService(false)
				.setShowOptionsSwitch(true)
				.setManipulatorConfiguration(maipulatorConfig)
				.setShowNrPointsPreview(true)
				.setPrograms(programs)
				.setFilters((List) filters) 
				.setBufferWindow(24*60*60*1000L)
				.build();
//        ScheduleViewerConfiguration config = new ScheduleViewerConfiguration(true, true, false, true, maipulatorConfig, true, null, null, programs, (List) filters, 24*60*60*1000L);
        config.showIndividualConfigBtn = true;
        config.showStandardIntervals = true;
//        ResourceScheduleViewer<Schedule> widget = new ResourceScheduleViewer<>(viewSchedulePage, "viewerWidget", appManager, config, null, Schedule.class);
        ScheduleViewerBasic<?> widget = new ScheduleViewerBasic<ReadOnlyTimeSeries>(viewSchedulePage, "viewerWidget", appManager, config, null) {

			private static final long serialVersionUID = 1L;
        	
			@Override
			protected List<ReadOnlyTimeSeries> update(final OgemaHttpRequest req) {
				final long now = System.currentTimeMillis();
				final boolean cancelUpdate;
				synchronized (this) {
					if (now-lastUpdate < MAX_UPDATE_INTERVAL)
						cancelUpdate = true;
					else {
						lastUpdate = now;
						cancelUpdate = false;
					}
				}
				if (cancelUpdate)
					return items;
				final List<ReadOnlyTimeSeries> newScheds = new ArrayList<>();
				for (ReadOnlyTimeSeries schedule : am.getResourceAccess().getResources(Schedule.class)) {
					newScheds.add(schedule);
				}
				RecordedDataStorage rds;
				for (String id : dataRecorder.getAllRecordedDataStorageIDs()) {
					rds = dataRecorder.getRecordedDataStorage(id);
					if (rds != null) {
						newScheds.add(rds);
					}
				}
				items.retainAll(newScheds);
				items.addAllAbsent(newScheds);
				return items;
			}
			
        };
        
        
        viewSchedulePage.append(widget);
        	
        viewSchedulePage.showOverlay(true);
        
        // 2n page
        final WidgetPage<?> onlinePage = wApp.createWidgetPage("onlineData.html");
        new OnlineDataViewer(onlinePage, timeSeriesCache, appManager);
        
        	// navigation menu
        NavigationMenu customMenu = new NavigationMenu(" Select page");
		customMenu.addEntry("View schedules", viewSchedulePage);
		customMenu.addEntry("View online data", onlinePage);
		MenuConfiguration mc = viewSchedulePage.getMenuConfiguration();
		mc.setCustomNavigation(customMenu);
		mc = onlinePage.getMenuConfiguration();
		mc.setCustomNavigation(customMenu);
		Boolean testRes = Boolean.getBoolean("org.ogema.apps.createtestresources");
		if (testRes)
			createTestResource();
		//
//		configs = new ResourcePatternManagement<SchedViewConfigPattern, ScheduleViewer>(appManager, SchedViewConfigPattern.class, this);
    }

    @Override
    public void stop(AppStopReason reason) {
        if (wApp != null)
        	wApp.close();    	
    	logger.info("{} stopped", getClass().getName());
    	logger = null;
    	wApp = null;
    	am = null;
    }

   
    private void createTestResource()  {
    	TemperatureSensor fl = am.getResourceManagement().createResource("scheduleViewerBasicTestResource", TemperatureSensor.class);
    	fl.reading().program().create();
    	Room room = am.getResourceManagement().createResource("testRoom", Room.class);
    	fl.location().room().setAsReference(room);
    	long t0 = am.getFrameworkTime();
    	List<SampledValue> values = new ArrayList<>();
    	int nrValues = 10;
    	for (int i=0; i< nrValues; i++) {
    		float value = (float) Math.sin(2*Math.PI * i/nrValues);
    		SampledValue sv= new SampledValue(new FloatValue(value), t0 + i* 10*60*1000, Quality.GOOD);
    		values.add(sv);
    	}
    	fl.reading().program().addValues(values);
    	fl.reading().program().activate(false);
    	room.activate(false);
    }
    
}
