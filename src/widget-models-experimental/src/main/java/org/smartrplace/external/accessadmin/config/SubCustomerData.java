package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.prototypes.Data;
import org.smartrplace.system.guiappstore.config.ThermostatInstallationData;

public interface SubCustomerData extends Data {
	//Room-mapping is stored in room as each room must be in exactly sub customer
	//we have to use a decorator "subcustomer"
	//ResourceList<Room> rooms();
	
	/** Building / usage type. If the building has only a single subCustomer the type is relevant
	 * for the entire building. The following types are defined:<br>
	 * 	 CUSTOMER_TYPE_OFFICE = 10<br>
	 *   CUSTOMER_TYPE_LIVING = 20<br>
	 *   CUSTOMER_TYPE_COWORKING = 30<br>
	 *   CUSTOMER_TYPE_CHURCH = 40<br>
	 *   CUSTOMER_TYPE_SALES = 50<br>
	 *   CUSTOMER_TYPE_SCHOOL = 60<br>
	 *   CUSTOMER_TYPE_COMMUNTIY_BUILDING = 70<br>
	 *   CUSTOMER_TYPE_PRODUCTION = 80<br>
	 */
	IntegerResource subCustomerType();
	
	/** Eco mode applied only to sub customer, not entire building*/
	BooleanResource ecoModeActive();
	
	/** Indeces of days that are considered working days, 1=Monday, 7=Sunday*/
	IntegerArrayResource workingDays();
	
	/** The default lower temperature is applied to all rooms as a default*/
	//TemperatureResource defaultEcoTemperatureHeating();
	//TemperatureResource defaultEcoTemperatureCooling();
	
	BooleanResource setpointCurveEditableByUsers();
	
	//public static SubCustomerData getData(Room room) {
	//	return room.getSubResource("subcustomer", SubCustomerData.class);
	//}
	
	AccessConfigUser userAttribute();
	BuildingPropertyUnit roomGroup();
	
	/** 0 = standard<br>
	 *  1 = entire building representation
	 */
	IntegerResource aggregationType();
	
	/** Standard settings saved if not acquired from rooms*/
	ResourceList<TemperatureControlSettings> roomTypeSettingsHeating();
	ResourceList<TemperatureControlSettings> roomTypeSettingsCooling();
	
	/** Comma-separated list of email addresses that shall be used for customer maintenance requests in
	 * addition to the admin email addresses received from CMS (or customer addresses directly created on the gateway)
	 */
	StringResource additionalAdminEmailAddresses();
	
	/** Salutations for email. Shall contain entries like "Frau Müller" or "Herr Maier". The rest of the template shall
	 * be filled automatically. The first entries are for additionalAdminEmailAddresses followed by salutations for addresses
	 * received from CMS.
	 */
	StringResource personalSalutations();
	
	/** If true then no user addresses will be evaluated*/
	BooleanResource useOnlyAdditionalAddresses();

	/** Comma-separated list of email addresses that shall be used for IT maintenance requests
	 */
	StringResource emailAddressesIT();
	
	/** Salutations for IT email addresses
	 */
	StringResource personalSalutationsIT();
	
	/******************************
	/*** Hardware installation data 
	*******************************/
	
	/** 0: unknown<br>
	 *  1: customer does not change any batteries<br>
	 *  2: customer changes single batteries in urgent cases<br>
	 *  3: customer performs all battery changes
	 */
	BooleanResource batteryChangeLevel();
	
	/** Customer can be asked to uninstall thermostat and reinstall, adapt thermostat */
	BooleanResource customerPerformsThermostatAdapt();
	
	/** Add 0.5 to indicate inverted seasons compared to Europe (southern hemisphere)
	 *  0: standard switching between seasons<br>
	 *  1: no switching by service provider<br>
	 *  2: service provider switches only from summer to winter<br>
	 *  3: service provider switches only from winter to summer<br>
	 *  4: special (see separate data)
	 */
	FloatResource summerWinterModeSwitching();
	
	/** 0: Unknown<br>
	 *  1: No known restrictions tested<br>
	 *  2: Teach-in not possible<br>
	 *  3: VPN blocked<br>
	 *  100: Everything blocked
	 */
	IntegerResource networkRestrictions();
	
	/** 0: unknown<br>
	 *  1: not required<br>
	 *  2: do contact sales first
	 */
	IntegerResource contactSalesBeforeInformation();
	
	/** 0: unknown<br>
	 *  1: yes
	 *  2: full lowering during weekends
	 *  3: no
	 *  otherwise: Indicate estimated start time in minutes from start of day
	 */
	IntegerResource centralHeatingNightlyLowering();
	IntegerResource centralHeatingNightlyLoweringEnd();
	
	/** 1: Full weekend saturday/sunday
	 *  2: Extension full saturday only
	 *  3: Extension full sunday only
	 *  otherwise: bitwise information which days are lowered, 4=monday, 256=sunday, if bit is 1, then day is fully lowered
	 */
	IntegerResource centralHeatingWeekendMode();
	
	/** General information how thermostats are installed. Special information can be added to {@link Thermostat} resources as
	 * decorators. */
	ThermostatInstallationData thermostatInstallationData();

}
