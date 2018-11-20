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
package de.iwes.widgets.api.extended.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;

/**
 *
 * @author jlapp
 */
// copied from framework-gui
public class BundleIcon {

	public enum IconType {
		PNG("image/png"), JPG("image/jpg"), SVG("image/svg+xml");
		private final String contentType;

		IconType(String contentType) {
			this.contentType = contentType;
		}

	};

	private final URL url;
	private final IconType type;

	public BundleIcon(URL url, IconType type) {
		this.url = url;
		this.type = type;
	}

	// TODO buffer those
	public static BundleIcon forBundle(Bundle b, BundleIcon defaultIcon) {
		URL url = b.getResource("/icon.svg");
		if (url != null) {
			return new BundleIcon(url, IconType.SVG);
		}
		url = b.getResource("/icon.png");
		if (url != null) {
			return new BundleIcon(url, IconType.PNG);
		}
		url = b.getResource("/icon.jpg");
		if (url != null) {
			return new BundleIcon(url, IconType.JPG);
		}
		return defaultIcon;
	}

	public void writeIcon(HttpServletResponse resp) throws IOException {
		if (url == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
        resp.setContentType(type.contentType);
        resp.setHeader("Cache-Control", "max-age=86400");
        byte[] buf = new byte[4096];
        try (InputStream is = url.openStream(); OutputStream out = resp.getOutputStream()) {
            int len;
            while ((len = is.read(buf)) != -1){
                out.write(buf, 0, len);
            }
        }
    }
}
