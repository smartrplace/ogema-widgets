package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Collection;

import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class AllLogdata implements Profile {

	@Override
	public String id() {
		return "allLogdata";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "All logdata";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "All logdata";
	}

	@Override
	public InterpolationMode defaultInterpolationMode() {
		return null;
	}

	@Override
	public boolean accept(ReadOnlyTimeSeries schedule) {
		return schedule instanceof RecordedData;
	}

	@Override
	public ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn) {
		return null; // not supported
	}
	
}
