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
package de.iwes.widgets.pattern.widget.dragdropassign;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.services.IconService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.dragdropassign.Container;
import de.iwes.widgets.html.dragdropassign.DragDropAssign;
import de.iwes.widgets.html.dragdropassign.DragDropAssignData;
import de.iwes.widgets.html.dragdropassign.DragDropData;
import de.iwes.widgets.html.dragdropassign.Item;

/**
 * Drag&amp;assign widget that allows to assign resource patterns of type T to
 * resources of type R (e.g. assign patterns finding devices to resources of
 * type room)
 *
 * @param <T> Resource-patterns of type T (for Items)
 * @param <R> Resources of type R (for Containers)
 */
public abstract class PatternDragDropAssign<T extends ResourcePattern<?>, R extends ResourcePattern<?>> extends DragDropAssign {

    private static final long serialVersionUID = -2773407501782806191L;
    private final static String SEPARATOR = "___roomLinkSep___";
//    private final List<T> patternList;
//    private final List<R> containerList;
//    private final ResourcePatternManagement<T, ?> itemsManagement;
//    private final ResourcePatternManagement<R, ?> containerManagement;
    
    private final Container unassigned;
    private final DragDropData ddata;
    
    private final IconService iconService;
    private final NameService nameService;
    private final ResourcePatternAccess rpa;
    private final ApplicationManager am;
    private final Class<T> itemsClass;
    private final Class<R> containerClass;
    
    /******** Abstract methods **************/
    
    /**
     * May be null if not assigned yet
     *
     * @param pattern
     * @return
     */
    public abstract R getItemContainer(T pattern);

    /**
     * Callback when an item has been moved to a new container
     */
    public abstract void onUpdate(T pattern, R from, R to);
    
    /**
     * Override to set background image to container
     */
    public String getContainerImage(String id) {
    	return null;
    }
    
    /**
     * Override this to enable icons for the containers, which link to
     * another page. Use {@link PatternDragDropAssign#getIconType(String)}
     * to set the icon. 
     */
    public String getIconLink(String id) {
    	return null;
    }
    
    /** 
     * default: "glyphicon glyphicon-envelope"
     */
    public String getIconType(String id) {
    	return null;
    }
    
    /******* Constructors ***********/
    
    public PatternDragDropAssign(WidgetPage<?> page, String id, ApplicationManager am, Class<T> itemsClass, Class<R> containerClass) {
    	this(page, id, am, itemsClass, containerClass, null,null);
    }

    /**
     * @param page
     * @param id
     * @param am
     * @param itemsClass
     * @param containerClass
     * @param iconService
     * @param nameService
     */
	public PatternDragDropAssign(WidgetPage<?> page, String id, ApplicationManager am, Class<T> itemsClass, Class<R> containerClass, IconService iconService, NameService nameService) {
        super(page, id, null, true);
        this.ddata = new DragDropData();

//        setData(ddata, null);  // this is problematic, since it will prevent the setDefault... methods inherited from OgemaWidget to fail
        setDefaultData(ddata);
//        this.patternList = patternList;
//        this.containerList = containerList;
//        this.itemsManagement = new ResourcePatternManagement(am, itemsClass, null);
//        this.containerManagement = new ResourcePatternManagement(am, containerClass, null);
        this.am =am;
        this.rpa = am.getResourcePatternAccess();
        this.itemsClass = itemsClass;
        this.containerClass = containerClass;
        this.unassigned = new Container("unassigned", "Unassigned", "/ogema/widget/dragdropassign/icons/unassignedContainer2.svg");
        this.iconService = iconService;
        this.nameService = nameService;
    }
    
    /******* Internal methods ************/

    @Override
    public DragDropAssignData createNewSession() {
        return new PatternDragDropOptions(this);
    }

    public class PatternDragDropOptions extends DragDropAssignData {

        public PatternDragDropOptions(DragDropAssign mainWidget) {
            super(mainWidget);
        }

