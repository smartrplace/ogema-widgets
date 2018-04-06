package de.iwes.timeseries.eval.garo.multibase.generic;

import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting;

/**
 * Generic GaRoSingleEvalProvider that allows to defined eval providers without
 * separate EvaluationInstance classes.
 */
public abstract class GenericGaRoSingleEvalProviderPreEval extends GenericGaRoSingleEvalProvider implements GaRoSingleEvalProviderPreEvalRequesting {
	
	public GenericGaRoSingleEvalProviderPreEval(String id, String label, String description) {
        super(id, label, description);
    }
    
	protected String currentGwId;
	protected String currentRoomId;
	@Override
	public void provideCurrentValues(String gwId, String roomId) {
		currentGwId = gwId;
		currentRoomId = roomId;
	}
}
