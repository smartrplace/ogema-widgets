package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.prototypes.Data;

/** Relevant for heating or cooling, used in Subcustomer roomtype-settings*/
public interface TemperatureControlSettings extends Data {
	/** Room type to which the settings are applied
	 * -1 : All */
	IntegerResource roomType();
	
	TemperatureResource comfortTemperature();
	TemperatureResource absenceTemperature();
	TemperatureResource occupancyTemperature();
	TemperatureResource windowOpenTemperature();
	TemperatureResource minimumSetpoint();
	TemperatureResource maximumSetpoint();
	
	BooleanResource ecoEqualsOff();
	
	/** 0: Configuration mainly via working day / weekend settings, special days are allowed
	 *  1: Configuration only via working day / weekend settings, special days shall not be offered
	 *  2: Configuration only via single days of week, no offering of working day / weekend settings
	 */
	IntegerResource dayTypeSettingMode();
	
	ResourceList<TimeArrayResource> startEndTimes();
}
