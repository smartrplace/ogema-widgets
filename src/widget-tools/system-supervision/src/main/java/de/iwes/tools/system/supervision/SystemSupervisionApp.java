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

import java.util.HashSet;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.ResourceDemandListener;

import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

@Component(specVersion = "1.2")
@Service(Application.class)
public class SystemSupervisionApp implements Application, ResourceDemandListener<SystemSupervisionConfig> {

	private ApplicationManager appMan;
	private final Set<SystemSupervisionConfig> configs = new HashSet<>();
	private SystemSupervisionConfig activeConfig;
	private Tasks tasks;
	private TriggerListener triggerListener;
	private ShellCommands shellCommands;
	private ShellCommandsInitial shellInitial;
	
	@Override
	public void start(final ApplicationManager appManager) {
		this.appMan = appManager;
		appManager.getResourceAccess().addResourceDemand(SystemSupervisionConfig.class, this);
		this.shellInitial = new ShellCommandsInitial(appManager, appManager.getAppID().getBundle().getBundleContext());
	}

	@Override
	public void stop(AppStopReason reason) {
		final ShellCommandsInitial initial = this.shellInitial;
		this.shellInitial = null;
		if (initial != null)
			initial.close();
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
			this.shellCommands = new ShellCommands(tasks, config, appMan.getAppID().getBundle().getBundleContext());
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
