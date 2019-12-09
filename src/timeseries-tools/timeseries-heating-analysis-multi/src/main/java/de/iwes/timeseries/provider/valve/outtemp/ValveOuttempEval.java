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
package de.iwes.timeseries.provider.valve.outtemp;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.generictype.GenericAttribute;
import org.ogema.generictype.GenericAttributeImpl;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI.Level;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProviderPreEval;
import de.iwes.timeseries.provider.genericcollection.OutsideTempGenericEvalProvider;
import de.iwes.timeseries.provider.genericcollection.OutsideTempGenericMultiResult;
import de.iwes.timeseries.provider.genericcollection.WinHeatGenericEvalProvider;

/**
 * Evaluates number of rooms heated versus outside temperature
 * TODO: This provider is intended to just use per-Day values of pre-evaluation, so does not need to call
 * processValue. Does this make sense?
 */
@Service(EvaluationProvider.class)
@Component
public class ValveOuttempEval extends GenericGaRoSingleEvalProviderPreEval {
	
    public final static String ID = "roomheated_valve_outtemp";
    public final static String LABEL = "Rooms heated versus outside temperature";
    public final static String DESCRIPTION = "Estimates number of rooms heated versus outside temperature";
    
    public ValveOuttempEval() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	    		GaRoDataType.ValvePosition};
	}
	public static final int VALVE_IDX = 0;
        
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList();
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}
	
    public final static GenericGaRoResultType OUTSIDE_TEMP_AVERAGE_OVERALL = new GenericGaRoResultType("Average_Outside_Temperature_Overall",
    		"Average outside temperature over entire evaluation period", TemperatureResource.class, Level.OVERALL, ID)  {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return null;
		}
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}
    };

	//TODO: Versions here must declare OVERALL as Level !
	private static final List<GenericGaRoResultType> OVERALL_RESULTS = Arrays.asList(
	    		OUTSIDE_TEMP_AVERAGE_OVERALL);
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRoOverall() {
		return OVERALL_RESULTS;
	}

	
	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return null;
	}
	
	//We expect that we do not need special result format here

	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return Arrays.asList(new PreEvaluationRequested(OutsideTempGenericEvalProvider.ID),
				new PreEvaluationRequested(WinHeatGenericEvalProvider.ID));
	}
	
	@Override
	public boolean executeSuperLevelOnly() {
		return true;
	}
	@Override
	protected void performSuperEval(AbstractSuperMultiResult<?> destination,
			List<AbstractSuperMultiResult<?>> preEvalSources) {
		//Implement evaluation here
		GaRoSuperEvalResult<?> outTemp = (GaRoSuperEvalResult<?>) preEvalSources.get(0);
		GaRoSuperEvalResult<?> valve = (GaRoSuperEvalResult<?>) preEvalSources.get(1);
		GaRoSuperEvalResult<?> result = (GaRoSuperEvalResult<?>)destination;
		result.evalResults = new HashMap<String, String>();
		int outIdx = 0;
		//EfficientTimeSeriesArray outTSEff = outTemp.timeSeriesResults.get(OutsideTempGenericEvalProvider.OUTSIDE_TEMP_TIMESERIES_OVERALL.id());
		//ReadOnlyTimeSeries outTS = outTSEff.toFloatTimeSeries();
		float[] resClasses = new float[3];
		int[] countClasses = new int[3];
		for(GaRoMultiResult valveSingle: valve.intervalResults) {
			//The following can be used if both inputs have to be obtained from the intervals
			while((outIdx < outTemp.intervalResults.size()) &&
					(outTemp.intervalResults.get(outIdx).startTime < valveSingle.startTime)) outIdx++;
			if(outIdx >= outTemp.intervalResults.size()) {
				break;
			}
			GaRoMultiResult outSingleIn = outTemp.intervalResults.get(outIdx);
			if(outSingleIn.startTime != valveSingle.startTime)
				continue;
			OutsideTempGenericMultiResult outSingle = (OutsideTempGenericMultiResult)outSingleIn;
			float out = outSingle.generalOutsideTemperatureAv;
			if(Float.isNaN(out))
				continue;
			
			//This is an attempt to get the value directly from an overall timeseries. This
			//does not work, though, as the time series is not aligned with days
			//SampledValue outSv = outTS.getValue(valveSingle.startTime);
			//if(outSv == null) continue;
			//float out = outSv.getValue().getFloatValue();
			float sum = 0;
			int count = 0;
			for(RoomData room: valveSingle.roomEvals) {
				if(room.evalResults == null) continue;
				String val = room.evalResults.get(WinHeatGenericEvalProvider.VALVE_HOURS_TOTAL_NEW.id());
				if(val == null) continue;
				float fval = Float.parseFloat(val);
				if(Float.isNaN(fval)) continue;
				sum += fval;
				count++;
			}
			if(count == 0)
				continue;
			float valveAv = sum/count;
			if(out < (10+273.15)) {
				resClasses[0] += valveAv;
				(countClasses[0])++;
			} else if(out < (16+273.15)) {
				resClasses[1] += valveAv;
				(countClasses[1])++;
			} else {
				resClasses[2] += valveAv;
				(countClasses[2])++;
			}
		}
		putResult("Under10Av", resClasses[0]/countClasses[0], result);
		putResult("Under10Count", countClasses[0], result);
		System.out.println("Under10Av:"+resClasses[0]/countClasses[0]+"  Count:"+countClasses[0]);
		putResult("Under16Av", resClasses[1]/countClasses[0], result);
		putResult("Under16Count", countClasses[1], result);
		System.out.println("Under16Av:"+resClasses[1]/countClasses[1]+"  Count:"+countClasses[1]);
		putResult("SummerAv", resClasses[2]/countClasses[0], result);
		putResult("SummerCount", countClasses[2], result);
		System.out.println("SummerAv:"+resClasses[2]/countClasses[2]+"  Count:"+countClasses[2]);
	}
	
	void putResult(String id, float value, GaRoSuperEvalResult<?> result) {
		result.evalResults.put(id, String.format("%f", value));
	}
}
