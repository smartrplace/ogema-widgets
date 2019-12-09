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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.tools.resource.util.TimeUtils;

import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.sema.supereval.EvalResult.DataType;
import de.iwes.util.format.StringFormatHelper;

@Deprecated
public class EvalHelperExtended {
	
	public static void addResult(ResultType resultType, List<SingleEvaluationResult> singleResultList,
			 Map<ResultType,EvaluationResult> results) {
		results.put(resultType, new EvaluationResultImpl(singleResultList, resultType));
	}
	
	public static void addSingleResult(ResultType resultType, SingleEvaluationResult singleResult, Map<ResultType,EvaluationResult> results) {
//		List<SingleEvaluationResult> singleResultList = new ArrayList<>();
//		singleResultList.add(singleResult);
//		results.put(resultType, new EvaluationResultImpl(singleResultList, resultType));
		addResult(resultType, Collections.singletonList(singleResult), results);
	}
	
	public static void addSingleResult(ResultType resultType, float value, Map<ResultType,EvaluationResult> results) {
		List<SingleEvaluationResult> singleResultList = new ArrayList<>();
		singleResultList.add(new SingleValueResultImpl<>(resultType, value, Collections.<TimeSeriesData> emptyList()));
		results.put(resultType, new EvaluationResultImpl(singleResultList, resultType));
	}
	
	public static double getSingleResultValue(ResultType resultType, EvaluationInstance instance) {
		Map<ResultType, EvaluationResult> evalRes = instance.getResults();
		EvaluationResult min = evalRes.get(resultType);
		SingleEvaluationResult ser = min.getResults().get(0);
		if(ser instanceof SingleValueResult) {
			@SuppressWarnings("unchecked")
			SingleValueResult<Float> svr = (SingleValueResult<Float>)ser;
			return (double) svr.getValue();
		}
		throw new IllegalStateException("Unexpected result type");
	}
	
	public static int getSingleResultInt(ResultType resultType, EvaluationInstance instance) {
		Map<ResultType, EvaluationResult> evalRes = instance.getResults();
		EvaluationResult min = evalRes.get(resultType);
		SingleEvaluationResult ser = min.getResults().get(0);
		if(ser instanceof SingleValueResult) {
			@SuppressWarnings("unchecked")
			SingleValueResult<Integer> svr = (SingleValueResult<Integer>)ser;
			return (int) svr.getValue();
		}
		throw new IllegalStateException("Unexpected result type");
	}
	
	public static long getSingleResultLong(ResultType resultType, EvaluationInstance instance) {
		Map<ResultType, EvaluationResult> evalRes = instance.getResults();
		EvaluationResult min = evalRes.get(resultType);
		SingleEvaluationResult ser = min.getResults().get(0);
		if(ser instanceof SingleValueResult) {
			@SuppressWarnings("unchecked")
			SingleValueResult<Long> svr = (SingleValueResult<Long>)ser;
			return (long) svr.getValue();
		}
		throw new IllegalStateException("Unexpected result type");
	}
	
	public enum PrintMode {
		OverallEval,
		PerGW
	}
	
	public static void printToStream(PrintStream out, long[] startEnd, SuperEvalResult ser, Map<DataType, EvalResult> results,
			PrintMode mode) {
        out.println("***********************************************************");
        switch(mode) {
        case OverallEval:
            out.println("Result starting from: " + TimeUtils.getDateAndTimeString(startEnd[0])+" to "+TimeUtils.getDateAndTimeString(startEnd[1]));
            break;
        case PerGW:
            out.println("* Per Gateway:");
            break;
        }
        out.println("***********************************************************");
        if(mode == PrintMode.OverallEval) {
        	EvalResult any = results.get(DataType.Any);
        	String missingSources = "";
        	if(any != null && any.missingSources != null) {
        		missingSources = StringFormatHelper.getListToPrint(any.missingSources);
        	}
        	out.println("Found total " + ser.gwCountWithData+ " of "+ ser.gwCount + " gateways with data of quality >=" + "!! Should not be used anymore!!" +
        			", missingGWs:"+missingSources);
        }
        for (EvalResult res : results.values()) {
            EvalResult.printResult(res, out);
        }
    }
	
	public static class SuperEvalResultTotal {
		public long[] startEnd;
		public SuperEvalResult ser;
		public Map<DataType, EvalResult> results;
		public Map<DataType, EvalResult> resultsPerGw;
		public Map<Integer, Map<DataType, EvalResult>> resultsPerRoomType;
	}
	public static void exportResultCSV(Path evalOutputPath, long[] startEnd, SuperEvalResult ser, Map<DataType, EvalResult> results,
			Map<DataType, EvalResult> resultsPerGw) {
		SuperEvalResultTotal sert = new SuperEvalResultTotal();
		sert.startEnd = startEnd;
		sert.ser = ser;
		sert.results = results;
		sert.resultsPerGw = resultsPerGw;
		exportResultCSV(evalOutputPath, sert);
	}
	
