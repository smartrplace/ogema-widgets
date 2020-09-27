package de.iwes.ogema.bacnet.models;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface BACnetSubdevice extends PhysicalElement {
    StringResource description();
}
