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

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;

import de.iwes.tools.statistics.StatisticsHelper.AggregationResolution;
import de.iwes.util.timer.AbsoluteTimeHelper;

/**Helper for super statistical providers like PowerDrawnFromGridStatProvider*/
@Deprecated
public class StatSuperInputDataHelper {
	public final FloatResource inputValue;
	final ApplicationManager appMan;
	public final AggregationResolution aggRes;
	public final StatisticalAggregationCallback callback;
	
	public final StatisticalAggregation evaluation;
	public StatisticalAggregationProvider provider;
	
	private boolean newValueAvailable = false;

	/**
	 * 
	 * @param inputValue
	 * @param aggregationResourceName
	 * @param appMan
	 * @param aggRes
	 * @param callback may null, then standard methods are used
	 */
	public StatSuperInputDataHelper(FloatResource inputValue, String aggregationResourceName,
			ApplicationManager appMan, AggregationResolution aggRes,
			final StatisticalAggregationCallback callback) {
		super();
		this.inputValue = inputValue;
		this.appMan = appMan;
		this.aggRes = aggRes;
		this.callback = callback;

		evaluation = inputValue.getSubResource(aggregationResourceName, StatisticalAggregation.class);
		StatisticsHelper.initStatisticalAggregation(evaluation, aggRes);
		StatisticsHelper.initStatValue(evaluation.tenSecondValue());
		AbsoluteTimeHelper.initAlignedInterval(evaluation.minimalInterval(), true, evaluation);
		provider = new StatisticalAggregationProvider(inputValue, evaluation,
				appMan, new StatisticalAggregationCallbackTemplate() {
			@Override
			public float primaryIntervalEnd(FloatResource primaryAggregation, long absoluteTime, long timeStep) {
				float locNetImport;
				if(callback == null) {
					locNetImport = super.primaryIntervalEnd(primaryAggregation, absoluteTime, timeStep);
				} else {
					locNetImport = callback.primaryIntervalEnd(primaryAggregation, absoluteTime, timeStep);
				}
				newValueAvailable = true;
				return locNetImport;
			}
		});
	}

	public boolean isNewValueAvailable() {
		return newValueAvailable;
	}

	public void confirmLastValueprocessed() {
		newValueAvailable = false;
	}
}
