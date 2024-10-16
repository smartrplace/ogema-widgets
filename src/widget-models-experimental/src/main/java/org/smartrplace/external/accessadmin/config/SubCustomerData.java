package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.prototypes.Data;

public interface SubCustomerData extends Data {
	//Room-mapping is stored in room as each room must be in exactly sub customer
	//we have to use a decorator "subcustomer"
	//ResourceList<Room> rooms();
	
	/** Reference to superior master data in database*/
	SubCustomerSuperiorData databaseData();
	
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
	
	/** Index[0]: Devices types heating, [1]: Devices types cooling<br>
	 * Supported device types: TH, AC
	 */
	StringArrayResource seasonDeviceTypes();
	
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
	 * <br>Moved to {@link SubCustomerSuperiorData}
	 */
	@Deprecated
	StringResource additionalAdminEmailAddresses();
	
	/** Salutations for email. Shall contain entries like "Frau Müller" or "Herr Maier". The rest of the template shall
	 * be filled automatically. The first entries are for additionalAdminEmailAddresses followed by salutations for addresses
	 * received from CMS.
	 * <br>Moved to {@link SubCustomerSuperiorData}
	 */
	@Deprecated
	StringResource personalSalutations();
	
	/** If true then no user addresses will be evaluated
	 * <br>Moved to {@link SubCustomerSuperiorData}
	 * */
	@Deprecated
	BooleanResource useOnlyAdditionalAddresses();

	/** Comma-separated list of email addresses that shall be used for IT maintenance requests
	 * <br>Moved to {@link SubCustomerSuperiorData}
	 */
	@Deprecated
	StringResource emailAddressesIT();
	
	/** Salutations for IT email addresses
	 * <br>Moved to {@link SubCustomerSuperiorData}
	 */
	@Deprecated
	StringResource personalSalutationsIT();

	/** Counter for tenant data reset e.g. due to change of tenant*/
	IntegerResource resetCounter();
}
