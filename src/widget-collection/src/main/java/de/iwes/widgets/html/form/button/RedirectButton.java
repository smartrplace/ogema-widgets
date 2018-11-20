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

import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A redirect button. When the user clicks the button, a new window will be opened,
 * whose url can be set via {@link #setUrl(String, OgemaHttpRequest)} (per session),
 * or {@link #setDefaultUrl(String)} (for all new sessions).
 * <br>
 * It is possible to set additional parameters as key-value pairs, which will be appended to the url
 * in the form
 * <code>?key1=value1&amp;key2=value2a,value2b&amp;key3=...</code>
 * The use of parameters is particularly useful if the base url does not change and is
 * the same for all users, but the parameters depend on some user selection. Otherwise
 * the parameters can be integrated into the URL string as well.  
 */
public class RedirectButton extends Button {  

    private static final long serialVersionUID = 5507136541033621L;
    private String defaultUrl = "#";
    private Map<String,String[]> defaultParameters = null;
    private boolean defaultOpenInNewTab = true;
    
    /************* constructor **********************/
    
    public RedirectButton(WidgetPage<?> page, String id, String text) {
    	this(page, id, text, null);
    }
    
    public RedirectButton(WidgetPage<?> page, String id, String text, String destinationUrl) {
    	this(page, id, text,destinationUrl, false);
    }
    
    public RedirectButton(WidgetPage<?> page, String id, String text, String destinationUrl, OgemaHttpRequest req) {
    	super(page, id, text, req);
    	if (destinationUrl != null)
    		this.defaultUrl = destinationUrl;
    }

    public RedirectButton(WidgetPage<?> page, String id, String text, String destinationUrl, boolean globalWidget) {
    	super(page, id, text, globalWidget);
    	if (destinationUrl != null)
    		this.defaultUrl = destinationUrl;
    	this.defaultUrl = destinationUrl;
    }

//    public RedirectButton(WidgetPageI<?> page, String id, String text, String destinationUrl, OgemaWidget<?> parameterSetter) {
//    	this(page, id, text, destinationUrl);
//       	triggerAction(parameterSetter, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST,null); // XXX why? 
//    }
    
    public RedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, OgemaHttpRequest req) {
    	super(parent, id, req);
    	if (destinationUrl != null)
    		this.defaultUrl = destinationUrl;
    	setDefaultText(text);
    }
    
    @Override
    protected void registerJsDependencies() {
    	Class<? extends OgemaWidgetBase<?>> clazz = getWidgetClass();
    	String className = clazz.getSimpleName();
    	String guessUrl = "/ogema/widget/button/" + className + ".js";
    	this.registerLibrary(true, className, guessUrl);
    }
    
    /********** methods to be overridden *************/
	
    // new method below - not to be overwritten
//    public Map<String, String> getParameters(OgemaHttpRequest req) {
//    	return new HashMap<String, String>();
//    }
//    
//    public String getConfigId(OgemaHttpRequest req) {
//    	return null;
//    }
    
    /********** options *************/
    
    @Override
	public RedirectButtonData createNewSession() {
    	return new RedirectButtonData(this);
    }
    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return RedirectButton.class;
    }
    
    @Override
	public RedirectButtonData getData(OgemaHttpRequest req) {
    	return (RedirectButtonData) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(ButtonData opt) {
    	super.setDefaultValues(opt);
    	RedirectButtonData ropt = (RedirectButtonData) opt;
    	ropt.setUrl(defaultUrl);
    	ropt.setParameters(defaultParameters);
    	ropt.setOpenInNewTab(defaultOpenInNewTab);
    }
    
    /********** Public methods *************/
    
    /**
     * Set the default target URL for the redirect button.
     * @param url
     * 		The URL. Pass null or an empty string to disable redirection.
     */
    public void setDefaultUrl(String url) {
    	this.defaultUrl = url;
    }
    
    public String getUrl(String url, OgemaHttpRequest req) {
    	return getData(req).getUrl();
    }
    
    /**
     * Set the target URL for the redirect button.
     * @param url
     * 		The URL. Pass null or an empty string to disable redirection.
     * @param req
     */
    public void setUrl(String url, OgemaHttpRequest req) {
    	getData(req).setUrl(url);
    }
    
	public void setDefaultParameters(Map<String,String[]> parameters) {
		this.defaultParameters = parameters;
	}
    
	public void addParameter(String key, String value, OgemaHttpRequest req) {
		getData(req).addParameter(key, value);
	}
	
	public void setParameters(Map<String,String[]> parameters, OgemaHttpRequest req) {
		getData(req).setParameters(parameters);
	}
	
	public boolean removeParameter(String key, OgemaHttpRequest req) {
		return getData(req).removeParameter(key);
	}
	
	public Map<String,String[]> getParameters( OgemaHttpRequest req) {
		return getData(req).getParameters();
	}
	
	public boolean isOpenInNewTab(OgemaHttpRequest req) {
		return getData(req).isOpenInNewTab();
	}

	public void setOpenInNewTab(boolean openInNewTab, OgemaHttpRequest req) {
		getData(req).setOpenInNewTab(openInNewTab);
	}
	public void setDefaultOpenInNewTab(boolean openInNewTab) {
		defaultOpenInNewTab = openInNewTab;
	}

}