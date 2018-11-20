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
package de.iwes.widgets.html.alert;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class AlertData extends WidgetData {
	
	public static final WidgetStyle<Alert> BOOTSTRAP_DANGER= new WidgetStyle<>("alertBody",Arrays.asList("alert","alert-danger"),0);
	public static final WidgetStyle<Alert> BOOTSTRAP_SUCCESS = new WidgetStyle<>("alertBody",Arrays.asList("alert","alert-success"),0);
	public static final WidgetStyle<Alert> BOOTSTRAP_INFO = new WidgetStyle<>("alertBody",Arrays.asList("alert","alert-info"),0);
	public static final WidgetStyle<Alert> BOOTSTRAP_WARNING = new WidgetStyle<>("alertBody",Arrays.asList("alert","alert-warning"),0);
	
	// FIXME still required? Use SHOW_WIDGET, HIDE_WIDGET instead?
	public static final TriggeredAction SET_VISIBILITY_TRUE = new TriggeredAction("setVisibility",new Object[]{true});
	public static final TriggeredAction SET_VISIBILITY_FALSE = new TriggeredAction("setVisibility",new Object[]{false});

    String text = "";
    String textEscaped = "";
    private boolean allowDismiss = false;
    private long autoDismiss = -1;
    
	
	/*********** Constructor **********/
	
	public AlertData(Alert alert) {
		super(alert);
	}
	
	/******* Inherited methods ******/
	

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result;
        result = new JSONObject();
        result.put("text", textEscaped);
        result.put("allowDismiss", allowDismiss);
        result.put("autoDismiss", autoDismiss);
        return result;
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) { // should not really occur
        return new JSONObject(json);
    }
    
    @Override
    protected Collection<OgemaWidget> getSubWidgets() {	// this is a hack to set visibility to false after one GET when auto-dismiss is active
    													 	// this has to be after visibility has been evaluated in GET, therefore it cannot be done in retrieveGETData
    	if (autoDismiss > 0) {
    		setWidgetVisibility(false);
    	}
    	return super.getSubWidgets();
    }
	
	/********** Public methods **********/
	

    public void setText(String text) {
    	writeLock();
    	try {
	    	if (text == null) {
	    		this.text = "";
	    		this.textEscaped = "";
	    	}
	    	else {
	    		this.text = text;
	    		this.textEscaped = StringEscapeUtils.escapeHtml4(text);
	    	}
    	} finally {
    		writeUnlock();
    	}
    }
    
    /**
     * Never feed unvalidated user input into this method, as this enables XSS attacks. It is recommended to prefer 
     * {@link #setText(String)} where possible.
     * <br>
     * The passed String argument will be interpreted as Html.
     * @param html
     */
    protected void setHtml(final String html) {
    	writeLock();
    	try {
	    	if (html == null) {
	    		this.text = "";
	    		this.textEscaped = "";
	    	}
	    	else {
	    		this.text = html;
	    		this.textEscaped = html;
	    	}
    	} finally {
    		writeUnlock();
    	}
    }

    public String getText() {
        return text;
    }

    public void allowDismiss(boolean allowDismiss) {
        this.allowDismiss = allowDismiss;
    }

    public void autoDismiss(long duration) {
        autoDismiss = duration;
    }
 


}
