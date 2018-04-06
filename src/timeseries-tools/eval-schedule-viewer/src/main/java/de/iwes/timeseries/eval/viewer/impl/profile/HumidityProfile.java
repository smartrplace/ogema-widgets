package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesBuilder;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

class HumidityProfile implements Profile {

	@Override
	public String id() {
		return "humidityStd";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Humidity";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Humidity";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return InterpolationMode.LINEAR;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		if (schedule instanceof Schedule)
			return ((Schedule) schedule).getParent() != null && ((Schedule) schedule).getParent().getParent() instanceof HumiditySensor;
		if (schedule instanceof OnlineTimeSeries)
			return ((OnlineTimeSeries) schedule).getResource().getParent() instanceof HumiditySensor;
		if (!(schedule instanceof RecordedData))
			return false;
		final String id= ((RecordedData) schedule).getPath();
		String[] components = id.split("/");
		int length = components.length;
		if (length < 2)
			return false;
		String last = components[length-1];
		String secondLast = components[length-2].toLowerCase();
		switch (last) {
		case "reading":
			return secondLast.contains("humidity");
		default:
			return false;
		}
	}

	// the average humidity
	@Override
	public ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn) {
		if (constituents == null || constituents.isEmpty())
			return null;
		final ReadOnlyTimeSeries result;
		if (constituents.size() == 1) {
			result = constituents.iterator().next();
		}
		else {
			result = MultiTimeSeriesBuilder.newBuilder(constituents, Float.class)
					.setInterpolationModeForConstituents(InterpolationMode.LINEAR)
					.setInterpolationModeForResult(InterpolationMode.LINEAR)
					.setAverage()
					.ignoreGaps(true)
					.build();
		}
		final StringBuilder sb = new StringBuilder("Humidity");
		if (labelAddOn != null)
			sb.append(':').append(' ').append(labelAddOn);
		boolean online = false;
		for (ReadOnlyTimeSeries ts: constituents) {
			if (ts instanceof OnlineTimeSeries) {
				online = true;
				break;
			}
		}
		return new PresentationDataImpl(result, FloatResource.class, sb.toString(), InterpolationMode.LINEAR, this, online);	
	}
	
}
