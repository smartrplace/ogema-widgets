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
