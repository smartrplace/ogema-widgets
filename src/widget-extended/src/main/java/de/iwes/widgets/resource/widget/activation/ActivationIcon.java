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
package de.iwes.widgets.resource.widget.activation;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconData;
import de.iwes.widgets.html.icon.IconType;

/**
 * A global widget that displays an icon and a toggle button
 * to represent the activation state of a resource
 *
 */
@SuppressWarnings("serial")
public class ActivationIcon extends Flexbox {

	private final Icon icon;
	private final Button button;
	private Resource defaultResource;
	
	public ActivationIcon(WidgetPage<?> page, String id, boolean activateRecursively) {
		super(page, id, true);
		this.icon = new Icon(page, id + "_icon") {
			
			@Override
			public IconData createNewSession() {
				return new ActivationIconData(this);
			}
			
			@Override
			protected void setDefaultValues(IconData opt) {
				super.setDefaultValues(opt);
				((ActivationIconData) opt).setResource(defaultResource);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final Resource r = ((ActivationIconData) getData(req)).getResource();
				if (r == null) {
					setIconType(null, req);
					return;
				}
				setIconType(r.isActive() ? IconType.CHECK_MARK : IconType.CLOSE, req);
			}
			
		};
		icon.setDefaultWidth("2em");
		icon.setDefaultMargin("0.5em", false, false, false, true);
		this.button = new Button(page, id+ "_btn") {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final Resource r = getResource(req);
				if (r == null) {
					disable(req);
					setWidgetVisibility(false, req);
					return;
				}
				enable(req);
				setWidgetVisibility(true, req);
				setText(r.isActive() ? "Deactivate" : "Activate", req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final Resource r = getResource(req);
				if (r == null)
					return;
				if (r.isActive())
					r.deactivate(activateRecursively);
				else
					r.activate(activateRecursively);
			}
			
		};
		this.setDefaultJustifyContent(JustifyContent.FLEX_LEFT);
		this.addItem(icon, null);
		this.addItem(button, null);
		this.button.triggerAction(icon, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		this.button.triggerAction(button, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(icon, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(button, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public Button getButton() {
		return button;
	}
	
	public void setDefaultResource(Resource resource) {
		this.defaultResource = resource;
	}
	
	public void setResource(Resource resource, OgemaHttpRequest req) {
		((ActivationIconData) icon.getData(req)).setResource(resource);
	}
	
	public Resource getResource(OgemaHttpRequest req) {
		return ((ActivationIconData) icon.getData(req)).getResource();
	}
	
	public boolean isActive(OgemaHttpRequest req) {
		final Resource r = getResource(req);
		return r == null ? false: r.isActive();
	}
	
}
