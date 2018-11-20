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
package org.ogema.tools.widgets.test.base;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.ogema.tools.widgets.test.base.util.Utils;
import org.ogema.tools.widgets.test.base.widgets.TestWidgetsFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;


public class WidgetLoader {
	
	private final CloseableHttpClient client;
//	protected final CloseableHttpAsyncClient asyncClient;
	private final WidgetPage<?> page;
	protected final String pageBaseUrl;
	private final String widgetServiceBaseUrl;
	protected final String pageBasePath;
	private final String widgetServiceBasePath;
	protected final WidgetGroups widgetGroups;
//	private final Map<String,JSONArray> widgets = new LinkedHashMap<>();
	private final Map<String,GenericWidget> widgets = new ConcurrentHashMap<>();
	protected Map<String,JSONObject> widgetsInitData = new ConcurrentHashMap<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean doReload = false;
	private boolean reloading = false;
	private final Object lock = new Object();
	private int instanceId = -1;
	private String cookie = null;
	protected final int port;
	private Header cookieHeader = null;
	private String[] upw = null;
//	protected final SSLContext ctx;
	
	public WidgetLoader(WidgetPage<?> page, int port) {
		this.page=page;
		this.port = port;
		String pagePath = page.getFullUrl();
		pagePath = pagePath.substring(0, pagePath.length()-5); // remove ".html"
		this.pageBaseUrl  = "http://localhost:" + port + pagePath;
		this.pageBasePath = pagePath;
		this.widgetServiceBasePath = "/ogema/widget/servlet";
		this.widgetServiceBaseUrl = "http://localhost:" + port + "/ogema/widget/servlet";
		this.widgetGroups = new WidgetGroups(this);
//        try {
//            ctx = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
//        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
//            throw new RuntimeException(ex);
//        }
        
		this.client = HttpClients.createDefault();
//        this.client  = HttpClients.custom()
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
////                .setSslcontext(ctx)
//                .build();
//        this.asyncClient = HttpAsyncClients.custom()
//        		.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//        		.setSSLContext(ctx)
//        		
//        		.build();
//        asyncClient.start();
	}
	
	private final URIBuilder getURIBuilder() {
		return new URIBuilder()
			.setScheme("http")
			.setHost("localhost")
			.setPort(port);
	}
	
	/**
	 * waits for the initial widgets to load, additional ones are loaded asynchronously
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws URISyntaxException 
	 * @throws ParseException 
	 */
	public void connect() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, ParseException, URISyntaxException {
		login();
		loadPage();
		getInitData();
		// this works slightly differently from the Browser client... the first requests are sent synchronously here
		reloadWidgetsInternal().get(); 
	}
	
	private void loadPage() throws ParseException, IOException, URISyntaxException {
		final String html = getStringResponse(pageBasePath + ".html", null);
		this.upw = Utils.extractUserAndPw(html);
	}
	
	public Future<Void> close() {
//		Utils.closeSmoothly(asyncClient);
		Utils.closeSmoothly(client);
		for (GenericWidget w: widgets.values()) {
			w.close();
		}
		widgets.clear();
		executor.shutdownNow();
		return new Future<Void>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return executor.isShutdown();
			}

			@Override
			public Void get() throws InterruptedException, ExecutionException {
				executor.awaitTermination(120, TimeUnit.SECONDS);
				return null;
			}

			@Override
			public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				executor.awaitTermination(timeout, unit);
				return null;
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public <W extends GenericWidget> W getWidget(String id) {
		return (W) widgets.get(id);
	}
	
