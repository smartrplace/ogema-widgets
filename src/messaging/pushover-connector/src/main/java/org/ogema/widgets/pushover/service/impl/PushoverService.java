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
/**
 * Copyright 2009 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IEE
 *
 * All Rights reserved
 */
package org.ogema.widgets.pushover.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.StringResource;
import org.ogema.widgets.pushover.model.PushoverConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Response;
import org.json.JSONObject;
import org.ogema.widgets.pushover.model.EmergencyMessage;

@Component(specVersion = "1.2")
@Service(Application.class)
public class PushoverService implements Application, MessageListener {

        /** pushover emergency message retry interval in seconds, minimum 30 */
	private final static int PRIO2_RETRY_S = 30;
        /** pushover emergency message expire time (time to retry) in seconds, maximum of 3 hours */
        private final static int PRIO2_EXPIRE_S = 3 * 60 * 60;
        /** number of retries in case of connection problems with pushover server */
        private final static int HTTP_RETRIES = 10;
        /** delay (seconds) for repeated attempts to connect to pushover server */
        private final static int HTTP_DELAY_S = 30;
        /** delay (seconds) for repeated attempts if pushover server return an internal error (5xx) */
        private final static int HTTP_5XX_DELAY_S = 30;
    
        private final static String TARGET_URI = "https://api.pushover.net/1/messages.json";
	private volatile BundleContext ctx;
	private volatile ServiceRegistration<MessageListener> sreg;
	private volatile PushoverConfiguration config;
	private volatile Logger logger;
	private WidgetApp wapp;
        private ScheduledExecutorService ses;
        private PushoverConfirmationMessaging confMsg;
	
	@Reference
	private OgemaGuiService widgetService;
        
        @Reference
        private MessagingService messaging;
	
	@Activate
	protected void start(BundleContext ctx) {
		this.ctx = ctx;
	}
	
	@Deactivate
	protected void stop() {
		this.ctx = null;
	}
	
	@Override
	public void start(ApplicationManager appManager) {
		this.config = appManager.getResourceManagement().createResource("pushoverConfiguration", PushoverConfiguration.class);
		config.userTokens().create();
		config.applicationTokens().create();
		this.logger = appManager.getLogger();
		this.wapp = widgetService.createWidgetApp("/de/iwes/messaging/pushover/config", appManager);
		new ConfigPage(wapp.createStartPage(), config);
                ses = Executors.newSingleThreadScheduledExecutor();
                ses.scheduleWithFixedDelay(this::updateEmergencyMessageInfo, 0, 30, TimeUnit.SECONDS);
                this.sreg = ctx.registerService(MessageListener.class, this, null);
                confMsg = new PushoverConfirmationMessaging(appManager, messaging);
                confMsg.start();
	}

	@Override
	public void stop(AppStopReason reason) {
		final ServiceRegistration<MessageListener> sreg = this.sreg;
		if (sreg != null)
			sreg.unregister();
		if (wapp != null)
			wapp.close();
		this.wapp = null;
		this.sreg = null;
		this.config = null;
		this.logger = null;
                if (confMsg != null) {
                    confMsg.stop();
                }
	}
	
	@Override
	public String getId() {
		return "Pushover";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "Forward messages through the Pushover app.";
	}

	@Override
	public void newMessageAvailable(final ReceivedMessage message, final List<String> recipients) {
		logger.trace("New message from {}, to {}",message.getAppName(),recipients);
		final List<String> receivers = new ArrayList<>(recipients);
		receivers.retainAll(getKnownUsers());
		if (receivers.isEmpty()) {
			logger.info("No recipient found; original list: " + recipients);
			return;
		}
                List<String> users = config.userTokens().getAllElements().stream()
                        .filter(StringResource::isActive)
                        .filter(sr -> !sr.getValue().trim().isEmpty())
                        .map(StringResource::getValue).collect(Collectors.toList());
		if (users.isEmpty()) {
			logger.warn("No sender found, could not forward message");
			return;
		}
		final URI uri;
		try {
			uri = new URI(TARGET_URI);
		} catch (URISyntaxException e) {
			logger.error("Unexpected exception:",e);
			return;
		}
                String apiToken = receivers.iterator().next();
                
                users.forEach(sender -> sendPushoverMessage(uri, message, apiToken, sender));
	}
        
        private void sendPushoverMessage(URI uri, ReceivedMessage message, String apiToken, String user) {
                final int poPrio;
                switch (message.getOriginalMessage().priority()) {
                        case HIGH : poPrio = 2; break;
                        case MEDIUM : poPrio = 1; break;
                        case LOW : poPrio = 0; break;
                        default : poPrio = 0;
                }
                final Request request = Request.Post(uri).body(buildRequestBody(message, apiToken, user, poPrio));
                RetryPolicy<HttpResponse> rp = new RetryPolicy<HttpResponse>()
                        .handle(IOException.class)
                        .withDelay(Duration.ofSeconds(HTTP_DELAY_S))
                        .withMaxAttempts(HTTP_RETRIES)
                        .onFailedAttempt(e -> {
                            logger.warn("sending of PushOver message failed on attempt {}", e.getAttemptCount(),  e.getLastFailure());
                        })
                        .onRetriesExceeded(e -> {
                            logger.error("sending of pushover message failed {} times, giving up", e.getAttemptCount());
                        });
                Failsafe.with(rp).getAsync(() -> {
                    return AccessController.doPrivileged((PrivilegedExceptionAction<HttpResponse>) () -> request.execute().returnResponse());
                }).thenAccept(resp -> {
                    handleResponse(resp, uri, message, apiToken, user, poPrio);
                });
        }
        
