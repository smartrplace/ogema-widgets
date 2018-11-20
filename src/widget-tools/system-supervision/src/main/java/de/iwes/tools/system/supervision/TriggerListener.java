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
package de.iwes.tools.system.supervision;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.resourcemanager.ResourceValueListener;

import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

public class TriggerListener implements ResourceValueListener<BooleanResource> {

	private final SystemSupervisionConfig config;
	private final Tasks tasks;
	
	public TriggerListener(ApplicationManager appMan, SystemSupervisionConfig config, Tasks tasks) {
		this.config = config;
		this.tasks = tasks;
		config.triggerDiskCheck().<BooleanResource> create().activate(false);
		config.triggerMemoryCheck().<BooleanResource> create().activate(false);
		config.triggerResourceCheck().<BooleanResource> create().activate(false);
		config.triggerDiskCheck().addValueListener(this, true);
		config.triggerMemoryCheck().addValueListener(this, true);
		config.triggerResourceCheck().addValueListener(this, true);
	}

	void close() {
		config.triggerMemoryCheck().removeValueListener(this);
		config.triggerResourceCheck().removeValueListener(this);
		config.triggerDiskCheck().removeValueListener(this);
	}

	@Override
	public void resourceChanged(BooleanResource resource) {
		if (resource.equalsLocation(config.triggerDiskCheck())) 
			tasks.diskSupervision.timerElapsed(null);
		else if (resource.equalsLocation(config.triggerMemoryCheck()))
			tasks.ramSupervision.timerElapsed(null);
		else if (resource.equalsLocation(config.triggerResourceCheck()))
			tasks.resourceSupervision.timerElapsed(null);
	}
	
}
