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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ogema.tools.widgets.test.base.util.RequestFuture;
import org.ogema.tools.widgets.test.base.util.Utils;

import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;

/**
 * Represents a client side widget; for testing
 * All derived widget classes need to provide a constructor with the same arguments.
 * 
 * @author cnoelle
 */
public class GenericWidget {
	
	private final String id;
	protected final String servletPath;
	// synchronized on this
	protected volatile JSONObject widgetData; // widget GET data
	protected final WidgetLoader widgetLoader;
	// the asyncclient cannot execute two response callbacks in parallel, hence we need individual clients for all types of requests and all widgets
	private CloseableHttpAsyncClient asyncClientGET = null; // initialized when needed
	private CloseableHttpAsyncClient asyncClientPOST = null;
	private final Object asynClientLock = new Object();
	
	public GenericWidget(WidgetLoader client, String id, String servletPath) {
		this.widgetData = new JSONObject();
		this.id = id;
		this.widgetLoader = client;
		this.servletPath = servletPath;
		sendGET(); // initialized on initalWidgetData, if available
//		  SSLContext ctx;
//	        try {
//	            ctx = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
//	        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
//	            throw new RuntimeException(ex);
//	        }
	}
	
	public void close() {
		synchronized(asynClientLock) {
			Utils.closeSmoothly(asyncClientGET);
			Utils.closeSmoothly(asyncClientPOST);
			asyncClientGET = null;
			asyncClientPOST = null;
		}
		synchronized (this) {
			widgetData = null;
		}
	}
	
	public void initGETClient() {
		getClient(true);
	}
	
	public void initPOSTClient() {
		getClient(false);
	}
	
	/**
	 * 
	 * @param get
	 * 		true: get GET-client, false: get POST-client
	 * @return
	 */
	protected CloseableHttpAsyncClient getClient(boolean get) {
		synchronized(asynClientLock) {
			if (get) {
				if (asyncClientGET == null) {
//					asyncClientGET = HttpAsyncClients.custom()
//			        		.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
////			        		.setSSLContext(widgetLoader.ctx)
//			        		.build();
					asyncClientGET = HttpAsyncClients.createDefault();
					asyncClientGET.start();
				}
				return asyncClientGET;
			}
			else {
				if (asyncClientPOST == null) {
//					asyncClientPOST = HttpAsyncClients.custom()
//			        		.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
////			        		.setSSLContext(widgetLoader.ctx)
//			        		.build();
					asyncClientPOST = HttpAsyncClients.createDefault();
					asyncClientPOST.start();
				}
				return asyncClientPOST;
			}
			
		}
	}

	/*
	 ********* overwrite in derived class ******
	 */
	
	/**
	 * Called before submitting a POST request
	 * @return
	 * 		either a stringified JSON object or a stringified JSONArray 
	 */
	protected String getSubmitData() {
		return new JSONObject().toString();
	}

	/**
	 * Called when receiving new GET data 
	 * Usually not required here, since we keep track of the last 
	 * response, and hence can extract all the value from it.
	 * @param data
	 */
	protected void update(JSONObject data) {
	}
	
	
	/**
	 * Compare new data against old (widgetData).
	 * Overwrite if necessary
	 * @param newData
	 * @return
	 */
	protected boolean checkForReload(JSONObject newData) {
		return false;
	}
	
	protected void processPOSTResponse(JSONObject response) {
	}
	
	/*
	 ********** public mehtods *****
	 */
	
	public synchronized JSONObject getWidgetData() {
		return new JSONObject(widgetData.toString()); // deep clone
	}

	public synchronized void setWidgetData(JSONObject widgetData) {
		this.widgetData = new JSONObject(widgetData.toString()); // deep clone
	}

	public String getId() {
		return id;
	}
	
	public Future<?> sendPOST() {
		return sendPOST(false);
	}
	
	public Future<?> sendPOST(boolean waitForTriggeredActions) {
		JSONObject data = new JSONObject();
		String submitData = getSubmitData();
		data.put("data", submitData);
		appendPrePOSTData(data);
		// System.out.println("   Widget POST to " + getId() + ": " + data );
		final HttpPost post;
		try {
			post = new HttpPost(widgetLoader.getWidgetUrl(this).build());
		} catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
		StringEntity body = new StringEntity(data.toString(), ContentType.APPLICATION_JSON);
		post.setEntity(body);
		widgetLoader.addCookie(post);
		CountDownLatch latch = new CountDownLatch(1);
//		widgetLoader.asyncClient.execute(post, new PostRequestCallback(latch, waitForTriggeredActions));
		PostRequestCallback callback =  new PostRequestCallback(latch, waitForTriggeredActions);
		getClient(false).execute(post,callback);
		RequestFuture result = new RequestFuture(latch,callback);
//		Future<HttpResponse> result = widgetLoader.asyncClient.execute(HttpAsyncMethods.create(post), new BasicAsyncResponseConsumer(),new PostRequestCallback());
//		RequestFuture result = new RequestFuture(latch);
		return result;
		
	}
	
