package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** 
 * Data representing a software update over all bundles and repositories
 */
public interface SystemUpdate extends Data {
	/** All commits up to this time shall be included into the system update*/
	TimeResource commitMaxTime();
	
	/** Time when the artifact generation was triggered*/
	TimeResource generationTime();
	
	/** Time when all artifacts where deployed. Note that this may not be set if the feedback
	 * was not received, e.g. because of a restart of the appstore instance*/
	TimeResource generationFinishTime();
	
	/** Only relevant for local appstore. In this case bundles are used that are built earlier, so
	 * this information shall be stored separately
	 */
	TimeResource buildTime();
	
	/** Last part of version of bundles in update, which shall be a unique index*/
	IntegerResource updateIndex();
	
	/** List of bundles in update, repoPath shall be remoteUrl*/
	StringArrayResource repoPath();
	StringArrayResource mavenCoordUnmversioned();
	StringArrayResource version();
	
	//Dynamic data
	/** 0 : unknown
	** 1 : Untested
	** 2 : Approved for Test Group
	** 3 : Approved for Main Group
	** 4 : In Long-term operation*/
	IntegerResource approvalStatus();
	StringResource comment();
	
	/** Reference to another system update that determines the versions of all bundles that are not
	 * in this SystemUpdate
	 */
	SystemUpdate parentUpdate();
}
