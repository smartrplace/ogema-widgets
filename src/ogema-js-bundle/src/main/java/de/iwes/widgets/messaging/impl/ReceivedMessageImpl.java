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
package de.iwes.widgets.messaging.impl;

import org.ogema.core.application.AppID;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.listener.MessageStatus;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;

/**
 * Allows access to protected methods in impl package
 */
class ReceivedMessageImpl implements ReceivedMessage {

	private final Message message;
	private final long sendTime;
	private final AppID appId;
	private final String appName;
	private volatile MessageStatus status = MessageStatus.SENT;	
	
	ReceivedMessageImpl(Message message, long timestamp, AppID appId, String appName) {
		this.message = message;
		this.sendTime = timestamp;
		this.appId = appId;
		this.appName = appName;
	}
	
	ReceivedMessageImpl(Message message, long timestamp, AppID appId) {
		this.message = message;
		this.sendTime = timestamp;
		this.appId = appId;
		this.appName = appId.getIDString();
	}
	
	@Override
	public Message getOriginalMessage() {
		return message;
	}
	
	@Override
	public long getTimestamp() {
		return sendTime;
	}
	
	@Override
	public MessageStatus getStatus() {
		return status;
	}
	
	void setStatus(MessageStatus status) {
		this.status = status;
	}

	@Override
	public AppID getAppId() {
		return appId;
	}

	@Override
	public String getAppName() {
		return appName;
	}

}
