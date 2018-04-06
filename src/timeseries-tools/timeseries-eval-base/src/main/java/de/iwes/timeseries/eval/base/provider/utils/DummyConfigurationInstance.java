package de.iwes.timeseries.eval.base.provider.utils;

import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;

/**Only use for JSON-Deserialization*/
public class DummyConfigurationInstance implements ConfigurationInstance {

	@Override
	public Configuration<?> getConfigurationType() {
		return null;
	}

}
