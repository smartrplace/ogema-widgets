package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
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
	 * This shall include tech contacts and admin email addresses received from CMS (or customer addresses directly created on the gateway)<br>
	 * !!! NOTE: The name "additional" is misleading now, but the existing resources cannot easily be renamed !!!
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
	/*** Gatway connection data 
	*******************************/

	/** Gateway connected via mobile of provider */
	BooleanResource gatewayConnectionViaMobileProvider();
	
	/** 0: Unknown<br>
	 *  1: No known restrictions tested<br>
	 *  2: Teach-in not possible<br>
	 *  3: VPN blocked<br>
	 *  100: Everything blocked
	 */
	IntegerResource networkRestrictions();
	
	/******************************
	/*** Battery change data 
	*******************************/
	
	/** 0: unknown<br>
	 *  1: customer does not change any batteries<br>
	 *  2: customer changes single batteries in urgent cases<br>
	 *  3: customer performs all battery changes
	 *  4: in urgent cases contact customer to offer own battery change as an exception<br>
	 *  5: no batteries
	 */
	IntegerResource batteryChangeLevel();
	
	BooleanResource batteriesMustBeSent();
	
	/** Write yes/no or device types that need to be changed in full beginning of heating/cooling season*/
	StringResource changeBatteriesStartingSeason();
	
	/*********************************
	 * Season data
	 ********************************/
	
	/** If true the customer received everything required for the season-starting (e.g. appointment for battery changes, batteries sent out, battery report, ...=
	 */
	BooleanResource customerReceivedWhatRequired();
	
	/** Comment for gateway maintenance, not regarding ongoing issues (e.g. what still has to be sent for battery exchange*/
	StringResource gatewayMaintenanceComment();
	
	/** Last thermostat decalcification time of most devices
	 */
	TimeResource lastGatewayDecalc();

	/******************************
	/*** Hardware installation data 
	*******************************/
	
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
	
	/** 0: unknown<br>
	 *  1: not required<br>
	 *  2: do contact sales first
	 */
	IntegerResource contactSalesBeforeInformation();
	
	/** 0: unknown/default (like option 1)<br>
	 *  1: via email, use phone if urgent <br>
	 *  2: via email only, even if urgent <br>
	 *  3: via phone only, do not send email if not coordinated with sales <br>
	 *  4: via email, then directly also phone (email for documentation purposes)<br>
	 *  5: via phone and email if not reached via phone<br>
	 */
	IntegerResource contactPreferences();
	
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
	@Deprecated // Not used yet, but shall be switched in the future
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

	/******************************
	/*** Service organization 
	*******************************/
	
	/** Define regional codes for each site*/
	IntegerResource region();
	
	/** 0: unknown<br>
	 *  1: not relevant<br>
	 *  2: not urgent<br>
	 *  3: intermediate<br>
	 *  4: urgent<br>
	 *  5: very urgent<br>
	 */
	IntegerResource onsiteVisitPriority();
	//IntegerResource batteryChangePriority();
	
	/** 0: no special testing gateway<br>
	 *  1: test gateway that might have special software version
	 */
	//IntegerResource testGatewayStatus();
}