        private HttpEntity buildRequestBody(ReceivedMessage message, String apiToken, String user, int poPrio) {
                final List<NameValuePair> bodyEntries = new ArrayList<>();
		bodyEntries.add(new BasicNameValuePair("token", apiToken));
		bodyEntries.add(new BasicNameValuePair("user", user));
		bodyEntries.add(new BasicNameValuePair("message", message.getOriginalMessage().message(OgemaLocale.ENGLISH)));
                MessagePriority prio = message.getOriginalMessage().priority();
                bodyEntries.add(new BasicNameValuePair("priority", Integer.toString(poPrio)));
                if (poPrio == 2) {
                    bodyEntries.add(new BasicNameValuePair("expire", Integer.toString(PRIO2_EXPIRE_S)));
                    bodyEntries.add(new BasicNameValuePair("retry", Integer.toString(PRIO2_RETRY_S)));
                }
		String title = message.getOriginalMessage().title(OgemaLocale.ENGLISH);
		if (title != null) {
			title= title.trim();
			if (!title.isEmpty()) {
				bodyEntries.add(new BasicNameValuePair("title", title));
			}
		}
                return new UrlEncodedFormEntity(bodyEntries, StandardCharsets.UTF_8);
        }
        
        private void handleResponse(HttpResponse response, URI uri, ReceivedMessage message, String apiToken, String user, int poPrio) {
                final StatusLine status = response.getStatusLine();
		final int code = status.getStatusCode();
                if (code >= 500) {
                        logger.warn("Pushover server problem ({}), retry message shortly", code);
                        ses.schedule(() -> sendPushoverMessage(uri, message, apiToken, user), HTTP_5XX_DELAY_S, TimeUnit.SECONDS);
                } else if (code >= 400) {
                        try {
                                String body = readResponseBody(response);
                                logger.error("Message rejected by server, check code! Will not try again. Response: {}, {}", status, body);
                        } catch (IOException ex) {
                                logger.error("Message rejected by server, check code! Will not try again. Unable to read response, status code is: {}", status, ex);
                        }		
                } else {
                        if (poPrio == 2) {
                            try {
                                String json = readResponseBody(response);
                                JSONObject o = new JSONObject(json);
                                String receipt = o.getString("receipt");
                                storeEmergencyMessage(receipt, message, apiToken);
                                logger.debug("Emergency message sent. Receipt={}", receipt);
                            } catch (IOException ex) {
                                logger.error("Message sent successfully but could not parse reponse.", ex);
                            }
                        } else {
                                logger.debug("Message sent successfully. Response: {}", code);
                        }
                }
        }
        
        private void storeEmergencyMessage(String receipt, ReceivedMessage msg, String apiToken) {
            config.emergencyMessages().getSubResource("_" + receipt, EmergencyMessage.class).storeMessage(receipt, msg, apiToken);
        }
        
        private String readResponseBody(HttpResponse response) throws IOException {
                StringBuilder sb = new StringBuilder();
                try (InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
                        BufferedReader br = new BufferedReader(isr)) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                        sb.append(line).append("\n");
                                }
                }
                return sb.toString();
        }
        
        private void updateEmergencyMessageInfo() {
                logger.debug("updating receipt info for emergency messages, total messages: {}", config.emergencyMessages().getAllElements().size());
                // receipt info does not change after first ack (except expired flag)
                config.emergencyMessages().getAllElements().stream()
                        .filter(m -> !m.receiptExpired())
                        .filter(m -> !m.receiptInfo().isAcknowledged()).forEach(this::updateEmergencyMessageInfo);
        }
        
        private void updateEmergencyMessageInfo(EmergencyMessage m) {
                String receipt = m.receipt().getValue();
                logger.debug("updating receipt info for {}, sent at {}", receipt, m.sendTime().getValue());
                String receiptUri = String.format("https://api.pushover.net/1/receipts/%s.json?token=%s", receipt, m.appToken().getValue());
                Request poReceipt = Request.Get(receiptUri);
                try {
                        Response resp = poReceipt.execute();
                        HttpResponse hr = resp.returnResponse();
                        JSONObject receiptInfo = new JSONObject(readResponseBody(hr));
                        if (receiptInfo.getInt("status") == 1) {
                                boolean wasAcknowledged = m.receiptInfo().isAcknowledged();
                                m.receiptInfo().store(receiptInfo);
                                if (!wasAcknowledged && m.receiptInfo().isAcknowledged()) {
                                    confMsg.messageConfirmed(m);
                                }
                        } else {
                                logger.warn("receipt info request for {} failed: {}", receipt, receiptInfo.toMap());
                        }
                } catch (IOException | RuntimeException ex) {
                        logger.warn("could not update receipt info for {}", receipt, ex);
                }
            try {
                // pushover: do not query more than once in 5s
                Thread.sleep(8000);
            } catch (InterruptedException ex) {
                // okay
            }
        }

	@Override
	public List<String> getKnownUsers() {
		final PushoverConfiguration config = this.config;
		if (config == null)
			return Collections.emptyList();
		final List<String> users = new ArrayList<>();
		for (StringResource app : config.applicationTokens().getAllElements()) {
			if (app.isActive()) 
				users.add(app.getValue());
		}
		return users;
	}

}
