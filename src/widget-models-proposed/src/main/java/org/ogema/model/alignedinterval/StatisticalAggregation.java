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
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.alignedinterval;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/**Result of a single-output statistical evaluation. Note
 * that this model does not support all interval options defined in {@link TimeIntervalLength}. Some seem
 * to short, some seem too abundant for initial implementation.
 * The result of an evaluation is typically stored in historicalData() of the FloatResources created.*/
public interface StatisticalAggregation extends Data {
	/** create this element only if evaluations for each second-interval shall be calculated*/
	FloatResource secondValue();
	/** create this element only if evaluations for each ten-second-interval shall be calculated*/
	FloatResource tenSecondValue();
	/** create this element only if evaluations for each thirty-second-interval shall be calculated*/
	FloatResource halfMinuteValue();
	/** create this element only if evaluations for each one-minute-interval shall be calculated*/
	FloatResource minuteValue();
	/** create this element only if evaluations for each five-minute-interval shall be calculated*/
	FloatResource fiveMinuteValue();
	/** create this element only if evaluations for each ten-minute-interval shall be calculated*/
	FloatResource tenMinuteValue();
	/** create this element only if evaluations for each fifteen-minute-interval shall be calculated*/
	FloatResource fifteenMinuteValue();
	/** create this element only if evaluations for each thirty-minute-interval shall be calculated*/
	FloatResource halfHourValue();
	/** create this element only if evaluations for each one-hour-interval shall be calculated*/
	FloatResource hourValue();
	/** create this element only if evaluations for each four-hour-interval shall be calculated*/
	FloatResource fourHourValue();
	/** create this element only if evaluations for each one-day-interval shall be calculated*/
	FloatResource dayValue();
	/** create this element only if evaluations for each weekly interval shall be calculated*/
	FloatResource weekValue();
	/** create this element only if evaluations for each monthly interval shall be calculated*/
	FloatResource monthValue();
	/** create this element only if evaluations for each quarter-of-the-year-interval shall be calculated*/
	FloatResource quarterYearValue();
	/** create this element only if evaluations for each yearly interval shall be calculated*/
	FloatResource yearValue();
	
	/** Last time any aggregation field was updated*/
	TimeResource lastUpdate();
	/**persistent storage of last sensor value*/
	FloatResource lastValue();
	FloatResource primaryAggregation();
	/** The minimal interval must give the shortest time required to update all elements that exist
	 * and shall be udpated. It also defines alignment information for all elements.
	 */
	AlignedTimeIntervalLength minimalInterval();
}
