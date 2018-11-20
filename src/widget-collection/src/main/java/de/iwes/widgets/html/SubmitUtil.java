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
