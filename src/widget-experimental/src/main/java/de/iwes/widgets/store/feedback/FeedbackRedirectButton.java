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
package de.iwes.widgets.store.feedback;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;

public class FeedbackRedirectButton extends RedirectButton {
	private static final long serialVersionUID = 1L;
	private final String appId;
	private final boolean useGlyphicons;

	public FeedbackRedirectButton(WidgetPage<?> page, String id, String appId) {
		this(page, id, "Feedback", appId, true);
	}
	public FeedbackRedirectButton(WidgetPage<?> page, String id, String text, String appId) {
		this(page, id, text, appId, true);
	}
	public FeedbackRedirectButton(WidgetPage<?> page, String id, String text, String appId,
			boolean useGlyphicons) {
		super(page, id, text);
		this.appId = appId;
		this.useGlyphicons = useGlyphicons;
	}
	
	public FeedbackRedirectButton(OgemaWidget parent, String id, String appId,
			OgemaHttpRequest req) {
		this(parent, id,  "Feedback", appId, true, req);
	}
	public FeedbackRedirectButton(OgemaWidget parent, String id, String text, String appId,
			OgemaHttpRequest req) {
		this(parent, id,  text, appId, true, req);
	}
	public FeedbackRedirectButton(OgemaWidget parent, String id, String text, String appId,
			boolean useGlyphicons, OgemaHttpRequest req) {
		super(parent, id, text, "", req);
		this.appId = appId;
		this.useGlyphicons = useGlyphicons;
	}
	
	public void setInitialCommentText(String text, OgemaHttpRequest req) {
		addParameter("initialText", text, req);
	}
	public void setDefaultInitialCommentText(String text) {
		Map<String, String[]> parameters = new HashMap<>();
		parameters.put("initialText", new String[] {text});
		setDefaultParameters(parameters);
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		if(useGlyphicons)  setGlyphicon("glyphicon glyphicon-comment", req);
//		setCss(buttonStyle, req);
	}

	@Override
	public void onPrePOST(String data, OgemaHttpRequest req) {
		String dest = AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				return "https://" + System.getProperty("org.smartrplace.store.viewer.host", "localhost") 
				+ ":" + System.getProperty("org.smartrplace.store.viewer.port", "8510") + "/smartrplace/appstore/details.html";
			}
		});
		setUrl(dest, req);
		setParameters(Collections.singletonMap("configId", new String[]{appId}),req);
	}


}
