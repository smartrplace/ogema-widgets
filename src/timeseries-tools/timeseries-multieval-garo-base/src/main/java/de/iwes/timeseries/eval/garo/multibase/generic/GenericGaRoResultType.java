package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.ResultTypeDefault;

public abstract class GenericGaRoResultType extends ResultTypeDefault {
	
	public GenericGaRoResultType(String label) {
		this(label, label);
	}
	public GenericGaRoResultType(String label, String description) {
		this(label, description, ResultStructure.COMBINED);
	}
	public GenericGaRoResultType(String label, ResultStructure resultStructure) {
		this(label, label, resultStructure);
	}
	public GenericGaRoResultType(String label, String description, ResultStructure resultStructure) {
		super(label, description, resultStructure);
	}

	public abstract SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore evalContainer, ResultType rt, List<TimeSeriesData> inputData);
}
