package org.ogema.model.viewpattern;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface StatusLineElement extends Data {
	/**Type to which the status bar element refers. This may be non-existing or empty. In this
	 * case the element path must be absolute.*/
	StringResource baseType();
	
	PatternElement element();
}
