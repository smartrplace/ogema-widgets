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
package org.ogema.apps.roomlink.utils;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.model.devices.connectiondevices.HeatConnectionBox;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.TemperatureSensor;

/**
 *
 * @author Tobias Gries <tobias.gries@iwes.fraunhofer.de>
 */
public class RoomLinkDummyResourceCreator {

    private final ApplicationManager applicationManager;
    private static final int NUMBER_OF_ROOMS = 5;

    public RoomLinkDummyResourceCreator(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    public boolean generateDummyResources() {

        ResourceManagement resourceManagement = applicationManager.getResourceManagement();
        ResourceAccess resourceAccess = applicationManager.getResourceAccess();

        for (int i = 0; i < NUMBER_OF_ROOMS; i++) {
            Resource resource = resourceAccess.getResource("Room" + i);
            if (resource == null) {
                Room room = resourceManagement.createResource("Room" + i, Room.class);
                IntegerResource roomType = room.type().create();
                roomType.setValue(i + 1);
                room.temperatureSensor().reading().<TemperatureResource> create().setCelsius(17.3F + 1.3F*i);
                room.activate(true);
            }
        }

        TemperatureSensor tempSens = resourceManagement.createResource("TemperatureSensor0", TemperatureSensor.class);
        tempSens.activate(true);

        HeatConnectionBox heatConnectionBox = resourceManagement.createResource("HeatConnectionBox0", HeatConnectionBox.class);
        heatConnectionBox.activate(true);

        FloatResource floatTest = resourceManagement.createResource("FloatResource0", FloatResource.class);
        AbsoluteSchedule schedule = floatTest.program();
        schedule.create();
        floatTest.activate(true);

        for (int i = 0; i < 1000; i++) {
            schedule.addValue(applicationManager.getFrameworkTime() - i * 60000, new FloatValue(10.0f));
        }

        return true;

    }
}
