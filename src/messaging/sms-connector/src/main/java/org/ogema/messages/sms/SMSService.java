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
package org.ogema.messages.sms;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(specVersion = "1.2")
@Service({ Application.class, MessageListener.class })
public class SMSService implements Application, MessageListener {

	private volatile ResourcePatternAccess patternAccess;
	private volatile OgemaLogger logger;

	@Reference
	private OgemaGuiService guiService;

	@Override
	public void start(ApplicationManager am) {
		this.patternAccess = am.getResourcePatternAccess();
		this.logger = am.getLogger();
	}

	private boolean sendSMSviaMail(final ReceivedMessage ms, SmsSenderPattern sender, List<String> recipients,
			Properties properties, String encoding) throws RuntimeException {

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

		// toUser = "Phonenumber.username@tmsg.de"
		// Phonenumber and username@tmsg.de have to be registered in one team on teammessage.de
		// example toUser = "4915725925725.testtransmitter@tmsg.de"

		List<SmsReceiverPattern> receivers = patternAccess.getPatterns(SmsReceiverPattern.class, AccessPriority.PRIO_LOWEST);
		if (receivers.isEmpty()) {
			logger.warn("No sms-receivers activated");
			return false;
		}

		for (String receiver : recipients) {
			try {
				SmsReceiverPattern rec = getReceiver(receiver, receivers);
				if (rec == null) {
					logger.warn("User '" + receiver + "' not found");
					continue;
				}
				logger.debug("Current sms-receiver is '" + rec.userName + "' with sms(email) '" + rec.email.getValue() + "'");

				Message message = new MimeMessage(session);

				message.setFrom(new InternetAddress(email));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec.email.getValue()));
				message.setSubject(ms.getOriginalMessage().title(null));
				String body = "Notification : " + ms.getOriginalMessage().message(null) + "\n" + "Sender : "
						+ ms.getAppName() + "\n" + "Time : " + new Date(ms.getTimestamp()).toString();
				message.setText(body);
				message.setSentDate(new Date());

				AccessController.doPrivileged(new PrivilegedAction<Void>() {

					@Override
					public Void run() {
						try {
							Transport.send(message, email, pw);
						} catch (MessagingException e) {
							throw new RuntimeException(e);
						}
						logger.error("Sms(email) sent from {} to {} via {}", ms.getAppName(), rec.email.getValue(), encoding);
						return null;
					}
				});

			} catch (Exception e) {
				logger.error("MessagingExpection caught while sending a new email from user " + sender.email.getValue()
						+ " to user " + receiver, e);
				allMessagesSent = false;
			}
		}

		return allMessagesSent;
	}

	private final static SmsReceiverPattern getReceiver(String userName, List<SmsReceiverPattern> receivers) {
		for (SmsReceiverPattern pat : receivers) {
			if (pat.userName.getValue().equals(userName))
				return pat;
		}
		return null;
	}

	@Override
	public List<String> getKnownUsers() {
		if (patternAccess == null)
			return Collections.emptyList();
		List<SmsReceiverPattern> receivers = patternAccess.getPatterns(SmsReceiverPattern.class,
				AccessPriority.PRIO_LOWEST);
		List<String> users = new ArrayList<String>();
		for (SmsReceiverPattern pattern : receivers) {
			users.add(pattern.userName.getValue());
		}
		return users;
	}

	@Override
	public void newMessageAvailable(ReceivedMessage message, List<String> recipients) {

		if (patternAccess == null)
			return;

		// Sender
		List<SmsSenderPattern> senders = patternAccess.getPatterns(SmsSenderPattern.class, AccessPriority.PRIO_LOWEST);

		if (senders.isEmpty()) {
			logger.error("No sender configured, could not send sms(email)");
			return;
		}
		SmsSenderPattern sender = senders.get(0);
		for (SmsSenderPattern pat : senders) {
			if (pat.active.getValue()) {
				sender = pat;
				break;
			}
		}

		final String url = sender.serverURL.getValue();
		final int port = sender.port.getValue();

		// This method tries to send the message via tls (standart). If this does not
		// work for some reason,
		// it will try sending over SSL (with the same port and same host).

		Properties properties = new Properties();
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.starttls.required", "true");
		properties.put("mail.smtp.host", url);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.debug", "true");

		boolean allMessagesSent = sendSMSviaMail(message, sender, recipients, properties, "STARTTLS");
		if (!allMessagesSent) {
			logger.error(
					"Sending message via STARTTLS failed, trying to send it via SSL now, using the same host and port");

			properties = new Properties();
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.host", url);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.debug", "true");

			sendSMSviaMail(message, sender, recipients, properties, "SSL");
		}
	}

	@Override
	public void stop(AppStopReason arg0) {
		patternAccess = null;
		logger = null;
	}

	@Override
	public String getId() {
		return "Sms-connector";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "Sms-connector";
	}

}
