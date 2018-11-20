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