	/**
	 * To be called by widgets (client-side) that create or delete subwidgets in their GET or POST handling methods (server side).
	 * TODO return a future
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void reloadWidgets() throws ClientProtocolException, IOException {
		boolean reload = false;
		synchronized (lock) {
			if (reloading)
				doReload = true;
			else {
				reloading = true;
				reload = true;
			}
		}
		if (reload)
			reloadWidgetsInternal();
	}
	
	private Future<Void> reloadWidgetsInternal() throws ClientProtocolException, IOException {
		Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				synchronized (lock) {
					doReload = false;
					reloading = true;
				}
				// sends Http requests, may take some time
				Map<String,JSONArray> widgets = getWidgets();
				synchronized (widgets) {
					// remove widgets no longer relevant
					Iterator<String> ids = WidgetLoader.this.widgets.keySet().iterator();
					while (ids.hasNext()) {
						String id = ids.next();
						if (widgets.containsKey(id)) 
							continue;
						ids.remove();
					}
					// add new widgets
					for (Map.Entry<String, JSONArray> entry: widgets.entrySet()) {
						String id = entry.getKey();
						if (WidgetLoader.this.widgets.containsKey(id))
							continue;
						GenericWidget widget = TestWidgetsFactory.create(entry.getValue(), WidgetLoader.this);
						WidgetLoader.this.widgets.put(id, widget);
					}
				}
				boolean reload = false;
				synchronized (lock) {
					if (doReload) 
						reload = true;
					else
						reloading = false;
				}
				if (reload)
					reloadWidgetsInternal();
				return null;
			}
		};
		return executor.submit(callable);
	}
	
	
	protected URIBuilder getWidgetUrl(GenericWidget widget) {
		final URIBuilder builder = getURIBuilder()
				.setPath(pageBasePath + "/" + widget.getId());
		final Map<String,String> params = getPageParameters();
		if (params != null)
			params.entrySet().stream().forEach(entry -> builder.addParameter(entry.getKey(), entry.getValue()));
		return builder;
	}
	
//	private String getWidgetPageServiceUrl() {
////		"https://localhost:8443/ogema/widget/servlet?boundPagePath=https://localhost:8443/widgets/update/test/index.html&pageInstance=&locale=en"
//		return widgetServiceBaseUrl + "?boundPagePath=" + pageBaseUrl + ".html" + getPageParameters();
//	}
	
//	private String getInitialWidgetDataUrl() {
////		https://localhost:8443/ogema/widget/servlet?initialWidgetInformation=https://localhost:8443/widgets/update/test/index.html
//		return widgetServiceBaseUrl + "?initialWidgetInformation=" + pageBaseUrl + ".html" + getPageParameters();
//	}
	
	private void getInitData() throws ClientProtocolException, IOException, ParseException, URISyntaxException {
		final Map<String,String> params = getPageParameters();
		params.put("initialWidgetInformation", pageBaseUrl + ".html");
		String respStr = getStringResponse(widgetServiceBasePath, params);
		JSONObject resp = new JSONObject(respStr);
		instanceId = resp.getJSONArray("pageInstance").getInt(0); 
		Iterator<String> it = resp.keys();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equals("pageInstance")) 
				continue;
			widgetsInitData.put(key,resp.getJSONArray(key).getJSONObject(0));
		}
	}
	
	private Map<String,JSONArray> getWidgets() throws ClientProtocolException, IOException, ParseException, URISyntaxException {
		final Map<String,String> params = getPageParameters();
		params.put("boundPagePath", pageBaseUrl + ".html");
		String respStr = getStringResponse(widgetServiceBasePath, params);
		JSONArray json = new JSONArray(respStr);
		Map<String,JSONArray> widgets = new HashMap<>();
		for (int j=0;j<json.length();j++) {
			JSONArray arr = json.getJSONArray(j);
			String id = arr.getString(0);
			widgets.put(id, arr);
		}
		return widgets;
	}
	
	private Map<String,String> getPageParameters() {
		final Map<String,String> map = new HashMap<>(4);
		final boolean hasInstance=  instanceId >= 0;
		if (hasInstance)
			map.put("pageInstance", instanceId + "");
		if (upw != null) {
			map.put("user", upw[0]);
			map.put("pw", upw[1]);
		}
//				sb.append(hasInstance ? '&' : '?').append("user=").append(URLEncoder.encode(upw[0], "UTF-8")).append('&').append("pw=").append(URLEncoder.encode(upw[1], "UTF-8"));
		return map;
	}
	
	private String getStringResponse(final String path, final Map<String,String> parameters) throws ParseException, IOException, URISyntaxException {
		final URIBuilder builder = getURIBuilder()
			.setPath(path);
		if (parameters != null && !parameters.isEmpty())
			parameters.entrySet().stream().forEach(entry -> builder.addParameter(entry.getKey(),entry.getValue()));
		HttpGet get = new HttpGet(builder.build());
		addCookie(get);
		// System.out.println("    sending requets to " + url);
		CloseableHttpResponse resp = client.execute(get);
		try {
			StatusLine sl = resp.getStatusLine();
			String respStr = EntityUtils.toString(resp.getEntity());
//			System.out.println("  http response from url: " + url + ":\n    " + respStr);
			Assert.assertEquals("Problem with Http request: " + sl.getStatusCode() + ": " + sl.getReasonPhrase(),HttpServletResponse.SC_OK, sl.getStatusCode());
			return respStr;
		} finally {
			resp.close();
		}
	}
	
//	protected String getGroupUrl(String groupId) {
////		"https://localhost:8443/ogema/widget/servlet?pageInstance=1&locale=de&groupId=test&boundPagePath=https://localhost:8443/widgets/update/test/index.html"
//		return getWidgetPageServiceUrl() + "&groupId=" + groupId;
//	}
	
	/*
	 ************ Asserts ************ 
	 */
	
