package org.smartrplace.alarming.escalation.model;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface AlarmingMessagingApp extends Data {
	
	@Override
	/** Human readable name*/
	StringResource name();
	
	StringResource appId();
	
	StringArrayResource usersForPushMessage();
	
	StringResource lastNameRegistered();
}
