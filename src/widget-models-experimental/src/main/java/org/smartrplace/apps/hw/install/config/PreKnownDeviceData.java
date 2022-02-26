package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;

public interface PreKnownDeviceData extends Data {
	/** For homematic this is the last four digits of the serial number. For other device types
	 * a special definition has to be made for each*/
	StringResource deviceEndCode();
	/**Part of device id behind device identifiert*/
	StringResource deviceIdNumber();
	/** Set reference if room is already known before device is available*/
	Room room();
	StringResource installationLocation();
	StringResource comment();
}
