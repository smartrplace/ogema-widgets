package de.iwes.timeseries.eval.garo.multibase;

import java.util.List;

import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;

public interface GaRoSingleEvalProviderPreEvalRequesting extends GaRoSingleEvalProvider {
	  
	   public enum GaRoHierarchyLevel {
		   OVERALLDATA,
		   SINGLE_TIMEINTERVAL,
		   SINGLE_GATEWAY,
		   SINGLE_ROOM,
		   SINGLE_TIMESERIES
	   }
	   public class PreEvaluationRequested {
		   private final String sourceProvider;
		   //private final GaRoHierarchyLevel hierarchyLevel;
		   //private final String resultType;
		   
		   /** 
		    * @param sourceProvider usually simple name of the respective GaRoSingleEvalProvider class. The name can
		    * 		be created programmatically if the respective class is available or as direct String to avoid
		    * 		a dependency that otherwise would not be necessary.
		    */
		   public PreEvaluationRequested(String sourceProvider) {
			   this.sourceProvider = sourceProvider;
			   //this.hierarchyLevel = hierarchyLevel;
			   //this.resultType = resultType;
		   }
		   //* @param hierarchyLevel set to null if more than one hierarchy level is required or cannot be specified. In
		   //* 		most cases the hierarchy level is not evaluated
		   //* @param resultType

		   /*public PreEvaluationRequested(String sourceProvider, GaRoHierarchyLevel hierarchyLevel, String resultType) {
			   this.sourceProvider = sourceProvider;
			   this.hierarchyLevel = hierarchyLevel;
			   this.resultType = resultType;
		   }*/

		   
		   public String getSourceProvider() {
			   return sourceProvider;
		   };
		   /*public GaRoHierarchyLevel getHierarchyLevel() {
			   return hierarchyLevel;
		   }
		   public String getResultType() {
			   return resultType;
		   }*/
	   }
	   
	   List<PreEvaluationRequested> preEvaluationsRequested();
	   
	   /** Receive PreEvaluationProvider
	    * TODO: Giving this input to the provider only makes sense in a Multi-Evaluation-environment where each
	    * provider instance is organized by the Multi-evaluation environment and typically will pass the same
	    * provider to all evaluations. In such an environment this structure should be quite efficient, though.
	    * 
	    * @param requestIdx redundent with providerId, provided for convenience
	    * @param providerId
	    * @param provider
	    */
	   void preEvaluationProviderAvailable(int requestIdx, String providerId, GaRoPreEvaluationProvider<?> provider);
	   
	   //TODO: Is there a better way to provide this information to the single evaluation?
	   void provideCurrentValues(String gwId, String roomId);
}
