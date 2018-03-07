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

package de.iwes.widgets.icon.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.model.actors.Actor;
import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.devices.buildingtechnology.Radiator;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.generators.PVPlant;
import org.ogema.model.devices.generators.WindPlant;
import org.ogema.model.devices.vehicles.Vehicle;
import org.ogema.model.devices.whitegoods.CoolingDevice;
import org.ogema.model.locations.Room;
import org.ogema.model.metering.ElectricityMeter;
import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.services.IconService;

/**
 * Implementation of the default icon service for OGEMA
 * 
 * @author Tobias Gries <tobias.gries@iwes.fraunhofer.de>
 */
@Component(specVersion = "1.2")
@Service(IconService.class)
public class IconServiceImpl extends HttpServlet implements IconService {
    
    private static final long serialVersionUID = 2388594254205606700L;
    
    private final Map<Class<? extends Resource>, String> iconMap = new HashMap<>();
    private final String servletUrl= "/ogema/widget/icons/servlet";
    
    @Reference
    private HttpService httpService;

    private void setStandardIcons() {
        
        /**
         * Ensure all icons are free for commercial use, without requiring
         * any sort of attribution (preferably, icons should be in the public domain)
         */
        iconMap.put(Resource.class, "/ogema/widget/icons/Resource.svg");
        iconMap.put(Vehicle.class, "/ogema/widget/icons/Vehicle.png");
        iconMap.put(Schedule.class, "/ogema/widget/icons/Schedule.png");
        iconMap.put(Sensor.class, "/ogema/widget/icons/Sensor.png");
        iconMap.put(Actor.class, "/ogema/widget/icons/Actor.png");
        iconMap.put(Room.class, "/ogema/widget/icons/Room.png");
        iconMap.put(Thermostat.class, "/ogema/widget/icons/Thermostat.jpg");
        iconMap.put(PVPlant.class, "/ogema/widget/icons/PVPlant.svg");
        iconMap.put(WindPlant.class, "/ogema/widget/icons/WindPlant.svg");
        addResourceDefaultPath(TemperatureSensor.class, true);
        addResourceDefaultPath(HumiditySensor.class,true);
        addResourceDefaultPath(PhysicalElement.class,false);
        addResourceDefaultPath(ElectricLight.class, true);
        addResourceDefaultPath(Radiator.class, false);
        addResourceDefaultPath(ElectricityMeter.class, false);
        addResourceDefaultPath(CoolingDevice.class, true);
    }
    
    /**
     * @param resourceType
     * @param imageType
     * 		true: svg, false: png
     */
    private void addResourceDefaultPath(Class<? extends Resource> resourceType, boolean imageType) {
    	String name = resourceType.getSimpleName();
    	iconMap.put(resourceType, "/ogema/widget/icons/" + name + (imageType ? ".svg" :".png"));
    }

    @Activate
    private void registerResources() {
        try {
        	setStandardIcons();
            httpService.registerServlet(servletUrl, this, null, null);
            httpService.registerResources("/ogema/widget/icons", "/org/ogema/tools/icons", null);
        } catch (ServletException | NamespaceException ex) {
            ex.printStackTrace();
        }
    }

    @Deactivate
    private void unregisterResources() {
    	try {
    		httpService.unregister("/ogema/widget/icons");
    		httpService.unregister(servletUrl);
    	} catch (Exception e) {
    		LoggerFactory.getLogger(getClass()).warn("Unable to unregister web resources: " + e);
    	}
    }
    
    @Override
    public String getIcon(Resource res) {
        Class<? extends Resource> resourceClass = res.getResourceType();
        return getIcon(resourceClass);
    }

    @SuppressWarnings("unchecked")
	@Override
    public String getIcon(Class<? extends Resource> resourceClass) {
  
        if(resourceClass == null) {
            return iconMap.get(Resource.class);
        }

        while(!iconMap.containsKey(resourceClass)) {
            
            resourceClass = (Class<? extends Resource>) resourceClass.getGenericInterfaces()[0];
            if(resourceClass == null || Resource.class.equals(resourceClass)) {
                return iconMap.get(Resource.class);
            } 
        }
 
        return iconMap.get(resourceClass);

    }

    @Override
    public Set<Class<? extends Resource>> getAvailableTypes() {
        return iconMap.keySet();
    }

    @Override
    public boolean isTypeAvailable(Class<? extends Resource> resourceType) {
        return iconMap.get(resourceType) != null;
    }

    @Override
    public String getServletUrl() throws UnsupportedOperationException {
        return servletUrl;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        String resource = req.getParameter("resource");
        boolean asImage = false;
        
        if(req.getParameter("asImage") != null) {
            asImage = req.getParameter("asImage").equals("true");
        }

        if(resource != null) {

            Class<? extends Resource> resourceClass = null;
      
            try {
                resourceClass = (Class<? extends Resource>) Class.forName(resource, false, Data.class.getClassLoader());
            } catch (ClassNotFoundException ex) {}

            if(resourceClass == null) {
                try {
                    resourceClass = (Class<? extends Resource>) Class.forName(resource, false, Resource.class.getClassLoader());
                } catch (ClassNotFoundException ex) {
                    resourceClass = Resource.class;
                }
            }
            write(resp, getIcon(resourceClass), asImage);
                      
        } else {
            write(resp, getIcon(Resource.class), asImage);  
        }   
    }
    
    private void write(HttpServletResponse resp, String path, boolean asImage) throws ServletException, IOException {
        if(asImage) {
            resp.sendRedirect(path);
        } else {
            resp.getWriter().write(path);
        }
        resp.setStatus(200);
    }
    
    
    
}
