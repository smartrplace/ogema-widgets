package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.prototypes.Data;
import org.smartrplace.system.guiappstore.config.ThermostatInstallationData;

public interface SubCustomerSuperiorData extends Data {
	/** Reference to local {@link SubCustomerData} in {@link AccessAdminConfig}*/
	SubCustomerData lcoalData();
	
	/** Must be same as respective {@link SubCustomerData#aggregationType()} 
	 * 0 = standard<br>
	 *  1 = entire building representation
	 */
	IntegerResource aggregationType();
	
	/** Comma-separated list of email addresses that shall be used for customer maintenance requests. 
	 * This shall include tech contacts and admin email addresses received from CMS (or customer addresses directly created on the gateway)
	 */
	StringResource additionalAdminEmailAddresses();
	
	/** Salutations for email. Shall contain entries like "Frau Müller" or "Herr Maier". The rest of the template shall
	 * be filled automatically. The first entries are for additionalAdminEmailAddresses followed by salutations for addresses
	 * received from CMS.
	 */
	StringResource personalSalutations();
	
	StringResource phoneNumbers();

	/** If true then no user addresses will be evaluated*/
	// NOT USED ANYMORE, we always use this only
	//BooleanResource useOnlyAdditionalAddresses();

	/** Comma-separated list of email addresses that shall be used for IT maintenance requests
	 */
	StringResource emailAddressesIT();
	
	/** Salutations for IT email addresses
	 */
	StringResource personalSalutationsIT();
	
	StringResource phoneNumbersIT();

	/******************************
	/*** Hardware installation data 
	*******************************/
	
	/** 0: unknown<br>
	 *  1: customer does not change any batteries<br>
	 *  2: customer changes single batteries in urgent cases<br>
	 *  3: customer performs all battery changes
	 */
	IntegerResource batteryChangeLevel();
	
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
	 *  2: no
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

	/** Base URL of gateway to connect to via internet, e.g. https://customer.manufacturer.de:2000*/
	StringResource gatewayBaseUrl();

	/** Url to gateway installation and operation documentation
	 * <br>Copied from LocalGatewayInformation*/
	StringResource gatewayOperationDatabaseUrl();
	
	/** Url to overview on gateway documentation sources. If this is existing then the {@link #gatewayOperationDatabaseUrl()} may 
	 * not be used as the link overview should usually contain this.
	 * <br>Copied from LocalGatewayInformation*/
	StringResource gatewayLinkOverviewUrl();

	/** ID of system default locale (obtained by OgemaLocale.getLocale().getLanguage() ), which can
	 * be used to obtain OgemaLocale object by OgemaLocale#getLocale
	 * <br>Copied from LocalGatewayInformation*/
	StringResource systemLocale();

}