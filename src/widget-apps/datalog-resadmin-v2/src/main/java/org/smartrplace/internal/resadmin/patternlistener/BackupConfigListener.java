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
package org.smartrplace.internal.resadmin.patternlistener;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.resourcemanager.pattern.PatternListener;

import org.smartrplace.internal.resadmin.pattern.BackupConfigPattern;

import de.iwes.util.resource.ValueResourceHelper;

/**
 * A pattern listener for the TemplateContextPattern. It is informed by the framework 
 * about new pattern matches and patterns that no longer match.
 */
public class BackupConfigListener implements PatternListener<BackupConfigPattern> {
	
	// not used
	private final List<BackupConfigPattern> availablePatterns = new ArrayList<>();
	
 	/** Note that in the pattern accept method you have access to the app controller context
 	 * in this template/listener variant
 	 */
	@Override
	public void patternAvailable(BackupConfigPattern pattern) {
		availablePatterns.add(pattern);
		if(pattern.stateControl.getValue()) pattern.controlListener.resourceChanged(pattern.stateControl);
		ValueResourceHelper.referenceIfNew(pattern.action.description(), pattern.model.name());
	}
	
	@Override
	public void patternUnavailable(BackupConfigPattern pattern) {
		// TODO process remove
		
		availablePatterns.remove(pattern);
	}
	
	
}
