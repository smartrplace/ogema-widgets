package de.iwes.timeseries.eval.garo.multibase;

import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.extended.MultiResult;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;

public interface KPIStatisticsManagementI {
	public ReadOnlyTimeSeries getIntervalSchedule(int intervalType);
	
	/** Provides last update times, times should refer to basic interval
	 * In the future a StringArrayResource may contain version Strings to identify the
	 * exact version of the evaluation, which requires effective versioning of these
	 * algorithms, though.
	 */
	public ReadOnlyTimeSeries getLastUpdateSchedule();
	
	public SampledValue getValue(int intervalType, long alignedNow, int intervalsIntoPast);
	public SampledValue getTimeOfCalculation(int intervalType, long alignedNow, int intervalsIntoPast);
		
	/** Use this if additional upper intervals shall be calculated for existing base interval values*/
	public void updateUpperTimeSteps(int baseType, int[] intervalTypesToUse);
	
	/** TODO: Make sure only completed intervals are written as we do not store values for ongoing intervals
	 * 
	 * @param result
	 * @param intervalTypesToUse
	 * @param subGW if null a summary KPI for all gateways in the result are calculated
	 */
	public void writeKPIs(AbstractSuperMultiResult<MultiResult> result, int[] intervalTypesToUse,
			String subGW);
	
	public int getBaseInterval();
	
	public SampledValue getValueNonAligned(int intervalType, long nonAlignedNow, int intervalsIntoPast);
	public SampledValue getTimeOfCalculationNonAligned(int intervalType, long nonAlignedNow, int intervalsIntoPast);
	public List<long[]> getGapTimes(int intervalType, long startTimeNonAligned, long endTimeNonAligned,
			boolean checkMoreResultsOfProvider);
	public String providerId();
	public String resultTypeId();
	public String specialLindeId();
	//The list must have corresponding indecies with List columns
	public List<KPIStatisticsManagementI> ksmList();

	/** Get String value for timestamp
	 * 
	 * @param timeStamp not required to be aligned
	 * @param intervalsIntoPast
	 * @return
	 */
	public String getStringValue(long timeStamp, int intervalsIntoPast);

	public GaRoSingleEvalProvider getEvalProvider();

	public void addKPIHelperForSameProvider(KPIStatisticsManagementI other);
	
	public void setBaseInterval(int intervalType);
	//Object must be of type EvalScheduler
	public void setScheduler(Object scheduler);
	
	public String evalConfigLocation();
}
