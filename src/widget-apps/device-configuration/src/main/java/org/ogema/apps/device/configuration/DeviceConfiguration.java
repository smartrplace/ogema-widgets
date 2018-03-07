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

package org.ogema.apps.device.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.apps.device.configuration.localisation.*;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.whitegoods.CoolingDevice;
import org.ogema.model.devices.whitegoods.WashingMachine;
import org.ogema.model.prototypes.PhysicalElement;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.pattern.widget.patternedit.PatternCreator;
import de.iwes.widgets.pattern.widget.patternedit.PatternCreatorConfiguration;
import de.iwes.widgets.pattern.widget.patternedit.PatternEditor;

@Component(specVersion = "1.2")
@Service(Application.class)
public class DeviceConfiguration implements Application {

	private OgemaLogger logger;
    private WidgetApp widgetApp;

    @Reference
    private OgemaGuiService widgetService;
    
	@Override
    public void start(final ApplicationManager am) {
        this.logger = am.getLogger();

        logger.debug("{} started", getClass().getName());
        widgetApp = widgetService.createWidgetApp("/de/iwes/apps/device-configuration", am);
     
        final WidgetPage<DevicePageDictionary> page = widgetApp.createWidgetPage("devices.html", true);
        PatternEditor<PhysicalElementPattern, DevicePageDictionary> patternEditor 
        	= PatternEditor.getInstance(page, "devices", PhysicalElementPattern.class, am, DevicePageDictionary.class, false);
        
        Header header = new Header(page, "header", "Show devices") {

			private static final long serialVersionUID = 1L;

			@Override
        	public void onGET(OgemaHttpRequest req) {
        		setText(page.getDictionary(req).pageTitle(), req);
        	}
        	
        };
        header.addDefaultStyle(HeaderData.CENTERED);
        page.append(header).linebreak().append(patternEditor);
        
//        WidgetPageSimple<DevicePageDictionary> page = 
//        		(WidgetPageSimple<DevicePageDictionary>) patternPageUtil.editPatternPage(PhysicalElementPattern.class, widgetApp, "devices.html", am, true, DevicePageDictionary.class);
        page.registerLocalisation(DevicePageDictionary_de.class).registerLocalisation(DevicePageDictionary_en.class).registerLocalisation(DevicePageDictionary_fr.class);
        
        List<Class<? extends PhysicalElement>> allowedTypes = new ArrayList<Class<? extends PhysicalElement>>();
        allowedTypes.add(ElectricLight.class);allowedTypes.add(Thermostat.class);allowedTypes.add(CoolingDevice.class);allowedTypes.add(WashingMachine.class); // TODO extend
        
        PatternCreatorConfiguration<PhysicalElementPattern, PhysicalElement> config = new PatternCreatorConfiguration<>(allowedTypes);
        final WidgetPage<DeviceCreationPageDictionary> pageCreate = widgetApp.createWidgetPage("new_devices.html", false);
        PatternCreator<PhysicalElementPattern, PhysicalElement, DeviceCreationPageDictionary> patternCreator 
        	= PatternCreator.getInstance(pageCreate, "patternCreator", PhysicalElementPattern.class, am, config, DeviceCreationPageDictionary.class);
        header = new Header(pageCreate, "header", "Create a device") {

			private static final long serialVersionUID = 1L;

			@Override
        	public void onGET(OgemaHttpRequest req) {
        		setText(pageCreate.getDictionary(req).pageTitle(), req);
        	}
        	
        };
        header.addDefaultStyle(HeaderData.CENTERED);
        pageCreate.append(header).linebreak().append(patternCreator);
        
//        PatternCreatorConfiguration<PhysicalElementPattern, PhysicalElement> config = new PatternCreatorConfiguration<>(allowedTypes);
//        WidgetPageSimple<DeviceCreationPageDictionary> pageCreate = (WidgetPageSimple<DeviceCreationPageDictionary>) patternPageUtil
//        		.createPatternPage(PhysicalElementPattern.class, widgetApp, "new_devices.html", am, false, config, DeviceCreationPageDictionary.class);
        pageCreate.registerLocalisation(DeviceCreationPageDictionary_de.class).registerLocalisation(DeviceCreationPageDictionary_en.class).registerLocalisation(DeviceCreationPageDictionary_fr.class);

		NavigationMenu menu = new NavigationMenu(" Browse pages");
		menu.addEntry("Device configuration", page);
		menu.addEntry("Create devices", pageCreate);
		page.getMenuConfiguration().setCustomNavigation(menu);
		pageCreate.getMenuConfiguration().setCustomNavigation(menu);
		page.getMenuConfiguration().setCustomNavigation(menu);		
    }

    @Override
    public void stop(Application.AppStopReason reason) {
    	if (widgetApp != null)
    		widgetApp.close();
    	widgetApp = null;
    	logger = null;
    }
}
