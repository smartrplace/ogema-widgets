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

package de.iwes.widgets.html;

import java.util.ArrayList;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;

/**
 * A helper tool for SubmitWidgets; instantiate one object per SubmitWidget, and
 * pass methods to be implemented to the util object.  
 */
public class SubmitUtil {

	private final OgemaWidget widget;
	private WidgetGroup dependentWidgets = null;
	
	public SubmitUtil(OgemaWidget contextWidget) {
		this.widget = contextWidget;
	}
	
	protected void init() {
		dependentWidgets = widget.getPage().registerWidgetGroup(widget.getId() + "__dependentWidgets", new ArrayList<OgemaWidget>());
//		widget.triggerAction(dependentWidgets, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); 
		widget.triggerAction(dependentWidgets, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST); // XXX why?
	}
	
	// TODO deal with non-session-specific widget
	public synchronized void registerWidget(OgemaWidget widget) {
		if (dependentWidgets == null)
			init();
		if (!((OgemaWidgetBase<?>) widget).isSessionSpecific()) {
			dependentWidgets.addWidget(widget); 
			// FIXME move to group/init 
			this.widget.triggerAction(widget, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST); // XXX why not use the group?
			widget.setDefaultSendValueOnChange(false);
		}
//		else 
//			this.widget.triggerAction(widget, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST, req);
	}
	
	public synchronized void unregisterWidget(OgemaWidget widget) {
		if (dependentWidgets == null)
			return;
		dependentWidgets.removeWidget(widget);
		this.widget.removeTriggerAction(widget, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST);
	}
	
	public synchronized void destroy() {
		if (dependentWidgets == null)
			return;
		for (OgemaWidget widget: dependentWidgets.getWidgets()) {
			try {
				unregisterWidget(widget);
//				widget.destroyWidget();
			} catch (Exception e) {
				// maybe widget has been destroyed already
			}
		}
	}
	
}
