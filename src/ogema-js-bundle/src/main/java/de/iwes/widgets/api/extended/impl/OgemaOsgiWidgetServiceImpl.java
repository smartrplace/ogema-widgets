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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.accesscontrol.AccessManager;
import org.ogema.core.application.AppID;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.security.WebAccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.LazyPage;
import de.iwes.widgets.api.extended.LazyPageRegistration;
import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.PageRegistration;
import de.iwes.widgets.api.extended.PageRegistrationI;
import de.iwes.widgets.api.extended.WidgetAdminService;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.extended.util.UserLocaleUtil;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.start.JsBundleApp;

/**
 * @author esternberg
 */
//@Component(specVersion = "1.2")
//@Service(WidgetAdminService.class)
public class OgemaOsgiWidgetServiceImpl extends HttpServlet implements WidgetAdminService, OgemaOsgiWidgetService {
	
	public final static Logger logger = LoggerFactory.getLogger(JsBundleApp.class);
	private static final long serialVersionUID = 1L;
	
	// initialized sessions Map<sessionId, lastUpdateTime>
//	private final ConcurrentMap<String,Long> sessions = new ConcurrentHashMap<String, Long>();
//	 Map<boundPagePath, session expiry time>
	private final ConcurrentMap<String,Long> expiryTimes = new ConcurrentHashMap<String, Long>();
//	 Map<boundPagePath, max nr sessions>
	private final ConcurrentMap<String,Integer> maxNrSessions = new ConcurrentHashMap<String, Integer>();
	private final SessionManagement sessionManagement = new SessionManagement();
	private final Timer timer = new Timer("widget-session-mgmt");

	//private final static String PATH_PREFIX = "/ogema/";
    private final static String PATH_PREFIX = "/ogema/widget/";
    private final AccessManager accessManager;
    private final ApplicationManager appMan;
    
    // FIXME use only logging mechanism!
    @Deprecated
    private final static String printmessages = AccessController.doPrivileged(new PrivilegedAction<String>() {

		@Override
		public String run() {
			return System.getProperty("org.ogema.widgetservice.printmessages");
		}
    	
	});
    
//    @Reference
//    private HttpService httpService;
    
    //Map<boundPagePath, Map<WidgetId, Widget>>
//    private final ConcurrentMap<String, ConcurrentMap<String, ConfiguredWidget>> registeredWidgets = new ConcurrentHashMap<>();
    // replaced: Map<bound page path, PageRegistration>
    private final ConcurrentMap<String, PageRegistrationI> registeredPages = new ConcurrentHashMap<>();
    
    // Map<sessionId, Widget>   // note that these widgets are also registered in the registeredWidgets field; this one is mainly for convenience 
//    private final ConcurrentMap<String, List<ConfiguredWidget>> sessionWidgets = new ConcurrentHashMap<>();
//    private final ConcurrentMap<String, ConfiguredWidget> initWidgets = new ConcurrentHashMap<>();  // FIXME remove?
    // key: boundPagePath (w/o https:// and .htm*), value: use page specific session id?
//    private final ConcurrentMap<String, Boolean> pageSpecificId = new ConcurrentHashMap<String, Boolean>();  // TODO move to PageRegistration?
    
    // Map<boundPagePath, Map<widgetGroupId, WidgetGroup>> 
//    private final ConcurrentMap<String, ConcurrentMap<String,WidgetGroupDerived>> widgetGroups = new ConcurrentHashMap<String, ConcurrentMap<String,WidgetGroupDerived>>();
    private final Map<String, WidgetAppImpl> apps = new LinkedHashMap<>();
    
    public OgemaOsgiWidgetServiceImpl(AccessManager accessManager, ApplicationManager appMan) {
    	this.accessManager = accessManager;
    	this.appMan = appMan;
    	timer.schedule(sessionManagement, Constants.SESSION_CHECK_PERIOD, Constants.SESSION_CHECK_PERIOD);
    }
    
