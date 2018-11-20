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
package de.iwes.tools.system.supervision.gui;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.tools.system.supervision.gui.model.SupervisionMessageSettings;
import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;
import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class Messenger implements ResourceValueListener<SingleValueResource> {

	private final SupervisionMessageSettings settings;
	private final SystemSupervisionConfig config;
	private final ApplicationManager am;
	private final MessagingService messagingService;

	public Messenger(SupervisionMessageSettings settings, ApplicationManager am, MessagingService messagingService) {
		this.settings = settings;
		this.messagingService = messagingService;
		config = settings.getParent();
		if (config == null)
			throw new IllegalStateException(
					"Messenger settings not a subresource of the supervision config resource: " + settings);
		this.am = am;
		config.results().freeDiskSpace().addValueListener(this);
		config.results().usedMemorySize().addValueListener(this);
		config.results().nrResources().addValueListener(this);
		resourceChanged(config.results().freeDiskSpace());
		resourceChanged(config.results().usedMemorySize());
		resourceChanged(config.results().nrResources());
	}

	void close() {
		try {
			config.results().freeDiskSpace().removeValueListener(this);
			config.results().usedMemorySize().removeValueListener(this);
			config.results().nrResources().removeValueListener(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("", e);
		}
	}

	@Override
	public void resourceChanged(SingleValueResource resource) {
		final int type;
		if (resource.equalsLocation(config.results().freeDiskSpace()))
			type = 0;
		else if (resource.equalsLocation(config.results().usedMemorySize()))
			type = 1;
		else if (resource.equalsLocation(config.results().nrResources()))
			type = 2;
		else
			throw new IllegalArgumentException();
		final long value = ((Number) ValueResourceUtils.getValue((ValueResource) resource)).longValue();
		final int lastLevel = getLastWarningLevel(resource);
		for (int level = 3; level > lastLevel; level--) {
			final long threshold = getThreshold(resource, level);
			if (threshold > 0 && ((type == 0 && value < threshold) || (type != 0 && value > threshold))) {
				sendMessage(type, level, value);
				setLastWarningLevel(resource, level);
				return;
			}
		}
		if (lastLevel == 0)
			return;
		for (int level = lastLevel; level > 0; level--) {
			final long threshold = getThreshold(resource, level);
			if (threshold > 0 && value > threshold) {
				setLastWarningLevel(resource, level);
				return;
			}
		}
		setLastWarningLevel(resource, 0);
	}

	private void sendMessage(final int type, final int level, long value) {
		try {
			messagingService.sendMessage(am, new MessageImpl(type, level, value, config));
		} catch (Exception e) {
			am.getLogger().warn("Could not send message", e);
		}
	}

	// note: if this returns 0, then the threshold is not set
	private final long getThreshold(final SingleValueResource resource, final int level) {
		if (resource.equalsLocation(config.results().freeDiskSpace())) {
			switch (level) {
			case 1:
				return settings.freeDiskWarnThresholdLow().getValue();
			case 2:
				return settings.freeDiskWarnThresholdMedium().getValue();
			case 3:
				return settings.freeDiskWarnThresholdHigh().getValue();
			}
		} else if (resource.equalsLocation(config.results().usedMemorySize())) {
			switch (level) {
			case 1:
				return settings.memoryWarnThresholdLow().getValue();
			case 2:
				return settings.memoryWarnThresholdMedium().getValue();
			case 3:
				return settings.memoryWarnThresholdHigh().getValue();
			}
		} else if (resource.equalsLocation(config.results().nrResources())) {
			switch (level) {
			case 1:
				return settings.resourcesWarnThresholdLow().getValue();
			case 2:
				return settings.resourcesWarnThresholdMedium().getValue();
			case 3:
				return settings.resourcesWarnThresholdHigh().getValue();
			}
		}
		throw new IllegalArgumentException();
	}

	private final int getLastWarningLevel(final SingleValueResource resource) {
		if (resource.equalsLocation(config.results().freeDiskSpace()))
			return settings.lastDiskWarnLevel().getValue();
		else if (resource.equalsLocation(config.results().usedMemorySize()))
			return settings.lastMemoryWarnLevel().getValue();
		else if (resource.equalsLocation(config.results().nrResources()))
			return settings.lastResourceWarnLevel().getValue();
		throw new IllegalArgumentException();
	}

	private final void setLastWarningLevel(final SingleValueResource resource, final int level) {
		final IntegerResource target;
		if (resource.equalsLocation(config.results().freeDiskSpace()))
			target = settings.lastDiskWarnLevel();
		else if (resource.equalsLocation(config.results().usedMemorySize()))
			target = settings.lastMemoryWarnLevel();
		else if (resource.equalsLocation(config.results().nrResources()))
			target = settings.lastResourceWarnLevel();
		else
			throw new IllegalArgumentException();
		target.<IntegerResource>create().setValue(level);
		target.activate(false);
	}

	private static class MessageImpl implements Message {

		private final String title;
		private final String message;
		private final MessagePriority prio;
		private final String baseResource;

		public MessageImpl(int type, int level, long value, SystemSupervisionConfig baseResource) {
			if (type == 0) {
				title = "Disk usage exceeds limit";
				message = "Free disk space is " + (value / SystemSupervisionPage.mb) + " MB";
			} else if (type == 1) {
				title = "RAM usage exceeds limit";
				message = "Current RAM size is " + (value / SystemSupervisionPage.mb) + " MB";
			} else if (type == 2) {
				title = "Number of resources exceeds limit";
				message = "Number of OGEMA resources in the system is " + value;
			} else
				throw new IllegalArgumentException();
			prio = level == 1 ? MessagePriority.LOW
					: level == 2 ? MessagePriority.MEDIUM : level == 3 ? MessagePriority.HIGH : null;
			if (prio == null)
				throw new IllegalArgumentException();
			this.baseResource = baseResource.getLocation();
		}

		@Override
		public String title(OgemaLocale locale) {
			return title;
		}

		@Override
		public String message(OgemaLocale locale) {
			return message + " (base: " + baseResource + ")";
		}

		@Override
		public String link() {
			return SystemSupervisionGui.URL_BASE + "/index.html";
		}

		@Override
		public MessagePriority priority() {
			return prio;
		}

	}

}
