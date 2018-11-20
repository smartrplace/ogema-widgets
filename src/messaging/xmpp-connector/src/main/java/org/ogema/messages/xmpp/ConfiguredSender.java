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
package org.ogema.messages.xmpp;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;

public class ConfiguredSender {
	
	final XmppSenderPattern sender;
	final XMPPConnection connection;
	final ChatManager chatManager;
	
	public ConfiguredSender(final XmppSenderPattern sender, XMPPConnection connection, final ChatManager chatManager) {
		this.sender = sender;
		this.connection = connection;
		this.chatManager = chatManager;
	}

}
