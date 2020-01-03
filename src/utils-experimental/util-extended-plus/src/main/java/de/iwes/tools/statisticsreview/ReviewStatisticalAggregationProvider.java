/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.tools.statisticsreview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;

import de.iwes.tools.statistics.StatisticsHelper;
import de.iwes.util.format.StringFormatHelper;

/** The review statistical aggregation provider allows to read data from input schedules and provide
 * averages for intervals as input data to a statistical evaluation
 * Note: Doe not do anything meaningful yet!
 * @author dnestle
 * 
 */
@Deprecated
public class ReviewStatisticalAggregationProvider {
	private StatisticalAggregation aggregationResource;
	//private List<StatisticalAggregation> subAggregations;
	private ApplicationManager appMan;
	private OgemaLogger log;
	private ReviewAggregationCallback callback;
	private boolean init = false;
	final protected ExecutorService executor = Executors
			.newSingleThreadExecutor();
	Future<?> currentComputation;

	private class ReviewSourceData {
		public ReviewSourceData(Schedule inputData) {
			this.subAgg = subAgg;
		}
		public Schedule subAgg;
		public int maxIntervalType = -1;
	}
	private List<ReviewSourceData> sourceDataList = new ArrayList<>();
	
	/** in some cases we need a reference before we can initialize, so call constructor and
	 * init as soon as possible. After call of init the review is processed. The class does nothing
	 * more before the next call of init*/
	public ReviewStatisticalAggregationProvider() {} ;
	
	public void init(
			StatisticalAggregation aggregationResource,
			List<Schedule> subAggregations,
			ApplicationManager appMan, ReviewAggregationCallback callback) {
		this.aggregationResource = aggregationResource;
		//this.subAggregations = subAggregations;
		this.appMan = appMan;
		log = appMan.getLogger();
		this.callback = callback;
		
		for(Schedule sAgg: subAggregations) {
			sourceDataList.add(new ReviewSourceData(sAgg));
		}
		init = true;
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				synchronized (executor) {
					currentComputation = null;
				}
			}
		};
		synchronized (executor) {
			log.info("Review into "+aggregationResource.getLocation()+" starting now ("+appMan.getFrameworkTime()+"...");
			currentComputation = executor.submit(r);
		}

	}
	
	public void newSubAggValue(StatisticalAggregation subAgg, int maxIntervalType,
			long absoluteTime) {
		int maxTypeFound = 9999999;
		boolean allSet = true;
		if(!init) return;
		for(ReviewSourceData sd: sourceDataList) {
			if(sd.subAgg.equals(subAgg)) {
				sd.maxIntervalType = maxIntervalType;
				if(!allSet) return;
			}
			if((sd.maxIntervalType > 0)&&(sd.maxIntervalType < maxTypeFound)) {
				maxTypeFound = sd.maxIntervalType;
			}
			if(sd.maxIntervalType < 0) {
				allSet = false;
			}
		}
		if(allSet) {
			checkNewIntervalUpdate(maxTypeFound, absoluteTime);
			for(ReviewSourceData sd: sourceDataList) {
				sd.maxIntervalType = -1;
			}
		}
	}
	
	private void checkNewIntervalUpdate(int maxIntervalType, long absoluteTime) {
		switch(maxIntervalType) {
		case 1:
		case 3:
		case 10:
			setAutarkyQuote(10, absoluteTime);
		case 100:
			setAutarkyQuote(100, absoluteTime);
		case 101:
			setAutarkyQuote(101, absoluteTime);
		case 1020:
			setAutarkyQuote(1020, absoluteTime);
			break;
		default:
			throw new IllegalArgumentException("Type does not fit:"+maxIntervalType);
		}
	}
	private void setAutarkyQuote(int type, long absoluteTime) {
		List<Float> valueList = new ArrayList<>();
		for(ReviewSourceData s: sourceDataList) {
			valueList.add(s.subAgg.getValue(absoluteTime).getValue().getFloatValue());
		}
		float val = callback.intervalUdpateFinished(type, absoluteTime, valueList);
		FloatResource dest = StatisticsHelper.getIntervalTypeStatistics(type, aggregationResource);
		log.debug("CSP:Add:"+val+" to sch./type:"+type+" dest:"+dest.getLocation()+" time:"+StringFormatHelper.getTimeOfDayInLocalTimeZone(absoluteTime-100)+"/"+StringFormatHelper.getTimeOfDayInLocalTimeZone(appMan.getFrameworkTime()));	
		dest.setValue(val);
		dest.historicalData().addValue(absoluteTime, new FloatValue(val));
	}
}
