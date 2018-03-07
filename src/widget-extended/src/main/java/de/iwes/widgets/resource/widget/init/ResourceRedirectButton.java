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
 * <code>?configId=<SELECTED_RESOURCE></code>
 * where SELECTED_RESOURCE can be determined by a {@see ResourceDropdown} or some other
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
