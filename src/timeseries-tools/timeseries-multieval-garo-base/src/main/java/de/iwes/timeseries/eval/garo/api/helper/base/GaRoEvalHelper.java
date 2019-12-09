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
package de.iwes.timeseries.eval.garo.api.helper.base;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.api.extended.util.TimeSeriesDataExtendedImpl;
import de.iwes.timeseries.eval.base.provider.utils.RequiredInputDefault;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInput;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;
import de.iwes.timeseries.eval.garo.util.MultiEvaluationUtilsGaRo;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class GaRoEvalHelper {
	public static GaRoDataType getDataType(String recId) {
		if(recId.contains("chargeSensor")) return GaRoDataType.ChargeSensor;
		if(recId.contains("internalVoltage")) return GaRoDataType.ChargeVoltage;
		if(recId.contains("HUMIDITY")) return GaRoDataType.HumidityMeasurement;
		if(recId.contains("SHUTTER_CONTACT")) return GaRoDataType.WindowOpen;
		if(recId.contains("MOTION_DETECTOR")) return GaRoDataType.MotionDetection;
		//type TemperatureSetpoint is not recognized anymore
		if(recId.contains("temperatureSensor/deviceFeedback/setpoint")) return GaRoDataType.TemperatureSetpointFeedback;
		if(recId.contains("temperatureSensor/settings/setpoint")) return GaRoDataType.TemperatureSetpointSet;
		if(recId.contains("temperatureSensor/reading")) return GaRoDataType.TemperatureMeasurementThermostat;
		if(recId.contains("TEMPERATURE/reading")) return GaRoDataType.TemperatureMeasurementRoomSensor;
		if(recId.contains("valve/setting/stateFeedback")) return GaRoDataType.ValvePosition;
		if(recId.contains("connection/powerSensor/reading")) return GaRoDataType.PowerMeter;
		if(recId.contains("connection/energySensor/reading")) return GaRoDataType.PowerMeterEnergy;
		if(recId.contains("connection/currentSensor/reading")) return GaRoDataType.PowerMeterCurrent;
		if(recId.contains("connection/voltageSensor/reading")) return GaRoDataType.PowerMeterVoltage;
		if(recId.contains("connection/frequencySensor/reading")) return GaRoDataType.PowerMeterFrequency;
		if(recId.contains("connection/subPhaseConnections")
				&&recId.contains("powerSensor/reading")) return GaRoDataType.PowerMeterSubphase;
		if(recId.contains("connection/subPhaseConnections")
				&&recId.contains("energySensor/reading")) return GaRoDataType.PowerMeterEnergySubphase;
		if(recId.contains("connection/subPhaseConnections")
				&&recId.contains("currentSensor/reading")) return GaRoDataType.PowerMeterCurrentSubphase;
		if(recId.contains("connection/subPhaseConnections")
				&&recId.contains("voltageSensor/reading")) return GaRoDataType.PowerMeterVoltageSubphase;
		if(recId.contains("connection/subPhaseConnections")
				&&recId.contains("reactiveAngleSensor/reading")) return GaRoDataType.PowerMeterReactiveAngleSubphase;
		if(recId.contains("RexometerSerial/configs/gas_energy/value")) return GaRoDataType.GasMeter;
		if(recId.contains("RexometerSerial/configs/gas_batteryVoltage/value")) return GaRoDataType.GasMeterBatteryVoltage;
		if(recId.contains("semaLevel")) return GaRoDataType.CompetitionLevel;
		if(recId.contains("competitionPosition")) return GaRoDataType.CompetitionPosition;
		if(recId.contains("Points")) return GaRoDataType.CompetitionPoints;
		
		if(recId.contains("/powerSensor/reading")) return GaRoDataType.PowerMeterOutlet;
		if(recId.contains("/currentSensor/reading")) return GaRoDataType.CurrentSensorOutlet;
		if(recId.contains("/voltageSensor/reading")) return GaRoDataType.VoltageSensorOutlet;
		if(recId.contains("/frequencySensor/reading")) return GaRoDataType.FrequencySensorOutlet;
		if(recId.contains("/energySensor/reading")) return GaRoDataType.EnergyIntegralOutlet;
		if(recId.contains("/stateFeedback")) return GaRoDataType.SwitchStateFeedback; //"onOffSwitch/stateFeedback"
		if(recId.contains("/POWER_")) return GaRoDataType.Heatpower;
		if(recId.contains("/ENERGY_")) return GaRoDataType.HeatEnergyIntegral;
		if(recId.contains("/VOLUME_FLOW_")) return GaRoDataType.HeatFlow;
		if(recId.contains("/VOLUME")) return GaRoDataType.HeatVolumeIntegral;
		if(recId.contains("/FLOW_TEMPERATURE_")) return GaRoDataType.HeatSupplyTemperatur;
		if(recId.contains("/RETURN_TEMPERATURE_")) return GaRoDataType.HeatReturnTemperatur;
		
		for(GaRoDataType type: GaRoDataType.standardTypes) {
    		if(type.label(null).equals(recId)) return type;
    	}
		
		return GaRoDataType.Unknown;
	}
	
	/*public static List<GaRoDataType> getGatewayTypes() {
		return Arrays.asList(new GaRoDataType[] {GaRoDataType.PowerMeter, GaRoDataType.CompetitionLevel,
				GaRoDataType.CompetitionPoints, GaRoDataType.CompetitionPosition,
				GaRoDataType.OutsideTemperatureGw});
	}
	public static List<GaRoDataType> getOverallTypes() {
		return Arrays.asList(new GaRoDataType[] {GaRoDataType.OutsideTemperatureOverall});
	}*/

	@SuppressWarnings("unchecked")
	public static RequiredInputData getRequiredInput(GaRoDataTypeI dt) {
		String description = dt.description(null);
		return new RequiredInputDefault(dt.label(null),
				(description!=null)?description:dt.label(null),
				(Class<? extends SingleValueResource>) dt.representingResourceType());
		//RequiredInputData result = getRequiredInputInternal(dataType,
		//		(Class<? extends SingleValueResource>) dataType.representingResourceType());
		//return result;
	}
	/*private static RequiredInputData getRequiredInputInternal(GaRoDataType dataType,
			Class<? extends SingleValueResource> type) {
		switch(dataType.label(null)) {
		case "TemperatureMeasurementRoomSensor":
			return new RequiredInputDefault("Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
				+ "evaluated. This are temperature measurements of room sensors.",
				type); //TemperatureResource.class);
		case "TemperatureMeasurementThermostat": // FIXME duplicate ids not admissible!
			return new RequiredInputDefault("Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
				+ "evaluated. This are temperature measurements of thermostats.",
				type); //TemperatureResource.class);
		case "TemperatureSetpointFeedback":
			return new RequiredInputDefault("Temperature setpoints in the room", "Provide all temperature setpoints that shall be"
				+ "evaluated.",
				type); //TemperatureResource.class);
		case "WindowOpen":
			return new RequiredInputDefault(
		    		"Window opening sensors of the room", "Provide time series for all window opening sensors of the room",
		    		type); //BooleanResource.class);
		case "ValvePosition":
			return new RequiredInputDefault(
		    		"Valve positions of the room", "Provide time series for all valves of the room",
		    		type); //FloatResource.class);
		case "MotionDetection":
			return new RequiredInputDefault(
		    		"Motion detection sensor in the room", "Provide time series for a motion detector of the room",
		    		type); //FloatResource.class);
		case "PowerMeter":
			return new RequiredInputDefault(
		    		"PowerMeter of gateway", "Provide time series for a power meter of the gateway",
		    		type); //FloatResource.class);
		case "PreEvaluated":
			return new RequiredInputDefault(
		    		"Pre-evaluated time series", "Provide some pre-evaluated time series, see Pre-evaluation defintion for details",
		    		type); //FloatResource.class);
		default:
			throw new IllegalStateException(dataType.label(null)+" not implemented yet as Required Input");
		}
	}*/
	
	public static List<RequiredInputData> getInputDataTypes(GaRoDataTypeI[] gaRoDataTypeIs) {
		List<RequiredInputData> result = new ArrayList<>();
		for(GaRoDataTypeI dt: gaRoDataTypeIs) {
			result.add(getRequiredInput(dt));
		}
		return result;
	}

    public static void printAllResults(final String room, final Map<ResultType, EvaluationResult> results, long[] startEnd) {
   // public static void printResults(final String room, final Map<ResultType, EvaluationResult> results, long start, long end) {
		int ll = Integer.getInteger("org.ogema.multieval.loglevel", 10);
		if(ll >= 10) System.out.println("*********************************");
		if(ll >= 10) System.out.println("Room base evaluation results: " + room);
    	//if (startEnd!= null)
		if(ll >= 10) System.out.println("Duration: " + new Date(startEnd[0]) + " - " + new Date(startEnd[1])); // FIXME
    	for (Entry<ResultType, EvaluationResult> re: results.entrySet()) {
			final SingleEvaluationResult r = re.getValue().getResults().iterator().next();
			if (r instanceof SingleValueResult<?>) {
				final Object value = ((SingleValueResult<?>) r).getValue();
				if(ll >= 10) System.out.println(" " + re.getKey().description(OgemaLocale.ENGLISH) + ": " + value);
			}
    	}
    	if(ll >= 10) System.out.println("*********************************");
    }
    
   
    /*public static <P extends GaRoSingleEvalProvider> GaRoTestStarterRes<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
				ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval) {
    	return performGenericMultiEvalOverAllData(singleEvalProvider, appMan, startTime, endTime,
    			resultStepSize, doExportCSV, doBasicEval, null);
    }
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarterRes<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			ApplicationManager appMan, long startTime,
			long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
			GaRoPreEvaluationProvider[] preEvalProviders) {
		return performGenericMultiEvalOverAllData(singleEvalProvider, appMan,
				startTime, endTime, resultStepSize, doExportCSV, doBasicEval,
				preEvalProviders, null);
	}
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarterRes<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
				GaRoPreEvaluationProvider[] preEvalProviders, List<ResultType> resultsRequested) {
    	return performGenericMultiEvalOverAllData(singleEvalProvider, appMan, startTime, endTime, resultStepSize, doExportCSV, doBasicEval, preEvalProviders, resultsRequested,
    			null, null);
    }
	*/
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
     * @param resultResource file name, should usually end on ".json"
     * @return
     */ 
	/*public static <P extends GaRoSingleEvalProvider> GaRoTestStarterRes<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
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
			GaRoTestStarterRes<GaRoMultiResult> result = new GaRoTestStarterRes<GaRoMultiResult>((
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
	
	
	
    public static <T extends GaRoMultiResult> MultiEvaluationInstance<T> startGaRoMultiEvaluationOverAllData(
    		long startTime, long endTime, ChronoUnit resultStepSize,
    		GaRoEvalProvider<T> garoEval, ApplicationManager appMan,
    		List<ResultType> resultsRequested, List<String> roomIds) {

        DataProvider<?> dataProvider = new GaRoMultiEvalDataProviderResource(appMan);
        return startGaRoMultiEvaluation(startTime, endTime, resultStepSize, garoEval,
        		appMan, dataProvider, resultsRequested, roomIds);
    }    	
 
    public static <T extends GaRoMultiResult> MultiEvaluationInstance<T> startGaRoMultiEvaluation(
    		long startTime, long endTime, ChronoUnit resultStepSize,
        	GaRoEvalProvider<T> garoEval, ApplicationManager appMan,
        	DataProvider<?> dataProvider, List<ResultType> resultsRequested, List<String> roomIds) {

    	Collection<ConfigurationInstance> configurations = EvalHelperExtended.addStartEndTime(startTime, endTime, null);
        List<DataProviderType> providerTypes = garoEval.inputDataTypes();
 		MultiEvaluationInputGeneric[] arr = new MultiEvaluationInputGeneric[garoEval.inputDataTypes().size()];
        int inputIdx = 0;
        int maxDependencyIdx =  garoEval.maxTreeIndex;
        for(GaRoDataTypeI dt: garoEval.getInputTypesFromRoom()) {
        	MultiEvaluationInputGeneric ts = new GaRoMultiEvaluationInput(
            		providerTypes.get(maxDependencyIdx), dataProvider, dt, roomIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID);
         	arr[inputIdx] = ts;
         	inputIdx++;
        }
        List<MultiEvaluationInputGeneric> input = Arrays.<MultiEvaluationInputGeneric>asList(arr);
         
        //TODO
        if(garoEval.getInputTypesFromGw() != null)
        	throw new UnsupportedOperationException("GetInputTypesFromGw not fully implemented yet!");
        
		MultiEvaluationInstance<T> result = garoEval.newEvaluation(input,
        		configurations, resultStepSize, resultsRequested);
        result.execute();
        return result;
    }
    
    
    public static class GaRoTestStarterRes<T extends GaRoMultiResult> implements Callable<Void> {
    	private final String FILE_PATH = System.getProperty("de.iwes.tools.timeseries-multieval.resultpath", "../evaluationresults");
    	private final GaRoEvalProvider<T> garoEval;
    	private final ApplicationManager appMan;
    	private final long startTime;
    	private final long endTime;
    	private final ChronoUnit resultStepSize;
    	private final String jsonOutFileName;
        private final CSVArchiveExporter doExportCSV;
		private final List<ResultType> resultsRequested;
		private final List<String> roomIds;
		public GaRoTestStarterRes(GaRoEvalProvider<T> garoEval, ApplicationManager appMan, long startTime,
				long endTime, ChronoUnit resultStepSize,
				String jsonOutFileName, CSVArchiveExporter doExportCSV) {
			this(garoEval, appMan, startTime, endTime, resultStepSize, jsonOutFileName, doExportCSV,
					null, null);
		}
		public GaRoTestStarterRes(GaRoEvalProvider<T> garoEval, ApplicationManager appMan, long startTime,
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
            MultiEvaluationInstance<T> eval
            	//= MainPage.this.startGaRoEvaluation(startTime, endTime);
        		= startGaRoMultiEvaluationOverAllData(startTime, endTime, resultStepSize,
        				garoEval, appMan, resultsRequested, roomIds);

            Set<GaRoTimeSeriesId> timeSeriesEvalAll = new HashSet<>();
            AbstractSuperMultiResult<T> result = eval.getResult();
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
    }*/

	public static interface CSVArchiveExporter {
		void writeGatewayDataArchive(FileOutputStream file,
				Collection<GaRoTimeSeriesId> timeSeriesToExport,
	            long startTime, long endTime) throws IOException;
	}

	private static class AggData {
		float sum = 0;
		int countRoom = 0;
	}
	private static class AggDataString {
		String elements = null;
	}

    /** Extract averages over all rooms and gateways for all results in the roomEvals struct
     * of a GaRoMultiResult. Note that each GaRoMultiResult represents a single evaluation interval,
     * so averaging over intervals is not relevant here anyways.
     * TODO: Support not only averaging in the future
     * @param singleTimeStepResult
     * @return
     */
	public static Map<String, Float> getKPIs(GaRoMultiResult singleTimeStepResult, String subGw) {
		Map<String, Float> result = new HashMap<>();
		Map<String, AggData> aggData = new HashMap<>();
		for(RoomData rd: singleTimeStepResult.roomEvals) {
			if((subGw != null)&&(!rd.gwId.equals(subGw))) continue;
			if(rd.evalResults != null) for(Entry<String, String> resData: rd.evalResults.entrySet()) {
				try {
					float val = Float.parseFloat(resData.getValue());
					if(Float.isNaN(val)) continue;
					AggData ad = aggData.get(resData.getKey());
					if(ad == null) {
						ad = new AggData();
						aggData.put(resData.getKey(), ad);
					}
					ad.sum += val;
					ad.countRoom++;
				} catch(NumberFormatException e) {
					//do nothing
				}
			}
		}
		for(Entry<String, AggData> ad: aggData.entrySet()) {
			final float value;
			if(ad.getValue().countRoom == 0) value = Float.NaN;
			else value = ad.getValue().sum /  ad.getValue().countRoom;
			result.put(ad.getKey(), value);
		}
		return result ;
	}
	public static Map<String, String> getStringKPIs(GaRoMultiResult singleTimeStepResult, String subGw) {
		Map<String, String> result = new HashMap<>();
		Map<String, AggDataString> aggData = new HashMap<>();
		for(RoomData rd: singleTimeStepResult.roomEvals) {
			if((subGw != null)&&(!rd.gwId.equals(subGw))) continue;
			if(rd.evalResults != null) for(Entry<String, String> resData: rd.evalResults.entrySet()) {
				try {
					if(!resData.getKey().startsWith("$")) continue;
					//float val = Float.parseFloat(resData.getValue());
					//if(Float.isNaN(val)) continue;
					AggDataString ad = aggData.get(resData.getKey());
					if(ad == null) {
						ad = new AggDataString();
						aggData.put(resData.getKey(), ad);
					}
					if(ad.elements == null)
						ad.elements = resData.getValue();
					else
						ad.elements += ", "+resData.getValue();
				} catch(NumberFormatException e) {
					//do nothing
				}
			}
		}
		for(Entry<String, AggDataString> ad: aggData.entrySet()) {
			//final float value;
			//if(ad.getValue().countRoom == 0) value = Float.NaN;
			//else value = ad.getValue().sum /  ad.getValue().countRoom;
			if(ad.getValue().elements != null) result.put(ad.getKey(), ad.getValue().elements);
		}
		return result ;
	}
	/*public static Map<String, Float> getOverallKPIs(GaRoSuperEvalResult<?> superResult, String... ids) {
		Map<String, AggData> aggData = new HashMap<>();
		for(GaRoMultiResult singleTimeStepResult: superResult.intervalResults) {
			for(RoomData rd: singleTimeStepResult.roomEvals) {
				if(rd.evalResults != null) {
					if(ids.length == 0) for(Entry<String, String> resData: rd.evalResults.entrySet()) {
						processEntry(resData.getKey(), resData.getValue(), aggData);
					}
					else for(String id: ids) {
						String val = rd.evalResults.get(id);
						if(val != null) processEntry(id, val, aggData);
					}
				}
			}
		}
		return finalizeKPIs(aggData);
	}
	private static void processEntry(String key, String value, Map<String, AggData> aggData) {
		try {
			float val = Float.parseFloat(value);
			if(Float.isNaN(val)) return;
			AggData ad = aggData.get(key);
			if(ad == null) {
				ad = new AggData();
				aggData.put(key, ad);
			}
			ad.sum += val;
			ad.countRoom++;
		} catch(NumberFormatException e) {
			//do nothing
		}		
	}
	private static Map<String, Float> finalizeKPIs(Map<String, AggData> aggData) {
		Map<String, Float> result = new HashMap<>();
		for(Entry<String, AggData> ad: aggData.entrySet()) {
			final float value;
			if(ad.getValue().countRoom == 0) value = Float.NaN;
			else value = ad.getValue().sum /  ad.getValue().countRoom;
			result.put(ad.getKey(), value);
		}
		return result;
	}*/
	
	/** See {@link MultiEvaluationUtils#getFittingTSforEval(DataProvider, de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationProvider, List)}
	 * @param dp DataProvider to use
	 * @param provider EvaluationProvider to use
	 * @param gwIds gatewayIds to be included. Note that result is unstructued, so more
	 * 		than one gateway may lead to a quite long unstructured list
	 * @return the result is not structured here in contrast would is needed for real evaluation.
	 * 		All input time series are just collected in a list. We do not check for the same time
	 * 		series being in the list here. We get new TimeSeriesData object usually, so using
	 * 		a Set would probably not help
	 */
	public static <SI extends SelectionItem, P extends GaRoSingleEvalProvider> List<TimeSeriesData> getFittingTSforEval(DataProvider<?> dp,
			P singleProvider,
			List<String> gwIds) {
		return getFittingTSforEval(dp, singleProvider, gwIds, null);
	}
	public static <SI extends SelectionItem, P extends GaRoSingleEvalProvider> List<TimeSeriesData> getFittingTSforEval(DataProvider<?> dp,
			P singleProvider,
			List<String> gwIds, List<String> roomIds) {
		@SuppressWarnings("unchecked")
		GaRoEvalProvider<?> garoEval = new GenericGaRoMultiProvider<P>(singleProvider, false) {
			@Override
			protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(
					List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
					GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
					GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw, P singleProvider,
					boolean doBasicEval, List<ResultType> resultsRequested) {
				throw new IllegalStateException("This is a generic instance that cannot be used to create actual evaluation instances!");
			}
		};
		MultiEvaluationInputGeneric[] arr = new MultiEvaluationInputGeneric[garoEval.inputDataTypes().size()];
        int inputIdx = 0;
        int maxDependencyIdx =  garoEval.maxTreeIndex;
        List<DataProviderType> providerTypes = garoEval.inputDataTypes();
	    GaRoMultiEvalDataProvider<?> gdp = null;
	    if(dp instanceof GaRoMultiEvalDataProvider)
	    	gdp = (GaRoMultiEvalDataProvider<?>)dp;
        for(GaRoDataTypeI dt: garoEval.getInputTypesFromRoom()) {
        	MultiEvaluationInputGeneric ts = null;
        	if(gdp != null)
        		ts = gdp.provideMultiEvaluationInput(providerTypes.get(maxDependencyIdx), dp, dt, gwIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID);
			if(ts == null)
				ts = new GaRoMultiEvaluationInput(
	        		providerTypes.get(maxDependencyIdx), dp, dt, gwIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID); //isMulti?GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID:GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID);
	     	arr[inputIdx] = ts;
	     	inputIdx++;
	    }
	    List<MultiEvaluationInputGeneric> input = Arrays.<MultiEvaluationInputGeneric>asList(arr);
	    List<TimeSeriesData> result = MultiEvaluationUtilsGaRo.getFittingTSforEval(dp, garoEval, input);
	    // The GaRo DataProvider returns room time series also for the overall room. We have to filter this out
	    List<TimeSeriesData> toRemove = new ArrayList<>();
	    //candidates are time TimeSeriesData elements of BUILDING_OVERALL_ROOM_ID for which no fitting
	    //room has been found
	    Map<String, TimeSeriesData> toRemoveCandidate = new HashMap<>();
	    Set<ReadOnlyTimeSeries> knownTS = new HashSet<>();
	    for(TimeSeriesData ts: result) {
			if(!(ts instanceof TimeSeriesDataOffline)) throw new IllegalStateException("getStartAndEndTime only works on TimeSeriesData input!");
			TimeSeriesDataOffline tsd = (TimeSeriesDataOffline) ts;
	    	ReadOnlyTimeSeries rots = tsd.getTimeSeries();
	    	final TimeSeriesDataExtendedImpl tse;
	    	final String roomId;
	    	if(tsd instanceof TimeSeriesDataExtendedImpl) {
				tse = (TimeSeriesDataExtendedImpl)tsd;
				if(tse.getIds() == null || tse.getIds().size() < 2) {
					continue;
					//throw new IllegalStateException("No room information - check if we should just keep the element or error?");
				}
				roomId = tse.getIds().get(1);
    		} else continue;
	    	if(knownTS.contains(rots)) {
				if(roomId.equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) {
					toRemove.add(ts);
					continue;
				} else {
					TimeSeriesData tsCand = toRemoveCandidate.get(roomId);
					if(tsCand != null) toRemove.add(tsCand);
				}
	    	} else knownTS.add(rots);
	    	if(roomId.equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) {
	    		toRemoveCandidate.put(roomId, ts);
	    	}
	    	if(roomIds != null) {
	    		if(!roomIds.contains(roomId))
	    			toRemove.add(ts);
	    	}
	    }
	    result.removeAll(toRemove);
	    return result;
	}

}
