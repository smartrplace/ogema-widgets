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
 * Copyright 2017
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.smartrplace.internal.resadmin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.action.Action;
import org.ogema.model.sensors.TemperatureSensor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.smartrplace.resadmin.config.BackupConfig;

@Descriptor("OGEMA backup commands")
public class ShellCommands {

	private final ServiceRegistration<ShellCommands> ownReg;
	private final ResAdminController controller;
	
	public ShellCommands(ResAdminController controller, BundleContext ctx) {
		this.controller = controller; 
		
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("osgi.command.scope", "ogm");
		props.put("osgi.command.function", new String[] { "replayBackup", "runBackupConfig", "listBackupConfigs", "createCustomBackup" });
		this.ownReg = ctx.registerService(ShellCommands.class, this, props);

	}
	
	public void close() {
		try {
			ownReg.unregister();
		} catch (Exception ignore) {}
	}
	
	@Descriptor("List available backup configurations and their properties")
	public void listBackupConfigs() {
		System.out.println("Backup configurations:");
		for (BackupConfig config : controller.appConfigData.configList().getAllElements()) {
			printConfigData(config);
		}
	}
	 
	private static void printConfigData(BackupConfig config) {
		final String dir = (config.destinationDirectory().isActive() ? config.destinationDirectory().getValue() : "n/a");
		final boolean overwrite = config.overwriteExistingBackup().getValue();
		final long updateRate = (config.autoBackupInterval().isActive() ? config.autoBackupInterval().getValue() : 0);
		final String ur = (updateRate > 0 ? (updateRate/60000) + " min" : "n/a");
		System.out.format(" Configuration: %s:\n    directory: %s,\n    format: %s,\n    update rate: %s\n", 
				config.name().getValue(), dir, (overwrite ? "single files" : "zip files"), ur);
	}

	@Descriptor("Store a backup of the specified resources")
	public void createCustomBackup(
			@Parameter(names={"-r", "--references"}, absentValue="false", presentValue="true")
			@Descriptor("Resolve references? (default: false)")
			String followReferences,
			@Parameter(names={"-s", "--schedules"}, absentValue="false", presentValue="true")
			@Descriptor("Serialize schedules? (default: false)")
			String serializeSchedules,
			@Parameter(names={"-d", "--depth"}, absentValue="20")
			@Descriptor("Depth for inclusion of subresources. (default: 20)")
			String depth,
			@Parameter(names={"-e", "--ending"}, absentValue=".ogx")
			@Descriptor("Specify the file ending (typically .xml, .ogx, .json or .ogj). (default: .ogx)")
			String fileEnding,
			@Parameter(names={"-t", "--type"}, absentValue="")
			@Descriptor("Specify a resource type (full class name), alternatively to the paths; "
					+ "this parameter is ignored if the paths are given explicitly. This may not work for custom types.")
			String type,
			@Descriptor("The target directory. Will be created if non-existent. If a file to be created already exists it will be overwritten.")
			String directory,
			@Descriptor("Resource paths to be included")
			String... resourcePaths
			) throws IOException {
		Path target = Paths.get(directory);
		if (Files.exists(target) && !Files.isDirectory(target)) {
			System.out.println("File " + target + " already exists, but is not a directory");
			return;
		}
		final int dpth = Integer.parseInt(depth);
		if (dpth < 0) {
			System.out.println("Negative values for depth not allowed");
			return;
		}
		final boolean schedules = Boolean.parseBoolean(serializeSchedules);
		final boolean references = Boolean.parseBoolean(followReferences);
		Files.createDirectories(target);
		final ResourceAccess ra = controller.appMan.getResourceAccess();
		int cnt = 0;
		if (resourcePaths.length == 0) {
			if (!type.trim().isEmpty()) {
				final Class<? extends Resource> resourceType = loadType(type);
				if (resourceType != null) {
					for (Resource r : controller.appMan.getResourceAccess().getResources(resourceType)) {
						try {
							final Path file = target.resolve(URLEncoder.encode("A" + String.format("%03d",cnt) + "_" + r.getPath(), "UTF-8") + fileEnding);
							writeFile(file, r, dpth, references, schedules);
							cnt++;
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
					System.out.println(cnt + " files successfully written to backup: " + target);
					return;
				}
			}
			System.out.println("No resources specified");
			return;
		}
		for (String path : resourcePaths) {
			try {
				final Resource r = ra.getResource(path);
				if (r == null || !r.exists()) {
					System.out.println("Resource " + path + " not found, skipping it.");
					continue;
				}
				final Path file = target.resolve(URLEncoder.encode("A" + String.format("%03d",cnt) + "_" + path, "UTF-8") + fileEnding);
				writeFile(file, r, dpth, references, schedules);
				cnt++;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		System.out.println(cnt + " files successfully written to backup: " + target);
	}
	
	private static Class<? extends Resource> loadType(String type) {
		Class<? extends Resource> result = null;
		result = loadType(type, TemperatureSensor.class.getClassLoader());
		if (result != null)
			return result;
		result = loadType(type, Action.class.getClassLoader()); // relies on the Action model to be defined in another bundle from the standard models
		if (result != null)
			return result;
		result = loadType(type, Resource.class.getClassLoader());
		if (result != null)
			return result;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Class<? extends Resource> loadType(String type, ClassLoader classloader) {
		try {
			return (Class<? extends Resource>) classloader.loadClass(type);
		} catch (ClassNotFoundException | ClassCastException e) {
			return null;
		}
	}

	private void writeFile(Path target, Resource res, int depth, boolean references, boolean schedules) throws IOException {
		try (final PrintWriter out = new PrintWriter(target.toFile())) {
			if (ResAdminController.isJsonFile(target))
				controller.appMan.getSerializationManager(depth, references, schedules).writeJson(out, res);
			else
				controller.appMan.getSerializationManager(depth, references, schedules).writeXml(out, res);
		}
	}
	
	@Descriptor("Run backup")
	public void runBackupConfig(
			@Descriptor("Optional directory to store the backup in; if not specified, the standard directory for the configuration is used.")
			@Parameter(names={"-d","--directory"}, absentValue="")
			String directory,
			@Descriptor("Backup configuration to run")
			String configuration
			) {
		BackupConfig cfg = null;
		configuration = configuration.toLowerCase();
		for (BackupConfig config : controller.appConfigData.configList().getAllElements()) {
			final String name = config.name().getValue().toLowerCase();
			if (configuration.equals(name)) {
				cfg = config;
				break;
			}
		}
		if (cfg == null) {
			System.out.println("Backup configuration " + configuration + " not found");
			return;
		}
		final File result;
		if (directory.trim().isEmpty())
			result = controller.runBackup(cfg);
		else
			result = controller.runBackup(cfg, directory);
		if (result != null) 
			System.out.println("Backup available in " + result);
		else
			System.out.println("Something went wrong...");
	}
	
	@Descriptor("Replay backup from a directory, or a single file")
	public void replayBackup(
			@Descriptor(value="Path to import directory/file, relative to rundir")
			final String path) throws IOException {
		final Path p = Paths.get(path);
		if (!Files.exists(p)) {
			System.out.println("Path " + path + " does not exist");
			return;
		}
		System.out.println(controller.replayBackup(path, null));
	}
}
