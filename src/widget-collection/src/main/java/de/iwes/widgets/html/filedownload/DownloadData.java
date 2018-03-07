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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.security.WebAccessManager;

import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class DownloadData extends WidgetData {

    public static final TriggeredAction STARTDOWNLOAD = new TriggeredAction("download");
    public static final TriggeredAction GET_AND_STARTDOWNLOAD = new TriggeredAction("getAndDownload");
	private static final SecureRandom random = new SecureRandom();
	private final ApplicationManager appMan;
	private DownloadServlet servlet;
	private String customFilename;
	// all synchronized on this
	private List<DownloadListener> listeners = null;
    private String customWebPath;
    private String webPath;
    private volatile String url;
    private final AtomicBoolean active = new AtomicBoolean(false);

	protected DownloadData(Download widget, ApplicationManager appMan) {
		super(widget);
		this.appMan = appMan;
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		final JSONObject json = new JSONObject();
		final boolean disabled = isDisabled();
		json.put("disabled", disabled);
		final String webPath0 = !disabled && active.get() ? this.url : null;
		final String webPath = webPath0 != null ? webPath0 : "";
		json.put("url", webPath);
		return json;
	}
	
	protected void setSource(final File file, boolean reusable, final String contentType) {
		setSource(Files.asByteSource(file), null, reusable, contentType);
	}
	
	protected void setSource(final InputStream stream, final String contentType) {
		setSource(new StreamSource(stream), null, false, contentType);
	}
	
	protected void setSource(final URL url, final boolean reusable, final String contentType) {
		setSource(Resources.asByteSource(url), null, reusable, contentType);
	}
	
	protected void setSource(final byte[] bytes, final boolean reusable, final String contentType) {
		setSource(ByteSource.wrap(bytes), null, reusable, contentType);
	}
	
	protected void setSource(final Reader reader, final String contentType) throws IOException {
		// FIXME not very nice, since it parses the full reader at once, but the conversion
		// reader -> InputStream is non-trivial
		setSource(new ByteArrayInputStream(CharStreams.toString(reader).getBytes(StandardCharsets.UTF_8)), contentType);
	}
	
	protected void setSource(final Consumer<Writer> writerConsumer, final boolean reusable, final String contentType) {
		setSource(null, writerConsumer, reusable, contentType);
	}
	
	protected synchronized void setSource(final ByteSource source, final Consumer<Writer> consumer, final boolean reusable, final String contentType) {
		if ((source == null && consumer == null) || this.servlet != null)
			finalize(); // unregister existing servlet if it is still registered
		if (source == null && consumer == null) {
			this.servlet = null;
			this.webPath = null;
			this.url = null;
			return;
		}
		final boolean empty = this.listeners == null || this.listeners.isEmpty();
		if (customWebPath == null) //If application didn't set webPath
            webPath = "/dl/" + nextSessionId();     
        else
            webPath = customWebPath;
		final DownloadListener ownListener = !reusable ? new UniqueDownloadListener(webPath, appMan.getWebAccessManager(), active) : null;
		final List<DownloadListener> listeners;
		if (!reusable && empty) 
			listeners = Collections.singletonList(ownListener);
		else if (empty)
			listeners = null;
		else {
			listeners = new ArrayList<>(this.listeners);
			if (!reusable)
				listeners.add(ownListener);
		}
		this.servlet = source != null ? new DownloadServlet(source, reusable, contentType, appMan, listeners)
				: new DownloadServlet(consumer, reusable, contentType, appMan, listeners);
		this.url = appMan.getWebAccessManager().registerWebResourcePath(webPath, servlet);
		if (customFilename != null)
			url += "/" + customFilename;
		active.set(true);
	}
	
	protected synchronized void addListener(final DownloadListener listener) {
		Objects.requireNonNull(listener);
		if (listeners == null)
			listeners = new ArrayList<>(4);
		listeners.add(listener);
	}
	
	protected synchronized void removeListener(final DownloadListener listener) {
		Objects.requireNonNull(listener);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}
	
	protected void setCustomFilename(String filename) {
		this.customFilename = filename == null || filename.isEmpty() ? null : filename; 
	}
	
    private static String nextSessionId() { //Own session-id for download-security
        return new BigInteger(130, random).toString(32);
    }

	@Override
	protected void finalize() {
		try {
			if (webPath != null) {
				appMan.getWebAccessManager().unregisterWebResourcePath(webPath);
			}
		} catch (Throwable expected) {}
	}
	
	
	// a source that cannot be reused!
	private final static class StreamSource extends ByteSource {
		final InputStream stream;
		
		public StreamSource(InputStream stream) {
			this.stream = stream;
		}

		@Override
		public InputStream openStream() throws IOException {
			return stream;
		}
	}

	private final static class UniqueDownloadListener implements DownloadListener {
		
		private final String path;
		private final WebAccessManager wam;
		private final AtomicBoolean active;
		
		public UniqueDownloadListener(String path, WebAccessManager wam, final AtomicBoolean active) {
			this.path = path;
			this.wam = wam;
			this.active = active;
		}
		
		@Override
		public void downloadCompleted() {
			wam.unregisterWebResourcePath(path);
			active.set(false);
		}
		
	}

}
