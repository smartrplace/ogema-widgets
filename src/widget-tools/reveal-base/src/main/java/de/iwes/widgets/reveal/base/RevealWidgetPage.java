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
package de.iwes.widgets.reveal.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ogema.core.security.WebAccessManager;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.HtmlLibrary;
import de.iwes.widgets.api.extended.ServletBasedWidgetPage;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.HtmlLibrary.LibType;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.start.JsBundleApp;

/*
 * TODO
 *  - refactor: common base class for WidgetPageImpl and this?
 */
public class RevealWidgetPage<S extends LocaleDictionary> extends WidgetPageBase<S> implements ServletBasedWidgetPage<S> {
	
	final static String ID_PREFIX = "slide_";
	private final MenuConfiguration menuConfiguration = new MenuConfiguration();
	// regarding synchronization: for the time being we expect the slides to be static... i.e. they are 
	// generated in the same thread as the page, and will not be changed any more
	final LinkedHashMap<String, PageSnippet> mainRowSlides = new LinkedHashMap<>();
	// column slides Map<column id (mainRowSlides), column slides>
	Map<String, ColumnSlides> columnSlides; // instantiated when needed
	final Set<HtmlLibrary> externalLibs = new LinkedHashSet<HtmlLibrary>();
	private final HttpServlet servlet;
	private WidgetGroup allWidgets = null;
	/**
	 * Of type
	 *  org.ogema.webadmin.AdminWebAccessManager.StaticRegistration
	 */
	// quasi-final
	volatile Object staticRegistration;
	
	public RevealWidgetPage(WidgetAppImpl app, String startHtml) {
		this(app, startHtml, false);
	}

	public RevealWidgetPage(WidgetAppImpl app, String startHtml, boolean setAsStartPage) {
		super(app, startHtml, setAsStartPage);
		this.servlet = new RevealServlet(this);
	}
	
	@Override
	public HttpServlet getServlet() {
		return servlet;
	}
	
	@Override
	public MenuConfiguration getMenuConfiguration() {
		return menuConfiguration;
	}
	
	@Override
	public void registerLibrary(HtmlLibrary lib) {
		if (lib.getType() == LibType.CSS) return; // FIXME CSS not yet supported... how to avoid loading it twice?
		if (DEBUG || !PRELOADED_WIDGET_SCRIPTS.containsKey(lib.getIdentifier()))
			externalLibs.add(lib);
	}
	
	/**
	 * Add a slide with arbitrary id (will be generated)
	 * @return
	 */
	public PageSnippetI addSlide() {
		return addSlide(null);
	}
	
	/**
	 * Add a slide with a specific id
	 * @param id
	 * @return
	 * @throws IllegalArgumentException if a slide with the given id exists.
	 */
	public synchronized PageSnippetI addSlide(String id) {
		if (id == null)
			id = generateId();
		else if (slideExists(id))
			throw new IllegalArgumentException("Slide with id " + id + " already exists");
		id = mapId(id);
		final PageSnippet slide = new PageSnippet(this, id, true);
		mainRowSlides.put(id, slide);
		return slide;
	}
	
	private static String mapId(String id) {
		if (id.startsWith(ID_PREFIX))
			return id;
		return ID_PREFIX + ResourceUtils.getValidResourceName(id);
	}
	
	public void setColumnsTemplate(final PageSnippetI slide, final ColumnTemplate template) {
		Objects.requireNonNull(template);
		Objects.requireNonNull(slide);
		synchronized (this) {
			final String id = mainRowSlides.entrySet().stream()
				.filter(entry -> entry.getValue().equals(slide))
				.map(Map.Entry::getKey)
				.findAny().orElseThrow(() -> new IllegalArgumentException("Not a valid slide " + slide));
			if (columnSlides == null)
				columnSlides = new HashMap<>();
			final ColumnSlides slides = new ColumnSlides(this, ResourceUtils.getValidResourceName(id) + "_col", template);
			final ColumnSlides old = columnSlides.put(id, slides);
 			if (old != null)
				old.destroyWidget();
		}
	}
	