	public void assertHasWidget(OgemaWidget widget) {
		Class<? extends OgemaWidgetBase<?>> serverWidgetType = ((OgemaWidgetBase<?>) widget).getWidgetClass();
		Class<? extends GenericWidget> expectedType = TestWidgetsFactory.getClientWidgetType(serverWidgetType);
		GenericWidget w = widgets.get(widget.getId());
		Assert.assertNotNull("Widget " + widget.getId() + " not found", w);
		Class<? extends GenericWidget> typeFound = w.getClass();
		Assert.assertTrue("Widget type unexpected: expected " + expectedType.getName() + ", found " + typeFound.getName(), expectedType.isAssignableFrom(typeFound)); 
	}
	
	private void login() throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(pageBaseUrl + ".html");
		HttpResponse resp = client.execute(get); // should return the login page 
		setCookie(resp);
		HttpPost post = new HttpPost("http://localhost:" + port + "/ogema/login");
		StringEntity body = new StringEntity("usr=master&pwd=master",ContentType.APPLICATION_FORM_URLENCODED);
		post.setEntity(body);
		addCookie(post);
		resp = client.execute(post);
		setCookie(resp);
		resp.getEntity().getContent().close(); // otherwise it hangs...
//		boolean hd  = post.containsHeader("Cookie");
//		System.out.println(" POST with header: " + hd + ", " + (hd ? post.getHeaders("Cookie")[0].getValue() : serialize(post.getAllHeaders())));
//		System.out.println(" Login response: " + EntityUtils.toString(resp.getEntity()));
		
		
	}
	
	private void setCookie(HttpResponse response) {
		Header[] cookies = response.getHeaders("Set-Cookie");
		if (cookies.length < 1)  // FIXME this sometimes happens in MultiClientTest -> ? 
			throw new RuntimeException("No session cookie received");
		cookie = cookies[0].getValue().split(";")[0].split("=")[1];
		CharArrayBuffer buff = new CharArrayBuffer(20);
		buff.append("Cookie:");
		buff.append("JSESSIONID=" + cookie);
		cookieHeader = new BufferedHeader(buff);
//		System.out.println("    Cookie received: " + cookie);
	}
	
	// for testing
//	private static final String serialize(Header[] headers) {
//		if (headers == null ||headers.length == 0)
//			return "";
//		StringBuilder sb = new StringBuilder();
//		sb.append('[');
//		for (int i=0;i<headers.length - 1;i++) {
//			Header hd = headers[i];
//			sb.append(hd.getName()).append(": ").append(hd.getValue()).append(", ");
//		}
//		Header hd = headers[headers.length-1];
// 		sb.append(hd.getName()).append(": ").append(hd.getValue()).append(']');
// 		return sb.toString();
//	}
	
	protected void addCookie(HttpRequest request) {
		if (cookieHeader != null)
			request.addHeader(cookieHeader.getName(), cookieHeader.getValue());
	}
	
	
}
