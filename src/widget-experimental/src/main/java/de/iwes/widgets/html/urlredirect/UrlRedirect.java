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
	 * @param req
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
	 * @param req
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
