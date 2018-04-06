package de.iwes.timeseries.eval.base.provider.utils;

import java.util.Objects;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.InterpolationMode;

import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class TimeSeriesDataOnlineImpl implements TimeSeriesDataOnline {
	
	private final SingleValueResource resource;
	private final String label;
	private final String description;
	private final InterpolationMode mode;
	private final float offset;
	private final float factor;
	
	public TimeSeriesDataOnlineImpl(SingleValueResource resource, String label, String description, InterpolationMode mode) {
		this(resource, label, description, mode, 0, 0);
	}
	
	public TimeSeriesDataOnlineImpl(SingleValueResource resource, String label, String description, InterpolationMode mode,
			float offset, float factor) {
		Objects.requireNonNull(resource);
		this.resource = resource;
		this.label = label;
		this.description = description;
		this.mode = mode;
		this.offset = offset;
		this.factor = factor;
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
	public SingleValueResource getResource() {
		return resource;
	}
}
