package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.IntegerResource;

public interface KnownIssueDataAggforLog {
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
