package de.iwes.timeseries.eval.base.provider.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

public class EvaluationInputImpl implements EvaluationInput {
	
	private final List<TimeSeriesData> timeSeries;
	private final boolean online;
	
	public EvaluationInputImpl(List<TimeSeriesData> timeSeries) {
		this.timeSeries = Collections.unmodifiableList(new ArrayList<>(timeSeries));
		boolean online = false;
		for (TimeSeriesData tsd : timeSeries) {
			if(tsd instanceof TimeSeriesDataOffline) {
				if (((TimeSeriesDataOffline)tsd).getTimeSeries() instanceof OnlineTimeSeries) { 
					online = true;
					break;
				}
			} else {
				online = true;
				break;
			}
		}
		this.online = online; 
	}

	@Override
	public boolean isOnlineEvaluation() {
		return online;
	}

	@Override
	public List<TimeSeriesData> getInputData() {
		return timeSeries;
	}

}
