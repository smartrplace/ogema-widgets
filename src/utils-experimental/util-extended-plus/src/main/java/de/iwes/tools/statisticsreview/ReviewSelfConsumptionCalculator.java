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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.joda.time.DateTimeZone;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.util.format.StringFormatHelper;
import de.iwes.util.resource.ScheduleHelper;
import de.iwes.util.resource.ScheduleHelper.ScheduleIntervalEvalResult;
import de.iwes.util.timer.AbsoluteTimeHelper;

/**Provides self consumption evaluation for a single generator connected to a local grid with a single
 * connection to the public grid
 * @author dnestle
 */
public class ReviewSelfConsumptionCalculator {
	final protected ExecutorService executor = Executors
			.newSingleThreadExecutor();
	Future<?> currentComputation;
	ApplicationManager appMan;
	OgemaLogger log;

	public ReadOnlyTimeSeries generatorPower;

	public ReadOnlyTimeSeries gridImport = null;

	public ReadOnlyTimeSeries gridExport = null;
	
	public ReadOnlyTimeSeries twoWayImport = null;
	
	public Schedule result;
	public Schedule autarkyQuote;
	
	int type;
	long startTime;
	long endTime;
	long maxOKInterval;
	public boolean acceptBadValues = false;
	/**If true the gridNetImport and export are considered consumption/generation register meter schedules*/
	public boolean registerMode = false;
	/**
	 * 
	 * @param generatorPowerValue
	 * @param gridNetImportValue This is the net import value, so this may be zero or negative!
	 * @param gridExport
	 * @param appMan
	 * @param autarkyQuote may be null if not required
	 * Note: currently not supported
	 */
	public ReviewSelfConsumptionCalculator(ReadOnlyTimeSeries generatorPower,ReadOnlyTimeSeries gridImport,
			ReadOnlyTimeSeries gridExport, ApplicationManager appMan, Schedule selfConsumptionResult,
			Schedule autarkyQuote, int timeIntervalLengthType, long maxOKInterval) {
		this.appMan = appMan;
		this.log = appMan.getLogger();
		this.generatorPower = generatorPower;
		this.gridImport = gridImport;
		this.twoWayImport = gridImport;
		this.gridExport = gridExport;
		this.result = selfConsumptionResult;
		this.autarkyQuote = autarkyQuote;
		this.type = timeIntervalLengthType;
		this.maxOKInterval = maxOKInterval;
	}
	public ReviewSelfConsumptionCalculator(ReadOnlyTimeSeries generatorPower, ReadOnlyTimeSeries twoWayImport,
			ApplicationManager appMan, Schedule selfConsumptionResult,
			Schedule autarkyQuote, int timeIntervalLengthType, long maxOKInterval) {
		this.appMan = appMan;
		this.log = appMan.getLogger();
		this.generatorPower = generatorPower;
		this.twoWayImport = twoWayImport;
		this.result = selfConsumptionResult;
		this.autarkyQuote = autarkyQuote;
		this.type = timeIntervalLengthType;
		this.maxOKInterval = maxOKInterval;
	}
	
	private long getStartTime(long earliestAllowed) {
		//SampledValue sv = generatorPower.getNextValue(0);
		List<SampledValue> ls = generatorPower.getValues(0);
		long t = appMan.getFrameworkTime(); 
		if(!ls.isEmpty()) {
			t = ls.get(0).getTimestamp();
		}
System.out.println("Start time 1:"+StringFormatHelper.getFullTimeDateInLocalTimeZone(t)+" Len:"+ls.size());
		if(earliestAllowed > t) {
			t = earliestAllowed;
		}
		ls = twoWayImport.getValues(0);
		long t2 = appMan.getFrameworkTime(); 
		if(!ls.isEmpty()) {
			t2 = ls.get(0).getTimestamp();
		}
System.out.println("Start time 2:"+StringFormatHelper.getFullTimeDateInLocalTimeZone(t2)+" Len:"+ls.size());
		if(t2 > t) {
			t = t2;
		}
		//t2 = gridExport.getNextValue(0).getTimestamp();
		//if(t2 > t) {
		//	t = t2;
		//}
		return t;
	}

