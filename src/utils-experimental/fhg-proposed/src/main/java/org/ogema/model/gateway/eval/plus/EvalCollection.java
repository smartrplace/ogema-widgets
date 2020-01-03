package org.ogema.model.gateway.eval.plus;

import org.ogema.core.model.ResourceList;
import org.ogema.model.scheduleviewer.config.ScheduleViewerConfig;

/** Collection of evaluation information provided on the OGEMA instance
 */
public interface EvalCollection extends org.ogema.model.gateway.EvalCollection {
	//ResourceList<IncidentProvider> incidentProviders();
	//ResourceList<IncidentAutoClearanceConfiguration> incidenceAutoClearanceConfigs();
	//ResourceList<IncidentNotificationTriggerConfiguration> incidentTriggerConfigs();
	/**@deprecated: use incidentProviders instead*/
	//@Deprecated
	//IncidentManagement incidents();
	ResourceList<ScheduleViewerConfig> scheduleConfigs();
	ScheduleViewerConfig scheduleViewerConfigStandard();
}