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
package de.iwes.widgets.api.extended.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.accesscontrol.AccessManager;
import org.ogema.accesscontrol.PermissionManager;
import org.ogema.accesscontrol.SessionAuth;
import org.ogema.core.administration.AdminApplication;
import org.ogema.core.application.AppID;
import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// functions mostly copied from framework-gui
public class AppsServlet extends HttpServlet {
 
	private static final long serialVersionUID = 1L;
	private final ApplicationManager appMan;
	private final PermissionManager permissionManager;
	private final AccessManager accessManager;
	// Map<User, visible apps> // FIXME caching should be done by the browser, but it seems
	// impossible to tell them to activate caching for individual targets 
	private final Cache<String,char[]> apps = CacheBuilder.newBuilder().softValues().build();
	// Map<User, last update time>
	private final Map<String,Long> lastUpdate = new ConcurrentHashMap<>();
	private final BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();
	private final BundleIcon defaultIcon = new BundleIcon(getClass().getResource(
			"/org/ogema/frameworkgui/gui/img/svg/appdefaultlogo.svg"), BundleIcon.IconType.SVG);
	
	public AppsServlet(ApplicationManager appMan, PermissionManager permMan) {
		this.appMan = appMan;
		this.permissionManager = permMan;
		this.accessManager = permMan.getAccessManager();
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String action = req.getParameter("action");
		if (action == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		switch (action) {
		case "listAll":
			final String user = getUser(req);
			if (user == null) {
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			// TODO check: does this apply to the current user only?
			resp.setHeader("Cache-Control", "max-age=60");
			Long lastUpdateUser = lastUpdate.get(user);
			if (lastUpdateUser != null && System.currentTimeMillis()-lastUpdateUser < 60000) {
				char[] response = apps.getIfPresent(user);
				if (response != null) {
					resp.getWriter().write(response);
					return;
				}
			}
			char[] response = null;
			synchronized (apps) { // FIXME avoid collision between users
				// check again
				if (lastUpdateUser != null && System.currentTimeMillis()-lastUpdateUser < 30000) 
					response = apps.getIfPresent(user);
				if (response == null) {
					response = getAppsInternal(user);
					lastUpdate.put(user, System.currentTimeMillis());
					apps.put(user, response);
				}
			}
			resp.getWriter().write(response);
			break;
		case "getIcon":
			int id = Integer.valueOf(req.getParameter("id"));
			BundleIcon.forBundle(ctx.getBundle(id), defaultIcon).writeIcon(resp);
			break;
		default:
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		
//		if ("/installedapps".equals(path)) {
//			if ("listAll".equals(action)) {
//				StringBuffer sb = controller.appsList2JSON(user);
//				String data = sb.toString();
//				printResponse(resp, data);
//			}
//			else if ("getIcon".equals(action)) {
//				int id = Integer.valueOf(req.getParameter("id"));
//				BundleIcon.forBundle(controller.getBundleContext().getBundle(id), defaultIcon).writeIcon(resp);
//			}
//		}
	}
	
	private static String getUser(HttpServletRequest req) {
		SessionAuth sesAuth = (SessionAuth) req.getSession().getAttribute("ogemaAuth");
		String user = null;
		if (sesAuth != null) // if security is disabled sesAuth is null
			user = sesAuth.getName();
		return user;
	}
	
	private char[] getAppsInternal(String user) {
//		final Map<String, JSONObject> map = new LinkedHashMap<>();
		final JSONArray result = new JSONArray();
		for (AdminApplication aa : appMan.getAdministrationManager().getAllApps()) {
			try {
				final AppID appId = aa.getID();
				if (!accessManager.isAppPermitted(user, appId)) {
					continue;
				}
				final String name = appId.getBundle().getSymbolicName();
//				String fileName = entry.getID().getBundle().getLocation();
//				int lastSeperator = fileName.lastIndexOf("/");
//				fileName = fileName.substring(lastSeperator + 1, fileName.length());

				final Bundle bundle = aa.getBundleRef();
				long id = bundle.getBundleId();
				final Map<String, String> metainfo = new HashMap<String, String>();

				Dictionary<String, String> bundleDictionary = bundle.getHeaders();
				Enumeration<String> dictionaryEnums = bundleDictionary.keys();
				while (dictionaryEnums.hasMoreElements()) {
					String key = dictionaryEnums.nextElement();
					String element = bundleDictionary.get(key);

					if (!("Import-Package".equals(key) || "Export-Package".equals(key))) {
						String formattedKey = key.replace('-', '_');
						metainfo.put(formattedKey, element);
					}
				}

//				AppsJsonGet singleApp = new AppsJsonGet();
				final JSONObject singleApp = new JSONObject();
				singleApp.put("name", name);
				singleApp.put("id", id);
				singleApp.put("metainfo", metainfo);
				
				//			StringBuffer jsonBuffer = webResourceTree2JSON((long) id, "#", null);
//				StringBuffer jsonBuffer = webResourceTree2JSON(aa);
				final JSONArray webResourcesApp = webResourceTree2JSON(aa);
//				String jsonString = jsonBuffer.toString();
//				List<AppsJsonWebResource> webResourcesApp = new ArrayList<AppsJsonWebResource>();

				//TODO: remove map/list type

				if (webResourcesApp == null) {
//				if (webResourcesApp.get(0).getAlias().equals("null")) {
//					singleApp.setHasWebResources(false);
					singleApp.put("hasWebResources", false);
					continue;
				}
				else {
					singleApp.put("hasWebResources", true);
					final JSONArray arr = new JSONArray();
					arr.put(webResourcesApp.getJSONObject(0).get("alias"));
					singleApp.put("webResourcePaths",arr);
//					singleApp.setHasWebResources(true);
//					singleApp.getWebResourcePaths().add(webResourcesApp.get(0).getAlias());

				}

				result.put(singleApp);
			} catch (Exception e) {
				continue;
			}
		}
		return result.toString().toCharArray();
	}
	
	public JSONArray webResourceTree2JSON(AdminApplication app) {
		final JSONArray obj = new JSONArray();
		
		AppID appid = app.getID();
		final String baseUrl = permissionManager.getWebAccess(appid).getStartUrl();
		if (baseUrl == null)
			return null;
//		Map<String, String> entries = new HashMap<String, String>();
//		entries.put(baseUrl, appid.getIDString());
//		Set<Map.Entry<String, String>> entrySet = entries.entrySet();
		final JSONObject inner = new JSONObject();
		inner.put("text", baseUrl);
		inner.put("id", appid.getIDString());
		inner.put("alias", baseUrl);
		inner.put("children", true);
		obj.put(inner);
		return obj;
		
//		sb.append('[');
//		for (Map.Entry<String, String> e : entrySet) {
//			String key = e.getKey();
//			String name = e.getValue();
//			if (index++ != 0) {
//				sb.append(',');
//			}
//			sb.append("{\"text\":\"");
//			sb.append(key);
//			sb.append('"');
//			sb.append(',');
//			sb.append("\"id\":\"");
//			sb.append(name);
//			sb.append('"');
//			sb.append(',');
//			sb.append("\"alias\":\"");
//			sb.append(key);
//			sb.append('"');
//			sb.append(',');
//			sb.append("\"children\":true");
//			sb.append('}');
//		}
//		sb.append(']');
//		return sb;
	}

	
	
}
