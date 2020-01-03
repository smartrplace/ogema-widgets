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
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;

import de.iwes.util.format.StringFormatHelper;

/** The normal {@link StatisticalAggregationProvider} provides statistical evaluation of a single value resource,
 * typically a FloatResource. A complex statistical aggregation works on top of several FloatResources, e.g. 
 * a self consumption rate that needs information from PV plants, load meter, export meter or similar. The 
 * ComplexStatisticalAggregationProvider requires that all FloatResources that are used as a base of the
 * complex evaluation have a normal StatisticalAggregationProvider themselves.<br>
 * The ComplexStatisticalAggregationProvider requires that the result on each interval length can be calculated
 * based on the input aggregation values for the respective interval. This is the case for PV self consumption.
 * For average price per kWh drawn from grid with a generator on site a two step-approach has to be taken.
 * First a statistics of the cost of power drawn from grid by the loads considered has to be calculated
 * using an {@link IntegratingStatProvider} then this can be
 * used as an input to the ComplexStatisticalAggregationProvider calculating average prices per kWh.
 * @author dnestle
 *
 */
public class ComplexStatisticalAggregationProvider implements StatisticalProvider {
	private StatisticalAggregation aggregationResource;
	//private List<StatisticalAggregation> subAggregations;
	private ApplicationManager appMan;
	private OgemaLogger log;
	private ComplexStatisticalAggregationCallback callback;
	private boolean init = false;

	private class SourceData {
		public SourceData(StatisticalAggregation subAgg) {
			this.subAgg = subAgg;
		}
		public StatisticalAggregation subAgg;
		public int maxIntervalType = -1;
	}
	private List<SourceData> sourceDataList = new ArrayList<>();
	
	/** in some cases we need a reference before we can initialize, so call constructor and
	 * init as soon as possible*/
	public ComplexStatisticalAggregationProvider() {} ;
	
	public void init(
			StatisticalAggregation aggregationResource,
			List<StatisticalAggregation> subAggregations,
			ApplicationManager appMan, ComplexStatisticalAggregationCallback callback) {
		this.aggregationResource = aggregationResource;
		//this.subAggregations = subAggregations;
		this.appMan = appMan;
		log = appMan.getLogger();
		this.callback = callback;
		
		for(StatisticalAggregation sAgg: subAggregations) {
			sourceDataList.add(new SourceData(sAgg));
		}
		init = true;
	}
	
	/**Call this whenever a contributing StatisticalAggregation has been updated*/
	public void newSubAggValue(StatisticalAggregation subAgg, int maxIntervalType,
			long absoluteTime) {
		int maxTypeFound = 9999999;
		boolean allSet = true;
		if(!init) return;
		for(SourceData sd: sourceDataList) {
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
			for(SourceData sd: sourceDataList) {
				sd.maxIntervalType = -1;
			}
		}
	}
	
	private void checkNewIntervalUpdate(int maxIntervalType, long absoluteTime) {
		switch(maxIntervalType) {
		case 1:
		case 3:
		case 10:
			setResult(10, absoluteTime);
		case 100:
			setResult(100, absoluteTime);
		case 101:
			setResult(101, absoluteTime);
		case 1020:
			setResult(1020, absoluteTime);
			break;
		default:
			throw new IllegalArgumentException("Type does not fit:"+maxIntervalType);
		}
	}
	private void setResult(int type, long absoluteTime) {
		float val = callback.intervalUdpateFinished(type, absoluteTime, aggregationResource);
		FloatResource dest = StatisticsHelper.getIntervalTypeStatistics(type, aggregationResource);
		log.debug("CSP:Add:"+val+" to sch./type:"+type+" dest:"+dest.getLocation()+" time:"+StringFormatHelper.getTimeOfDayInLocalTimeZone(absoluteTime-100)+"/"+StringFormatHelper.getTimeOfDayInLocalTimeZone(appMan.getFrameworkTime()));	
		dest.setValue(val);
		dest.historicalData().addValue(absoluteTime, new FloatValue(val));
	}

	@Override
	public void close() {}
}