    // release all references
    public void deactivate() {
		sessionManagement.close();
		timer.cancel();
		final Collection<WidgetAppImpl> apps;
    	synchronized (this.apps) {
    		apps = new ArrayList<>(this.apps.values());
    		this.apps.clear();
    	}
    	for (WidgetAppImpl a : apps)
    		a.close();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public List<OgemaWidget> getPageWidgets(WidgetPage<?> page) {
    	Objects.requireNonNull(page);
    	PageRegistration pr = getPageRegistration(((WidgetPageBase<?>) page).getServletBase());
    	if (pr == null)
    		return Collections.emptyList();
    	return (List) pr.getWidgetsBase(null);
    }
    
    public SessionManagement getSessionManagement() {
    	return sessionManagement;
    }
    
    /**
     * Register an OgemaWidget, this will register the neccesary Resource and servlet path
     * in the Osgi HttpService. If a new Widget instance of a Class with the same boundPath
     * is registered, the old instance is unregistered.
     * @param widget
     * @return 
     */
    @Override
    public ConfiguredWidget<?> registerWidgetNew(OgemaWidgetBase<?> widget, String boundPagePath,WebAccessManager wam) {
    	return registerWidgetInternal(widget, boundPagePath, wam);
    }
    
//	@Override
//	public void setTnitWidget(OgemaWidgetBase<?> widget, String boundPagePath, boolean initStatus) {
//		ConfiguredWidget c = getConfiguredWidget(widget, boundPagePath);
//		ConfiguredWidget existingInitWidget = initWidgets.get(boundPagePath);
//		if(c == null) throw new IllegalArgumentException("Trying to operate on widget not registered for the page!");
//		if(!initStatus) {
//			if((existingInitWidget != null) && (existingInitWidget.equals(c))) {
//				initWidgets.remove(boundPagePath);
//				existingInitWidget.setInitWidget(false);
//			}
//		} else {
//			if(existingInitWidget != null) {
//				throw new IllegalStateException("Init widget is already set for the page!"); // FIXME this does happen upon stop and restart of an app
//			}
//			initWidgets.put(boundPagePath, c);
//			c.setInitWidget(true);
//		}
//    }
//	
//	@Override
//	public void setWidgetDepedencyStatus(OgemaWidgetBase<?> widget, String boundPagePath, boolean dependencyStatus) {
//		ConfiguredWidget c = getConfiguredWidget(widget, boundPagePath);
//		if(c == null) throw new IllegalArgumentException("Trying to operate on widget not registered for the page!");
//		c.setInitWidget(dependencyStatus);
//	}
    
    private ConfiguredWidget<?> registerWidgetInternal(OgemaWidgetBase<?> widget, String boundPagePath,WebAccessManager wam) {
//    	System.out.println("Registering widget " + widget.getId()+ ", under " + boundPagePath);
        final String widgetSystemPath = widget.getWidgetClass().getPackage().getName().replaceAll("\\.", "/");
        String widgetWebResourcePath = PATH_PREFIX + widgetSystemPath.substring( widgetSystemPath.lastIndexOf("/")+1 );

        String widgetServletPath = boundPagePath + "/" + widget.getId();
        
        widgetWebResourcePath = widgetWebResourcePath.replaceAll("/+", "/");
        widgetServletPath = widgetServletPath.replaceAll("/+", "/");
        PageRegistrationI pr = createPageRegistration(widget.getPage(), wam);
//        if(registeredWidgets.get(boundPagePath) == null) {
//	        synchronized (registeredWidgets) {
//		        if(registeredWidgets.get(boundPagePath) == null){
//		        	PageRegistration newPage = new PageRegistration(widget.getPage(), sessionManagement);
//		        	wam.registerWebResource(newPage.getServletBase(), newPage);
//		            registeredWidgets.put(boundPagePath, newPage);
//		        }
//	        }
//        }
        @SuppressWarnings({ "unchecked", "rawtypes" })
		final ConfiguredWidget<?> confWidget = new ConfiguredWidget(widget, widgetSystemPath, widgetWebResourcePath, pr);
        registeredPages.get(boundPagePath).addWidget(confWidget);
//        pr.pageWidgets.put(widget.getId(), confWidget);
        return confWidget;
    }
    

    // register a session-specific widget // 
    @Override
    public ConfiguredWidget<?> registerWidgetNew(OgemaWidgetBase<?> widget, String boundAppPath,WebAccessManager wam, OgemaHttpRequest request) {
    	  final String widgetSystemPath = widget.getWidgetClass().getPackage().getName().replaceAll("\\.", "/");
          String widgetWebResourcePath = PATH_PREFIX + widgetSystemPath.substring( widgetSystemPath.lastIndexOf("/")+1 );
          String widgetServletPath = boundAppPath + "/" + widget.getId();    
          widgetWebResourcePath = widgetWebResourcePath.replaceAll("/+", "/");
          widgetServletPath = widgetServletPath.replaceAll("/+", "/");
          
          PageRegistrationI newPage = createPageRegistration(widget.getPage(), wam);
          String sessionId = request.getSessionId();
//          this.pageSpecificId.putIfAbsent(boundAppPath, pageSpecific);
          @SuppressWarnings({ "unchecked", "rawtypes" })
          final ConfiguredWidget<?> confWidget = new ConfiguredWidget(widget, widgetSystemPath, widgetWebResourcePath, sessionId, newPage);
          newPage.addWidget(confWidget); 
          
//          sessionWidgets.putIfAbsent(sessionId, new ArrayList<ConfiguredWidget>());
//          sessionWidgets.get(sessionId).add(confWidget);
//          wam.registerWebResource(widgetServletPath, widget);
          return confWidget;
    	
    }
  
    @Override
	public void unregisterWidget(String boundPagePath,OgemaWidgetBase<?> widget) {	
    	PageRegistrationI pr = registeredPages.get(boundPagePath);
    	if (pr == null)
    		return;
        pr.removeWidget(widget);
//    	ConcurrentMap<String, ConfiguredWidget> map = registeredWidgets.get(boundPagePath).pageWidgets;
//    	if (map != null)  {
//    		ConfiguredWidget cw = map.remove(widgetID);
//    		if (cw != null) {  		
//    			String sessionId = cw.getSessionId();
//    			if (sessionId != null && sessionWidgets.containsKey(sessionId)) {
//    				sessionWidgets.get(sessionId).remove(cw);
//    			}
//    			
//    		}
//    	}
//    	ConcurrentMap<String, WidgetGroupDerived> groups = widgetGroups.get(boundPagePath); // remove widget from all groups
//    	if (groups != null) {
//    		Iterator<WidgetGroupDerived> it = groups.values().iterator();
//    		while (it.hasNext()) {
//    			WidgetGroupImpl grp  = (WidgetGroupImpl) it.next();
//    			grp.removeWidget(widgetID);
//    		}
//    	}
     	
    }
    
	/** 
	 * Set session expiry time in ms. <br>
	 * -1: use default value 
	 */ 
    @Override
	public void setSessionExpiryTime(WidgetPage<?> page, long sessionExpiryTime) {
    	if (sessionExpiryTime > 0) expiryTimes.put(((WidgetPageBase<?>) page).getServletBase(), sessionExpiryTime);
    }
	
	/** 
	 * Set maximum nr. of parallel sessions. <br>
	 * -1: use default value
	 */
    @Override
	public void setMaxNrSessions(WidgetPage<?> page, int maxNrSessions) {
    	if (maxNrSessions > 0) this.maxNrSessions.put(((WidgetPageBase<?>) page).getServletBase(), maxNrSessions);
    }
    
    private String getPreferredLanguage(HttpServletRequest req) {
    	String user = UserLocaleUtil.getUserLoggedInBase(req.getSession());
    	String locl = UserLocaleUtil.getLocaleString(user, appMan);
    	if (locl != null) return locl;

    	locl = req.getHeader("Accept-Language");
    	if (locl == null)
    		locl = "en";
    	if (locl.contains(",")) {
    		int idx = locl.indexOf(",");
    		locl = locl.substring(0, idx);
    	}
    	return locl;
    }
     
    /**
     * Is called from the widget.js so the client can load the widget html/js/css data 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
        resp.setCharacterEncoding("UTF-8");
    	String initalWidgetInformation = req.getParameter("initialWidgetInformation");
    	String configParameters = req.getParameter("getConfigurationParameters");
    	String boundPagePath = req.getParameter("boundPagePath");
    	if (boundPagePath != null)
    		boundPagePath = getPageServletUrl(boundPagePath);
    	String groupId = req.getParameter("groupId");
    	String getGroup = req.getParameter("getGroup");
		OgemaHttpRequest ogReq = new OgemaHttpRequest(req, false);
		if(ogReq.getLocaleString() == null) {
	    	String locl = getPreferredLanguage(req);
	    	Locale inLocale = Locale.forLanguageTag(locl);
	    	if (inLocale == null) inLocale = Locale.ENGLISH; 
 			String inLocaleString = inLocale.getLanguage();
			ogReq = new OgemaHttpRequest(req, false, inLocaleString );
		}
		JSONObject result = new JSONObject();
		// FIXME
		if (logger.isTraceEnabled() && printmessages != null) {
			try {
				String referer = getAppPath(req.getHeader("Referer"));
		    	if (referer.equals(printmessages) || printmessages.equals("all")) {
		    		logger.trace("doGet for "+printmessages+":");
		    		Enumeration<?> headerNames = req.getHeaderNames();
		    		while(headerNames.hasMoreElements()) {
		    			String headerName = (String)headerNames.nextElement();
		    			logger.trace("Header Name - " + headerName + ", Value - " + req.getHeader(headerName));
		    		}
		    		Enumeration<?> params = req.getParameterNames(); 
		    		while(params.hasMoreElements()){
		    			String paramName = (String)params.nextElement();
		    			logger.trace("Parameter Name - "+paramName+", Value - "+req.getParameter(paramName));
		    		}
		    	}
			} catch (Exception e) { /* ignore */ }
		}
		
        if(initalWidgetInformation != null) {
        	boundPagePath = getPageServletUrl(initalWidgetInformation);
        	PageRegistrationI pageReg0 = registeredPages.get(boundPagePath);
        	if (pageReg0 == null) 
        		throw new IllegalArgumentException("Invalid page: " + boundPagePath);
        	PageRegistration page = pageReg0 instanceof PageRegistration ? (PageRegistration) pageReg0 : 
        			((LazyPageRegistration) pageReg0).getOrCreateRegistration(); // get or create?
			String sessionId;
			sessionId = ogReq.getSessionId();
			
        	// parameters
        	String zerothParam = "";
            int j=initalWidgetInformation.indexOf("?");
            Map<String,String[]> params = null;
            if (j > 0 && initalWidgetInformation.length() > j+1) {
            	zerothParam = initalWidgetInformation.substring(j+1);
            	params = getPageParameters(boundPagePath,sessionId,zerothParam,req);
            }
        	pageReg0.increaseAccessCount(params);
            
			sessionManagement.createNewSession(sessionId, page, params);
			
            if (expiryTimes.containsKey(boundPagePath)) {
            	sessionManagement.setExpiryTime(sessionId, expiryTimes.get(boundPagePath));
            }
        	page.initialize();
            for (ConfiguredWidget<?> confWidget: page.getWidgets(sessionId)) {
            	try {
            		OgemaWidgetBase<?> widget = confWidget.getWidget();
            		//only used rarely by apps
            		if (widget.isPostponeLoading())
            			continue;
            		if (widget instanceof InitWidget) 
            			((InitWidget) widget).init(ogReq);
            		widget.appendWidgetInformation(ogReq,result);
            		widget.updateDependentWidgets(ogReq);
               	} catch (Exception e) {
                	LoggerFactory.getLogger(JsBundleApp.class).error("Error retrieving widget data ",e);
               	}
            }
            result.append("pageInstance", ogReq.getPageInstanceId());
            resp.setContentType("application/json");
            resp.getWriter().write(result.toString());
            resp.setStatus(200);
            
        } else if (configParameters != null) {
        	/*String locl = req.getHeader("Accept-Language");
        	if (locl == null)
        		locl = "en";
        	if (locl.contains(",")) {
        		int idx = locl.indexOf(",");
        		locl = locl.substring(0, idx);
        	}*/
        	String locl = getPreferredLanguage(req);
        	Locale inLocale = Locale.forLanguageTag(locl);   // set initial locale to Browser locale
        	if (inLocale == null) inLocale = Locale.ENGLISH;
        	// System.out.println("  In Locale for config request " + inLocale.getLanguage() + ", locl = " + locl);
        	
        	JSONObject obj = new JSONObject();
        	JSONArray languages = new JSONArray();
        	Set<OgemaLocale> locales = OgemaLocale.getAllLocales();
        	Iterator<OgemaLocale> it = locales.iterator();
        	int counter = 0;
        	while(it.hasNext()) {
        		counter++;
        		OgemaLocale loc = it.next();
        		JSONObject langObj = loc.getJson(inLocale);
        		langObj.put("selected", loc.getLanguage().equals(locl));
        		langObj.put("value", counter);
        		languages.put(langObj);
        	}
        	obj.put("languages", languages);        	
        	resp.setContentType("application/json");
            resp.getWriter().write(obj.toString());
            resp.setStatus(200);
        }
        else if (groupId != null) {
        	//return full widget information
        	if (ogReq.getPageInstanceId().isEmpty()) {
        		LoggerFactory.getLogger(JsBundleApp.class).warn("Received a group request before pageInstanceId was set. Ignoring this.");
        		String reply = "{\"reply\":\"pageInstanceId not yet set\"}";
                resp.setContentType("application/json");
                resp.getWriter().write(reply.toString());
                resp.setStatus(400);
                return;
        	}
        	Set<OgemaWidgetBase<?>> widgets;
        	long pollingInterval;
        	// treat differently the case that all widgets are requested from the generic one
        	if (groupId.trim().toLowerCase().equals("all")) {
        		String sessionId = ogReq.getSessionId();
        		widgets = new LinkedHashSet<>(getPageRegistration(boundPagePath).getWidgetsBase(sessionId));
        		pollingInterval = -1; // TODO
        	}
        	else {
	        	PageRegistrationI pr = registeredPages.get(boundPagePath);
	        	WidgetGroupDerived group = pr.getGroup(groupId);
	        	if (group == null) {
	        		LoggerFactory.getLogger(JsBundleApp.class).debug("Widget group {} page {} not found.", groupId, boundPagePath);
	                resp.setStatus(400); // bad request
	                return;
	        	}
	        	widgets = new LinkedHashSet<OgemaWidgetBase<?>>(group.getWidgetsImpl());
	        	pollingInterval = group.getPollingInterval();
	        	result.put("polling", pollingInterval);
        	}
            Iterator<OgemaWidgetBase<?>> it = widgets.iterator();
        	while (it.hasNext()) {
        		OgemaWidgetBase<?> widget = it.next();
        		widget.appendWidgetInformation(ogReq,result); // executes onGET and appends widget info to ogReg
        	}
        	resp.setContentType("application/json");
            resp.getWriter().write(result.toString());
            resp.setStatus(200);
        }
        else if (getGroup != null) {
        	Set<String> widgetIds;
        	if (getGroup.trim().toLowerCase().equals("all")) {
        		widgetIds = getPageRegistration(boundPagePath).getWidgetIds(ogReq.getSessionId()); // FIXME filter out session-specific widgets not meant for this request 
        	}
        	else {
        		WidgetGroupDerived group = registeredPages.get(boundPagePath).getGroup(getGroup);
	        	if (group == null) {
	                resp.setStatus(400); // bad request
	                return;
	        	}
	        	widgetIds = new LinkedHashSet<String>();
	        	Set<OgemaWidgetBase<?>> widgets = group.getWidgetsImpl();
	        	Iterator<OgemaWidgetBase<?>> it = widgets.iterator();
	        	while (it.hasNext()) {
	        		OgemaWidgetBase<?> widget = it.next();
	        		widgetIds.add(widget.getId());
	        	}
        	}
        	JSONArray array = new JSONArray(widgetIds);
        	resp.setContentType("application/json");
            resp.getWriter().write(array.toString());
            resp.setStatus(200);
        }
        else {
        	//initial request containing boundPagePath parameter (potentially more)
            String sessionId = ogReq.getSessionId();
            final WidgetJsonConfig jsonConf = new WidgetJsonConfig();
            PageRegistration pr = getPageRegistration(boundPagePath);
            //look up the corresponding widgets bound under the given path
            if( pr != null){
            	//FIXME: Should not be necessary here anymore as it is done in the initialWidgetInformationRequest
            	pr.initialize();
                for(ConfiguredWidget<?> w : pr.getWidgets(sessionId)){
                    final String className = w.getWidget().getWidgetClass().getSimpleName();
                    jsonConf.addScriptResourcePath(w.getWidget().getId(), className, 
                            w.getWebResourcePath()+"/"+className+".html");
               }
            }
            //System.out.println("jsonConf.toJson(): " + jsonConf.toJson());
            resp.setContentType("application/json");
            resp.getWriter().write(jsonConf.toJson());
            resp.setStatus(200);
        }
    } catch(Throwable e) {
    	e.printStackTrace();
        resp.setContentType("text/plain"); // correct?
        e.printStackTrace(resp.getWriter());
        resp.setStatus(500);
    }
    }

