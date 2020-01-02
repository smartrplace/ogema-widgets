package org.ogema.model.viewpattern;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.ranges.TimeRange;

public interface TimeValueConfiguration extends SingleValueConfiguration {
	TimeRange allowedRange();
	ResourceList<SpecialValue> specialValues();
	
	TimeResource defaultValue();
}
