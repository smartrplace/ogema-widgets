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

package de.iwes.widgets.pattern.widget.init;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.pattern.PatternSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

/**
 * A {@link RedirectButton} that opens a new page with attached parameter
 * <code>?configId=<SELECTED_PATTERN</code>
 * where SELECTED_PATTERN is determined by a {@see PatternDropdown} or some other
 * {@link PatternSelector}, or can be set explicitly in the app. The constructor must be chosen
 * accordingly to the operating mode.
 * <br>
 * On the new page the parameter can be evaluated for instance by a {@link PatternInitDropdown}.
 *
 * @see TemplateRedirectButton
 * @param <P>
 */
public class PatternRedirectButton<P extends ResourcePattern<?>> extends TemplateRedirectButton<P> {

	private static final long serialVersionUID = 1L;
	
	public PatternRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl) {
		super(page, id, text, defaultUrl);
	}

	 public PatternRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl,OgemaHttpRequest req) {
	    	super(parent, id, text, destinationUrl, req);
	    }
	
	public PatternRedirectButton(WidgetPage<?> page, String id, String text, PatternSelector<P> selector) {
		super(page, id, text, selector);
	}
	
    public PatternRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl,PatternSelector<P> selector, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, selector, req);
    }
    
    @Override
    protected String getConfigId(P object) {
//    	return object.model.getPath("_");
    	return object.model.getPath();
    }

}
