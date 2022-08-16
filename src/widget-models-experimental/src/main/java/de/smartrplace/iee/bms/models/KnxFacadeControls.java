package de.smartrplace.iee.bms.models;

import org.ogema.model.actors.OnOffSwitch;
import org.ogema.model.prototypes.PhysicalElement;

/**
 *
 * @author jlapp
 */
public interface KnxFacadeControls extends PhysicalElement {
	
	OnOffSwitch windowCleaning();
	
	OnOffSwitch shutterCleaning();
	
	OnOffSwitch windAlert();
	
	OnOffSwitch windClearance();
	
}
