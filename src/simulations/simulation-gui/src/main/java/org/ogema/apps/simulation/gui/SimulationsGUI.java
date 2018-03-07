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

/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur F�rderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.apps.simulation.gui;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.apps.simulation.gui.configuration.ConfigModal;
import org.ogema.apps.simulation.gui.configuration.CreateModal;
import org.ogema.apps.simulation.gui.configuration.PlotsModal;
import org.ogema.apps.simulation.gui.configuration.SimQuModal;
import org.ogema.apps.simulation.gui.plots.GrafanaServletUpdater;
import org.ogema.apps.simulation.gui.speed.SimulationFactorListener;
import org.ogema.apps.simulation.gui.speed.SimulationFactorPattern;
import org.ogema.apps.simulation.gui.templates.SimulatedDeviceRowTemplate;
import org.ogema.apps.simulation.gui.templates.SimulationAccordionItem;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.tools.grafana.base.InfluxFake;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.DynamicTableData;

/**
 * Generic simulations GUI 
 * @author cnoelle
 */
@Component(specVersion = "1.2")
@Service(Application.class)
public class SimulationsGUI implements Application {

	private final static String WEB_RESOURCE_PREFIX = "/de/iwes/simulations";
	private final static String WEB_RESOURCE_SERVLET_PREFIX = WEB_RESOURCE_PREFIX + "/simgui/servlet";
	
	private OgemaLogger logger;
	private ApplicationManager am;
	private DynamicTable<String> table;
	private List<SimulationProvider<?>> providers;
	private SimulationListener simListener;
    private String webResourceBrowserPath;
//    private String servletPath;
    private Map<String,SimulationAccordionItem> accItems;
    private Map<String,Map> panels;
    private long updateInterval = 5000;  // influx update interval 5s
    private long panelsUpdateInterval = 15000;
    private InfluxFake infl;
    private Timer grafanaServletTimer;
    private String influxServlet;
    private Map<String,Map> individualPanels;
    private InfluxFake indInfl;
    private String indvidualInfluxServlet;
    private Map<String,Map> singlePanels;
    private InfluxFake singleInflux;
    private String singleInfluxServlet;
    private SimulationFactorListener simFactorListener;
    private WidgetAppImpl myApp;
    // XXX ugly 
    static volatile Utils utils;
    
    @Reference
    SimulationServiceAdmin simulationServiceAdmin;    

    @Reference
    OgemaGuiService widgetService;

