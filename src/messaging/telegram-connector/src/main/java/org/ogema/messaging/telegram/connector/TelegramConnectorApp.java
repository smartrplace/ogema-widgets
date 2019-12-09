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
package org.ogema.messaging.telegram.connector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Connects to the Telegram bot API; a dedicated Telegram bot is required 
 * for this, which currently has to be configured via system properties 
 */
// TODO switch to config admin props
@Component(
		immediate=true,
		service= {}
)
public class TelegramConnectorApp implements MessageListener, Application {
	
	private static final String SEPARATOR = "___XXX___";
	private final static Logger logger = LoggerFactory.getLogger(TelegramConnectorApp.class);
	private volatile ResourceList<TelegramContact> contactList;
	private volatile long lastUpdate;
	
	@Reference
	private OgemaGuiService widgetsService;
	private WidgetApp wapp;
	private BundleContext ctx;
	private ServiceRegistration<Application> appReg;
	private volatile ServiceRegistration<MessageListener> listenerReg;
	
	
	@Activate
	protected void activate(final BundleContext ctx) {
		this.ctx = ctx;
		if (Constants.BOT_KEY == null) { // TODO use config admin properties instead!
			throw new ComponentException("API key not specified, going to sleep.");
		}
		this.appReg = ctx.registerService(Application.class, this, null);
	}
	
	@Deactivate
	protected void deactivate() {
		ForkJoinPool.commonPool().submit(() -> {
			try {
				if (appReg != null)
					appReg.unregister();
			} catch (Exception ignore) {}
			try {
				if (listenerReg != null)
					listenerReg.unregister();
			} catch (Exception ignore) {}
			appReg = null;
			listenerReg = null;
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(ApplicationManager appManager) {
		contactList = appManager.getResourceManagement().createResource("telegramContactList", ResourceList.class);
		contactList.setElementType(TelegramContact.class);
		contactList.activate(false);
		this.listenerReg = ctx.registerService(MessageListener.class, this, null);
		if (Constants.BOT_PRIVATE)
			updateStatus();
		this.wapp = widgetsService.createWidgetApp("/de/iwes/messaging/telegram", appManager);
		wapp.createLazyPage("index.html", page -> new ContactsPage(page, contactList), true);
	}
	
	@Override
	public void stop(AppStopReason reason) {
		final ServiceRegistration<MessageListener> listenerReg = this.listenerReg;
		this.listenerReg = null;
		if (listenerReg != null) {
			ForkJoinPool.commonPool().submit(listenerReg::unregister);
		}
		contactList = null;
		final WidgetApp wapp = this.wapp;
		this.wapp = null;
		if (wapp != null)
			wapp.close();
	}
	
	@Override
	public String getId() {
		return "Telegram provider";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "Telegram messaging";
	}

	// FIXME evaluate recipients
	@Override
	public void newMessageAvailable(ReceivedMessage message, List<String> recipients) {
		if (Constants.BOT_PRIVATE) {
			if (System.currentTimeMillis() - lastUpdate > 15000)
				updateStatus();
		}
		for (String user: recipients) {
			sendToUser(message, user);
		}
	}
	
	private void updateStatus() {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				final String url = getURL("getUpdates");
				if (url == null)
					return null;
				HttpGet get = new HttpGet(getURL("getUpdates"));
				JSONObject response;
				try (CloseableHttpClient client = getClient(); CloseableHttpResponse resp = client.execute(get)) {
					StatusLine sl = resp.getStatusLine();	
					if (logger.isDebugEnabled())
						logger.debug("Telegram message sent, {}: {}",sl.getStatusCode(),sl.getReasonPhrase());
					if (sl.getStatusCode() >= 300) {
						logger.warn("Status code not ok, {}: {}",sl.getStatusCode(),sl.getReasonPhrase());
						return null;
					}
					String text = null;
				    try (Scanner scanner = new Scanner(resp.getEntity().getContent(), StandardCharsets.UTF_8.name())) {
				        text = scanner.useDelimiter("\\A").next();
				    }
			    	logger.trace("Update response: {}", text);
				    response = new JSONObject(text);
				} catch (IOException e) {
					logger.error("Telegram provider failed",e);
					return null;
				}
				JSONArray arr = response.getJSONArray("result");
				parseMessages(arr);
				lastUpdate = System.currentTimeMillis();
				return null;
			}
			
		});
	}
	
