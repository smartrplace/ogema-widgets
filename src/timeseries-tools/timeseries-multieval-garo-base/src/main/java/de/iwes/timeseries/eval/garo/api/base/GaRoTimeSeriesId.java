package de.iwes.timeseries.eval.garo.api.base;
public class GaRoTimeSeriesId {
	
	public String gwId;
	public String timeSeriesId;
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GaRoTimeSeriesId)) return false;
		GaRoTimeSeriesId other = (GaRoTimeSeriesId)obj;
		return gwId.equals(other.gwId) && timeSeriesId.equals(other.timeSeriesId);
	}
	
	@Override
	public int hashCode() {
		return 7*gwId.hashCode()+11*timeSeriesId.hashCode();
	}
	
}
