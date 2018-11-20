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
package de.iwes.timeseries.eval.api.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

public class EfficientTimeSeriesArray {
	public float[] data;
	public String[] timeStamps;
	
	public static EfficientTimeSeriesArray getInstance(ReadOnlyTimeSeries timeSeries) {
		EfficientTimeSeriesArray result = new EfficientTimeSeriesArray();
		if(timeSeries == null) return result;
		int len = timeSeries.size();
		result.data = new float[len];
		result.timeStamps = new String[len];
		int i = 0;
		for(SampledValue v: timeSeries.getValues(Long.MIN_VALUE)) {
			result.data[i] = v.getValue().getFloatValue();
			result.timeStamps[i] = new DateTime(v.getTimestamp()).toString();
			i++;
		}
		return result;
	}
	
	public FloatTreeTimeSeries toFloatTimeSeries() {
		List<SampledValue> vlist = new ArrayList<>();
		FloatTreeTimeSeries result = new FloatTreeTimeSeries();
		if(timeStamps == null) return result;
		for(int i=0; i<timeStamps.length; i++) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
			try {
			    Date d = f.parse(timeStamps[i]);
			    long t = d.getTime();
				vlist.add(new SampledValue(new FloatValue(data[i]), t, Quality.GOOD));
			} catch (ParseException e) {
			    e.printStackTrace();
			}
		}
		result.addValues(vlist);
		return result;
	}

}
