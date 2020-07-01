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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** Text field with background color for alert messages */
public class Alert extends OgemaWidgetBase<AlertData> {

	private static final long serialVersionUID = 1L;
	private String defaultText;
	private boolean defaultTextAsHtml;
	private boolean defaultAllowDismiss = false;
	
	/*********** Constructor **********/
	
    public Alert(WidgetPage<?> page, String id, String text) {
        super(page, id);
        this.defaultText = text;
    }
    
    public Alert(WidgetPage<?> page, String id, boolean globalWidget, String text) {
        super(page, id, globalWidget);
        this.defaultText = text;
    }
    
    /******* Inherited methods *****/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Alert.class;
    }

	@Override
	public AlertData createNewSession() {
		return new AlertData(this);
	}
	
	@Override
	protected void setDefaultValues(AlertData opt) {
		if (defaultTextAsHtml)
			opt.setHtml(defaultText);
		else
			opt.setText(defaultText);
		opt.allowDismiss(defaultAllowDismiss);
		super.setDefaultValues(opt);
	}

    /******* Public methods ********/

	/**
	 * Parse the default text as Html?
	 * @param html
	 */
	public void setDefaultTextAsHtml(boolean html) {
		this.defaultTextAsHtml = html;
	}

    public void setDefaultText(String text) {
    	this.defaultText = text;
    }

    	
    public void setText(String text,OgemaHttpRequest req) {
    	getData(req).setText(text);
    }
    
    /**
     * Never feed unvalidated user input into this method, as this enables XSS attacks. It is recommended to prefer 
     * {@link #setText(String, OgemaHttpRequest)} where possible.
     * <br>
     * The passed String argument will be interpreted as Html.
     * @param html
     * @param req
     */    
    public void setHtml(final String html,OgemaHttpRequest req) {
    	getData(req).setHtml(html);
    }

    public String getText(OgemaHttpRequest req) {
        return getData(req).getText();
    }

    /**
     * Shall the user be able to close the alert? Default is false.
     * @param allowDismiss
     */
    public void setDefaultAllowDismiss(boolean allowDismiss) {
    	this.defaultAllowDismiss = allowDismiss;
    }

    public void allowDismiss(boolean allowDismiss,OgemaHttpRequest req) {
    	getData(req).allowDismiss(allowDismiss);
    }

    public void autoDismiss(long duration,OgemaHttpRequest req) {
    	getData(req).autoDismiss(duration);
    }
    
    /**
     * A convenience method using some sensible default settings to display messages;
     * @param success
     * 		true: alert background color green; false: background color red 		
     * @param message
     */
    public void showAlert(String message, boolean success, OgemaHttpRequest req) {
    	setWidgetVisibility(true, req);
		autoDismiss(6000, req);
		allowDismiss(true, req);
		setText(message, req);
		setStyle(success ? AlertData.BOOTSTRAP_SUCCESS : AlertData.BOOTSTRAP_DANGER, req);
    }
    /**
     * A convenience method using some sensible default settings to display messages;
     * @param success
     * 		true: alert background color green; false: background color red
     * @param showDuration interval duration for which the alert shall be shown. If not provided
     * 		the default value is 6000 milliseconds	
     * @param message
     */
   public void showAlert(String message, boolean success, long showDuration, OgemaHttpRequest req) {
    	setWidgetVisibility(true, req);
		autoDismiss(showDuration, req);
		allowDismiss(true, req);
		setText(message, req);
		setStyle(success ? AlertData.BOOTSTRAP_SUCCESS : AlertData.BOOTSTRAP_DANGER, req);
    }

}
