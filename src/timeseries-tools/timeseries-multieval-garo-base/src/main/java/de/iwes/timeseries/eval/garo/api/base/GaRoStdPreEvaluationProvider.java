package de.iwes.timeseries.eval.garo.api.base;

import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;

public class GaRoStdPreEvaluationProvider<R, T extends GaRoMultiResult<R>, S extends GaRoSuperEvalResult<R, T>>
		implements GaRoPreEvaluationProvider<R> {
	private final S superEval;
	private final Class<S> superResultClass;
	//private final Class<T> resultClass;

	@SuppressWarnings("unchecked")
	public GaRoStdPreEvaluationProvider(S superEval) {
		this.superEval = superEval;
		superResultClass = (Class<S>) superEval.getClass();
		//if(superEval.intervalResults.isEmpty())
		//	resultClass = null;
		//else
		//	resultClass = (Class<T>) superEval.intervalResults.get(0).getClass();
	}

/*	public GaRoStdPreEvaluationProvider(Class<S> superResultClass, Class<T> resultClass,
			String jsonInputFile) {
		this.superResultClass = superResultClass;
		this.resultClass = resultClass;
		//TODO: This will not work as the resultClass is missing
		this.superEval = MultiEvaluationUtils.importFromJSON(jsonInputFile, superResultClass);
	}*/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GaRoStdPreEvaluationProvider(Class<? extends GaRoSuperEvalResult> superResultClass2,
			String jsonInputFile) {
		this.superResultClass = (Class<S>) superResultClass2;
		//this.resultClass = (Class<T>) resultClass2;
		//TODO: This will not work as the resultClass is missing
		this.superEval = MultiEvaluationUtils.importFromJSON(jsonInputFile, superResultClass);
	}

	@Override
	public S getSuperEvalData() {
		return superEval;
	}

	@Override
	public T getIntervalData(long startTime) {
		for(T ir: superEval.intervalResults) {
			if((ir.startTime <= startTime) && (ir.endTime > startTime))
				return ir;
		}
		return null;
	}

	@Override
	public RoomData getRoomData(long startTime, String gwId, String roomId) {
		T ir = getIntervalData(startTime);
		for(RoomData room: ir.roomEvals) {
			if(room.gwId.equals(gwId) && room.id.equals(roomId)) return room;
		}
		return null;
	}

	public Class<S> getSuperResultClass() {
		return superResultClass;
	}

	//public Class<T> getResultClass() {
	//	return resultClass;
	//}

}
