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
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package com.example.driver.template.drivermodel;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.communication.CommunicationInformation;

public interface TemplateDriverModel extends CommunicationInformation {

	/**
	 * Resource to read/write; change resource type to what the driver provides. If the driver is intended
	 * to write into existing resources that are referenced, rename to "target"
	 */
	FloatResource value();
}
