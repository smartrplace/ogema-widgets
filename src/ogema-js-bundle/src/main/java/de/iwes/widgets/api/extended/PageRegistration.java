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
package de.iwes.widgets.api.extended;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ogema.accesscontrol.AccessManager;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.impl.Session;
import de.iwes.widgets.api.extended.impl.SessionManagement;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Stores references to page-global widgets, whereas all session-specific widgets are only referenced
 * in the respective Session object.
 */
// FIXME move to implementation package?
public class PageRegistration extends PageRegistrationI {

	private static final long serialVersionUID = 1L;
//	ConcurrentSkipListMap<K, V>
//	public final ConcurrentMap<String, ConfiguredWidget<?>> pageWidgets = new ConcurrentHashMap<>();
	private final Object initWidgetsLock = new Object();
	// needed for resorting after initial creation of pageWidgets
	private final Object widgetsLock = new Object(); 
	// only contains globally existing widgets, not session widgets; FIXME why isn't this a map? Makes iteration slow!
	volatile List<ConfiguredWidget<?>> pageWidgets = null; // will be replaced with a CopyOnWriteArrayList, once the page is initialized
	private List<ConfiguredWidget<?>> initialWidgets = new ArrayList<>(); // will be set to null once the page is initialized
	// Map<widgetId, WidgetSessionData>

	
	// Map<SessionId, Session widgets>  -> get from sessionManagement instead
//	private final ConcurrentMap<String,ConcurrentMap<String,ConfiguredWidget<?>>> sessionWidgets = new ConcurrentHashMap<>(); 
	
	// Map<groupdId, group>
	private final Map<String,WidgetGroupDerived> groups = new ConcurrentHashMap<>();

//	private final boolean pageSpecificId;
	
	/**
	 * Actually an implementation object, do not instantiate
	 * @param page
	 * @param sm
	 */
	// FIXME make constructor private and instantiate via reflections?
	public PageRegistration(WidgetPage<?> page, SessionManagement sm, AccessManager acessManager) {
		super(sm, acessManager, (WidgetPageBase<?>) page);
//		this.pageSpecificId = pageSpecificId;
	}
	
	public void addWidget(ConfiguredWidget<?> cw) {
		checkWidget(cw);
		String sessionId = cw.getSessionId();
		if (sessionId != null) {
			Session session = sessionManagement.getSession(sessionId);
			if (session == null) {
				return;
			}
			session.addSessionWidget(cw);
			
			
//			Map<String,ConfiguredWidget<?>> widgets = sessionWidgets.get(sessionId);
//			if (widgets == null) {
//				sessionWidgets.putIfAbsent(sessionId, new ConcurrentHashMap<String,ConfiguredWidget<?>>());
//				widgets  =sessionWidgets.get(sessionId);
//			}
//			widgets.put(cw.getWidget().getId(), cw);
			return;
		}
		if (pageWidgets == null) {
			synchronized(initWidgetsLock) {
				if (pageWidgets == null) {
					if (pageWidgets == null) {
						initialWidgets.add(cw);
						return;
					}
				}
			}
		}
		// TODO sort?
		synchronized (widgetsLock) {
			pageWidgets.add(cw);
		}
	}
	
	/**
	 * 
	 * @param widget
	 * @throws IllegalArgumentException
	 * 		if a widget with the same id is already registered
	 */
	private void checkWidget(ConfiguredWidget<?> widget) throws IllegalArgumentException {
		String sessionId = widget.getSessionId();
		if (getWidgetIds(sessionId).contains(widget.getWidget().getId())) 
				throw new IllegalArgumentException("Widget with id " + widget.getWidget().getId() + " already exists");
	}
	
//	public void removeWidget(ConfiguredWidget<?> cw) {
//		String sessionId = cw.getSessionId();
//		if (sessionId != null) {
//			Map<String,ConfiguredWidget<?>> widgets = sessionWidgets.get(sessionId);
//			if (widgets == null)
//				return;
//			widgets.remove(cw.getWidget().getId());
//			return;
//		}
//		if (pageWidgets == null) {
//			synchronized(initWidgetsLock) {
//				if (pageWidgets == null) {
//					if (pageWidgets == null) {
//						initialWidgets.remove(cw);
//						return;
//					}
//				}
//			}
//		}
//		synchronized(widgetsLock) {
//			pageWidgets.remove(cw);
//		}
//	}

	public ConfiguredWidget<?> removeWidget(OgemaWidgetBase<?> widget) {
		sessionManagement.removeWidget(widget);
		for (WidgetGroupDerived group: groups.values()) {
			group.removeWidget(widget);
		}
		ConfiguredWidget<?> cw = null;
		if (pageWidgets == null) {
			synchronized (initWidgetsLock) {
				if (pageWidgets == null) {
					for (ConfiguredWidget<?> w: initialWidgets) {
						if (w.widget.equals(widget)) {
							cw = w;
							break;
						}
					}
				}
				if (cw != null) {
					return (initialWidgets.remove(cw) ? cw : null);
				}
			}
		}
		for (ConfiguredWidget<?> w: pageWidgets) {
			if (w.widget.equals(widget)) {
				cw = w;
				break;
			}
		}
		if (cw != null) {
			synchronized (widgetsLock) {
				pageWidgets.remove(cw);
			}
			return cw;
		}
		// expensive, would need an inverse index here (id -> session -> ConfiguredWidget)
//		for (ConcurrentMap<String,ConfiguredWidget<?>> map :sessionWidgets.values()) {
//			cw = map.remove(widget.getId());
//			if (cw != null)
//				return cw;
//		}
		
		return null;
	}
	
//	public void removeSession(String sessionId) {
//		sessionWidgets.remove(sessionId);
//	}
	
