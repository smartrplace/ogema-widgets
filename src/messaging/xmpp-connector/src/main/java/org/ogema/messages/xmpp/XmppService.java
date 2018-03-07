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

package org.ogema.messages.xmpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import com.google.common.annotations.Beta;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(immediate = true, specVersion = "1.2")
@Service({Application.class, MessageListener.class})

// FIXME this uses global state variables which may be accessed from multiple threads
// not production ready
@Beta
public class XmppService implements Application, MessageListener {
	
	private ResourcePatternAccess patternAccess;
	private OgemaLogger logger;
	
	private static final int packetReplyTimeout = 1000;
	
	private volatile ConfiguredSender configuredSender; // FIXME
	private final MyXmppMessageListener messageListener = new MyXmppMessageListener();
	
	@Reference
	private OgemaGuiService guiService;
	
	@Override
	public void start(ApplicationManager am) {
		this.patternAccess = am.getResourcePatternAccess();
		this.logger = am.getLogger();
	}
	
	@Override
	public void newMessageAvailable(ReceivedMessage message, List<String> recipients) {
		prepareSending(message, recipients);
	}
	
	private  void prepareSending(ReceivedMessage message, List<String> recipients){
		if (patternAccess == null)
			return;

		final ConfiguredSender sender = config();
		if (sender == null)
			return;

		List<XmppReceiverPattern> receivers = patternAccess.getPatterns(XmppReceiverPattern.class, AccessPriority.PRIO_LOWEST);

		if(receivers.isEmpty()) {
			logger.warn("No Xmpp-Receivers activated");
			return;
		}
		
		for (String receiver : recipients) {
			XmppReceiverPattern rec = getReceiver(receiver, receivers);
			if (rec == null) {
				logger.warn("User " + receiver + " not found");
				continue;
			}
			String receiverAddress = rec.xmpp.getValue();
			createEntry(receiverAddress);
			sendXmppMessage(message, receiverAddress, sender);
		}
		//TODO maybe its smart to close the connection to the server here. There probably aren't more than a 
		//			few messages in the month so a permanent connection isnt needed
	}
	
	private ConfiguredSender config(){
		
		final List<XmppSenderPattern> senders = patternAccess.getPatterns(XmppSenderPattern.class, AccessPriority.PRIO_LOWEST);
		if (senders.isEmpty()) {
			configuredSender = null;
			logger.error("No Xmpp-Sender configured, could not send xmpp-message");
			return null;
		}
		XmppSenderPattern sender = null;
		for (XmppSenderPattern pattern: senders) {
			if (!pattern.active.getValue())
				continue;
			sender = pattern;
			break;
		}
		
		synchronized (this) {
			if(configuredSender!= null) {
				if (sender.equals(configuredSender.sender))
					return configuredSender;
				disconnect(configuredSender);
				configuredSender = null;
			}
			final ConnectionConfiguration newConfig = new ConnectionConfiguration(sender.xmpp.getValue().split("@")[1], sender.port.getValue());
			newConfig.setSASLAuthenticationEnabled(true);
			newConfig.setSecurityMode(SecurityMode.enabled);
			final XMPPConnection connection = new XMPPConnection(newConfig);
			
			try {
				connection.connect();
				connection.login(sender.xmpp.getValue().split("@")[0], sender.password.getValue());
				setStatus(true,"online", configuredSender);
			} catch (Exception e) {
				try {
					connection.disconnect();
				} catch (Exception ee) {}
				logger.error("Could not connect to server",e);
				return null;
			}
			final ChatManager chatManager = connection.getChatManager();
			configuredSender = new ConfiguredSender(sender, connection, chatManager);
			return configuredSender;
		}
	}
	
	//not in use atm, maybe in the future when answering the messages is implemented (and needed)
	private static void setStatus(boolean available, String status, ConfiguredSender sender) {
		Presence.Type type = available? Type.available: Type.unavailable;
		Presence presence = new Presence(type);
		
		presence.setStatus(status);
		sender.connection.sendPacket(presence);
	}
	
	private void createEntry(String toUser) {
		Roster roster = configuredSender.connection.getRoster();
		try {
			roster.createEntry(toUser, toUser, null);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	private void sendXmppMessage(ReceivedMessage ms, String toUser, final ConfiguredSender sender) {
		String body = "Notification : " + ms.getOriginalMessage().message(null) + 
				"\n" + "Sender : " + ms.getAppName() + 
				"\n" + "Time : " + new Date(ms.getTimestamp()).toString();
		
		Chat chat = sender.chatManager.createChat(toUser, messageListener);
		
		try {
			chat.sendMessage(body);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void disconnect(ConfiguredSender sender) {
		if (sender.connection!=null && sender.connection.isConnected()) {
			sender.connection.disconnect();
		}
	}
	
	@Override
	public List<String> getKnownUsers() {
		if (patternAccess == null)
			return Collections.emptyList();
		List<XmppReceiverPattern> receivers = patternAccess.getPatterns(XmppReceiverPattern.class, AccessPriority.PRIO_LOWEST);
		List<String> users = new ArrayList<String>();
		for (XmppReceiverPattern pattern: receivers) {
			users.add(pattern.userName.getValue());
		}
		return users;
	}

	private final static XmppReceiverPattern getReceiver(String userName, List<XmppReceiverPattern> receivers) {
		for (XmppReceiverPattern pat: receivers) {
			if (pat.userName.getValue().equals(userName))
				return pat;
		}
		return null;
	}
	
	@Override
	public void stop(AppStopReason arg0) {
		patternAccess = null;
		logger = null;
	}

	//not in use at the moment, this is for answering to messages
	/*
	public void receiveMessage() {
		ChatManager chatManager = connection.getChatManager();
        org.jivesoftware.smack.MessageListener mL = new MyXmppMessageListener();

        ChatManagerListener chatManagerListener = new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener((org.jivesoftware.smack.MessageListener) messageListener);
            }
        };
        chatManager.addChatListener(chatManagerListener);

        Chat chat = connection.getChatManager().createChat("fillInToUserHere", mL);
        
        sendXmppMessage("Give me some instructions :");

        while (true) {
        	try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    	
	}
	
	private void sendXmppMessage(String ms, String toUser) {
		
		Chat chat = chatManager.createChat(toUser, messageListener);
		try {
			chat.sendMessage(ms);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
	}*/

	@Override
	public String getId() {
		return "XMPP-connector";
	}

	@Override
	public String getDescription(OgemaLocale locale) {
		return "XMPP-connector";
	}

}
