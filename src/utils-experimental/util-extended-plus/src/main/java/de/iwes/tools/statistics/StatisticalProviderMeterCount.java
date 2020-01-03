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
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.tools.resourcemanipulator.timer.CountDownAbsoluteTimer;

import de.iwes.util.format.StringFormatHelper;
import de.iwes.util.timer.AbsolutePersistentTimer;
import de.iwes.util.timer.AbsoluteTimeHelper;
import de.iwes.util.timer.AbsoluteTimerListener;

/**Evaluates a meter counter into StatisticalAggregation. For an energy meter kWh or J for each
 * time interval would be the result.*/
public class StatisticalProviderMeterCount implements AbsoluteTimerListener, StatisticalProvider {
	private StatisticalAggregation aggregationResource;
	private FloatResource primaryPreviousValue;
	int primaryType;

	private FloatResource dataSource;
	
	private AbsolutePersistentTimer absTimer;
	private ApplicationManager appMan;
	private OgemaLogger log;
	private final StatisticalAggregationCallback callback;
	final private String timeZone;
	
	//private boolean integrate = false;

	/** In the integrating mode each interval value does not contain the average of
	 * the lower intervals, but the integral. This requires that the newValue method
	 * in the callback template is overwritten also to to avoid division by the
	 * timeStep.
	 */
	//public boolean isIntegrating() {
	//	return integrate;
	//}

	//public void doIntegrate() {
	//	this.integrate = true;
	//}

	public void close() {
		absTimer.stop();
	}
	
	/** Constructor
	 * @param dataSource if null the input values have to be provided via callback.valueChanged
	 * @param aggregationResource
	 * @param appMan
	 * @param callback if null a standard sum up will be performed instead of the callback. The method primaryIntervalEnd
	 * is not used here
	 */
	public StatisticalProviderMeterCount(FloatResource dataSource,
			StatisticalAggregation aggregationResource, ApplicationManager appMan,
			StatisticalAggregationCallback callback) {
		this.aggregationResource = aggregationResource;
		this.dataSource = dataSource;
		this.appMan = appMan;
		log = appMan.getLogger();
		this.callback = callback;
		
		primaryPreviousValue = aggregationResource.primaryAggregation();
		if(!primaryPreviousValue.exists()) {
			primaryPreviousValue.create();
			primaryPreviousValue.activate(false);
		}
		primaryType = aggregationResource.minimalInterval().timeIntervalLength().type().getValue();
		timeZone = aggregationResource.minimalInterval().timeZone().getValue();
		
		//avoid large initial runs
		//if(aggregationResource.lastUpdate().exists()) {
		//	jumpOverLongGap(primaryType, 10);
		//}
		absTimer = new AbsolutePersistentTimer(aggregationResource.lastUpdate(),
				aggregationResource.minimalInterval(), this, appMan);
		//if(dataSource != null) {
		//	dataSource.addValueListener(this);
		//}
		//int minType = aggregationResource.minimalInterval().timeIntervalLength().type().getValue();
		//primaryAggregation = StatisticsHelper.getIntervalTypeStatistics(minType, aggregationResource);
		primaryPreviousValue.setValue(getCurrentValue());
		log.info("Finished SAP_MeterCount constructor at:"+StringFormatHelper.getTimeOfDayInLocalTimeZone(appMan.getFrameworkTime()));	
	}
	
	/*private void jumpOverLongGap(int startType, float maxSteps) {
		long lastUpdate = aggregationResource.lastUpdate().getValue();
		int currentType = startType;
		List<Integer> subTypes = new ArrayList<>();
		while(currentType != 0) {
			FloatResource dest = StatisticsHelper.getIntervalTypeStatistics(currentType, aggregationResource);
			if(!dest.exists()) {
				currentType = StatisticsHelper.getUpperNextIntervalType(currentType);
				continue;
			}
			if(lastDataValueCallback - lastUpdate < maxSteps*AbsoluteTimeHelper.getStandardInterval(currentType)) {
				lastUpdate = jumpOverGapSingleType(currentType, lastUpdate);
				while(!subTypes.isEmpty()) {
					lastUpdate = jumpOverGapSingleType(subTypes.remove(subTypes.size()-1), lastUpdate);
				}
				break;
			}
			subTypes.add(currentType);
			currentType = StatisticsHelper.getUpperNextIntervalType(currentType);
		}
		aggregationResource.lastUpdate().setValue(lastUpdate);
	}
	private long jumpOverGapSingleType(int currentType, long lastUpdate) {
		while(lastDataValueCallback - lastUpdate > AbsoluteTimeHelper.getStandardInterval(currentType)) {
			lastUpdate = AbsoluteTimeHelper.getNextStepTime(lastUpdate, timeZone, currentType);
			performValueUpdates(lastUpdate, 0);
		}
		return lastUpdate;
	}*/
	
	private float getCurrentValue() {
		if((callback != null)&&(dataSource == null)) {
			return callback.valueChanged(null, primaryPreviousValue, -1, -1);
		} else {
			return dataSource.getValue();
		}
	}

	@Override
	public void timerElapsed(CountDownAbsoluteTimer myTimer, long absoluteTime, long timeStep) {
		if(timeStep <= 0) {
			throw new IllegalStateException("Timer timeStep: "+timeStep+" for "+aggregationResource.getLocation());
		}
		//updateValue(absoluteTime);
		float initialSumUp;
		float currentValue = getCurrentValue();
		initialSumUp = currentValue - primaryPreviousValue.getValue();
if(Float.isNaN(initialSumUp )) {
	log.info("!!+NaN in timerElapsed for:"+currentValue+" / "+primaryPreviousValue.getValue());					
}
		//FloatResource prevDest = null;
		//int lastType = currentType;
		int lastType = performValueUpdates(absoluteTime, initialSumUp);
		if(callback != null) {
			callback.intervalUdpateFinished(lastType, absoluteTime, aggregationResource);
		}
		primaryPreviousValue.setValue(currentValue);
	}
	
	private int performValueUpdates(long absoluteTime, float newValue) {
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
				//SampledValue lastVal = dest.historicalData().getValue(absoluteTime);
				//if(lastVal != null) {
				//	long lastValBeginning = AbsoluteTimeHelper.getIntervalStart(lastVal.getTimestamp(),
				//			aggregationResource.minimalInterval());
				//}
				long newIntervalBeginning = AbsoluteTimeHelper.getIntervalStart(absoluteTime,
						timeZone, currentType);
//System.out.println("Begin-dif:"+(newIntervalBeginning-lastValBeginning));
				if(newIntervalBeginning == lastValBeginning) {
					//we do not have to do anything more
					break;
				}
				List<SampledValue> vals = prevDest.historicalData().getValues(lastValBeginning, absoluteTime);
				sumUp = 0;
				for(SampledValue sv: vals) {
					if(sv.getTimestamp() > lastValBeginning) {
						sumUp += sv.getValue().getFloatValue();
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
	log.info("!!++NaN in timerElapsed for:"+primaryPreviousValue.getLocation()+":"+(getCurrentValue()-primaryPreviousValue.getValue())+" / "+sumUp);			
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

	/*private void updateValue(long valueEndInstant) {
		long delta = valueEndInstant - lastDataValueCallback;
		float newVal;
		if(callback != null) {
			newVal = callback.valueChanged(aggregationResource.lastValue(),
					primaryAggregation, valueEndInstant, delta);
		} else {
			newVal = aggregationResource.lastValue().getValue();
		}
		primaryAggregation.setValue(primaryAggregation.getValue() + 
				newVal*delta);
		lastDataValueCallback = valueEndInstant;
	}*/
}
