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
package de.iwes.timeseries.eval.garo.multibase;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoSuperEvalResultDeser;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
//@Service(EvaluationProvider.class)
//@Component
public interface GaRoSingleEvalProvider extends EvaluationProvider, EvaluationListener {
   GaRoDataTypeI[] getGaRoInputTypes();

   /**
    * @return null if all room types shall be used, -1 if evaluation shall be made on
    * entire building (unit). If -1 is returned the evaluation runs on gateway level, otherwise
    * on room-level. Running on gateway-level means that per gateway only a single room is
    * evaluated with name
    * GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID and that all fitting input for all
    * rooms of the gateway is provided to this evaluation. For this reason you should be careful
    * to specify room-specific inputs on evaluations running on gateway-level, but it can be required
    * in some cases. If the evaluation runs on room-level and gateway-specific input is specified
    * the gateway-specific input data will be applied to the evaluations of all rooms of the
    * gateway.<br>
    * See {@link GaRoEvalHelper#getGatewayTypes() and GaRoEvalHelper#getOverallTypes()} for a specification
    * which input types are gateway-specific and the same overall the multi-evaluation for a
    * time step. All other input types are room-specific.<br>
    * Note that input that is gateway-specific is provided in a virtual room  .<br>
    * Note that evaluation on "overall" level (super-gateway level) is currently not
    * supported.<br>
    * Other negative values than -1 mean that any specific room with a negative roomType shall be
    * used (including a room that has type == -1)
    */
   int[] getRoomTypes();
   
   /**
    * 
    * @return null if no extended result definition is used. Note that usually also the method
    * 	{@link #getSuperResultClassForDeserialization()} has to override with a special version
    * 	of GaRoSuperEvalResult in order to allow for proper JSON deserialization.
    */
    Class<? extends GaRoMultiResultExtended> extendedResultDefinition();
	
    /** Override this if a special class for deserialization shall be used*/
    default Class<? extends GaRoSuperEvalResult<?>> getSuperResultClassForDeserialization() {
		return (Class<? extends GaRoSuperEvalResult<?>>) GaRoSuperEvalResultDeser.class;
	}

	public enum IntervalAggregationMode {
		AVERAGING,
		INTEGRATING,
		MIN,
		MAX,
		/** In mode OTHER the evaluation has to run for every aggregation level requested by the user. If this
		 * is expected to be very time-consuming defined pre-evaluations with another aggregation mode, which
		 * can avoid touching the raw data again when calculating aggregated results.
		 */
		OTHER
	}
	
	/**Get aggregation mode for a result type
	 * @param resultType must be one of the result returned by {@link #resultTypes()}*/
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType);

	/** Provision of the gwId and roomId currently evaluated to the EvaluationProvider. Note that in the default
	 * EvaluationProvider API including the standard GaRo-API the single evaluation does not get this information.
	 * The method is called by the MultiEvaluation framework before the evaluation is started.
	 * So a PreEvalRequesting version of the GaRo provider may just be used to get this information.<br>
	 * If using the GenericGaRoSingleEvalProviderPreEval as implementation the member variables currentGwId
	 * and currentRoomId will be set after completion of the method.
	 * 
	 * @param gwId current gatewayId
	 * @param roomId current roomId
	 */
	default void provideCurrentValues(String gwId, String roomId, long startTime, AbstractSuperMultiResult<GaRoMultiResult> superResult) {};

	public static final ChronoUnit DEFAULT_CHRONO = ChronoUnit.DAYS;
	public static final int DEFAULT_INTERVALS_TO_CALC = 3;
	public static interface MessageGenerator {
		/** Determine after calculation of new KPIs whether a message shall be generated to be
		 * sent to support
		 * @param timeSeriesToCheck KPIs for base time interval or the
		 * 		selected interval type
		 * @return null if no message shall be sent based on the result otherwise
		 * 		the first element is the title of the message, more elements are
		 * 		rows
		 */
		String[] sendMessage(List<ReadOnlyTimeSeries[]> timeSeriesToCheck,
				int intervalType);
	}
	public static class KPIPageDefintion {
		public List<String> providerId;
		public List<String[]> resultIds = new ArrayList<>();
		public String configName;
		public String urlAlias;
		public ChronoUnit chronoUnit = DEFAULT_CHRONO;
		public int defaultIntervalsToCalc = DEFAULT_INTERVALS_TO_CALC;
		public int defaultIntervalsPerColumnType = 2;
		public Map<String, Integer> specialIntervalsPerColumn = new HashMap<>();
	}
	default List<KPIPageDefintion> getPageDefinitionsOffered() {return null;}
}
