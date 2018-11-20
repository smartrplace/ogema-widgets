/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.smartrplace.resadmin.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.action.BackupAction;
//import org.ogema.model.action.Action;
import org.ogema.model.prototypes.Configuration;

/** put an instance of this resource for each program into a ResourceList*/
public interface BackupConfig extends Configuration {
	BackupAction run();

	public StringResource destinationDirectory();
//	no need ?
//	ResourceList<KnownBackups> knownBackups();
	BooleanResource overwriteExistingBackup();
	/** include rooms, users references*/
	BooleanResource includeStandardReferences();
	BooleanResource backupAllExceptResoucesIncluded();
	/**if false write XML*/
	BooleanResource writeJSON();

	/** non-existent or non-positive if not auto-backup shall be performed*/
	TimeResource autoBackupInterval();
	TimeResource nextBackupScheduled();

//	LegalEntity owner();
	
	/** including elements of resource lists*/
	// TODO replace by StringArrayResource?
	ResourceList<StringResource> topLevelResourcesIncluded();
	//ResourceList<ResourceTypeConfigurations> resourceTypeConfigs();
	
}
