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
package de.iwes.timeseries.eval.base.provider.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

public class EvaluationInputImpl implements EvaluationInput {
	
	private final List<TimeSeriesData> timeSeries;
	private final boolean online;
	
	public EvaluationInputImpl(List<TimeSeriesData> timeSeries) {
		this.timeSeries = Collections.unmodifiableList(new ArrayList<>(timeSeries));
		boolean online = false;
		for (TimeSeriesData tsd : timeSeries) {
			if(tsd instanceof TimeSeriesDataOffline) {
				if (((TimeSeriesDataOffline)tsd).getTimeSeries() instanceof OnlineTimeSeries) { 
					online = true;
					break;
				}
			} else {
				online = true;
				break;
			}
		}
		this.online = online; 
	}

	@Override
	public boolean isOnlineEvaluation() {
		return online;
	}

	@Override
	public List<TimeSeriesData> getInputData() {
		return timeSeries;
	}

}
