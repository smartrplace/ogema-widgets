package org.ogema.model.gateway.master;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;
	
/** External log data
*/
public interface BackupData extends Data {
	StringResource localDirectory();
	TimeResource lastBackupTime();
	IntegerResource lastBackupSize();
	//ResourceList<StringResource> knownBackups();
}