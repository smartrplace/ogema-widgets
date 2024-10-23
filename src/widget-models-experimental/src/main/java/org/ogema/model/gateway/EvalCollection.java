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
package org.ogema.model.gateway;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;

/** Collection of evaluation information provided on the OGEMA instance
 * Use this model: Note that now org.ogema.model.gateway.eval.plus.EvalCollection is marked deprecated*/
public interface EvalCollection extends PhysicalElement {
	/** This counter shall be incremented each time a change in the device-room structure is detected.
	 * Increments may only be made after several changes that are made directly after each other shall
	 * be committed together. Applications can detect these increments and trigger a restart of the
	 * application logic etc.
	 */
	IntegerResource roomDeviceUpdateCounter();
	
	ResourceList<StringResource> initDoneStatus();
	
	ResourceList<CustomerMessageData> customerMessages();
	
	//ResourceList<IncidentProvider> incidentProviders();
	//ResourceList<IncidentAutoClearanceConfiguration> incidenceAutoClearanceConfigs();
	//ResourceList<IncidentNotificationTriggerConfiguration> incidentTriggerConfigs();
	/**@deprecated: use incidentProviders instead*/
	//@Deprecated
	//IncidentManagement incidents();
	//ResourceList<ScheduleViewerConfig> scheduleConfigs();
	//ScheduleViewerConfig scheduleViewerConfigStandard();
}