package de.iee.timeseries.provider.heatcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeParam;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProviderPreEval;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregatorMultiSingleTS;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregatorMultiSingleTS.ValueDurationMulti;

/**
 * Evaluate thermostat usage and heat control system quality
 */
@Service(EvaluationProvider.class)
@Component
public class ThermostatEvalProvider extends GenericGaRoSingleEvalProviderPreEval {
	
	public static final long MINUTE_MILLIS = 60000;
	public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
	public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
		
	/** Adapt these values to your provider*/
    public final static String ID = "basic-thermostat_eval_provider";
    public final static String LABEL = "Basic Thermostat: Critical event evaluation";
    public final static String DESCRIPTION = "Basic Thermostat: Provides critical event evaluation, additional information in log file";
    
    protected static final Logger logger = LoggerFactory.getLogger(ThermostatEvalProvider.class);
    
    public ThermostatEvalProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	/** Provide your data types here*/
	/** It is recommended to define the indices of your input here.*/
	public static final int SETP_IDX = 0;
	public static final int FB_IDX = 1;
    public static final GaRoDataTypeParam setPointType = new GaRoDataTypeParam(GaRoDataType.TemperatureSetpointSet, false);
    public static final GaRoDataTypeParam feedbackType = new GaRoDataTypeParam(GaRoDataType.TemperatureSetpointFeedback, false);
	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {setPointType,
				feedbackType};
	}
    public static GaRoDataTypeParam getParamType(int idxOfReqInput) {
    	switch(idxOfReqInput) {
    	case SETP_IDX: return setPointType;
    	case FB_IDX: return feedbackType;
    	default: throw new IllegalStateException("unsupported IDX:"+idxOfReqInput);
    	}
    }
	
    @Override
    public int[] getRoomTypes() {
    	return new int[] {-1};
    }

    public class EvalCore extends GenericGaRoEvaluationCore {
 		public class CurrentSensorData {
 			Float setpoint;
 			Float lastSetpoint;
 			Float feedback;
 			Float lastFeedback;
 			int onThermostatEventNum = 0;
 			int onGatewayEventNum = 0;
 			long[] maxReactionTime = new long[] {-1, -1};
 			int[] wrongReactionNum = new int[] {0,0};
 			//Note that agg.values[0] should be equal to setpoint, agg.values[1] equal to feedback
 			public InputSeriesAggregatorMultiSingleTS agg = new
 					InputSeriesAggregatorMultiSingleTS(2, endTime);
 			
 			//internal if >=0 the requiredInputIdx of initial change is given here
 			int isInEvent = -1;
 			long eventStartTime;

 		}
    	final int[] idxSumOfPrevious;
    	final int size;
 		
 		final long totalTime;
       	final long startTime;
       	final long endTime;
   	
    	/** Application specific state variables, see also documentation of the util classes used*/

    	long durationTime = 0;
    	Map<String, CurrentSensorData> sensorData = new HashMap<>();
		
    	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
    		this.idxSumOfPrevious = idxSumOfPrevious;
    		this.size = size;
    		
    		totalTime = startEnd[1] - startEnd[0];
    		startTime = startEnd[0];
    		endTime = startEnd[1];
       	}
      	
    	/** In processValue the core data processing takes place. This method is called for each input
    	 * value of any input time series.*/
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		durationTime += duration;
    		
    		GaRoDataTypeParam type = getParamType(idxOfRequestedInput);
			//String ts = type.inputInfo.get(idxOfEvaluationInput).id();
    		String devId = type.deviceInfo.get(idxOfEvaluationInput).getDeviceResourceLocation(); //.PrimaryTSQualityEvalProviderBase.getDeviceShortId(
    		//		PrimaryTSQualityEvalProviderBase.getDevicePath(ts));
    		CurrentSensorData sensData = sensorData.get(devId);
    		if(sensData == null) {
    			sensData = new CurrentSensorData();
    			sensorData.put(devId, sensData);
    		}
    		final boolean changed;
    		switch(idxOfRequestedInput) {
    		case SETP_IDX:
    			sensData.lastSetpoint = sensData.setpoint;
    			sensData.setpoint = sv.getValue().getFloatValue();
    			changed = (sensData.lastSetpoint != null) && (!sensData.lastSetpoint.equals(sensData.setpoint));
    			break;
    		case FB_IDX:
    			sensData.lastFeedback = sensData.feedback;
    			sensData.feedback = sv.getValue().getFloatValue();
    			changed = (sensData.lastFeedback != null) && (!sensData.lastFeedback.equals(sensData.feedback));
    			break;
    		default:
    			throw new IllegalStateException("Unknown input idx:"+idxOfRequestedInput);
    		}
    		//TODO: There is a problem with the timing
    		ValueDurationMulti vdm = sensData.agg.getCurrentValueDurationMulti(idxOfRequestedInput,
    				sv, dataPoint, true, totalInputIdx);
    		if(sensData.lastSetpoint != null && sensData.lastFeedback != null) {
    			if(changed) {
    				if(sensData.isInEvent >= 0) {
    					if(sensData.feedback.equals(sensData.setpoint)) {
    						long eventDuration = timeStamp - sensData.eventStartTime;
    						if(sensData.maxReactionTime[sensData.isInEvent] < eventDuration)
    							sensData.maxReactionTime[sensData.isInEvent] = eventDuration;
    						sensData.isInEvent = -1;
    					} else {
    						(sensData.wrongReactionNum[sensData.isInEvent])++;
    					}
    				} else {
    					sensData.isInEvent = idxOfRequestedInput;
    					sensData.eventStartTime = timeStamp;
	    				if(idxOfRequestedInput == SETP_IDX)
	    					sensData.onGatewayEventNum++;
	    				else sensData.onThermostatEventNum++;
    				}
    			}
    		}
    		//System.out.println("Timestamp:"+timeStamp+" F:"+TimeUtils.getDateAndTimeString(timeStamp)+" DurT:"+durationTime+" Dif:"+(timeStamp-startTime)+ "gap:"+gapTime);
    	}
    	
        private FinalResult result = null;
        public FinalResult getFinalResult() {
			if(result != null) return result;
        	result = new FinalResult();
			
        	for(int idx = 0; idx < size; idx++) {
				//String ts = getTimeSeriesId(idx, PrimaryHumidityTempEvalProvider.this);
	    		//String devId = PrimaryTSQualityEvalProviderBase.getDeviceShortId(
	    		//		PrimaryTSQualityEvalProviderBase.getDevicePath(ts));
        		int idxOfEvaluationInput = getEvaluationInputIdx(idx);
        		GaRoDataTypeParam type = getTimeSeriesType(idx, ThermostatEvalProvider.this);
				String devId = type.deviceInfo.get(idxOfEvaluationInput).getDeviceResourceLocation();
	    		CurrentSensorData sensData = sensorData.get(devId);
	    		if(sensData == null) continue;
        		result.onGatewayEventNum += sensData.onGatewayEventNum;
           		result.onThermostatEventNum += sensData.onThermostatEventNum;
           		for(int i=0; i<2; i++) {
           			if(result.maxReactionTime[i]  < sensData.maxReactionTime[i]) {
           				result.maxReactionTime[i]  = sensData.maxReactionTime[i];
    					String devName = type.deviceInfo.get(idxOfEvaluationInput).getDeviceName();
    					result.critSensor = devName!=null?devName:devId;
           			}
           			result.wrongReactionNum[i] += sensData.wrongReactionNum[i];
           		}
        	}
			return result;
        }
 	}
    
    public static class FinalResult {
			int onThermostatEventNum = 0;
			int onGatewayEventNum = 0;
			long[] maxReactionTime = new long[] {-1, -1};
			int[] wrongReactionNum = new int[] {0,0};   	
			String critSensor;
    }
 	
 	/**
 	* Define the results of the evaluation here including the final calculation
 	*/
    public final static GenericGaRoResultType THERM_EVT_NUM = new GenericGaRoResultType("THERM_EVT_NUM",
    		"Number of thermostat manual events", IntegerResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<Integer>(rt, res.onThermostatEventNum, inputData);
		}
    };
    public final static GenericGaRoResultType GW_EVT_NUM = new GenericGaRoResultType("GW_EVT_NUM",
    		"Number of setpoint changes initiated from gateway", IntegerResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<Integer>(rt, res.onGatewayEventNum, inputData);
		}
    };
    public final static GenericGaRoResultType THERM_WRONG_NUM = new GenericGaRoResultType("THERM_WRONG_NUM",
    		"Number of wrong reactions of setpoint to manual change on thermostat", IntegerResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<Integer>(rt, res.wrongReactionNum[FB_IDX], inputData);
		}
    };
    public final static GenericGaRoResultType GW_WRONG_NUM = new GenericGaRoResultType("GW_WRONG_NUM",
    		"Number of wrong reactions of feedback to setpoint change request from gateway", IntegerResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<Integer>(rt, res.wrongReactionNum[SETP_IDX], inputData);
		}
    };
    public final static GenericGaRoResultType THERM_DUR_MAX = new GenericGaRoResultType("THERM_DUR_MAX",
    		"Maximum duration after manuel event on thermostat for setpoint to follow in minutes", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<Float>(rt, res.maxReactionTime[FB_IDX]*(1.0f/60000f), inputData);
		}
    };
    public final static GenericGaRoResultType GW_DUR_MAX = new GenericGaRoResultType("GW_DUR_MAX",
    		"Maximum duration after gw request for feedback to follow in minutes", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<Float>(rt, res.maxReactionTime[SETP_IDX]*(1.0f/60000f), inputData);
		}
    };

    public final static GenericGaRoResultType MOST_CRITICAL_SENSOR = new GenericGaRoResultType("$MOST_CRITICAL",
    		"ID of most crititcal sensor", StringResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			FinalResult res = cec.getFinalResult();
			return new SingleValueResultImpl<String>(rt, (res.critSensor!=null)?res.critSensor:"--", inputData);
			//return new SingleValueResultImpl<String>(rt, (cec.critSensor!=null)?cec.critSensor:"--", inputData);
		}
    };

    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(THERM_EVT_NUM,
    		GW_EVT_NUM, THERM_WRONG_NUM, GW_WRONG_NUM,
    		THERM_DUR_MAX, GW_DUR_MAX,
    		MOST_CRITICAL_SENSOR); //EVAL_RESULT
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}

	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return new EvalCore(input, requestedResults, configurations, listener, time, size, nrInput, idxSumOfPrevious, startEnd);
	}

	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return null;
	}
	
	public final static String[] allResults = new String[]{"THERM_EVT_NUM",
    		"GW_EVT_NUM", "THERM_WRONG_NUM", "GW_WRONG_NUM", "THERM_DUR_MAX", "GW_DUR_MAX",
    		"$MOST_CRITICAL", "timeOfCalculation"};

	@Override
	public List<KPIPageDefinition> getPageDefinitionsOffered() {
		List<KPIPageDefinition> result = new ArrayList<>();
		
		//Humidity page
		KPIPageDefinition def = new KPIPageDefinition();
		def.resultIds.add(allResults);
		def.providerId = Arrays.asList(new String[] {ID});
		def.configName = "Thermostat Reaction Report";
		def.urlAlias = "alltherm";
		def.specialIntervalsPerColumn.put("timeOfCalculation", 1);
		result.add(def);
		return result;
	}
}
