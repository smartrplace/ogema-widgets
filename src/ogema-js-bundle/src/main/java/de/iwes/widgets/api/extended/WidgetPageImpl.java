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

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.impl.HtmlLibrary;
import de.iwes.widgets.api.extended.impl.MenuData;
import de.iwes.widgets.api.extended.impl.HtmlLibrary.LibType;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.api.widgets.navigation.NavbarType;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.start.JsBundleApp;

public class WidgetPageImpl<S extends LocaleDictionary> extends WidgetPageBase<S> {

	private final static boolean DEBUG;

	static {
		DEBUG = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

			@Override
			public Boolean run() {
				return Boolean.getBoolean("org.ogema.widgets.debug");
			}
		});

	}


	// we need not add these widget scripts to the html, since they are preloaded in minified form anyway (unless DEBUG is true)
	private static final Map<String,String> PRELOADED_WIDGET_SCRIPTS;

	static {
		PRELOADED_WIDGET_SCRIPTS = new HashMap<>();
		PRELOADED_WIDGET_SCRIPTS.put("Alert", "/ogema/widget/alert/Alert.js");
		PRELOADED_WIDGET_SCRIPTS.put("Accordion", "/ogema/widget/accordion/Accordion.js");
		PRELOADED_WIDGET_SCRIPTS.put("EmptyWidget", "/ogema/widget/emptywidget/EmptyWidget.js");
		PRELOADED_WIDGET_SCRIPTS.put("Button", "/ogema/widget/button/Button.js");
		PRELOADED_WIDGET_SCRIPTS.put("Checkbox", "/ogema/widget/checkbox/Checkbox.js");
		PRELOADED_WIDGET_SCRIPTS.put("Dropdown", "/ogema/widget/dropdown/Dropdown.js");
		PRELOADED_WIDGET_SCRIPTS.put("Label", "/ogema/widget/label/Label.js");
		PRELOADED_WIDGET_SCRIPTS.put("TextField", "/ogema/widget/textfield/TextField.js");
		PRELOADED_WIDGET_SCRIPTS.put("Flexbox", "/ogema/widget/html5/Flexbox.js");
		PRELOADED_WIDGET_SCRIPTS.put("Icon", "/ogema/widget/icon/Icon.js");
		PRELOADED_WIDGET_SCRIPTS.put("Multiselect", "/ogema/widget/multiselect/Multiselect.js");
		PRELOADED_WIDGET_SCRIPTS.put("Popup", "/ogema/widget/popup/Popup.js");
	}

	private HttpServlet servlet;
	private String title = null;
	private final PageSnippet rootWidget;
	private boolean fullScreenWidth = true;
	private final MenuConfiguration menuConfiguration = new MenuConfiguration();
	private final String version;
	private final String address;
	private final boolean doLoad;
	private final Set<HtmlLibrary> externalLibs = new LinkedHashSet<HtmlLibrary>();
	private boolean showOverlay = false;
	private String backgroundImg = null;
	// FIXME we cannot buffer the full page, because individual OTP must be injected
