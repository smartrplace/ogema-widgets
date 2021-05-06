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
    FloatResource pstTSServlet();
    FloatResource pstTSServletCounter();
}
