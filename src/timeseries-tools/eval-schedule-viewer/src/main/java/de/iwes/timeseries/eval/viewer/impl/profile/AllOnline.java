package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

public class AllOnline implements Profile {

	@Override
	public String id() {
		return "allOnline";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "All online timeseries";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "All online timeseries";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return null;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		return schedule instanceof OnlineTimeSeries;
	}

	@Override
	public ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn) {
		return null; // not supported
	}
	
}
