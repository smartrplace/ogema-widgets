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
package org.ogema.model.action;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.alignedinterval.RepeatingOperationConfiguration;

/** Configuration of action that collects some kind of backup information into
 * a repository (typically this destination is &lt;rundir&gt;/data/extBackup/&lt;appDir&gt;)*/
public interface BackupAction extends Action {
	/**Typically name of external application which data shall be back-up'ed. This
	 * usually defines the backup source files*/
	StringResource backupDataName();
	
	FilesGenerationOperation destinationConfig();
	
	@Deprecated
	StringResource backupDestination();
	/**The backup results may be zipped by the operation or they may be just transferred to the
	 * destination for later Zipping/transfer to backup storage
	 * @deprecated Use {@link FilesGenerationOperation#doZip() destinationConfig.doZip} instead 
	 */
	@Deprecated
	BooleanResource doZip();
	
	@Deprecated
	RepeatingOperationConfiguration backupTimes();
	
	/** If this field is set to false the action shall not be performed via a timer, but shall be triggered
	 * by the transfer action
	 * 
	 * @return
	 */
	BooleanResource triggerByTimer();
}
