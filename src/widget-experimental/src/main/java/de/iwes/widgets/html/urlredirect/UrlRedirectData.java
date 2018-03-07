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
