package de.iwes.util.logconfig.plus;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.model.gateway.EvalCollection;
import org.ogema.model.scheduleviewer.config.ScheduleViewerConfig;

import de.iwes.util.logconfig.EvalHelper;


public class EvalHelperPlus {
	
	public static ResourceList<ScheduleViewerConfig> getScheduleConfigs(ApplicationManager appMan) {
		EvalCollection config = EvalHelper.getEvalCollection(appMan);
		return config.getSubResource("scheduleConfigs", ResourceList.class);
	}
	
	public static ScheduleViewerConfig getScheduleViewerConfigStandard(ApplicationManager appMan) {
		EvalCollection config = EvalHelper.getEvalCollection(appMan);
		return config.getSubResource("scheduleViewerConfigStandard", ScheduleViewerConfig.class);
	}
}
