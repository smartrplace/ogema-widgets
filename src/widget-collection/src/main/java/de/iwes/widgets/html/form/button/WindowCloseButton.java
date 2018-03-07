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

package de.iwes.widgets.html.form.button;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class WindowCloseButton extends RedirectButton  {
	
    private static final long serialVersionUID = 550713654103033621L;
    
    /************* constructor **********************/

    public WindowCloseButton(WidgetPage<?> page, String id) {
    	super(page, id, null, null);
    }
    
    public WindowCloseButton(WidgetPage<?> page, String id, String text) {
    	super(page, id, text, null, false);
    }
    
    public WindowCloseButton(WidgetPage<?> page, String id, String text, String destinationUrl) {
    	super(page, id, text, destinationUrl);
    }
    
    public WindowCloseButton(WidgetPage<?> page, String id, String text, OgemaHttpRequest req) {
    	super(page, id, text, null, req);
    }
    public WindowCloseButton(WidgetPage<?> page, String id, String text, String destinationUrl, OgemaHttpRequest req) {
    	super(page, id, text, destinationUrl, req);
    }
    
    public WindowCloseButton(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, null, null, globalWidget);
    }
    
    public WindowCloseButton(WidgetPage<?> page, String id, String text, boolean globalWidget) {
    	super(page, id, text, null, globalWidget);
    }
    
    public WindowCloseButton(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent, id, null, null, req);
    }
    
    /******* Inherited methods ******/
    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return WindowCloseButton.class;
    }
}
