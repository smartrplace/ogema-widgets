package de.iwes.timeseries.eval.garo.api.base;

import de.iwes.timeseries.eval.api.extended.PreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;

public interface GaRoPreEvaluationProvider<R>
		extends PreEvaluationProvider<R> {

	RoomData getRoomData(long startTime, String gwId, String roomId);
}
