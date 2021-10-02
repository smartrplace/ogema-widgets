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
package org.ogema.messages.email;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(specVersion = "1.2")
@Service({ Application.class, MessageListener.class })
public class EMailService implements Application, MessageListener {

	private volatile ResourcePatternAccess patternAccess;
	private volatile OgemaLogger logger;
	/** Server URL -> Authentication Method Type supported 
	 *  0 = first try STARTTLS, then try SSL (also used if no entry is found)
	 *  1 = only STARTTLS
	 *  2 = only SSL
	 */

	private Map<String, Integer> authMethods = new HashMap<>();

	@Override
	public void start(ApplicationManager am) {
		this.patternAccess = am.getResourcePatternAccess();
		this.logger = am.getLogger();
	}

	public boolean sendMail(final ReceivedMessage ms, EmailSenderPattern sender, List<String> recipients,
			Properties properties, String encoding, boolean isTrial) throws RuntimeException {

		if(Boolean.getBoolean("org.ogema.messages.email.testwithoutconnection"))
			return true;
		boolean allMessagesSent = true;

		final String email = sender.email.getValue();
		final String pw = sender.password.getValue();

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, pw);
			}
		});

		// session.setDebug(true);

		// Receiver
		List<EmailReceiverPattern> receivers = patternAccess.getPatterns(EmailReceiverPattern.class,
				AccessPriority.PRIO_LOWEST);

		if (receivers.isEmpty()) {
			logger.warn("No email-receivers activated");
			return false;
		}

		for (String receiver : recipients) {

			try {
				final EmailReceiverPattern rec = getReceiver(receiver, receivers);
				if (rec == null) {
					logger.error("User '" + receiver + "' not found");
					continue;
				}

				final Message message = new MimeMessage(session);

				message.setFrom(new InternetAddress(sender.email.getValue()));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec.emailAddress.getValue()));
				message.setSubject(ms.getOriginalMessage().title(null));
				String body;
				String msgRaw = ms.getOriginalMessage().message(null);
				if(msgRaw.startsWith("<!DOCTYPE html")) {
					body = msgRaw;
					message.setContent(body, "text/html");
				} else {
					body = "Notification : " + msgRaw + "\n" + "Sender : "
							+ ms.getAppName() + "\n" + "Time : " + new Date(ms.getTimestamp()).toString();
					message.setText(body);
				}

				message.setSentDate(new Date());

				AccessController.doPrivileged(new PrivilegedAction<Void>() {

					@Override
					public Void run() {
						try {
							Transport.send(message, email, pw);
						} catch (MessagingException e) {
							throw new RuntimeException(e);
						}
						logger.info("Email sent from {} to {} via {}", ms.getAppName(), rec.emailAddress.getValue(),
								encoding);
						return null;
					}
				});

			} catch (Exception e) {
				if(isTrial)
					logger.info("MessagingExpection caught while sending a new email from user " + sender.email.getValue()
					+ " to user " + receiver + "Error"+e.getMessage());
				else
					logger.error("MessagingExpection caught while sending a new email from user " + sender.email.getValue()
							+ " to user " + receiver, e);
				allMessagesSent = false;
			}
		}
		
		return allMessagesSent;
	}

	private final static EmailReceiverPattern getReceiver(String userName, List<EmailReceiverPattern> receivers) {
		for (EmailReceiverPattern pat : receivers) {
			if (pat.userName.getValue().equals(userName))
				return pat;
		}
		return null;
	}

	@Override
	public List<String> getKnownUsers() {
		if (patternAccess == null)
			return Collections.emptyList();
		List<EmailReceiverPattern> receivers = patternAccess.getPatterns(EmailReceiverPattern.class,
				AccessPriority.PRIO_LOWEST);
		List<String> users = new ArrayList<String>();
		for (EmailReceiverPattern pattern : receivers) {
			users.add(pattern.userName.getValue());
		}
		return users;
	}

	@Override
	public void newMessageAvailable(ReceivedMessage message, List<String> recipients) {

		if (patternAccess == null)
			return;

		// Sender
		List<EmailSenderPattern> senders = patternAccess.getPatterns(EmailSenderPattern.class,
				AccessPriority.PRIO_LOWEST);

		if (senders.isEmpty()) {
			logger.error("No sender configured, could not send email");
			return;
		}
		EmailSenderPattern sender = senders.get(0);
		for (EmailSenderPattern pat : senders) {
			if (pat.active.getValue()) {
				sender = pat;
				break;
			}
		}

		final String url = sender.serverURL.getValue();
		final int port = sender.port.getValue();

		// This method tries to send the message via tls (default). If this does not work for some reason,
		// it will try sending over SSL (with the same port and same host).

		/** We do not store the authentication method persistently, but search for an accepted
		 * method after each restart of the bundle
		 */
		Integer authMethod = authMethods.get(url);
		boolean allMessagesSent = false;
		if(authMethod == null || authMethod == 0 || authMethod == 1) {
			Properties properties = new Properties();
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.starttls.required", "true");
			properties.put("mail.smtp.host", url);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.debug", "true");
	
			allMessagesSent = sendMail(message, sender, recipients, properties, "STARTTLS", authMethod == null);
			if(allMessagesSent)
				authMethods.put(url, 1);
			else if(authMethod == null || authMethod == 0)
				logger.error("Sending message via STARTTLS failed, trying to send it via SSL now, using the same host and port");
			else
				logger.error("Sending message via STARTTLS failed!!");
		}
		if ((!allMessagesSent) && (authMethod == null || authMethod == 0 || authMethod == 2)) {

			Properties properties = new Properties();
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.host", url);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.debug", "true");

			allMessagesSent = sendMail(message, sender, recipients, properties, "SSL", false);
			if(allMessagesSent)
				authMethods.put(url, 2);
			else
				logger.error("Sending message via SSL failed!!");
		}

	}

	@Override
	public void stop(AppStopReason arg0) {
		patternAccess = null;
		logger = null;
	}

	@Override
	public String getId() {
		return "Email-connector";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "Email-connector";
	}

}