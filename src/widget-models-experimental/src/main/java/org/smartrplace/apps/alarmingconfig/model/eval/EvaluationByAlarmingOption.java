package org.smartrplace.apps.alarmingconfig.model.eval;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.prototypes.Data;

public interface EvaluationByAlarmingOption extends Data {
	BooleanResource isSelected();
}
