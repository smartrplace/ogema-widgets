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
package de.iwes.widgets.html.filedownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ogema.core.application.ApplicationManager;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	// exactly one of the following three fields is non-null 
	private final ByteSource in;
	private final Consumer<Writer> writerConsumer;
	// 
	private final boolean isReusable;
	// null unless isReusable is false
	private final Semaphore sema;
	private volatile boolean first = true;
	// may be null
	private final String contentType;
	private final List<DownloadListener> listeners;
	private final ApplicationManager appMan;

	protected DownloadServlet(final URL url, boolean isReusable, final String contentType, ApplicationManager appMan, List<DownloadListener> listeners) {
		this(Resources.asByteSource(url), isReusable, contentType, appMan, listeners);
	}
	
	protected DownloadServlet(final ByteSource source, boolean isReusable, String contentType, 
				ApplicationManager appMan, List<DownloadListener> listeners) {
		this(Objects.requireNonNull(source), null, isReusable, contentType, appMan, listeners);
	}
	
	protected DownloadServlet(final Consumer<Writer> consumer, boolean isReusable, String contentType, 
			ApplicationManager appMan, List<DownloadListener> listeners) {
		this(null, Objects.requireNonNull(consumer), isReusable, contentType, appMan, listeners);
	}
	
	private DownloadServlet(final ByteSource source, final Consumer<Writer> consumer, boolean isReusable, String contentType, 
			ApplicationManager appMan, List<DownloadListener> listeners) {
		this.in= source;
		this.writerConsumer = consumer;
		this.isReusable = isReusable;
		this.sema = isReusable ? null : new Semaphore(1);
		this.contentType = contentType; 
		this.listeners = listeners == null || listeners.isEmpty() ? Collections.emptyList() : listeners;
		this.appMan = appMan;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (sema != null) {
			if (!sema.tryAcquire() || !first) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Download not possible any more");
				return;
			}
		}
		final boolean wasFirst = this.first;
		try {
			if (contentType != null)
				resp.setContentType(contentType);
			if (in != null) {
				try (final InputStream input = in.openStream(); final OutputStream output = resp.getOutputStream()) {
					ByteStreams.copy(input, output);
				}
			} else {
				try (final Writer writer = resp.getWriter()) {
					writerConsumer.accept(writer);
				}
			}
			first = false;
			resp.setStatus(HttpServletResponse.SC_OK);
			if (wasFirst)
				informListeners();
		} finally {
			if (sema != null)
				sema.release();
		}
	}
	
	private final void informListeners() {
		for (DownloadListener l : listeners) {
			appMan.submitEvent(new Info(l));
		}
	}
	
	private final static class Info implements Callable<Void> {
		
		private final DownloadListener listener;
		
		public Info(DownloadListener listener) {
			this.listener = listener;
		}

		@Override
		public Void call() throws Exception {
			listener.downloadCompleted();
			return null;
		}
		
	}


}
