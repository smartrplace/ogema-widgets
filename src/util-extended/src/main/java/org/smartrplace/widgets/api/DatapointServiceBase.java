package org.smartrplace.widgets.api;

import org.ogema.model.prototypes.PhysicalElement;
import org.smartrplace.apps.hw.install.config.InstallAppDevice;

public interface DatapointServiceBase {
	public InstallAppDevice getMangedDeviceResource(PhysicalElement device);
}
