package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.IntegerResource;

public interface KnownIssueDataGwAgg extends KnownIssueDataGw {
	IntegerResource replicationMissing();
	
	IntegerResource heartbeatFailed();
	
	IntegerResource alarmingDeactivated();

	/** Addition resource for logging */
	IntegerResource thermostatNum();
	IntegerResource wallThermostatNum();
	IntegerResource windowSensNum();
	IntegerResource airconNum();
	IntegerResource otherRealDeviceNum();
}
