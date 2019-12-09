package org.ogema.messaging.msgpublisher;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface MsgPublisherConfig {

	@AttributeDefinition(description = "Maximum lifetime of remote message resources in days. If this is <= 0, then "
		+ " the message resources are kept indefinitely.", defaultValue = "30")
	long daysToKeepMessages() default 30;
	@AttributeDefinition(description = "Maximum number of remote message resources to keep. If this is <= 0, then "
			+ " no limit is imposed", defaultValue = "100")
	int maxMessagesToKeep() default 100;
	
}
