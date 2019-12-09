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
package org.ogema.messaging.msgpublisher;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.ResourceDemandListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;

import de.iee.sema.remote.message.receiver.model.RemoteMessage;
import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(
		service = Application.class,
		configurationPid = ClientMsgPublisher.PID,
		configurationPolicy = ConfigurationPolicy.OPTIONAL
)
@Designate(ocd=MsgPublisherConfig.class)
public class ClientMsgPublisher implements Application, ResourceDemandListener<RemoteMessage> {

	public static final String PID = "org.ogema.messaging.ClientMsgPublisher";
	
	private long messageTimestampThreshold;
	private ApplicationManager appMan;
	private Cleanup cleanUp;
	private MsgPublisherConfig config;
	private final AtomicInteger msgCnt = new AtomicInteger(0);
	@Reference
	private MessagingService messaging;
	
	@Activate
	protected void activate(MsgPublisherConfig config) {
		this.config = config;
	}

	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		messaging.registerMessagingApp(appManager.getAppID(), "Client message publisher");
		this.messageTimestampThreshold = appManager.getResourceAccess().getResources(RemoteMessage.class).stream()
			.filter(rm -> rm.timestamp().isActive())
			.map(RemoteMessage::timestamp)
			.mapToLong(TimeResource::getValue)
			.max()
			.orElse(Long.MIN_VALUE);
		this.cleanUp = new Cleanup(config, appManager);
		appManager.getResourceAccess().addResourceDemand(RemoteMessage.class, this);
	}

	@Override
	public void stop(AppStopReason reason) {
		messaging.unregisterMessagingApp(appMan.getAppID());
		this.appMan = null;
		this.cleanUp = null;
	}

	@Override
	public void resourceAvailable(RemoteMessage resource) {
		if (!resource.timestamp().isActive() || resource.timestamp().getValue() <= messageTimestampThreshold) // do not bother about old messages
			return;
		if (!resource.subject().isActive() && !resource.body().isActive()) {
			appMan.getLogger().warn("Remote message without subject and body: {}" , resource);
			return;
		}
		final Message m = convert(resource);
		if (msgCnt.getAndIncrement() % 10 == 0) { // execute clean up after every 10th message
			final Logger l = appMan.getLogger();
			CompletableFuture.supplyAsync(cleanUp)
				.whenComplete((r,e) -> {
					if (e != null) {
						l.warn("Clean up failed",e);
					} else if (r > 0) {
						l.info("Removed {} old gateway messages", r);
					}
				});
		}
		messaging.sendMessage(appMan.getAppID(), m);
	}

	@Override
	public void resourceUnavailable(RemoteMessage resource) {}
	
	
	private static Message convert(final RemoteMessage remote) {
		return new Message() {
			
			@Override
			public String title(OgemaLocale locale) {
				return remote.subject().isActive() ? remote.subject().getValue(): "Untitled message";
			}
			
			@Override
			public MessagePriority priority() {
				final int v = remote.priority().isActive() ? remote.priority().getValue() : -1;
				return Arrays.stream(MessagePriority.values())
					.filter(p -> p.getPriority() == v)
					.findAny()
					.orElse(MessagePriority.LOW);
			}
			
			@Override
			public String message(OgemaLocale locale) {
				return remote.body().isActive() ? remote.body().getValue() : "";
			}
			
			@Override
			public String link() {
				return null;
			}
		};
	}
	
	
}
