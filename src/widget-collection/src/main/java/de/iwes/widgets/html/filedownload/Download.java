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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.ogema.core.application.ApplicationManager;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;

public class Download extends OgemaWidgetBase<DownloadData> {

	private static final long serialVersionUID = 1L;
	private final ApplicationManager appMan;
	private List<DownloadListener> defaultListeners;
	private ByteSource defaultSource;
	private Consumer<Writer> defaultConsumer;
	private String defaultContentType;
	
	public Download(WidgetPage<?> page, String id, ApplicationManager appMan) {
		super(page, id);
		this.appMan = Objects.requireNonNull(appMan); 
	}
	
	public Download(WidgetPage<?> page, String id, boolean globalWidget, ApplicationManager appMan) {
		super(page, id, globalWidget);
		this.appMan = Objects.requireNonNull(appMan); 
	}

	@SuppressWarnings("deprecation")
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return FileDownload.class;
	}

	@Override
	public DownloadData createNewSession() {
		return new DownloadData(this, appMan);
	}
	
	@Override
	protected void setDefaultValues(DownloadData opt) {
		super.setDefaultValues(opt);
		if (defaultListeners != null) {
			for (DownloadListener l : defaultListeners) {
				opt.addListener(l);
			}
		}
		if (defaultSource != null)
			opt.setSource(defaultSource, null, true, defaultContentType);
		else if (defaultConsumer != null)
			opt.setSource(null, defaultConsumer, true, defaultContentType);
	}
	
	public void setDefaultSource(final ByteSource source, final String contentType) {
		this.defaultSource = source;
		this.defaultContentType = contentType;
		this.defaultConsumer = null;
	}
	
	public void setDefaultSource(final URL url, final String contentType) {
		this.defaultSource = Resources.asByteSource(url);
		this.defaultContentType = contentType;
		this.defaultConsumer = null;
	}
	
	public void setDefaultSource(final File file, final String contentType) {
		this.defaultSource = Files.asByteSource(file);
		this.defaultContentType = contentType;
		this.defaultConsumer = null;
	}
	
	public void setDefaultSource(final byte[] bytes, final String contentType) {
		this.defaultSource = ByteSource.wrap(bytes);
		this.defaultContentType = contentType;
		this.defaultConsumer = null;
	}
	
	public void setDefaultSource(final String content, final String contentType) {
		setDefaultSource(content.getBytes(StandardCharsets.UTF_8), contentType);
	}
	
	public void setDefaultSource(final Consumer<Writer> source, final boolean reusable, final String contentType) {
		this.defaultConsumer = source;
		this.defaultSource = null;
		this.defaultContentType = contentType;
	}
	
	/**
	 * Use an InputStream as source. Note that this implies, that the resource 
	 * can only be downloaded once. Afterwards the stream will be closed.
	 * If you would like to enable multiple downloads, consider using either 
	 * {@link #setSource(URL, boolean, String, OgemaHttpRequest)} or
	 * {@link #setSource(File, boolean, String, OgemaHttpRequest)} instead, or the more
	 * generic {@link #setSource(ByteSource, boolean, String, OgemaHttpRequest)}.
	 * @param stream
	 * @param contentType
	 * @param req
	 */
	public void setSource(final InputStream stream, final String contentType, final OgemaHttpRequest req) {
		getData(req).setSource(stream, contentType);
	}
	
	/**
	 * See {@link #setSource(InputStream, String, OgemaHttpRequest)}. The same remark applies.
	 * @param reader
	 * @param contentType
	 * @param req
	 * @throws IOException 
	 */
	public void setSource(final Reader reader, final String contentType, final OgemaHttpRequest req) throws IOException {
		getData(req).setSource(reader, contentType);
	}
	
	public void setSource(final URL url, final boolean reusable, final String contentType, final OgemaHttpRequest req) {
		getData(req).setSource(url, reusable, contentType);
	}
	
	public void setSource(final File file, final boolean reusable, final String contentType, final OgemaHttpRequest req) {
		getData(req).setSource(file, reusable, contentType);
	}
	
	public void setSource(final String content, final boolean reusable, final String contentType, final OgemaHttpRequest req) {
		setSource(content.getBytes(StandardCharsets.UTF_8), reusable, contentType, req);
	}
	
	public void setSource(final byte[] bytes, final boolean reusable, final String contentType, final OgemaHttpRequest req) {
		getData(req).setSource(bytes, reusable, contentType);
	}
	
	public void setSource(final ByteSource source, final boolean reusable, final String contentType, final OgemaHttpRequest req) {
		getData(req).setSource(source, null, reusable, contentType);
	}
	
	public void setSource(final Consumer<Writer> source, final boolean reusable, final String contentType, final OgemaHttpRequest req) {
		getData(req).setSource(null, source, reusable, contentType);
	}
	
	/**
	 * Must be called before setting the source
	 * @param filename
	 * @param req
	 */
	public void setCustomFilename(String filename, final OgemaHttpRequest req) {
		getData(req).setCustomFilename(filename);
	}
	
	/**
	 * The listener will be called every time a new source is downloaded in any session
	 * for the first time.
	 * @param listener
	 */
	public void addDefaultListener(DownloadListener listener) {
		Objects.requireNonNull(listener);
		if (defaultListeners == null)
			defaultListeners = new ArrayList<>(4);
		defaultListeners.add(listener);
	}
	
	/**
	 * The listener will be called every time a new source is downloaded in the specified session
	 * for the first time.
	 * @param listener
	 * @param req
	 */
	public void addListener(final DownloadListener listener, final OgemaHttpRequest req) {
		getData(req).addListener(listener);
	}
	
	public void removeListener(final DownloadListener listener, final OgemaHttpRequest req) {
		getData(req).removeListener(listener);
	}
	
}
