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
package com.example.app.template.pattern;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.model.sensors.TemperatureSensor;

import com.example.app.template.TemplateController;

public class TemplateContextPattern extends ContextSensitivePattern<TemperatureSensor, TemplateController> { 
	
	//@Access(mode = AccessMode.READ_ONLY)
	//@Existence(required=CreateMode.OPTIONAL)
	public final StringResource name = model.name();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework. Must be public
	 */
	public TemplateContextPattern(Resource device) {
		super(device);
	}
	
	/**
	 * Custom acceptance check
	 */
	@Override
	public boolean accept() {
		
		String serverIP = context.appConfigData.sampleElement().getValue();
		context.log.info("New pattern available; resource path: " + model.getPath() + "; IP: " + serverIP); 
		
		return true;
	}
}
