/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.action.spextended;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.action.Action;

/** Configuration of action performing backup of OGEMA system (typically this destination is <rundir>/data/extBackup/ogemaRundir)*/
public interface OGEMABackupAction extends BackupAction_$SRP$EP {
	/**Typically name of external application which data shall be back-up'ed. This
	 * usually defines the backup source files*/
	BooleanResource backupDatabase();
	BooleanResource backupConfigDir();
	BooleanResource backupSlotsDB();
	
	Action resAdminBackupAction();
}
