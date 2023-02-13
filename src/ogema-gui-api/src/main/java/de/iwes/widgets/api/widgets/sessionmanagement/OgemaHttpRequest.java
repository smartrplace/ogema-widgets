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
package de.iwes.widgets.api.widgets.sessionmanagement;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class OgemaHttpRequest {
	
	private static final String WIDGET_SESSION_ID = "org.ogema.widgets.session";
	private static final AtomicInteger lastPageInstanceId = new AtomicInteger(0);
	private static final SecureRandom rand = new SecureRandom();
	
	private final HttpServletRequest req;
	private volatile OgemaLocale locale = null;
	private final String localeString;
	private final String pageInstanceId;
	private boolean isPolling;
	private final String sessionId; 
	
	public OgemaHttpRequest(HttpServletRequest req, boolean isPolling) {
		this(req, isPolling, req.getParameter("locale"));
	}
	public OgemaHttpRequest(HttpServletRequest req, boolean isPolling, String localeString) {
		this.req = req;
		this.isPolling = isPolling;
		final String pageInstanceId0 = req.getParameter("pageInstance");
		this.pageInstanceId = pageInstanceId0 == null ? Integer.toString(lastPageInstanceId.incrementAndGet()) : pageInstanceId0;
		this.localeString = localeString; //req.getParameter("locale"); // TODO standard element?
		this.sessionId = getSessionId(req.getSession()) + "_" + pageInstanceId;
	}
	
	public HttpServletRequest getReq() {
		return req;
	}
	
	public String getPageInstanceId() {
		return pageInstanceId;
	}

	/**
	 * @param configId
	 * @deprecated ever used? If so, implement using reflections to overcome final modifiers
	 */
	@Deprecated
	public void changePageInstanceIdForNewURL(String configId) {
//		pageInstanceId = configId;
	}
	
	public boolean isPolling() {
		return isPolling;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * @param pageSpecificId
	 * @return
	 * @deprecated use {@link #getSessionId()} instead
	 */
	@Deprecated
	public String getSessionId(boolean pageSpecificId) {
		if (pageSpecificId) {
			return sessionId;
		} else {
			final int lastIdx = sessionId.lastIndexOf('_');
			return sessionId.substring(0, lastIdx);
		}
	}

	@Deprecated
	public boolean equalsSession(OgemaHttpRequest req2, boolean pageSpecificId) {
		return getSessionId(pageSpecificId).equals(req2.getSessionId(pageSpecificId));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof OgemaHttpRequest))
			return false;
		return Objects.equals(this.sessionId, ((OgemaHttpRequest) obj).sessionId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(sessionId);
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
	
	/**
	 * @deprecated use {@link OgemaHttpRequest} directly 
	 */
	@Deprecated
	public final WrappedSession getWrapper() {
		return new WrappedSession(this);
	}
	
	/**
	 * Generate a random alphanumeric (plus the character '_') string 
	 * @return
	 */
	private static String nextRandomString() {
		final byte[] bytes = new byte[16];
		rand.nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes).replace('+', '_').replace('=', '_').replace('/', '_');
	}
	
	private static String getSessionId(final HttpSession session) {
		Object session0 = session.getAttribute(WIDGET_SESSION_ID);
		if (!(session0 instanceof String)) {
			final String session1 = nextRandomString();
			synchronized (OgemaHttpRequest.class) {
				session0 = session.getAttribute(WIDGET_SESSION_ID);
				if (!(session0 instanceof String)) {
					session0 = session1;
					session.setAttribute(WIDGET_SESSION_ID, session0);
				}
			}
		}
		return (String) session0;
	}
	
	/**
	 * For use as keys in a map
	 * @deprecated use {@link OgemaHttpRequest} directly 
	 */
	@Deprecated
	public final static class WrappedSession {
		
		private final String id;
		private final int hash;
		
		private WrappedSession(OgemaHttpRequest req) {
			this.id = req.getPageInstanceId();
			this.hash = Objects.hash(id);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof WrappedSession) || this.hash != obj.hashCode())
				return false;
			final WrappedSession other = (WrappedSession) obj;
			return Objects.equals(this.id, other.id);
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
		
		@Override
		public String toString() {
			return "Session " + id;
		}
		
	}
	
}
