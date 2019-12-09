package org.ogema.messaging.msgpublisher;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

import de.iee.sema.remote.message.receiver.model.RemoteMessage;

class Cleanup implements Supplier<Integer> {
	
	// if <= 0 then keep indefinitely
	private final long daysToKeepMessages;
	private final int maxNrMessagesToKeep;
	private final ApplicationManager  appMan;
	private final Semaphore sema = new Semaphore(1);
	
	public Cleanup(MsgPublisherConfig config, ApplicationManager appMan) {
		this.daysToKeepMessages = config.daysToKeepMessages();
		this.maxNrMessagesToKeep = config.maxMessagesToKeep();
		this.appMan = Objects.requireNonNull(appMan);
	}

	// returns nr of resources that were deleted
	@Override
	public Integer get() {
		if (!sema.tryAcquire())
			return 0;
		try {
			return cleanUp();
		} finally {
			sema.release();
		}
	}
	
	private int cleanUp() {
		if (daysToKeepMessages <= 0 && maxNrMessagesToKeep <= 0)
			return 0;
		final AtomicInteger cnt = new AtomicInteger(0);
		if (daysToKeepMessages > 0) {
			final long now = appMan.getFrameworkTime();
			appMan.getResourceAccess().getResources(RemoteMessage.class).stream()
				.filter(m -> getTimestamp(m) < now - daysToKeepMessages * Duration.ofDays(daysToKeepMessages).toMillis())
				.forEach(r -> delete(r, cnt));
		}
		if (maxNrMessagesToKeep > 0) {
			// sorted: newest first
			final List<RemoteMessage> messages0 = appMan.getResourceAccess().getResources(RemoteMessage.class);
			if (messages0.size() > maxNrMessagesToKeep) {
				final List<RemoteMessage> messages = messages0.stream()
						.sorted((m1, m2) -> -Long.compare(getTimestamp(m1), getTimestamp(m2)))
						.collect(Collectors.toList());
				messages.subList(maxNrMessagesToKeep, messages.size())
					.forEach(r -> delete(r, cnt));
			}
		}
		return cnt.get();
	}
	
	private static long getTimestamp(final RemoteMessage message) {
		return message.timestamp().isActive() ? message.timestamp().getValue() : Long.MIN_VALUE;
	}
	
	private final void delete(final Resource r, final AtomicInteger cnt) {
		r.delete();
		cnt.incrementAndGet();
	}

}
