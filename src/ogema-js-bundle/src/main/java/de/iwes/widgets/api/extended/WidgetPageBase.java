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

package de.iwes.widgets.api.extended;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.impl.HtmlLibrary;
import de.iwes.widgets.api.extended.impl.OgemaOsgiWidgetService;
import de.iwes.widgets.api.extended.impl.OgemaOsgiWidgetServiceImpl;
import de.iwes.widgets.api.extended.impl.WidgetSessionManagement;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.start.JsBundleApp;

public class WidgetPageBase<S extends LocaleDictionary> implements WidgetPage<S> {

	private final String startHtml;
	final WidgetAppImpl app; // need to be public?
	private WidgetGroup allWidgets = null; 
	/**
	 * Of type
	 *  org.ogema.webadmin.AdminWebAccessManager.StaticRegistration
	 */
	final Object staticRegistration;
	
	/***************** Constructors ********************/

//	private Map<String, OgemaWidgetBase<?>> simpleWidgets = new ConcurrentHashMap<String, OgemaWidgetBase<?>>();
	// Map<String language, OgemaLocale locale>
	/**
	 * If no url provided the url is set to index.html
	 */
	public WidgetPageBase(WidgetAppImpl app) {
		this(app, "index.html",false);
	}
	
	public WidgetPageBase(WidgetAppImpl app, boolean setAsStartPage) {
		this(app, "index.html", setAsStartPage);
	}
	
	public WidgetPageBase(WidgetAppImpl app, String startHtml) {
		this(app, startHtml, false);
	}
	
	/**
	 * Create page with specified start HTML page
	 * @param startHtml
	 *            to page
	 */
	public WidgetPageBase(final WidgetAppImpl app, String startHtml, final boolean setAsStartPage) {
		this.app = app;
		if (!startHtml.contains(".htm")) startHtml = startHtml +".html"; // required later on
		this.startHtml = startHtml;
		initBeforeRegistration();
		Object staticReg = null;
		try {
			staticReg = AccessController.doPrivileged(new PrivilegedAction<Object>() {

				@Override
				public Object run() {
					return app.register(WidgetPageBase.this,setAsStartPage);
				}
			});
		} catch(NoClassDefFoundError | ClassCastException | SecurityException e) { // fallback for OGEMA v < 2.1.2
			app.registerFallback(this, setAsStartPage);
		}
		this.staticRegistration = staticReg;
			
//		app.widgetService.setSessionExpiryTime(this, app.getSessionExpiryTime());
//		app.widgetService.setMaxNrSessions(this, app.getMaxNrSessions());
	}
	
	/*************** Public methods *****************/
	
//	//Override if required 
//	public void init() {};
//	
	//Override if required
	protected void initBeforeRegistration() {};

	/**
	 *  returns the URL relative to app base
	 */
	@Override
	public String getUrl() {
		return startHtml;
	}
	
	@Override
	public String getFullUrl() {
		String appUrl = getApp().htmlPath;
		return appUrl + "/" + startHtml;
	}
	
	public final String unregister(OgemaWidgetBase<?> widget) {
		// remove widget from service
		final OgemaOsgiWidgetService widgetService = getApp().getWidgetService();
		if (widgetService != null)
			widgetService.unregisterWidget(getServletBase(), widget);
		return widget.getId();
	}
	
	final void close() {
//		Iterator<Entry<String, OgemaWidgetBase<?>>> it = simpleWidgets.entrySet().iterator();
//		while (it.hasNext()) {
//			try {
//				getWidgetService().unregisterWidget(getServletBase(),it.next().getValue());
//			} catch (Exception e) {
//				LoggerFactory.getLogger(getClass()).warn("Widget unregistration failed");
//			}
//		}
//		simpleWidgets.clear();
	}


/*	public void register(IOgemaWidgetSimple widget) {

		// insert all widgets item, nested, container
		for ( IOgemaWidgetSimple w : widget.getAllWidgets()) {
			simpleWidgets.put(w.getId(), w);
		}
	}
*/
	// TODO needs replacement
//	public Map<String, OgemaWidgetBase<?>> getWidgets() {
//		return simpleWidgets;
//	}
	
	//@Deprecated
    //public AppSessionData getSession(OgemaHttpRequest req) {
    //	return getApp().sessions.getSessionData(req);
    //}

//    public final boolean usePageSpecificId() {
//    	return getApp().pageSpecificId;
//    }
	
	public final String getServletBase() {
		String servletPath =  getApp().appUrl()+"/"+getUrl();
		int idx = servletPath.lastIndexOf(".htm");
		return servletPath.substring(0, idx);
		
	}
	
