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
package de.iee.sema.remote.message.connector;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;
import org.ogema.serialization.jaxb.IntegerResource;
import org.ogema.serialization.jaxb.Resource;
import org.ogema.serialization.jaxb.StringResource;
import org.ogema.serialization.jaxb.TimeResource;
import org.osgi.service.component.annotations.Component;

import de.iee.sema.remote.message.forwarder.config.RemoteMessagePattern;
import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(service= {Application.class, MessageListener.class})
public class RemoteMessageConnector implements Application, MessageListener {

	private ResourcePatternAccess patternAccess;
	private OgemaLogger logger;
	
	@Override
	public void start(ApplicationManager appMan) {
		this.patternAccess = appMan.getResourcePatternAccess();
		this.logger = appMan.getLogger();
	}
	
	@Override
	public void newMessageAvailable(final ReceivedMessage receivedMessage, final List<String> recipients) {
		
		final Message msg = receivedMessage.getOriginalMessage();
		
		final StringResource subject = new StringResource();
		subject.setName("subject");
		subject.setType(org.ogema.core.model.simple.StringResource.class);
		subject.setValue(msg.title(null));
		subject.setDecorating(false);
		subject.setActive(true);
		
		final StringResource message = new StringResource();
		message.setName("body");
		message.setType(org.ogema.core.model.simple.StringResource.class);
		message.setValue(msg.message(null));
		message.setDecorating(false);
		message.setActive(true);
	
		final StringResource senderApp = new StringResource();
		senderApp.setName("sender");
		senderApp.setType(org.ogema.core.model.simple.StringResource.class);
		senderApp.setValue(receivedMessage.getAppId().getIDString());
		senderApp.setDecorating(false);
		senderApp.setActive(true);
		
		final TimeResource timestamp = new TimeResource();
		final long time = receivedMessage.getTimestamp();
		timestamp.setName("timestamp");
		timestamp.setType(org.ogema.core.model.simple.TimeResource.class);
		timestamp.setValue(time);
		timestamp.setDecorating(false);
		timestamp.setActive(true);
		
		final IntegerResource prio = new IntegerResource();
		prio.setName("priority");
		prio.setType(org.ogema.core.model.simple.IntegerResource.class);
		prio.setValue(msg.priority().getPriority());
		prio.setDecorating(false);
		prio.setActive(true);
		
		
		// Build message resource
		final Resource remoteMessage = new Resource();
		remoteMessage.setType("de.iee.sema.remote.message.receiver.model.RemoteMessage");
		remoteMessage.setName("message_" + time);
		remoteMessage.setDecorating(true);
		remoteMessage.setActive(true);
		
		List<Object> subResources = remoteMessage.getSubresources();
		
		subResources.add(subject);
		subResources.add(message);
		subResources.add(senderApp);
		subResources.add(timestamp);
		subResources.add(prio);
		
		// Serialize the created message resource
		final StringWriter writer = new StringWriter();
		try {
			final JAXBContext context = JAXBContext.newInstance(Resource.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(remoteMessage, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		logger.debug("rest message (xml): " + writer.toString());
		
		final List<RemoteMessagePattern> receivers = patternAccess.getPatterns(RemoteMessagePattern.class,
				AccessPriority.PRIO_LOWEST);
		
		// Send the serialized message to all the receivers
		final CloseableHttpClient client = getClient();
		
		for(String receiver : recipients) {
			final RemoteMessagePattern rec = getReceiver(receiver, receivers);
			String restAddress = rec.restAddress.getValue();
			if (!restAddress.endsWith("/"))
				restAddress = restAddress + "/";
			final String restUser = rec.restUser.getValue();
			
			// Old
//			final StringJoiner sj = new StringJoiner("/", restAddress, "/?user=" + restUser + "&pw=" + rec.restPassword.getValue());
//			sj.add(restUser).add("messages");
			
			String restUrl = restAddress + restUser + "/messages";
			
			logger.debug("rest adress: {}", restUrl);
			
			final HttpPost post = new HttpPost(restUrl);
	        post.setEntity(new StringEntity(writer.getBuffer().toString(), ContentType.APPLICATION_XML));
	        
	        final String auth0 = restUser + ":" + rec.restPassword.getValue();
	        final String auth1 = "Basic "+ Base64.getEncoder().encodeToString(auth0.getBytes(StandardCharsets.UTF_8));
	        post.setHeader("Authorization", auth1);
	        
	        final HttpResponse resp;
			try {
				resp = client.execute(post);
				int code = resp.getStatusLine().getStatusCode();
		        
				logger.debug("Remote message sent: Http-code: {}", code);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
		}
		
	}
	
	final static TrustStrategy TRUST_ALL = new TrustStrategy() {
		
		@Override
		public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			return true;
		}
	};

    protected static CloseableHttpClient getClient() {
        final SSLContext ctx;
        try {
            ctx = SSLContexts.custom().loadTrustMaterial(TRUST_ALL).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            throw new RuntimeException(ex);
        }
        
        @SuppressWarnings("deprecation")
		final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSslcontext(ctx)
                .build();
        return httpClient;
    }
	
	private RemoteMessagePattern getReceiver(String receiver, List<RemoteMessagePattern> receivers) {
		for (RemoteMessagePattern pat : receivers) {
			if (pat.userName.getValue().equals(receiver))
				return pat;
		}
		return null;
	}

	@Override
	public List<String> getKnownUsers() {
		if (patternAccess == null)
			return Collections.emptyList();
		final List<RemoteMessagePattern> receivers = patternAccess.getPatterns(RemoteMessagePattern.class,
				AccessPriority.PRIO_LOWEST);
		final List<String> users = new ArrayList<String>();
		for (RemoteMessagePattern pattern : receivers) {
			users.add(pattern.userName.getValue());
		}
		return users;
	}

	@Override
	public String getId() {
		return "Remote-Message-connector";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "This service serializes messages and forwards them to a given rest server";
	}
	
	@Override
	public void stop(AppStopReason arg0) {
		patternAccess = null;
		logger = null;
	}

}