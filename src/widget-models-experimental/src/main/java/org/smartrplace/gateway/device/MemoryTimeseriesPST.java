package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface MemoryTimeseriesPST extends PhysicalElement {
    FloatResource pstMultiToSingleEvents();
    FloatResource pstMultiToSingleCounter();
    FloatResource pstMultiToSingleAggregations();
    FloatResource pstMultiToSingleAggregationsCounter();

    FloatResource pstBlockingSingeEvents();
    FloatResource pstBlockingCounter();
    FloatResource pstSubTsBuild();
    FloatResource pstSubTsBuildCounter();

    FloatResource pstUpdateValuesPS2();
    FloatResource pstUpdateValuesPS2Counter();
    
    /** Currently not used*/
    FloatResource pstTSServlet();
    FloatResource pstTSServletCounter();
    
    /** For each job started the index is logged here at the time of start*/
    FloatResource jobIdxStarted();
    /** For each job finished the duration is logged here since the start*/
    FloatResource jobDuration();
    /** The relative amount of time used for jobs in the app is give here as 0.0..1.0
     * Logging is performed when a job is finished, but maximum every 5 minutes or adjustable fixed interval
     */
    FloatResource jobLoad();
    
    FloatResource hmDevicesLost();
    FloatResource hmDevicesLostHighPrio();
}
