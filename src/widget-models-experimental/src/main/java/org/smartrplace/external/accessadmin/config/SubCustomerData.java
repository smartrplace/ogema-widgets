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
	 * for the entire building
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
}