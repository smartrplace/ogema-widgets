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

package org.ogema.widgets.name.service.impl;

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
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(immediate = true, specVersion = "1.2")
@Service({Application.class, MessageListener.class})
public class EMailService implements Application, MessageListener{

	private volatile ResourcePatternAccess patternAccess;
	private volatile OgemaLogger logger;
	
	@Override
	public void start(ApplicationManager am) {
		this.patternAccess = am.getResourcePatternAccess();
		this.logger = am.getLogger();
	}
	
	public void sendMail(ReceivedMessage ms, List<String> recipients){
		if (patternAccess == null)
			return;
		
//Sender
		List<EmailSenderPattern> senders = patternAccess.getPatterns(EmailSenderPattern.class, AccessPriority.PRIO_LOWEST);
		
		if (senders.isEmpty()) {
			logger.error("No sender configured, could not send email");
			return;
		}
		EmailSenderPattern sender = senders.get(0);
		for(EmailSenderPattern pat : senders) {
			if(pat.active.getValue()) {
				sender = pat;
				break;
			}
		}
		final String email = sender.email.getValue();
		final String url = sender.serverURL.getValue();
		final int port = sender.port.getValue();
		final String pw = sender.password.getValue();
		
		Properties props=new Properties();
		props.put("mail.smtp.host", url);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		
//SSL Einstellungen
		//TODO dont blindly trust the url
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", url);
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, pw);
			}
		});
		
		//session.setDebug(true);
		
//Receiver 
		List<EmailReceiverPattern> receivers = patternAccess.getPatterns(EmailReceiverPattern.class, AccessPriority.PRIO_LOWEST);
		
		if(receivers.isEmpty()) {
			logger.warn("No Email-Receivers activated");
			return;
		}
		
		for (String receiver : recipients) {
			
			try {
				EmailReceiverPattern rec = getReceiver(receiver, receivers);
				if (rec == null) {
					logger.warn("User " + receiver + " not found");
					continue;
				}
				
				Message message=new MimeMessage(session);
				
				message.setFrom(new InternetAddress(sender.email.getValue()));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec.emailAddress.getValue()));
				message.setSubject(ms.getOriginalMessage().title(null));
				String body = "Notification : " + ms.getOriginalMessage().message(null) + 
								"\n" + "Sender : " + ms.getAppName() + 
								"\n" + "Time : " + new Date(ms.getTimestamp()).toString();
				
				message.setText(body);
				message.setSentDate(new Date());
				
				Transport.send(message,email,pw);
				
			} catch (MessagingException e) {
				logger.error("MessagingExpection caught while sending a new email from User " + sender.email.getValue() + " to User " + receiver + e.getMessage());
			}
		}
	}
	
	private final static EmailReceiverPattern getReceiver(String userName, List<EmailReceiverPattern> receivers) {
		for (EmailReceiverPattern pat: receivers) {
			if (pat.userName.getValue().equals(userName))
				return pat;
		}
		return null;
	}

	@Override
	public List<String> getKnownUsers() {
		if (patternAccess == null)
			return Collections.emptyList();
		List<EmailReceiverPattern> receivers = patternAccess.getPatterns(EmailReceiverPattern.class, AccessPriority.PRIO_LOWEST);
		List<String> users = new ArrayList<String>();
		for (EmailReceiverPattern pattern: receivers) {
			users.add(pattern.userName.getValue());
		}
		return users;
 	}

	@Override
	public void newMessageAvailable(ReceivedMessage message, List<String> recipients) {
		sendMail(message, recipients);
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