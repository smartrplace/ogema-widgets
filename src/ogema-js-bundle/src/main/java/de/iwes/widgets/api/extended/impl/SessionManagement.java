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
package de.iwes.widgets.api.extended.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.iwes.widgets.api.extended.PageRegistration;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

// TODO move sessions map to respective PageRegistration?
public class SessionManagement extends TimerTask {
	
	// Map<Session Id, Session object>
	//private final Cache<String, Session> sessions = CacheBuilder.newBuilder().softValues().build();
	private final Cache<String, Session> sessions = CacheBuilder.newBuilder().maximumSize(5).expireAfterAccess(60, TimeUnit.MINUTES).build();

	SessionManagement() {}
	
	void close() {
		for (Session session: sessions.asMap().values()) {
			session.close();
		}
	}
	
	void deleteAllSessions() {
		sessions.invalidateAll();
	}
	
	public final Session getSession(String sessionId) {
		synchronized (sessions) {
			return sessions.getIfPresent(sessionId);
		}
	}
	
	void setExpiryTime(String sessionId, long expiryTime) {
		Session session = sessions.getIfPresent(sessionId);
		if (session != null)
			session.setExpiryTime(expiryTime);
	}
	
	int getNumberOfSessions(WidgetPage<?> page) {
		int cnt = 0;
		for (Session session: sessions.asMap().values()) {
			if (session.getPage().getPage().equals(page))
				cnt++;
		}
		return cnt;
	}
	
	int getNumberOfSessions(WidgetApp app) {
		int cnt = 0;
		for (Session session: sessions.asMap().values()) {
			if (session.getPage().getPage().getWidgetApp().equals(app))
				cnt++;
		}
		return cnt;
	}
	
//	@SuppressWarnings("unchecked")
//	public <T extends WidgetData> WidgetSessionData<T> getSessionManagement(OgemaWidgetBase<T> widget, String boundPagePath) {
//		String id = getId(boundPagePath, widget.getId());	
//		options.putIfAbsent(id, new WidgetSessionData<T>(widget));
//		return (WidgetSessionData<T>) options.get(id);
//	}
	
	public void removeWidget(OgemaWidget widget) {
		String widgetId = widget.getId();
		WidgetPage<?> page = widget.getPage();
//		String boundPagePath = ((WidgetPageBase<?>) page).getServletBase(); 
		// System.out.println("      session mgt removing widget " + widgetId);
//		String id = getId(boundPagePath,widgetId);
//		WidgetSessionData<? extends WidgetData> opt = options.remove(id);

		for (Session session : sessions.asMap().values()) {
			if (!session.getPage().getPage().equals(page))
				continue;
			session.removeWidget(widgetId);
		}
		
	}
	
	void removePage(WidgetPage<?> page) {
		for (Map.Entry<String, Session> entry : sessions.asMap().entrySet()) {
			if (entry.getValue().getPage().getPage().equals(page)) 
				sessions.invalidate(entry.getKey());
		}
	}
	
	void createNewSession(String sessionId, PageRegistration page, Map<String,String[]> params) {
		synchronized (sessions) {
	 		if (sessions.getIfPresent(sessionId) != null)
				throw new IllegalArgumentException("Session already exists");
			Session session = new Session(page, params);
			sessions.put(sessionId, session);
		}
	}
	
//	private void deleteSession(String sessionId) {
//		sessions.invalidate(sessionId);
//		
////		Iterator<WidgetSessionData<? extends WidgetData>> it = options.values().iterator();
////		while (it.hasNext()) {
////			WidgetSessionData<? extends WidgetData> data = it.next();
////			WidgetData opt = data.sessions.get(sessionId);
////			if (opt != null) {
////				opt.destroy();  // in particular removes subwidgets
////				data.sessions.remove(sessionId);  
////			}			
////		}
////		ExecutorService es = executors.remove(sessionId);
////		if (es != null) {
////			try {
////				es.shutdown();
////				if (!es.awaitTermination(2, TimeUnit.SECONDS)) {
////					 es.shutdownNow();
////				}
////			} catch (Exception e) {/* ignore */}
////		}
////		initializedSessions.remove(sessionId);
////		expiryTimes.remove(sessionId);
//////		maxNrSessions.remove(sessionId);
////		pageParameters.remove(sessionId);
//		
//		
//	}
	
