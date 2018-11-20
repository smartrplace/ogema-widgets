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
package de.iwes.widgets.resource.widget.init;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

/**
 * A {@link RedirectButton} that opens a new page with attached parameter
 * <code>?configId=&lt;SELECTED_RESOURCE&gt;</code>
 * where SELECTED_RESOURCE can be determined by a ResourceDropdown or some other
 * {@link ResourceSelector}, or can be set explicitly in the app. The constructor must be chosen
 * accordingly to the operating mode.
 * <br>
 * On the new page the parameter can be evaluated for instance by a {@link ResourceInitDropdown}.
 *
 * @see TemplateRedirectButton
 * @param <R>
 */
public class ResourceRedirectButton<R extends Resource> extends TemplateRedirectButton<R> {

	private static final long serialVersionUID = 1L;
	
	public ResourceRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl) {
		super(page, id, text, defaultUrl);
	}

	public ResourceRedirectButton(OgemaWidget parent, String id, String text, String defaultUrl, OgemaHttpRequest req) {
		super(parent, id, text, defaultUrl, req);
	}
	
	public ResourceRedirectButton(WidgetPage<?> page, String id, String text, ResourceSelector<R> selector) {
		super(page, id, text, selector);
	}
	
	public ResourceRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl, ResourceSelector<R> selector) {
		super(page, id, text, defaultUrl, selector, false);
	}
	
    public ResourceRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl,ResourceSelector<R> selector, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, selector, req);
    }
    
    @Override
    protected String getConfigId(R object) {
//    	return object.getPath("_");
    	return object.getLocation();
    }

}
