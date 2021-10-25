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
package org.ogema.apps.roomlink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary;
import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary_de;
import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary_en;
import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary_fr;
import org.ogema.apps.roomlink.pattern.PhysicalElementPattern;
import org.ogema.apps.roomlink.pattern.RoomPattern;
import org.ogema.apps.roomlink.test.DummyDriver;
import org.ogema.apps.roomlink.utils.RoomLinkUtils;
import org.ogema.apps.roomlink.utils.RoomLinkDummyResourceCreator;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.driverconfig.LLDriverInterface;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.html.bricks.SampleImages;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.dragdropassign.DragDropAssignData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.pattern.widget.dragdropassign.PatternDragDropAssign;

@Component(specVersion = "1.2")
@Service(Application.class)
@Reference(referenceInterface=LLDriverInterface.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC, 
		bind="addDriver",
		unbind="removeDriver")
@Properties({
	@Property(name="org.ogema.apps.device.mgmt", value="org.ogema.model.locations.Room"), // announce itself as a rooms managing app
	@Property(name=Constants.SERVICE_RANKING, intValue=1)
})
public class RoomLink implements Application {

	private OgemaLogger logger;
    private ApplicationManager appMan;
    private ResourceAccess resAcc;
    private WidgetApp widgetApp;
    private String imagesPath;
    private DummyDriver driver;
    private ServiceRegistration<LLDriverInterface> sr;

    @Reference
    private OgemaGuiService widgetService;
    
    private final ConcurrentMap<String, LLDriverInterface> llDrivers = new ConcurrentHashMap<>();
    
	@Override
    public void start(final ApplicationManager appManager) {
        this.appMan = appManager;
        this.logger = appManager.getLogger();
        this.resAcc = appManager.getResourceAccess();

        logger.debug("{} started", getClass().getName());
        
        File images = appManager.getDataFile("images");
        images.mkdirs();
//        uploadPaths = appManager.getWebAccessManager().registerWebResourcePath("images", images.getAbsolutePath()); // not working, tries to resolve path relative o bundle loc
        /**
         * access image via https://localhost:8443/org/ogema/widgets/room-link/images?file=FILENAME
         */
        // TODO more convenient way to use an image
        HttpServlet imagesServlet = new HttpServlet() {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				String file = req.getParameter("file");
				if (file == null || file.isEmpty()) {
					resp.setStatus(200);
					return;
				}
				File fl = appManager.getDataFile("images/" + file);
				if (!fl.exists()) {
					resp.setStatus(404); // not found
					return;
				}
				OutputStream out = resp.getOutputStream();
				InputStream inputStream = null;
				try {
	                inputStream = new FileInputStream(fl);        	
		            Integer c;
		            //continue reading till the end of the file
		            while ((c = inputStream.read()) != -1) {
		                //writes to the output Stream
		                out.write(c);
		            }
				} finally {
					try {
						out.flush();
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (inputStream != null)
							inputStream.close();
					}
				}
				String contentType  = RoomLinkUtils.getContentType(fl);
				resp.setContentType(contentType);
				resp.setStatus(200);
			}
			
		};
		imagesPath = appManager.getWebAccessManager().registerWebResourcePath("images", imagesServlet);

		// create testresources flag: use either app-specific or generic system property
		boolean createTestResources = Boolean.getBoolean("org.ogema.apps.createtestresources");
		
		if (createTestResources) {
	        RoomLinkDummyResourceCreator creator = new RoomLinkDummyResourceCreator(appManager);
	        creator.generateDummyResources();
		}

