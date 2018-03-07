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

import java.util.Hashtable;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

@Descriptor("OGEMA system supervision commands")
public class ShellCommands {

	private final ServiceRegistration<ShellCommands> ownReg;
	private final Tasks tasks;
	private final SystemSupervisionConfig config;
	
	public ShellCommands(Tasks tasks, SystemSupervisionConfig config) {
		this.tasks = tasks;
		this.config = config;
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("osgi.command.scope", "ogmsys");
		props.put("osgi.command.function", new String[] { "countResources", "diskUsage", "memoryUsage" });
		this.ownReg = FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(ShellCommands.class, this, props);
	}
	
	public void close() {
		try {
			ownReg.unregister();
		} catch (Exception ignore) {}
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