	@Override
	public Map<String,String[]> getPageParameters(OgemaHttpRequest req) {
		return getWidgetService().getPageParameters(this, req);
	}
	
	
	/*************** Internal methods *****************/
	
	void registerLibrary(HtmlLibrary lib) {
		// currently only used by WidgetPageSimple
	}
	
	/**
	 * Register a SimpleWidget on a page.
	 * 
	 * @param widget
	 * @return widget id
	 */
	@SuppressWarnings("unchecked")
	final <T extends WidgetData> WidgetSessionManagement<T> registerNew(OgemaWidgetBase<T> widget, boolean globalWidget) {

		// init widget
		// add to widgets
		//String servletPath =  app.appUrl()+"/"+getUrl().replace(".html", "").replace(".htm", "");
		String servletPath = getServletBase();
		ConfiguredWidget<T> cw = (ConfiguredWidget<T>) getWidgetService().registerWidgetNew(widget,servletPath,getApp().wam);
//		simpleWidgets.put(widget.getId(), widget);
		return cw;
	}
	/** register a session-dependent widget **/
	@SuppressWarnings("unchecked")
	final <T extends WidgetData> WidgetSessionManagement<T> registerNew(OgemaWidgetBase<T> widget, OgemaHttpRequest session) {  // globalWidget = true
		// add to widgets
		//String servletPath =  app.appUrl()+"/"+getUrl().replace(".html", "").replace(".htm", "");
		String servletPath = getServletBase();
		return (WidgetSessionManagement<T>) getWidgetService().registerWidgetNew(widget,servletPath,getApp().wam,session);
	}
	
	@SuppressWarnings("unchecked")
	final  <T extends WidgetData> WidgetSessionManagement<T> registerNew(OgemaWidget widget, OgemaHttpRequest session) {
		return registerNew((OgemaWidgetBase<T>) widget, session);
	}

	public final WidgetAppImpl getApp() {
		return app;
	}
	
	private final Map<String, S> dicts = new HashMap<String, S>();

/*	public WidgetPage<S> registerLocalisation(S dict) {
//		S dict = (S) dictionary;
		String code = dict.getLocale().getLanguage();
		dicts.put(code, dict);
		return this;
	} */ 
	
	@Override
	public final <T extends S> WidgetPageBase<S> registerLocalisation(final Class<T> clazz) {
		return AccessController.doPrivileged(new PrivilegedAction<WidgetPageBase<S>>() {  // need reflection permission here
			@Override
			public WidgetPageBase<S> run() {
				try {
					T object = clazz.getConstructor().newInstance();
					String code = object.getLocale().getLanguage();
					dicts.put(code, object); 
				} catch (NoSuchMethodException e) {
					LoggerFactory.getLogger(JsBundleApp.class).error("Class {} does not provide a public default constructor.",clazz);
				} catch (Exception e) {
					LoggerFactory.getLogger(JsBundleApp.class).error("Could not register locale {}: {}",clazz,e);
				}
				return WidgetPageBase.this;
			}
		});
	}
	
	
	@Override
	public final S getDictionary(String language) {  // TODO let app/user set a fallback language
		if (dicts.isEmpty()) 
			throw new IllegalStateException("No dictionary has been registered with the page " + getUrl());
		S dict = dicts.get(language);
		if (dict == null) {
			dict = dicts.get(Locale.getDefault().getLanguage());
			if (dict == null) {
				dict = dicts.get(Locale.ENGLISH.getLanguage());
				if (dict == null && !dicts.isEmpty()) {
					dict = dicts.values().iterator().next();
				}
			}
		}
		return dict;
	}
	
	@Override
	public final S getDictionary(OgemaHttpRequest req) {
		return getDictionary(req.getLocaleString());
	}
	
	/**
	 * @param groupId
	 * 		a unique id
	 * @param widgets
	 * 		widgets constituting the group. It is possible to add widgets to the group later on, using {@see WidgetGroup#addWidget(OgemaWidget)}
	 * @return
	 * 		the newly created WidgetGroup
	 * @throws IllegalArgumentException
	 * 		if a WidgetGroup with the given id already exists
	 */
	@Override
	public final WidgetGroup registerWidgetGroup(String groupId, Collection<OgemaWidget> widgets) throws IllegalArgumentException {
		if (widgets == null) 
			widgets = Collections.emptySet();
		Iterator<OgemaWidget> it = widgets.iterator();
		while(it.hasNext()) {
			OgemaWidget widget = it.next();
			if (!widget.getPage().equals(this))
				throw new IllegalArgumentException("Cannot assign widget belonging to another page to a WidgetGroup");
			((OgemaWidgetBase<?>) widget).addGroup(groupId);
		}
		return getWidgetService().registerWidgetGroup(this, groupId, widgets, app.wam);		
	}
	
