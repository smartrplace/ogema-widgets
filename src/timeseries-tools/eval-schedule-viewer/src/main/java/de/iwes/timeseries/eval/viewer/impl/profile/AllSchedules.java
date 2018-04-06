package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class AllSchedules implements Profile {

	@Override
	public String id() {
		return "allSchedules";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "All schedules";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "All schedules";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return null;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		return schedule instanceof Schedule;
	}

	@Override
	public ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn) {
		return null; // not supported
	}
	
}
