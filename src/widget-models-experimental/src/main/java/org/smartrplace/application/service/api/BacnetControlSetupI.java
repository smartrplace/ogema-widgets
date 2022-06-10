package org.smartrplace.application.service.api;

import java.util.Map;

import org.ogema.core.model.Resource;

public interface BacnetControlSetupI {
	public Map<String, Resource> getResourcesByVendorSpecificAddress();
}
