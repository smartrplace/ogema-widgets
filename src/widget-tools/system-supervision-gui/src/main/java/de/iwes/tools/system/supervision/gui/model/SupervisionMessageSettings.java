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
package de.iwes.tools.system.supervision.gui.model;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Configuration;

public interface SupervisionMessageSettings extends Configuration {

	TimeResource freeDiskWarnThresholdLow();
	TimeResource freeDiskWarnThresholdMedium();
	TimeResource freeDiskWarnThresholdHigh();
	
	TimeResource memoryWarnThresholdLow();
	TimeResource memoryWarnThresholdMedium();
	TimeResource memoryWarnThresholdHigh();
	
	IntegerResource resourcesWarnThresholdLow();
	IntegerResource resourcesWarnThresholdMedium();
	IntegerResource resourcesWarnThresholdHigh();
	
	// state; 0 = not yet triggered, 1 = low, 2 = medium, 3 = high
	IntegerResource lastDiskWarnLevel();
	IntegerResource lastMemoryWarnLevel();
	IntegerResource lastResourceWarnLevel();
	
}