//	private volatile SoftReference<char[]> htmlBuffer = new SoftReference<char[]>(null);

	/***** Constructors *****/

	public WidgetPageImpl(WidgetAppImpl app) {
		this(app,"index.html");
	}

	public WidgetPageImpl(WidgetAppImpl app, boolean setAsStartPage) {
		this(app,"index.html", setAsStartPage);
	}

	/**
	 * @param url
	 * 		the path to the widget page; note that this path must not yet be registered
	 * 		with the WebAccessManager or HttpService. It is therefore not possible at the moment
	 * 		to mix WidgetPageSimple with ordinary {@link WidgetPageBase}s within one {@link WidgetAppImpl}.
	 */
	public WidgetPageImpl(WidgetAppImpl app, String url) {
		this(app, url, false);
	}

	public WidgetPageImpl(WidgetAppImpl app, String url, boolean setAsStartPage) {
		super(app, url, setAsStartPage);
		this.rootWidget = createRootWidget();
		this.version = app.appVersion;
		// FIXME load only in WidgetApp?
		PrivilegedAction<String> getSystemPropLoadExternal = new PrivilegedAction<String>() {

			@Override
			public String run() {
				return System.getProperty("org.ogema.ui.externalLibsAddress");
			}
		};
		String addressAux = AccessController.doPrivileged(getSystemPropLoadExternal);
		doLoad = (addressAux != null && !addressAux.isEmpty());
		if (doLoad) {
			if (!addressAux.endsWith("/"))
				addressAux = addressAux + "/";
			address = addressAux + version;
		}
		else
			address = "";
	}

	protected PageSnippet createRootWidget() {
		return new PageSnippet(this, "rootWidget",true);
	}

	/***** public methods *****/

	public String getTitle() {
		return title;
	}

	@Override
	public WidgetPageImpl<S> setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public WidgetPageImpl<S> append (String html) {
		rootWidget.append(html,null);
		return this;
	}

	@Override
	public WidgetPageImpl<S> append (HtmlItem item) {
		rootWidget.append(item,null);
		return this;
	}

	@Override
	public WidgetPageImpl<S> append(OgemaWidget widget) {
		if (!widget.getPage().equals(this))
			throw new IllegalArgumentException("Cannot append a widget from another page");
		rootWidget.append(widget, null);
		return this;
	}

	@Override
	public WidgetPageImpl<S> linebreak() {
		rootWidget.linebreak(null);
		return this;
	}

	public PageSnippet getRootWidget() {
		return rootWidget;
	}

	@Override
	public MenuConfiguration getMenuConfiguration() {
		return menuConfiguration;
	}