	public static void exportResultCSV(Path evalOutputPath, SuperEvalResultTotal sert) {

        File csvFile = evalOutputPath.resolve("gwAnalysis-" + StringFormatHelper.getDateForPath(sert.startEnd[0]) + ".csv").toFile();
        try (PrintStream out = new PrintStream(csvFile, StandardCharsets.UTF_8.name())) {
           	out.println("StartTime, "+StringFormatHelper.getFullTimeDateInLocalTimeZone(sert.startEnd[0]));
           	out.println("EndTime, "+StringFormatHelper.getFullTimeDateInLocalTimeZone(sert.startEnd[1]));
           	out.println("GatewaysInDataset, "+sert.ser.gwCount);
           	out.println("GatewaysEvaluated, "+sert.ser.gwCountWithData);
           	out.println("RoomsInDataset, "+sert.ser.countRooms);
           	out.println("RoomsEvaluated, "+sert.ser.countRoomsWithData);
           	out.println("RoomsEvaluatedAllData, "+sert.ser.countRoomsWithAllDeviceData);
           	out.println("DevicesInDataset, "+sert.ser.deviceCount);
           	out.println("DevicesEvaluated, "+sert.ser.deviceCountWithData);
            out.println("DataType, TimeSeriesNum, TimeSeriesNonEmptyNum, MaxTSNumSourceId, DataPointNum, "
            		+ "Average, Minimum, Maximum, InputTime, NonGapTime, NonGapTime2");
            for (EvalResult res : sert.results.values()) {
            	EvalResult.printCSV(res, out, "");
            }
            for (EvalResult res :sert. resultsPerGw.values()) {
            	EvalResult.printCSV(res, out, "_PerGW");
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
	}
	
	public static void exportSuperResultCSV(Path evalOutputPath, List<SuperEvalResultTotal> results, long[] startEndTotal) {
        File csvFile = evalOutputPath.resolve("gwAnalysis-" + StringFormatHelper.getDateForPath(startEndTotal[0]) + ".csv").toFile();
        try (PrintStream out = new PrintStream(csvFile, StandardCharsets.UTF_8.name())) {
	        out.println("StartTime, EndTime, GatewaysInDataset, GatewaysEvaluated, RoomsInDataset, "+
	           			"RoomsEvaluated, RoomsEvaluatedAllData, DevicesInDataset, DevicesEvaluated");
	       	for(SuperEvalResultTotal sert: results) {
	           	out.printf(StringFormatHelper.getFullTimeDateInLocalTimeZone(sert.startEnd[0])+", ");
	           	out.printf(StringFormatHelper.getFullTimeDateInLocalTimeZone(sert.startEnd[1])+", ");
	           	out.printf(sert.ser.gwCount+", ");
	           	out.printf(sert.ser.gwCountWithData+", ");
	           	out.printf(sert.ser.countRooms+", ");
	           	out.printf(sert.ser.countRoomsWithData+", ");
	           	out.printf(sert.ser.countRoomsWithAllDeviceData+", ");
	           	out.printf(sert.ser.deviceCount+", ");
	           	out.println(sert.ser.deviceCountWithData);
	       	}
	        out.println("DataType, TimeSeriesNum, TimeSeriesNonEmptyNum, MaxTSNumSourceId, DataPointNum, "
	        		+ "Average, Minimum, Maximum, InputTime, NonGapTime, NonGapTime2");
	        
	        Set<DataType> pivot = results.get(0).results.keySet();
	        List<DataType> sorted = asSortedList(pivot);
	        for(DataType dt:sorted) {
		        int index = 0;
	        	for(SuperEvalResultTotal sert: results) {
	        		EvalResult res = sert.results.get(dt);
	        		EvalResult.printCSV(res, out, "_"+index);
	        		index++;
	        	}
	        }
	        for(DataType dt:sorted) {
		        int index = 0;
	        	for(SuperEvalResultTotal sert: results) {
	        		EvalResult resPerGw = sert.resultsPerGw.get(dt);
	            	if(resPerGw != null && resPerGw.source != null)
	            		EvalResult.printCSV(resPerGw, out, "_PerGW_"+index);
	        		index++;
	        	}
	        }
	        Set<Integer> pivotRT = results.get(0).resultsPerRoomType.keySet();
	        List<Integer> sortedRT = asSortedList(pivotRT);
	        for(int rt:sortedRT) {
		        for(DataType dt:sorted) {
			        int index = 0;
		        	for(SuperEvalResultTotal sert: results) {
		        		Map<DataType, EvalResult> dtmap = sert.resultsPerRoomType.get(rt);
		        		if(dtmap != null) {
		        			EvalResult res = sert.resultsPerRoomType.get(rt).get(dt);
			            	if(res != null && res.source != null)
			            		EvalResult.printCSV(res, out, "_"+rt+"_"+index);
		        		}
		        		index++;
		        	}
		        }
	        }
         } catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static EvalResult getOrCreateResult(Map<DataType, EvalResult> results, DataType dt) {
		EvalResult res = results.get(dt);
		if(res == null) {
			res = new EvalResult(dt);
			results.put(dt, res);
		}
		return res;
	}
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
}
