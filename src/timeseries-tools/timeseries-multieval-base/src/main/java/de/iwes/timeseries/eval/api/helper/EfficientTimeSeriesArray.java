package de.iwes.timeseries.eval.api.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

public class EfficientTimeSeriesArray {
	public float[] data;
	public String[] timeStamps;
	
	public static EfficientTimeSeriesArray getValue(ReadOnlyTimeSeries timeSeries) {
		EfficientTimeSeriesArray result = new EfficientTimeSeriesArray();
		if(timeSeries == null) return result;
		int len = timeSeries.size();
		result.data = new float[len];
		result.timeStamps = new String[len];
		int i = 0;
		for(SampledValue v: timeSeries.getValues(Long.MIN_VALUE)) {
			result.data[i] = v.getValue().getFloatValue();
			result.timeStamps[i] = new DateTime(v.getTimestamp()).toString();
			i++;
		}
		return result;
	}
	
	public static FloatTreeTimeSeries setValue(EfficientTimeSeriesArray array) {
		List<SampledValue> vlist = new ArrayList<>();
		FloatTreeTimeSeries result = new FloatTreeTimeSeries();
		if((array == null)||(array.timeStamps == null)) return result;
		for(int i=0; i<array.timeStamps.length; i++) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
			try {
			    Date d = f.parse(array.timeStamps[i]);
			    long t = d.getTime();
				vlist.add(new SampledValue(new FloatValue(array.data[i]), t, Quality.GOOD));
			} catch (ParseException e) {
			    e.printStackTrace();
			}
		}
		result.addValues(vlist);
		return result;
	}

}
