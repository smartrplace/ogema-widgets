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
	
	/**
	 * Used as sensor, read feedback for wind alert as reported by the
	 * weather stations.
	 * 
	 * @return wind alert sensor, only read feedback.
	 */
	OnOffSwitch windAlert();
	
	OnOffSwitch windClearance();
	
	OnOffSwitch shutterAutomation();
	
}