	public void setColumnsTemplate(final PageSnippetI slide, final ColumnTemplate template, 
			final Collection<OgemaWidget> triggers, final Collection<OgemaWidget> triggered) {
		Objects.requireNonNull(template);
		Objects.requireNonNull(slide);
		synchronized (this) {
			final String id = mainRowSlides.entrySet().stream()
				.filter(entry -> entry.getValue().equals(slide))
				.map(Map.Entry::getKey)
				.findAny().orElseThrow(() -> new IllegalArgumentException("Not a valid slide " + slide));
			if (columnSlides == null)
				columnSlides = new HashMap<>();
			final ColumnSlides slides = new ColumnSlides(this, ResourceUtils.getValidResourceName(id) + "_col", false, 
					template, triggers, triggered);
			final ColumnSlides old = columnSlides.put(id, slides);
 			if (old != null)
				old.destroyWidget();
		}
	}
	
	private final String generateId() {
		return IntStream.range(0, Integer.MAX_VALUE)
			.mapToObj(i -> String.valueOf(i))
			.filter(i -> !slideExists(i))
			.findFirst().get();
	}
    
	private final boolean slideExists(final String id0) {
		final String id = mapId(id0);
		if (mainRowSlides.containsKey(id))
			return true;
		if (columnSlides == null)
			return false;
		return columnSlides.values().stream()
				.filter(slide -> slide.getId().equals(id))
				.findAny().isPresent();
	}
	
	private static class RevealServlet extends HttpServlet {
		
		protected static List<HtmlLibrary> getLibraries() {
			final Builder<HtmlLibrary> builder = Stream.<HtmlLibrary> builder()
					.add(new HtmlLibrary(LibType.JS,"jQuery", "/ogema/jslib/jquery/jquery-3.2.1.min.js",true));
					// FIXME bootstrap apparently incompatible with reveal.js... we cannot use it here
//					.add(new HtmlLibrary(LibType.CSS,"", "/ogema/jslib/bootstrap/3.3.7/css/bootstrap.min.css",true)) 
//					.add(new HtmlLibrary(LibType.JS,"jQuery.fn.modal", "/ogema/jslib/bootstrap/3.3.7/js/bootstrap.min.js",true));
			if (DEBUG) {
				builder.add(new HtmlLibrary(LibType.JS,"GenericWidget", "/ogema/jslib/GenericWidget.js",true))
					.add(new HtmlLibrary(LibType.JS,"WidgetGroups", "/ogema/jslib/WidgetGroups.js",true))
					.add(new HtmlLibrary(LibType.JS,"ogema.reloadWidgets", "/ogema/jslib/widgetLoader.js", true));
			} else {
				builder.add(new HtmlLibrary(LibType.JS,"widgetsCompressed", "/ogema/jslib/minified/widgets-base.js",true))
				// preloads some commonly used widgets JS; in debug mode these are added to the external libs instead
					.add(new HtmlLibrary(LibType.JS,"widgetsCollectionBase", "/ogema/widget/minified/widget-collection.js",true));
			}
			// in between, external libs will be loaded
			builder.add(new HtmlLibrary(LibType.CSS,"", "/ogema/jslib/widgets.css"));
			return builder.build().collect(Collectors.toList());
		}

		
		private static final long serialVersionUID = 1L;
		private final RevealWidgetPage<?> page;
		
		RevealServlet(RevealWidgetPage<?> page) {
			this.page = page;
		}
		
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			final Collection<HtmlLibrary> libs = getLibraries();
            final StringBuilder sb= new StringBuilder();
			final String slides = page.mainRowSlides.values().stream()
					.map(page::getSlideHtml)
					.collect(Collectors.joining());
			sb.append("<!doctype html><html>");
			sb.append(HEAD);
			sb.append(BODY_OPEN);
			sb.append(slides);
			sb.append(MAIN_LIBS);
			getWidgetLoaderHtml(sb, req);
			appendLibsHtml(sb, libs,true);  // loads early libs
			appendLibsHtml(sb, page.externalLibs, false);
			sb.append("<script>ogema.widgetLoader.init();</script>"); // run widgetLoader
			// it is better to initialize reveal after loading the first widgets, otherwise the alignment of the widgets fails
			sb.append(REVEAL_INIT);
			sb.append(FRAGMENT_HIDER);
			appendLibsHtml(sb, libs,false);  // loads late libs
			sb.append(BODY_CLOSE);
			sb.append("</html>");
            resp.setContentType("text/html");
            resp.getWriter().write(sb.toString());
            resp.setStatus(200);
		}

