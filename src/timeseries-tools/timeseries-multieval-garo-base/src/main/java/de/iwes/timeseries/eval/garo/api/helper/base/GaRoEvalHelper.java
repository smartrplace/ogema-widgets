package de.iwes.timeseries.eval.garo.api.helper.base;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.TemperatureResource;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.DataProviderResInfoGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.api.helper.EvalHelperExtended;
import de.iwes.timeseries.eval.base.provider.utils.RequiredInputDefault;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInput;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting.PreEvaluationRequested;
import de.iwes.timeseries.eval.garo.resource.GaRoMultiEvalDataProviderResource;
import de.iwes.timeseries.eval.garo.resource.GenericGaRoMultiProviderResource;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class GaRoEvalHelper {
	public static GaRoDataType getDataType(String recId) {
		if(recId.contains("chargeSensor")) return GaRoDataType.ChargeSensor;
		if(recId.contains("HUMIDITY")) return GaRoDataType.HumidityMeasurement;
		if(recId.contains("SHUTTER_CONTACT")) return GaRoDataType.WindowOpen;
		if(recId.contains("MOTION_DETECTOR")) return GaRoDataType.MotionDetection;
		//type TemperatureSetpoint is not recognized anymore
		if(recId.contains("temperatureSensor/deviceFeedback/setpoint")) return GaRoDataType.TemperatureSetpointFeedback;
		if(recId.contains("temperatureSensor/settings/setpoint")) return GaRoDataType.TemperatureSetpointSet;
		if(recId.contains("temperatureSensor/reading")) return GaRoDataType.TemperatureMeasurementThermostat;
		if(recId.contains("TEMPERATURE/reading")) return GaRoDataType.TemperatureMeasurementRoomSensor;
		if(recId.contains("valve/setting/stateFeedback")) return GaRoDataType.ValvePosition;
		if(recId.contains("electricityConnectionBox/connection/powerSensor/reading")) return GaRoDataType.PowerMeter;
		if(recId.contains("semaLevel")) return GaRoDataType.CompetitionLevel;
		if(recId.contains("competitionPosition")) return GaRoDataType.CompetitionPosition;
		if(recId.contains("Points")) return GaRoDataType.CompetitionPoints;
		return GaRoDataType.Unknown;
	}
	
	public static RequiredInputData getRequiredInput(GaRoDataType dataType) {
		switch(dataType) {
		case TemperatureMeasurementRoomSensor:
			return new RequiredInputDefault("Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
				+ "evaluated. This are temperature measurements of room sensors.",
				TemperatureResource.class);
		case TemperatureMeasurementThermostat: // FIXME duplicate ids not admissible!
			return new RequiredInputDefault("Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
				+ "evaluated. This are temperature measurements of thermostats.",
				TemperatureResource.class);
		case TemperatureSetpointFeedback:
			return new RequiredInputDefault("Temperature setpoints in the room", "Provide all temperature setpoints that shall be"
				+ "evaluated.",
				TemperatureResource.class);
		case WindowOpen:
			return new RequiredInputDefault(
		    		"Window opening sensors of the room", "Provide time series for all window opening sensors of the room",
		    		BooleanResource.class);
		case ValvePosition:
			return new RequiredInputDefault(
		    		"Valve positions of the room", "Provide time series for all valves of the room",
		    		FloatResource.class);
		case MotionDetection:
			return new RequiredInputDefault(
		    		"Motion detection sensor in the room", "Provide time series for a motion detector of the room",
		    		FloatResource.class);
		case PowerMeter:
			return new RequiredInputDefault(
		    		"PowerMeter of gateway", "Provide time series for a power meter of the gateway",
		    		FloatResource.class);
		default:
			throw new IllegalStateException(dataType.name()+" not implemented yet as Required Input");
		}
	}
	
	public static List<RequiredInputData> getInputDataTypes(GaRoDataType[] dataTypes) {
		List<RequiredInputData> result = new ArrayList<>();
		for(GaRoDataType dt: dataTypes) {
			result.add(getRequiredInput(dt));
		}
		return result;
	}

    public static void printAllResults(final String room, final Map<ResultType, EvaluationResult> results, long[] startEnd) {
   // public static void printResults(final String room, final Map<ResultType, EvaluationResult> results, long start, long end) {
    	System.out.println("*********************************");
    	System.out.println("Room base evaluation results: " + room);
    	//if (startEnd!= null)
    		System.out.println("Duration: " + new Date(startEnd[0]) + " - " + new Date(startEnd[1])); // FIXME
    	for (Entry<ResultType, EvaluationResult> re: results.entrySet()) {
			final SingleEvaluationResult r = re.getValue().getResults().iterator().next();
			if (r instanceof SingleValueResult<?>) {
				final Object value = ((SingleValueResult<?>) r).getValue();
				System.out.println(" " + re.getKey().description(OgemaLocale.ENGLISH) + ": " + value);
			}
    	}
    	System.out.println("*********************************");
    }
    
   
    public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult<Resource>> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
				ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval) {
    	return performGenericMultiEvalOverAllData(singleEvalProvider, appMan, startTime, endTime,
    			resultStepSize, doExportCSV, doBasicEval, null);
    }
    @SuppressWarnings("rawtypes")
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult<Resource>> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			ApplicationManager appMan, long startTime,
			long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
			GaRoPreEvaluationProvider[] preEvalProviders) {
		return performGenericMultiEvalOverAllData(singleEvalProvider, appMan,
				startTime, endTime, resultStepSize, doExportCSV, doBasicEval,
				preEvalProviders, null);
	}
    @SuppressWarnings("rawtypes")
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult<Resource>> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
				GaRoPreEvaluationProvider[] preEvalProviders, List<ResultType> resultsRequested) {
    	return performGenericMultiEvalOverAllData(singleEvalProvider, appMan, startTime, endTime, resultStepSize, doExportCSV, doBasicEval, preEvalProviders, resultsRequested,
    			null, null);
    }

    /** Perform multi-evaluation on resources for a GaRoSingleEvalProvider class
     * 
     * @param singleEvalProvider evaluation provider class to use
     * @param appMan object providing source data
     * @param startTime
     * @param endTime
     * @param resultStepSize
     * @param doExportCSV if true the result will be exported as zipped csv file
     * @param doBasicEval if true the basic evaluation provider will be executed for quality checks (not recommended anymore)
     * @param preEvalProviders if singleEvalProvider requests pre evaluation data provide the respective providers here
     * @param resultsRequested if null all results offered by the provider will be calculated
     * @param roomIds location Strings of rooms to be evaluated. If null no filtering of input will be applied.
     * @param result file name, should usually end on ".json"
     * @return
     */ 
    @SuppressWarnings("rawtypes")
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult<Resource>> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
				GaRoPreEvaluationProvider[] preEvalProviders, List<ResultType> resultsRequested, List<String> roomIds,
				String resultFileName) {
		try {
			P singleProvider = singleEvalProvider.newInstance();
			if(singleProvider instanceof GaRoSingleEvalProviderPreEvalRequesting) {
				GaRoSingleEvalProviderPreEvalRequesting preEval = (GaRoSingleEvalProviderPreEvalRequesting)singleProvider;
				int i= 0;
				for(PreEvaluationRequested req: preEval.preEvaluationsRequested()) {
					preEval.preEvaluationProviderAvailable(i, req.getSourceProvider(), preEvalProviders[i]);
					i++;
				}
			}
			GaRoTestStarter<GaRoMultiResult<Resource>> result = new GaRoTestStarter<GaRoMultiResult<Resource>>((
					new GenericGaRoMultiProviderResource<P>(singleProvider, doBasicEval)),
				appMan, startTime, endTime, resultStepSize,
				(resultFileName!=null)?resultFileName:singleEvalProvider.getSimpleName()+"Result.json",
				doExportCSV, resultsRequested, roomIds);
			Executors.newSingleThreadExecutor().submit(result);
			return result;
		} catch(InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
    }
	
	
	
    public static <T extends GaRoMultiResult<Resource>> MultiEvaluationInstance<Resource, T> startGaRoMultiEvaluationOverAllData(
    		long startTime, long endTime, ChronoUnit resultStepSize,
    		GaRoEvalProvider<Resource, T> garoEval, ApplicationManager appMan,
    		List<ResultType> resultsRequested, List<String> roomIds, String resultFileName) {

        DataProviderResInfoGeneric<Resource, ?> dataProvider = new GaRoMultiEvalDataProviderResource(appMan);
        return startGaRoMultiEvaluation(startTime, endTime, resultStepSize, garoEval,
        		appMan, dataProvider, resultsRequested, roomIds, resultFileName);
    }    	
 
    public static <T extends GaRoMultiResult<Resource>> MultiEvaluationInstance<Resource, T> startGaRoMultiEvaluation(
    		long startTime, long endTime, ChronoUnit resultStepSize,
        	GaRoEvalProvider<Resource, T> garoEval, ApplicationManager appMan,
        	DataProviderResInfoGeneric<Resource, ?> dataProvider, List<ResultType> resultsRequested, List<String> roomIds,
        	String resultFileName) {

    	Collection<ConfigurationInstance> configurations = EvalHelperExtended.addStartEndTime(startTime, endTime, null);
        List<DataProviderType> providerTypes = garoEval.inputDataTypes();
        @SuppressWarnings("unchecked")
		MultiEvaluationInputGeneric<Resource>[] arr = new MultiEvaluationInputGeneric[garoEval.inputDataTypes().size()];
        int inputIdx = 0;
        int maxDependencyIdx =  garoEval.maxTreeIndex;
        for(GaRoDataType dt: garoEval.getInputTypesFromRoom()) {
        	MultiEvaluationInputGeneric<Resource> ts = new GaRoMultiEvaluationInput<Resource>(
            		providerTypes.get(maxDependencyIdx), dataProvider, dt, roomIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID);
         	arr[inputIdx] = ts;
         	inputIdx++;
        }
        List<MultiEvaluationInputGeneric<Resource>> input = Arrays.<MultiEvaluationInputGeneric<Resource>>asList(arr);
         
        //TODO
        if(garoEval.getInputTypesFromGw() != null)
        	throw new UnsupportedOperationException("GetInputTypesFromGw not fully implemented yet!");
        
		MultiEvaluationInstance<Resource, T> result = garoEval.newEvaluation(input,
        		configurations, resultStepSize, resultsRequested);
        result.execute();
        return result;
    }
    
    
	public static interface CSVArchiveExporter {
		void writeGatewayDataArchive(FileOutputStream file,
				Collection<GaRoTimeSeriesId> timeSeriesToExport,
	            long startTime, long endTime) throws IOException;
	}

    public static class GaRoTestStarter<T extends GaRoMultiResult<Resource>> implements Callable<Void> {
    	private final String FILE_PATH = System.getProperty("de.iwes.tools.timeseries-multieval.resultpath", "../evaluationresults");    	private final GaRoEvalProvider<Resource, T> garoEval;
    	private final ApplicationManager appMan;
    	private final long startTime;
    	private final long endTime;
    	private final ChronoUnit resultStepSize;
    	private final String jsonOutFileName;
        private final CSVArchiveExporter doExportCSV;
		private final List<ResultType> resultsRequested;
		private final List<String> roomIds;
		public GaRoTestStarter(GaRoEvalProvider<Resource, T> garoEval, ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize,
				String jsonOutFileName, CSVArchiveExporter doExportCSV) {
			this(garoEval, appMan, startTime, endTime, resultStepSize, jsonOutFileName, doExportCSV,
					null, null);
		}
		public GaRoTestStarter(GaRoEvalProvider<Resource, T> garoEval, ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize,
				String jsonOutFileName, CSVArchiveExporter doExportCSV,
				List<ResultType> resultsRequested, List<String> roomIds) {
			super();
			this.garoEval = garoEval;
			this.appMan = appMan;
			this.startTime = startTime;
			this.endTime = endTime;
			this.resultStepSize = resultStepSize;
			this.jsonOutFileName = jsonOutFileName;
			this.doExportCSV = doExportCSV;
			this.resultsRequested = resultsRequested;
			this.roomIds = roomIds;
		}
		
        @Override
        public Void call() throws Exception {
        	try {
            MultiEvaluationInstance<Resource, T> eval
            	//= MainPage.this.startGaRoEvaluation(startTime, endTime);
        		= startGaRoMultiEvaluationOverAllData(startTime, endTime, resultStepSize,
        				garoEval, appMan, resultsRequested, roomIds, jsonOutFileName);

            Set<GaRoTimeSeriesId> timeSeriesEvalAll = new HashSet<>();
            AbstractSuperMultiResult<Resource, T> result = eval.getResult();
            for(T res: result.intervalResults) {
            	timeSeriesEvalAll.addAll(res.timeSeriesEvaluated);
            }
            
            System.out.printf("evaluation runs done: %d\n", result.intervalResults.size());

            String fileName = FILE_PATH+"/"+jsonOutFileName;
            MultiEvaluationUtils.exportToJSONFile(fileName, result);
            if (doExportCSV != null) {
            	fileName = FILE_PATH+"/evaluation-output-test.zip";
            	doExportCSV.writeGatewayDataArchive(new FileOutputStream(fileName),
            			timeSeriesEvalAll, startTime, endTime);
                Path of = Paths.get(fileName);
                System.out.printf("export done, %dkb in %s%n", Files.size(of)/1024, of);
            } else {
                System.out.printf("evaluation done, evaluated %d time series%n", result.intervalResults.size(), timeSeriesEvalAll.size());                	
            }
            return null;
        	} catch(Exception e) {
        		e.printStackTrace();
        		throw e;
        	}
        }
    }

}