    @Override
    public void start(final ApplicationManager am) {
        this.am = am;
        utils = new Utils(am);
        this.logger = am.getLogger();
        this.providers = new LinkedList<SimulationProvider<?>>();
        // register web resources
        logger.debug("Simulations GUI started");
        String webResourcePackagePath = "org/ogema/apps/simulation/gui";
        String appNameLowerCase = "SimulationsGui".toLowerCase();
        webResourceBrowserPath = WEB_RESOURCE_PREFIX + "/" + appNameLowerCase;
//        servletPath = "/apps/ogema/" + appNameLowerCase;
        am.getWebAccessManager().registerWebResource(webResourceBrowserPath, webResourcePackagePath);
        
//        SessionDataManagement<SimulationAppSessionData> sessionMgt = new SessionDataManagement<SimulationAppSessionData>(SimulationAppSessionData.class);
//        WidgetApp myApp = new WidgetApp(appNameLowerCase, webResourcePackagePath, widgetService, am.getWebAccessManager(), logger, sessionMgt);
//        WidgetApp myApp = new WidgetApp(appNameLowerCase, webResourceBrowserPath, widgetService, am, sessionMgt);
        myApp = new WidgetAppImpl(webResourceBrowserPath, widgetService, am);
        WidgetPageBase<?> page = new WidgetPageBase(myApp,"index3.html");
        // create table widget, listeners for thermostats and humidity sensors, and set table values
        table = new DynamicTable<String>(page, "simulationProvidersTable",true);
        table.setRowTemplate(new SimulatedDeviceRowTemplate(page, am, providers));
//        table.setTableClasses("table-striped");
        table.addStyle(DynamicTableData.TABLE_STRIPED,null);
 //       table.setTableClasses("table tbody tr:first-child { font-weight: bold; }"); / see CSS file
              
        this.singlePanels = new LinkedHashMap<String,Map>(); 
        this.singleInflux = new InfluxFake(am,singlePanels,updateInterval);
        this.singleInflux.setStrictMode(true);
        singleInfluxServlet = am.getWebAccessManager().registerWebResource(WEB_RESOURCE_SERVLET_PREFIX +"/single/fake_influxdb/series",singleInflux); 
//        singleInfluxServlet = am.getWebAccessManager().registerWebResource("/org/ogema/apps/simulation-gui/single/fake_influxdb/series",singleInflux); 
      
        // create proper -> index.html
        accItems = new LinkedHashMap<>();
        DynamicSimulationGUI dynGUI = new DynamicSimulationGUI(myApp, "index.html",am,accItems);
        Alert alert = new Alert(dynGUI, "genericAlert", "");
        alert.setDefaultVisibility(false);
        CreateModal createModal = new CreateModal(dynGUI,"createModal",alert, am.getResourceAccess());
        ConfigModal configModal  = new ConfigModal(dynGUI, "myModal",alert);
        SimQuModal simQuModal = new SimQuModal(dynGUI, "simQuModal", alert);
        PlotsModal plotsModal = new PlotsModal(dynGUI,"plotsModal",singleInflux,alert);
        this.simListener = new SimulationListener(providers,dynGUI,table,dynGUI.getAccordion(),accItems,configModal,createModal,simQuModal,plotsModal,alert, simulationServiceAdmin);
        
        this.panels = new LinkedHashMap<String,Map>(); 
        this.infl = new InfluxFake(am,panels,updateInterval);
        this.infl.setStrictMode(true);
        //servletPath = "/apps/ogema/" + appNameLowerCase + "/fake_influxdb/series";
        influxServlet = am.getWebAccessManager().registerWebResource(WEB_RESOURCE_SERVLET_PREFIX + "/fake_influxdb/series",infl);
        this.individualPanels = new LinkedHashMap<String,Map>(); 
        this.indInfl = new InfluxFake(am,individualPanels,updateInterval);
        this.indInfl.setStrictMode(true);
        indvidualInfluxServlet = am.getWebAccessManager().registerWebResource(WEB_RESOURCE_SERVLET_PREFIX+ "/individual/fake_influxdb/series",indInfl);
//        indvidualInfluxServlet = am.getWebAccessManager().registerWebResource("/org/ogema/apps/simulation-gui/individual/fake_influxdb/series",indInfl);  
//        System.out.println("   registered: " + indvidualInfluxServlet);
        
        
        grafanaServletTimer = am.createTimer(panelsUpdateInterval, new GrafanaServletUpdater(providers, panels,individualPanels,am));
		infl.setPanels(panels);
		indInfl.setPanels(individualPanels);
		
		simFactorListener = new SimulationFactorListener(am.getAdministrationManager(),logger);
		am.getResourcePatternAccess().addPatternDemand(SimulationFactorPattern.class, simFactorListener,AccessPriority.PRIO_LOWEST);
    }

    @Override
    public void stop(AppStopReason reason) {
    	if (myApp != null)
    		myApp.close();
        if (am != null) {
        	try {
		    	am.getWebAccessManager().unregisterWebResource(webResourceBrowserPath);
		        am.getWebAccessManager().unregisterWebResource(singleInfluxServlet);
		        am.getWebAccessManager().unregisterWebResource(indvidualInfluxServlet);
		        am.getWebAccessManager().unregisterWebResource(influxServlet);
		        am.getResourcePatternAccess().removePatternDemand(SimulationFactorPattern.class, simFactorListener);
        	} catch (Exception e) { /* ignore */ }
        }
        if (simListener != null)
        	simListener.close();
        simListener = null;
        if (grafanaServletTimer != null)
        	grafanaServletTimer.destroy();
        grafanaServletTimer = null;
        try {
        	infl.destroy();
        	infl = null;
        } catch (Exception e) {}
        try {
        	indInfl.destroy();
        	indInfl = null;
        } catch (Exception e) {}
        try {
        	singleInflux.destroy();
        	singleInflux = null;
        } catch (Exception e) {}

        if (simFactorListener != null)
        	simFactorListener.destroy();
        simFactorListener = null;
        am = null;
        myApp = null;
        panels = null;
        singlePanels = null;
        individualPanels = null;
        accItems = null;
        logger = null;
        table =  null;
        providers = null;
        utils = null;
    }

}