		private static final String BODY_OPEN = "<body><div class=\"reveal\"><div class=\"slides\">";
		private static final String BODY_CLOSE = "</div></div></body>";
		// TODO paths!
		private static final String MAIN_LIBS = 
				  "<script src=\"" + ResourceRegistration.BROWSER_PATH + "/lib/js/head.min.js\"></script>"
				+ "<script src=\"" + ResourceRegistration.BROWSER_PATH + "/js/reveal.js\"></script>";
		// More info https://github.com/hakimel/reveal.js#configuration
		// More info https://github.com/hakimel/reveal.js#dependencies
		private static final String REVEAL_INIT = "<script>" + 
				"Reveal.initialize({controls: true, progress: true, history: true, center: true," + 
				"	transition: 'slide'," + // none/fade/slide/convex/concave/zoom" + 
				"	dependencies: [" + 
				"		{ src: '" + ResourceRegistration.BROWSER_PATH + "/plugin/highlight/highlight.js', async: true, callback: function() { hljs.initHighlightingOnLoad(); } }," + 
				"		{ src: '" + ResourceRegistration.BROWSER_PATH + "/plugin/search/search.js', async: true }," + 
				"		{ src: '" + ResourceRegistration.BROWSER_PATH + "/plugin/zoom-js/zoom.js', async: true }" + 
//				"		{ src: '" + ResourceRegistration.BROWSER_PATH + "/plugin/notes/notes.js', async: true }" + 
				"	]" + 
				"});" + 
				"</script>";
//		required to hide fragments again after they appeared once... somewhat hacky
		private static final String FRAGMENT_HIDER =  
				"<style> " + 
				".fragment.current-visible.visible:not(.current-fragment) {" + 
				"    display: none;" + 
				"    height:0px;" + 
				"    line-height: 0px;" + 
				"    font-size: 0px;" + 
				"}" + 
				"</style>";
		
		// TODO configurable
		private static final String HEAD = "<head>" + 
				"<meta charset=\"utf-8\">" + 
				"<title>OGEMA 2.0 - Open source energy management</title>" + 
				"<meta name=\"description\" content=\"A Java/OSGi based energy management software framework\">" + 
				"<meta name=\"author\" content=\"Fraunhofer\">" + 
				"<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">" + 
				"<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\">" + 
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" + 
				"<link rel=\"stylesheet\" href=\"" + ResourceRegistration.BROWSER_PATH + "/css/reveal.css\">" + 
				"<link rel=\"stylesheet\" href=\"" + ResourceRegistration.BROWSER_PATH + "/css/theme/black.css\" id=\"theme\">" + 
				"<link rel=\"stylesheet\" href=\"" + ResourceRegistration.BROWSER_PATH + "/lib/css/hybrid.css\">" + 
				"<script>" + 
				"var link = document.createElement( 'link' );" + 
				"link.rel = 'stylesheet';" + 
				"link.type = 'text/css';" + 
				"link.href = window.location.search.match( /print-pdf/gi ) ? '" + ResourceRegistration.BROWSER_PATH + "/css/print/pdf.css' "
					+ ": '" + ResourceRegistration.BROWSER_PATH + "/css/print/paper.css';" + 
				"document.getElementsByTagName( 'head' )[0].appendChild( link );" + 
				"</script>" + 
				"<!--[if lt IE 9]>" + 
				"<script src=\"lib/js/html5shiv.js\"></script>" + 
				"<![endif]-->" + 
				"</head>";

