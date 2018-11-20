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

import de.iwes.widgets.api.extended.resource.ResourceMultiSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.TemplateMultiRedirectButton;

/**
 * A redirect button that sets the page parameters according to a set of selected resources.
 * On the new page the parameter can be evaluated for instance by a {@link ResourceInitMultiselect} widget.
 * 
 * @see TemplateMultiRedirectButton
 * @see ResourceRedirectButton 
 * @param <R>
 */
public class MultiResourceRedirectButton<R extends Resource> extends TemplateMultiRedirectButton<R> {

	private static final long serialVersionUID = 1L;
	
	public MultiResourceRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl) {
		super(page, id, text, defaultUrl);
	}
	
	public MultiResourceRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, OgemaHttpRequest req) {
		super(parent, id, text, destinationUrl, req);
	}

	public MultiResourceRedirectButton(WidgetPage<?> page, String id, String text, ResourceMultiSelector<R> selector) {
		super(page, id, text, selector);
	}
	
    public MultiResourceRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, ResourceMultiSelector<R> selector, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, selector, req);
    }
    
    @Override
    protected String getConfigId(R object) {
    	return object.getPath();
    }

}
