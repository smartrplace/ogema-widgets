package de.iwes.timeseries.eval.api.extended.concepts;

import java.lang.reflect.Field;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.MultiResult;

public interface MultiEvaluationProviderOnline<R, T extends MultiResult<R>> extends MultiEvaluationProvider<R, T> {
	public interface MultiEvaluationOnlineListener {
		/**
		 * 
		 * @param outputIndex index from return value of {@link #onlinOutputFields}
		 * @param value the new value if applicable. If the field is not a numerical
		 *   type or an array, time series etc. of a numerical type, value is not
		 *   relevant and the value has to be obtained from the field directly
		 */
		void newValue(int outputIndex, float value, long timeStamp);
	}
	
	public interface MultiOnlineResultType extends ResultType {
		Field dataField();
	}
	
	/**Fields from T that are part of the listener with additional information
	 * like SingleEvaluation results in ResultType. The fields listed here are
	 * typically overall results as it is not possible to provide
	 * per-gateway or per-room evaluation here*/
	public List<MultiOnlineResultType> onlineOutputFields();
	
	public void registerListener(MultiEvaluationOnlineListener listener);
}
