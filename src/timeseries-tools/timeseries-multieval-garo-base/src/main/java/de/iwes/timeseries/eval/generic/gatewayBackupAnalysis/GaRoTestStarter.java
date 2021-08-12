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
package de.iwes.timeseries.eval.generic.gatewayBackupAnalysis;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper.CSVArchiveExporter;
import de.iwes.timeseries.eval.generic.gatewayBackupAnalysis.GaRoEvalHelperGeneric.InstanceReceiver;

public class GaRoTestStarter<T extends GaRoMultiResult> implements Callable<Void> {
	public final static String FILE_PATH = System.getProperty("de.iwes.tools.timeseries-multieval.resultpath", "./data/evaluationresults"); //"../evaluationresults");
	private final GaRoEvalProvider<T> garoEval;
	//private final GatewayBackupAnalysisAccess gatewayParser;
	private final long startTime;
	private final long endTime;
	private final ChronoUnit resultStepSize;
	private final String jsonOutFileName;
    private final CSVArchiveExporter doExportCSV;
	private final List<ResultType> resultsRequested;
	private final List<String> gwIds;
	private final ResultHandler<T> resultHandler;
	private List<GaRoMultiEvalDataProvider<?>> dataProviders;
	private final Collection<ConfigurationInstance> additionalConfigurations;
	
	private final InstanceReceiver instanceReceiver = new InstanceReceiver() {

		@SuppressWarnings("unchecked")
		@Override
		public <S extends GaRoMultiResult> void getMultiEvalInstance(
				MultiEvaluationInstance<S> evalInstance) {
			eval = (MultiEvaluationInstance<T>) evalInstance;
			
		}
		
	};
	public volatile MultiEvaluationInstance<T> eval = null;
	public MultiEvaluationInstance<T> getEval() {
		return eval;
	}
	public GaRoEvalProvider<T> getEvalProvider() {
		return garoEval;
	}
	
	public GaRoTestStarter(GaRoEvalProvider<T> garoEval, long startTime,
			long endTime, ChronoUnit resultStepSize,
			String jsonOutFileName, CSVArchiveExporter doExportCSV) {
		this(garoEval, startTime, endTime, resultStepSize, jsonOutFileName, doExportCSV,
				null, null, null, null);
	}
	public GaRoTestStarter(GaRoEvalProvider<T> garoEval, long startTime,
			long endTime, ChronoUnit resultStepSize,
			String jsonOutFileName, CSVArchiveExporter doExportCSV,
			List<ResultType> resultsRequested, List<String> gwIds,
			ResultHandler<T> resultHandler, List<GaRoMultiEvalDataProvider<?>> dataProviders) {
		this(garoEval, startTime, endTime, resultStepSize, jsonOutFileName, doExportCSV,
				resultsRequested, gwIds, resultHandler, dataProviders, null);
	}
	@Deprecated
	public static List<GaRoMultiEvalDataProvider<?>> getDataProvidersFromGatewayParser(GatewayBackupAnalysisAccess gatewayParser) {
		List<GaRoMultiEvalDataProvider<?>> dataProviders = new ArrayList<>();
		GaRoMultiEvalDataProvider<?> dataProvider = gatewayParser.getDataProvider(); //new GaRoMultiEvalDataProviderJAXB(gatewayParser);
		dataProviders.add(dataProvider);
		return dataProviders;
	}
	public GaRoTestStarter(GaRoEvalProvider<T> garoEval, long startTime,
			long endTime, ChronoUnit resultStepSize,
			String jsonOutFileName, CSVArchiveExporter doExportCSV,
			List<ResultType> resultsRequested, List<String> gwIds,
			ResultHandler<T> resultHandler, List<GaRoMultiEvalDataProvider<?>> dataProviders,
			Collection<ConfigurationInstance> additionalConfigurations) {
		this.garoEval = garoEval;
		this.startTime = startTime;
		this.endTime = endTime;
		this.resultStepSize = resultStepSize;
		this.jsonOutFileName = jsonOutFileName;
		this.doExportCSV = doExportCSV;
		this.resultsRequested = resultsRequested;
		this.gwIds = gwIds;
		this.resultHandler = resultHandler;
		this.dataProviders = dataProviders;
		this.additionalConfigurations = additionalConfigurations;
	}
	
    @Override
    public Void call() throws Exception {
    	try {
    	if(dataProviders == null) {
    		throw new IllegalStateException("At least one data provider has to be set now!");
    		//dataProviders = new ArrayList<>();
    		//GaRoMultiEvalDataProvider<?> dataProvider = gatewayParser.getDataProvider(); //new GaRoMultiEvalDataProviderJAXB(gatewayParser);
    		//dataProviders.add(dataProvider);
    	}
        GaRoEvalHelperGeneric.startGaRoMultiEvaluationOverAllData(startTime, endTime, resultStepSize,
    				garoEval, resultsRequested, gwIds, instanceReceiver,
    				dataProviders, additionalConfigurations);

        Set<GaRoTimeSeriesId> timeSeriesEvalAll = new HashSet<>();
        AbstractSuperMultiResult<T> result = eval.getResult();
        for(T res: result.intervalResults) {
        	timeSeriesEvalAll.addAll(res.timeSeriesEvaluated);
        }
        
        System.out.printf("evaluation runs done: %d\n", result.intervalResults.size());

        if(resultHandler != null) resultHandler.resultAvailable(result, jsonOutFileName);
        else  {
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
        }
        return null;
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    }

}
