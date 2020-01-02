package org.ogema.model.viewpattern;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface TablePattern extends Data {
	/**Resource type of pattern*/
	StringResource modelType();
	
	ResourceList<PatternElement> elements();
	
	/** If true the table shall have  a dropdown above allowing to choose whether also
	 * inactive resources shall be displayed (active/inactive/both)
	 */
	BooleanResource inactiveDropdown();
	
	/**Filter options to be offered; needs to be developed in the future*/
	StringResource filterOptions();
}
