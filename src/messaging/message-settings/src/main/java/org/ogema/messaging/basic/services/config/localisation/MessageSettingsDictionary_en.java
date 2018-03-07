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

package org.ogema.messaging.basic.services.config.localisation;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class MessageSettingsDictionary_en implements MessageSettingsDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.ENGLISH;
	}

	@Override
	public String headerSenders() {
		return "Sender configuration of the basic messenger services";
	}
	
	@Override
	public String headerReceivers() {
		return "Receiver configuration of the basic messenger services";
	}

	@Override
	public String descriptionReceivers() {
		return "This page allows you to configure receivers for messages sent by OGEMA apps, for the " 
				+ "three basic messenger services Email, SMS and XMPP. Creating a receiver does yet not imply "
				+ "that any messages will be forwarded to the respective address, rather there is another "
				+ "page which allows the <a href=\"" + SELECT_CONNECTOR_LINK + "\"><b>configuration of the message forwarding</b></a>.\n"
				+ "Before any of the three basic services can send any messages, it is required that a "
				+ "sender address be configured, which can be done <a href=\""
				+  SENDER_LINK + "\"><b>on this page</b></a>.\n"
				+ "Any data entered is only stored locally on the gateway.\n"
				+ "Messages sent by OGEMA applications can also be viewed in the broser: "
				+ "<a href=\"" + MESSAGE_READER_LINK + "\"><b>OGEMA Message Reader</b></a>.";
	}

	@Override
	public String descriptionSenders() {
		return "Here you can configure sender accounts for the three basic messenger services Email, SMS and XMPP. "
				+ "No messages can be sent unless a sender account is configured. "
				+ "In order to send messages via one of the services, the password for the sender account is required."
				+ "It is therefore recommended to create a special account for this purpose, and in particular, not to "
				+ "use for instance a personal email account.\n"
				+ "Receiver addresses for the respective services can be configured <a href=\"" + RECEIVER_LINK +"\"><b>on this page</b></a>.";
	}

	
}
