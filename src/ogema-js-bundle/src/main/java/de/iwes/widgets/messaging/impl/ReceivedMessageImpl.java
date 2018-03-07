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
