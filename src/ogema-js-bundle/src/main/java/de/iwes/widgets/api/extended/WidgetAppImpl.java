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

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.Objects;

import org.ogema.core.application.AppID;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.security.WebAccessManager;
//import org.ogema.util.datamanagement.AppSessionData;
//import org.ogema.util.datamanagement.SessionDataManagement;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.impl.OgemaOsgiWidgetServiceImpl;
import de.iwes.widgets.api.extended.impl.WidgetServiceImpl;
import de.iwes.widgets.api.services.IconService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.start.JsBundleApp;

public class WidgetAppImpl implements WidgetApp {

	@SuppressWarnings("rawtypes")
	private HashMap<String, WidgetPageBase> pages = new HashMap<String, WidgetPageBase>();

//	@Deprecated
//	private final String widgetAppId;
	final WebAccessManager wam;
	public final OgemaLogger log;
	protected final String htmlPath;
	private final OgemaGuiService guiService;
//	protected final SessionDataManagement<? extends AppSessionData> sessions;
	protected final AppID appId;
//	final boolean pageSpecificId;
	final String appVersion;

	public WidgetAppImpl(String htmlPath, OgemaGuiService guiService, ApplicationManager am) {
		this(htmlPath, guiService, am, true);
	}

	@Deprecated
	public WidgetAppImpl(String widgetAppId, String htmlPath,  OgemaGuiService guiService, ApplicationManager am) {
		this(widgetAppId, htmlPath, guiService, am, true);
	}

//	@Deprecated
//	public WidgetApp(String widgetAppId, String htmlPath, OgemaOsgiWidgetService widgetService, ApplicationManager am,
//			SessionDataManagement<? extends AppSessionData> sessions) {
//		this.widgetAppId = widgetAppId;
//		this.wam = am.getWebAccessManager();
//		this.log = am.getLogger();
//		this.appId = am.getAppID();
//		if (!htmlPath.startsWith("/")) throw new IllegalArgumentException("URL must start with a slash (\"/\"). Got instead " + htmlPath);
//		this.htmlPath = htmlPath;
//		this.widgetService = widgetService;
//		if(sessions== null) {
//			this.sessions = new SessionDataManagement<AppSessionData>(AppSessionData.class);
//		} else {
//			this.sessions = sessions;
//		}
//		pageSpecificId = true;
//		this.appVersion = AccessController.doPrivileged(getVersion);
//	}

	@Deprecated
	public WidgetAppImpl(String widgetAppId, String htmlPath, OgemaGuiService guiService, ApplicationManager am, boolean pageSpecificId) {
		Objects.requireNonNull(am);
		Objects.requireNonNull(guiService);
		Objects.requireNonNull(htmlPath);
		checkPath(htmlPath);
		this.guiService = guiService;
//		this.widgetAppId = widgetAppId;
		this.wam = am.getWebAccessManager();
		this.log = am.getLogger();
		this.appId = am.getAppID();
		this.htmlPath = htmlPath;
//		this.pageSpecificId = pageSpecificId;
//		this.sessions = null;
		this.appVersion = AccessController.doPrivileged(getVersion);
		getWidgetService().addApp(this);
	}

	public WidgetAppImpl(String htmlPath, OgemaGuiService guiService, ApplicationManager am, boolean pageSpecificId) throws IllegalStateException {
		// we do this first, since it throws an exception if an app is already registered under the same url
		Objects.requireNonNull(am);
		Objects.requireNonNull(guiService);
		checkPath(htmlPath);
		this.guiService = guiService;
		this.wam = am.getWebAccessManager();
		this.log = am.getLogger();
		this.appId = am.getAppID();
		this.htmlPath = htmlPath;
//		this.pageSpecificId = pageSpecificId;
//		this.sessions = null;
		this.appVersion = AccessController.doPrivileged(getVersion);
		getWidgetService().addApp(this);
	}

	private static final void checkPath(String url) {
		if (url == null || url.trim().isEmpty() || !url.startsWith("/"))
			throw new IllegalArgumentException("URL must start with a slash (\"/\"). Got instead " + url);
		if (url.startsWith("/ogema/") || url.startsWith("/org/ogema"))
			throw new IllegalArgumentException("URL must not start with '/ogema' or ' /org/ogema'");
		if (url.length() - url.replace("/", "").length() < 2)
			throw new IllegalArgumentException("URL must contain at least two separators (\"/\")");
		if (url.trim().length() < 6)
			throw new IllegalArgumentException("URL too short: " + url + "; must be 6 characters at least");
		try {
			new URI(url);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("URL not valid: "+ url,e);
		}
	}

	//abstract public void init();

	// TODO: Check if url is well formed must start with /app/... and end
	// without /
	//abstract public String appUrl();

