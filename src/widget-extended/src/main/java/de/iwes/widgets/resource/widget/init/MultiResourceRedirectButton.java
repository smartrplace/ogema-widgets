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
