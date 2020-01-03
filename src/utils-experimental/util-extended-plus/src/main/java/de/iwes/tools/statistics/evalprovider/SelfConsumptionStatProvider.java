/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.tools.statistics.evalprovider;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.prototypes.Data;

import de.iwes.tools.statistics.ComplexStatisticalAggregationCallback;
import de.iwes.tools.statistics.ComplexStatisticalAggregationProvider;
import de.iwes.tools.statistics.StatisticalAggregationCallbackTemplate;
import de.iwes.tools.statistics.StatisticalAggregationProvider;
import de.iwes.tools.statistics.StatisticsHelper;
import de.iwes.tools.statistics.StatisticsHelper.AggregationResolution;
import de.iwes.util.timer.AbsoluteTimeHelper;

/**Provides self consumption evaluation for a single generator connected to a local grid with a single
 * connection to the public grid
 * @author dnestle
 */
public class SelfConsumptionStatProvider {
	public final StatisticalAggregation generatorNetImportEvaluation;
	public StatisticalAggregationProvider generatorNetImportProvider;

	public final StatisticalAggregation gridNetImportEvaluation;
	public StatisticalAggregationProvider gridNetImportProvider;

	public final StatisticalAggregation gridExportEvaluation;
	public StatisticalAggregationProvider gridExportProvider;
	
	public final StatisticalAggregation selfConsumptionEvaluation;
	public ComplexStatisticalAggregationProvider complexProvider;
	
	/**
	 * 
	 * @param generatorPowerValue
	 * @param gridNetImportValue This is the net import value, so this may be zero or negative!
	 * @param gridExportValue
	 * @param appMan
	 */
	public SelfConsumptionStatProvider(FloatResource generatorPowerValue, FloatResource gridNetImportValue,
			FloatResource gridExportValue, ApplicationManager appMan, StatisticalAggregation selfConsumptionEvaluation,
			AggregationResolution aggRes) {
		this.selfConsumptionEvaluation = selfConsumptionEvaluation;
		Data subEvals = selfConsumptionEvaluation.getSubResource("subEvals", Data.class);
		if(!subEvals.isActive()) {
			subEvals.create().activate(false);
		}
		generatorNetImportEvaluation = subEvals.getSubResource("genImportEvaluation", StatisticalAggregation.class);

		initSubProvider(generatorNetImportEvaluation, aggRes);
		generatorNetImportProvider = new StatisticalAggregationProvider(generatorPowerValue, generatorNetImportEvaluation,
				appMan, new StatisticalAggregationCallbackTemplate() {
			@Override
			public void intervalUdpateFinished(int maxIntervalType, long absoluteTime,
					StatisticalAggregation aggregation) {
				newSubAggValue(generatorNetImportEvaluation, maxIntervalType, absoluteTime);
			}
		});
		
		gridNetImportEvaluation = subEvals.getSubResource("gridNetImportEvaluation", StatisticalAggregation.class);
		initSubProvider(gridNetImportEvaluation, aggRes);
		gridNetImportProvider = new StatisticalAggregationProvider(gridNetImportValue, gridNetImportEvaluation,
				appMan, new StatisticalAggregationCallbackTemplate() {
			@Override
			public void intervalUdpateFinished(int maxIntervalType, long absoluteTime,
					StatisticalAggregation aggregation) {
				newSubAggValue(gridNetImportEvaluation, maxIntervalType, absoluteTime);
			}
		});

		gridExportEvaluation = subEvals.getSubResource("exportEvaluation", StatisticalAggregation.class);
		initSubProvider(gridExportEvaluation, aggRes);
		gridExportProvider = new StatisticalAggregationProvider(gridExportValue, gridExportEvaluation,
				appMan, new StatisticalAggregationCallbackTemplate() {
			@Override
			public float valueChanged(FloatResource value,
					FloatResource primaryAggregation, long valueEndInstant,
					long timeStep) {
				if(value.getValue() < 0) {
					return value.getValue();
				} else {
					return 0;
				}
			}
			@Override
			public void intervalUdpateFinished(int maxIntervalType, long absoluteTime,
					StatisticalAggregation aggregation) {
				newSubAggValue(gridExportEvaluation, maxIntervalType, absoluteTime);
			}
		});
		
		//selfConsumptionEvaluation = sAggParent.getSubResource("selfConsumptionEvaluation", StatisticalAggregation.class);
		if(aggRes != null) {
			StatisticsHelper.initStatisticalAggregation(selfConsumptionEvaluation, aggRes);
		}

		List<StatisticalAggregation> subAggregations = new ArrayList<>();
		subAggregations.add(generatorNetImportEvaluation);
		subAggregations.add(gridNetImportEvaluation);
		subAggregations.add(gridExportEvaluation);
		complexProvider = new ComplexStatisticalAggregationProvider();
		complexProvider.init(selfConsumptionEvaluation, subAggregations, appMan,
				new ComplexStatisticalAggregationCallback() {
			@Override
			public float intervalUdpateFinished(int maxIntervalType, long absoluteTime,
					StatisticalAggregation aggregation) {
				float val = getPvSelfConsumptionQuote(maxIntervalType, absoluteTime);
				if(Float.isNaN(val)) return 0;
				if(val < -0.1) return -0.1f;
				if(val > 1.2) return 1.2f;
				return val;
			}
		});
	}
	
	private void initSubProvider(StatisticalAggregation subAgg, AggregationResolution aggRes) {
		if(aggRes == null) {
			StatisticsHelper.copyIntervalResources(subAgg, selfConsumptionEvaluation);
		} else {
			StatisticsHelper.initStatisticalAggregation(subAgg, aggRes);
		}
		StatisticsHelper.initStatValue(subAgg.tenSecondValue());
		AbsoluteTimeHelper.initAlignedInterval(subAgg.minimalInterval(), true, subAgg);
		
	}
	
	private float getPvSelfConsumptionQuote(int type, long absoluteTime) {
		return KPIHelpers.getPvSelfConsumptionQuote(
				StatisticsHelper.getIntervalTypeStatistics(type, generatorNetImportEvaluation),
				StatisticsHelper.getIntervalTypeStatistics(type, gridNetImportEvaluation),
						StatisticsHelper.getIntervalTypeStatistics(type, gridExportEvaluation));
	}

	public void newSubAggValue(StatisticalAggregation subAgg, int maxIntervalType, long absoluteTime) {
		if(complexProvider != null)
			complexProvider.newSubAggValue(subAgg, maxIntervalType, absoluteTime);
	}
}
