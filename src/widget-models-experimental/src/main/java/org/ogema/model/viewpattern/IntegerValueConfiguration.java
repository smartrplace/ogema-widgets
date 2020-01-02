package org.ogema.model.viewpattern;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.ranges.IntegerRange;

public interface IntegerValueConfiguration extends SingleValueConfiguration {
	IntegerRange allowedRange();
	ResourceList<SpecialValue> specialValues();
	
	IntegerResource defaultValue();
}