	private void calculate(long latestAllowed) {
		List<SampledValue> generatorValues = generatorPower.getValues(startTime);
		List<SampledValue> twoWayImportValues = twoWayImport.getValues(startTime);
		//List<SampledValue> gridNetImportValues = gridNetImport.getValues(startTime);
		//List<SampledValue> gridExportValues = gridExport.getValues(startTime);
System.out.println("Gen#"+generatorValues.size()+" TwoWayImport#"+ twoWayImportValues.size()+ " start:"+StringFormatHelper.getFullTimeDateInLocalTimeZone(startTime));
		if(generatorValues.isEmpty() ||  twoWayImportValues.isEmpty() )  {
			endTime = -1;
			return;
		}
System.out.println("First time steps:"+StringFormatHelper.getFullTimeDateInLocalTimeZone(generatorValues.get(0).getTimestamp())+
		" / "+StringFormatHelper.getFullTimeDateInLocalTimeZone(twoWayImportValues.get(0).getTimestamp()));
		long t = generatorValues.get(generatorValues.size()-1).getTimestamp();
System.out.println("End time 1:"+StringFormatHelper.getFullTimeDateInLocalTimeZone(t));
		long t2 = twoWayImportValues.get(twoWayImportValues.size()-1).getTimestamp();
System.out.println("End time 2:"+StringFormatHelper.getFullTimeDateInLocalTimeZone(t2));
		if(t2 < t) {
			t = t2;
		}
		if(latestAllowed < t) {
			t = latestAllowed;
		}
		//t2 = gridExportValues.get(generatorValues.size()-1).getTimestamp();
		//if(t2 < t) {
		//	t = t2;
		//}
		endTime = t;
		
		if(!result.exists()) {
			result.create().activate(true);
		}
		
		long curTime = startTime;
		while(curTime <= endTime) {
			long nextCurTime = AbsoluteTimeHelper.addIntervalsFromAlignedTime(curTime, 1, DateTimeZone.getDefault().getID(),
					type);
			ScheduleIntervalEvalResult genVal = ScheduleHelper.getAverageFromGoodValues(generatorPower, curTime,
					nextCurTime, maxOKInterval, acceptBadValues, 0, false);
			float ePV = -genVal.average;

			ScheduleIntervalEvalResult exportVal;
			if(registerMode) {
				exportVal = ScheduleHelper.getAverageFromGoodValues(gridExport, curTime,
						nextCurTime, maxOKInterval, acceptBadValues , 0, true);				
			} else {
				exportVal = ScheduleHelper.getAverageFromGoodValues(twoWayImport, curTime,
					nextCurTime, maxOKInterval, acceptBadValues , -1, false);
			}
			float eGE = -exportVal.average;
			float val = 1-eGE/ePV;
			
			float autQ = 1;
			if(!Float.isNaN(val)) {
				if(val < 0) val = 0;
				if(val > 1.2) val = 1.2f;
				result.addValue(curTime, new FloatValue(val));
			}
			if(autarkyQuote != null) {
				ScheduleIntervalEvalResult netOrTotalImportVal;
				float eGI;
				if(registerMode) {
					netOrTotalImportVal = ScheduleHelper.getAverageFromGoodValues(gridImport, curTime,
							nextCurTime, maxOKInterval, acceptBadValues, 0, true);				
					eGI = netOrTotalImportVal.average;
				} else {
					netOrTotalImportVal = ScheduleHelper.getAverageFromGoodValues(twoWayImport, curTime,
						nextCurTime, maxOKInterval, acceptBadValues, 0, false);
					eGI = netOrTotalImportVal.average + eGE;
				}
				autQ =  1-eGI/(ePV-eGE+eGI);
				if(!Float.isNaN(autQ)) {
					if(autQ < 0) autQ = 0;
					if(autQ > 1.2) autQ = 1.2f;
					autarkyQuote.addValue(curTime, new FloatValue(autQ));
				}
			}
System.out.println("PVsc; "+StringFormatHelper.getDateInLocalTimeZone(curTime)+"; "+val+
		"; "+autQ+"; "+StringFormatHelper.getPerCent(genVal.badValueTime/(nextCurTime-curTime))+
		"; "+StringFormatHelper.getPerCent(exportVal.badValueTime/(nextCurTime-curTime)));
			curTime = nextCurTime;
		}
	}
	public void performCalculation() {
		performCalculation(-1, -1);
	}
	public void performCalculation(final long runStartTime, final long runEndtime) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
//for(int debugRep=0; debugRep<3; debugRep++) {
				try {
System.out.println("Starting PV eval thread...");
				startTime = AbsoluteTimeHelper.getIntervalStart(getStartTime(runStartTime),
							DateTimeZone.getDefault().getID(), type);
				calculate(runEndtime);
//				break;
				} catch(Exception e) {
					e.printStackTrace();
				}
//}			
				synchronized (executor) {
					currentComputation = null;
				}
			}
		};
		synchronized (executor) {
			log.info("PVSelfReview into "+result.getLocation()+" starting now ("+appMan.getFrameworkTime()+"...");
			currentComputation = executor.submit(r);
		}
	}
	
	//private float getPvSelfConsumptionQuote(long absoluteTime) {
		//return KPIHelpers.getPvSelfConsumptionQuote(
		//		1, 2, 3);
	//}
}
