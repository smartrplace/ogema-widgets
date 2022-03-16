package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.prototypes.Data;

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
	TemperatureResource defaultEcoTemperatureHeating();
	TemperatureResource defaultEcoTemperatureCooling();
	
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
}
