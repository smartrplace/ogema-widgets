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

package de.iwes.widgets.html.accordion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.LabelledItem;

public class AccordionData extends WidgetData {

    public static final WidgetStyle<Accordion> BOOTSTRAP_BLUE = new WidgetStyle<Accordion>("panel", Arrays.asList("panel", "panel-primary"), 1);
    public static final WidgetStyle<Accordion> BOOTSTRAP_RED = new WidgetStyle<Accordion>("panel", Arrays.asList("panel", "panel-danger"), 1);
    public static final WidgetStyle<Accordion> BOOTSTRAP_GREEN = new WidgetStyle<Accordion>("panel", Arrays.asList("panel", "panel-success"), 1);
    public static final WidgetStyle<Accordion> BOOTSTRAP_LIGHT_BLUE = new WidgetStyle<Accordion>("panel", Arrays.asList("panel", "panel-info"), 1);
    public static final WidgetStyle<Accordion> BOOTSTRAP_DEFAULT = new WidgetStyle<Accordion>("panel", Arrays.asList("panel", "panel-default"), 1);
    public static final WidgetStyle<Accordion> BOOTSTRAP_ORANGE = new WidgetStyle<Accordion>("panel", Arrays.asList("panel", "panel-warning"), 1);

    protected final Set<AccordionItem> items;
    private boolean hideInactive;
    
    // only relevant if this is a global options elements
    // Map<SessionId, Map<tabId, active>> 
//    private ConcurrentMap<String,Map<String,Boolean>> activeTabs = new ConcurrentHashMap<String, Map<String,Boolean>>();
    
    /*********** Constructor **********/

    public AccordionData(Accordion accordion) {
        super(accordion);
        items = new LinkedHashSet<AccordionItem>();     
    }
    
    /******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {

        JSONObject result = new JSONObject();
        JSONArray j_items = new JSONArray();

        readLock();
        try {
	        for (AccordionItem a : items) {
	            j_items.put(a.getJSON(req.getLocale()));
	        }
	        result.put("hideInactive", hideInactive);
        } finally {
        	readUnlock();
        }
        result.put("items", j_items);

        return result;
    }
    
    @Override
    protected Collection<OgemaWidget> getSubWidgets() {
    	Set<OgemaWidget> widgets = new LinkedHashSet<OgemaWidget>();
    	readLock();
    	try {
	    	Iterator<AccordionItem> it = items.iterator();
	    	while(it.hasNext()) {
	    		AccordionItem item = it.next();
	    		if (item.getWidget()!= null) {
	    			widgets.add(item.getWidget());
	    		}
	    	}
    	} finally {
    		readUnlock();
    	}
    	return widgets;
    }
    
    @Override
    protected void removeSubWidgets() {
    	final List<OgemaWidgetBase<?>> subs = new ArrayList<>();
    	writeLock();
    	try {
	    	Iterator<AccordionItem> it = items.iterator();
	    	while (it.hasNext()) {
	           	final AccordionItem item = it.next();
	           	final OgemaWidgetBase<?> sub = item.getWidget();
	            if (sub != null) {
	                it.remove();
	                subs.add(sub);
	            }
	        }
    	} finally {
    		writeUnlock();
    	}
    	for (OgemaWidgetBase<?> w : subs) 
    		w.destroyWidget();
    }
    
    @Override
    protected boolean removeSubWidget(OgemaWidgetBase<?> subwidget) {
    	writeLock();
    	boolean found = false;
    	try {
	    	Iterator<AccordionItem> it = items.iterator();
	    	while (it.hasNext()) {
	           	AccordionItem item = it.next();
	            if (item.getWidget()!= null && item.getWidget().equals(subwidget)) {
	                it.remove();
//	                subwidget.destroyWidget(); // risk of deadlock?
//	                return true;
	                found = true;
	                break;
	            }
	        }
    	} finally {
    		writeUnlock();
    	}
    	if (found)
    		subwidget.destroyWidget();
    	return found;
    }
    
    /********** Public methods **********/
    
    //Collapse items except for active-item
    public void hideInactive(boolean hideInactive) {
    	writeLock();
    	try {
    		this.hideInactive = hideInactive;
    	} finally {
    		writeUnlock();
    	}
    }

    //Widgets (single)
    public AccordionItem addWidget(String id, OgemaWidget widget, boolean expanded) {
//        super.registerSubWidget(widget);	// TODO
    	writeLock();
    	try {
    		if (hasItem(id))
    			throw new IllegalArgumentException("Item with id " + id + " already exists.");
    		final AccordionItem item = new AccordionItem(id, (OgemaWidgetBase<?>) widget, expanded);
    		items.add(item);
    		return item;
    	} finally {
    		writeUnlock();
    	}
    }

    public AccordionItem addWidget(String id, OgemaWidget widget) {
        return addWidget(id, widget, false);
    }
    
