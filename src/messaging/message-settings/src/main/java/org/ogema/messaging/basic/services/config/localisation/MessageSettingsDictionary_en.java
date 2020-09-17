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
		if(Boolean.getBoolean("org.ogema.messaging.basic.services.config.fixconfigenglish"))
			return "For the configuration which applications send mesages to which receiver "+
				"see <a href=\"" + SELECT_CONNECTOR_LINK + "\"><b>here</b></a>.\n";
		return "This page allows you to configure receivers for messages, for the " 
				+ "three basic messenger services Email, SMS and XMPP. Creating a receiver does yet not imply "
				+ "that any messages will be forwarded to the respective address, rather there is another "
				+ "page which allows the <a href=\"" + SELECT_CONNECTOR_LINK + "\"><b>configuration of the message forwarding</b></a>.\n"
				+ "Before any of the three basic services can send any messages, it is required that a "
				+ "sender address be configured, which can be done <a href=\""
				+  SENDER_LINK + "\"><b>on this page</b></a>.\n"
				+ "Any data entered is only stored locally on the gateway.\n"
				+ "Messages sent can also be viewed in the broser: "
				+ "<a href=\"" + MESSAGE_READER_LINK + "\"><b>Message Reader</b></a>.";
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
