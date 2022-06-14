package de.smartrplace.iee.bms.models;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.actors.MultiSwitch;
import org.ogema.model.actors.OnOffSwitch;

/**
 * Note: this is only a draft. Will probably not be a ResourcePattern. For now
 * treat lights OnOffSwitch as pattern base model.
 *
 * @author jlapp
 */
public class LightingControlModel extends ResourcePattern<OnOffSwitch> {

    public final OnOffSwitch sunblindUpDown
            = model.getSubResource("sunblindUpDown", OnOffSwitch.class);
    public final OnOffSwitch sunblindSlatsStep
            = model.getSubResource("sunblindSlatsStep", OnOffSwitch.class);
    /*
     * True iff the sunblind is currently moving.
     * Probably not possible: there is not feedback when sunblind reaches top / bottom
     */
    /*
    public final BooleanResource sunblindMoving
            = model.getSubResource("sunblindMoving", BooleanResource.class);
    */

    public final MultiSwitch lightingPercentage
            = model.getSubResource("lightingPercentage", MultiSwitch.class);

    public LightingControlModel(OnOffSwitch sw) {
        super(sw);
        activate();
    }

    private void activate() {
        /*
        // needs some internal wiring for sunblind moving state
        sunblindUpDown.stateFeedback().addValueListener(br -> {
            if (!sunblindMoving.getValue()) {
                sunblindMoving.setValue(true);
            }
        }, true);
        sunblindSlatsStep.stateFeedback().addValueListener(br -> {
            if (sunblindMoving.getValue()) {
                sunblindMoving.setValue(false);
            }
        }, true);
        */
    }
    
    public void sunblindMove(boolean up) {
        sunblindUpDown.stateControl().setValue(up);
    }
    
    public void sunblindStop() {
        //if (sunblindMoving.getValue()) { //XXX maybe better to write always
            sunblindSlatsStep.stateControl().setValue(true);
        //}
    }
    
    public void setLightState(boolean on) {
        model.stateControl().setValue(on);
    }
    
    public void getLightState() {
        //XXX do we get that?
        model.stateFeedback().getValue();
    }
    
    /**
     * @param pct value range is 0..1
     */
    public void setLightPercentage(float pct) {
        lightingPercentage.stateControl().setValue(pct);
    }
    
    public float getLightPercentage() {
        return lightingPercentage.stateFeedback().getValue();
    }

}
