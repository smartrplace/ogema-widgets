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
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.model.alignedinterval.StatisticalAggregation;

import de.iwes.util.timer.AbsoluteTimeHelper;

public class StatisticsHelper {
	public enum AggregationResolution {
		DEBUG_FAST,
		MINUTE_RESOLUTION,
		HOUR_RESOLUTION,
		DAY_RESOLUTION,
		PRE_INIT
	}
	/**return true when resource was newly created*/
	public static boolean initStatValue(FloatResource val) {
		boolean res = !val.historicalData().exists();
		val.create();
		val.historicalData().create();
		val.historicalData().setInterpolationMode(InterpolationMode.NEAREST);
		return res;
	}
	public static boolean initStatisticalAggregation(StatisticalAggregation sAgg, int[] intervalTypes) {
		boolean activate = false;
		for(int type: intervalTypes) {
			if(StatisticsHelper.addIntervalType(sAgg, type)) activate = true;
		}
		StatisticsHelper.initStatisticalAggregation(sAgg, AggregationResolution.PRE_INIT);
		return activate;

	}
	public static void initStatisticalAggregation(StatisticalAggregation sAgg, AggregationResolution mode) {
		switch(mode) {
		case DEBUG_FAST:
			initStatValue(sAgg.tenSecondValue());
			//fallthrough
		case MINUTE_RESOLUTION:
			initStatValue(sAgg.minuteValue());
			//fallthrough
		case HOUR_RESOLUTION:
			initStatValue(sAgg.hourValue());
			//fallthrough
		case DAY_RESOLUTION:
			initStatValue(sAgg.yearValue());
			initStatValue(sAgg.monthValue());
			initStatValue(sAgg.weekValue());
			initStatValue(sAgg.dayValue());	
		default: //do nothing
		}
		AbsoluteTimeHelper.initAlignedInterval(sAgg.minimalInterval(), true, sAgg);
	}
	
	public static boolean addIntervalType(StatisticalAggregation sAgg, int intervalType) {
		return initStatValue(AbsoluteTimeHelper.getIntervalTypeStatistics(intervalType, sAgg));
	}
	
	/**
	 * @deprecated use {@link AbsoluteTimeHelper#getUpperNextIntervalType(int)}
	 */
	public static int getUpperNextIntervalType(int intervalType) {
		return AbsoluteTimeHelper.getUpperNextIntervalType(intervalType);
	}
	
	/**
	 * @deprecated use {@link AbsoluteTimeHelper#getIntervalTypeStatistics(int, StatisticalAggregation)}
	 */
	public static FloatResource getIntervalTypeStatistics(int intervalType, StatisticalAggregation sAgg) {
		return AbsoluteTimeHelper.getIntervalTypeStatistics(intervalType, sAgg);
		
	}
	public static void copyIntervalResources(StatisticalAggregation destination, StatisticalAggregation source) {
		if(source.tenSecondValue().exists()) {
			initStatValue(destination.tenSecondValue());
		}
		if(source.minuteValue().exists()) {
			initStatValue(destination.minuteValue());
		}
		if(source.fiveMinuteValue().exists()) {
			initStatValue(destination.fiveMinuteValue());
		}
		if(source.tenMinuteValue().exists()) {
			initStatValue(destination.tenMinuteValue());
		}
		if(source.fifteenMinuteValue().exists()) {
			initStatValue(destination.fifteenMinuteValue());
		}
		if(source.halfHourValue().exists()) {
			initStatValue(destination.halfHourValue());
		}
		if(source.hourValue().exists()) {
			initStatValue(destination.hourValue());
		}
		if(source.fourHourValue().exists()) {
			initStatValue(destination.fourHourValue());
		}
		if(source.dayValue().exists()) {
			initStatValue(destination.dayValue());
		}
		if(source.weekValue().exists()) {
			initStatValue(destination.weekValue());
		}
		if(source.monthValue().exists()) {
			initStatValue(destination.monthValue());
		}
		if(source.quarterYearValue().exists()) {
			initStatValue(destination.quarterYearValue());
		}
		if(source.yearValue().exists()) {
			initStatValue(destination.yearValue());
		}
	}
}
