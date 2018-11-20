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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Connects to the Telegram bot API; a dedicated Telegram bot is required 
 * for this, which currently has to be configured via system properties 
 */
// TODO how does this know about users?
// TODO configuration pages; store API key in 
@Component
@Service({MessageListener.class, Application.class})
public class TelegramConnectorApp implements MessageListener, Application {
	
	private static final String SEPARATOR = "___XXX___";
	private final static Logger logger = LoggerFactory.getLogger(TelegramConnectorApp.class);
	private volatile ResourceList<TelegramContact> contactList;
	private volatile long lastUpdate;
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(ApplicationManager appManager) {
		if (Constants.BOT_KEY == null) {
			logger.error("API key not specified, going to sleep.");
			return;
		}
		contactList = appManager.getResourceManagement().createResource("telegramContactList", ResourceList.class);
		contactList.setElementType(TelegramContact.class);
		contactList.activate(false);
		updateStatus();
	}
	
	@Override
	public void stop(AppStopReason reason) {
		contactList = null;
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
		if (System.currentTimeMillis() - lastUpdate > 15000)
			updateStatus();
		for (String user: recipients) {
			sendToUser(message, user);
		}
	}
	
	private void updateStatus() {
		final String url = getURL("getUpdates");
		if (url == null)
			return;
		HttpGet get = new HttpGet(getURL("getUpdates"));
		JSONObject response;
		try (CloseableHttpClient client = getClient(); CloseableHttpResponse resp = client.execute(get)) {
			StatusLine sl = resp.getStatusLine();	
			if (logger.isDebugEnabled())
				logger.debug("Telegram message sent, {}: {}",sl.getStatusCode(),sl.getReasonPhrase());
			if (sl.getStatusCode() >= 300) {
				logger.warn("Status code not ok, {}: {}",sl.getStatusCode(),sl.getReasonPhrase());
				return;
			}
			String text = null;
		    try (Scanner scanner = new Scanner(resp.getEntity().getContent(), StandardCharsets.UTF_8.name())) {
		        text = scanner.useDelimiter("\\A").next();
		    }
	    	logger.trace("Update response: {}", text);
		    response = new JSONObject(text);
		} catch (IOException e) {
			logger.error("Telegram provider failed",e);
			return;
		}
		JSONArray arr = response.getJSONArray("result");
		parseMessages(arr);
		lastUpdate = System.currentTimeMillis();
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
		
		HttpEntity body = new StringEntity(data.toString(), StandardCharsets.UTF_8);
		final String url = getURL("sendMessage");
		if (url == null)
			return;
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
				return;
			}
		} catch (IOException | SecurityException e) {
			logger.error("Telegram provider failed",e);
			return;
		}
	}
	
	private void parseMessages(JSONArray array) {
		for (int i =0;i<array.length(); i++) {
			JSONObject obj = array.getJSONObject(i).getJSONObject("message").getJSONObject("chat");
			String fn = obj.getString("first_name");
			String ln = obj.getString("last_name");
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
		TelegramContact c = contactList.getSubResource(firstName + SEPARATOR + lastName, TelegramContact.class);
		return c;
	}

	// need at least Map<String,String>, better dedicated object type
	@Override
	public List<String> getKnownUsers() {
		if (System.currentTimeMillis() - lastUpdate > 15000)
			updateStatus();
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
 