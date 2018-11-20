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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class UrlRedirect extends OgemaWidgetBase<UrlRedirectData> {

	private static final long serialVersionUID = 1L;
	private String defaultUrl;

	public UrlRedirect(WidgetPage<?> page, String id) {
		super(page, id);
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return UrlRedirect.class;
	}

	@Override
	public UrlRedirectData createNewSession() {
		return new UrlRedirectData(this);
	}
	
	@Override
	protected void setDefaultValues(UrlRedirectData opt) {
		super.setDefaultValues(opt);
		opt.setUrl(defaultUrl);
	}

	/**
	 * Set the redirect url
	 * @param url
	 * 		null to disable
	 * @param req
	 */
	public void setUrl(String url, OgemaHttpRequest req) {
		getData(req).setUrl(url);
	}
	
	/**
	 * Set the redirect url
	 * @param url
	 */
	public void setDefaultUrl(String url) {
		this.defaultUrl = url;
	}
	
	/**
	 * Instead of specifying a URL to redirect to, use the current page as redirect target.
	 * Overwrites values set by {@link #setUrl(String, OgemaHttpRequest)}.
	 * @param req
	 */
	public void setReloadCurrentPage(OgemaHttpRequest req) {
		getData(req).setReloadCurrentPage();
	}
	
	/**
	 * Instead of specifying a URL to redirect to, use the current page as redirect target.
	 * Overwrites values set by {@link #setUrl(String, OgemaHttpRequest)}.
	 */
	public void setDefaultReloadCurrentPage() {
		this.defaultUrl = UrlRedirectData.RELOAD_CURRENT_PAGE;
	}
	
	/**
	 * @param req
	 * @return
	 * 		null, if url is not set, the target url otherwise
	 */
	protected String getUrl(OgemaHttpRequest req) {
		return getData(req).getUrl();
	}

}
