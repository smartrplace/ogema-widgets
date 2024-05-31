package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface KnownIssueDataAggforLog extends PhysicalElement {
	/** Addition resource for logging */
	IntegerResource thermostatNum();
	IntegerResource wallThermostatNum();
	IntegerResource airconNum();
	IntegerResource windowSensNum();
	IntegerResource ccuHapNum();
	IntegerResource otherRealDeviceNum();
	IntegerResource roomNum();
	IntegerResource datapointNum();

	IntegerResource kniWoNotficationNum();
	IntegerResource kni3TimesSensNum();
	
	//High level evaluation
	IntegerResource kniDelayed1p();
	IntegerResource kniDelayed2p();
	IntegerResource kniAuto();
}
