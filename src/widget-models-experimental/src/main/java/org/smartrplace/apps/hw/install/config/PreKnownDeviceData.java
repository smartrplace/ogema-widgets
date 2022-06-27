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
	
	/** DeviceHandler for which the PreknownData is foreseen.
	 * If not existing or empty then the data is primarily foreseen for thermostats but can also be used for other devices if fitting.
	 * TODO: In the future also data provided for other device handlers may be used that
	 * could just be provided at the wrong place, but this needs further investigation and may
	 * also cause unintended behaviour*/
	StringResource deviceHandlerId();
	
	/** Each entry may be assigned to a CCU manually or the CCU by which the entry is used will
	 * be set automatically. If the entry is used then a reference to the CCU will be set
	 * overwriting any manual setting that may be made earlier.
	 */
	InstallAppDevice ccu();
}
