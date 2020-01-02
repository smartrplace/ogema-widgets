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

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.action.Action;
import org.ogema.model.action.BackupAction;

/** Configuration of action that collects some kind of backup information into
 * a repository (typically this destination is <rundir>/data/extBackup/<appDir>)*/
public interface BackupAction_$SRP$EP extends BackupAction {
	//TODO: Request to add to model
	ProgramHistoryManagement history();
	
	//TODO: Request to add to model
	StringResource identifierSelected();
	//TODO: Request to add to model
	/**Replay backup identified by identifierSelected (identifier according to history list)*/
	Action replay();
}
