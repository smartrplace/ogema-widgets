package org.smartrplace.gateway.device;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface KnownIssueDataGw extends PhysicalElement {
	@NonPersistent
	IntegerResource activeAlarmSupervision();
	@NonPersistent
	IntegerResource datapointsInAlarmState();
	@NonPersistent
	IntegerResource datapointsTotal();
	@NonPersistent
	IntegerResource devicesTotal();
	
	@NonPersistent
	IntegerResource knownIssuesUnassigned();
	@NonPersistent
	IntegerResource knownIssuesAssignedOther();
	@NonPersistent
	IntegerResource knownIssuesAssignedOperationOwn();
	@NonPersistent
	IntegerResource knownIssuesAssignedDevOwn();
	@NonPersistent
	IntegerResource knownIssuesAssignedCustomer();
	
	@NonPersistent
	IntegerResource knownIssuesOpExternal();
	@NonPersistent
	IntegerResource knownIssuesDevExternal();
	
	@NonPersistent
	IntegerResource qualityShort();
	@NonPersistent
	IntegerResource qualityLong();
	@NonPersistent
	IntegerResource qualityShortGold();
	@NonPersistent
	IntegerResource qualityLongGold();
}
