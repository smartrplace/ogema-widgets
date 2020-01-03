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

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.gateway.remotesupervision.DataLogTransferInfo;
import org.ogema.model.gateway.remotesupervision.GatewayTransferInfo;
import org.ogema.model.scheduleviewer.config.ScheduleViewerConfig;
import org.ogema.model.scheduleviewer.config.TimeSeriesPresentationData;

import de.iwes.util.logconfig.LogHelper;
import de.iwes.util.resource.ValueResourceHelper;
import de.iwes.util.timer.AbsoluteTiming;

/**Methods to be used with an own ResourceList<ScheduleConfigManagement>*/
public class LogHelperOwnList {
	/** Add configuration for schedule viewer to a list of such configurations
	 * 
	 * @param name name of new schedule viewer configuration
	 * @param scheduleConfigs resource list of schedule viewer configurations
	 * @return new configuration added
	 */
	public static ScheduleViewerConfig addTSView(String name, ResourceList<ScheduleViewerConfig> scheduleConfigs) {
	    	ScheduleViewerConfig svc = scheduleConfigs.add();
	    	svc.hideSelection().<BooleanResource>create().setValue(true);
			svc.timeSeriesData().create();
			svc.name().<StringResource>create().setValue(name);
			return svc;
    }
	/** Create (if necessary) and update schedule viewer configuration to current resource definition
	 * 
	 * @param svc schedule viewer configuration to be updated or null if to be created
	 * @param name name schedule viewer configuration to be added if svc is null
	 * @param scheduleConfigs resource list of schedule viewer configurations
	 * @return updated object svc or newly created object
	 */
    public static ScheduleViewerConfig createAndUpdate(ScheduleViewerConfig svc, String name,
    		ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	if(svc == null) {
    		svc = addTSView(name, scheduleConfigs);
    	}
    	//ResourceList<TimeSeriesPresentationFilter> filterList = 
    			svc.relevantFilters().create();
		//if(!filterList.contains(thFilter)) filterList.add(thFilter); 
		return svc;
    }

	/** find schedule viewer configuration
	 * 
	 * @param name name of new schedule viewer configuration to find
	 * @param scheduleConfigs resource list of schedule viewer configurations
	 * @return configuration with given name or null if not existing
	 */
    public static ScheduleViewerConfig getConfigByName(String name, ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	for(ScheduleViewerConfig svc: scheduleConfigs.getAllElements()) {
    		if((svc.name().exists())&&(svc.name().getValue().equals(name))) return svc;
    	}
    	return null;
    }
	/** find schedule viewer configuration or create it if not existing yet
	 * 
	 * @param name name of new schedule viewer configuration to find
	 * @param scheduleConfigs resource list of schedule viewer configurations
	 * @return configuration with given name
	 */
    public static ScheduleViewerConfig getOrCreateConfigByName(String name, ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	ScheduleViewerConfig svc = getConfigByName(name, scheduleConfigs);
    	svc = createAndUpdate(svc, name, scheduleConfigs);
    	return svc;
    }
    
    /**Configure logging for resource, add it to a ScheduleViewer configuration and configure it
	 * for remote supervision
	 * @param fres resource to activate logging for
	 * @param configName name of ScheduleViewer configuration set to which log shall be
	 * added. If the set does not yet exist it will be created
	 * @param label label of the log to be used for the ScheduleViewer entry and for remote supervision.
	 * If an existing entry is found and the label is different the label will be updated. 
	 * @param scheduleConfigs ResourceList where to search for the configuration set with
	 * name configName and where to add a new configuration set if it does not yet
	 * exist
	 * @param remoteTransfer add the log to list of logs to be transferred to the remote supervision
	 * server. If null no registration will take place. The registration will only be made
	 * if the log is not registered yet.
	 * @return ScheduleViewer entry representing the log (newly created or existing entry for the log)
	 */
    public static ScheduleViewerConfig addResourceToLog(SingleValueResource fres,
    		String configName, String label,
    		ResourceList<ScheduleViewerConfig> scheduleConfigs,
    		GatewayTransferInfo remoteTransfer) {
    	LogHelper.activateLogging(fres);
    	return addTimeSeriesConfig(fres, configName, label, scheduleConfigs, remoteTransfer);
    }
    /** Like {@link #addResourceToLog(SingleValueResource, String, String, ResourceList, GatewayTransferInfo)},
     * but do not activate logging (use for schedules or RecordedData this is already configured)
     * @param fres
     * @param configName
     * @param label
     * @param scheduleConfigs
     * @param remoteTransfer
     * @return
     */
    public static ScheduleViewerConfig addTimeSeriesConfig(ValueResource fres,
    		String configName, String label,
    		ResourceList<ScheduleViewerConfig> scheduleConfigs,
    		GatewayTransferInfo remoteTransfer) {
    	ScheduleViewerConfig svc = getOrCreateConfigByName(configName, scheduleConfigs);
    	LogHelperOwnList.cleanSchedToView(svc.timeSeriesData());
    	addSchedToView(svc.timeSeriesData(), fres, label, remoteTransfer);
    	svc.activate(true);
    	return svc;
    }
	
