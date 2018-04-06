package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.actors.MultiSwitch;
import org.ogema.model.devices.connectiondevices.ThermalValve;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesBuilder;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

class ValvePositionProfile implements Profile {

	@Override
	public String id() {
		return "valvePositionStd";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Valve position";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Valve position";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return InterpolationMode.STEPS;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		Resource reading = null;
		if (schedule instanceof Schedule)
			reading = ((Schedule) schedule).getParent();
		else if (schedule instanceof OnlineTimeSeries)
			reading = ((OnlineTimeSeries) schedule).getResource();
		if (reading != null) {
			if (!reading.getName().equals("stateFeedback"))
				return false;
			Resource parent=reading.getParent();
			if (!parent.getName().equals("setting") || !(parent instanceof MultiSwitch))
				return false;
			return parent.getParent() instanceof ThermalValve;
		}
		if (!(schedule instanceof RecordedData))
			return false;
		final String id= ((RecordedData) schedule).getPath();
		String[] components = id.split("/");
		int length = components.length;
		if (length < 3)
			return false;
		return (components[length-1].equals("stateFeedback") 
			&& components[length-2].equals("setting")
			&& components[length-3].equals("valve"));
	}

	// the average valve position
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
		final StringBuilder sb = new StringBuilder("Valve position");
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
