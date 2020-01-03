package de.iwes.util.logconfig.plus;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.locations.Room;
import org.ogema.model.scheduleviewer.config.ScheduleViewerConfig;
import org.ogema.model.scheduleviewer.config.TimeSeriesPresentationData;
import org.ogema.tools.resource.util.ResourceUtils;

/** Helper for managing own ResourceLists of element type {@link ScheduleViewerConfig}
 * Note that is is recommended in most cases to use the system lists provided by the
 * methods in {@link LogHelperPlus} that do not take such a ResourceList as input. 
 */
public class LogHelperScheduleViewerConfigMmgt {
    private static ScheduleViewerConfig addTSView(String name, ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	ScheduleViewerConfig svc = scheduleConfigs.add();
    	svc.hideSelection().<BooleanResource>create().setValue(true);
		svc.timeSeriesData().create();
		svc.name().<StringResource>create().setValue(name);
		return svc;
    }
    private static ScheduleViewerConfig createAndUpdate(ScheduleViewerConfig svc, String name,  ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	if(svc == null) {
    		svc = addTSView(name, scheduleConfigs);
    	}
		return svc;
    }
    
    /**Get schedule viewer configuration
     * 
     * @param name name of schedule viewer configuration to be returned
     * @param scheduleConfigs list of schedule viewer configurations to be searched
     * @return schedule viewer configuration with name requested or null if no such configuration exists
     */
    public static ScheduleViewerConfig getConfigByName(String name, ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	for(ScheduleViewerConfig svc: scheduleConfigs.getAllElements()) {
    		if((svc.name().exists())&&(svc.name().getValue().equals(name))) return svc;
    	}
    	return null;
    }
    /**Get schedule viewer configuration or create it if not yet existing<br>
     * Note that in most cases {@link LogHelperPlus#addResourceToLog(org.ogema.core.model.simple.SingleValueResource, String, String, org.ogema.model.gateway.remotesupervision.GatewayTransferInfo, ApplicationManager)
     * should be used.
     * 
     * @param name name of schedule viewer configuration to be returned
     * @param scheduleConfigs list of schedule viewer configurations to be searched
     * @return schedule viewer configuration with name requested
     */
   public static ScheduleViewerConfig getOrCreateConfigByName(String name, 
    		ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	ScheduleViewerConfig svc = getConfigByName(name, scheduleConfigs);
    	svc = createAndUpdate(svc, name, scheduleConfigs);
    	return svc;
    }
   /**Get schedule viewer configuration for a room
    * 
    * @param room room for which schedule viewer configuration shall be found
    * @param scheduleConfigs list of schedule viewer configurations to be searched
    * @return schedule viewer configuration for room requested or null if no such configuration exists
    */
    public static ScheduleViewerConfig getConfigForRoom(Room room,
    		ResourceList<ScheduleViewerConfig> scheduleConfigs,
    		ApplicationManager appMan) {
    	for(ScheduleViewerConfig svc: scheduleConfigs.getAllElements()) {
    		for(TimeSeriesPresentationData tsp: svc.timeSeriesData().getAllElements()) {
    			Resource r = appMan.getResourceAccess().getResource(tsp.scheduleLocation().getValue());
    			if((r!=null)) {
    				Room schedR = ResourceUtils.getDeviceLocationRoom(r);
    				if((schedR != null)&&(schedR.equalsLocation(room))) return svc;
    			}
    		}
    	}
    	return null;
    }
    /**Get schedule viewer configuration for a room or create it if not yet existing<br>
     * Note that in most cases {@link LogHelperPlus#addResourceToRoomLog(org.ogema.core.model.simple.SingleValueResource, String, org.ogema.model.gateway.remotesupervision.GatewayTransferInfo, ApplicationManager)
     * should be used.
     * 
     * @param room room for which schedule viewer configuration shall be found
     * @param scheduleConfigs list of schedule viewer configurations to be searched
     * @return schedule viewer configuration for room requested
     */
    public static ScheduleViewerConfig getOrCreateConfigForRoom(Room room, 
    		ResourceList<ScheduleViewerConfig> scheduleConfigs, ApplicationManager appMan) {
    	ScheduleViewerConfig svc = getConfigForRoom(room, scheduleConfigs, appMan);
    	svc = createAndUpdate(svc, ResourceUtils.getHumanReadableName(room), scheduleConfigs);
    	return svc;
    }
    
    /**Helper to be used for schedule configuration lists that need to be cleaned, e.g. due to an
     * update of the managing application.
     */
    public static void cleanScheduleViewerList(ResourceList<ScheduleViewerConfig> scheduleConfigs) {
    	List<String> knownLocs = new ArrayList<>();
    	for(ScheduleViewerConfig tsp: scheduleConfigs.getAllElements()) {
    		String loc = tsp.name().getValue();
    		if(knownLocs.contains(loc)) {
    			tsp.delete();
    		} else {
    			knownLocs.add(loc);
    		}
    	}
   	
    }
}
