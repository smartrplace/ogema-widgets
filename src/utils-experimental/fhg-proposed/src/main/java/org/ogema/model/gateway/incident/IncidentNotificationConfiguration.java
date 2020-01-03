package org.ogema.model.gateway.incident;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.prototypes.Configuration;

public interface IncidentNotificationConfiguration extends Configuration {
	//UserConfig priorityType();
	IncidentNotificationTriggerConfiguration baseTrigger();
	IncidentNotificationTriggerConfiguration resendTrigger();
	IncidentNotificationTriggerConfiguration summaryInclusionTrigger();
	BooleanResource sendWithSummaryBase();
	BooleanResource sendWithSummaryResend();	
}