    protected AccordionItem addWidget(LabelledItem labelledItem, OgemaWidget widget, boolean expanded) {
    	writeLock();
    	try {
    		if (hasItem(labelledItem.id()))
    			throw new IllegalArgumentException("Item with id " + labelledItem.id() + " already exists.");
    		final AccordionItem item =new AccordionItem(labelledItem, (OgemaWidgetBase<?>) widget, expanded); 
    		items.add(item);
    		return item;
    	} finally {
    		writeUnlock();
    	}
    }

    //Pages (external HTML)
    public AccordionItem addPage(String title, String path) {
        return addPage(title, path, false);
    }

    public AccordionItem addPage(String title, String path, boolean expanded) {
    	writeLock();
    	try {
        	if (hasItem(title))
        		throw new IllegalArgumentException("Item with id " + title + " already exists.");
    		final AccordionItem item  = new AccordionItem(title, path, expanded, ItemType.PAGE);
    		items.add(item);
    		return item;
    	} finally {
    		writeUnlock();
    	}
    }
    
    public boolean removePage(AccordionItem page) {
        return removeItem(page);
    }

    public boolean removePage(String title) {
        return removeItem(title);
    }

    //Items (HTML provided)
    public AccordionItem addItem(String title, String html) {
        return addItem(title, html, false);
    }
    
    protected AccordionItem addItem(LabelledItem item, String html, boolean expanded) {
    	writeLock();
    	try {
    		if (hasItem(item.id()))
    			throw new IllegalArgumentException("Item with id " + item.id() + " already exists.");
    		final AccordionItem newItem =new AccordionItem(item, html, expanded, ItemType.HTML); 
    		items.add(newItem);
    		return newItem;
    	} finally {
    		writeUnlock();
    	}
    }

    /**
     * Do not pass unchecked user data as html to this method, as it is vulnerable to 
     * cross site scripting attacks.
     * @param title
     * @param html
     * @param expanded
     */
    public AccordionItem addItem(String title, String html, boolean expanded) {
    	writeLock();
    	try {
    		if (hasItem(title))
    			throw new IllegalArgumentException("Item with id " + title + " already exists.");
    		final AccordionItem newItem = new AccordionItem(title, html, expanded, ItemType.HTML);
    		items.add(newItem);
    		return newItem;
    	} finally {
    		writeUnlock();
    	}
    }

    public boolean removeItem(AccordionItem item) {
    	final OgemaWidgetBase<?> w;
    	final boolean found;
    	writeLock();
    	try {
    		w = item.getWidget();
    		found = items.remove(item);
    	} finally {
    		writeUnlock();
    	}
    	if (w != null)
			w.destroyWidget();
    	return found;
    }

    public boolean removeItem(final String id) {
    	
        ArrayList<AccordionItem> toDelete = new ArrayList<>();
        ArrayList<OgemaWidgetBase<?>> widgetsToDelete = new ArrayList<OgemaWidgetBase<?>>();
        readLock();
        try {
	        for (AccordionItem item : items) {
	            if (item.getTitle().equals(id)) {
	            	if (item.getWidget() != null) widgetsToDelete.add(item.getWidget());
	            	else toDelete.add(item);
	            }
	        }
        } finally {
        	readUnlock();
        }
        if (toDelete.isEmpty() && widgetsToDelete.isEmpty()) {
            return false;
        } else {
        	Iterator<OgemaWidgetBase<?>> it = widgetsToDelete.iterator();
        	while (it.hasNext()) {
        		OgemaWidgetBase<?> wd = it.next();
        		removeSubWidget(wd); // calls subwidget method... we should not hold a lock here!
        	}    	
        	writeLock(); 
	    	try {
	            if (widgetsToDelete.isEmpty()) return items.removeAll(toDelete);
	            else return true;
	    	} finally {
	    		writeUnlock();
	    	}
        }
    }

    public void clearItems() {
    	writeLock();
    	try {
    		items.clear();
    	} finally {
    		writeUnlock();
    	}
    	removeSubWidgets();
    }
    
    public boolean hasItem(String id) {
    	readLock();
    	try {
	    	for (AccordionItem item: items) {
	    		if (item.getTitle().equals(id))
	    			return true;
	    	}
	    	return false;
    	} finally {
    		readUnlock();
    	}
    }
    
    public AccordionItem getItem(String id) {
    	readLock();
    	try {
	    	for (AccordionItem item: items) {
	    		if (item.getTitle().equals(id))
	    			return item;
	    	}
	    	return null;
    	} finally {
    		readUnlock();
    	}
    }
    
    public Set<String> getAllItems() {
    	readLock();
    	try {
	    	Set<String> itemTitles = new LinkedHashSet<>();
	    	for (AccordionItem item: items) {
	    		itemTitles.add(item.getTitle());
	    	}
	    	return itemTitles;
    	} finally {
    		readUnlock();
    	}
    }
    
    /********** Internal methods **********/
    
    protected void setItems(Collection<AccordionItem> items) {
    	readLock();
    	try {
    		// FIXME need to delete subwidgets!
	    	this.items.clear();
	    	this.items.addAll(items);
    	} finally {
    		readUnlock();
    	}
    }
    
}
