package org.smartrplace.apps.alarmingconfig.model.eval;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface EvaluationInterval extends Data {
	TimeResource start();
	TimeResource end();
	@Override
	StringResource name();
}
