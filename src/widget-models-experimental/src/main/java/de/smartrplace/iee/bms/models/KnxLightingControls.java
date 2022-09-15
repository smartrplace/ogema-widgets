package de.smartrplace.iee.bms.models;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.actors.MultiSwitch;
import org.ogema.model.actors.OnOffSwitch;
import org.ogema.model.prototypes.PhysicalElement;

/**
 *
 * @author jlapp
 */
public interface KnxLightingControls extends PhysicalElement {
    
    OnOffSwitch lights();
    
    MultiSwitch lightingPercentage();
    
    /** Write true to start shutter going up, false to start going down*/
    OnOffSwitch sunblindUpDown();
	
    /** True: shutter reached top-most position*/
	BooleanResource sunblindTop();
	
    /** True: shutter reached bottom-most position*/
	BooleanResource sunblindBottom();
    
	/** Short button press (control: emulation). True: short up, false: short down*/
    OnOffSwitch sunblindSlatsStep();
    
	@Deprecated
    IntegerResource dimmerStep();
    
    default void sunblindMove(boolean up) {
        sunblindUpDown().stateControl().setValue(up);
    }
    
    default void sunblindStop() {
        sunblindSlatsStep().stateControl().setValue(true);
    }
    
    default void setLightState(boolean on) {
        lights().stateControl().setValue(on);
    }
    
    default void getLightState() {
        //XXX do we get that?
        lights().stateFeedback().getValue();
    }
    
    /**
     * @param pct value range is 0..1
     */
    default void setLightPercentage(float pct) {
        lightingPercentage().stateControl().setValue(pct);
    }
    
    default float getLightPercentage() {
        return lightingPercentage().stateFeedback().getValue();
    }
    
	@Deprecated
    default void dimLights(boolean increase, int step) {
        dimmerStep().create();
        dimmerStep().setValue(step | (increase ? 8 : 0));
        dimmerStep().activate(false);
    }
    
    /**Provided by roomcontrol logic.
     * 0.0: Max Up
     * 1.0: Max Down*/
    FloatResource sunblindEstimatedPosition();
}
