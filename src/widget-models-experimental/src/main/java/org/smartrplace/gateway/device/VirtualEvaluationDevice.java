package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.prototypes.PhysicalElement;

/** Virtual device for development and evaluation tasks, typically tasks for
 * special projects. To be addded as decorator to a special model or to be
 * extended by special models. The DeviceHandler shall take care of the different
 * applications of the model.
 *
 */
public interface VirtualEvaluationDevice extends PhysicalElement {
	/** Set this as reference to the main result FloatResource the specific model
	 * provides for the generic DeviceHandler
	 */
	FloatResource mainResultValue();
}