	public Future<?> sendGET() {
		return sendGET(false);
	}
	
	public Future<?> sendGET(boolean waitForTriggeredActions) {
		final JSONObject obj = widgetLoader.widgetsInitData.remove(id);
		if (obj != null) {
			synchronized (this) {
				widgetData = obj;
			}
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
					return true;
				}

				@Override
				public Void get() throws InterruptedException, ExecutionException {
					return null;
				}

				@Override
				public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
					return null;
				}
			};
		}
		final HttpGet get;
		try {
			get = new HttpGet(widgetLoader.getWidgetUrl(this).build());
		} catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
		widgetLoader.addCookie(get);
		// System.out.println("    Widget GET to " + widgetLoader.getWidgetUrl(this));
//		Future<HttpResponse> future = widgetLoader.asyncClient.execute(get, new GetRequestCallback(latch));
		CountDownLatch latch = new CountDownLatch(1);
		GetRequestCallback callback = new GetRequestCallback(latch,waitForTriggeredActions);
		RequestFuture result = new RequestFuture(latch,callback);
//		widgetLoader.asyncClient.execute(get,new GetRequestCallback(latch,waitForTriggeredActions));
		getClient(true).execute(get,callback);
		return result;
	}
	
//	private class RequestFuture2 extends AsyncByteConsumer<JSONObject> {
//		
//		private JSONObject response = null;
//
//		@Override
//		protected void onByteReceived(ByteBuffer arg0, IOControl arg1) throws IOException {
//			// TODO Auto-generated method stub
//		}
//
//		@Override
//		protected synchronized JSONObject buildResult(HttpContext ctx) throws Exception {
//			return response;
//		}
//
//		@Override
//		protected synchronized void onResponseReceived(HttpResponse resp) throws HttpException, IOException {
//			try {
//				response = Utils.getDataFromResponse(resp);
//				boolean reload;
//				synchronized (GenericWidget.this) {
//					reload = checkForReload(response);
//					widgetData = new JSONObject(response.toString());
//				}
//				update(response);
//				if (reload)
//					widgetLoader.reloadWidgets();
//				triggerActions(TriggeringAction.GET_REQUEST);
//			} catch (Exception e) {
//				failed(e);
//			}
//		}
//		
//	}
	
	public static interface HttpCallback extends FutureCallback<HttpResponse> {
		
		Exception getException();
		
	};
	
	private class GetRequestCallback implements HttpCallback {
		
		private final CountDownLatch latch;
		private final boolean waitForTriggeredActions;
		private volatile Exception e = null;
		
		public GetRequestCallback(CountDownLatch latch,boolean waitForTriggeredActions) {
			this.latch = latch;
			this.waitForTriggeredActions = waitForTriggeredActions;
		}
		
		@Override
		public void cancelled() {
			failed(new RuntimeException(" GET request cancelled"));
		}

		@Override
		public void completed(HttpResponse resp) {
			try {
				JSONObject response = Utils.getDataFromResponse(resp);
				boolean reload;
				synchronized (GenericWidget.this) {
					reload = checkForReload(response);
					widgetData = new JSONObject(response.toString());
				}
				update(response);
				if (reload)
					widgetLoader.reloadWidgets();
				triggerActions(TriggeringAction.GET_REQUEST, waitForTriggeredActions);
				latch.countDown();
			} catch (Exception e) {
				failed(e);
			}
		}

		@Override
		public void failed(Exception e) {
			synchronized (GenericWidget.this) {
				widgetData = null;
			}
			this.e = e;
			latch.countDown();
		}
		
		public Exception getException() {
			return e;
		}
		
	}
	
	// TODO some overwritable POST handler?
	private class PostRequestCallback implements HttpCallback {
		
		private final CountDownLatch latch;
		private final boolean waitForTriggeredActions;
		private volatile Exception e = null;
		
		public PostRequestCallback(CountDownLatch latch,boolean waitForTriggeredActions) {
			this.latch = latch;
			this.waitForTriggeredActions =waitForTriggeredActions;
		}
		
		@Override
		public void cancelled() {
			failed(new RuntimeException("POST request cancelled"));
		}

		@Override
		public void completed(HttpResponse resp) {
			try {
				JSONObject obj = Utils.getDataFromResponse(resp);
				processPOSTResponse(obj); 
				triggerActions(TriggeringAction.POST_REQUEST, waitForTriggeredActions);
				latch.countDown();
			} catch (Exception e) {
				failed(e);
			}
		}

		@Override
		public void failed(Exception e) {
			this.e = e;
			latch.countDown();
		}
		
		public Exception getException() {
			return e;
		}
		
	}
	
	// TODO group actions
	private void appendPrePOSTData(JSONObject data) {
		Map<String,Action> actions = getTriggeredActions();
		if (actions != null) {
			for (Action act: actions.values()) {
				if (!act.trigger.equals(TriggeringAction.PRE_POST_REQUEST))
					continue;
				String id2 = act.widgetId2;
				GenericWidget widget = widgetLoader.getWidget(id2);
				if (widget == null) {
					System.out.println("Widget " + id2 + " not found");
					continue;
				}
				TriggeredAction triggered = act.triggered;
				if (triggered.equals(TriggeredAction.POST_REQUEST) || triggered.equals(new TriggeredAction("getSubmitData"))) {
					data.put(id2, widget.getSubmitData());
				}
				else if (triggered.equals(TriggeredAction.GET_REQUEST))
					widget.sendGET();
				else
					System.out.println("Triggered action " + triggered + " not supported by test client");
			}
		}
	}
	
