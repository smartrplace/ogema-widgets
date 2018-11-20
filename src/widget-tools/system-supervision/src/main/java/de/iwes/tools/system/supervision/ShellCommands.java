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

import java.util.Hashtable;
import java.util.concurrent.ForkJoinPool;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

@Descriptor("OGEMA system supervision commands")
class ShellCommands {

	private final ServiceRegistration<ShellCommands> ownReg;
	private final Tasks tasks;
	private final SystemSupervisionConfig config;
	
	ShellCommands(Tasks tasks, SystemSupervisionConfig config, BundleContext ctx) {
		this.tasks = tasks;
		this.config = config;
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("osgi.command.scope", "ogmsys");
		props.put("osgi.command.function", new String[] { "countResources", "diskUsage", "memoryUsage" });
		this.ownReg = ctx.registerService(ShellCommands.class, this, props);
	}
	
	public void close() {
		ForkJoinPool.commonPool().submit(() ->  { try { ownReg.unregister(); } catch (Exception ignore) {} });
	}
	
	@Descriptor("Count OGEMA resources in the system")
	public void countResources() {
		// actually this already logs the status, but we cannot easily determine whether the console logger is active
		tasks.resourceSupervision.timerElapsed(null);
		System.out.println("Number of resources: " + config.results().nrResources().getValue());
	}
	
	@Descriptor("Print disk usage information")
	public void diskUsage() {
		// actually this already logs the status, but we cannot easily determine whether the console logger is active
		tasks.diskSupervision.timerElapsed(null);
		System.out.println("Size of rundir folder: " + (config.results().rundirFolderSize().getValue()/Tasks.mb) + " MB.");
		System.out.println("Size of data folder:   " + (config.results().dataFolderSize().getValue()/Tasks.mb) + " MB.");
		System.out.println("Free disk space:       " + (config.results().freeDiskSpace().getValue()/Tasks.mb) + " MB.");
	}
	
	@Descriptor("Print memory usage information")
	public void memoryUsage() {
		// actually this already logs the status, but we cannot easily determine whether the console logger is active
		tasks.ramSupervision.timerElapsed(null);
		System.out.println("RAM used: " + (config.results().usedMemorySize().getValue()/Tasks.mb) + " MB, max RAM available: " 
				+ (config.results().maxAvailableMemorySize().getValue()/Tasks.mb) + " MB.");
	}
	
}
