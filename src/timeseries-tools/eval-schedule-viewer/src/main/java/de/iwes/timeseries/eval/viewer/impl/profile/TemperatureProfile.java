package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesBuilder;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

class TemperatureProfile implements Profile {

	@Override
	public String id() {
		return "temperatureStd";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Temperature";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Temperature";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return InterpolationMode.LINEAR;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		if (schedule instanceof Schedule)
			return schedule instanceof Schedule && ((Schedule) schedule).getParent() instanceof TemperatureResource;
		if (schedule instanceof OnlineTimeSeries)
			return ((OnlineTimeSeries) schedule).getResource() instanceof TemperatureResource;
		if (schedule instanceof SchedulePresentationData)
			return ((SchedulePresentationData) schedule).getScheduleType() == TemperatureResource.class;
		if (!(schedule instanceof RecordedData))
			return false;
		final String id = ((RecordedData) schedule).getPath();
		String[] components = id.split("/");
		int length = components.length;
		if (length < 2)
			return false;
		String last = components[length-1];
		String secondLast = components[length-2].toLowerCase();
		switch (last) {
		case "reading":
			return secondLast.contains("temperature");
		default:
			return false;
		}
	}

	// the average temperature
	@Override
	public ProfileSchedulePresentationData aggregate(final Collection<ReadOnlyTimeSeries> constituents, String labelAddOn) {
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
		final StringBuilder sb = new StringBuilder("Temperature");
		if (labelAddOn != null)
			sb.append(':').append(' ').append(labelAddOn);
		boolean online = false;
		for (ReadOnlyTimeSeries ts: constituents) {
			if (ts instanceof OnlineTimeSeries) {
				online = true;
				break;
			}
		}
		return new PresentationDataImpl(result, TemperatureResource.class, sb.toString(), InterpolationMode.LINEAR, this, online);		
	}
	
}
