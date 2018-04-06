package de.iwes.timeseries.eval.api.configuration;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;

public class StartEndInstance extends DateConfiguration {
	
	public StartEndInstance(boolean startOrEnd, long time) {
		super(time, startOrEnd ? StartEndConfiguration.START_CONFIGURATION : StartEndConfiguration.END_CONFIGURATION);
	}

}
