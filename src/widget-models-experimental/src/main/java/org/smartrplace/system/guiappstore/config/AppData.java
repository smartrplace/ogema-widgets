package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** 
 * Data for apps and groups of them
 */
public interface AppData extends Data {
    @SuppressWarnings("deprecation")
	ResourceList<AppGroupData> groups();

	/** Path to source code repository from which app artifacts are generated*/
	StringResource repoPath();

}
