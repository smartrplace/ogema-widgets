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


package de.iwes.widgets.start;

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
        wam.registerWebResource("/ogema/widget/images", "/org/ogema/img");
        wam.registerWebResource("/org/ogema/localisation/service", "/org/ogema/localisation/service");
        wam.registerWebResource("/ogema/widget/apps", new AppsServlet(am, permMan));
        wam.registerStartUrl(null);
        widgetService = new OgemaOsgiWidgetServiceImpl();
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
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
		    			sreg.unregister();
		    		} catch (Exception ignore) {}
				}
			}).start();
    	}
    	if (widgetService != null) {
	    	try {
		    	widgetService.deactivate();
	    	} catch (Exception e) {
	    		if (am != null)
	    			am.getLogger().error("Could not shut down session management");
	    	}
    	}
    	am = null;
    	if (wam != null) {
	    	wam.unregisterWebResource("/ogema/widget/servlet");
	    	wam.unregisterWebResource("/ogema/jslib");
	    	wam.unregisterWebResource("/ogema/widget/bricks");
	    	wam.unregisterWebResource("/ogema/widget/images");
	    	wam.unregisterWebResource("/org/ogema/localisation/service");
	    	wam.unregisterWebResourcePath("/ogema/widget/apps");
    	}
    }
    
}
