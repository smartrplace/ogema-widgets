package org.ogema.model.devices.security;

import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;

public interface BuildingPlanAreaData extends Data {
	Room room();
	StringResource shape();
	IntegerArrayResource coords();
}