	/**Configure logging for resource, add it to a ScheduleViewer configuration and configure it
	 * for remote supervision
	 * @param tlist schedule viewer configuration to add log of fres to if not in the list already
	 * @param fres resource to activate logging for
	 * @param label label of the log to be used for the ScheduleViewer entry and for remote supervision.
	 * If an existing entry is found and the label is different the label will be updated. 
	 * @param remoteTransfer add the log to list of logs to be transferred to the remote supervision
	 * server. If null no registration will take place. The registration will only be made
	 * if the log is not registered yet.
	 * @return ScheduleViewer entry representing the log (newly created or existing entry for the log)
	 */
    public static TimeSeriesPresentationData addSchedToView(ResourceList<TimeSeriesPresentationData> tlist,
    		ValueResource fres, String label, GatewayTransferInfo remoteTransfer) {
    	TimeSeriesPresentationData result = null;
    	for(TimeSeriesPresentationData tsp: tlist.getAllElements()) {
    		if(tsp.scheduleLocation().getValue().equals(fres.getLocation())) {
    			if(!label.equals(tsp.name().getValue())) {
    				tsp.name().<StringResource>create().setValue(label);
    				tsp.name().activate(false);
    			}
    			result = tsp;
    		}
    	}
    	if(result == null) result = addSchedToViewForce(tlist, fres, label);
    	if(remoteTransfer != null) {
    		DataLogTransferInfo dltfound = null;
        	for(DataLogTransferInfo dlt: remoteTransfer.dataLogs().getAllElements()) {
        		if(dlt.clientLocation().getValue().equals(fres.getLocation())) {
        			dltfound = dlt;
           			if(!label.equals(dlt.name().getValue())) {
        				dlt.name().<StringResource>create().setValue(label);
        				dlt.name().activate(false);
        			}
        		}
        	}
        	if(dltfound == null) addDataLogRemoteTransferForce(fres, label, remoteTransfer);
    	}
    	return result;
    }
    
    public static void cleanSchedToView(ResourceList<TimeSeriesPresentationData> tlist) {
    	List<String> knownLocs = new ArrayList<>();
    	for(TimeSeriesPresentationData tsp: tlist.getAllElements()) {
    		String loc = tsp.scheduleLocation().getValue();
    		if(knownLocs.contains(loc)) {
    			tsp.delete();
    		} else {
    			knownLocs.add(loc);
    		}
    	}
    }
    
    private static TimeSeriesPresentationData addSchedToViewForce(ResourceList<TimeSeriesPresentationData> tlist,
        		ValueResource fres, String label) {
    	TimeSeriesPresentationData tsp = tlist.add();
    	tsp.scheduleLocation().<StringResource>create().setValue(fres.getLocation());
    	tsp.name().<StringResource>create().setValue(label);
    	return tsp;
    }
    private static DataLogTransferInfo addDataLogRemoteTransferForce(
    		ValueResource fres, String label, GatewayTransferInfo remoteTransfer) { 
    	if(remoteTransfer != null) {
    		if(!remoteTransfer.dataLogs().isActive()) {
    			remoteTransfer.dataLogs().create().activate(false);
    		}
	    	DataLogTransferInfo remoteTS = remoteTransfer.dataLogs().add();
	    	remoteTS.clientLocation().<StringResource>create().setValue(fres.getLocation());
	    	// not used any more
//	    	remoteTS.transferInterval().timeIntervalLength().type().<IntegerResource>create().setValue(AbsoluteTiming.DAY);
	    	remoteTS.name().<StringResource>create().setValue(label);
	    	remoteTS.activate(true);
	    	return remoteTS;
    	}
    	return null;
    }
 }
