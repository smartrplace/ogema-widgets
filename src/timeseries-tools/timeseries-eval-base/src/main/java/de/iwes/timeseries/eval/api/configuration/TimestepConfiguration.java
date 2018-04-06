package de.iwes.timeseries.eval.api.configuration;

import java.util.Collection;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.GenericDurationConfiguration;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public enum TimestepConfiguration implements Configuration<GenericDurationConfiguration> {
	
	INSTANCE;
	
	private TimestepConfiguration() {};
	
	@Override
	public String id() {
		return "timestep_cfg";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Timestep configuration";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Timestep configuration";
	}
	
	@Override
	public Class<GenericDurationConfiguration> configurationType() {
		return GenericDurationConfiguration.class;
	}
	
	@Override
	public void filter(GenericDurationConfiguration instance) throws IllegalArgumentException {
		if (instance.getDuration() <= 0)
			throw new IllegalArgumentException("Duration non-positive: " + instance.getDuration());
	}
	
	@Override
	public Collection<ResultType> getApplicableResultTypes() {
		return null;
	}
	
	@Override
	public GenericDurationConfiguration defaultValues() {
		return null;
	}
	
	@Override
	public boolean isOptional() {
		return true;
	}
	
}

