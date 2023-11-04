package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.extended.alarming.AlarmGroupData;
import org.ogema.model.prototypes.Data;

public interface DeviceTypeData extends Data {
	StringResource deviceHandlerId();

	/** All deviceType-depdendet faults of the same device handler shall be considered
	 * having the same analysis, but shall not be
	 * released automatically with the devicetype parent.
	 */
	AlarmGroupData deviceTypeFaultParent();
}
