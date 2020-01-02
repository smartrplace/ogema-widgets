package org.ogema.model.viewpattern;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.ranges.GenericFloatRange;

public interface StringValueConfiguration extends SingleValueConfiguration {
	GenericFloatRange allowedRange();
	ResourceList<SpecialValue> specialValues();
	
	FloatResource defaultValue();
}