		widgetApp = widgetService.createWidgetApp("/de/iwes/apps/roomlink/gui", appManager);
        
//        final WidgetPage<RoomDetailsDictionary> roomDetailsPage = new WidgetPageImpl<RoomDetailsDictionary>(widgetApp,"roomDetails.html"); // TODO fill
//        roomDetailsPage.registerLocalisation(RoomDetailsDictionary_de.class).registerLocalisation(RoomDetailsDictionary_en.class).registerLocalisation(RoomDetailsDictionary_fr.class);
//        RoomDetailsPageBuilder.addWidgets(roomDetailsPage, appManager, nameService);
        
//        final WidgetPage<RoomLinkDictionary> widgetPage = new WidgetPageImpl<>(widgetApp,true);
        final WidgetPage<RoomLinkDictionary> widgetPage = widgetApp.createStartPage(); 
        widgetPage.registerLocalisation(RoomLinkDictionary_de.class).registerLocalisation(RoomLinkDictionary_en.class).registerLocalisation(RoomLinkDictionary_fr.class);

        widgetPage.setTitle("Room-Link");
        widgetPage.setBackgroundImg(SampleImages.BLUE_BLEND_LIGHTS);
        Header header = new Header(widgetPage, "header") {

			private static final long serialVersionUID = 1L;
        	
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(widgetPage.getDictionary(req).header(), req);
			}
        };
        header.addDefaultStyle(HeaderData.CENTERED);
        header.setDefaultColor("#AACCFF");
        widgetPage.append(header);

        // FIXME if a container is deleted, the widget still displays it
        final PatternDragDropAssign<PhysicalElementPattern, RoomPattern> ddAssign = new PatternDragDropAssign<PhysicalElementPattern, RoomPattern>(widgetPage, "ddAssign", appManager, 
        													PhysicalElementPattern.class, RoomPattern.class, widgetService.getIconService(), widgetService.getNameService()) {
            private static final long serialVersionUID = -5785953704167898304L;
            private volatile int pollCounter = 0;

            @Override
            public RoomPattern getItemContainer(PhysicalElementPattern pattern) {
            	if (pattern == null || !pattern.model.exists()) return null; // FIXME 2nd case should not occur
                PhysicalElementPattern pPattern = (PhysicalElementPattern) pattern;
                try {
	                Room rm = pPattern.model.location().room();
	                if (rm.exists()) {
	                    return new RoomPattern(rm);
	                } else {
	                    return null;
	                }
                } catch (SecurityException e) {
                	return null;
                }
            }
            
//            @Override
//            public void onUpdate(Item item, Container from,	Container to, OgemaHttpRequest req) {
            														// TODO Auto-generated method stub
            														
//            													}
            
            @Override
			public void onGET(OgemaHttpRequest req) {
            	// XXX
				if (getPollingInterval(req) > 0) {
					pollCounter++;
					if (pollCounter > 5) {
						setPollingInterval(0, req);
						pollCounter = 0;
					}
				}
				super.onGET(req);
			}

            @Override
            public void onUpdate(PhysicalElementPattern pattern, RoomPattern from, RoomPattern to) {
            	if (pattern == null || !pattern.model.exists()) return;
            	if (to == null) 
            		RoomLinkUtils.setLocation((PhysicalElement) pattern.model, null);
            	else
            		RoomLinkUtils.setLocation((PhysicalElement) pattern.model, (Room) to.model);
            }
            
            @Override
			public String getContainerImage(String id) {
            	try {
	            	Room room = resAcc.getResource(id);
	            	if (room == null)
	            		return null;
	            	StringResource str = room.getSubResource("imageFile", StringResource.class);
	            	if (str.isActive()) {
	            		String val  =str.getValue();
	            		if (val.trim().isEmpty()) return null;
	            		val = imagesPath + "?file=" + val;
	            		return val;
	            	}
            	} catch (Exception e) {
            		logger.error("Could not determine image file",e); 
            	}
            	return null;
			}
            
            // FIXME
            @Override
			public String getIconLink(String id) {
//            	return roomDetailsPage.getFullUrl() + "?configId=" + id;
            	return "#";
			}

        };
        Set<WidgetStyle<?>> ddStyles = new HashSet<WidgetStyle<?>>();
        ddStyles.add(DragDropAssignData.STYLE_A);ddStyles.add(DragDropAssignData.CONTAINER_IMAGE_CENTERED_BOTTOM);
        ddAssign.setDefaultStyles(ddStyles);
        
        Alert alert = new Alert(widgetPage, "genericAlert", "Moin");
        alert.setDefaultVisibility(false);
        widgetPage.append(alert).linebreak();
        
        Popup editRoomPopup = new Popup(widgetPage, "editRoomPopup", true);
        final Dropdown roomSelector = EditRoomPopupBuilder.addWidgets(widgetPage, editRoomPopup, alert, ddAssign, appManager);
        Button editRoomPopupTrigger =new Button(widgetPage, "editRoomPopupTrigger") {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
        		setText(widgetPage.getDictionary(req.getLocaleString()).editRoomButton(), req);
        	}
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				roomSelector.selectSingleOption(EditRoomPopupBuilder.emptyValue, req);
			}
        	
        };
        editRoomPopupTrigger.triggerAction(editRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        editRoomPopupTrigger.triggerAction(editRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
        
        Popup newRoomPopup = new Popup(widgetPage, "newRoomPopup", true);
        Button createBtn = NewRoomPopupBuilder.addWidgets(widgetPage, newRoomPopup, alert, appManager, true, null);
        createBtn.triggerAction(ddAssign, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        createBtn.triggerAction(newRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
        Button newRoomPopupTrigger =new Button(widgetPage, "newRoomPopupTrigger") {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
        		setText(widgetPage.getDictionary(req.getLocaleString()).createRoomButton(), req);
        	}
        	
        };
        newRoomPopupTrigger.triggerAction(newRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
        
        RegisterDevicePopup devicePopup = new RegisterDevicePopup(widgetPage, "registerDevice", llDrivers,ddAssign,appManager.getResourceAccess());
        Button deviceScanPopupTrigger = new Button(widgetPage, "deviceScanPopupTrigger", "Scan for devices") {
        	
        	private static final long serialVersionUID = 1L;
        	
        	@Override
        	public void onGET(OgemaHttpRequest req) {
        		setText(widgetPage.getDictionary(req).registerDeviceHeader(),req);
        	}
        	
        };
        deviceScanPopupTrigger.triggerAction(devicePopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
        deviceScanPopupTrigger.triggerAction(devicePopup.pairingModeActiveLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
        deviceScanPopupTrigger.triggerAction(devicePopup.submit, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

        widgetPage.append(editRoomPopupTrigger).append(newRoomPopupTrigger).append(deviceScanPopupTrigger).linebreak().linebreak();
        widgetPage.append(ddAssign).linebreak();
        widgetPage.append(newRoomPopup).linebreak().append(editRoomPopup).linebreak();
        widgetPage.append(devicePopup);
        
        // widgetPage.getMenuConfiguration().setShowMessages(false);
        
        // dummy driver; for tests only!
        Boolean testRes = Boolean.getBoolean("org.ogema.apps.roomlink.createdummydriver");
        if (testRes) {
	        driver = new DummyDriver(appManager);
	        BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();
	        sr = ctx.registerService(LLDriverInterface.class, driver, null);
        }
    }
	
    @Override
    public void stop(Application.AppStopReason reason) {
    	if (widgetApp != null)
    		widgetApp.close();
    	if (appMan != null)
    		appMan.getWebAccessManager().unregisterWebResourcePath("images");
    	widgetApp = null;
    	appMan = null;
    	resAcc = null;
    	logger= null;
    	imagesPath = null;
    	if (sr != null)
    		sr.unregister();
    	sr = null;
    	driver = null;
    }
    
    // use driver.whichTech to generate a (more) reader friendly description
    protected void addDriver(LLDriverInterface driver) {
    	llDrivers.put(driver.whichID(), driver);
    }
    
    protected void removeDriver(LLDriverInterface driver) {
    	llDrivers.remove(driver.whichID());
    }
    
    
}
