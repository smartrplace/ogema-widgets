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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.elsim.hmswitchbox;

import java.util.HashMap;
import java.util.Map;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

public class InternalRelay implements SimulationComplexConfiguration {
	
	private final BooleanResource stateFeedback;
	
	@Override
	public String getValue() {
		return stateFeedback.getValue()?"true":"false";
	}

	@Override
	public boolean setValue(String value) {
		try {
			boolean val = Boolean.parseBoolean(value);
			return stateFeedback.setValue(val);
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public InternalRelay(BooleanResource stateFeedback) {
		this.stateFeedback = stateFeedback;
	}

	@Override
	public String getId() {
		return "Set switchbox state";
	}

	@Override
	public String getDescription() {
		return "Set switchbox internal relay";
	}

	@Override
	public Map<String,String> getOptions() {
		Map<String, String> res = new HashMap<>();
		res.put("true", "on");
		res.put("false", "off");
		return res;
	}
}
