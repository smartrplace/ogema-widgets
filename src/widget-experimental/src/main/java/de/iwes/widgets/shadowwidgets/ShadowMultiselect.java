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
package de.iwes.widgets.shadowwidgets;

import com.google.common.annotations.Beta;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;

@Beta
public class ShadowMultiselect<T> extends TemplateMultiselect<T> {
	
	private static final long serialVersionUID = 1L;
	protected final TemplateMultiselect<T> image;

	public ShadowMultiselect(WidgetPage<?> page, String id, TemplateMultiselect<T> image) {
		super(page, id);
		this.image = image;
		this.setTemplate(image.getTemplate());
		this.triggerAction(image, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		image.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	public ShadowMultiselect(OgemaWidget parent, String id, OgemaHttpRequest req, TemplateMultiselect<T> image) {
		super(parent, id, req);
		this.image = image;
		this.setTemplate(image.getTemplate());
		this.triggerAction(image, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
		image.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
	}
	
	@Override
	public void onPOSTComplete(String data, OgemaHttpRequest req) {
		image.update(this.getItems(req), req);
		image.selectMultipleItems(this.getSelectedItems(req), req);
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		this.update(image.getItems(req), req);
		this.selectMultipleItems(image.getSelectedItems(req), req);
	}
	
	public TemplateMultiselect<T> getImage() {
		return image;
	}

}
