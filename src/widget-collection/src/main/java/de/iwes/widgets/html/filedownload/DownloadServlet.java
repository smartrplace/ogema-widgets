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
	private final Consumer<OutputStream> outputConsumer;
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
		this(Objects.requireNonNull(source), null, null, isReusable, contentType, appMan, listeners);
	}
	
	protected DownloadServlet(final Consumer<Writer> consumer, boolean isReusable, String contentType, 
			ApplicationManager appMan, List<DownloadListener> listeners) {
		this(null, Objects.requireNonNull(consumer), null, isReusable, contentType, appMan, listeners);
	}
	
	protected DownloadServlet(final Consumer<OutputStream> consumer, boolean isReusable, String contentType, 
			ApplicationManager appMan, List<DownloadListener> listeners, boolean dummy) {
		this(null, null, Objects.requireNonNull(consumer), isReusable, contentType, appMan, listeners);
	}
	
	private DownloadServlet(final ByteSource source, final Consumer<Writer> consumer, final Consumer<OutputStream> outputConsumer,
			boolean isReusable, String contentType, 
			ApplicationManager appMan, List<DownloadListener> listeners) {
		this.in= source;
		this.writerConsumer = consumer;
		this.outputConsumer = outputConsumer;
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
			} else if (writerConsumer != null) {
				try (final Writer writer = resp.getWriter()) {
					writerConsumer.accept(writer);
				}
			} else {
				try (final OutputStream stream = resp.getOutputStream()) {
					outputConsumer.accept(stream);
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
