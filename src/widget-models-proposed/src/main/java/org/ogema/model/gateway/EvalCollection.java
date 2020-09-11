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

import org.ogema.model.prototypes.PhysicalElement;

/** Collection of evaluation information provided on the OGEMA instance
 * Use this model: Note that now org.ogema.model.gateway.eval.plus.EvalCollection is marked deprecated*/
public interface EvalCollection extends PhysicalElement {
	//ResourceList<IncidentProvider> incidentProviders();
	//ResourceList<IncidentAutoClearanceConfiguration> incidenceAutoClearanceConfigs();
	//ResourceList<IncidentNotificationTriggerConfiguration> incidentTriggerConfigs();
	/**@deprecated: use incidentProviders instead*/
	//@Deprecated
	//IncidentManagement incidents();
	//ResourceList<ScheduleViewerConfig> scheduleConfigs();
	//ScheduleViewerConfig scheduleViewerConfigStandard();
}