//	/**
//	 * Change menu settings, e.g. to hide certain submenus. Parameter may be null,
//	 * then no menu is shown (equivalent to menuConfiguration.setMenuVisible(false)).
//	 */
//	public void setMenuConfiguration(MenuConfiguration menuConfiguration) {
//		this.menuConfiguration = menuConfiguration;
//	}

	public boolean isFullScreenWidth() {
		return fullScreenWidth;
	}

	// FIXME move to interface?
	public void setFullScreenWidth(boolean fullScreenWidth) {
		this.fullScreenWidth = fullScreenWidth;
	}

	public String getBackgroundImg() {
//		return rootWidget.getBackgroundImg(null);
		return backgroundImg;
	}

	@Override
	public void setBackgroundImg(String backgroundImg) {
//		rootWidget.setBackgroundImg(backgroundImg, null);
		this.backgroundImg = backgroundImg;
	}

	/***** internal methods *****/

	@Override
	protected void initBeforeRegistration() {
		this.servlet = new WidgetPageServlet(this);
	}

	protected HttpServlet getServlet() {
		return servlet;
	}

	@Override
	protected void registerLibrary(HtmlLibrary lib) {
		if (lib.getType() == LibType.CSS) return; // FIXME CSS not yet supported... how to avoid loading it twice?
		if (DEBUG || !PRELOADED_WIDGET_SCRIPTS.containsKey(lib.getIdentifier()))
			externalLibs.add(lib);
	}

	private final static class WidgetPageServlet extends HttpServlet {

		private final WidgetPageImpl<?> page;
		private static final long serialVersionUID = 1L;

		public WidgetPageServlet(WidgetPageImpl<?> page) {
			this.page=page;
		}

		@Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			if (req.getParameterMap().containsKey("menu")) {
				JSONObject response = MenuData.getMenuData(new OgemaHttpRequest(req, false));
	            resp.setContentType("application/json");
	            resp.getWriter().write(response.toString());
	            resp.setStatus(200);
				return;
			}
			char[] html = page.getInitHtml(req);
//			System.out.println("  GET request to WidgetPageSimple; returning " + html);
            resp.setContentType("text/html");
            resp.getWriter().write(html);
            resp.setStatus(200);
		}

	}

	private char[] getInitHtml(HttpServletRequest req) {
//		char[] arr = htmlBuffer.get();
//		if (arr != null)
//			return arr;
		char[] arr;
		final StringBuilder html = new StringBuilder();
		final Set<HtmlLibrary> libs = getLibraries();
//		libs.addAll(externalLibs);
		html.append("<!DOCTYPE html><html><head>");
		String[] otup = null;
		try {
			if (staticRegistration == null) 
				throw new NoClassDefFoundError(); // go to catch clause
			otup = ((org.ogema.webadmin.AdminWebAccessManager.StaticRegistration) staticRegistration).generateOneTimePwd(req);
		} catch (NoClassDefFoundError | ClassCastException e) { // fallback for OGEMA version < 2.1.2
//				otup = app.wam.registerStaticResource(servlet, req);
			try {
				final Class<?>  wamClass = app.wam.getClass();
				final Method register = wamClass.getMethod("registerStaticResource", HttpServlet.class, HttpServletRequest.class);
				otup = (String[]) register.invoke(app.wam, servlet, req);
			} catch (Exception ee) {
				LoggerFactory.getLogger(JsBundleApp.class).warn("Could not generate one-time-password for page {}",getFullUrl(),ee);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(JsBundleApp.class).warn("Could not generate one-time-password for page {}",getFullUrl(),e);
		}
		if (otup != null)
			html.append("<script>var otusr='" + otup[0] + "';var otpwd='" + otup[1] + "';</script>");
		final String otu = otup !=null ? otup[0]: null;
		final String otp = otup != null ? otup[1] : null;

		html.append("<script>var ogema={};ogema.dynamicHtml=true;ogema.widgetLoader={};ogema.widgetLoader.scriptsLoadedInit=[];ogema.showOverlay=").append(showOverlay)
			.append(";var externalAddress='").append(address).append("';</script>");
		String icon = getIconPath(otu,otp);
		if (icon != null && !icon.trim().isEmpty())
			html.append("<link rel=\"icon\" href=\"").append(icon).append("\">");
		html.append(getHtmlTitle());
		appendLibsHtml(html, libs,true);  // loads early libs
		html.append("</head>").append(getBodyOpeningTag()).append(getContainerOpenDiv());
		if (showMenu()) getGenericMenuHtml(html);
		html.append(getContainerOpenDiv()).append("<div class=\"row\"><div class=\"col col-sm-12\"><div class=\"ogema-widget\" id=\"rootWidget\"></div></div></div></div></div><br>"); // FIXME get rid of those divs!
		if (showMenu()) html.append(modalHtml);
		if (!DEBUG) {
			html.append("<script>");
			for (String url : PRELOADED_WIDGET_SCRIPTS.values()) {
				html.append("ogema.widgetLoader.scriptsLoadedInit.push('").append(url).append("');");
			}
			html.append("</script>");
		}

		appendLibsHtml(html, externalLibs,false);  // loads external libs
		html.append("<script>ogema.widgetLoader.init();</script>"); // run widgetLoader
		appendLibsHtml(html,libs,false);  // loads late libs;
		html.append("</body></html>");

		final int length = html.length();
		arr = new char[length];
		html.getChars(0, length, arr, 0);
//		htmlBuffer = new SoftReference<char[]>(arr);
		return arr;
	}

	private String getHtmlTitle() {
		String ttl;
		if (title == null)
			ttl = app.appId.getBundle().getHeaders().get("Bundle-Name");
		else
			ttl = title;
		if (ttl == null)
			ttl = "OGEMA Page";
		return "<title>" + ttl +"</title>";
	}

	// TODO default: OGEMA icon, otherwise: app specific
	private String getIconPath(String otu, String otp) {
		long bundleId = app.appId.getBundle().getBundleId();
//		return "https://de.wikipedia.org/favicon.ico";
		// FIXME favicon should be very small... here we use the app logo
		// FIXME svg favicons have very limited browser support
		// TODO provide default location for app's favicon, and a common servlet -> ogema-js-bundle (icon + favicon, no permission checks)
		final StringBuilder sb = new StringBuilder();
		sb.append("/ogema/widget/apps?action=getIcon&id=").append(bundleId);
		if (otu != null)
			sb.append("&user=").append(otu).append("&pw=").append(otp);
		return sb.toString();
	}

	private String getBodyOpeningTag() {
		if (backgroundImg == null)
			return "<body>";
		else
			return "<body style=\"background-image:url('" + backgroundImg + "')\">";
	}

//	private final PrivilegedAction<String> getSystemPropDebug = new PrivilegedAction<String>() {
//
//		@Override
//		public String run() {
//			return System.getProperty("org.ogema.webresourcemanager.RuntimeConfigurationType","DEPLOYMENT");
//		}
//	};

	private void appendLibsHtml(final StringBuilder html, Set<HtmlLibrary> libs, boolean loadEarly) {
//		int nrScripts = getNrScripts(map);
		Iterator<HtmlLibrary> it = libs.iterator();
		while (it.hasNext()) {
			HtmlLibrary lib = it.next();
			if (lib.isLoadEarly() != loadEarly) continue;
			LibType type = lib.getType();
/*			if (type == LibType.JS && doLoad) {
				html.append(addScriptWithErrorHandling(address,lib.getPath(), lib.getIdentifier()));
			} */
			if (type == LibType.JS)
				addScriptWithErrorHandling(html, address, lib.getPath(), lib.getIdentifier(), loadEarly);
			else {
				html.append(lib.getHtmlString());
			}
		}

/*		html.append("<script src=\"/ogema/jslib/jquery/jquery-1.11.1.min.js\"></script>"
		   + "<link rel=\"stylesheet\" href=\"/ogema/jslib/bootstrap/css/bootstrap.min.css\">"
		   + "<script src=\"/ogema/jslib/bootstrap/bootstrap.min.js\"></script>"
		   + (showMenu() ? "<script src=\"/ogema/jslib/jquery/jquery.ddslick.min.js\"></script>" : "")
           + "<script src=\"/ogema/jslib/GenericWidget.js\"></script>"
           + "<script src=\"/ogema/jslib/WidgetGroups.js\"></script>"
           + "<script src=\"/ogema/jslib/widgetLoader.js\"></script>"
           + "<link rel=\"stylesheet\" href=\"/ogema/jslib/widgets.css\">"
           + "<meta charset=\"UTF-8\"/>"
           + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>"
		   + "<title>" + title + "</title>"); */
	}



	/**
	 * We assume that all libraries are available at localhost and on the external server (if applicable)
	 * under the same relative path; relative to OGEMA base url on localhost, and relative to the server URL specified
	 * by the System property org.ogema.ui.loadLibsAddress<br>
	 * Note that the order of entries in the map is important and must not be changed
	 */
	// TODO allow for version configuration
	protected Set<HtmlLibrary> getLibraries() {
		Set<HtmlLibrary> libs = new LinkedHashSet<HtmlLibrary>();
		libs.add(new HtmlLibrary(LibType.JS,"jQuery", "/ogema/jslib/jquery/jquery-3.2.1.min.js",true));
		libs.add(new HtmlLibrary(LibType.CSS,"", "/ogema/jslib/bootstrap/3.3.7/css/bootstrap.min.css",true));
		libs.add(new HtmlLibrary(LibType.JS,"jQuery.fn.modal", "/ogema/jslib/bootstrap/3.3.7/js/bootstrap.min.js",true));

		if (DEBUG) {
			libs.add(new HtmlLibrary(LibType.JS,"GenericWidget", "/ogema/jslib/GenericWidget.js",true));
			libs.add(new HtmlLibrary(LibType.JS,"WidgetGroups", "/ogema/jslib/WidgetGroups.js",true));
			libs.add(new HtmlLibrary(LibType.JS,"ogema.reloadWidgets", "/ogema/jslib/widgetLoader.js", true));
		} else {
			libs.add(new HtmlLibrary(LibType.JS,"widgetsCompressed", "/ogema/jslib/minified/widgets-base.js",true));
			// preloads some commonly used widgets JS; in debug mode these are added to the external libs instead
			libs.add(new HtmlLibrary(LibType.JS,"widgetsCollectionBase", "/ogema/widget/minified/widget-collection.js",true));
		}
		// in between, external libs will be loaded
		libs.add(new HtmlLibrary(LibType.CSS,"", "/ogema/jslib/widgets.css"));
		if (showMenu()) {
			if (DEBUG) {
				libs.add(new HtmlLibrary(LibType.JS, "jQuery.fn.ddslick", "/ogema/jslib/jquery/jquery.ddslick.min.js"));
			    libs.add(new HtmlLibrary(LibType.JS,"ogema.menuIdentifier", "/ogema/jslib/menuInitializer.js"));  // initialize menu
			} else
				libs.add(new HtmlLibrary(LibType.JS,"ogema.menuIdentifier", "/ogema/jslib/minified/menu.js"));
		}
		if (showOverlay) {
			libs.add(new HtmlLibrary(LibType.CSS,"", "/ogema/jslib/font-awesome/css/font-awesome-4.6.2.min.css"));
			libs.add(new HtmlLibrary(LibType.JS,"jQuery.fn.overlay", "/ogema/jslib/jquery/jquery.easy-overlay.js")); // $("body").overlay(); $.overlayout
		}
		return libs;
	}

	/**** Javascript functions ****/



	/**
	 * Note that scripts from an external server can only be included in the initial html file, but
	 * cannot be dynamically loaded, due to CORS restriction. For this reason, we have to include the server
	 * version of the scripts in the initial HTMl, and fall back to the scripts from the OGEMA gateway in case
	 * there is an error.
	 */

	private static void addScriptWithErrorHandling(StringBuilder sb, String prefix, String path, String identifier, boolean doLoad) {
		sb.append("<script src='" + prefix + path +"'></script><script>");
		if (doLoad)
			sb.append("if (typeof " + identifier +" === \"undefined\"){"
				+	"document.write(unescape(\"%3Cscript src='" + path + "' type='text/javascript'%3E%3C/script%3E\"));"
				+ "}");
		sb.append("ogema.widgetLoader.scriptsLoadedInit.push('" + path + "');</script>");
	}

// this version looks cleaner (respectively could be simplified to look cleaner) but cannot ensure consistent loading order of scripts in fallback case
/*	private int counter  = -1;

    private String addScriptWithErrorHandling(String path,String prefix) {
	counter++;
	return "<script src='" + prefix + path +"' onload=addLoadedScripts(" +counter+ ") onerror=addFailedScript(" + counter +",'" + path + "')></script>";
	}

	private int getNrScripts(Map<String, HtmlLibrary> map) {
		int counter = 0;
		Iterator<HtmlLibrary> it  =map.values().iterator();
		while (it.hasNext()) {
			HtmlLibrary lib = it.next();
			if (lib.getType() == LibType.JS) {
				counter++;
			}
		}
		return counter;
	}

	private static String addScript() {
		return "<script>"
				+ "var failedScripts = {};"
				+ "var scriptsLoaded = [];"
				+ "function addScript(path){  "
				+ "var targetScript = document.createElement(\"script\");"
				+ "document.head.appendChild(targetScript); "
				+ "targetScript.src = path;"
				+ "}"
				+ "function loadFailedScripts() {"
				+ "console.log('loadFailedScripts started, failed:',failedScripts);"
				+ "var numbers=[];"
				+ "Object.keys(failedScripts).forEach(function(id){"
				+ "numbers.push(id);"
				+ "});"
				+ "numbers.sort();"
				+ "var dv = document.createElement('div');"
				+ "dv.id=\"newScripts\";"
				+ "for (var i=0;i<numbers.length;i++){"
				+ "var script=document.createElement(\"script\");"
				+ "dv.appendChild(script);"
				+ "script.src=failedScripts[numbers[i]];"
				+ "}"
				+ "console.log('div: ',dv);"
				+ "document.head.appendChild(dv);"
				+ "}"
				+ "function checkScriptsComplete() {"
				+ "var length=scriptsLoaded.length + Object.keys(failedScripts).length;"
				+ "console.log('Checking completion; length vs required length',length,nrScripts);"
				+ "if (length == nrScripts) {"
				+ "loadFailedScripts();"
				+ "}"
				+ "}"
				+ "function addFailedScript(nr,path) {"
				+ "failedScripts['idx' + nr] = path;"
				+ "checkScriptsComplete();"
				+ "}"
				+ "function addLoadedScripts(nr) {"
				+ "scriptsLoaded.push(nr);"
				+ "checkScriptsComplete();"
				+ "}"
				+ "</script>";
	}
	*/


	protected void getGenericMenuHtml(final StringBuilder sb) {
/*		sb.append("<div class=\"row headerBar\">");
			sb.append("<div class=\"col col-md-1 headerMenuItem\">");
				sb.append("<img id=\"menuLogo\" src=\"/ogema/img/svg/ogema.svg\" style=\"width:100%;\"></img>");
			sb.append("</div>");
			sb.append("<div class=\"col col-md-3 headerMenuItem\">");
				sb.append("<div class=\"headerMenuItem\" id=\"appsSelector\"></div>");
			sb.append("</div>");
			sb.append("<div class=\"col col-md-3 headerMenuItem\"></div>");
			sb.append("<div class=\"col col-md-3 headerMenuItem\"></div>");
			sb.append("<div class=\"col col-md-2 headerMenuItem\">");
				sb.append("<div class=\"headerMenuItem\" id=\"languageSelector\"></div>");
			sb.append("</div>");
		sb.append("</div>"); // close row div  */
//		sb.append(getContainerOpenDiv());
//		sb.append("<div style=\"height:80px;\">");
		sb.append("<nav class=\"navbar ");
		appendNavbarClass(sb);
		sb.append("\">");
		sb.append(getContainerOpenDiv());
		sb.append("<div class=\"navbar-header\">");
		sb.append("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#myNavbar\">");
		sb.append("<span class=\"icon-bar\"></span>");
		sb.append("<span class=\"icon-bar\"></span>");
		sb.append("<span class=\"icon-bar\"></span>");
		sb.append("</button>");
		sb.append("<img class=\"navbar-brand-ogema\" id=\"menuLogo\" src=\"" + menuConfiguration.getIconPath() + "\"></img>");
		sb.append("</div>");
		sb.append("<div class=\"collapse navbar-collapse\" id=\"myNavbar\">");
		sb.append("<ul class=\"nav navbar-nav\">");
//		sb.append("<li><a href=\"#\">Home</a></li>");
		try {
			if (showCustomMenu()) {
				sb.append("<li class=\"dropdown menuItem\"><div id=\"customNavigation\">");
				appendCustomNavigation(sb);
				sb.append("</div></li>");
			}
		} catch (Exception e) { LoggerFactory.getLogger(JsBundleApp.class).error("Could not create custom menu: " + e); }
		if (showAppNavigation())
			sb.append("<li style=\"width:300px; padding:17px 0px 0px 0px;\"><div id=\"appsSelector\"></div></li>");
//		sb.append("<li><a href=\"#\">Page 2</a></li>");
//		sb.append("<li><a href=\"#\">Page 3</a></li>");
		sb.append("</ul>");
		if (showLanguageSelection() || showLogoutBtn()) {
			sb.append("<ul class=\"nav navbar-nav navbar-right\">");
			if (showMessages())
				sb.append("<li><div class=\"messageBox\">"
						+ "<a id=\"messageAppLinkBox\" class=\"linkBox\" target=\"blank\"><span id=\"messageGlyphicon\" class=\"glyphicon glyphicon-envelope messageIcon inactive\"></span></a>"
						+ "<div id=\"msgNrBox\"></div>"
						+ "<textarea class=\"messageText inactive\" cols=\"20\" rows=\"2\" readonly=\"readonly\"></textarea></div>"
						+ "<div id=\"msgBox\" class=\"fadeInBox\"><a id=\"msgLink\" href=\"#\" target=\"blank\">"
						+ "<h3 id=\"messageTitle\" ></h3>"
						+ "<small id=\"sendingAppId\"></small><br><br>"
						+ "<span id=\"messageFullText\"></span>"
						+ "</a></div></li>");
			if (showLanguageSelection())
				sb.append("<li style=\"width:200px; padding:5px 0px 0px 0px;\"><div id=\"languageSelector\"></div></li>");
			// TODO functionality
			if (showLogoutBtn())
				sb.append("<li onclick=\"$('#menu-modal').modal('show');\" style=\"cursor:pointer;\"><a id=\"logoutField\"><span class=\"glyphicon glyphicon-log-out\"></span> Logout</a></li>");
			sb.append("</ul>");
		}
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</nav>");
//		sb.append("</div>"); // outer container
//		sb.append("<script src=\"/ogema/jslib/menuInitializer.js\"></script>");  // initialize menu
	}

	// TODO ensure modal lib is loaded ?
	// TODO localisation
	// TODO reuse modal for different purposes than logout?
	protected final static String modalHtml =
		"<div class=\"modal fade\" id=\"menu-modal\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"ModalLabel\" aria-hidden=\"true\">" +
            "<div id=\"dialog\" class=\"modal-dialog modal-sm\">" +
                "<div class=\"modal-content\">" +
                    "<div class=\"modal-header\">" +
                        "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>" +
                        "<h4 class=\"modal-title\" id=\"ModalLabel\">Logout</h4>" +
                        "<div id=\"ModalHeader\"></div>" +
                    "</div>" +
                    "<div class=\"modal-body\" id=\"ModalBody\">Do you really want to logout from this system?</div>" +
                    "<div class=\"modal-footer\" id=\"ModalFooter\">" +
                    	"<button class=\"btn btn-danger\" id=\"logoutBtn\" onclick=\"ogema.logout();\">Logout</button>" +
                    	"<button class=\"btn btn-primary\" id=\"cancelBtn\" onclick=\"$('#menu-modal').modal('hide');\">Close</button>" +
                    "</div>" +
                "</div>" +
            "</div>" +
        "</div>";

	private String checkOnlineStatus() { // FIXME remove?
		// might replace this by Offline.js library's Offline.check() or similar if it does not work reliably
		return "<script>function getOnlineStatus(){return window.navigator.onLine;}</script>";
	}

	private boolean showMenu() {
		if (menuConfiguration == null) return false;
		return menuConfiguration.isMenuVisible();
	}

	private boolean showAppNavigation() {
		if (menuConfiguration == null) return false;
		return menuConfiguration.isNavigationVisible();
	}

	private boolean showLanguageSelection() {
		if (menuConfiguration == null) return false;
		return menuConfiguration.isLanguageSelectionVisible();
	}

	private boolean showLogoutBtn() {
		if (menuConfiguration == null) return false;
		return menuConfiguration.isShowLogoutBtn();
	}

	private boolean showCustomMenu() {
		if (menuConfiguration == null) return false;
		return (menuConfiguration.getCustomNavigation() != null);
	}

	private boolean showMessages() {
		if (menuConfiguration == null) return false;
		return menuConfiguration.isShowMessages();
	}

	private void appendCustomNavigation(final StringBuilder sb) throws NullPointerException {

		NavigationMenu menu = menuConfiguration.getCustomNavigation();
		String glyphClass = menu.getGlyphicon();

		Map<String,String> entries = menu.getEntries();

		sb.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">");
		if (glyphClass != null && !glyphClass.isEmpty()) {
			sb.append("<span class=\"").append(glyphClass).append("\"></span>");
		}
		sb.append(menu.getTitle()).append(" <span class=\"caret\"></span></a>");
		sb.append("<ul class=\"dropdown-menu\">");
		Iterator<Entry<String,String>> it = entries.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,String> entry  =it.next();
			sb.append(" <li><a href=\"").append(entry.getValue()).append("\">").append(entry.getKey()).append("</a></li>");
		}
		sb.append("</ul>");
	}


	private void appendNavbarClass(final StringBuilder sb) {
		if (menuConfiguration == null) return;
		Set<NavbarType> types = menuConfiguration.getTypes();
		if (types.isEmpty()) {
			sb.append(NavbarType.DEFAULT.getClassname());
			return;
		}
		Iterator<NavbarType> it = types.iterator();
		while (it.hasNext()) {
			NavbarType tp = it.next();
			sb.append(tp.getClassname() + " ");
		}
	}

	// FIXME why not allow for non-full-screen?
	private final static String getContainerOpenDiv()  {
//		return "<div class=\"container" + (fullScreenWidth ? "-fluid" : "") + "\">";
		return "<div class=\"container-fluid\">";
	}

	@Override
	public void showOverlay(boolean show) {
		this.showOverlay = show;
	}

}