	@Override
	public WidgetGroup registerWidgetGroup(String groupId) {
		return registerWidgetGroup(groupId, null);
	}
	
	@Override
	public void removeWidgetGroup(WidgetGroup group) {
		Set<OgemaWidget> widgets  = group.getWidgets();
		for (OgemaWidget w: widgets) {
			try {
				((OgemaWidgetBase<?>) w).removeGroup(group.getId());
			} catch (Exception e) {}
		}
		getWidgetService().removeWidgetGroup(this, group);
		
	}
	
	@Override
	public final synchronized WidgetGroup getAllWidgets() {
		if (allWidgets == null) {
			allWidgets = new AllWidgetsGroupImpl(getPageRegistration());
		}
		return allWidgets;	
	}
	
//	/*
//	 * handle POST requests triggered in some other widget's onPrePOST method
//	 * @param request
//	 * @param req
//	 */
//	void handleTriggeredPOSTs(JSONObject request, OgemaHttpRequest req) {
//		Iterator<String> it = request.keys();
//		while(it.hasNext()) {
//			String id = it.next();
//			if (id.equals("data")) continue; // this is the data of the original triggering widget
//			OgemaWidgetBase<?> widget = simpleWidgets.get(id);
//			if (widget == null) {
//				app.log.warn("Widget for update not found: " + id);
//				continue;
//			}
//			try {
////				String data = request.getString(id);
//				JSONObject dataObj = request.getJSONObject(id);
//				String data = dataObj.toString();
//				widget.onPrePOST(data, req);
//				// TODO append results?
//				widget.getData(req).onPOST(data, req);
//				widget.onPOSTComplete(data, req);
//			} catch (UnsupportedOperationException e) { // if POST is not supported
//				continue;
//			} catch (Exception e) {
//				app.log.error("Error executing triggered POST: {}",widget, e);
//			}
//		}
//		
//	}

	@Override
	public WidgetApp getWidgetApp() {
		return app;
	}

	@Override
	public WidgetPage<?> append(OgemaWidget widget) {
		throw new UnsupportedOperationException("append method only supported by WidgetPageSimple");
	}

	@Override
	public WidgetPage<?> append(HtmlItem htmlItem) {
		throw new UnsupportedOperationException("append method only supported by WidgetPageSimple");
	}

	@Override
	public WidgetPage<?> append(String text) {
		throw new UnsupportedOperationException("append method only supported by WidgetPageSimple");
	}

	@Override
	public WidgetPage<S> linebreak() {
		throw new UnsupportedOperationException("linebreak only supported by WidgetPageSimple");
	}

	@Override
	public void showOverlay(boolean show) {
		throw new UnsupportedOperationException("spinner only supported by WidgetPageSimple");
	}
	
	private OgemaOsgiWidgetServiceImpl getWidgetService() {
		return (OgemaOsgiWidgetServiceImpl) app.getWidgetService();
	}

	@Override
	public MenuConfiguration getMenuConfiguration() {
		throw new UnsupportedOperationException("menu configuration only supported by WidgetPageSimple");
	}

	@Override
	public void setBackgroundImg(String backgroundImg) {
		throw new UnsupportedOperationException("background image only supported by WidgetPageSimple");
	}
	
	@Override
	public WidgetPage<S> setTitle(String title) {
		throw new UnsupportedOperationException("Title only supported by WidgetPageSimple");
	}
	
	@Override
	public int hashCode() {
		return 41 * app.hashCode() + startHtml.hashCode() * 3;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WidgetPageBase))
			return false;
		WidgetPageBase<?> other = (WidgetPageBase<?>) obj;
		if (!other.startHtml.equals(startHtml))
			return false;
		return other.getApp().equals(getApp()); 
	}
	
	private final PageRegistration getPageRegistration() {
		return getWidgetService().createPageRegistration(this, app.wam);
	}

	@Override
	public OgemaWidget getTriggeringWidget(OgemaHttpRequest req) {
		final String triggered = req.getReq().getParameter("triggeredBy");
		if (triggered == null)
			return null;
		final ConfiguredWidget<?> w = getPageRegistration().getConfiguredWidget(triggered, req.getSessionId());
		if (w == null)
			return null;
		return w.getWidget();
	}
	
}
