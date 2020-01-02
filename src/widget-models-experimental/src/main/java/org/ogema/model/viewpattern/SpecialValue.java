package org.ogema.model.viewpattern;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface SpecialValue extends Data {
	/**Use this for all SingleValueResources. For String resources certain
	 * numeric values may have a special meaning.*/
	FloatResource value();
	@Override
	/**Name to be displayed for this option*/
	StringResource name();
}