		/*
		 * below: copied from WidgetPageImpl
		 */
		
		private void getWidgetLoaderHtml(final StringBuilder html, final HttpServletRequest req) {
			String[] otup = null;
			try {
				if (page.staticRegistration == null)
					throw new NoClassDefFoundError(); // go to catch clause
				otup = ((org.ogema.webadmin.AdminWebAccessManager.StaticRegistration) page.staticRegistration).generateOneTimePwd(req);
			} catch (NoClassDefFoundError | ClassCastException e) { // fallback for OGEMA version < 2.1.2
//					otup = app.wam.registerStaticResource(servlet, req);
				try {
					final WidgetAppImpl wapp = page.getApp();
					final Field f = wapp.getClass().getDeclaredField("wam");
					f.setAccessible(true);
					final WebAccessManager wam = (WebAccessManager) f.get(wapp);
					final Class<?>  wamClass = wam.getClass();
					final Method register = wamClass.getMethod("registerStaticResource", HttpServlet.class, HttpServletRequest.class);
					otup = (String[]) register.invoke(wam, this, req);
				} catch (Exception ee) {
					LoggerFactory.getLogger(JsBundleApp.class).warn("Could not generate one-time-password for page {}",page.getFullUrl(),ee);
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(JsBundleApp.class).warn("Could not generate one-time-password for page {}",page.getFullUrl(),e);
			}
			if (otup != null)
				html.append("<script>var otusr='" + otup[0] + "';var otpwd='" + otup[1] + "';</script>");
			final String otu = otup !=null ? otup[0]: null;
			final String otp = otup != null ? otup[1] : null;
			html.append("<script>var ogema={};ogema.dynamicHtml=true;ogema.widgetLoader={};ogema.widgetLoader.scriptsLoadedInit=[];</script>");
			// TODO icon
//			String icon = getIconPath(otu,otp);
//			if (icon != null && !icon.trim().isEmpty())
//				html.append("<link rel=\"icon\" href=\"").append(icon).append("\">");
		}

		private static void appendLibsHtml(final StringBuilder html, final Collection<HtmlLibrary> libs, final boolean loadEarly) {
			libs.stream()
				.filter(lib ->lib.isLoadEarly() == loadEarly)
				.forEach(lib -> {
					if (lib.getType() == LibType.JS)
						addScriptWithErrorHandling(html, "", lib.getPath(), lib.getIdentifier(), loadEarly);
					else {
						html.append(lib.getHtmlString());
					}
				});
		}

		
		/**
		 * Note that scripts from an external server can only be included in the initial html file, but
		 * cannot be dynamically loaded, due to CORS restriction. For this reason, we have to include the server
		 * version of the scripts in the initial HTMl, and fall back to the scripts from the OGEMA gateway in case
		 * there is an error.
		 */

		private static void addScriptWithErrorHandling(StringBuilder sb, String prefix, String path, String identifier, boolean doLoad)	{
			sb.append("<script src='" + prefix + path +"'></script><script>");
			if (doLoad)
				sb.append("if (typeof " + identifier +" === \"undefined\"){"
					+	"document.write(unescape(\"%3Cscript src='" + path + "' type='text/javascript'%3E%3C/script%3E\"));"
					+ "}");
			sb.append("ogema.widgetLoader.scriptsLoadedInit.push('" + path + "');</script>");
		}
		
	}
	
	final String getSlideHtml(final PageSnippetI slide) {
		final String id = slide.getId();
		final ColumnSlides cols = columnSlides.get(id);
		if (cols != null) {
			return 
			  "<section>"
		    +     "<section id=\"" + id.substring(ID_PREFIX.length()) +  "\">"
			+       "<div class=\"ogema-widget\" id=\"" + id + "\"></div>"
			+     "</section>"
			+     cols.getTag()
			+ "</section>";
		}
		return "<section id=\"" + id.substring(ID_PREFIX.length()) +  "\"><div class=\"ogema-widget\" id=\"" + id + "\"></div></section>";
	}
	
	
}