	public List<OgemaWidgetBase<?>> getWidgetsBase(String sessionId) {
		List<OgemaWidgetBase<?>> set = new ArrayList<>();
		for (ConfiguredWidget<?> cw: getWidgetsList()) {
			set.add(cw.widget);
		}
		if (sessionId != null) {
			Session session = sessionManagement.getSession(sessionId);
			if (session != null) {
				Map<String,ConfiguredWidget<?>> sessionW = session.getSessionWidgets();
				if (sessionW != null) {
					for (ConfiguredWidget<?> cw: sessionW.values()) {
						set.add(cw.widget);
					}
				}
			}
		}
		return set;
	}
	
	// non-blocking once initialized
	private List<ConfiguredWidget<?>> getWidgetsList() {
		if (pageWidgets != null)
			return pageWidgets;
		List<ConfiguredWidget<?>> w = null;
		while (w == null) {
			try {
				if (pageWidgets != null)
					w = pageWidgets;
				else {
					synchronized (initWidgetsLock) {
						w= new ArrayList<>(initialWidgets);
					}
				}
			} catch (NullPointerException e) {}
		}
		return w;
	}
	
	private int getIndex(OgemaWidget w, int dummyIndex) {
		for (int i=0; i<pageWidgets.size(); i++ ) {
			if (pageWidgets.get(i).getWidget().equals(w))
				return i;
		}
		return Integer.MAX_VALUE - dummyIndex; // not found; dummyIndex is just to differentiate between multiple widgets that were not found
	}
	
	public void sortWidgets(List<OgemaWidget> widgets) {
		if (widgets == null || widgets.isEmpty() || pageWidgets == null)
			return;
		SortedMap<Integer, OgemaWidget> positions = new TreeMap<>();
		for (int i=0;i<widgets.size();i++) {
			OgemaWidget w =  widgets.get(i);
			positions.put(getIndex(w, i), w);
		}
		widgets.clear();
		widgets.addAll(positions.values()); // now they should be in the right order; TODO is this efficient?
	}
	
	public void removeSessionWidget(String sessionId, OgemaWidget w) {
		Session session = sessionManagement.getSession(sessionId);
		if (session != null) {
			session.removeWidget(w.getId());
		}
	}

	
	public List<ConfiguredWidget<?>> getWidgets(String sessionId) {
		List<ConfiguredWidget<?>> set  = new ArrayList<>(getWidgetsList());
//		Set<ConfiguredWidget<?>> set = new HashSet<>(getWidgetsList());
//		for (ConfiguredWidget<?> cw: getWidgetsList()) {
//			if (cw.getSessionId() == null || cw.getSessionId().equals(sessionId)) 
//				set.add(cw);
//		}
		if (sessionId != null) {
			Session session = sessionManagement.getSession(sessionId);
			if (session != null) {
				Map<String,ConfiguredWidget<?>> sessionW  = session.getSessionWidgets();
				if (sessionW != null) {
					set.addAll(sessionW.values());
				}
			}
		}
		return set;
	}
	
	public Set<String> getWidgetIds(String sessionId) {
		Set<String> ids = new HashSet<>();
		for (ConfiguredWidget<?> cw: getWidgetsList()) {
			ids.add(cw.widget.getId());
		}
		if (sessionId != null) {
			Session session = sessionManagement.getSession(sessionId);
			if (session != null) {
				Map<String,ConfiguredWidget<?>> sessionW  = session.getSessionWidgets();
				if (sessionW != null) {
					ids.addAll(sessionW.keySet());
				}
			}
		}
		return ids;
	}
	
	public WidgetGroupDerived getGroup(String id) {
		return groups.get(id);
	}
	
	public WidgetGroupDerived removeGroup(String id) {
		return groups.remove(id);
	}
	
	public void addGroup(WidgetGroupDerived group) {
		groups.put(group.getId(), group);
	}
	
	
	public void initialize() {
		if (pageWidgets != null)
			return;
		synchronized (initWidgetsLock) {
			// need to check again, could have changed in the meantime
			if (pageWidgets != null)
				return;
			Comparator<ConfiguredWidget<?>> comp = new WidgetComparator(getMap(initialWidgets), groups);
			pageWidgets = new CopyOnWriteArrayList<>(WidgetsSort.sort(initialWidgets, comp));
			initialWidgets = null;
		}
	}
	
	private static Map<String, ConfiguredWidget<?>> getMap(List<ConfiguredWidget<?>> widgets) {
		return widgets.stream().collect(Collectors.toMap(cw -> cw.widget.getId(), Function.identity()));
	}
	