	@SuppressWarnings("rawtypes")
	@Override
	public void close() {
		try {
			synchronized(pages) {
				Iterator<Entry<String, WidgetPageBase>>  it = pages.entrySet().iterator();
				while (it.hasNext()) {
					WidgetPageBase<?> page = it.next().getValue();
					try {
						page.close();
					} catch (Exception e) {
						log.error("Page did not close down properly. {}",e);
					}
					unregisterServlets(page);
					it.remove();
				}
			}
			OgemaOsgiWidgetServiceImpl serviceImpl = getWidgetService();
			if (serviceImpl != null)
				serviceImpl.removeApp(this);
		} catch (Exception e) {
			log.error("Error closing app " + appUrl(),e);
		}
	}

	private void unregisterServlets(WidgetPageBase<?> page) {
		if (page instanceof WidgetPageImpl) {
			try {
				if (page.staticRegistration != null)
					((org.ogema.webadmin.AdminWebAccessManager.StaticRegistration)page.staticRegistration).unregister();
				else
					wam.unregisterWebResource(htmlPath + "/" + page.getUrl());
			} catch (NoClassDefFoundError | ClassCastException e) {
				wam.unregisterWebResource(htmlPath + "/" + page.getUrl());
			} catch (Exception e) {
				log.error("Could not unregister widget page " + page.getUrl() + "; "  + e);
			}
		}
		try {
			OgemaOsgiWidgetServiceImpl widgetService = ((OgemaOsgiWidgetServiceImpl) getWidgetService());
			// if widget service has been closed down already, it must have unregistered the page as well.
			if (widgetService == null)
				return;
			PageRegistrationI registration = widgetService.removePage(page);
			if (registration != null)
				wam.unregisterWebResource(registration.getServletBase());
		} catch (Exception e) {
			log.error("Page did not close down properly. {}",e);
		}
	}

	/**
	 * @return doc url
	 */
	@Override
	public String appUrl() {
		return htmlPath;
	}

	/**
	 * Register page on app.
	 *
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean registerFallback(WidgetPageBase<?> page, boolean setAsStartPage) {
		String relativeUrl = page.getUrl();
		String pageUrl = htmlPath + "/" + relativeUrl;
		synchronized (pages) {
			if (pages.containsKey(relativeUrl))
				throw new IllegalStateException("Page with url " + pageUrl + " already registered");
			if (page instanceof ServletBasedWidgetPage) {
				wam.registerWebResource(pageUrl, ((ServletBasedWidgetPage<?>) page).getServlet());
				LoggerFactory.getLogger(JsBundleApp.class).info("Page registered under " + pageUrl);
			}
			// register page
			pages.put(relativeUrl, page);
		}
		if (setAsStartPage)
			wam.registerStartUrl(pageUrl);
		getWidgetService().createPageRegistration(page, wam);
		return true;
	}


	/**
	 * Register page on app.
	 *
	 * @param page
	 * @return
	 * 		an object of type AdminWebAccessManager.StaticRegistration
	 * @throws NoClassDefFoundError
	 * @throws ClassCastException
	 */
	@SuppressWarnings("rawtypes")
	public Object register(WidgetPageBase<?> page, boolean setAsStartPage) {
//		if (page instanceof ReferencePage) // already registered for LazyPage, which will delegate to ReferencePage
//			return null;
		String relativeUrl = page.getUrl();
		String pageUrl = htmlPath + "/" + relativeUrl;
		final org.ogema.webadmin.AdminWebAccessManager.StaticRegistration staticRegistration;
		synchronized (pages) {
			if (pages.containsKey(relativeUrl))
				throw new IllegalStateException("Page with url " + pageUrl + " already registered");
			if (page instanceof ServletBasedWidgetPage) {
				staticRegistration = ((org.ogema.webadmin.AdminWebAccessManager) wam).registerStaticWebResource(pageUrl, ((ServletBasedWidgetPage) page).getServlet());
				LoggerFactory.getLogger(JsBundleApp.class).info("Page registered under " + pageUrl);
			}
			else
				staticRegistration = null;
			// register page
			pages.put(relativeUrl, page);
		}
		if (setAsStartPage)
			wam.registerStartUrl(pageUrl);
		getWidgetService().createPageRegistration(page, wam);
		return staticRegistration;
	}

	public WidgetPageBase<?> getPage(String relativeUrl) {
		synchronized(pages) {
			return pages.get(relativeUrl);
		}
	}

	// TODO check
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, WidgetPage<?>> getPages() {
		synchronized (pages) {
			return new HashMap(pages);
		}
	}

	void remove(WidgetPageBase<?> page) {
		synchronized (pages) {
			pages.remove(page.getUrl());
		}
		getWidgetService().removePage(page);
	}

