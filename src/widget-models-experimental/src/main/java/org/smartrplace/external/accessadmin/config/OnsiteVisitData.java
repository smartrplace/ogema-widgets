package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface OnsiteVisitData extends Data {
	/** Planned date (if in future) or real date */
	TimeResource date();
	
	/** Information, which parts of the building / site are visited etc.*/
	StringResource remarks();
	
	/** Link to task in task management system etc. */
	StringResource link();
}
