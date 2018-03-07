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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;

import de.iwes.tools.system.supervision.model.SupervisionUtils;
import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;

public class Tasks {

	private final ApplicationManager appMan;
	private final SystemSupervisionConfig config;
	private final Timer diskTimer;
	private final Timer ramTimer;
	private final Timer resourceTimer;
	private final Timer startTimer;
	private final Semaphore diskCheckSemaphore = new Semaphore(1);
	private final Semaphore ramCheckSemaphore = new Semaphore(1);
	private final Semaphore resourceCheckSemaphore = new Semaphore(1);
	
	public Tasks(ApplicationManager appMan, SystemSupervisionConfig config) {
		this.appMan = appMan;
		this.config = config;
		this.diskTimer = appMan.createTimer(SupervisionUtils.getInterval(config.diskCheckInterval(), SupervisionUtils.DEFAULT_DISK_SUPERVISION_ITV), diskSupervision);
		this.ramTimer = appMan.createTimer(SupervisionUtils.getInterval(config.memoryCheckInterval(), SupervisionUtils.DEFAULT_RAM_SUPERVISION_ITV), ramSupervision);
		this.resourceTimer = appMan.createTimer(SupervisionUtils.getInterval(config.resourceCheckInterval(), SupervisionUtils.DEFAULT_RESOURCE_SUPERVISION_ITV), resourceSupervision);
		config.lastStart().<TimeResource> create().setValue(appMan.getFrameworkTime());
		config.lastStart().activate(false);
		// run all timers once at the beginning
		this.startTimer = appMan.createTimer(60*1000, new TimerListener() {
			
			@Override
			public void timerElapsed(Timer timer) {
				timer.destroy();
				diskSupervision.timerElapsed(diskTimer);
				ramSupervision.timerElapsed(ramTimer);
				resourceSupervision.timerElapsed(resourceTimer);
			}
		});
	}
	
	void stop() {
		diskTimer.destroy();
		ramTimer.destroy();
		resourceTimer.stop();
		if (startTimer.isRunning())
			startTimer.destroy();
	}
	
	static final int mb = 1024*1024;
	
	final TimerListener ramSupervision = new TimerListener() {
		
		@Override
		public void timerElapsed(Timer timer /* may be null */) {
			if (!ramCheckSemaphore.tryAcquire())
				return;
			try {
				final Runtime runtime = Runtime.getRuntime();
				runtime.gc();
				final long used = ( runtime.totalMemory() - runtime.freeMemory());
				final long memMax = runtime.maxMemory();
				config.results().usedMemorySize().<TimeResource> create().setValue(used);
				config.results().maxAvailableMemorySize().<TimeResource> create().setValue(memMax); // should not really change
				config.results().usedMemorySize().activate(false);
				config.results().maxAvailableMemorySize().activate(false);
				appMan.getLogger().info("RAM used: {} MB, max. RAM available: {}", (used/mb), (memMax/mb));
			} finally {
				ramCheckSemaphore.release();
			}
		}
	};
	
	final TimerListener diskSupervision = new TimerListener() {
		
		@Override
		public void timerElapsed(Timer timer /* may be null */) {
			if (!diskCheckSemaphore.tryAcquire())
				return;
			try {
				final long dataSize = size(Paths.get("./data"));
				final long rundirSize = size(Paths.get("."));
				long free = Long.MIN_VALUE;
				try {
					free = Files.getFileStore(Paths.get("/")).getUsableSpace();
				} catch (IOException e) {
					appMan.getLogger().warn("Error determining free disk space",e);
				}
				config.results().rundirFolderSize().<TimeResource> create().setValue(rundirSize);
				config.results().dataFolderSize().<TimeResource> create().setValue(dataSize);
				if (free != Long.MIN_VALUE)
					config.results().freeDiskSpace().<TimeResource> create().setValue(free);
				config.results().rundirFolderSize().activate(false);
				config.results().dataFolderSize().activate(false);
				config.results().freeDiskSpace().activate(false);
				appMan.getLogger().info("Rundir folder size: {} MB, data folder: {} MB, free disk space: {} MB", 
						(rundirSize/mb), (dataSize/mb), (free != Long.MIN_VALUE ? (free/mb) : "n.a."));
			} finally {
				diskCheckSemaphore.release();
			}
		}
	};
	
	final TimerListener resourceSupervision = new TimerListener() {
		
		@Override
		public void timerElapsed(Timer timer /* may be null */) {
			if (!resourceCheckSemaphore.tryAcquire())
				return;
			try {
				final int size = appMan.getResourceAccess().getResources(Resource.class).size();
				config.results().nrResources().<IntegerResource> create().setValue(size);
				config.results().nrResources().activate(false);
				appMan.getLogger().info("Number of resources: {}", size);
			} finally {
				resourceCheckSemaphore.release();
			}
		}
	};
	
	/*
	 * http://stackoverflow.com/questions/2149785/get-size-of-folder-or-file/19877372#19877372
	 */
	private static long size(Path path) {

	    final AtomicLong size = new AtomicLong(0);

	    try {
	        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	            @Override
	            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
	                size.addAndGet(attrs.size());
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult visitFileFailed(Path file, IOException exc) {

	                System.out.println("skipped: " + file + " (" + exc + ")");
	                // Skip folders that can't be traversed
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

	                if (exc != null)
	                    System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
	                // Ignore errors traversing a folder
	                return FileVisitResult.CONTINUE;
	            }
	        });
	    } catch (IOException e) {
	        throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
	    }

	    return size.get();
	}
	
}
