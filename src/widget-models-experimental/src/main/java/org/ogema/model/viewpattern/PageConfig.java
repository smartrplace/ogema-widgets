package org.ogema.model.viewpattern;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface PageConfig extends Data {
	/**Title of page*/
	StringResource title();
	
	ResourceList<TablePattern> tables();
	
	ResourceList<StatusLineElement> statusLine();
}
