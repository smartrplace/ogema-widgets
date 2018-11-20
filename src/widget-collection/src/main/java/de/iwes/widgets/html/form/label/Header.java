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
package de.iwes.widgets.html.form.label;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;


public class Header extends Label  {
    
	private int defaultType = 1;
    private static final long serialVersionUID = 7367326133405921539L;

    /*********** Constructors **********/
    
    public Header(WidgetPage<?> page, String id) {
    	super(page,id);
    }
    
    public Header(WidgetPage<?> page, String id, String defaultText) {
    	super(page,id,defaultText);
    }
    
    public Header(WidgetPage<?> page, String id, String text, OgemaHttpRequest req) {
    	super(page, id, text, req);
    }
    
    public Header(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, globalWidget);
    }
    
    public Header(WidgetPage<?> page, String id, String text,  boolean globalWidget) {
    	super(page, id, text, globalWidget);
    }
    
    public Header(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent,id,req);
    }

    /******* Inherited methods ******/
    

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Label.class;
    }

	@Override
	public HeaderData createNewSession() { 
		return new HeaderData(this);
	}
	
	@Override
	protected void setDefaultValues(LabelData opt) {
		HeaderData ho = (HeaderData) opt;
		ho.setHeaderType(defaultType);
		super.setDefaultValues(opt);
	}
	
	// this should not be required here
	/**
	 * Same as {@link #setText(String, OgemaHttpRequest)}
	 */
	@Override
	public void setHtml(String html, OgemaHttpRequest req) {
		setText(html, req);
	}

    /******* Public methods ******/

	public void setDefaultHeaderType(int type) {
    	if (type <= 0 || type > 6) throw new IllegalArgumentException("type argument must be a value between 1 and 6.");
		defaultType = type;
	}
	
	public int getDefaultHeaderType() {
		return defaultType;
	}
	
	public void setHeaderType(int type, OgemaHttpRequest req) {
	   	if (type <= 0 || type > 6) throw new IllegalArgumentException("type argument must be a value between 1 and 6.");
		((HeaderData) getData(req)).setHeaderType(type);
	}
	
	public int getHeaderType(OgemaHttpRequest req) {
		return ((HeaderData) getData(req)).getHeaderType();
	}
	
	
}
