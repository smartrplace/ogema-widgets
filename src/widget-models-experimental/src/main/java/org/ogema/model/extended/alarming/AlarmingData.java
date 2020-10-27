package org.ogema.model.extended.alarming;

import org.ogema.core.model.ResourceList;
import org.ogema.model.prototypes.Data;
import org.smartrplace.apps.hw.install.config.InstallAppDevice;

/** Note that {@link AlarmConfiguration}s are store in {@link InstallAppDevice}s, not in
 * this configuration
 */
public interface AlarmingData extends Data {
	ResourceList<AlarmGroupData> ongoingGroups();
}