	/**
	 * Session expiry time in ms. <br>
	 * -1: use default value
	 */
//	@Deprecated
//	long getSessionExpiryTime() {
//		return sessions.getSessionExpiryTime();
//	}
//
//	/**
//	 * Maximum nr. of parallel sessions. <br>
//	 * -1: use default value
//	 */
//	@Deprecated
//	int getMaxNrSessions() {
//		return sessions.getMaxNrSessions();
//	}

	private PrivilegedAction<String> getVersion = new PrivilegedAction<String>() {

		@Override
		public String run() {
			return FrameworkUtil.getBundle(getClass()).getVersion().toString();
		}
	};

	@Override
	public <D extends LocaleDictionary> WidgetPage<D> createStartPage() {
		return createWidgetPage("index.html", true);
	}

	@Override
	public <D extends LocaleDictionary> WidgetPage<D> createWidgetPage(String relativeUrl) {
		return createWidgetPage(relativeUrl, false);

	}

	@Override
	public <D extends LocaleDictionary> WidgetPage<D> createWidgetPage(
			String relativeUrl, boolean setAsStartPage) {
		final WidgetPageImpl<D> page = new WidgetPageImpl<D>(this, relativeUrl, setAsStartPage);
		registerPageInternal(page, setAsStartPage);
		return page;
	}

	@Override
	public <D extends LocaleDictionary> void createLazyStartPage(Consumer<WidgetPage<D>> callback) {
		createLazyPage("index.html", callback, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <D extends LocaleDictionary> void createLazyPage(String relativUrl, Consumer<WidgetPage<D>> callback, boolean asStartPage) {
		final LazyPage<?> page = new LazyPage(callback, this, relativUrl, asStartPage, WidgetPageImpl.class);
		registerPageInternal(page, asStartPage);
	}

	@Override
	public <D extends LocaleDictionary> void createLazyStartPage(Consumer<WidgetPage<D>> callback,
			Class<? extends WidgetPage> pageType) {
		createLazyPage("index.html", callback, pageType, true);
	}
	
	@Override
	public <D extends LocaleDictionary> void createLazyPage(String relativUrl, Consumer<WidgetPage<D>> callback,
			Class<? extends WidgetPage> pageType, boolean asStartPage) {
		final LazyPage<?> page = new LazyPage(callback, this, relativUrl, asStartPage, pageType);
		registerPageInternal(page, asStartPage);
	}

	IconService getIconService() {
		return guiService.getIconService();
	}

	NameService getNameService() {
		return guiService.getNameService();
	}

	OgemaOsgiWidgetServiceImpl getWidgetService() {
		return (OgemaOsgiWidgetServiceImpl) ((WidgetServiceImpl) guiService).widgetServiceInternal;
	}

	@Override
	public String toString() {
		return "Widget app " + (appId != null ? appId.getIDString() : null);
	}

	@Override
	public int hashCode() {
		return 31 * htmlPath.hashCode() + 11;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WidgetAppImpl))
			return false;
		WidgetAppImpl other = (WidgetAppImpl) obj;
		return other.htmlPath.equals(htmlPath);
	}

	private void registerPageInternal(final WidgetPageBase<?> page, final boolean setAsStartPage) {
		page.staticRegistration = AccessController.doPrivileged(new PrivilegedAction<Object>() {

			@Override
			public Object run() {
				try {
					return register(page, setAsStartPage);
				} catch(NoClassDefFoundError | ClassCastException | SecurityException e) { // fallback for OGEMA v < 2.1.2
					registerFallback(page, setAsStartPage);
					return null;
				}
			}
		});
	}

	@Override
	public String addResource(String path, Class<? extends Application> app) {
		if (app != null && app.getResource("/" + path) == null) {
			log.warn("Could not register resource {} (File not found).", path);
			return null;
		}
		String url = this.appUrl().replaceAll("/$", "") + "/" + path;
		return wam.registerWebResource(url, path);
	}

	@Override
	public boolean addStylesheet(String stylesheetName, Class<? extends Application> app) {
		
		String stylesheetUrl = addResource(stylesheetName, app);
		if (stylesheetUrl == null)
			return false;
		
		HtmlLibrary style = new HtmlLibrary(HtmlLibrary.LibType.CSS, stylesheetName, stylesheetUrl);
		Collection<WidgetPage<?>> pages = this.getPages().values();
		for (WidgetPage<?> p : pages) {
			if (p instanceof ServletBasedWidgetPage<?>) {
				ServletBasedWidgetPage<?> pb = (ServletBasedWidgetPage<?>) p;
				pb.registerLibrary(style);
			} else {
				String tag = "<link rel=\"stylesheet\" href=\""+ stylesheetUrl + "\">";
				p.append(tag);
			}
		}
		
		return true;
	}

/*	public WidgetPage<?> createPage() {
		return createPage("index.html");
	}
	public WidgetPage<?> createPage(String startHtml) {
		WidgetPage<?> p = new WidgetPage(startHtml);
		if(register(p)) {
			return p;
		} else {
			return null;
		}
	}

*/}
