/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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
