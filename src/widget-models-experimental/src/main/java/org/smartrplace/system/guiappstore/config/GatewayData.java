package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;

/** 
 * Access configuration for gateways and groups of them.
 */
public interface GatewayData extends PhysicalElement {
	//@Deprecated
	//ResourceList<GatewayGroupData> groups();
	
	GatewayGroupData installationLevelGroup();
	
	BooleanResource expectedOnHeartbeat();
	
	/** If true the gateway shall ONLY be shown on config page and shall not be included into the summary calculation*/
	BooleanResource excludeFromStandardView();
	
	StringResource customer();
	//StringResource comment();
	StringResource guiLink();
	
	/** See {@link InstallAppDevice#installationStatus()}
	 */
	//IntegerResource installationStatus();
	
	/** GatewayId used to read remote slotsDb data*/
	StringResource remoteSlotsGatewayId();
	
	/** Maximum interval of heartbeat signals to avoid sending a connection lost message in milliseconds*/
	TimeResource warningMessageInterval();
	
	/** Interval for sending the entire communication structure requested in milliseconds*/
	TimeResource structureUpdateInterval();
	
	/** SmartrCockpit quick hacks*/
	StringResource buildingType();
	/** String to identify remote slotsDB data*/
	StringResource mainMeterLocationOnGateway();
	
	/** If true the gateway configuration shall be scanned for additional bundles to update*/
	BooleanResource hasSpecialBundles();
}
