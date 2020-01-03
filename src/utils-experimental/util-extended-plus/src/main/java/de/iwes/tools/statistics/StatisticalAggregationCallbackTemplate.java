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

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;

public abstract class StatisticalAggregationCallbackTemplate implements StatisticalAggregationCallback {

	@Override
	/**Adapt this to change / suppress certain incoming values*/
	public float valueChanged(FloatResource value,
			FloatResource primaryAggregation, long valueEndInstant,
			long timeStep) {
		return value.getValue();
	}
	
	@Override
	/**Adapt this to modify the calculation of the result based on the integrated source values
	 * @param primaryAggregation the resource into which the Statistical aggregation provider has 
	 * integrated the source values
	 * @param timeStep should be the duration of the last primary interval
	 * @return value to be written to primary aggregation schedule. If you do not want to average, but to
	 * integrate, modify this
	 */
	public float primaryIntervalEnd(FloatResource primaryAggregation,
			long absoluteTime, long timeStep) {
		return primaryAggregation.getValue() / timeStep;
	}
	
	@Override
	public void intervalUdpateFinished(int maxIntervalType, long absoluteTime,
			StatisticalAggregation aggregation) {}
}
