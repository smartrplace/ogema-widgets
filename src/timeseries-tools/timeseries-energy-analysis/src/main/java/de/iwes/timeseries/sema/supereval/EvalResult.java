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
package de.iwes.timeseries.sema.supereval;

import java.io.PrintStream;
import java.util.List;

import de.iwes.util.format.StringFormatHelper;

public class EvalResult {
	
	public enum DataType {
		TemperatureMeasurement,
		TemperatureSetpoint,
		ValvePosition,
		HumidityMeasurement,
		PowerMeter,
		MotionDetection,
		WindowOpen,
		CompetitionLevel,
		CompetitionPosition,
		CompetitionPoints,
		ChargeSensor,
		Unknown,
		Any,
		LowLevel, 
	}
	
	public float min = Float.MAX_VALUE;
	public float max = -Float.MAX_VALUE;
	public float av = Float.NaN;
	public double integral = 0;
	public int timeSeriesNum = 0;
	public int timeSeriesNonEmptyNum = 0;
	public int dpNum = 0;
	public long inputTime = 0;
	public long nonGapTime = 0;
	public long nonGapTime2 = 0;
	/**Gw id that is source of maximum timeseries*/
	public String maxTSNumSourceId = null;
	/**for aggregated results*/
	public List<String> missingSources = null;
	/**for single-gateway results*/
	public String gwSourceId = null;
	public final DataType source;
	public EvalResult(DataType source) {
		this.source = source;
	}
	
	public static void printResult(EvalResult result, PrintStream out) {
		out.println(result.source.toString()+
				" : Found "+result.timeSeriesNum+"("+result.timeSeriesNonEmptyNum+")"+" time series, "+
				((result.maxTSNumSourceId!=null)?"SourceGW:"+result.maxTSNumSourceId+", ":"")+
				result.dpNum+" dataPoints, av:"+result.av+" min:"+result.min+" max:"+result.max+
				" Quality:"+(double)result.nonGapTime/(double)result.inputTime+" of "+StringFormatHelper.getFormattedValue(result.inputTime)+"; Quality2:"+(double)result.nonGapTime2/(double)result.inputTime);
	}
	public static void printCSV(EvalResult result, PrintStream out, String typeSuffix) {
    	out.println(result.source.toString()+typeSuffix+", "+result.timeSeriesNum+", "+
        		result.timeSeriesNonEmptyNum+", "+((result.maxTSNumSourceId!=null)?result.maxTSNumSourceId:"")+", "+
        		result.dpNum+", "+result.av+", "+
        		result.min+", "+result.max+", "+result.inputTime+", "+result.nonGapTime+", "+result.nonGapTime2
        );
	}
}
