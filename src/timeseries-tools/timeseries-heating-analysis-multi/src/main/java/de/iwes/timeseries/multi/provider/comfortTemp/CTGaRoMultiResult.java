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
package de.iwes.timeseries.multi.provider.comfortTemp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ogema.tools.resource.util.TimeUtils;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.helper.base.SpecialGaRoEvalResult;

@Deprecated
public class CTGaRoMultiResult extends GaRoMultiResult {
	public static final int MINIMUM_DP_REQUIRED = 10;

	//input states
	//per gateway
	int dpNumGw;
	Map<GaRoDataType, SpecialGaRoEvalResult> resultsGw;

	public static class WinMultiOverallResults extends GaRoStdOverallResults {
		//internal data
		Map<Integer, Map<GaRoDataType, SpecialGaRoEvalResult>> resultsPerRoomType; // = new HashMap<>();
		SpecialGaRoEvalResult any = new SpecialGaRoEvalResult(GaRoDataType.Any);
	}
	public WinMultiOverallResults overallResults() {
		return (WinMultiOverallResults) overallResults;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CTGaRoMultiResult(List<MultiEvaluationInputGeneric> inputData, long start, long end, Collection<ConfigurationInstance> configurations) {
		super((List)inputData, start, end, configurations);
		overallResults = new WinMultiOverallResults();
	}

	@Override
	public String getSummary() {
		return "GaRoWinResult: start:"+TimeUtils.getDateAndTimeString(startTime)+
				" end:"+TimeUtils.getDateAndTimeString(endTime) + 
	        	" Found total " + overallResults.gwCountWithData+ " of "+ overallResults.gwCount + " gateways with data of quality >=" + CTGaRoEvaluation.MINIMUM_QUALITY_REQUIRED;
	}
}
