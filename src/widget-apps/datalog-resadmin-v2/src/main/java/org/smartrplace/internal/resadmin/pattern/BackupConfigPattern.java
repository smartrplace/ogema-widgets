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
package org.smartrplace.internal.resadmin.pattern;

import org.ogema.core.model.Resource;
import org.ogema.util.action.SubActionPattern;
//import org.ogema.util.pattern.SubActionPattern;
import org.smartrplace.internal.resadmin.ResAdminController;
import org.smartrplace.resadmin.config.BackupConfig;

/**
 * A variant of a ResourcePattern, which is context sensitive. This means, that a context object
 * is injected upon creation. 
 */
public class BackupConfigPattern extends SubActionPattern<BackupConfig, ResAdminController> { 
	public static final String APP_NAME = "datalog-resadmin";

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework. Must be public
	 */
	public BackupConfigPattern(Resource device) {
		super(device);
	}
	
	protected String getControllingApplication() {
		return APP_NAME;
	}

	protected void performAction() {
		context.runBackup(model);
	}
	
}
