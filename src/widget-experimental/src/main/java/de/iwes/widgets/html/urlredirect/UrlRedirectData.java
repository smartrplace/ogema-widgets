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
package de.iwes.widgets.html.urlredirect;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class UrlRedirectData extends WidgetData {
	
	private String url;
	public final static String RELOAD_CURRENT_PAGE = "CURRENT_LOCATION";
	
	protected UrlRedirectData(UrlRedirect widget) {
		super(widget);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final JSONObject obj = new JSONObject();
		if (url != null && !url.isEmpty()) 
			obj.put("url", url);
		return obj;
	}
	
	protected void setUrl(String url) {
		this.url = url != null ? url.trim() : null;
	}
	
	protected String getUrl() {
		return url;
	}
	
	protected void setReloadCurrentPage() {
		this.url = RELOAD_CURRENT_PAGE;
	}

}
