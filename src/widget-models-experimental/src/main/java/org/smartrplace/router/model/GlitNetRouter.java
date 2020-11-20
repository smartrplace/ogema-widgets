package org.smartrplace.router.model;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface GlitNetRouter extends PhysicalElement {
	@Override
	StringResource name();
	
	/** Number of IPv4 addresses found*/
	IntegerResource numberIP4AddressesHM();
	IntegerResource numberIP4AddressesSSH();
	
	/** Number of non-IPv4 addresses found*/
	IntegerResource numberIP6PlusAddressesHM();
	IntegerResource numberIP6PlusAddressesSSH();
}
