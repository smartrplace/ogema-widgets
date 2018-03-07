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

package de.iwes.widgets.html.plot.api;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Facade class to be extended by (line) plot widgets
 * 
 * @author cnoelle
 * 
 */
public abstract class Plot2D<C extends Plot2DConfiguration, D extends Plot2DDataSet, T extends Plot2DOptions<C,D>> extends OgemaWidgetBase<T> {
	
	private static final long serialVersionUID = 1L;
	private C defaultConfiguration = null;
	protected abstract C createNewConfiguration();

	/******* Constructors *****/
	
	public Plot2D(WidgetPage<?> page, String id) { super(page, id); }
	public Plot2D(WidgetPage<?> page, String id, SendValue sendValueOnChange) {	 super(page, id, sendValueOnChange); }
    public Plot2D(WidgetPage<?> page, String id, boolean globalWidget) { super(page, id, globalWidget); }
    public Plot2D(WidgetPage<?> page, String id, boolean globalWidget, SendValue sendValueOnChange) { super(page, id, globalWidget, sendValueOnChange); }
    public Plot2D(OgemaWidget parent, String id, OgemaHttpRequest req) { super(parent, id, req); }
    public Plot2D(OgemaWidget parent, String id, SendValue sendValueOnChange, OgemaHttpRequest req) { super(parent, id, sendValueOnChange, req); }
    public Plot2D(WidgetPage<?> page, String id, OgemaHttpRequest req) { super(page, id, req); }
//    public Plot2D(WidgetPageI<?> page, String id, SendValue sendValueOnChange, OgemaHttpRequest req) { super(page, id, sendValueOnChange, req);}

	/****** Inherited methods ****/
    
    @SuppressWarnings("unchecked")
	@Override
    protected void setDefaultValues(T opt) {
    	super.setDefaultValues(opt);
    	if (defaultConfiguration != null) 
    		opt.configuration = (C) defaultConfiguration.clone();
    	else
    		opt.configuration = createNewConfiguration();
    }
    
    /****** Public methods ****/
    
    
    public synchronized C getDefaultConfiguration() {
    	if (defaultConfiguration == null)
    		defaultConfiguration = createNewConfiguration();
    	return defaultConfiguration;    	
    }
    
	public C getConfiguration(OgemaHttpRequest req) {
		return getData(req).getConfiguration();
	}
}