	private void sendToUser(ReceivedMessage message, String user) {
		String[] u = user.split(SEPARATOR);
		if (u.length != 2) {
			logger.warn("Invlid user name " + user);
			return;
		}
		long chatId = getUserChatId(u[0], u[1]);
		if (chatId < 0) {
			logger.warn("No chat id for user {} {}",u[0],u[1]);
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(message.getAppName()).append(": ").append(message.getOriginalMessage().title(OgemaLocale.ENGLISH))
			.append("\n").append(message.getOriginalMessage().message(OgemaLocale.ENGLISH));
		JSONObject data = new JSONObject();
		data.put("text", sb.toString());
		data.put("chat_id", chatId); // FIXME
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				HttpEntity body = new StringEntity(data.toString(), StandardCharsets.UTF_8);
				final String url = getURL("sendMessage");
				if (url == null)
					return null;
				HttpPost post = new HttpPost(url);
				post.setEntity(body);
				post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				if (logger.isTraceEnabled())
					logger.trace("Sending message to  " + post.getURI() + ", content:\n" + data);
				try (CloseableHttpClient client = getClient(); CloseableHttpResponse resp = client.execute(post)) {
					StatusLine sl = resp.getStatusLine();	
					if (logger.isDebugEnabled())
						logger.debug("Telegram message sent, {}: {}",sl.getStatusCode(),sl.getReasonPhrase());
					if (sl.getStatusCode() >= 300) {
						logger.warn("Status code not ok, {}: {}",sl.getStatusCode(),sl.getReasonPhrase());
					}
				} catch (IOException | SecurityException e) {
					logger.error("Telegram provider failed",e);
				}
				return null;
			}
			
		});
	}
	
	private void parseMessages(JSONArray array) {
		for (int i =0;i<array.length(); i++) {
			JSONObject obj = array.getJSONObject(i).getJSONObject("message").getJSONObject("chat");
			String fn = obj.getString("first_name");
			String ln = obj.has("last_name") ? obj.getString("last_name") : "";
			long id = obj.getLong("id");
			TelegramContact c = getUser(fn,ln).create();
			c.firstName().<StringResource> create().setValue(fn);
			c.lastName().<StringResource> create().setValue(ln);
			c.chatId().<TimeResource> create().setValue(id);
			c.activate(true);
		}
		
	}
	
	private long getUserChatId(String firstName, String lastName) {
		TelegramContact tc = getUser(firstName, lastName);
		if (!tc.isActive() || !tc.chatId().isActive())
			return -1;
		return tc.chatId().getValue();
	}
	
	private TelegramContact getUser(String firstName, String lastName) {
		return contactList.getAllElements().stream()
			.filter(c -> firstName.equals(c.firstName().getValue()) && lastName.equals(c.lastName().getValue()))
			.findAny()
			.orElse(null);
	}

	// need at least Map<String,String>, better dedicated object type
	@Override
	public List<String> getKnownUsers() {
		if (Constants.BOT_PRIVATE) {
			if (System.currentTimeMillis() - lastUpdate > 15000)
				updateStatus();
		}
		List<String> l = new ArrayList<>();
		final ResourceList<TelegramContact> list = this.contactList;
		if (list == null)
			return Collections.emptyList();
		for (TelegramContact tc : list.getAllElements()) {
			if (!tc.isActive())
				continue;
			l.add(tc.firstName().getValue() + SEPARATOR + tc.lastName().getValue());
		}
		return l;
	}
	
	private static final String getURL(String command) {
		final String botKey = Constants.BOT_KEY;
		if (botKey == null)
			return null;
		return "https://api.telegram.org/bot" + botKey.trim() + "/" + command;
	}
	
	static CloseableHttpClient getClient() {
		return HttpClients.createDefault();
	}

}
 