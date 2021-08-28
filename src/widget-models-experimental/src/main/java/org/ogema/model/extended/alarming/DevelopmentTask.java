package org.ogema.model.extended.alarming;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;
import org.smartrplace.apps.hw.install.config.InstallAppDeviceBase;

/** A development task represents a general issue that may be relevant to more than one gateway.
 * It is also possible that a DevelopmentTask is only relevant for a single gateway
 * A development task can represent special alarming settings for the devices that refer to the DevTask
 *
 */
public interface DevelopmentTask extends Data {
	/** Human readable name if provided*/
	@Override
	StringResource name();
	
	StringResource comment();
	
	/** Link to bug/task tracking tool where alarm is processed by support.
	 * This link shall also be used for inter-gateway/superior identification*/
	StringResource linkToTaskTracking();
	
	/** For now only the value {@link AlarmConfiguration#maxIntervalBetweenNewValues()} is used. Others
	 * alarming parameters may be used in the future.<br>
	 * Virtual template devices are generated having a non-reference empty device() field. Also the
	 * deviceHandlerID is relevant, of course.*/
	ResourceList<InstallAppDeviceBase> templates();
	
	/** If true the next resource assigend to the task will copy its alarming settings into
	 * the task overwriting any existing settings. Otherwise only the first resource of a
	 * deviceHandler type will copy its alarming settings as initial template settings
	 */
	BooleanResource overWriteTemplateRequest();
}
