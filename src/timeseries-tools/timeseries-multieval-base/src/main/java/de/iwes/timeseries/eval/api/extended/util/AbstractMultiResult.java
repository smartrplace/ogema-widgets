package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;

import org.ogema.tools.resource.util.TimeUtils;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiResult;

public class AbstractMultiResult<R> implements MultiResult<R> {
	protected final List<MultiEvaluationInputGeneric<R>> inputData;
	
	/**TODO: make elements private and provide/use getters*/
	public long startTime;
	
	/**End time may only be determinable when the evaluation ends, so this cannot be final*/
	public long endTime;
	public Collection<ConfigurationInstance> configurations;
	
	@Override
	public List<MultiEvaluationInputGeneric<R>> getInputData() {
		return inputData;
	}

	public AbstractMultiResult(List<MultiEvaluationInputGeneric<R>> inputData, long start, long end, Collection<ConfigurationInstance> configurations) {
		this.inputData = inputData;
		this.startTime = start;
		this.endTime = end;
		this.configurations = configurations;
	}
	
	public AbstractMultiResult(List<MultiEvaluationInputGeneric<R>> inputData, long startTime, Collection<ConfigurationInstance> configurations) {
		this.inputData = inputData;
		this.startTime = startTime;
		this.configurations = configurations;
	}

	@Override
	public String getSummary() {
		return this.getClass().getSimpleName()+": start:"+TimeUtils.getDateAndTimeString(startTime)+
				" end:"+TimeUtils.getDateAndTimeString(endTime);
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public Collection<ConfigurationInstance> getConfigurations() {
		return configurations;
	}

}
