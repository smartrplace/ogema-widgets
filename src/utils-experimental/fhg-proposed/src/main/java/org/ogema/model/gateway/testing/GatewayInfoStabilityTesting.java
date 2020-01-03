package org.ogema.model.gateway.testing;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.gateway.master.GatewayInfo;
import org.ogema.model.prototypes.PhysicalElement;

/**
 * Use {@link PhysicalElement#name()} for a human readable name 
 *  
 * @author cnoelle
 */
public interface GatewayInfoStabilityTesting extends GatewayInfo {
	/** 
	 * Subresources for information provided or read by rpimaster App
	 */
	StringResource state();
	TimeResource lastStateChange();
	TimeResource plannedStateChange();
	IntegerResource resetCount();
	BooleanResource resetRequest();
	IntegerResource plugStateCorrectCounter();
	IntegerResource reviveCounter();
}