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
package de.iwes.timeseries.eval.api.configuration;

import java.util.Collection;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * The start time and end time configuration. This information is provided to all
 * eval providers, independently of whether they declare this configuration explicitly or not.
 */
public enum StartEndConfiguration implements Configuration<StartEndInstance> {

	START_CONFIGURATION(true),
	END_CONFIGURATION(false);
	
	private final boolean startOrEnd;
	
	private StartEndConfiguration(boolean startOrEnd) {
		this.startOrEnd = startOrEnd;
	}
	
	@Override
	public String id() {
		return startOrEnd ? "start" : "end";
	}

	@Override
	public String label(OgemaLocale locale) {
		return startOrEnd ? "Start" : "End";
	}

	@Override
	public String description(OgemaLocale locale) {
		return startOrEnd ? "Evaluation start time" : "Evaluation end time";
	}
	
	@Override
	public Class<StartEndInstance> configurationType() {
		return StartEndInstance.class;
	}
	
	@Override
	public void filter(StartEndInstance instance) throws IllegalArgumentException {
	}
	
	@Override
	public Collection<ResultType> getApplicableResultTypes() {
		return null;
	}
	
	@Override
	public StartEndInstance defaultValues() {
		return null;
	}
	
	@Override
	public boolean isOptional() {
		return false;
	}
	
}
