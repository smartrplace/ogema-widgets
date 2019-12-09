package de.iwes.timeseries.testdata.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;

/**
 * Generates different kinds of timeseries for testing purposes:
 * <ul>
 *  <li>schedules (future and past values)
 *  <li>log data 
 *  <li>online data (resources with changing values) (use logged resources)
 *  <li>sema server-specific testdata (slotsdb data and resources) - optional, if the project bundles are available 
 * </ul>
 */
@Service(Application.class)
@Component
public class TimeSeriesData implements Application {

	private final static String RESOURCE_PREFIX = "timeseriesEvalTestdata";
	
	@Override
	public void start(ApplicationManager appManager) {
		if (appManager.getResourceAccess().getResource(RESOURCE_PREFIX) != null) // unclean start
			return;
		@SuppressWarnings("unchecked")
		ResourceList<FloatResource> floats = appManager.getResourceManagement().createResource(RESOURCE_PREFIX, ResourceList.class);
		floats.setElementType(FloatResource.class);
		final long startTime = appManager.getFrameworkTime();
		SchedulesGeneration.run(floats, startTime);
		LogDataGeneration.run(floats, appManager);
		floats.activate(true);
		
		try {
			SemaDataGeneration.importLogdata(true, appManager);
		} catch (NoClassDefFoundError | Exception e) {
			appManager.getLogger().info("Could not import gateway data: " + e);
		}
		
	}

	@Override
	public void stop(AppStopReason reason) {
		// TODO Auto-generated method stub
		
	}

}
