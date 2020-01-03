package org.ogema.model.gateway.incident;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.alignedinterval.TimeIntervalLength;
import org.ogema.model.prototypes.Configuration;

/**Information on triggers defined. Note: In the future this could just be services provided. For now
 * we just define this by a simple configuration, real implementation needs flexible code, though*/
public interface IncidentNotificationTriggerConfiguration extends Configuration {
	@Override
	StringResource name();
	
	IntegerResource minimumRepetitionNum();
	/**Intervaltype according to {@link TimeIntervalLength#type()}*/
	IntegerResource intervalToConsider();
}
