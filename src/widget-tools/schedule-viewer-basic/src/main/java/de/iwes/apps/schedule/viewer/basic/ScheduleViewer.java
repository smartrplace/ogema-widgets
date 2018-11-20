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
package de.iwes.apps.schedule.viewer.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.recordeddata.DataRecorder;
import org.ogema.recordeddata.RecordedDataStorage;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.schedulemanipulator.ScheduleManipulatorConfiguration;
import de.iwes.widgets.reswidget.scheduleplot.container.TimeSeriesPlotGeneric;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationBuilder;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + ScheduleViewer.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=index.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=View schedules"
		}
)
public class ScheduleViewer implements LazyWidgetPage {
	
	static final String URL_BASE = "/de/iwes/tools/schedule/viewer-basic";

    @Reference(service=DataRecorder.class)
    private ComponentServiceObjects<DataRecorder> dataRecorder;
    
    public void init(ApplicationManager appMan, WidgetPage<?> page) {
    	new ScheduleViewerInit(page, appMan, dataRecorder);
    	Boolean testRes = Boolean.getBoolean("org.ogema.apps.createtestresources");
		if (testRes)
			createTestResource(appMan);
    }
    
    private static void createTestResource(final ApplicationManager am)  {
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
    
    private static class ScheduleViewerInit {

    	private final static long MAX_UPDATE_INTERVAL = 30000; // do not update values more often than every 30s...
    	private long lastUpdate = System.currentTimeMillis() - 2 * MAX_UPDATE_INTERVAL;
    	private final CopyOnWriteArrayList<ReadOnlyTimeSeries> items = new CopyOnWriteArrayList<>();
    	
    	private final WidgetPage<?> page;
    	private final Header header;
    	private final ScheduleViewerBasic<?> scheduleViewer;
    	
    	ScheduleViewerInit(final WidgetPage<?> page, final ApplicationManager appManager, final ComponentServiceObjects<DataRecorder> dataRecorder) {
    		this.page = page;
    		this.header = new Header(page, "header","Schedule viewer");
			header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
    			page.append(header).linebreak();
    	        
    			final List<Collection<TimeSeriesFilter>> programs = new ArrayList<>(4);
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
    			final List<ConditionalTimeSeriesFilter> filters1 = Arrays.asList((ConditionalTimeSeriesFilter) ConditionalFilters.roomFilter(appManager.getResourceAccess()));
    			final List<Collection<ConditionalTimeSeriesFilter>> filters  = Collections.singletonList(filters1);
    			
    			ScheduleManipulatorConfiguration maipulatorConfig = new ScheduleManipulatorConfiguration(null, true, true);
    			final ScheduleViewerConfiguration config = ScheduleViewerConfigurationBuilder.newBuilder()
    					.setShowManipulator(true)
    					.setShowCsvDownload(true)
    					.setUseNameService(false)
    					.setShowOptionsSwitch(true)
    					.setManipulatorConfiguration(maipulatorConfig)
    					.setShowNrPointsPreview(true)
    					.setShowPlotTypeSelector(true)
    					.setPrograms(programs)
    					.setFilters((List) filters) 
    					.setBufferWindow(24*60*60*1000L)
    					.setShowStandardIntervals(false)
    					.setShowDownsamplingInterval(true)
    					.setShowUpdateInterval(true)
    					.setPlotLibrary(TimeSeriesPlotGeneric.class)
    					.build();
//    	        ScheduleViewerConfiguration config = new ScheduleViewerConfiguration(true, true, false, true, maipulatorConfig, true, null, null, programs, (List) filters, 24*60*60*1000L);
    	        config.showIndividualConfigBtn = true;
    	        config.showStandardIntervals = true;
//    	        ResourceScheduleViewer<Schedule> widget = new ResourceScheduleViewer<>(page, "viewerWidget", appManager, config, null, Schedule.class);
    	        this.scheduleViewer = new ScheduleViewerBasic<ReadOnlyTimeSeries>(page, "viewerWidget", appManager, config, null) {

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
    					final DataRecorder dr = dataRecorder.getService();
    					try {
    						for (String id : dr.getAllRecordedDataStorageIDs()) {
    							rds = dr.getRecordedDataStorage(id);
    							if (rds != null) {
    								newScheds.add(rds);
    							}
    						}
    					} finally {
    						dataRecorder.ungetService(dr);
    					}
    					items.retainAll(newScheds);
    					items.addAllAbsent(newScheds);
    					return items;
    				}
    				
    	        };
    	        scheduleViewer.getDefaultPlotConfiguration().doScale(false);
    	        
    	        page.showOverlay(true);
    	        buildPage();
    	}
    	
    	private final void buildPage() {
    		page.append(header).linebreak().append(scheduleViewer);
    	}
    	
    }
    
}
