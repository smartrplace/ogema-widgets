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

public interface StatisticalAggregationCallback {
	/**
	 * 
	 * @param value
	 * @param primaryAggregation usually not required, only to check current aggregation value, e.g. for
	 * 			a min/max evaluation
	 * @param absoluteTime
	 * @param timeStep note: in most cases timeStep is not required as the value returned will be
	 * 		multiplied by timeStep by the StatisticalAggregationProvider itself before adding it
	 * 		to the primaryAggregation.
	 * @return
	 */
	float valueChanged(FloatResource value, FloatResource primaryAggregation, long absoluteTime, long timeStep);
	
	/**
	 * 
	 * @param primaryAggregation
	 * @param absoluteTime
	 * @param timeStep duration of entire primary interval
	 * @return primary average value that shall be written into historical data schedule
	 */
	float primaryIntervalEnd(FloatResource primaryAggregation, long absoluteTime, long timeStep);
	
	/** Notification that intervals are finished for further processing, e.g. calculation of
	 * standard deviation based on average and quadratic average
	 * @param maxIntervalType
	 * @param aggregation
	 */
	void intervalUdpateFinished(int maxIntervalType, long absoluteTime, StatisticalAggregation aggregation);
}
