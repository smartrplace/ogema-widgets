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
package com.example.driver.template.pattern;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.communication.DeviceAddress;
import org.ogema.model.communication.IPAddressV4;

import com.example.driver.template.drivermodel.TemplateDriverModel;

public class TemplateDriverPattern extends ResourcePattern<TemplateDriverModel> { 
	
	public final DeviceAddress address = model.comAddress();
	
	/**
	 * TODO this is an example for drivers based on IP communication; adapt to your needs
	 */
	@Existence(required=CreateMode.OPTIONAL)
	public final IPAddressV4 ipAddress = address.ipV4Address();
	
	public final FloatResource value = model.value();
	
	/**
	 * If the resource does not exist, we assume the data point not to be writeable
	 */
	@Existence(required=CreateMode.OPTIONAL)
	public final BooleanResource writeable = address.writeable();
	
	/**
	 * If the resource does not exist, we assume the data point not to be readable
	 */
	@Existence(required=CreateMode.OPTIONAL)
	public final BooleanResource readable = address.readable();
	
	@Existence(required=CreateMode.OPTIONAL)
	public final TimeResource pollingInterval = model.pollingConfiguration().pollingInterval();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework. Must be public.
	 */
	public TemplateDriverPattern(Resource device) {
		super(device);
	}

}
