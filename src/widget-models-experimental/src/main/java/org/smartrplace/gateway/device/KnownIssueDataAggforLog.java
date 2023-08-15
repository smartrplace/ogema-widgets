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

}