	public void updateOrder(ConfiguredWidget<?> widget) {
		if (pageWidgets == null)
			return;
		OgemaWidgetBase<?> base = widget.widget;
		if (base.globalConnectElements.isEmpty() && base.globalConnectGroups.isEmpty())
			return;
		// Use a copy... to avoid excessive copying during sort.
		synchronized (widgetsLock) {
			List<ConfiguredWidget<?>> copy = new ArrayList<>(pageWidgets);
			WidgetComparator comparator = new WidgetComparator(getMap(pageWidgets), groups);
			if (!copy.remove(widget))
				return;
			WidgetsSort.addEntry(pageWidgets, widget, comparator);
			pageWidgets = new CopyOnWriteArrayList<>(copy);
		}
	}
	
	public ConfiguredWidget<?> getConfiguredWidget(String id, String sessionId) {
		for (ConfiguredWidget<?> cw: getWidgetsList()) {
			if (cw.widget.getId().equals(id))
				return cw;
		}
		if (sessionId != null) {
			Session session = sessionManagement.getSession(sessionId);
			if (session != null) {
				return session.getSessionWidget(id);
			}
		}
		return null;
	}
	
	/**
	 * Does not return session widgets; only works when initialization has completed
	 * @param widget
	 * @return
	 */
	public ConfiguredWidget<?> getConfiguredWidget(OgemaWidgetBase<?> widget) {
		if (pageWidgets == null) return null;
		for (ConfiguredWidget<?> cw: getWidgetsList()) {
			if (cw.widget.getId().equals(widget))
				return cw;
		}
		return null;
	}
	
	private final AtomicBoolean closed = new AtomicBoolean(false);
	
	// this method may be called either from an app thread when the app stops, or
	// from the widget service when the latter closes down. So in a normal system shut down,
	// there are two attempts to close down the page, and we make sure only one proceeds.
	public void close() {
		boolean alreadyClosed = closed.getAndSet(true);
		if (alreadyClosed)
			return;
//		((WidgetAppImpl) page.getWidgetApp()).unregisterPageServlets((WidgetPageBase<?>) page);
		groups.clear();
		if (pageWidgets != null)
			pageWidgets.clear();
		synchronized (initWidgetsLock) {
			if (initialWidgets != null)
				initialWidgets.clear();
		}
		pageWidgets = null;
		initialWidgets = null;
	}
	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp, true);
	}
	
	private void handleRequest(final HttpServletRequest req, final HttpServletResponse resp, final boolean isGet) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (path.startsWith("/"))
			path = path.substring(1);
//		ConfiguredWidget<?> widget = pageWidgets.get(path);
		OgemaHttpRequest ogReq = new OgemaHttpRequest(req, false);
		ConfiguredWidget<?> widget = getConfiguredWidget(path, ogReq.getSessionId());
		if (widget == null) {
			LoggerFactory.getLogger(getClass()).trace("Widget not found {}. Probably the session has been closed.", path);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Widget " + path + " not found.");
			return;
		}
		final OgemaWidgetBase<?> widgetImpl = widget.getWidget();
		if (widgetImpl.isGlobalWidget()) {
			if (isGet)
				widgetImpl.doGet(req, resp);
			else
				widgetImpl.doPost(req, resp);
		}
		else {
			// create Callable wrapping the widget doPOST and submit it to the respective session's ExecutorService
			String sessionId = ogReq.getSessionId();   
			ExecutorService es = sessionManagement.getExecutor(sessionId);
			if (es == null) {
				LoggerFactory.getLogger(getClass()).trace("Widget executor found null. Probably the session has been closed.");
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			final String user = accessManager.getCurrentUser(); // never null
			Callable<Void> callable = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					accessManager.setCurrentUser(user);
					if (isGet)
						widgetImpl.doGet(req, resp);
					else
						widgetImpl.doPost(req, resp);
					return null;
				}
				
			};
			Future<Void> future = es.submit(callable);
			// FIXME not clear whether it is important to wait here... otherwise the widget request response might come too early ... but does it?
			try {
				future.get(1, TimeUnit.HOURS); 
			} catch (InterruptedException e) {
				LoggerFactory.getLogger(getClass()).info("Widget thread interrupted",e); // expected to happen when system shuts down
			} catch (ExecutionException e) {
				Throwable ee = e.getCause();
				if (ee instanceof ServletException)
					throw (ServletException) ee;
				throw new RuntimeException(ee);
			} catch (TimeoutException e) {
				throw new RuntimeException("Widget thread interrupted after waiting for one hour!",e);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp, false);
	}
	
//	private static final String getSessionId(HttpServletRequest req, boolean pageSpecificId) {
//		if (pageSpecificId) {
//			return req.getSession().getId() + "_" + req.getParameter("pageInstance");
//		} else {
//			return req.getSession().getId();
//		}
//	}

	public final WidgetPage<?> getPage() {
		return page;
	}
	

	
//	public final boolean usePageSpecificId() {
//		return pageSpecificId;
//	}
	
}
