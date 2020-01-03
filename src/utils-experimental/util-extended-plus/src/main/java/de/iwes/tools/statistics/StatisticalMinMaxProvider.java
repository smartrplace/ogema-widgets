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
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.tools.resourcemanipulator.timer.CountDownAbsoluteTimer;

import de.iwes.util.format.StringFormatHelper;
import de.iwes.util.timer.AbsoluteTimeHelper;

public class StatisticalMinMaxProvider extends StatisticalAggregationProvider {
	int mode;
	/** Extension of {@link StatisticalAggregationProvider}. In this version the minimum or maximum of the values
	 * received is calculated. The mode may be extended in the future to implement other means to select a certain
	 * value based on some metrics from all values found during an interval.
	 * @param mode 1: minimum, 2: maximum
	 */
	public StatisticalMinMaxProvider(FloatResource dataSource,
			StatisticalAggregation aggregationResource, ApplicationManager appMan,
			StatisticalAggregationCallback callback, int mode) {
		super(dataSource, aggregationResource, appMan, callback);
		this.mode = mode;
		primaryAggregation.setValue(getPrimaryInitValue());		
	}
	
	protected float getPrimaryInitValue() {
		switch(mode) {
		case 1:
			return Float.MAX_VALUE;
		default:
			return -Float.MAX_VALUE;
		}		
	}

	@Override
	public void timerElapsed(CountDownAbsoluteTimer myTimer, long absoluteTime, long timeStep) {
		if((dataSource != null) && (!dataSource.exists())) {
			appMan.getLogger().warn("dataSource for statistical aggregation does not exist:"+dataSource.getLocation());
			return;
		}
		if(timeStep <= 0) {
			throw new IllegalStateException("Timer timeStep: "+timeStep+" for "+aggregationResource.getLocation());
		}
		updateValue(absoluteTime);
		float initialSumUp;
		if(callback != null) {
			initialSumUp = callback.primaryIntervalEnd(primaryAggregation, absoluteTime, timeStep);
		} else {
			initialSumUp = primaryAggregation.getValue();
			if(initialSumUp == getPrimaryInitValue()) {
				initialSumUp = Float.NaN;
			}
		}
//if(Float.isNaN(initialSumUp )) {
//	log.info("!!+NaN in timerElapsed for:"+primaryAggregation.getValue()+" / "+timeStep);					
//}
		//FloatResource prevDest = null;
		//int lastType = currentType;
		int lastType = performValueUpdates(absoluteTime, initialSumUp);
		if(callback != null) {
			callback.intervalUdpateFinished(lastType, absoluteTime, aggregationResource);
		}
		primaryAggregation.setValue(getPrimaryInitValue());
	}
	
	@Override
	protected int performValueUpdates(long absoluteTime, float newValue) {
		int currentType = primaryType;
		FloatResource prevDest = null;
		int lastType = currentType;
		float sumUp = newValue;
		while(currentType != 0) {
			FloatResource dest = StatisticsHelper.getIntervalTypeStatistics(currentType, aggregationResource);
			if(!dest.exists()) {
				currentType = StatisticsHelper.getUpperNextIntervalType(currentType);
				continue;
			}
			int count = 0;
			if(prevDest != null) {
				long lastValBeginning = AbsoluteTimeHelper.getIntervalStart(aggregationResource.lastUpdate().getValue(),
						timeZone, currentType);
				long newIntervalBeginning = AbsoluteTimeHelper.getIntervalStart(absoluteTime,
						timeZone, currentType);
				if(newIntervalBeginning == lastValBeginning) {
					//we do not have to do anything more
					break;
				}
				List<SampledValue> vals = prevDest.historicalData().getValues(lastValBeginning, absoluteTime);
				sumUp = getPrimaryInitValue();
				for(SampledValue sv: vals) {
					if(sv.getTimestamp() > lastValBeginning) {
						float newVal = sv.getValue().getFloatValue();
						switch(mode) {
						case 1:
							if(newVal < sumUp) sumUp = newVal;
							break;
						default:
							if(newVal > sumUp) sumUp = newVal;
							break;
						}
						count++;
					}
				}
				if(count > 0) {
				} else {
					throw new IllegalStateException("At least the value of the previous interval size needs to be found, prevDest:"+prevDest.getLocation());
				}
			}
			dest.setValue(sumUp);
			
if(prevDest != null) {
	
	log.debug("Add:"+sumUp+" to sch./type:"+currentType+" dest:"+dest.getLocation()+" time:"+StringFormatHelper.getTimeOfDayInLocalTimeZone(absoluteTime-100)+"/"+StringFormatHelper.getTimeOfDayInLocalTimeZone(appMan.getFrameworkTime())+" count:"+count);
} else {
	log.debug("Add:"+sumUp+" to sch./type:"+currentType+" dest:"+dest.getLocation()+" time:"+StringFormatHelper.getTimeOfDayInLocalTimeZone(absoluteTime-100)+"/"+StringFormatHelper.getTimeOfDayInLocalTimeZone(appMan.getFrameworkTime()));	
}
if(Float.isNaN(sumUp)) {
	log.info("!!++NaN in timerElapsed for:"+primaryAggregation.getLocation()+" / "+sumUp);			
}
			dest.historicalData().addValue(absoluteTime-100, new FloatValue(sumUp));
			//do not use weeks as base, only days, then again months
			if(currentType != 6) {
				prevDest = dest;
			}
			lastType = currentType;
			currentType = StatisticsHelper.getUpperNextIntervalType(currentType);
		}
		return lastType;
	}

	@Override
	protected void updateValue(long valueEndInstant) {
		float newVal = 0;
		boolean dataToProcess = false;
		if(callback != null) {
			newVal = callback.valueChanged(aggregationResource.lastValue(),
					primaryAggregation, valueEndInstant, Long.MIN_VALUE);
			dataToProcess = true;
		} else if(dataSource != null) {
			newVal = aggregationResource.lastValue().getValue();
			dataToProcess = true;
		}
		if(dataToProcess) switch(mode) {
		case 1:
			if(newVal < primaryAggregation.getValue()) {
				primaryAggregation.setValue(newVal);				
			}
			break;
		default:
			if(newVal > primaryAggregation.getValue()) {
				primaryAggregation.setValue(newVal);				
			}
		}
		lastDataValueCallback = valueEndInstant;
	}
}
