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

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.prototypes.Data;

import de.iwes.tools.statistics.StatisticsHelper.AggregationResolution;
import de.iwes.tools.statistics.evalprovider.CostDrawnFromGridStatProvider;
import de.iwes.tools.statistics.evalprovider.EnergyDrawnFromGridStatProvider;

/**Provides evaluation of the average price per kWh drawn from grid. Evaluation is done based on ten second averages
 * of the base values
 * @author dnestle
 */
public class AverageConsumptionPriceStatProvider {
	//public final StatisticalAggregation gridNetImportEvaluation;
	//public StatisticalAggregationProvider gridNetImportProvider;

	//public final StatisticalAggregation gridPriceEvaluation;
	//public StatisticalAggregationProvider gridPriceProvider;
	
	public final StatisticalAggregation avPriceEvaluation;
	public ComplexStatisticalAggregationProvider complexProvider;
	
	//public final FloatResource avPriceValue;
	public final CostDrawnFromGridStatProvider costProvider;
	public final EnergyDrawnFromGridStatProvider energyProvider;
	public final IntegralStatProvider totalEnergyProvider;
	
	/**
	 * 
	 * @param generatorPowerValue
	 * @param gridNetImportValue This is the net import value, so this may be zero or negative!
	 * @param gridPriceValue
	 * @param appMan
	 * @param aggRes should be null if avPriceEvaluation is initialized, otherwise the elements of aggRes are
	 * 			added to avPriceEvaluation 
	 * @param pvInFeedTariff incentive for feeding PV power into the grid, unit must be the same as
	 * 			price values. May be zero or even negative if PV-infeed is punished
	 */
	public AverageConsumptionPriceStatProvider(FloatResource loadValue, FloatResource gridNetImportValue,
			FloatResource gridPriceValue, ApplicationManager appMan, StatisticalAggregation avPriceEvaluation,
			AggregationResolution aggRes, final float pvInFeedTariff) {
		complexProvider = new ComplexStatisticalAggregationProvider();
		this.avPriceEvaluation = avPriceEvaluation;
		Data subEvals = avPriceEvaluation.getSubResource("subEvals", Data.class);
		if(!subEvals.isActive()) {
			subEvals.create().activate(false);
		}
		costProvider = new CostDrawnFromGridStatProvider(loadValue, gridNetImportValue, gridPriceValue,
				appMan, avPriceEvaluation, aggRes, complexProvider);
		energyProvider = new EnergyDrawnFromGridStatProvider(loadValue, gridNetImportValue,
				appMan, avPriceEvaluation, aggRes, complexProvider);
		totalEnergyProvider = new IntegralStatProvider(loadValue,
				appMan, avPriceEvaluation, aggRes, complexProvider);
		
		//avPriceEvaluation = sAggParent.getSubResource("avPriceEvaluation", StatisticalAggregation.class);
		if(aggRes != null) {
			StatisticsHelper.initStatisticalAggregation(avPriceEvaluation, aggRes);
		}
		List<StatisticalAggregation> subAggregations = new ArrayList<>();
		subAggregations.add(costProvider.evaluation);
		subAggregations.add(energyProvider.evaluation);
		complexProvider.init(avPriceEvaluation, subAggregations, appMan,
				new ComplexStatisticalAggregationCallback() {
			@Override
			public float intervalUdpateFinished(int maxIntervalType, long absoluteTime,
					StatisticalAggregation aggregation) {
				FloatResource loadFromGrid = StatisticsHelper.getIntervalTypeStatistics(
						maxIntervalType, energyProvider.evaluation);
				FloatResource cost = StatisticsHelper.getIntervalTypeStatistics(
						maxIntervalType, costProvider.evaluation);
				FloatResource totalEnergy = StatisticsHelper.getIntervalTypeStatistics(
						maxIntervalType, totalEnergyProvider.evaluation);
				float eCoveredByPV = totalEnergy.getValue() - loadFromGrid.getValue();
				if(totalEnergy.getValue() == 0) {
					float val = (cost.getValue()+pvInFeedTariff*eCoveredByPV) /
							totalEnergy.getValue();
					System.out.println("val after division by zero:"+val);
					return 0;
				}
				return (cost.getValue()+pvInFeedTariff*eCoveredByPV) /
						totalEnergy.getValue();
			}
		});
	}
	
	public static float getAveragePriceOfLoad(Schedule price, Schedule load, long startTime, long endTime,
			long timeStep) {
		float weightedPrice = 0;
		float powerSum = 0;
		
		for(long t=startTime; t<=endTime; t+=timeStep) {
			SampledValue priceV = price.getValue(t);
			SampledValue powerV = load.getValue(t);
			if(priceV == null || powerV == null) {
				if((endTime - t) > 0.1*(endTime - startTime)) {
					throw new IllegalArgumentException("Schedule(s) are much too short!");
				}
				break;
			}
			weightedPrice += priceV.getValue().getFloatValue()*
					powerV.getValue().getFloatValue();
			powerSum += powerV.getValue().getFloatValue();
		}
		return weightedPrice/powerSum;
	}
}
