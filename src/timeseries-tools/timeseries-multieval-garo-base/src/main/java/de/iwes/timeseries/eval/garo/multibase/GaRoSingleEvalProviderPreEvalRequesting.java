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

import java.util.List;

import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;

public interface GaRoSingleEvalProviderPreEvalRequesting extends GaRoSingleEvalProvider {
	  
	   public enum IntervalRelation {
		   /** Same interval means that the pre-evaluation should be evaluated for the same interval
		    * as this evaluation using the result
		    */
		   SAME,
		   /** AHEAD means that the the pre-evaluation interval needs to be before the interval
		    * of this evaluation. In automated evaluation the input interval shall be the interval before
		    * the current interval.
		    */
		   AHEAD
	   }

	   public class PreEvaluationRequested {
		   private final String sourceProvider;
		   private final IntervalRelation intervalRelation;
		   private final boolean isRequired;
		   /** 
		    * @param sourceProvider the ID of the respective GaRoSingleEvalProvider class. The ID can
		    * 		be created programmatically if the respective class is available or as direct String to avoid
		    * 		a dependency that otherwise would not be necessary.
		    */
		   public PreEvaluationRequested(String sourceProvider) {
			   this(sourceProvider, IntervalRelation.SAME, true);
		   }
		   public PreEvaluationRequested(String sourceProvider, IntervalRelation intervalRelation,
				   boolean isRequired) {
			   this.sourceProvider = sourceProvider;
			   this.intervalRelation = intervalRelation;
			   this.isRequired = isRequired;
		   }
		   
		   public String getSourceProvider() {
			   return sourceProvider;
		   }

		   /** See documentation in IntervalRelation*/
		   public IntervalRelation getIntervalRelation() {return intervalRelation;}
		   
		   /** If a pre-evaluation is not required it is not generated when not available*/
		   public boolean isRequired() { return isRequired;}
	   }
	   
	   /** Pre-Evaluation providers requested*/
	   List<PreEvaluationRequested> preEvaluationsRequested();
	   
	   /** The requesting evaluation provider will be asked via this method whether it wants to inject
	    * additional time series into the input timeseries of its exection. The method will be called
	    * after {@link #preEvaluationProviderAvailable(int, String, GaRoPreEvaluationProvider)} and
	    * {@link #provideCurrentValues(String, String)} and can be used to return time series data 
	    * from the input provider, but in principal also from other sources
	    * @return may be null if no time series shall be injected
	    */
	   default List<EvaluationInputImpl> timeSeriesToInject() {return null;}
	   
	   /** Receive PreEvaluationProvider
	    * TODO: Giving this input to the provider only makes sense in a Multi-Evaluation-environment where each
	    * provider instance is organized by the Multi-evaluation environment and typically will pass the same
	    * provider to all evaluations. In such an environment this structure should be quite efficient, though.
	    * 
	    * @param requestIdx redundent with providerId, provided for convenience
	    * @param providerId
	    * @param provider
	    */
	   void preEvaluationProviderAvailable(int requestIdx, String providerId, GaRoPreEvaluationProvider provider);
	   
	   
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
	   //void provideCurrentValues(String gwId, String roomId, long startTime);
	   
	   /** Overwrite this if no gateway/room evaluations shall take place, but just super results shall be evaluated
		 * as inputs
		 */
	   default boolean executeSuperLevelOnly() {return false;}
	   void performSuperEval(AbstractSuperMultiResult<?> destination);

}
