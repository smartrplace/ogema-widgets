/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.tools.statistics;

import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.prototypes.Data;

import de.iwes.tools.statistics.StatisticsHelper.AggregationResolution;
import de.iwes.util.timer.AbsoluteTimeHelper;

/**Generic integration of a value calculated from one or several FloatResources and transfer to a
 * ComplexStatisticalAggregationProvider for further processing
 * @author dnestle
 */
public class IntegratingStatProvider implements StatisticalProvider {
	public List<FloatResource> inputValues;
	ResourceValueListener<FloatResource> inputListener;
	
	public FloatResource resultValue;

	public StatisticalAggregation evaluation;
	public StatisticalAggregationProvider provider;
	
	//private final ResourceValueListener<FloatResource> inputListener;
	public IntegratingStatProvider() {}
	/** init required here as this cannot be done in the constructor
	 * 
	 * @param inputValues resources providing input to this statistical aggregation
	 * @param inputListener listener called whenever one of these resources changes. Usually it does not
	 * 		matter which of the resources changed as the listener just reads all resources and uses the
	 * 		actual values.
	 * @param appMan
	 * @param sAggParent parent of the new resources to be created (FloatResource into which the value
	 * 		calculated from the inputValues is written by the listener and the StatisticalAggregation).
	 *      If it has a sub resource "subEvals" of class Data this will be used.
	 * @param resultValueName Name of the resources to be created (StatisticalAggregation will have an
	 * 		additional "Evaluation").
	 * @param aggRes TODO: we have to be more flexible here
	 * @param complexProvider
	 */
	public void init(List<FloatResource> inputValues,
			ResourceValueListener<FloatResource> inputListener, ApplicationManager appMan, Resource sAggParent,
			String resultValueName, AggregationResolution aggRes, final ComplexStatisticalAggregationProvider complexProvider) {
		
		this.inputListener = inputListener;
		this.inputValues = inputValues;
		for(FloatResource res: inputValues) {
			res.addValueListener(inputListener, false);
		}
		Resource newResParent;
		Data subEvals = sAggParent.getSubResource("subEvals", Data.class);
		if(subEvals.exists()) {
			newResParent = subEvals;
		} else { 
			newResParent = sAggParent;
		}
		resultValue = newResParent.getSubResource(resultValueName, FloatResource.class);
		if(!resultValue.exists()) {
			resultValue.create();
			resultValue.setValue(0);
			resultValue.activate(false);
		}
		evaluation = newResParent.getSubResource(resultValueName+"Evaluation", StatisticalAggregation.class);
		if(aggRes == null) {
			if(!(sAggParent instanceof StatisticalAggregation)) {
				throw new IllegalArgumentException("Interval types have to be determined either by aggRes or by parent!");
			}
			StatisticalAggregation statConfigParent = (StatisticalAggregation)sAggParent;
			StatisticsHelper.copyIntervalResources(evaluation, statConfigParent);
		} else {
			StatisticsHelper.initStatisticalAggregation(evaluation, aggRes);
		}
		StatisticsHelper.initStatValue(evaluation.tenSecondValue());
		AbsoluteTimeHelper.initAlignedInterval(evaluation.minimalInterval(), true, evaluation);

		provider = new StatisticalAggregationProvider(resultValue, evaluation, appMan,
				new StatisticalAggregationCallbackTemplate() {
			@Override
			public float primaryIntervalEnd(
					FloatResource primaryAggregation, long absoluteTime, long timeStep) {
				//we use integral (over seconds), not average
				return primaryAggregation.getValue() * 0.001f;
			}
			@Override
			public void intervalUdpateFinished(int maxIntervalType,
					long absoluteTime,
					StatisticalAggregation aggregation) {
				if(complexProvider != null) {
					complexProvider.newSubAggValue(evaluation, maxIntervalType, absoluteTime);
				}
			}
		});
		provider.doIntegrate();
		inputListener.resourceChanged(inputValues.get(0));
	}
	@Override
	public void close() {
		for(FloatResource res: inputValues) {
			res.removeValueListener(inputListener);
		}
		provider.close();
	}
}
