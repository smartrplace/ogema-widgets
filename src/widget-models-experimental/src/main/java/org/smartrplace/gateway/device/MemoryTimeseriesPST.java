package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface MemoryTimeseriesPST extends PhysicalElement {
    /** Used in TimeseriesSetProcMultiToSingle#tsSingleLog*/
	FloatResource pstMultiToSingleEvents();
    FloatResource pstMultiToSingleCounter();
    /** Used in TimeseriesSetProcMultiToSingle#aggregateLog*/
    FloatResource pstMultiToSingleAggregations();
    FloatResource pstMultiToSingleAggregationsCounter();

    /** Used in ProcessedReadOnlyTimeSeries#locklLog*/
    FloatResource pstBlockingSingeEvents();
    FloatResource pstBlockingCounter();
    /** Used in ProcessedReadOnlyTimeSeries#subTsBuildLog*/
    FloatResource pstSubTsBuild();
    FloatResource pstSubTsBuildCounter();

    /** Used in ProcessedReadOnlyTimeSeries2*/
    @Deprecated
    FloatResource pstUpdateValuesPS2();
    @Deprecated
    FloatResource pstUpdateValuesPS2Counter();
    
    /** Currently not used*/
    @Deprecated
	FloatResource pstTSServlet();
    @Deprecated
    FloatResource pstTSServletCounter();
    
    /** For each job started the index is logged here at the time of start*/
    FloatResource jobIdxStarted();
    /** For each job finished the duration is logged here since the start*/
    FloatResource jobDuration();
    
    /** The relative amount of time used for jobs in the app is give here as 0.0..1.0
     * Logging is performed when a job is finished, but maximum every 1 minutes or adjustable fixed interval
     * The relation base is either the interval time since the last value was written or the double logging interval
     * 		whatever is shorter.
     */
    FloatResource jobLoadIncludingOverhead();
    FloatResource jobLoadWithoutOverhead();
    
    FloatResource hmDevicesLost();
    FloatResource hmDevicesLostHighPrio();
}
