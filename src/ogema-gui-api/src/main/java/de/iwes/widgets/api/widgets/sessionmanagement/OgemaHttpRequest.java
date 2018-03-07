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

package de.iwes.widgets.api.widgets.sessionmanagement;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class OgemaHttpRequest {
	
	private static final AtomicInteger lastPageInstanceId = new AtomicInteger(0);
	
	private final HttpServletRequest req;
	private volatile OgemaLocale locale = null;
	private final String localeString;
	private String pageInstanceId;
	private boolean isPolling;
	
	public OgemaHttpRequest(HttpServletRequest req, boolean isPolling) {
		this.req = req;
		this.isPolling = isPolling;
		pageInstanceId = req.getParameter("pageInstance");
		if(pageInstanceId == null) {
			pageInstanceId = Integer.toString(lastPageInstanceId.incrementAndGet());
		}
		localeString = req.getParameter("locale"); // TODO standard element?
	}
	
	public HttpServletRequest getReq() {
		return req;
	}
	
	public String getPageInstanceId() {
		return pageInstanceId;
	}

	/**
	 * @param configId
	 * @deprecated ever used?
	 */
	@Deprecated
	public void changePageInstanceIdForNewURL(String configId) {
		pageInstanceId = configId;
	}
	
	public boolean isPolling() {
		return isPolling;
	}
	
	public String getSessionId() {
		return req.getSession().getId() + "_" + getPageInstanceId();
	}
	
	/**
	 * @param pageSpecificId
	 * @return
	 * @deprecated use {@link #getSessionId()} instead
	 */
	@Deprecated
	public String getSessionId(boolean pageSpecificId) {
		if (pageSpecificId) {
			return req.getSession().getId() + "_" + getPageInstanceId();
		} else {
			return req.getSession().getId();
		}
	}

	public boolean equalsSession(OgemaHttpRequest req2, boolean pageSpecificId) {
		if (!req2.getReq().getSession().getId().equals(this.getReq().getSession().getId())) return false;
		if (pageSpecificId) return true;
		if (!req2.getPageInstanceId().equals(this.getPageInstanceId())) return false;
		return true;
	}
	
	public String getLocaleString() {
		return localeString;
	}
	
	public OgemaLocale getLocale() { // initialized only when needed; not synchronized, but should be irrelevant
		if (locale == null) {
			locale = OgemaLocale.getLocale(localeString);
			if (locale == null)
				locale = OgemaLocale.ENGLISH;
		}
		return locale;
	}
	
	public final WrappedSession getWrapper() {
		return new WrappedSession(this);
	}
	
	/**
	 * For use as keys in a map 
	 */
	public final static class WrappedSession {
		
		private final String id;
		private final String pageInstance;
		private final int hash;
		
		private WrappedSession(OgemaHttpRequest req) {
			this.id = req.getReq().getSession().getId();
			this.pageInstance = req.getPageInstanceId();
			this.hash = hashInternal(id, pageInstance);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof WrappedSession) || this.hash != obj.hashCode())
				return false;
			final WrappedSession other = (WrappedSession) obj;
			return this.id.equals(other.id) && this.pageInstance.equals(other.pageInstance);
		}
		
		private static int hashInternal(String id,String instance) {
			return 23* id.hashCode() + 17*instance.hashCode();
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
		
		@Override
		public String toString() {
			return "Session " + id + "_" + pageInstance;
		}
		
	}
	
}
