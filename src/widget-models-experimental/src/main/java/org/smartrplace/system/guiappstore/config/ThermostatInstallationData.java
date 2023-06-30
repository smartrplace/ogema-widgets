package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.simple.IntegerResource;

public interface ThermostatInstallationData {
	/** 0: unknown<br>
	 *  1: no anti-theft is installed
	 *  2: all thermostats have anti-theft installed (used also for single thermostat if it is anti-theft installed)
	 *  1: a few thermostats are protected (those protected should have indication on thermostat level)
	 *  2: thermostats are protected partially
	 *  3: most thermostats are protected (those not protected should have indication on thermostat level)
	 */
	IntegerResource antiTheftType();
	
	/** Information on anti-vandalism installation of thermostats. Anti-vandalism means that the thermostat is not
	 * accessible by users/customers without special tools. This can be a special construction e.g. of wood
	 * or a special solution delivered by the manufacturer of the thermostat.<br>
	 * For code-levels see {@link #antiTheftType()}
	 */
	IntegerResource antiVandalismType();

	/** 0: unknown
	 *  1: no adapter
	 *  ...
	 */
	IntegerResource adapterType();
}
