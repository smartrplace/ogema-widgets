package org.ogema.model.gateway;

import org.ogema.core.model.ResourceList;
import org.ogema.model.devices.security.BuildingPlanImageData;
import org.ogema.model.devices.security.BuildingSupervisionCamera;
import org.ogema.model.prototypes.PhysicalElement;

/** Data that is managed for the application REST API, usually defined by instances outside
 * OGEMA that have no own database
 */
public interface APIDataCollection extends PhysicalElement {
	ResourceList<BuildingSupervisionCamera> cameras();
	ResourceList<BuildingPlanImageData> buildingPlanImages();
}
