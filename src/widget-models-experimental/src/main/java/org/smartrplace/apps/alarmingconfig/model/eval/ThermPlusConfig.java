package org.smartrplace.apps.alarmingconfig.model.eval;

import org.ogema.core.model.simple.FloatResource;

public interface ThermPlusConfig extends EvaluationByAlarmingOption {
	/** Maximum time between requesting setpoint and the new feedback is received*/
	FloatResource maxSetpointReactionTimeSeconds();
	
	/** Maximum time between new values (minutes)*/
	FloatResource maxIntervalBetweenNewValues();	
}
