package de.smartrplace.iee.bms.models;

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
    
    OnOffSwitch sunblindUpDown();
    
    OnOffSwitch sunblindSlatsStep();
    
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
    
    default void dimLights(boolean increase, int step) {
        dimmerStep().create();
        dimmerStep().setValue(step | (increase ? 8 : 0));
        dimmerStep().activate(false);
    }
    
    /**Provided by roomcontrol logic*/
    FloatResource sunblindEstimatedPosition();
}