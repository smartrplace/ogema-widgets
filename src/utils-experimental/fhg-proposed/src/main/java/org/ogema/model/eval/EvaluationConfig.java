package org.ogema.model.eval;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.action.Action;
import org.ogema.model.alignedinterval.RepeatingOperationConfiguration;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.prototypes.Data;
	
/** The fundamental capabilities categories of an EvaluationProvider have been described in
 * gateway-master-v2 -> org.smartrplace.remotesupervision.master.api.EvaluationProvider. Currently
 * the actual characteristics of the underlying evaluation provider should be described with a resource
 * model derived from EvaluationAction in text as EvaluationProviders are not modelled and processed by
 * the framework yet.
 * @deprecated no implementation using this available, use OnlineEvalProvider instead
*/
@Deprecated
public interface EvaluationConfig extends Data {
	StringResource controllingApplication();
	/** May be used for identification purposes of the application*/
	StringResource id();
	
	/** This should be available for evaluations supporting continuous operation and output provision in
	 * the StatisticalAggregation format (default). If the evaluation has more than one output structure
	 * use additional decorators or a ResourceList.
	 */
	StatisticalAggregation destinationStat();
	/** Allows to switch on/off the continuous evaluation, but also should be set to false when the
	 * evaluation ceases for some reason
	 */
	BooleanResource continuousOperationStateControl();
	/**This should be available when the evaluation is able to use all logged input data to calculate the
	 * evaluation for the past. The result is usually stored in destinationStat and other places also used
	 * by the continuous operation
	 */
	Action performRetroEvaluation();
	
	/**This should be available for evaluations providing an instantanious execution Action. The result
	 * of such an Action shall be provided here, but additional result resources may be added if more
	 * than one value is calculated */
	ValueResource result();
	/** Perform evaluation once. Should only be available if this is supported by the evaluation (continuous
	 * evaluation is the default, so this may not be available usually)
	 */
	Action instantaniousEvaluation();
	/** This should be available for evaluations where the operation times are not determined by
	 * StatisticalAggregation*/
	RepeatingOperationConfiguration operationTimes();
	
	
	/**If true the evaluation configuration is the one for the eval type that aggregates all clients on a master*/
	BooleanResource isPrimaryAggregation();
	
	ResourceList<EvaluationEvent> events();
}