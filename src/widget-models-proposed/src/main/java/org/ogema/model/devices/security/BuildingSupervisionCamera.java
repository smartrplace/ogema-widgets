package org.ogema.model.devices.security;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface BuildingSupervisionCamera extends PhysicalElement {
	StringResource url();
	/** TODO: What does this mean*/
	StringResource group();
}
