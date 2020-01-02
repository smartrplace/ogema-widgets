package org.ogema.model.viewpattern;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;

public interface FloatValueConfiguration extends SingleValueConfiguration {
	ResourceList<SpecialValue> specialValues();
	
	StringResource defaultValue();
}
