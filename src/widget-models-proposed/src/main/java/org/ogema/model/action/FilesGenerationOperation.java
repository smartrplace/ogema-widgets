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
import org.ogema.model.prototypes.Data;

/** Configuration for operations generating files such as compressing one or
 * several files into an archive or retrieving one or several files from
 * external sources*/
public interface FilesGenerationOperation extends Data {
	/**destination directory or file name*/
	StringResource path();
	
	/**The results may be zipped by the operation or they may be just transferred to the
	 * destination. The Zip file may be placed at the parent directory (standard)
	 * or in the same directory (depending on application)
	 */
	BooleanResource doZip();
	
	/** If true for each operation a separate directory is created containing
	 * date and time information. This makes sure that subsequent creation of the same file
	 * name does not overwrite and the directories can be used for housekeeping
	 */
	BooleanResource createDateDirectories();
	
	RepeatingOperationConfiguration operationTimes();
}
