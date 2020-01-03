/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.logconfig.plus;

import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.recordeddata.RecordedDataConfiguration;
import org.ogema.core.recordeddata.RecordedDataConfiguration.StorageType;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.gateway.remotesupervision.GatewayTransferInfo;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.scheduleviewer.config.ScheduleViewerConfig;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.util.logconfig.EvalHelper;
import de.iwes.util.logconfig.LogHelper;
import de.iwes.util.resource.ResourceHelper;

public class LogHelperPlus {

	/**Configure resource inside a Homematic device for logging, add it to a ScheduleViewer configuration and configure it
	 * for remote supervision. Group it into a schedule viewer configuration for its room
	 * @param fres resource to activate logging for
	 * @param baseLabel to generate the label of the log to be used for the ScheduleViewer entry and for remote supervision
	 * 		the last four digits of the homematic device id will be addded to baseLabel
	 * @param remoteTransfer add the log to list of logs to be transferred to the remote supervision
	 * server. If null no registration will take place. The registration will only be made
	 * if the log is not registered yet.
	 * @return ScheduleViewer entry representing the log (newly created or existing entry for the log)
	 */
	public static ScheduleViewerConfig addResourceToRoomLogHM(SingleValueResource fres,
			String baseLabel,
			GatewayTransferInfo remoteTransfer, ApplicationManager appMan) {
		return addResourceToRoomLog(fres, baseLabel+LogHelper.getDeviceId(fres),
				remoteTransfer, appMan);
	}
	/**Configure logging for resource, add it to a ScheduleViewer configuration and configure it
	 * for remote supervision. Group it into a schedule viewer configuration for its room
	 * @param fres resource to activate logging for
	 * @param label label of the log to be used for the ScheduleViewer entry and for remote supervision.
	 * If an existing entry is found and the label is different the label will be updated. 
	 * @param remoteTransfer add the log to list of logs to be transferred to the remote supervision
	 * server. If null no registration will take place. The registration will only be made
	 * if the log is not registered yet.
	 * @return ScheduleViewer entry representing the log (newly created or existing entry for the log)
	 */
	public static ScheduleViewerConfig addResourceToRoomLog(SingleValueResource fres,
    		String label,
    		GatewayTransferInfo remoteTransfer, ApplicationManager appMan) {
    	Room room = ResourceUtils.getDeviceLocationRoom(fres);
    	if((room == null)||(!room.isActive())) {
    		LogHelper.activateLogging(fres);
    		return null;
    	}
    	String configName = ResourceUtils.getHumanReadableName(room);
    	if((configName==null)||configName.equals("")) {
    		LogHelper.activateLogging(fres);
    		return null;
    	}
    	return addResourceToLog(fres, configName, label, remoteTransfer, appMan);
    }
	/**Configure logging for resource, add it to a ScheduleViewer configuration and configure it
	 * for remote supervision
	 * @param fres resource to activate logging for
	 * @param configName name of ScheduleViewer configuration set to which log shall be
	 * added. If the set does not yet exist it will be created
	 * @param label label of the log to be used for the ScheduleViewer entry and for remote supervision.
	 * If an existing entry is found and the label is different the label will be updated. 
	 * @param remoteTransfer add the log to list of logs to be transferred to the remote supervision
	 * server. If null no registration will take place. The registration will only be made
	 * if the log is not registered yet.
	 * @return ScheduleViewer entry representing the log (newly created or existing entry for the log)
	 */
	public static ScheduleViewerConfig addResourceToLog(SingleValueResource fres,
    		String configName, String label,
    		GatewayTransferInfo remoteTransfer, ApplicationManager appMan) {
    	ResourceList<ScheduleViewerConfig> scheduleConfigs = EvalHelperPlus.getScheduleConfigs(appMan); //.scheduleConfigs();
    	if(!scheduleConfigs.exists()) {
    		scheduleConfigs.create();
    		//scheduleConfigs = appMan.getResourceManagement().createResource(
    		//		generalScheduleViewerConfigName, ResourceList.class);
    		//scheduleConfigs.setElementType(ScheduleViewerConfig.class);
    	}
    	return LogHelperOwnList.addResourceToLog(fres, configName, label, scheduleConfigs, remoteTransfer);
    }
	
	public static ScheduleViewerConfig addScheduleViewerConfigEntry(String resourcePath,
    		String configName, String label,
    		GatewayTransferInfo remoteTransfer, ApplicationManager appMan) {
		ValueResource r = appMan.getResourceAccess().getResource(resourcePath);
		if(r == null) return null;
		return addScheduleViewerConfigEntry(r, configName, label, remoteTransfer, appMan);
	}
	public static ScheduleViewerConfig addScheduleViewerConfigEntry(ValueResource fres,
    		String configName, String label,
    		GatewayTransferInfo remoteTransfer, ApplicationManager appMan) {
    	ResourceList<ScheduleViewerConfig> scheduleConfigs =EvalHelperPlus.getScheduleConfigs(appMan); //.scheduleConfigs();
    	if(!scheduleConfigs.exists()) {
    		scheduleConfigs.create();
     	}
    	return LogHelperOwnList.addTimeSeriesConfig(fres, configName, label, scheduleConfigs, remoteTransfer);
    }

	/**Get schedule viewer configuration by name
	 * 
	 * @param configName
	 * @param appMan
	 * @return null if not existing
	 */
	public static ScheduleViewerConfig getScheduleViewerConfig(String configName, ApplicationManager appMan) {
    	ResourceList<ScheduleViewerConfig> scheduleConfigs = EvalHelperPlus.getScheduleConfigs(appMan); //.scheduleConfigs();
    	if(!scheduleConfigs.exists()) {
    		return null;
    	}
    	return LogHelperOwnList.getConfigByName(configName, scheduleConfigs);
	}
	public static boolean setDefaultConfig(ScheduleViewerConfig config, ApplicationManager appMan) {
    	ScheduleViewerConfig std = EvalHelperPlus.getScheduleViewerConfigStandard(appMan); //.scheduleViewerConfigStandard();
    	std.setAsReference(config);
		return true;
	}
	public static ScheduleViewerConfig getDefaultConfig(ApplicationManager appMan) {
    	ScheduleViewerConfig std = EvalHelperPlus.getScheduleViewerConfigStandard(appMan); //.scheduleViewerConfigStandard();
    	if(!std.exists()) return null;
    	return std;
	}
}
