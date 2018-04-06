package de.iwes.timeseries.eval.garo.multibase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.helper.EvalHelperExtended;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInstance;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;

public abstract class GenericGaRoMultiEvaluation<R, P extends GaRoSingleEvalProvider> extends GaRoMultiEvaluationInstance<R, GaRoMultiResult<R>> {
	public final boolean doBasicEval;

	private final BasicEvaluationProvider basicEval = new BasicEvaluationProvider();
	//private final GapEvaluationProvider gapEval = new GapEvaluationProvider();
	protected final P roomEval;
	private final Class<? extends GaRoMultiResultExtended<R>> resultTypeExtended;
	private final List<ResultType> resultsRequested;
	private GaRoMultiResultExtended<R> resultExtended = null;

	public GenericGaRoMultiEvaluation(List<MultiEvaluationInputGeneric<R>> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<R, GaRoMultiResult<R>> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw);
		this.roomEval = singleProvider;
		this.doBasicEval = doBasicEval;
		if(resultsRequested == null)
			this.resultsRequested = roomEval.resultTypes();
		else
			this.resultsRequested = resultsRequested;
		resultTypeExtended = singleProvider.extendedResultDefinition();
	}

	@Override
	public GaRoMultiResult<?> initNewResultGaRo(long start, long end, Collection<ConfigurationInstance> configurations) {
		if(resultTypeExtended != null) {
			try {
				Constructor<? extends GaRoMultiResultExtended<R>> cons =
						resultTypeExtended.getConstructor(List.class, long.class, long.class, Collection.class);
				resultExtended = cons.newInstance(input, start, end, configurations);
				return resultExtended;
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}			
		}
		GaRoMultiResult<R> result = new GaRoMultiResult<R>(input, start, end, configurations);
		
		//If you have overallResults, initialize in overwritten method here
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<GaRoMultiResultUntyped> getResultType() {
		return GaRoMultiResultUntyped.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<GaRoSuperEvalResult> getSuperResultType() {
		return GaRoSuperEvalResult.class;
	}

	@Override
	protected abstract List<GaRoSelectionItem<R>> startRoomLevel(List<GaRoSelectionItem<R>> levelItems, GaRoMultiResult<R> result, String gw);

	R currentRoom;
	@Override
	protected void startTSLevel(List<GaRoSelectionItem<R>> levelItems, GaRoMultiResult<R> result, R room) {
		currentRoom = room;
	}

	@Override
	protected void processInputType(int inputIdx, List<TimeSeriesData> tsList,
			 GaRoDataType dt, GaRoMultiResult<R> result) {
		try {
		for(TimeSeriesData data: tsList) {
			if(doBasicEval) {
				final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(new EvaluationInput[]{new EvaluationInputImpl(Arrays.<TimeSeriesData>asList(new TimeSeriesData[]{data}))});
				final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(basicEval, inputs, basicEval.resultTypes(), result.configurations);
					
				long inputTime = result.endTime - result.startTime;
				Long nonGapTime = EvalHelperExtended.getSingleResultLong(BasicEvaluationProvider.NON_GAPTIME, instance);
				if((nonGapTime == null) || (inputTime <= 0)) {
					//write to your result type if you want to store this information
					//result.overallResults().any.timeSeriesNum++;
					continue;					
				}
				double quality = ((double)(nonGapTime))/(double)(inputTime);
				if(quality < MINIMUM_QUALITY_REQUIRED) {
					//write to your result type if you want to store this information
					//result.overallResults().any.timeSeriesNum++;
					continue;
				}
			}
			GaRoTimeSeriesId tsEval = new GaRoTimeSeriesId();
			tsEval.gwId = result.gwId();		
			tsEval.timeSeriesId = data.id(); //tsid;
			result.timeSeriesEvaluated.add(tsEval);
		}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void performRoomEvaluation(List<TimeSeriesData>[] inputTimeSeries, GaRoMultiResult<R> result) {
		final List<EvaluationInput> inputs = new ArrayList<>();
		
		for(List<TimeSeriesData> tsdList: inputTimeSeries) {
			inputs.add(new EvaluationInputImpl(tsdList));
		}
		
		/*//You can build input data explicitly like this
		List<TimeSeriesData> tempSPData = inputTimeSeries[ComfortTemperatureEvaluation.TEMPSETP_IDX];
		final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(new EvaluationInput[]{new EvaluationInputImpl(tempSPData)});
		*/
		if(roomEval instanceof GaRoSingleEvalProviderPreEvalRequesting) {
			((GaRoSingleEvalProviderPreEvalRequesting) roomEval).
					provideCurrentValues(result.gwId(), getName(currentRoom));
		}
		final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(roomEval, inputs, resultsRequested, result.configurations);
		final Map<ResultType, EvaluationResult> results = instance.getResults();
		
		GaRoEvalHelper.printAllResults(result.roomData().id, results, EvaluationUtils.getStartAndEndTime(result.configurations, inputs, false));
		result.roomData().evalResults = EvalHelperExtended.getResults(instance);
		if(resultExtended != null) {
			result.roomData().initEvalResultObjects();
			for(Entry<ResultType, EvaluationResult> re: instance.getResults().entrySet()) {
				List<SingleEvaluationResult> rlist = re.getValue().getResults();
				if(!rlist.isEmpty()) result.roomData().evalResultObjects().put(re.getKey(), rlist.get(0));
			}
			resultExtended.finishRoom(resultExtended, currentRoom);
		}
	}
	
	@Override
	protected void finishRoomLevel(GaRoMultiResult<R> result) {
		if(resultExtended != null) {
			resultExtended.finishGateway(resultExtended, result.gwId());
		}
	}

	@Override
	protected void finishGwLevel(GaRoMultiResult<R> result) {
		if(resultExtended != null) {
			resultExtended.finishTimeStep(resultExtended);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Status finish() {
		if(resultTypeExtended != null) {
			resultExtended.finishTotal((GaRoSuperEvalResult) superResult);
		}
		return super.finish();
	}
}
