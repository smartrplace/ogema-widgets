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

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.alignedinterval.RepeatingOperationConfiguration;

/** Action that fetches data from external system via SCP. The destination typically
 * is $home/ogemaCollect/&lt;appDir&gt;.<br>
 * TODO: An element / mechanisms should be added to make source we can save a gatewayId
 * information from the source gateway to identify the source. For now we just store
 * the URL in the source info.*/
public interface SCPDataCollectionAction extends Action {
	/**Typically name of external application which data shall be back-up'ed*/
	StringResource host();
	/**If not available, use port 22*/
	IntegerResource port();
	/**File or directory to be used as data source. If a source directory is specified it will be zipped
	 * before sending the data
	 */
	StringResource sourcePath();
	/**User name to be used for login to the remote server*/
	StringResource userName();
	/**Path to a certificate to be used for authentication. Only either password or certificate should
	 * be active.
	 */
	StringResource certPath();
	/** Use certificate for securtiy reasons. Provide password only for testing
	 * purposes and change later on*/
	StringResource password();
	
	/** If true the file(s) shall be sent to host, not fetched from host. In this case the
	 * sourcePath must be a directory the content of which is zipped and sent out. Note that the
	 * destination directory currently has to exist on the server pushed to as automated
	 * creation may not be supported by the implementation.*/
	BooleanResource pushOperation();
	/**Only used in push mode to generate source info file*/
	StringResource sourceInfo();
	//TODO: Removing this might be critical
	//FileTransmissionTaskData fileTransmissionTaskData();

	FilesGenerationOperation destinationConfig();
	@Deprecated
	StringResource destination();
	@Deprecated
	RepeatingOperationConfiguration backupTimes();
	
	/**Actions to perform before the transfer shall take place*/
	ResourceList<Action> collectionActions();
	
	/**If the resource exists it is listened to like to Action.stateControl. When the size is set to
	 * a positive value the newest files from the source directory are collected up to the maximum
	 * source size of controlByMaxSizeKb kilobytes, the files are zipped and transmitted.
	 * Afterwards the resource is set back to zero.
	 */
	IntegerResource controlByMaxSizeKb();
}
