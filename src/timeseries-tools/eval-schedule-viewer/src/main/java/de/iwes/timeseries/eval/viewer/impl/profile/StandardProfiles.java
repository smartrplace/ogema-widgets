package de.iwes.timeseries.eval.viewer.impl.profile;

import java.util.Arrays;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileCategory;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Service(ProfileCategory.class)
@Component
public class StandardProfiles implements ProfileCategory {

	private final static List<Profile> PROFILES = Arrays.asList(
		 new AllLogdata(),
		 new AllSchedules(),
		 new AllOnline(),
		 new TemperatureProfile(),
		 new HumidityProfile(),
		 new PowerProfile(),
		 new ThermostatSetpointProfile(),
		 new ValvePositionProfile(),
		 new WindowSensorProfile(),
		 new PresenceProfile()
	);

	@Override
	public String id() {
		return "stdProfiles";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Standard profiles"; 
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Standard profiles"; 
	}

	@Override
	public List<Profile> getProfiles() {
		return PROFILES;
	}
	
}
