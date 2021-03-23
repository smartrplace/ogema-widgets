package org.smartrplace.apps.alarmingconfig.model.eval;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface EvaluationByAlarmConfig extends Data {
	/** Start time for running new evaluations*/
	TimeResource evaluationStart();
	
	/** Operation / testing intervals for which evaluations shall take place*/
	ResourceList<EvaluationInterval> showIntervals();
	
	ResourceList<EvaluationByAlarmingOption> configOptionsToTest();
}
