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
package de.iwes.timeseries.eval.garo.helper.jaxb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Executors;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.helper.EvalHelperExtended;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInput;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting.PreEvaluationRequested;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;

public class GaRoEvalHelperGeneric {
	
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			GatewayBackupAnalysis gatewayParser, long startTime,
			long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
			GaRoPreEvaluationProvider[] preEvalProviders, List<ResultType> resultsRequested, List<String> gwIds,
			String resultFileName, ResultHandler<GaRoMultiResult> resultHandler,
			List<GaRoMultiEvalDataProvider<?>> dataProviders) {
		return performGenericMultiEvalOverAllData(singleEvalProvider, gatewayParser,
				startTime, endTime, resultStepSize, doExportCSV, doBasicEval, preEvalProviders,
				resultsRequested, gwIds, resultFileName, resultHandler, dataProviders, null);
	}
   /** Perform multi-evaluation on given DataProviders for a GaRoSingleEvalProvider class
     * 
     * @param singleEvalProvider evaluation provider class to use. Note that this method is
     * 		usually not applicable for providers that require initialization. In this case reflective construction will fail.
     * @param gatewayParser object providing source data
     * @param startTime
     * @param endTime
     * @param resultStepSize
     * @param doExportCSV if not null the result will be exported as zipped csv file
     * @param doBasicEval if true the basic evaluation provider will be executed for quality checks (not recommended anymore)
     * @param preEvalProviders if singleEvalProvider requests pre evaluation data provide the respective providers here
     * @param resultsRequested if null all results offered by the provider will be calculated
     * @param gwIds gateways to be evaluated. If null no filtering of input will be applied.
     * @param resultFileName should usually end on ".json". Only relevant if not resultHandler is provided
     * @param resultHandler may be null
     * @param dataProviders DataProviders that shall be used to obtain evaluation data. Usually each top-level
     * 		data set (e.g. gateway information) is only evaluated within a single data provider, so you cannot
     * 		take e.g. temperature information for a room from one data provider and humidity information for
     * 		the same room from another provider. This may be realized in the future.
     * @param additionalConfigurations configurations for the evaluations besides the mandatory start
     * 		end time configuration, which will be added by the framework later on. May be null if no
     * 		evaluation-specific configurations shall be provided
     * @return
     */
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
				GatewayBackupAnalysis gatewayParser, long startTime,
				long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
				GaRoPreEvaluationProvider[] preEvalProviders, List<ResultType> resultsRequested, List<String> gwIds,
				String resultFileName, ResultHandler<GaRoMultiResult> resultHandler,
				List<GaRoMultiEvalDataProvider<?>> dataProviders,
				Collection<ConfigurationInstance> additionalConfigurations) {
		return performGenericMultiEvalOverAllData(singleEvalProvider, gatewayParser,
				startTime, endTime, resultStepSize, doExportCSV, doBasicEval, preEvalProviders,
				resultsRequested, gwIds, resultFileName, resultHandler, dataProviders, additionalConfigurations,
				false);
	}
	public static <P extends GaRoSingleEvalProvider> GaRoTestStarter<GaRoMultiResult> performGenericMultiEvalOverAllData(Class<P> singleEvalProvider,
			GatewayBackupAnalysis gatewayParser, long startTime,
			long endTime, ChronoUnit resultStepSize, CSVArchiveExporter doExportCSV, boolean doBasicEval,
			GaRoPreEvaluationProvider[] preEvalProviders, List<ResultType> resultsRequested, List<String> gwIds,
			String resultFileName, ResultHandler<GaRoMultiResult> resultHandler,
			List<GaRoMultiEvalDataProvider<?>> dataProviders,
			Collection<ConfigurationInstance> additionalConfigurations, boolean performBlocking) {

		try {
			P singleProvider = singleEvalProvider.newInstance();
			if(singleProvider instanceof GaRoSingleEvalProviderPreEvalRequesting) {
				GaRoSingleEvalProviderPreEvalRequesting preEval = (GaRoSingleEvalProviderPreEvalRequesting)singleProvider;
				int i= 0;
				try {
				List<PreEvaluationRequested> reqs = preEval.preEvaluationsRequested();
				if(reqs != null) for(PreEvaluationRequested req: reqs) {
					preEval.preEvaluationProviderAvailable(i, req.getSourceProvider(), preEvalProviders[i]);
					i++;
				}
				} catch(ConcurrentModificationException e) {
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
			}
			@SuppressWarnings("unchecked")
			GaRoTestStarter<GaRoMultiResult> result = new GaRoTestStarter<GaRoMultiResult>((
					new GenericGaRoMultiProvider<P>(singleProvider, doBasicEval) {
						@Override
						protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(
								List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
								GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
								GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw, P singleProvider,
								boolean doBasicEval, List<ResultType> resultsRequested) {
							throw new IllegalStateException("This is a generic instance that cannot be used to create actual evaluation instances!");
						}
						
					}),
				gatewayParser, startTime, endTime, resultStepSize,
				resultFileName!=null?resultFileName:singleEvalProvider.getSimpleName()+"Result.json", doExportCSV, resultsRequested, gwIds,
						resultHandler, dataProviders,additionalConfigurations);
			if(performBlocking) {
				try {
					result.call();
				} catch(Exception e) {
					throw new IllegalStateException(e);					
				}
			} else
			Executors.newSingleThreadExecutor().submit(result);
			return result;
		} catch(InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
    }
	
    
    public static <T extends GaRoMultiResult> MultiEvaluationInstance<T> startGaRoMultiEvaluationOverAllData(
    		long startTime, long endTime, ChronoUnit resultStepSize,
    		GaRoEvalProvider<T> garoEval, GatewayBackupAnalysis gatewayParser,
    		List<ResultType> resultsRequested, List<String> gwIds,
    		InstanceReceiver instanceReceiver, List<GaRoMultiEvalDataProvider<?>> dataProviders,
    		Collection<ConfigurationInstance> additionalConfigurations) {

        return startGaRoMultiEvaluation(startTime, endTime, resultStepSize, garoEval,
        		gatewayParser, dataProviders, resultsRequested, gwIds, instanceReceiver,
        		additionalConfigurations);
    }    
    public static interface InstanceReceiver {
    	<T extends GaRoMultiResult> void getMultiEvalInstance(MultiEvaluationInstance<T> evalInstance);
    }
    public static <T extends GaRoMultiResult> MultiEvaluationInstance<T> startGaRoMultiEvaluation(
    		long startTime, long endTime, ChronoUnit resultStepSize,
        	GaRoEvalProvider<T> garoEval, GatewayBackupAnalysis gatewayParser,
        	List<GaRoMultiEvalDataProvider<?>> dataProviders, List<ResultType> resultsRequested, List<String> gwIds,
        	InstanceReceiver instanceReceiver, Collection<ConfigurationInstance> additionalConfigurations) {

    	Collection<ConfigurationInstance> configurations = EvalHelperExtended.addStartEndTime(startTime, endTime,  additionalConfigurations);
        List<DataProviderType> providerTypes = garoEval.inputDataTypes();
		MultiEvaluationInputGeneric[] arr = new MultiEvaluationInputGeneric[garoEval.inputDataTypes().size()];
        int inputIdx = 0;
        int maxDependencyIdx =  garoEval.maxTreeIndex;
        /*boolean isMulti = false;
        for(GaRoMultiEvalDataProvider<?> dp: dataProviders) {
        	if(dp.providesMultipleGateways()) isMulti = true;
        }*/
        //for(DataProvider<?> dp: dataProviders) {
	        for(GaRoDataTypeI dt: garoEval.getInputTypesFromRoom()) {
	        	@SuppressWarnings({ "unchecked", "rawtypes" })
				MultiEvaluationInputGeneric ts = new GaRoMultiEvaluationInput(
	            		providerTypes.get(maxDependencyIdx), (List)dataProviders, dt, gwIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID); //isMulti?GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID:GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID);
	         	arr[inputIdx] = ts;
	         	inputIdx++;
	        }
        //}
        List<MultiEvaluationInputGeneric> input = Arrays.<MultiEvaluationInputGeneric>asList(arr);
         
        //TODO
        if(garoEval.getInputTypesFromGw() != null)
        	throw new UnsupportedOperationException("GetInputTypesFromGw not fully implemented yet!");
        
		MultiEvaluationInstance<T> result = garoEval.newEvaluation(input,
        		configurations, resultStepSize, resultsRequested);
        if(instanceReceiver != null) instanceReceiver.getMultiEvalInstance(result);
		result.execute();
        return result;
    }
    
    //TODO
	public static <T extends GaRoMultiResult> List<ReadOnlyTimeSeries> getAllInputData(GaRoEvalProvider<T> garoEval,
			GatewayBackupAnalysis gatewayParser, long startTime,
			long endTime, List<String> gwIds) {
		//TODO
		return null;
	}

    
	public static interface CSVArchiveExporter {
		void writeGatewayDataArchive(FileOutputStream file,
				Collection<GaRoTimeSeriesId> timeSeriesToExport,
	            long startTime, long endTime) throws IOException;
	}
}
