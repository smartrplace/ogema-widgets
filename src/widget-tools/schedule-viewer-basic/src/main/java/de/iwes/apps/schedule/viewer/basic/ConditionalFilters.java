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

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.tools.standard.pattern.StandardRoomPattern;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;

public class ConditionalFilters {

	public static final ConditionalTimeSeriesFilter<StandardRoomPattern> roomFilter(final ResourceAccess ra) {
		
		return new ConditionalTimeSeriesFilter<StandardRoomPattern>() {
	
			@Override
			public String id() {
				return "room_filter";
			}
	
			@Override
			public String label(OgemaLocale locale) {
				return "Device location (room)";
			}
	
			@Override
			public boolean accept(ReadOnlyTimeSeries schedule) {
				return true;
			}
	
			@Override
			public Class<StandardRoomPattern> getPatternClass() {
				return StandardRoomPattern.class;
			}
	
			@Override
			public boolean accept(ReadOnlyTimeSeries schedule, StandardRoomPattern instance) {
				final Resource r;
				if (schedule instanceof Schedule) 
					r = (Schedule) schedule;
				else if (schedule instanceof RecordedData) 
					r = ra.getResource(((RecordedData) schedule).getPath());
				else
					r = null;
				final Room room = ResourceUtils.getDeviceRoom(r);
				if (room == null)
					return false;
				return room.equalsLocation(instance.model);
			}
		};
	}
	
	
}
