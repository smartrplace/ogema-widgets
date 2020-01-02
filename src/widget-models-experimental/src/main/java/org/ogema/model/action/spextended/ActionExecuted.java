package org.ogema.model.action.spextended;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.action.Action;
import org.ogema.model.prototypes.Data;

public interface ActionExecuted extends Data {
	Action action();
	TimeResource startTime();
	TimeResource endTime();
	/** This may be the file/directory where a backup/result is stored or
	 * a name given by the user or the program
	 */
	StringResource identifier();
}
