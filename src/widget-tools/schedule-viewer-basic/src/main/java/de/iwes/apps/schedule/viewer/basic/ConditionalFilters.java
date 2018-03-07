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
