package org.smartrplace.gateway.device;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.extended.alarming.AlarmGroupData;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.GenericFloatSensor;
import org.smartrplace.monitoring.vnstat.resources.NetworkTrafficData;

/** Sensor and actor data for the gateway itself<br>
 * Note that a top-level resource of this type can be obtained/created via the method ResourceHelper#getLocalDevice(ApplicationManager)*/
public interface GatewayDevice extends PhysicalElement {
	/** If this is active and non-empty then the gateway is a subgateway. If possible provide the super-gateway-id
	 * here, otherwise just "T" (for true)
	 */
	StringResource subGatewayToSuperGw();
	
	/** Each time a pull operation on Git is performed on update shall be written into the resource:
	 * 1: success, no update found
	 * 2: update found, OGEMA restart triggered (more details may be provided with additional positive values)
	 * 0: RundirUpdateStatus/failed written to false
	 * -1: RundirUpdateStatus/failed written to true: pull operation failed (more detailed negative values may be defined to provide more information)
	 * ==> Values 0 and 1 are "normal", 2 indicates a relevant update found, -1 is an error.
	 */
	IntegerResource gitUpdateStatus();
	
	/** Indication of an operating system and bundle restart
	 * 0 : Restart of the OGEMA framework detected 
	 * 1  : Reboot of the operating system detected (e.g. because power was disconnected for some time or because reboot was
	 * 		initiated from console)
	 *  04: Alarming App Expert
	 *  08: User Management expert
	 *  16: Permissions app
	 *  32: Charts expert
	 *  64: Installation & Setup expert
	 * 128: API-testing bundle (no custom resources)
	 * 256: Roomcontrol bundle
	 * 512: Monitoring driver services
	 */
	IntegerResource systemRestart();
	/** Counter is incremented on each system startup and set to zero after 2 hours of operation*/
	IntegerResource systemRestartCounterLastHours();
	
	/** Interval for heartbeat sending to superior instance*/
	TimeResource heartBeatDelay();
	
	@Deprecated
	IntegerResource activeAlarmSupervision();
	@Deprecated
	IntegerResource datapointsInAlarmState();
	
	@Deprecated
	IntegerResource knownIssuesOther();
	@Deprecated
	IntegerResource knownIssuesAssignedOperation();
	@Deprecated
	IntegerResource knownIssuesAssignedDev();
	@Deprecated
	IntegerResource knownIssuesAssignedCustomer();
	@Deprecated
	IntegerResource knownIssuesAssignedBacklog();
	
	/** Known issues of different types given as same index as {@link AlarmGroupData#USER_ROLES}*/
	//IntegerArrayResource knownIssues();
	
	ResourceList<NetworkTrafficData> networkTrafficData();
	PhysicalElement networkTrafficDataViaMirror();
	
	StringArrayResource apiMethods();
	/** When the API is accessed then an entry into this resource is made (maximum one entry per 5 seconds per API method)
	 * The index of the API method is from {@link #apiMethods()} is written. For mobile access 0.5 is added.
	 * Note that the method indeces are stored persistently but will vary between gateways. This may be adapted in the
	 * future, but would require coordinated indexing of the methods
	 */
	FloatResource apiMethodAccess();
    
    /**
     * The gateway's public IP address, if any.
     * @return (optional) gateways public IP address
     */
    StringResource publicAddress();
   
    /** Each time the public address is written this is updated with the information to be written into 
     * {@link #foundPublicAddressLastPart()} if a change is detected or a negative error code if no scanning is possible
     */
    IntegerResource foundPublicAddressLastPartRaw();
    
    /**When a change in the publicAddress is detected in the relevant last part then the new value is written here
     * Depending on local requirements this may be the full IP address as integer*/
    IntegerResource foundPublicAddressLastPart();
  
    /** link to room control ecoMode*/
    BooleanResource ecoMode();
    
    /** link to room control seasonMode */
    IntegerResource seasonMode();
    
    /** Used disk space in per cent*/
    GenericFloatSensor usedSpace_Root();
    
    /** Traffic assigned to startup and software updates (detected via startups).
     * NOTE: Currently not used*/
    FloatResource trafficUpdatePlus();
	
    /** Traffic counted by heartbeat*/
    FloatResource heartbeatTraffic();
    
    /** Write to this resource to trigger an alarm indicating that the log files should be
     * checked for further analysis.<br>
     * 1: Check for notes regarding Faulty timestamp
     * 2: unspecified API exception in POST method (write or pre-processing of POST-read)
     * 3: unspecified API exception in Read method (GET-read or POST-read)
     * 4: unspecified API exception during getProviders
     * 5: page not found requested on API (SC_METHOD_NOT_ALLOWED)
     * 6: objectId not found requested on API (SC_NO_CONTENT)
     * 49: other unspecified API exception
     * ...
     */
    FloatResource logFileCheckNotification();
    
    /** Each time room control devices are cleaned up (ThermostatResetService) then the number of devices unassigned
     * is written. As devices without open alarms are cleaned up these devices need to have an open alarm. With
     * auto-actions these devices should be devices that are newly in alarm state.<br>
     * This should only be counteds if the respective controller (CCU, Gateway) is not in alarm state.
     */
    FloatResource countDeviceUnassignedWithOpenAlarm();
    
    /** Memore timeseries supervision*/
    @Deprecated
    FloatResource pstMultiToSingleEvents();
    @Deprecated
    FloatResource pstMultiToSingleCounter();
    @Deprecated
    FloatResource pstMultiToSingleAggregations();
    @Deprecated
    FloatResource pstMultiToSingleAggregationsCounter();

    @Deprecated
    FloatResource pstBlockingSingeEvents();
    @Deprecated
    FloatResource pstBlockingCounter();
    @Deprecated
    FloatResource pstSubTsBuild();
    @Deprecated
    FloatResource pstSubTsBuildCounter();

    @Deprecated
    FloatResource pstUpdateValuesPS2();
    @Deprecated
    FloatResource pstUpdateValuesPS2Counter();
    @Deprecated
    FloatResource pstTSServlet();
    @Deprecated
    FloatResource pstTSServletCounter();
	
	ResourceList<InterfaceInfo> networkInterfaces();
}