	/****************************************************************/
	
	//  XXX move to page registration
	// Map<boundPagePath + "__" + widgetId, WidgetSessionData>
//	private final ConcurrentMap<String,WidgetSessionData<? extends WidgetData>> options = new ConcurrentHashMap<String, WidgetSessionData<? extends WidgetData>>();
	// Map<sessionId, lastUpdateTime>
//	private /* final */ ConcurrentMap<String,Long> initializedSessions;
//	// Map<sessionId, session expiry time>  // does not need to contain values for all cases, in which case default values are used  // XXX not used(?); shouldn't it be time per page?
//	private final ConcurrentMap<String,Long> expiryTimes = new ConcurrentHashMap<String, Long>();
//	// Map<sessionId, Map<boundPagePath, Map of page parameters (?key1=value1&key2=value2)>>
//	private final ConcurrentMap<String, ConcurrentMap<String,Map<String,String[]>>> pageParameters = new ConcurrentHashMap<String, ConcurrentMap<String,Map<String,String[]>>>();
//	// MAp<sessionId, exec>
//	private final ConcurrentMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
	// Map<pagePath, PageRegistration>

//	protected SessionManagement(ConcurrentMap<String, PageRegistration> pageRegistrations) {
//		this.initializedSessions = initializedSessions;
//		this.pageRegistrations = pageRegistrations;
//	}

//	void closeBak() {
//		options.clear();
//		expiryTimes.clear();
////		maxNrSessions.clear();
//		pageParameters.clear();
//		List<ExecutorService> execs =  new ArrayList<>(executors.values());
//		executors.clear();
//		Iterator<ExecutorService> it = execs.iterator();
//		while (it.hasNext()) {
//			ExecutorService exec = it.next();
//			exec.shutdown();
//		}
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		for (ExecutorService es: execs) {
//			if (es.isTerminated())
//				continue;
//			es.shutdownNow();
//		}
// 	}
	
	
//	@SuppressWarnings("unchecked")
//	public <T extends WidgetData> WidgetSessionData<T> getSessionManagementBak(OgemaWidgetBase<T> widget, String boundPagePath) {
//		String id = getId(boundPagePath, widget.getId());	
//		options.putIfAbsent(id, new WidgetSessionData<T>(widget));
//		return (WidgetSessionData<T>) options.get(id);
//	}
	
	@Override
	public void run() {
		long currentTime = System.currentTimeMillis();
		Set<String> outdatedSessions = new HashSet<String>();
		try {
			Iterator<Entry<String,Session>> sessionsIt = sessions.asMap().entrySet().iterator();
			while (sessionsIt.hasNext()) {
				Entry<String,Session> entry = sessionsIt.next();
				Session session = entry.getValue();
				long lastUpdate = session.getLastInteractionTime();
				long expiryTime = session.getExpiryTime();
				if (currentTime-lastUpdate > expiryTime) 
					outdatedSessions.add(entry.getKey());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Iterator<String> delIt = outdatedSessions.iterator();
		while (delIt.hasNext()) {
			sessions.invalidate(delIt.next());
		}
		// TODO treat too many sessions
	}

//	void setMaxNrSessions(String sessionId, int maxSessions) {
//		maxNrSessions.put(sessionId, maxSessions);
//	}
	
//	void removeWidgetBak(String widgetId, String boundPagePath) {
//		// System.out.println("      session mgt removing widget " + widgetId);
//		String id = getId(boundPagePath,widgetId);
//		WidgetSessionData<? extends WidgetData> opt = options.get(id);
//		if (opt == null) return;
//		Iterator<? extends WidgetData> it = opt.sessions.values().iterator();
//		while (it.hasNext()) {
//			WidgetData session = it.next();
//			session.destroy();  // in particular removes subwidgets
//		}
//		opt.sessions.clear();
//		options.remove(id);
//	}
	
//	private void deleteSession(String sessionId) {
//		Iterator<WidgetSessionData<? extends WidgetData>> it = options.values().iterator();
//		while (it.hasNext()) {
//			WidgetSessionData<? extends WidgetData> data = it.next();
//			WidgetData opt = data.sessions.get(sessionId);
//			if (opt != null) {
//				opt.destroy();  // in particular removes subwidgets
//				data.sessions.remove(sessionId);  
//			}			
//		}
//		ExecutorService es = executors.remove(sessionId);
//		if (es != null) {
//			try {
//				es.shutdown();
//				if (!es.awaitTermination(2, TimeUnit.SECONDS)) {
//					 es.shutdownNow();
//				}
//			} catch (Exception e) {/* ignore */}
//		}
//		for (PageRegistration pr : pageRegistrations.values()) {
//			pr.removeSession(sessionId);
//		}
//		initializedSessions.remove(sessionId);
//		expiryTimes.remove(sessionId);
////		maxNrSessions.remove(sessionId);
//		pageParameters.remove(sessionId);
//		
//		
//	}
	
	
//	/** Session management for a widget */
//	public class WidgetSessionData<T extends WidgetData> {
//		
////		ConcurrentMap<String,T> sessions = new ConcurrentHashMap<String, T>();
//		private final OgemaWidgetBase<T> widget;
//		
//		private WidgetSessionData (OgemaWidgetBase<T> widget) {
//			this.widget =widget;
//		}
//		
////		public T getSessionData(String sessionId) {
//////			System.out.println("  Obtaining new session mgt object " + sessionId +", session exists: " + initializedSessions.containsKey(sessionId));
////			if (sessionId == null || sessionId.isEmpty() || !initializedSessions.keySet().contains(sessionId)) {
////				return null;  // last option: session expired!
////			}
////			sessions.putIfAbsent(sessionId, widget.createNewSession());
////			return sessions.get(sessionId);
////		}
//		
//		// TODO cache sessions?; this will be called very often
//		public T getSessionData(String sessionId) {
//			Session session = sessions.getIfPresent(sessionId);
//			// FIXME
//			System.out.println("           session: " + sessionId + ", found: " + (session != null) + "; available: " + sessions.asMap());
//			if (session == null)
//				return null;
//			return session.getWidgetData(widget);
//		}
//		
////		public void setLastInteractionTime(String sessionId) {
////			if (sessionId == null || sessionId.isEmpty() || !initializedSessions.keySet().contains(sessionId)) return;
////			initializedSessions.put(sessionId, System.currentTimeMillis());
////			// 	System.out.println("  initialised sessions " + initializedSessions);
////		}
//		
//		public void setLastInteractionTime(String sessionId) {
//			Session session = sessions.getIfPresent(sessionId);
//			if (session == null)
//				return;
//			session.setLastInteractionTime(System.currentTimeMillis());
//		}
//		
//	}
	
	/**
	 * @param boundPagePath
	 * @param sessionId
	 * @return
	 * 		null, if session does not exist, an unmodifiable map of page parameters otherwise.
	 */
	public Map<String,String[]> getPageParameters(String boundPagePath, String sessionId) {
		if (sessionId == null || sessionId.isEmpty()) {
			return null;  
		}
		Session session = sessions.getIfPresent(sessionId);
		if (session == null)
			return null;
		return session.getPageParameters();
	}
	
//	private static final String getId(String boundPagePath,String widgetId) {
//		return boundPagePath + "__" + widgetId;
//	}
	
	public ExecutorService getExecutor(String sessionId) {
		if (sessionId == null || sessionId.isEmpty()) {
			return null;  
		}
		Session session = sessions.getIfPresent(sessionId);
		if (session == null)
			return null;
		return session.getExecutor();
	}
	
	
	
}
