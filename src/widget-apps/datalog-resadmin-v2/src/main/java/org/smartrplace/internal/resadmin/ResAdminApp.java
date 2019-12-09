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
package org.smartrplace.internal.resadmin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.LoggerFactory;
import org.smartrplace.internal.resadmin.gui.MainPageImpl;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

/**
 * Backup management for OGEMA resources
 */
//@Component(specVersion = "1.2", immediate = true)
//@Service(Application.class) // registers service programmatically
public class ResAdminApp implements BundleActivator, Application {
	
	public static final String urlPath = "/org/smartrplace/external/datalogresadminv2";
	private final static String cleanStateMarker = "clean";
	
    private OgemaLogger log;
    private ApplicationManager appMan;
    public ResAdminController controller;
    private ShellCommands shellCommands;
    private volatile BundleContext ctx;
    private volatile CountDownLatch startLatch;
    private volatile ServiceRegistration<Application> sreg;
    private volatile ServiceTracker<OgemaGuiService, OgemaGuiService> tracker;
    private volatile Thread initThread;

	private WidgetApp widgetApp;

//	@Reference
	private volatile OgemaGuiService guiService;
	
	public void start(final BundleContext ctx) {
		this.ctx = ctx;
		final CountDownLatch guiServiceLatch = new CountDownLatch(1);
		this.startLatch = new CountDownLatch(1);
		final boolean firstStart = isClean(ctx);
		this.tracker = new ServiceTracker<>(ctx, OgemaGuiService.class, new ServiceTrackerCustomizer<OgemaGuiService, OgemaGuiService>() {

			@Override
			public OgemaGuiService addingService(ServiceReference<OgemaGuiService> reference) {
				final OgemaGuiService service = ctx.getService(reference);
				if (service != null) {
					guiService = service;
					guiServiceLatch.countDown();
					startAppInNewThread(); // we must not block any OSGi threads
				}
				return service;
			}

			@Override
			public void modifiedService(ServiceReference<OgemaGuiService> reference, OgemaGuiService service) {
				stop(ctx);
				start(ctx);
			}

			@Override
			public void removedService(ServiceReference<OgemaGuiService> reference, OgemaGuiService service) {
				stop(ctx);
			}
		});
		tracker.open();
		final ServiceReference<OgemaGuiService> guiRef = ctx.getServiceReference(OgemaGuiService.class);
		if (guiRef != null) {
			final OgemaGuiService guiService = ctx.getService(guiRef);
			if (guiService != null)
				this.guiService = guiService;
		}
		if (!firstStart) {
			if (this.guiService != null) // otherwise we'll get a callback anyway
				startAppInNewThread();
			return;
		}
		setUncleanMarker(ctx);
		if (this.guiService == null) {
			try {
				if (!guiServiceLatch.await(2, TimeUnit.SECONDS)) {
					// the framework will continue to start normally in this case, also this app will start eventually, 
					// when the OgemaGuiService becomes available; the only downside is that this app does not block the startup
					// of other apps. This happens if the start level of this app is not higher than the one of the OgemaGuiService.
					LoggerFactory.getLogger(ResAdminApp.class).warn("Wait timed out for OgemaGuiService; releasing framework level lock...");
					return;
				} 
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		registerApp(ctx);
	}
	
	private void startAppInNewThread() {
		this.initThread = new Thread(new Runnable() { 
			
			@Override
			public void run() {
				registerApp(ctx);
				
			}
		}, "ResAdminApp-App-wait");
		initThread.start();
	}
	
	private static boolean isClean(final BundleContext ctx) {
		try {
			return !Files.exists(ctx.getDataFile(cleanStateMarker).toPath());
		} catch (Exception e) {
			return false;
		}
	}
	
	private static void setUncleanMarker(final BundleContext ctx) {
		try {
			final Path target = ctx.getDataFile(cleanStateMarker).toPath();
			if (!Files.exists(target))
				Files.createFile(target);
		} catch (Exception e) {
		}
	}
	
	private synchronized void registerApp(final BundleContext ctx) {
		if (this.sreg != null)
			return;
		try {
			this.sreg = ctx.registerService(Application.class, this, null);
			// block the startup process until all resources from the replay directory have been parsed
			final CountDownLatch startLatch = this.startLatch;
			if (startLatch != null && !startLatch.await(5, TimeUnit.MINUTES))
				 LoggerFactory.getLogger(ResAdminApp.class).error("ResAdmin app failed to start within 5 minutes");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} 
	}
	
	public void stop(BundleContext ctx) {
		this.ctx = null;
		this.startLatch = null;
		final ServiceRegistration<Application> sreg = this.sreg;
		final ServiceTracker<OgemaGuiService, OgemaGuiService> tracker = this.tracker;
		this.sreg = null;
		this.tracker = null;
		this.guiService = null;
		final Thread initThread = this.initThread;
		this.initThread = null;
		if (sreg != null || tracker != null) {
			ForkJoinPool.commonPool().submit(() -> {
				if (sreg != null) {
					try {
						sreg.unregister();
					} catch (Exception ignore) {}
				}
				if (tracker != null) {
					try {
						tracker.close();
					} catch (Exception ignore) {}
				}
			});
		}
		if (initThread != null && initThread.isAlive()) {
			try {
				initThread.interrupt();
			} catch (SecurityException e) {}
		}
	}

    /*
     * This is the entry point to the application.
     */
 	@Override
    public void start(ApplicationManager appManager) {
 		try {
	        // Remember framework references for later.
	        appMan = appManager;
	        log = appManager.getLogger();
	
	        // 
	        controller = new ResAdminController(appMan);
			
			//register a web page with dynamically generated HTML
			widgetApp = guiService.createWidgetApp(urlPath, appManager);
			WidgetPage<?> page = widgetApp.createStartPage();
			//mainPage = new MainPage(page, this);
			//WidgetApp wReal = new WidgetApp(urlPath, urlPath, widgetService, appManager);
			new MainPageImpl(page, controller);
			try {
				this.shellCommands = new ShellCommands(controller, ctx);
			} catch (NoClassDefFoundError expected) {} // optional dependency
 		} finally {
 			final CountDownLatch latch = this.startLatch;
 			if (latch != null)
 				latch.countDown();
 		}
     }

     /*
     * Callback called when the application is going to be stopped.
     */
    @Override
    public void stop(AppStopReason reason) {
    	if (widgetApp != null) 
    		widgetApp.close();
		if (controller != null)
    		controller.close();
		if (log != null)
			log.info("{} stopped", getClass().getName());
        if (shellCommands != null)
        	shellCommands.close();
        shellCommands = null;
        controller = null;
        appMan = null;
        log = null;
    }
}
