package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.IntegerResource;

public interface KnownIssueDataGwAgg extends KnownIssueDataGw {
	IntegerResource replicationMissing();
	
	IntegerResource heartbeatFailed();
	
	IntegerResource alarmingDeactivated();

	KnownIssueDataAggforLog kniForLog();
}