//	private void triggerActions(TriggeringAction trigger) {
//		try {
//			triggerActions(trigger, false);
//		} catch (InterruptedException | ExecutionException e) {
//			// cannot actually happen in this case
//		}
//	}
	
	/**
	 * TODO future to wait for all actions
	 * @param post
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void triggerActions(TriggeringAction trigger, boolean waitForResponse) throws InterruptedException, ExecutionException {
		List<Future<?>> futures = new ArrayList<>();
		Map<String,Action> actions = getTriggeredActions();
		if (actions != null) {
			for (Action act: actions.values()) {
				if (!act.trigger.equals(trigger))
					continue;
				GenericWidget gw = widgetLoader.getWidget(act.widgetId2);
				if (gw == null)
					continue;
				TriggeredAction triggered = act.triggered;
				if (triggered.equals(TriggeredAction.POST_REQUEST))
					futures.add(gw.sendPOST(waitForResponse));
				else if (triggered.equals(TriggeredAction.GET_REQUEST))
					futures.add(gw.sendGET(waitForResponse));
				else
					System.out.println("Triggered action " + triggered + " not supported by test client");
			}
		}
		// TODO
		Map<String,Action> groupactions = getTriggeredGroupActions();
		if (groupactions != null) {
			for (Action act: groupactions.values()) {
				if (!act.trigger.equals(trigger))
					continue;
				String groupId = act.widgetId2;
				TriggeredAction triggered = act.triggered;
				if (triggered.equals(TriggeredAction.POST_REQUEST))
					widgetLoader.widgetGroups.sendPOST(groupId);  // FIXME not implemented yet // TODO wait
				else if (triggered.equals(TriggeredAction.GET_REQUEST))
					widgetLoader.widgetGroups.sendGET(groupId);  // FIXME not implemented yet
				else
					System.out.println("Triggered action " + triggered + " not supported by test client");
			}
		}
		
		if (waitForResponse) {
			for (Future<?> f: futures)
				f.get();
		}
	}
	
	/**
	 * @return
	 * 		Map<WidgetId2, Action>, or null
	 */
	public Map<String,Action> getTriggeredActions() {
		JSONArray connectWidgets;
		try {
			connectWidgets = getWidgetData().getJSONArray("connectWidgets");
		} catch (JSONException e) {
			return null;
		}
		return getTriggeredElements(connectWidgets, false);
	}
	
	/**
	 * @return
	 * 		Map<GroupId2, Action>
	 */
	public Map<String,Action> getTriggeredGroupActions() {
		JSONArray connectGroups;
		try {
			connectGroups = getWidgetData().getJSONArray("connectGroups");
		} catch (JSONException e) {
			return null;
		}
		return getTriggeredElements(connectGroups, true);
	}
	
	// targetTypes: widgetID2, groupID2
	private final Map<String,Action> getTriggeredElements(JSONArray array, boolean groupTarget) {
		String target;
		if (groupTarget)
			target = "groupID2";
		else
			target = "widgetID2";
		Map<String,Action> map = new HashMap<>();
		Iterator<Object> it  =array.iterator();
		while (it.hasNext()) {
			JSONObject obj = (JSONObject) it.next();
			String id2 = obj.getString(target);
			String triggered = obj.getString("triggeredAction");
			String triggering = obj.getString("triggeringAction");
			Object[] args = null;
			if (obj.has("args")) {
				JSONArray argsArr = obj.getJSONArray("args");
				args = new Object[argsArr.length()];
				for (int i=0;i<args.length;i++) {
					args[i] = argsArr.get(i);
				}
			}
			TriggeringAction triggeringAction = new TriggeringAction(triggering);
			TriggeredAction triggeredAction = new TriggeredAction(triggered, args);
			Action act = new Action(getId(), id2, triggeringAction, triggeredAction);
			map.put(id2, act);
		}
		return map;
	}
	
	
}
