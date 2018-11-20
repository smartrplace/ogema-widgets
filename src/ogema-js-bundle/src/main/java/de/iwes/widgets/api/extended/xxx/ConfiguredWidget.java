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
package de.iwes.widgets.api.extended.xxx;

import java.util.Iterator;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.PageRegistration;
import de.iwes.widgets.api.extended.PageRegistrationI;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.impl.Session;
import de.iwes.widgets.api.extended.impl.SessionManagement;
import de.iwes.widgets.api.extended.impl.WidgetSessionManagement;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.start.JsBundleApp;

/**
 *
 * @author esternberg
 */
public class ConfiguredWidget<T extends WidgetData> implements WidgetSessionManagement<T> {
    
    public final OgemaWidgetBase<T> widget;

    private final String systemResourcePath;
    private final String webResourcePath;
    // if null this is valid for all sessions
    private final String sessionId;
    private final SessionManagement sessions;
    private final PageRegistrationI page;
//    private boolean isInitWidget = false;
    
    public ConfiguredWidget(OgemaWidgetBase<T> widget2, String systemResourcePath, String webResourcePath, PageRegistrationI page){
    	this(widget2, systemResourcePath, webResourcePath, null, page);
    }
    
    public ConfiguredWidget(OgemaWidgetBase<T> widget2, String systemResourcePath, String webResourcePath, String sessionId, PageRegistrationI page){
        this.widget = widget2;
        this.systemResourcePath = systemResourcePath;
        this.webResourcePath = webResourcePath;
        this.sessionId = sessionId;
        this.sessions = (page != null) ? page.sessionManagement : null;
        this.page = page;
    } 
    
    public OgemaWidgetBase<?> getWidget() {
        return widget;
    }

    public String getSystemResourcePath() {
        return systemResourcePath;
    }

    public String getWebResourcePath() {
        return webResourcePath;
    }

//	public boolean isInitWidget() {
//		return isInitWidget;
//	}
//
//	public void setInitWidget(boolean isInitWidget) {
//		this.isInitWidget = isInitWidget;
//	}
    
    public String getSessionId() {
    	return sessionId;
    } 
    
    @Override
    public boolean equals(Object obj) {							// equality iff widgetIds coincide
    	if (!(obj instanceof ConfiguredWidget)) return false;
    	ConfiguredWidget<?> cw2 = (ConfiguredWidget<?>) obj;
    	return cw2.widget.equals(this.widget);  
    }
    
    @Override
    public int hashCode() {
    	return widget.hashCode();
    }
    
    @Override
    public String toString() {
    	return widget.toString();
    }
    
    @Override
    public T getSessionData(OgemaHttpRequest req) {
    	if (sessions == null) 
    		return null;
    	String sessionId = req.getSessionId();
		Session session = sessions.getSession(sessionId);
		if (session == null) 
			return null;
		return session.getWidgetData(widget);
	}

	@Override
	public void setLastInteractionTime(OgemaHttpRequest req) {
		if (sessions == null)
			return;
		Session session = sessions.getSession(req.getSessionId());
		if (session == null)
			return;
		session.setLastInteractionTime(System.currentTimeMillis());
	}
	
	@Override
	/*
	 * handle POST requests triggered in some other widget's onPrePOST method
	 * @param request
	 * @param req
	 */
	public void handleTriggeredPOSTs(JSONObject request, OgemaHttpRequest req) {
		Iterator<String> it = request.keys();
		while(it.hasNext()) {
			String id = it.next();
			if (id.equals("data")) continue; // this is the data of the original triggering widget
			ConfiguredWidget<?> other = page.getConfiguredWidget(id, req.getSessionId());
			OgemaWidgetBase<?> widget = (other != null ? other.widget : null);
			if (widget == null) {
				LoggerFactory.getLogger(JsBundleApp.class).warn("Widget for update not found: " + id);
				continue;
			}
			try {
//				String data = request.getString(id);
				JSONObject dataObj = request.getJSONObject(id);
				String data = dataObj.toString();
				widget.onPrePOST(data, req);
				// TODO append results?
				widget.getData(req).onPOST(data, req);
				widget.onPOSTComplete(data, req);
			} catch (UnsupportedOperationException e) { // if POST is not supported
				continue;
			} catch (Exception e) {
				LoggerFactory.getLogger(JsBundleApp.class).error("Error executing triggered POST: {}",widget, e);
			}
		}
		
	}
    
}
