package de.iwes.timeseries.eval.base.provider.utils;

import java.util.Objects;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class TimeSeriesDataImpl implements TimeSeriesDataOffline {
	
	private final ReadOnlyTimeSeries timeSeries;
	private final String label;
	private final String description;
	private final InterpolationMode mode;
	private final float offset;
	private final float factor;
	private final long timeOffset;
	
	public TimeSeriesDataImpl(ReadOnlyTimeSeries timeSeries, String label, String description, InterpolationMode mode) {
		this(timeSeries, label, description, mode, 0, 0, 0);
	}
	
	public TimeSeriesDataImpl(ReadOnlyTimeSeries timeSeries, String label, String description, InterpolationMode mode,
			float offset, float factor, long timeOffset) {
		Objects.requireNonNull(timeSeries);
		this.timeSeries = timeSeries;
		this.label = label;
		this.description = description;
		this.mode = mode != null ? mode : timeSeries.getInterpolationMode();
		this.offset = offset;
		this.factor = factor;
		this.timeOffset= timeOffset;
	}

	@Override
	public ReadOnlyTimeSeries getTimeSeries() {
		return timeSeries;
	}
	
	@Override
	public String id() {
		return label(OgemaLocale.ENGLISH);
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public String description(OgemaLocale locale) {
		return description;
	}

	@Override
	public InterpolationMode interpolationMode() {
		return mode;
	}

	@Override
	public float offset() {
		return offset;
	}

	@Override
	public float factor() {
		return factor;
	}

	@Override
	public long timeOffset() {
		return timeOffset;
	}
	
	@Override
	public String toString() {
		return "TimeSeriesDataImpl[" + label + "]";
	}

	
	
}