//	@Override
//	public void registerApp(WidgetApp widgetApp) {
//		// TODO Auto-generated method stub
//	}
    
    /**
     * Note: this may return null, for instance for LazyWidgetPages which have not been
     * accessed yet. See {@link #getPageRegistrationInternal(String)} for a method to get 
     * the non-null registration object in this case
     * @param boundPagePath
     * @return
     */
    public PageRegistration getPageRegistration(String boundPagePath) {
    	final PageRegistrationI pageI = registeredPages.get(boundPagePath);
    	if (pageI instanceof PageRegistration)
    		return (PageRegistration) pageI;
    	if (pageI instanceof LazyPageRegistration)
    		return ((LazyPageRegistration) pageI).getRegistration();
    	return null;
    }
    
    public PageRegistrationI getPageRegistrationInternal(String boundPagePath) {
    	return registeredPages.get(boundPagePath);
    }
    
    
    public PageRegistrationI createPageRegistration(WidgetPageBase<?> page, WebAccessManager wam) {
    	String boundPagePath = page.getServletBase();
    	PageRegistrationI pr = registeredPages.get(boundPagePath);
    	if (pr == null) {
	    	synchronized (registeredPages) {
	    		pr = registeredPages.get(boundPagePath);
				if (pr == null) {
					if (page instanceof LazyPage<?>) {
						pr = new LazyPageRegistration((LazyPage) page, sessionManagement, accessManager, page.getApp());
					}
					else {
						pr = new PageRegistration(page, sessionManagement, accessManager);
					}
					wam.registerWebResource(boundPagePath, pr);
					registeredPages.put(boundPagePath, pr);
				}
			}
    	}
    	return pr;
    }
    
	public WidgetGroup registerWidgetGroup(WidgetPageBase<?> page, String groupId, Collection<OgemaWidget> widgets, WebAccessManager wam) throws IllegalArgumentException {
		PageRegistrationI pr = createPageRegistration(page,wam);
//		widgetGroups.putIfAbsent(baseUrl, new ConcurrentHashMap<String, WidgetGroupDerived>());
		if (pr.getGroup(groupId) != null)  throw new IllegalArgumentException("WidgetGroup with id " + groupId + " already exists");
//		ConcurrentMap<String,WidgetGroupDerived> groups = widgetGroups.get(baseUrl);		
//		if (groups.containsKey(groupId)) throw new IllegalArgumentException("WidgetGroup with id " + groupId + " already exists");
		WidgetGroupImpl wg = new WidgetGroupImpl(groupId, widgets);
		pr.addGroup(wg);
//		groups.put(groupId, wg);
		return wg;
	}
	
	public void update(OgemaWidgetBase<?> widget) {
		PageRegistration pr = getPageRegistration(widget.getPage().getServletBase());
		if (pr == null) return;
		ConfiguredWidget<?> cw = pr.getConfiguredWidget(widget);
		if (cw == null) return; // may be null, e.g. if it is a session widget..
		pr.updateOrder(cw);
	}
	
	public boolean removeWidgetGroup(WidgetPageBase<?> page, WidgetGroup group) {
		PageRegistrationI pr = registeredPages.get(page.getServletBase());
		if (pr == null)
			return false;
		return pr.removeGroup(group.getId()) != null;
	}
	
	public PageRegistrationI removePage(WidgetPageBase<?> page) {
		String boundPagePath = page.getServletBase();
//		widgetGroups.remove(boundPagePath);
		PageRegistrationI registration = registeredPages.remove(boundPagePath);
		if (registration != null)
			registration.close();
		// removes all cached sessions for this page
		sessionManagement.removePage(page);
		return registration;
	}
	
	//including final html-page
	private static String getPageServletUrl(String url) {
        int idx1 = url.lastIndexOf(".htm");
        int idx0 = url.indexOf("//");
        if (idx0 >= 0) { // format https://<URL>:<PORT>/path/to/page.html?params
        	url = url.substring(idx0 + 2, idx1);
        	url = url.substring(url.indexOf("/"));
        }
        else {  // format path/to/page.html?params
        	url = url.substring(0, idx1);
        	if (!url.startsWith("/"))
        		url = "/" + url;
        }
        return url;
//		String result = url.substring(url.indexOf("//")+2);
//        int idx = result.lastIndexOf(".htm");
//        result = result.substring(0, idx);
//        //remove hosts ip etc
//        return result.substring(result.indexOf("/"));
	}
	
	//without final html-page
	private static String getAppPath(String url) {
		int idx1 = url.lastIndexOf("/");
        int idx0 = url.indexOf("//");
        if (idx0 >= 0) { // format https://<URL>:<PORT>/path/to/page.html?params
        	url = url.substring(idx0 + 2, idx1);
        	url = url.substring(url.indexOf("/"));
        }
        else {  // format path/to/page.html?params
        	url = url.substring(0, idx1);
        	if (!url.startsWith("/"))
        		url = "/" + url;
        }
        return url;
		
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String,String[]> getPageParameters(String pagePath, String sessionId, String zerothParameter, HttpServletRequest req) {

		try {
			Map<String,String[]> params = extractPageParameters(zerothParameter);
			for (Map.Entry<String, String[]> entry : ((Set<Map.Entry<String, String[]>>) req.getParameterMap().entrySet())) {
				String key = entry.getKey();
				if (key.equals("initialWidgetInformation")) continue;
				String[] value = entry.getValue();
				params.put(key, value);
			}
			return params;
		} catch(Exception e) {
			LoggerFactory.getLogger(OgemaOsgiWidgetServiceImpl.class).error("Could not set page parameters for " + pagePath + ". Parameters: " + zerothParameter,e);
			return null;
		}
	}
	
	// actually not really required, since only one parameter will be considered part of initialWidgetInformation; others are separate entries in the HttpServletRequest
	private static Map<String,String[]> extractPageParameters(String parameters) {
		Map<String,String[]> params = new HashMap<String, String[]>();
		String[] paramStrings = parameters.split("&");
		for (String paramString : paramStrings) {
			String[] keyValPair = paramString.split("=");
			if (keyValPair.length != 2) {
				LoggerFactory.getLogger(WidgetAdminService.class).error("Invalid page parameter format: " + paramString);
				continue;
			}
			String[] values = keyValPair[1].split(",");
			params.put(keyValPair[0], values);
		}
		return params;
	}
	
	public Map<String,String[]> getPageParameters(WidgetPageBase<?> page, OgemaHttpRequest req) {
		return sessionManagement.getPageParameters(page.getServletBase(), req.getSessionId());
	}
	
	public void addApp(WidgetAppImpl app) {
		logger.info("Widget app being added " + app);
		synchronized (apps) {
			String url = app.appUrl();
			if (url == null) 
				throw new NullPointerException("URL is null for app " + app);
			if (apps.containsKey(url)) {
				// check if app is still active, otherwise simply replace the registration
				final WidgetAppImpl impl = apps.get(url);
				if (impl != null) {
					boolean active = true;
					try {
						active = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
	
							@Override
							public Boolean run() {
								try {
									final Field appId = WidgetAppImpl.class.getDeclaredField("appId");
									appId.setAccessible(true);
									final AppID id = (AppID) appId.get(impl);
									// FIXME new API method in OGEMA 2.1.4, currently using reflections for backwards compatibility
									final Method isActive = id.getClass().getMethod("isActive");
									return (Boolean) isActive.invoke(id);
								} catch (Exception e) {
									if (e instanceof RuntimeException)
										throw (RuntimeException) e;
									throw new RuntimeException(e);
								}
							}
							
						});
					} catch (RuntimeException e) { /* assume active == true */ }
					if (active)
						throw new IllegalStateException("There is already an app registered at the URL " + url);
				}
			}
			apps.put(app.appUrl(), app);
		}
	}
	
	public void removeApp(WidgetAppImpl app) {
		logger.info("Widget app being removed " + app);
		synchronized (apps) {
			apps.remove(app.appUrl());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, WidgetApp> getRegisteredApps() {
		synchronized(apps) {
			return new LinkedHashMap(apps);
		}
	}

	@Override
	public WidgetApp getApp(String baseUrl) {
		synchronized(apps) {
			return apps.get(baseUrl);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void sortWidgets(List<OgemaWidgetBase<?>> widgets) {
		if (widgets == null || widgets.size() < 2)
			return;
		PageRegistration pr = getPageRegistration(widgets.get(0).getPage().getServletBase());
		if (pr != null)
			pr.sortWidgets((List) widgets);
	}

//	private boolean usePageSpecificId(String boundPagePath) {
//		if (!registeredWidgets.containsKey(boundPagePath))
//			return false;
//		return registeredWidgets.get(boundPagePath).usePageSpecificId();
//	}
	
	@Override
	public int getNumberOfSessions(WidgetPage<?> page) {
		return sessionManagement.getNumberOfSessions(page);
	}
	
	@Override
	public int getNumberOfSessions(WidgetApp app) {
		return sessionManagement.getNumberOfSessions(app);
	}
	
	@Override
	public void deleteAllSessions() {
		sessionManagement.deleteAllSessions();
	}

	// FIXME what if the page is reregistered?
	@Override
	public void setPersistentAccessCount(WidgetPage<?> page, IntegerResource count) {
		final PageRegistrationI pr = getPageRegistrationInternal(((WidgetPageBase<?>) page).getServletBase());
		pr.setPersistentAccessCount(count);
	}
	
	// FIXME what if the page is reregistered?
	@Override
	public void setPersistentAccessCountForParameters(WidgetPage<?> page, IntegerResource count,
			Map<String, String[]> parameters) {
		final PageRegistrationI pr = getPageRegistrationInternal(((WidgetPageBase<?>) page).getServletBase());
		pr.setPersistentAccessCountForParameters(page, count, parameters);
	}

	@Override
	public int getAccessCount(WidgetPage<?> page) {
		final PageRegistrationI pr = getPageRegistrationInternal(((WidgetPageBase<?>) page).getServletBase());
		return pr.getAccessCount();
	}
	
}
