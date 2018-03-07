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

import java.util.HashSet;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.ResourceDemandListener;

import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

@Component(immediate = true, specVersion = "1.2")
@Service(Application.class)
public class SystemSupervisionApp implements Application, ResourceDemandListener<SystemSupervisionConfig> {

	private ApplicationManager appMan;
	private final Set<SystemSupervisionConfig> configs = new HashSet<>();
	private SystemSupervisionConfig activeConfig;
	private Tasks tasks;
	private TriggerListener triggerListener;
	private ShellCommands shellCommands;
	
	@Override
	public void start(ApplicationManager appManager) {
		this.appMan  =appManager;
		appManager.getResourceAccess().addResourceDemand(SystemSupervisionConfig.class, this);
	}

	@Override
	public void stop(AppStopReason reason) {
		stopInternal();
		if (appMan != null)
			appMan.getResourceAccess().removeResourceDemand(SystemSupervisionConfig.class, this);
		appMan = null;
	}
	
	private void startInternal() {
		final SystemSupervisionConfig config = activeConfig;
		if (config == null || !config.isActive()) {
			appMan.getLogger().error("Inactive configuration resource {}", config);
			return;
		}
		this.tasks = new Tasks(appMan, config);
		this.triggerListener = new TriggerListener(appMan, config, tasks);
		try {
			this.shellCommands = new ShellCommands(tasks, config);
		} catch (NoClassDefFoundError e) {} // optional dependency
	}
	
	private void stopInternal() {
		if (tasks != null) {
			tasks.stop();
			tasks = null;
		}
		if (triggerListener != null) {
			triggerListener.close();
			triggerListener = null;
		}
		if (shellCommands != null) {
			shellCommands.close();
			shellCommands = null;
		}
	}
	

	@Override
	public void resourceAvailable(SystemSupervisionConfig resource) {
		if (!this.configs.isEmpty()) 
			stopInternal();
		configs.add(resource);
		activeConfig = resource;
		startInternal();
	}

	@Override
	public void resourceUnavailable(SystemSupervisionConfig resource) {
		configs.remove(resource);
		if (activeConfig != null && activeConfig.equalsLocation(resource)) {
			stopInternal();
		}
		if (configs.isEmpty()) 
			activeConfig = null;
		else {
			activeConfig = configs.iterator().next();
			startInternal();
		}
	}

	
	
}
