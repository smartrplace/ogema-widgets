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

package de.iwes.widgets.start;

import java.util.concurrent.ForkJoinPool;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.accesscontrol.PermissionManager;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.security.WebAccessManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

import de.iwes.widgets.api.extended.WidgetAdminService;
import de.iwes.widgets.api.extended.impl.AppsServlet;
import de.iwes.widgets.api.extended.impl.OgemaOsgiWidgetServiceImpl;

/**
 *
 * @author esternberg
 */
@Component(specVersion = "1.2")
@Service(Application.class)
public class JsBundleApp implements Application {

    @Reference
    private HttpService httpService;
    private ApplicationManager am;
    private WebAccessManager wam;
    private volatile BundleContext ctx;
    
    private OgemaOsgiWidgetServiceImpl widgetService;
    private ServiceRegistration<WidgetAdminService> sreg;
    
    @Reference
    private PermissionManager permMan;
    
    @Activate
    protected void activate(BundleContext ctx) {
    	this.ctx = ctx;
    }
    
    @Override
    public void start(ApplicationManager am) {
    	this.am = am;
    	this.wam = am.getWebAccessManager();
        wam.registerWebResource("/ogema/jslib", "/org/ogema/tools");
        wam.registerWebResource("/ogema/widget/bricks", "/org/ogema/widget/html/bricks");
        wam.registerWebResource("/ogema/widget/fragment", "/org/ogema/widget/html/fragment");
        wam.registerWebResource("/ogema/widget/images", "/org/ogema/img");
        wam.registerWebResource("/org/ogema/localisation/service", "/org/ogema/localisation/service");
        wam.registerWebResource("/ogema/widget/apps", new AppsServlet(am, permMan));
        wam.registerStartUrl(null);
        widgetService = new OgemaOsgiWidgetServiceImpl(permMan.getAccessManager(), am);
        wam.registerWebResource("/ogema/widget/servlet" , widgetService);
        this.sreg = ctx.registerService(WidgetAdminService.class, widgetService, null);
    }

    @Override
    public void stop(AppStopReason asr) {
    	final OgemaOsgiWidgetServiceImpl widgetService = this.widgetService;
    	final WebAccessManager wam = this.wam;
    	final ServiceRegistration<WidgetAdminService> sreg = this.sreg;
    	this.wam = null;
    	this.widgetService = null;
    	this.sreg = null;
    	if (sreg != null) {
    		// unregister in stop call may block
    		ForkJoinPool.commonPool().submit(() -> {
    			try {
	    			sreg.unregister();
	    		} catch (Exception ignore) {}
    		});
    	}
    	if (widgetService != null) {
	    	try {
		    	widgetService.deactivate();
	    	} catch (Exception e) {
	    		if (am != null)
	    			am.getLogger().error("Could not shut down session management: ",e);
	    	}
    	}
    	am = null;
    	if (wam != null) {
	    	wam.unregisterWebResource("/ogema/widget/servlet");
	    	wam.unregisterWebResource("/ogema/jslib");
	    	wam.unregisterWebResource("/ogema/widget/bricks");
	    	wam.unregisterWebResource("/ogema/widget/fragment");
	    	wam.unregisterWebResource("/ogema/widget/images");
	    	wam.unregisterWebResource("/org/ogema/localisation/service");
	    	wam.unregisterWebResource("/ogema/widget/apps");
    	}
    }
    
}