        @Override
        public JSONObject retrieveGETData(OgemaHttpRequest req) {
        	OgemaLocale loc = req.getLocale();
            ddata.getContainers().clear();
            ddata.getContainers().add(unassigned);
//            for (R container : containerManagement.getElements()) {
            for (R container : rpa.getPatterns(containerClass,AccessPriority.PRIO_LOWEST)) {
            	String nm = (nameService == null) ? container.model.getLocation() : nameService.getName(container.model, loc, true, true);
            	if (nm == null) nm = container.model.getLocation();
            	String id = getElementId(container.model.getLocation());
            	Container cont = new Container(
                        null,
                        id,
                        nm,
                        null);
            	cont.setIconLink(getIconLink(id));
            	cont.setIconType(getIconType(id));
                ddata.getContainers().add(cont);
                
                String bgImagePath = getContainerImage(id);
                if (bgImagePath != null && !bgImagePath.isEmpty())
                	setContainerBackground(id, bgImagePath);
            }
            ddata.getItems().clear();
            for (T itemPattern :  rpa.getPatterns(itemsClass,AccessPriority.PRIO_LOWEST)) {
            	String nm = (nameService == null) ? itemPattern.model.getLocation() : nameService.getName(itemPattern.model, loc, true, true);
            	if (nm == null) nm = itemPattern.model.getLocation();
            	String iconPath = (iconService == null) ? "" : iconService.getIcon(itemPattern.model.getResourceType());
                Item item = new Item(null,
                        getElementId(itemPattern.model.getLocation()),
                        nm,
                        "description",
                        iconPath); 

                R assignedContainer = getItemContainer(itemPattern);
                Container container = getContainerFromResource(assignedContainer);
                if (assignedContainer == null) {
                    item.setContainer(unassigned);
                } else {
                    item.setContainer(container);
                }
                ddata.getItems().add(item);
            }

            JSONObject result = super.retrieveGETData(req);
            return result;
        }
    }

    //override this
    public String getPatternLocation(T pattern) {
        return pattern.model.getLocation();
    }

    private static String getElementId(String id) {
        return id.replaceAll("\\s", "").replace("/", SEPARATOR);
    }
    
    private static String getPathFromId(String path) {
    	return path.replace(SEPARATOR, "/");
    }

    private R getResourceFromContainer(Container c) {
//    	String path = c.getId().replace("_", "/");  // FIXME this is not possible... the resource name may have regular '_' characters
    	String path = getPathFromId(c.getId());
    	final Resource res = am.getResourceAccess().getResource(path);
    	if (res == null)
    		return null;
    	return AccessController.doPrivileged(new PrivilegedAction<R>() {

			@Override
			public R run() {
				try {
					return containerClass.getConstructor(Resource.class).newInstance(res);
				} catch (Exception e) {
					return null;
				}
			}
		});
//        for (R res : containerManagement.getElements()) {
//            if (getElementId(res.model.getLocation()).equals(c.getId())) {
//                return res;
//            }
//        }
//        return null;
    }

    private Container getContainerFromResource(R resourceContainer) {
    	if (resourceContainer == null) return null;
        for (Container c : ddata.getContainers()) {
            if (c.getId().equals(getElementId(resourceContainer.model.getLocation()))) {
                return c;
            }
        }
        return null;
    }

    public List<T> getPatternList() {
//        return Collections.unmodifiableList(itemsManagement.getElements());
    	return rpa.getPatterns(itemsClass, AccessPriority.PRIO_LOWEST);
    }

    public List<R> getContainerList() {
//        return Collections.unmodifiableList(containerManagement.getElements());
    	return rpa.getPatterns(containerClass, AccessPriority.PRIO_LOWEST);
    }

    /**
     * Use {@link #onUpdate(ResourcePattern, ResourcePattern, ResourcePattern)} instead
     */
    @Override
    public final void onUpdate(Item item, Container from, Container to, OgemaHttpRequest req) {
        T pattern = null;
        R fromRes = null;
        R toRes = null;

        for (T p : getPatternList()) {
            if (getElementId(p.model.getLocation()).equals(item.getId())) {
                pattern = p;
                break;
            }
        }
        if (pattern == null) {
        	LoggerFactory.getLogger(getClass()).error("No pattern found for item [" + item.getId() + "]!");
        	return;
        } else {
            if (!from.getId().equals(unassigned.getId())) { //see 'to'
                fromRes = getResourceFromContainer(from);
                if (fromRes == null) {
                    throw new IllegalStateException("No resource found for from-container [" + from.getId() + "]!");
                }
            }
            if (!to.getId().equals(unassigned.getId())) {  //if items are moved to unassigned-container (doesn't have a corresponding room-resource)
                toRes = getResourceFromContainer(to);
                if (toRes == null) {
                    throw new IllegalStateException("No resource found for to-container [" + to.getId() + "]!");
                }
            }
        }
        onUpdate(pattern, fromRes, toRes);
    }
    
    /*
     * FIXME problematic... when is this supposed to be set? Only before creation of container reasonably possible
     */
    public void setContainerBackground(String id, String bgImagePath) { //Query container by id and set background-image
        for (Container c : ddata.getContainers()) {
            if (c.getId().equals(id)) {
                c.setBgImagePath(bgImagePath);
                return;
            }
        }
    }
}
