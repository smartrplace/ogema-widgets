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

import org.ogema.model.alignedinterval.StatisticalAggregation;

public interface ComplexStatisticalAggregationCallback {
	/** Notification that intervals are finished for further processing, e.g. calculation of
	 * standard deviation based on average and quadratic average
	 * @param intervalType this is a single aggregation type to be evaluated. See example in
	 * application pv-selfconsume, method connectPVToMeter
	 * @param aggregation
	 * @return 
	 */
	float intervalUdpateFinished(int intervalType, long absoluteTime, StatisticalAggregation aggregation);
}
