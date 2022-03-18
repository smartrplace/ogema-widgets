package org.ogema.model.extended.alarming;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** Base resource to store escalation application data. The application data
 * shall be added as decorators.
 */
public interface EscalationData extends Data {
	StringResource providerId();
	TimeResource lastTimeProcessed();
}
