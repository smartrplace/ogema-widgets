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
package de.iwes.timeseries.eval.garo.api.base;

import java.util.List;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.util.resource.ResourceHelper.DeviceInfo;

public class EvaluationInputImplGaRo extends EvaluationInputImpl {
	
	private final List<DeviceInfo> deviceInfo;
	
	public EvaluationInputImplGaRo(List<TimeSeriesData> timeSeries, List<DeviceInfo> deviceInfo) {
		super(timeSeries);
		this.deviceInfo = deviceInfo;
	}

	public List<DeviceInfo> getDeviceInfo() {
		return deviceInfo;
	}
}
