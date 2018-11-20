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
