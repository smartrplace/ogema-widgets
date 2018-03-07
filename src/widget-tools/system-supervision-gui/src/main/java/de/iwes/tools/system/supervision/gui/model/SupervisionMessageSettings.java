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
package de.iwes.tools.system.supervision.gui.model;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Configuration;

import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

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
