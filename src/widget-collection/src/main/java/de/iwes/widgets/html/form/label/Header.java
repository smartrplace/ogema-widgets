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
