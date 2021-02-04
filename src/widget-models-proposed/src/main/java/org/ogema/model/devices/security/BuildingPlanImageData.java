package org.ogema.model.devices.security;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface BuildingPlanImageData extends Data {
	StringResource planImagePath();
	ResourceList<BuildingPlanAreaData> areaData();
}
