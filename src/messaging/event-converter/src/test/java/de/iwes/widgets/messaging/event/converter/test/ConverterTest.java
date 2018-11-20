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
package de.iwes.widgets.messaging.event.converter.test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.widgets.test.base.WidgetsTestBaseMin;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.messaging.model.MessagingApp;
import de.iwes.widgets.messaging.model.UserConfig;

@ExamReactorStrategy(PerClass.class)
public class ConverterTest extends WidgetsTestBaseMin {

	// FIXME super(true) not working... Component entry not found... need frameworkBundles workaround!
	public ConverterTest() {
		super(false);
	}
	
	@Override
	public Option[] frameworkBundles() {
		final Option[] opts = super.frameworkBundles();
		final Option[] opts2 = new Option[opts.length+1];
		System.arraycopy(opts, 0, opts2, 0, opts.length);
		opts2[opts2.length-1] = CoreOptions.mavenBundle("org.ogema.messaging", "event-converter", getWidgetsVersion());
		return opts2;
	}
	
	@Inject 
	EventAdmin eventAdmin;
	
	@Before
	public void createMessagingConfiguration() {
		if (getApplicationManager().getResourceAccess().getResources(MessagingApp.class).isEmpty()) {
			final MessagingApp app = getApplicationManager().getResourceManagement().createResource("ma", MessagingApp.class);
			app.appId().<StringResource> create().setValue("OGEMA Events"); // listen only to test app!
			app.active().<BooleanResource> create().setValue(true);
			app.services().create();
			final de.iwes.widgets.messaging.model.MessagingService ms = app.services().add();
			ms.serviceId().<StringResource> create().setValue(MListener.ID);
			ms.users().create();
			final UserConfig user = ms.users().add();
			user.priority().<IntegerResource> create().setValue(0);
			app.activate(true);
		}
	}
	
	private static void executeWithService(final ServiceRegistration<?> reg, Runnable task) {
		try {
			 task.run();
		} finally {
			reg.unregister();
		}
	}

	@Test
	public void sendingEventsWorks() {
		MListener listener=  new MListener(1);
		final ServiceRegistration<?> reg = ctx.registerService(MessageListener.class, listener, null);
		executeWithService(reg, () -> {
			final Event event = new Event("ogema/test", Collections.singletonMap("testprop", "testvalue"));
			eventAdmin.postEvent(event);
			Assert.assertTrue("Message not received", listener.await(5, TimeUnit.SECONDS));
			System.out.println("  Message: " + listener.getLastMessage().getOriginalMessage());
		});
	}
	
	private static class MListener implements MessageListener {
		
		static final String ID  = "test";
		private volatile CountDownLatch latch;
		private volatile ReceivedMessage lastMessage;
		
		MListener(int expectedMessages) {
			this.latch = new CountDownLatch(expectedMessages);
		}

		@Override
		public String getDescription(OgemaLocale arg0) {
			return ID;
		}

		@Override
		public String getId() {
			return ID;
		}

		@Override
		public List<String> getKnownUsers() {
			return Collections.singletonList(ID);
		}

		@Override
		public void newMessageAvailable(ReceivedMessage msg, List<String> arg1) {
			this.lastMessage = msg;
			latch.countDown();
		}

		public boolean await(long timeout, TimeUnit unit) {
			try {
				return latch.await(timeout, unit);
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		}
		
		public ReceivedMessage getLastMessage() {
			return lastMessage;
		}
		
	}
	
}
